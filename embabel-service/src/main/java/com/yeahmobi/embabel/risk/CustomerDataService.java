package com.yeahmobi.embabel.risk;

import com.yeahmobi.embabel.risk.model.CustomerInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CustomerDataService {

    /** 模拟根据客户ID获取数据 */
    public CustomerInfo getCustomerInfo(String customerName) {
        // 实际生产环境可调用数据库/ERP/CRM等
        CustomerInfo info = new CustomerInfo();
//        info.setCompanyName("深圳优创科技有限公司");
        info.setCompanyName(customerName);
        info.setRegisteredCapital(1500);
        info.setYearsEstablished(12);
        info.setShareholderStructure("三名自然人股东，主要股东持股70%");
        info.setOperatingStatus("正常");
        info.setLitigationCount(2);
        info.setCourtExecutionCount(0);
        info.setAveragePaymentDays(3.2);
        info.setMaxOverdueDays(7);
        info.setOverdueCount(2);
        info.setCreditLimit(5000);
        info.setMonthlyFlowAvg(450);
        info.setMonthlyFlowStdDev(30);
        info.setRecentNews(Arrays.asList(
                "客户被列入黑名单风险提示",
                "公司获得政府补贴",
                "近期媒体正面报道"
        ));
        return info;
    }
}

