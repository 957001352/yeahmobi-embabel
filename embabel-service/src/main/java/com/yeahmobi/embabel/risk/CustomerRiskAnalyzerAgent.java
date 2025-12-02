package com.yeahmobi.embabel.risk;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeahmobi.embabel.risk.model.*;
import com.yeahmobi.kunlun.rm.portrait.CustomerSentimentInformation;
import com.yeahmobi.kunlun.rm.portrait.EdiCompanyInfoDto;
import com.yeahmobi.kunlun.rm.portrait.ai.CustomerBasicRiskQxbInfoIndex;
import com.yeahmobi.kunlun.rm.processor.entity.CooperationPaymentHistoryData;
import com.yeahmobi.kunlun.rm.processor.entity.CustomerLimitInfoData;
import com.yeahmobi.kunlun.rm.processor.entity.CustomerProfileResult;
import com.yeahmobi.kunlun.rm.processor.entity.CustomerRiskProfileData;
import jakarta.annotation.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public String extractCustomerName(UserInput userInput, OperationContext context) {
        return context.ai()
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
    @Action(description = "根据客户名称查询客户的舆情和合作信息", toolGroups = {"risk"})
    public CustomerSentimentInformation extractKunlunCustomerCode(@Param("customerName") String customerName, OperationContext context) {
        System.out.println("extractKunlunCustomerCode customerName ---> " + customerName);
        LlmOptions llmOptions = new LlmOptions();
        llmOptions.withMaxTokens(5000);
        llmOptions.setRole("cheapest");
        CustomerSentimentInformation customerSentimentInformation = context.ai()
                .withLlm(llmOptions)
                .withTemplate("risk-analyzer/customer_risk_tool")
                .createObject(
                        CustomerSentimentInformation.class,
                        Map.of(
                                "input", customerName
                        )
                );
        return customerSentimentInformation;
    }

    /** 3️⃣ AI生成SWOT分析 */
    @Action(description = "AI生成客户SWOT分析")
    public CustomerSwot generateSwot(CustomerSentimentInformation customerSentimentInformation, OperationContext context) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(customerSentimentInformation);
        System.out.println("--------------->"+jsonData);
        return context.ai().withDefaultLlm()
                .withTemplate("risk-analyzer/customer_swoft_analysis")
                .createObject(
                        CustomerSwot.class,
                        Map.of(
                                "input", jsonData
                        )
                );
    }

    /** 4️⃣ AI生成客户合作风险评估 */
    @Action(description = "AI生成客户合作风险评估")
    public CustomerRiskTags assessCustomerRiskAssessment(CustomerSentimentInformation customerSentimentInformation, OperationContext context) {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        CustomerProfileResult internalOpinion = customerSentimentInformation.getInternalOpinion();
        int count = 0;
        if(internalOpinion != null){
            CustomerLimitInfoData customerLimitInfoData = internalOpinion.getCustomerLimitInfoData();
            if(customerLimitInfoData != null){
                BigDecimal creditLimit = customerLimitInfoData.getCreditLimit();
                sb.append(index++).append(". 授信额度（USD）: ").append(String.format("%.2f", creditLimit)).append("\n");
                String contractPeriodName = customerLimitInfoData.getContractPeriodName();
                sb.append(index++).append(". 最大账期（天）: ").append(contractPeriodName).append("\n");
                String controlTypeName = customerLimitInfoData.getControlTypeName();
                sb.append(index++).append(". 管控形式: ").append(controlTypeName).append("\n");
            }
            CooperationPaymentHistoryData cooperationPaymentHistoryData = internalOpinion.getCooperationPaymentHistoryData();
            if(cooperationPaymentHistoryData != null){
                Double avgMonthlyCollectionDays = cooperationPaymentHistoryData.getAvgMonthlyCollectionDays();
                sb.append(index++).append(". 月均回款天数: ").append(String.format("%.2f", avgMonthlyCollectionDays)).append("\n");
            }
            CustomerRiskProfileData customerRiskProfileData = internalOpinion.getCustomerRiskProfileData();
            if(customerRiskProfileData != null){
                String isAgent = customerRiskProfileData.getIsAgent();
                sb.append(index++).append(". 是否代理: ").append(isAgent).append("\n");
                String existValidInsuranceName = customerRiskProfileData.getExistValidInsuranceName();
                sb.append(index++).append(". 是否有有效担保: ").append(existValidInsuranceName).append("\n");
            }
        }
        CustomerBasicRiskQxbInfoIndex customerBasicRiskQxbInfo = customerSentimentInformation.getCustomerBasicRiskQxbInfoIndex();
        if(customerBasicRiskQxbInfo != null){
            String riskInfo = customerBasicRiskQxbInfo.toString();
            sb.append(index++).append(". 客户舆情信息: ").append(riskInfo).append("\n");
            String isXbt = "否";
            sb.append(index++).append(". 是否海外: ").append(isXbt).append("\n");
        }else {
            EdiCompanyInfoDto customerBasicRiskXbtInfo = customerSentimentInformation.getCustomerBasicRiskXbtInfo();
            if(customerBasicRiskXbtInfo != null){
                String isXbt = "是";
                sb.append(index++).append(". 是否海外: ").append(isXbt).append("\n");
                String riskInfo = customerBasicRiskXbtInfo.toString();
                sb.append(index++).append(". 客户舆情信息: ").append(riskInfo).append("\n");
            }else {
                String isXbt = "否";
                sb.append(index++).append(". 是否海外: ").append(isXbt).append("\n");
            }
        }
        if(!sb.isEmpty()){
            // 移除最后一个换行符
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return context.ai().withDefaultLlm()
                    .withTemplate("risk-analyzer/customer_risk_assessment")
                    .createObject(
                            CustomerRiskTags.class,
                            Map.of(
                                    "input", sb.toString()
                            )
                    );
        }
        return null;
    }

    /** 4️⃣ AI生成客户履约能力与合作稳定性评估 */
    @Action(description = "AI生成客户履约能力与合作稳定性评估")
    public CustomerReliability calculateCustomerReliability(CustomerSentimentInformation customerSentimentInformation, OperationContext context) {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        BigDecimal creditLimit = null;
        BigDecimal averageIncome = null;
        if(customerSentimentInformation.getInternalOpinion() != null) {
            CustomerLimitInfoData customerLimitInfoData = customerSentimentInformation.getInternalOpinion().getCustomerLimitInfoData();
            if(customerLimitInfoData != null){
                creditLimit = customerLimitInfoData.getCreditLimit();
                sb.append(index++).append(". 授信额度: ").append(String.format("%.2f", creditLimit.doubleValue())).append("\n");
                BigDecimal overdueMoney = customerLimitInfoData.getOverdueMoney();
                if(overdueMoney != null && overdueMoney.compareTo(BigDecimal.ZERO) > 0){
                    String isOverdue = "是";
                    sb.append(index++).append(". 当前是否逾期: ").append(isOverdue).append("\n");
                }else {
                    String isOverdue = "否";
                    sb.append(index++).append(". 当前是否逾期: ").append(isOverdue).append("\n");

                }
            }
            CooperationPaymentHistoryData cooperationPaymentHistoryData = customerSentimentInformation.getInternalOpinion().getCooperationPaymentHistoryData();
            if (cooperationPaymentHistoryData != null) {
                Integer repaymentCount = cooperationPaymentHistoryData.getRepaymentCount();
                sb.append(index++).append(". 还款次数: ").append(repaymentCount).append("\n");
                Integer overdueThirtyThousandMaxDay = cooperationPaymentHistoryData.getOverdueThirtyThousandMaxDay();
                sb.append(index++).append(". 3W以上最大逾期天数: ").append(overdueThirtyThousandMaxDay).append("\n");
                Integer overdueFifteenCount = cooperationPaymentHistoryData.getOverdueFifteenCount();
                sb.append(index++).append(". 逾期超过15天次数: ").append(overdueFifteenCount).append("\n");
                Double avgMonthlyCollectionDays = cooperationPaymentHistoryData.getAvgMonthlyCollectionDays();
                sb.append(index++).append(". 月均回款天数: ").append(String.format("%.2f", avgMonthlyCollectionDays)).append("\n");
                BigDecimal advanceRepaymentCountRatio = cooperationPaymentHistoryData.getAdvanceRepaymentCountRatio();
                sb.append(index++).append(". 提前还款次数占比: ").append(String.format("%.2f", advanceRepaymentCountRatio.doubleValue())).append("\n");
                Integer maxContinuousCooperationMonths = cooperationPaymentHistoryData.getMaxContinuousCooperationMonths();
                sb.append(index++).append(". 连续合作月份: ").append(maxContinuousCooperationMonths).append("\n");
                BigDecimal standardDeviation = cooperationPaymentHistoryData.getStandardDeviation();
                sb.append(index++).append(". 近6个月的流水波动率: ").append(String.format("%.2f", standardDeviation.doubleValue())).append("\n");
                averageIncome = cooperationPaymentHistoryData.getAverageIncome();
                sb.append(index++).append(". 近6个月月均流水: ").append(String.format("%.2f", averageIncome.doubleValue())).append("\n");
            }
        }
        double averageIncomeRadio = 0.0;
        if(creditLimit != null && averageIncome != null){
            if(creditLimit.compareTo(BigDecimal.ZERO) != 0){
                averageIncomeRadio = averageIncome
                        .divide(creditLimit,4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                sb.append(index++).append(". 近6个月月均流水占比: ").append(String.format("%.4f", averageIncomeRadio)).append("\n");
            }else {
                if(averageIncome.compareTo(BigDecimal.ZERO) != 0){
                    averageIncomeRadio = 100.0;
                }
                sb.append(index++).append(". 近6个月月均流水占比: ").append(String.format("%.4f", averageIncomeRadio)).append("\n");
            }
        }

        if(!sb.isEmpty()){
            // 移除最后一个换行符
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return context.ai().withDefaultLlm()
                    .withTemplate("risk-analyzer/customer_reliability")
                    .createObject(
                            CustomerReliability.class,
                            Map.of(
                                    "input", sb.toString()
                            )
                    );
        }
        return null;
    }

    /** 5️⃣ AI生成综合风险报告 */
    @AchievesGoal(
            description = "生成综合客户风险分析报告",
            export = @Export(
                    remote = true,
                    name = "customerRiskReport",
                    startingInputTypes = {CustomerSwot.class, CustomerRiskTags.class, CustomerReliability.class}
            )
    )
    @Action(description = "生成综合客户风险分析报告")
    public CustomerRiskReport generateReport(CustomerSwot swot,
                                             CustomerRiskTags riskTags,
                                             CustomerReliability reliability,
                                             OperationContext context) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是一名企业信用风控分析师。");

        // 添加数据部分
        boolean hasData = false;
        List<String> sections = new ArrayList<>();
        List<String> summaries = new ArrayList<>();

        if (swot != null) {
            hasData = true;
            sections.add("1. SWOT分析：" + objectMapper.writeValueAsString(swot));
            summaries.add("- SWOT分析：仅总结输入内容");
        }

        if (riskTags != null) {
            hasData = true;
            sections.add("2. 客户合作风险：" + objectMapper.writeValueAsString(riskTags));
            summaries.add("- 客户合作风险：仅总结输入内容");
        }

        if (reliability != null) {
            hasData = true;
            sections.add("3. 客户履约能力与合作稳定性：" + objectMapper.writeValueAsString(reliability));
            summaries.add("- 客户履约能力与合作稳定性：仅总结输入内容");
        }

        // 构建prompt
        if (hasData) {
            String sectionDesc = sections.size() == 1 ? "一段 JSON 是上游系统的分析结果" : sections.size() + "段 JSON 是上游系统的分析结果";
            prompt.append("下面").append(sectionDesc).append("，请勿修改其内容、勿新增事实、勿进行推测，只基于它们生成总结性结论。\n\n")
                    .append(String.join("\n", sections))
                    .append("\n\n请生成以下客户风险报告（仅输出存在数据的部分）：\n")
                    .append(String.join("\n", summaries))
                    .append("\n- 综合AI建议：基于以上内容给出授信与合作建议，但不得创造不存在的数据（禁止出现具体金额、具体数字）");
        } else {
            prompt.append("请基于已有信息生成客户风险报告。\n\n请生成以下客户风险报告：\n")
                    .append("- 综合AI建议：基于已有信息给出授信与合作建议，但不得创造不存在的数据（禁止出现具体金额、具体数字）");
            return null;
        }

        prompt.append("\n\n语言要求专业、中立。");

        String finalPrompt = prompt.toString();

        LlmOptions llmOptions = new LlmOptions();
        llmOptions.withMaxTokens(5000);
        llmOptions.setTemperature(0.8);
        llmOptions.setRole("best");
        return context.ai().withLlm(llmOptions)
                .createObject(finalPrompt, CustomerRiskReport.class);
    }
}

