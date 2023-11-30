/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import javax.persistence.*;
import lombok.Data;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableWithUTCDateTimeCustom;

@Data
@Entity
@Table(name = "m_metropol_crb_credit_info_enhanced_report")
public class MetropolAccountInfo extends AbstractAuditableWithUTCDateTimeCustom {

    @ManyToOne
    @JoinColumn(name = "creditInfoEnhancedId", nullable = true)
    private MetropolCrbCreditInfoEnhancedReport crbCreditInfoEnhancedReport;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "account_status")
    private String accountStatus;
    @Column(name = "current_balance")
    private String currentBalance;
    @Column(name = "date_opened")
    private String dateOpened;
    @Column(name = "days_in_arrears")
    private Integer daysInArrears;
    @Column(name = "delinquency_code")
    private String delinquencyCode;
    @Column(name = "highest_days_in_arrears")
    private Integer highestDaysInArrears;
    @Column(name = "is_your_account")
    private Boolean isYourAccount;
    @Column(name = "last_payment_amount")
    private String lastPaymentAmount;
    @Column(name = "last_payment_date")
    private String lastPaymentDate;
    @Column(name = "loaded_at")
    private String loadedAt;
    @Column(name = "original_amount")
    private String originalAmount;
    @Column(name = "overdue_balance")
    private String overdueBalance;
    @Column(name = "overdue_date")
    private String overdueDate;
    @Column(name = "product_type_id")
    private Integer productTypeId;

}
