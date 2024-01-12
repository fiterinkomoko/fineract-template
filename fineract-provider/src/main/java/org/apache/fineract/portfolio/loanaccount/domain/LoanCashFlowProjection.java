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

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "m_loan_cashflow_projection")
public class LoanCashFlowProjection extends AbstractAuditableCustom {

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private LoanRepaymentScheduleInstallment scheduleInstallment;

    @Column(name = "cashflow_info_id")
    private Long cashflowInfoId;

    @Column(name = "projection_rate")
    private Integer projectionRate;
    @Column(name = "amount")
    private BigDecimal amount;

    public LoanCashFlowProjection() {}

    public LoanCashFlowProjection(LoanRepaymentScheduleInstallment scheduleInstallment, Long cashflowInfoId, Integer projectionRate,
            BigDecimal amount) {
        this.scheduleInstallment = scheduleInstallment;
        this.cashflowInfoId = cashflowInfoId;
        this.projectionRate = projectionRate;
        this.amount = amount;
    }
}
