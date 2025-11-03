package com.yeahmobi.embabel.risk.model;

import lombok.Data;
import java.util.List;

/**
 * 客户原始信息
 */
@Data
public class CustomerInfo {
    /** 公司名称 */
    private String companyName; // 示例: "深圳优创科技有限公司"

    /** 注册资本（万元） */
    private double registeredCapital; // 示例: 1500.0

    /** 成立年限（年） */
    private int yearsEstablished; // 示例: 12

    /** 股东结构描述 */
    private String shareholderStructure; // 示例: "三名自然人股东，主要股东持股70%"

    /** 经营状态 */
    private String operatingStatus; // 示例: "正常"

    /** 历史诉讼次数 */
    private int litigationCount; // 示例: 2

    /** 法院执行记录次数 */
    private int courtExecutionCount; // 示例: 0

    /** 平均回款天数 */
    private double averagePaymentDays; // 示例: 3.2

    /** 最大逾期天数 */
    private int maxOverdueDays; // 示例: 7

    /** 逾期次数 */
    private int overdueCount; // 示例: 2

    /** 授信额度 */
    private double creditLimit; // 示例: 5000.0

    /** 月均流水（万元） */
    private double monthlyFlowAvg; // 示例: 450.0

    /** 月流水标准差 */
    private double monthlyFlowStdDev; // 示例: 30.0

    /** 最近30天舆情摘要 */
    private List<String> recentNews; // 示例: ["客户被列入黑名单风险提示", "公司获得政府补贴", "近期媒体正面报道"]

    // Getters and Setters 省略，可使用 Lombok @Data 简化
}

