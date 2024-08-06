package com.nc1_test.entities;

import java.util.Arrays;

public enum WebsiteData {

    PRAVDA("https://www.pravda.com.ua"),
    ;

    private final String websiteLink;


    WebsiteData(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getWebsiteLink() {
        return this.websiteLink;
    }

    public static WebsiteData getByWebsiteLink(String websiteLink) {
        return Arrays.stream(WebsiteData.values())
                .filter(website -> website.getWebsiteLink().equals(websiteLink))
                .findFirst()
                .orElse(null);
    }

}