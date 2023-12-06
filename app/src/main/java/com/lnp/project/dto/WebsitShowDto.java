package com.lnp.project.dto;

public class WebsitShowDto {

    private Integer id;
    private String websiteBannerName;
    private String websiteBannerUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWebsiteBannerName() {
        return websiteBannerName;
    }

    public void setWebsiteBannerName(String websiteBannerName) {
        this.websiteBannerName = websiteBannerName;
    }

    public String getWebsiteBannerUrl() {
        return websiteBannerUrl;
    }

    public void setWebsiteBannerUrl(String websiteBannerUrl) {
        this.websiteBannerUrl = websiteBannerUrl;
    }
}
