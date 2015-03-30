package org.avitoparser.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class PageParser{
    private final static Logger logger = LoggerFactory.getLogger(PageParser.class);
    static final private Properties propAvito = PropertiesSingelton.getInstance();
    static final String S_BEFORE_CATALOG = propAvito.getProperty("avito.catalog_before");

    public static void main(String[] args) {
        PageParser pageParser = new PageParser();
        try{
        pageParser.parsePage(propAvito.getProperty("avito.kvartiry.vtorichka"));
        }catch(IOException ex){
            logger.error("while parsing page{}", propAvito.getProperty("avito.doma_dachi_kottedzhi"),ex);
        }
    }

    public void parsePage(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        Elements info = doc.select("div.genre").select("a[href]");

        /* extract list */
        info = doc.select(S_BEFORE_CATALOG);
        List<Element> catalogBeforeAds = info.select("a[href]");
        System.out.println(catalogBeforeAds);


    }
}