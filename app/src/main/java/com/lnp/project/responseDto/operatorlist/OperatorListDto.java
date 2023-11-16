package com.lnp.project.responseDto.operatorlist;

import java.util.ArrayList;
import java.util.List;

public class OperatorListDto {

    private Integer responseCode;
    private Boolean status;
    private List<OperatorListDataDto> operatorListDataDtos = new ArrayList<>();

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<OperatorListDataDto> getOperatorListDataDtos() {
        return operatorListDataDtos;
    }

    public void setOperatorListDataDtos(List<OperatorListDataDto> operatorListDataDtos) {
        this.operatorListDataDtos = operatorListDataDtos;
    }
    //    {
//        "responsecode": 1,
//            "status": true,
//            "data": [
//        {
//            "": "1",
//                "": "Aircel",
//                "": "Prepaid"
//        },
//        {
//            "id": "11",
//                "name": "Airtel",
//                "category": "Prepaid"
//        }
//  ],
//        "message": "Operator List Fetched"
//    }
}
