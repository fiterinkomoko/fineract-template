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

import java.math.BigDecimal;

public class KivaLoanAccount {

    private BigDecimal amount;
    private String client_id;
    private String first_name;
    private String gender;
    private String last_name;
    private String loan_id;

    public KivaLoanAccount(BigDecimal amount, String client_id, String first_name, String gender, String last_name, String loan_id) {
        this.amount = amount;
        this.client_id = client_id;
        this.first_name = first_name;
        this.gender = gender;
        this.last_name = last_name;
        this.loan_id = loan_id;
    }

    @Override
    public String toString() {
        return "KivaLoanAccount{" + "amount=" + amount + ", client_id='" + client_id + '\'' + ", first_name='" + first_name + '\''
                + ", gender='" + gender + '\'' + ", last_name='" + last_name + '\'' + ", loan_id='" + loan_id + '\'' + '}';
    }
}
