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
package org.apache.fineract.portfolio.loanaccount.data;

import lombok.Data;

@Data
public class MetropolAccountInfoData {

    private Integer id;
    private String accountNumber;
    private String accountStatus;
    private String currentBalance;
    private String dateOpened;
    private Integer daysInArrears;
    private String delinquencyCode;
    private Integer highestDaysInArrears;
    private Boolean isYourAccount;
    private String lastPaymentAmount;
    private String lastPaymentDate;
    private String loadedAt;
    private String originalAmount;
    private String overdueBalance;
    private String overdueDate;
    private Integer productTypeId;

    public MetropolAccountInfoData(Integer id, String accountNumber, String accountStatus, String currentBalance, String dateOpened,
            Integer daysInArrears, String delinquencyCode, Integer highestDaysInArrears, Boolean isYourAccount, String lastPaymentAmount,
            String lastPaymentDate, String loadedAt, String originalAmount, String overdueBalance, String overdueDate,
            Integer productTypeId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountStatus = accountStatus;
        this.currentBalance = currentBalance;
        this.dateOpened = dateOpened;
        this.daysInArrears = daysInArrears;
        this.delinquencyCode = delinquencyCode;
        this.highestDaysInArrears = highestDaysInArrears;
        this.isYourAccount = isYourAccount;
        this.lastPaymentAmount = lastPaymentAmount;
        this.lastPaymentDate = lastPaymentDate;
        this.loadedAt = loadedAt;
        this.originalAmount = originalAmount;
        this.overdueBalance = overdueBalance;
        this.overdueDate = overdueDate;
        this.productTypeId = productTypeId;
    }
}
