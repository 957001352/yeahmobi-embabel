package com.yeahmobi.embabel.model;

import com.yeahmobi.embabel.annotation.FieldMeta;

public class CustomerDto {

//    @FieldMeta(value = "客户编号", example = "C2025001", required = true)
    private String customerCode;

    @FieldMeta(value = "客户姓名")
    private String customerName;

    @FieldMeta(value = "累计回款金额", unit = "元")
    private java.math.BigDecimal totalReceived;

    // getter / setter
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public java.math.BigDecimal getTotalReceived() { return totalReceived; }
    public void setTotalReceived(java.math.BigDecimal totalReceived) { this.totalReceived = totalReceived; }
}
