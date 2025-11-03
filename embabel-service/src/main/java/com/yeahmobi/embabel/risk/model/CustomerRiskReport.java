package com.yeahmobi.embabel.risk.model;

import lombok.Data;

import java.util.List;

/**
 * 综合客户风险报告
 */
@Data
public class CustomerRiskReport {
    private String companyName;
    private CustomerSwot swot;
    private CustomerRiskTags riskTags;
    private String performanceRating; // 履约能力等级 示例: "良"
    private String performanceAnalysis; // 履约能力说明
    private String cooperationStability; // 合作稳定性说明
    private List<String> recentNews; // 最近30天舆情摘要
    private String aiRecommendation; // 综合AI建议

    // Getters and Setters
}

