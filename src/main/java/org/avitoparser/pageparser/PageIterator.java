package org.avitoparser.pageparser;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.avitoparser.utils.PropertiesSingelton;
import org.avitoparser.utils.WebClientSingltone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class PageIterator {
    private static final Logger logger = LoggerFactory.getLogger(PageIterator.class);
    private static final int delayTime = 1000;
    private static final Properties propAvito = PropertiesSingelton.getInstance();
    private static final String S_BEFORE_CATALOG = propAvito.getProperty("avito.catalog_before");
    private static final String tagName = propAvito.getProperty("import.io.json_tag_link_name"),
            tagResults = propAvito.getProperty("import.io.json_results_tag");


    public static void main(String[] args) throws Exception {
        PageIterator pageIter = new PageIterator();
        System.out.println(pageIter.getAdvertLinks(1));


    }

    public List<String> getAdvertLinks(int pageNum) {
        List<String> advertList = new LinkedList<>();
        StringBuilder apiBuilder = new StringBuilder();
        apiBuilder.append(propAvito.getProperty("import.io.get_api"));
        apiBuilder.append(propAvito.getProperty("import.io.city_url"));
        apiBuilder.append(propAvito.getProperty("import.io.define_type"));
        apiBuilder.append(pageNum);
        apiBuilder.append(propAvito.getProperty("import.io.user_type"));
        apiBuilder.append(propAvito.getProperty("import.io.uid"));
        apiBuilder.append(propAvito.getProperty("import.io.key"));
        logger.info("Page {} is loaded", pageNum);
        try {
            WebClient client = WebClientSingltone.getInstance();
            WebResponse response = client.getPage(apiBuilder.toString()).getWebResponse();
            if (response.getContentType().equals("application/json")) {
                String jsonString = response.getContentAsString();
                client.closeAllWindows();

                JSONObject json = new JSONObject(jsonString);
                JSONArray urls = json.getJSONArray(tagResults);
                for (int i = 0; i < urls.length(); i++) {
                    advertList.add(urls.getJSONObject(i).getString(tagName).toString());
                }

            }
        } catch (JSONException | IOException e){
            logger.error("", e);
        }

        return advertList;
    }
}
