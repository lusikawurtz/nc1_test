package com.nc1_test.entities;

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
        for (WebsiteData website : WebsiteData.values()) {
            if (website.getWebsiteLink().equals(websiteLink))
                return website;
        }
        return null;
    }

}