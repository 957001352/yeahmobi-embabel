package com.yeahmobi.embabel.risk.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
@JsonClassDescription("客户履约能力与合作稳定性")
public class CustomerReliability {

    @JsonPropertyDescription("履约能力等级")
    private String performanceRating; // 履约能力等级 示例: "良"
    @JsonPropertyDescription("履约能力结论说明")
    private String performanceConclusion; // 履约能力说明
    @JsonPropertyDescription("履约能力结论原因分析")
    private List<String> performanceDesc; // 履约能力原因分析

    @JsonPropertyDescription("合作稳定性等级")
    private String cooperationRating; // 合作稳定性说明
    @JsonPropertyDescription("合作稳定性结论说明")
    private String cooperationConclusion; // 合作稳定性说明
    @JsonPropertyDescription("合作稳定性结论原因分析")
    private List<String> cooperationDesc; // 合作稳定性说明
}
