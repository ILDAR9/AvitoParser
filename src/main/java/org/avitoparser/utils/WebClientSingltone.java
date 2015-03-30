package org.avitoparser.utils;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Created by ildar on 20.02.15.
 */
public class WebClientSingltone {

    private static WebClient webClient;

    private WebClientSingltone(){}

    public static WebClient getInstance(){
        if (webClient == null){
            webClient = new WebClient();
        }
        return webClient;
    }

}
