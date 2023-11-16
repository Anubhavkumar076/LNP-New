package com.lnp.project.responseDto.browseplan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowsePlan {

    private Boolean status;
    private Integer responseCode;
    private Map<String, List<BrowsePlanInfoDto>> browsePlanInfoDtoMap = new HashMap<>();

    private String message;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, List<BrowsePlanInfoDto>> getBrowsePlanInfoDtoMap() {
        return browsePlanInfoDtoMap;
    }

    public void setBrowsePlanInfoDtoMap(Map<String, List<BrowsePlanInfoDto>> browsePlanInfoDtoMap) {
        this.browsePlanInfoDtoMap = browsePlanInfoDtoMap;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
