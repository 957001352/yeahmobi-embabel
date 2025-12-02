package com.yeahmobi.embabel.risk.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

/**
 * SWOT分析
 */
@Data
@JsonClassDescription("客户SWOFT评估")
public class CustomerSwot {
    @JsonPropertyDescription("优势")
    private List<String> strengths;  // 优势示例: ["成立年限久（>10年）", "注册资本高（>1000万）"]
    @JsonPropertyDescription("劣势")
    private List<String> weaknesses; // 劣势示例: ["多次被列入法院被执行人"]
    @JsonPropertyDescription("机会")
    private List<String> opportunities; // 机会示例: ["行业景气度高", "获得政府补贴"]
    @JsonPropertyDescription("威胁")
    private List<String> threats; // 威胁示例: ["行业波动大", "股东频繁变更"]

    // Getters and Setters
}
