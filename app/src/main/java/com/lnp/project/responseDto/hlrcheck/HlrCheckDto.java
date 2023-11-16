package com.lnp.project.responseDto.hlrcheck;

public class HlrCheckDto {
    private Boolean status;
    private Integer responseCode;

    private String message;

    private HlrCheckInfoDto info;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HlrCheckInfoDto getInfo() {
        return info;
    }

    public void setInfo(HlrCheckInfoDto info) {
        this.info = info;
    }
}
