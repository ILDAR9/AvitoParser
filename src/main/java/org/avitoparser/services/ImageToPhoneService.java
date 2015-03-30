package org.avitoparser.services;


import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.avitoparser.utils.DataUtils;
import org.avitoparser.utils.PropertiesSingelton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by ildar on 20.02.15.
 */
public class ImageToPhoneService {

    private static final Logger logger = LoggerFactory.getLogger(ImageToPhoneService.class);
    private static final Properties avitoProp = PropertiesSingelton.getInstance();
    private Tesseract imageRecognition;

    public ImageToPhoneService() {
        imageRecognition = Tesseract.getInstance();  // JNA Interface Mapping
    }


    public Map<String, String> proceedImageBunch() {
        Map<String, String> phoneMap = new HashMap();
        File imgFolder = new File(avitoProp.getProperty("avito.phone_recognition.folder_resized"));
        String result;
        for (File imgFile : imgFolder.listFiles()) {
            try {
                result = imageRecognition.doOCR(imgFile);
                if (!DataUtils.isEmpty(result)) {
                    result = filterCorrector(result);
                    phoneMap.put(imgFile.getName(), result);
                }
            } catch (TesseractException e) {
                logger.error("", e);
            }

        }
        return phoneMap;

    }

    public void resizeAllImages() {
        try {
            String cmd = String.format(avitoProp.getProperty("avito.phone_recognition.cmd"),
                    avitoProp.getProperty("avito.phone_recognition.folder_resized"),
                    avitoProp.getProperty("avito.phone_recognition.folder"));
            System.out.println(cmd);
            Runtime.getRuntime().exec(cmd);
            Thread.sleep(3000);
        } catch (IOException | InterruptedException e) {
            logger.error("", e);
        }
    }

    private String filterCorrector(String result) {
        return result.replaceAll("B", "8")
                .trim()
                .replaceAll("I", "1")
                .replaceAll("l", "1")
                .replaceAll("o", "0")
                .replaceAll("O", "0");
    }

    /* for test */
    public static void main(String[] args) {
        ImageToPhoneService imgRecognService = new ImageToPhoneService();
        imgRecognService.resizeAllImages();
        Map<String, String> phoneMap = imgRecognService.proceedImageBunch();

        System.out.println(phoneMap);
    }


}
