package org.avitoparser.controller;

import org.avitoparser.services.ImageToPhoneService;
import org.avitoparser.utils.SaveDataUtil;

import java.util.Map;

/**
 * Created by ildar on 21.02.15.
 */
public class ImageProcessController {

    public static void main(String[] args) {
        ImageToPhoneService imgRecognService = new ImageToPhoneService();
        imgRecognService.resizeAllImages();
        Map<String, String> phoneMap = imgRecognService.proceedImageBunch();

        SaveDataUtil saveDataUtil = new SaveDataUtil(true);
        saveDataUtil.updatePhoneNumbers(phoneMap);
    }
}
