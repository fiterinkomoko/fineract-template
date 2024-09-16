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
package org.apache.fineract.infrastructure.Odoo;

import lombok.Data;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.organisation.office.domain.Office;

@Data
public class AccountingEntry {

    private String type;
    private Integer account_id;
    private Integer partner_id;
    private Long name;
    private Double credit;
    private Double debit;
    private Long cbs_office_id;

    public AccountingEntry() {}

    public AccountingEntry(JournalEntry journalEntry, Integer accountId, Integer partnerId, Office office) {
        this.account_id = accountId;
        this.partner_id = partnerId;
        this.name = journalEntry.getId();
        this.cbs_office_id = office.getId();

        if (journalEntry.getType() == 2) {
            this.debit = journalEntry.getAmount().doubleValue();
            this.credit = 0.0;
            this.type = "debit";
        } else {
            this.credit = journalEntry.getAmount().doubleValue();
            this.debit = 0.0;
            this.type = "credit";
        }

    }
}
