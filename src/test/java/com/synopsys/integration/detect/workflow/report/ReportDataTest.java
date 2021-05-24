package com.synopsys.integration.detect.workflow.report;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.blackduck.api.generated.component.RiskProfileCountsView;
import com.synopsys.integration.blackduck.api.generated.enumeration.RiskPriorityType;
import com.synopsys.integration.blackduck.api.generated.view.RiskProfileView;
import com.synopsys.integration.detect.workflow.blackduck.report.BomComponent;
import com.synopsys.integration.detect.workflow.blackduck.report.ReportData;

public class ReportDataTest {
    @Test
    public void testCountsCorrect() {
        List<BomComponent> components = new LinkedList<>();

        BomComponent component1 = new BomComponent();
        RiskProfileView riskProfileView1 = new RiskProfileView();
        List<RiskProfileCountsView> counts1 = new LinkedList<>();
        RiskProfileCountsView countsView1 = new RiskProfileCountsView();
        countsView1.setCountType(RiskPriorityType.CRITICAL);
        countsView1.setCount(new BigDecimal(1));
        counts1.add(countsView1);
        riskProfileView1.setCounts(counts1);
        component1.addSecurityRiskProfile(riskProfileView1);
        components.add(component1);

        BomComponent component2 = new BomComponent();
        RiskProfileView riskProfileView2 = new RiskProfileView();
        List<RiskProfileCountsView> counts2 = new LinkedList<>();
        RiskProfileCountsView countsView2 = new RiskProfileCountsView();
        countsView2.setCountType(RiskPriorityType.HIGH);
        countsView2.setCount(new BigDecimal(1));
        counts2.add(countsView2);
        riskProfileView2.setCounts(counts2);
        component2.addSecurityRiskProfile(riskProfileView2);
        components.add(component2);

        BomComponent component3 = new BomComponent();
        RiskProfileView riskProfileView3 = new RiskProfileView();
        List<RiskProfileCountsView> counts3 = new LinkedList<>();
        RiskProfileCountsView countsView3 = new RiskProfileCountsView();
        countsView3.setCountType(RiskPriorityType.CRITICAL);
        countsView3.setCount(new BigDecimal(2));
        counts3.add(countsView3);
        riskProfileView3.setCounts(counts3);
        component3.addSecurityRiskProfile(riskProfileView3);
        components.add(component3);

        BomComponent component4 = new BomComponent();
        RiskProfileView riskProfileView4 = new RiskProfileView();
        component4.addSecurityRiskProfile(riskProfileView4);
        components.add(component4);

        ReportData reportData = new ReportData();
        reportData.setComponents(components);

        Assertions.assertEquals(2, reportData.getVulnerabilityRiskCriticalCount());
        Assertions.assertEquals(1, reportData.getVulnerabilityRiskHighCount());
        Assertions.assertEquals(0, reportData.getVulnerabilityRiskLowCount());
        Assertions.assertEquals(1, reportData.getVulnerabilityRiskNoneCount());
    }
}

