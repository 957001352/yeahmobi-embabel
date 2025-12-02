package com.yeahmobi.embabel.risk.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

/**
 * 客户风险标签与解释
 */
@Data
@JsonClassDescription("客户合作风险评估")
public class CustomerRiskTags {

    @JsonPropertyDescription("坏账风险")
    private String badDebtRisk; // 示例: "低"
    @JsonPropertyDescription("坏账风险结论说明")
    private String badDebtRiskConclusion; // 示例: "历史无重大逾期，坏账可能性低"
    @JsonPropertyDescription("坏账风险原因分析")
    private List<String> badDebtRiskDesc; // 示例: "历史无重大逾期，坏账可能性低"

    @JsonPropertyDescription("资金占用风险")
    private String fundOccupationRisk; // 示例: "中"
    @JsonPropertyDescription("资金占用风险结论说明")
    private String fundOccupationRiskConclusion; // 示例: "授信额度大，但月流水波动略高"
    @JsonPropertyDescription("资金占用风险原因分析")
    private List<String> fundOccupationRiskDesc; // 示例: "授信额度大，但月流水波动略高"

    @JsonPropertyDescription("回款实效风险")
    private String paymentTimelinessRisk; // 示例: "良"
    @JsonPropertyDescription("回款实效风险结论说明")
    private String paymentTimelinessRiskConclusion; // 示例: "平均回款天数为3天，偶有延迟"
    @JsonPropertyDescription("回款实效风险原因分析")
    private List<String> paymentTimelinessRiskDesc; // 示例: "平均回款天数为3天，偶有延迟"

    @JsonPropertyDescription("司法诉讼风险")
    private String litigationRisk; // 示例: "低"
    @JsonPropertyDescription("司法诉讼风险结论说明")
    private String litigationRiskConclusion; // 示例: "历史诉讼次数少，法院执行记录为0"
    @JsonPropertyDescription("司法诉讼风险原因分析")
    private List<String> litigationRiskDesc; // 示例: "历史诉讼次数少，法院执行记录为0"

    @JsonPropertyDescription("经营异常风险")
    private String operatingAbnormality; // 示例: "无"
    @JsonPropertyDescription("经营异常结论说明")
    private String operatingAbnormalityConclusion; // 示例: "公司经营状态正常，无异常记录"
    @JsonPropertyDescription("经营异常原因分析")
    private List<String> operatingAbnormalityDesc; // 示例: "公司经营状态正常，无异常记录"

    @JsonPropertyDescription("其他舆情风险")
    private String otherPublicOpinionRisk; // 示例: "无"
    @JsonPropertyDescription("其他舆情结论说明")
    private String otherPublicOpinionRiskConclusion; // 示例: "公司经营状态正常，无异常记录"
    @JsonPropertyDescription("其他舆情原因分析")
    private List<String> otherPublicOpinionRiskDesc; // 示例: "公司经营状态正常，无异常记录"
    // Getters and Setters
}

