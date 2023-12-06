package com.lnp.project.dto;

public class FundRequestDto {
    private String fundRequestUserName;

    private String fundRequestUserId;

    private String fundRequestRupees;
    private String fundRequestDateAdded;

    private String requestId;

    private String fundRequestStatus;

    public String getFundRequestUserName() {
        return fundRequestUserName;
    }

    public void setFundRequestUserName(String fundRequestUserName) {
        this.fundRequestUserName = fundRequestUserName;
    }

    public String getFundRequestUserId() {
        return fundRequestUserId;
    }

    public void setFundRequestUserId(String fundRequestUserId) {
        this.fundRequestUserId = fundRequestUserId;
    }

    public String getFundRequestRupees() {
        return fundRequestRupees;
    }

    public void setFundRequestRupees(String fundRequestRupees) {
        this.fundRequestRupees = fundRequestRupees;
    }

    public String getFundRequestDateAdded() {
        return fundRequestDateAdded;
    }

    public void setFundRequestDateAdded(String fundRequestDateAdded) {
        this.fundRequestDateAdded = fundRequestDateAdded;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFundRequestStatus() {
        return fundRequestStatus;
    }

    public void setFundRequestStatus(String fundRequestStatus) {
        this.fundRequestStatus = fundRequestStatus;
    }
}
