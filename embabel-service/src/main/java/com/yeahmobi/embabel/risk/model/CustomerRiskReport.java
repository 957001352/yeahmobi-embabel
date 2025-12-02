package com.yeahmobi.embabel.risk.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

/**
 * 综合客户风险报告
 */
@Data
@JsonClassDescription("综合客户风险报告")
public class CustomerRiskReport {
    @JsonPropertyDescription("客户名称")
    private String companyName;
    @JsonPropertyDescription("客户SWOFT评估")
    private CustomerSwot swot;
    @JsonPropertyDescription("客户合作风险评估")
    private CustomerRiskTags riskTags;
    @JsonPropertyDescription("客户履约能力与合作稳定性")
    private CustomerReliability reliability;
//    @JsonPropertyDescription("最近30天舆情摘要")
//    private List<String> recentNews; // 最近30天舆情摘要
    @JsonPropertyDescription("综合AI建议")
    private List<String> aiRecommendation; // 综合AI建议

    // Getters and Setters
}

