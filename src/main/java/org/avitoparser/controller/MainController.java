package org.avitoparser.controller;

import org.avitoparser.pageparser.AdvertPageParser;
import org.avitoparser.pageparser.PageIterator;
import org.avitoparser.utils.DataUtils;
import org.avitoparser.utils.PropertiesSingelton;
import org.avitoparser.utils.SaveDataUtil;
import org.avitoparser.utils.WebClientSingltone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ildar on 9/14/14.
 */
public class MainController {
    final private static Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final Properties propAvito = PropertiesSingelton.getInstance();
    private SaveDataUtil saveDataUtil;

    public void saveAdvertPage(String url) {
        AdvertPageParser pageParser = new AdvertPageParser();
        Map<AdvertPageParser.Param, String> resMap = null;
        try {

            resMap = pageParser.parsePage(url);
            saveDataUtil.saveData(resMap);

        } catch (IOException ex) {
            logger.error("while parsing page: {}", url, ex);
        }
        WebClientSingltone.getInstance().closeAllWindows();
        if (DataUtils.isEmpty(resMap)) {
            logger.info("Page: {} was not loaded");
        }


        try (FileWriter fileWriter = new FileWriter("phoneImg/datafile.txt", true);) {
            fileWriter.write(resMap.toString());
            fileWriter.write("\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void proceedAdvertPages() {
        int pageNum = Integer.parseInt(propAvito.get("avito.page_parse.add_list_num").toString());
        PageIterator pageIterator = new PageIterator();
        List<String> links = pageIterator.getAdvertLinks(pageNum);

        if (!DataUtils.isEmpty(links)) {
            int linkNumbe = 0;
            saveDataUtil = new SaveDataUtil(false);
            for (String link : links) {
                saveAdvertPage(link);
                logger.debug("Link {} is loaded", linkNumbe++);
            }
            saveDataUtil.closeSession();
        }
    }

    public static void main(String[] args) {
        MainController mainController = new MainController();
        mainController.proceedAdvertPages();
    }

}
