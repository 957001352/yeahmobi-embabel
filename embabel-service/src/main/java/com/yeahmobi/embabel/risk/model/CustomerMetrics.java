package com.yeahmobi.embabel.risk.model;

import lombok.Data;

/**
 * 客户量化指标（结构化数据）
 */
@Data
public class CustomerMetrics {
    private String companyName;
    private int historyLitigation;
    private double averagePaymentDays;
    private double monthlyFlowVolatility; // 月流水波动率 = 标准差 / 平均值
    private int maxOverdueDays;
    private int overdueCount;
    private double creditLimit;

    public CustomerMetrics(String companyName, int historyLitigation, double averagePaymentDays,
                           double monthlyFlowVolatility, int maxOverdueDays, int overdueCount,
                           double creditLimit) {
        this.companyName = companyName;
        this.historyLitigation = historyLitigation;
        this.averagePaymentDays = averagePaymentDays;
        this.monthlyFlowVolatility = monthlyFlowVolatility;
        this.maxOverdueDays = maxOverdueDays;
        this.overdueCount = overdueCount;
        this.creditLimit = creditLimit;
    }

    // Getters and Setters
}

