package com.yeahmobi.embabel.risk.model;

import lombok.Data;

import java.util.List;

/**
 * SWOT分析
 */
@Data
public class CustomerSwot {
    private List<String> strengths;  // 优势示例: ["成立年限久（>10年）", "注册资本高（>1000万）"]
    private List<String> weaknesses; // 劣势示例: ["多次被列入法院被执行人"]
    private List<String> opportunities; // 机会示例: ["行业景气度高", "获得政府补贴"]
    private List<String> threats; // 威胁示例: ["行业波动大", "股东频繁变更"]

    // Getters and Setters
}
