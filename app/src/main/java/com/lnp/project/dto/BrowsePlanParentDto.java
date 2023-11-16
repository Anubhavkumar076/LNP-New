package com.lnp.project.dto;

import java.util.List;

public class BrowsePlanParentDto {
    private List<BrowsePlanDto> browsePlanDto;
    private String browsePlanHeading;

    public List<BrowsePlanDto> getBrowsePlanDto() {
        return browsePlanDto;
    }

    public void setBrowsePlanDto(List<BrowsePlanDto> browsePlanDto) {
        this.browsePlanDto = browsePlanDto;
    }

    public String getBrowsePlanHeading() {
        return browsePlanHeading;
    }

    public void setBrowsePlanHeading(String browsePlanHeading) {
        this.browsePlanHeading = browsePlanHeading;
    }
}
