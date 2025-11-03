package com.yeahmobi.embabel.risk.model;

import lombok.Data;

/**
 * 客户风险标签与解释
 */
@Data
public class CustomerRiskTags {
    private String badDebtRisk; // 示例: "低"
    private String badDebtRiskDesc; // 示例: "历史无重大逾期，坏账可能性低"

    private String fundOccupationRisk; // 示例: "中"
    private String fundOccupationRiskDesc; // 示例: "授信额度大，但月流水波动略高"

    private String paymentTimelinessRisk; // 示例: "良"
    private String paymentTimelinessRiskDesc; // 示例: "平均回款天数为3天，偶有延迟"

    private String litigationRisk; // 示例: "低"
    private String litigationRiskDesc; // 示例: "历史诉讼次数少，法院执行记录为0"

    private String operatingAbnormality; // 示例: "无"
    private String operatingAbnormalityDesc; // 示例: "公司经营状态正常，无异常记录"

    // Getters and Setters
}

