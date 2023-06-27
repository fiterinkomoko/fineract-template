package org.apache.fineract.portfolio.client.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "m_business_detail")
public class ClientBusinessDetail  extends AbstractPersistableCustom {
    private String businessType;
    private LocalDate businessCreationDate;
    private BigDecimal startingCapital;
    private String sourceOfCapital;
    private Long totalEmployee;


    private BigDecimal businessRevenue;
    private BigDecimal averageMonthlyRevenue;
    private String bestMonth;
    private String reasonForBestMonth;
    private String worstMonth;
    private String reasonForWorstMonth;
    private Long numberOfPurchase;
    private String purchaseFrequency;
    private BigDecimal totalPurchaseLastMonth;
    private String lastPurchase;
    private BigDecimal lastPurchaseAmount;
    private BigDecimal businessAsset;
    private BigDecimal amountAtCash;
    private BigDecimal amountAtSavings;
    private BigDecimal amountAtInventory;
    private BigDecimal fixedAssetsCost;


    private BigDecimal totalInTax;
    private BigDecimal totalInTransport;
    private BigDecimal totalInRent;
    private BigDecimal totalInCommunications;
    private String otherExpenses;
    private BigDecimal otherExpenseAmount;
    private BigDecimal totalUtility;
    private BigDecimal totalWorkerSalary ;
    private BigDecimal totalWage ;
}
