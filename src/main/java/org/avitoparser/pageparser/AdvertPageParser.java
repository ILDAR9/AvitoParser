package org.avitoparser.pageparser;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.avitoparser.utils.DataUtils;
import org.avitoparser.utils.PropertiesSingelton;
import org.avitoparser.utils.WebClientSingltone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvertPageParser {
    private static final Logger logger = LoggerFactory.getLogger(AdvertPageParser.class);
    private static final Properties propAvito;
    private static final String SEL_TITLE, SEL_TIME_POST, SEL_PRICE, SEL_CITY, SEL_ADDRESS, SEL_DESCRIPTION, SEL_AVITO_ID, SEL_PERSON_NAME, SEL_HOUSE_TYPE;
    private static final String[] MONTH_ARRAY = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static final int TIME_OUT = 1500;
    private static final String imgFolderName;

    private static final Pattern PATTERN_TITLE, PATTERN_TIME_TODAY, PATTERN_TIME_YESTERDAY, PATTERN_TIME_DATE;
    private static final SimpleDateFormat DATE_FORMAT;

    public static enum Param {
        ROOM_COUNT, AREA, FLOOR_COUNT, FLOOR_CURRENT, DATE_POST, PRICE, CITY, ADDRESS, DESCRIPTION, AVITO_ID, PERSON_NAME, HOUSE_TYPE, PHONE_CODE
    }

    static {
        propAvito = PropertiesSingelton.getInstance();
        DATE_FORMAT = new SimpleDateFormat(propAvito.getProperty("avito.page_parse.date_format"));
        SEL_TITLE = propAvito.getProperty("avito.page_parse.selector_title");
        SEL_TIME_POST = propAvito.getProperty("avito.page_parse.selector_time_post");
        SEL_PRICE = propAvito.getProperty("avito.page_parse.selector_price");
        SEL_CITY = propAvito.getProperty("avito.page_parse.selector_city");
        SEL_ADDRESS = propAvito.getProperty("avito.page_parse.selector_address");
        SEL_DESCRIPTION = propAvito.getProperty("avito.page_parse.description");
        SEL_AVITO_ID = propAvito.getProperty("avito.page_parse.selector_avito_id");
        SEL_PERSON_NAME = propAvito.getProperty("avito.page_parse.selector_person_name");
        SEL_HOUSE_TYPE = "a[title^=Тип дома]";
        PATTERN_TITLE = Pattern.compile(propAvito.getProperty("avito.page_parse.pattern_title"));
        PATTERN_TIME_TODAY = Pattern.compile(propAvito.getProperty("avito.page_parse.pattern_time_today"));
        PATTERN_TIME_YESTERDAY = Pattern.compile(propAvito.getProperty("avito.page_parse.pattern_time_yesterday"));
        PATTERN_TIME_DATE = Pattern.compile(propAvito.getProperty("avito.page_parse.pattern_time_date"));
        imgFolderName = propAvito.getProperty("avito.page_parse.img_folder_name");
    }

    private final Map<Param, String> resultMap;
    private Elements content;

    final WebClient webClient = WebClientSingltone.getInstance();

    public AdvertPageParser() {
        resultMap = new HashMap<>();
    }

    public Map<Param, String> parsePage(String url) throws IOException {
        //ToDO elicit content data from htmlUnit to jsoup
        Document doc = Jsoup.connect(url).get();
        content = doc.select("div.l-content");

        /* parse title */
        parseTitle();
        /* parse time post */
        parseTimePost();
        /* parse price */
        parsePrice();
        /* parse City */
        parseSimple(Param.CITY, SEL_CITY);
        /* parse Address */
        parseSimple(Param.ADDRESS, SEL_ADDRESS);
        /* parse description */
        parseDescription();
        /* parse avito id */
        parseSimple(Param.AVITO_ID, SEL_AVITO_ID);
        /* parse person name */
        parseSimple(Param.PERSON_NAME, SEL_PERSON_NAME);
        /* parse house type */
        parseSimple(Param.HOUSE_TYPE, SEL_HOUSE_TYPE);
        /* parse img src and save img to folder phoneImg by img_src text */
        parseTelephoneImgURI(url);

        return resultMap;
    }


    /* for testing */
    public static void main(String[] args) throws IOException {
        AdvertPageParser adParserr = new AdvertPageParser();
        System.out.println(adParserr.parsePage("https://www.avito.ru/kazan/kvartiry/1-k_kvartira_33_m_59_et._515393554"));
    }

    /**
     * room_counts, area, floor_current, floor_count
     */
    private boolean parseTitle() {
        boolean done = false;
        Elements title = content.select(SEL_TITLE).remove();
        Matcher matcher_title = PATTERN_TITLE.matcher(title.html());
        if (matcher_title.matches()) {
            resultMap.put(Param.ROOM_COUNT, matcher_title.group(1));
            resultMap.put(Param.AREA, matcher_title.group(2));
            resultMap.put(Param.FLOOR_CURRENT, matcher_title.group(3));
            resultMap.put(Param.FLOOR_COUNT, matcher_title.group(4));
            done = true;
        } else {
            logger.debug("Title pattern does not match");
        }
        return done;
    }

    /**
     * cases: today, yesterday, appropriate day
     */
    private boolean parseTimePost() {
        boolean done = false;
        Elements timePost = content.select(SEL_TIME_POST).remove();
        timePost.select("a").remove();
        Calendar cal = null;
        Matcher matcher_title = PATTERN_TIME_TODAY.matcher(timePost.html());
        if (matcher_title.matches()) {
            cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher_title.group(1)));
            cal.set(Calendar.MINUTE, Integer.parseInt(matcher_title.group(2)));
        } else if ((matcher_title = PATTERN_TIME_YESTERDAY.matcher(timePost.html())).matches()) {
            cal = new GregorianCalendar();
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher_title.group(1)));
            cal.set(Calendar.MINUTE, Integer.parseInt(matcher_title.group(2)));
        } else if ((matcher_title = PATTERN_TIME_DATE.matcher(timePost.html())).matches()) {
            cal = new GregorianCalendar();
            String monthFound = matcher_title.group(2);
            int i;
            for (i = 0; i < MONTH_ARRAY.length && monthFound.equals(MONTH_ARRAY[i]); i++) ;
            if (i < MONTH_ARRAY.length) {
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher_title.group(1)));
                cal.set(Calendar.MONTH, i);
            }
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher_title.group(3)));
            cal.set(Calendar.MINUTE, Integer.parseInt(matcher_title.group(4)));
        } else {
            logger.debug("No any time pattern does not match");
        }
        if (cal != null) {
            resultMap.put(Param.DATE_POST, DATE_FORMAT.format(cal.getTime()));
            done = true;
        }
        return done;
    }

    /**
     * elicit price
     */
    private boolean parsePrice() {
        boolean done = false;
        Elements price = content.select(SEL_PRICE).remove();
        if (!DataUtils.isEmpty(price.html())) {
            resultMap.put(Param.PRICE, price.html().replaceAll("[^\\d]", ""));
            done = true;
        }
        return done;
    }

    /**
     * elicit description
     */
    private boolean parseDescription() {
        boolean done = false;
        Elements descriptionElem = content.select(SEL_DESCRIPTION).remove();
        String description = "";
        for (Element elem : descriptionElem.select("p")) {
            description += elem.text();
            description += "\n";
        }
        if (!DataUtils.isEmpty(description)) {
            resultMap.put(Param.DESCRIPTION, description);
            done = true;
        }

        return done;
    }

    /**
     * @param param
     * @param selector
     */
    private boolean parseSimple(Param param, String selector) {
        boolean done = false;
        Elements item = content.select(selector).remove();
        if (!DataUtils.isEmpty(item.html())) {
            resultMap.put(param, item.html());
            done = true;
        }
        return done;
    }

    private boolean parseTelephoneImgURI(String url) throws IOException {
        final HtmlPage page = webClient.getPage(url);
        ArrayList<Object> listSpan = (ArrayList<Object>) page.getByXPath(".//*[@id='i_contact']/div[4]/div/span[1]");
        boolean done = false;
        if (!DataUtils.isEmpty(listSpan)) {
            HtmlSpan span = (HtmlSpan) listSpan.get(0);
            System.out.println("BEFORE: " + span.getTextContent());
            span.click();
            listSpan = null;
            for (int i = 0; DataUtils.isEmpty(listSpan) && i < 5; i++) {
                webClient.waitForBackgroundJavaScript(TIME_OUT);
                listSpan = (ArrayList<Object>) page.getByXPath(".//*[@id='i_contact']/div[4]/div/span[1]/span/img");
                if (!DataUtils.isEmpty(listSpan)) {
                    HtmlImage img = (HtmlImage) listSpan.get(0);
                    System.out.println("AFTER: " + img);
                    String imgName = resultMap.get(Param.AVITO_ID) + ".png";
                    img.saveAs(new File(imgFolderName, imgName));
                    resultMap.put(Param.PHONE_CODE, imgName);
                    done = true;
                } else {
                    logger.warn("Phone img not loaded on {} iteration by url: {}", i, url);
                }
            }
            webClient.closeAllWindows();

        }
        return done;
    }

}
