package com.yeahmobi.embabel.risk;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import com.yeahmobi.embabel.risk.model.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 企业客户风险分析 Agent
 */
@Agent(
        name = "CustomerRiskAnalyzer",
        description = "根据客户名称，生成该客户完整的风险分析报告（SWOT、风险标签、AI建议）",
        beanName = "customerRiskAnalyzer"
)
@Component
public class CustomerRiskAnalyzerAgent {

    @Resource
    private CustomerDataService customerDataService; // 内部系统数据服务

    /**1️⃣ 从用户输入中提取客户名称 */
    @Action(description="从用户输入中提取客户名称")
    public String extractCustomerName(UserInput userInput, Ai ai) {
        return ai
                .withDefaultLlm()
                .createObjectIfPossible(
                        """
                        从以下输入中提取出客户名称：
                        %s
                        """.formatted(userInput.getContent()),
                        String.class
                );
    }

    /** 1️⃣ 根据客户名称拉取原始数据 */
    @Action(description="根据客户名称拉取原始数据")
    public CustomerInfo extractCustomerInfo(String customerName) {
        return customerDataService.getCustomerInfo(customerName);
    }

    /** 2️⃣ 计算量化指标 */
    @Action(description="计算量化指标")
    public CustomerMetrics extractMetrics(CustomerInfo info) {
        return new CustomerMetrics(
                info.getCompanyName(),
                info.getLitigationCount(),
                info.getAveragePaymentDays(),
                info.getMonthlyFlowStdDev() / info.getMonthlyFlowAvg(),
                info.getMaxOverdueDays(),
                info.getOverdueCount(),
                info.getCreditLimit()
        );
    }

    /** 3️⃣ AI生成SWOT分析 */
    @Action(description = "AI生成SWOT分析", toolGroups = {CoreToolGroups.WEB})
    public CustomerSwot generateSwot(CustomerMetrics metrics, Ai ai) {
        String prompt = String.format("""
                根据客户指标生成SWOT分析：
                公司名称: %s
                历史诉讼次数: %d
                平均回款天数: %.1f
                流水波动率: %.2f
                最大逾期天数: %d
                逾期次数: %d
                授信额度: %.2f
                输出格式:
                Strengths: 列出优势
                Weaknesses: 列出劣势
                Opportunities: 列出机会
                Threats: 列出威胁
                """, metrics.getCompanyName(),
                metrics.getHistoryLitigation(),
                metrics.getAveragePaymentDays(),
                metrics.getMonthlyFlowVolatility(),
                metrics.getMaxOverdueDays(),
                metrics.getOverdueCount(),
                metrics.getCreditLimit());

        return ai.withDefaultLlm().createObject(prompt, CustomerSwot.class);
    }

    /** 4️⃣ AI生成风险标签与解释 */
    @Action(description = "AI生成风险标签与解释")
    public CustomerRiskTags generateRiskTags(CustomerMetrics metrics, Ai ai) {
        String prompt = String.format("""
                根据客户指标生成风险标签（低/中/高）及解释：
                历史诉讼次数: %d
                平均回款天数: %.1f
                最大逾期天数: %d
                逾期次数: %d
                流水波动率: %.2f
                """,
                metrics.getHistoryLitigation(),
                metrics.getAveragePaymentDays(),
                metrics.getMaxOverdueDays(),
                metrics.getOverdueCount(),
                metrics.getMonthlyFlowVolatility());

        return ai.withDefaultLlm().createObject(prompt, CustomerRiskTags.class);
    }

    /** 5️⃣ AI生成综合风险报告 */
    @AchievesGoal(
            description = "生成综合客户风险分析报告",
            export = @Export(
                    remote = true,
                    name = "customerRiskReport",
                    startingInputTypes = {CustomerInfo.class, CustomerSwot.class, CustomerRiskTags.class}
            )
    )
    @Action(description = "生成综合客户风险分析报告")
    public CustomerRiskReport generateReport(CustomerInfo info, CustomerSwot swot, CustomerRiskTags tags, Ai ai) {
        String prompt = String.format("""
                根据以下信息生成综合客户风险报告：
                1. 客户信息: %s
                2. SWOT分析: %s
                3. 风险标签及解释: %s
                输出内容应包含:
                - 履约能力等级及说明
                - 合作稳定性分析
                - 最近30天舆情摘要
                - 综合AI建议
                """, info.toString(), swot.toString(), tags.toString());

        return ai.withLlm(LlmOptions.withDefaultLlm().withTemperature(0.8))
                .createObject(prompt, CustomerRiskReport.class);
    }
}

