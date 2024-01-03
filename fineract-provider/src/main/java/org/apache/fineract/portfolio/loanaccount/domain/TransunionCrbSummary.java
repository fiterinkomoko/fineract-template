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
@Table(name = "m_transunion_crb_summary")
public class TransunionCrbSummary extends AbstractAuditableWithUTCDateTimeCustom {

    private static final long serialVersionUID = 9181640245194392646L;
    @ManyToOne
    @JoinColumn(name = "header_id", nullable = true)
    private TransunionCrbHeader headerId;
    private Integer bcAllSectors;
    private Integer bcMySector;
    private Integer bcOtherSectors;
    private Integer bc180AllSectors;
    private Integer bc180MySector;
    private Integer bc180OtherSectors;
    private Integer bc90AllSectors;
    private Integer bc90MySector;
    private Integer bc90OtherSectors;
    private Integer bc365AllSectors;
    private Integer bc365MySector;
    private Integer bc365OtherSectors;
    private Integer fcAllSectors;
    private Integer fcMySector;
    private Integer fcOtherSectors;
    private String lastBouncedChequeDate;
    private String lastCreditApplicationDate;
    private String lastFraudDate;
    private String lastNPAListingDate;
    private String lastPAListingDate;
    private String lastInsurancePolicyDate;
    private Integer npaAccountsAllSectors;
    private Integer npaAccountsMySector;
    private Integer npaAccountsOtherSectors;
    private Integer openAccountsAllSectors;
    private Integer openAccountsMySector;
    private Integer openAccountsOtherSectors;
    private Integer paAccountsAllSectors;
    private Integer paAccountsMySector;
    private Integer paAccountsOtherSectors;
    private Integer paAccountsWithDhAllSectors;
    private Integer paAccountsWithDhMySector;
    private Integer paAccountsWithDhOtherSectors;
    private Integer closedAccountsAllSectors;
    private Integer closedAccountsMySector;
    private Integer closedAccountsOtherSectors;
    private Integer caAllSectors;
    private Integer caMySector;
    private Integer caOtherSectors;
    private Integer chAllSectors;
    private Integer chMySector;
    private Integer chOtherSectors;
    private Integer enq31to60DaysAllSectors;
    private Integer enq31to60DaysMySector;
    private Integer enq31to60DaysOtherSectors;
    private Integer enq61to90DaysAllSectors;
    private Integer enq61to90DaysMySector;
    private Integer enq61to90DaysOtherSectors;
    private Integer enq91to180DaysAllSectors;
    private Integer enq91to180DaysMySector;
    private Integer enq91to180DaysOtherSectors;
    private Integer enqLast30DaysAllSectors;
    private Integer enqLast30DaysMySector;
    private Integer enqLast30DaysOtherSectors;
    private Integer paClosedAccountsAllSectors;
    private Integer paClosedAccountsMySector;
    private Integer paClosedAccountsOtherSectors;
    private Integer paClosedAccountsWithDhAllSectors;
    private Integer paClosedAccountsWithDhMySector;
    private Integer paClosedAccountsWithDhOtherSectors;
    private Integer insurancePoliciesAllSectors;
    private Integer insurancePoliciesMySector;
    private Integer insurancePoliciesOtherSectors;

}
