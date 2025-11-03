package com.yeahmobi.embabel.model.order;

import com.yeahmobi.embabel.annotation.FieldMeta;
import com.yeahmobi.embabel.model.CustomerDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto {
    @FieldMeta(value = "客户信息", example = "C2025001", required = true)
    List<CustomerDto> customer;
    BigDecimal amount;
}

