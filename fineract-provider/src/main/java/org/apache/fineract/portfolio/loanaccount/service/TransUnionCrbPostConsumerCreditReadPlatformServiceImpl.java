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
package org.apache.fineract.portfolio.loanaccount.service;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaConsumerCreditData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TransUnionCrbPostConsumerCreditReadPlatformServiceImpl implements TransUnionCrbPostConsumerCreditReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<TransUnionRwandaConsumerCreditData> retrieveAllConsumerCredits() {
        final ConsumerCreditMapper mapper = new ConsumerCreditMapper();
        final String sql = "select " + mapper.schema() + " order by l.id ";
        return this.jdbcTemplate.query(sql, mapper, new Object[] {});
    }

    private static final class ConsumerCreditMapper implements RowMapper<TransUnionRwandaConsumerCreditData> {

        public String schema() {
            return " l.id AS loanId,l.account_no AS accountNumber,l.loan_status_id AS loanStatus, l.currency_code AS currencyType,country_cv.code_value AS country " +
                    "        ,mc.firstname AS surName, EXTRACT(DAY FROM (now()::TIMESTAMP - mlaa.overdue_since_date_derived::TIMESTAMP)) AS daysInArrears " +
                    "        ,mc.firstname AS foreName1,mc.middlename AS foreName2,mc.lastname AS foreName3, " +
                    "       l.repayment_period_frequency_enum AS accountRepaymentTerm, " +
                    "       l.principal_disbursed_derived AS openingBalance, " +
                    "       CASE " +
                    "           WHEN l.repayment_period_frequency_enum = 0 THEN 'DLY' " +
                    "           WHEN l.repayment_period_frequency_enum = 1 THEN 'WKY' " +
                    "           WHEN l.repayment_period_frequency_enum = 2 THEN 'MTH' " +
                    "           WHEN l.repayment_period_frequency_enum = 3 THEN 'ANN' " +
                    "           ELSE 'IRR' " +
                    "           END AS accountRepaymentTerm, " +
                    "       l.total_outstanding_derived AS currentBalance, " +
                    "       'O' AS accountOwner, " +
                    "       CASE " +
                    "           WHEN l.repayment_period_frequency_enum = 1 THEN 'WKY' " +
                    "           WHEN l.repayment_period_frequency_enum = 2 THEN 'MTH' " +
                    "           WHEN l.repayment_period_frequency_enum = 3 THEN 'ANN' " +
                    "           END AS incomeFrequency, " +
                    "       l.principal_disbursed_derived + l.interest_charged_derived AS scheduledPaymentAmount, " +
                    "       mc.mobile_no AS mobileTelephone, " +
                    "       (l.principal_repaid_derived + l.interest_repaid_derived + l.fee_charges_repaid_derived + l.penalty_charges_repaid_derived) AS actualPaymentAmount, " +
                    "       l.disbursedon_date  AS dateAccountOpened, " +
                    "       l.nominal_interest_rate_per_period  AS interestRateAtDisbursement, " +
                    "       info.number_of_dependents AS noOfDependants, " +
                    "       nationality_cv.code_value AS nationality, " +
                    "       title_cv.code_value AS salutation, " +
                    "       address.postal_code AS postalCode, " +
                    "       province_cv.code_value AS physicalAddressProvince, " +
                    "       CASE " +
                    "           WHEN marital_cv.code_value = 'Single' THEN 'S' " +
                    "           WHEN marital_cv.code_value = 'Married' THEN 'M' " +
                    "           WHEN marital_cv.code_value= 'Divorced' THEN 'D' " +
                    "           WHEN marital_cv.code_value = 'Widowed' THEN 'W' " +
                    "           ELSE 'O' " +
                    "           END AS maritalStatus, " +
                    "       address.postal_code AS postalAddressNumber, " +
                    "       l.approvedon_date AS approvalDate, " +
                    "        CASE " +
                    "           WHEN gender_cv.code_value = 'Male' THEN 'M' " +
                    "           WHEN gender_cv.code_value = 'Female' THEN 'F' " +
                    "           END AS gender , " +
                    "       mlt.transaction_date AS firstPaymentDate, " +
                    "       l.closedon_date AS dateClosed, " +
                    "       CASE " +
                    "           WHEN l.loan_status_id = 300 THEN 'A' " +
                    "           WHEN l.loan_status_id = 600 THEN 'C' " +
                    "           WHEN l.loan_status_id = 601 THEN 'W' " +
                    "           WHEN l.loan_status_id = 700 THEN 'X' " +
                    "           END AS accountStatus, " +
                    "       l.number_of_repayments AS termsDuration, " +
                    "       l.last_repayment_date AS lastPaymentDate, " +
                    "       mc.date_of_birth AS dateOfBirth, " +
                    "       l.account_no AS accountNumber, " +
                    "       l.maturedon_date AS finalPaymentDate, " +
                    "       mlaa.principal_overdue_derived AS amountPastDue, " +
                    "       '40' AS category " +
                    " " +
                    "FROM m_loan l " +
                    "         INNER JOIN m_client mc on l.client_id = mc.id " +
                    "         LEFT JOIN m_client_recruitment_survey mcrs on mc.id = mcrs.client_id " +
                    "         LEFT JOIN m_code_value country_cv on mcrs.country_cv_id = country_cv.id " +
                    "         LEFT JOIN m_loan_arrears_aging mlaa on l.id = mlaa.loan_id " +
                    "         LEFT JOIN m_client_other_info info on mc.id = info.client_id " +
                    "         LEFT JOIN m_code_value nationality_cv on info.nationality_cv_id = nationality_cv.id " +
                    "         LEFT JOIN m_client_additional_info ad_info on mc.id = ad_info.client_id " +
                    "         LEFT JOIN m_code_value title_cv on ad_info.title = title_cv.id " +
                    "         LEFT JOIN m_client_address mca on mc.id = mca.client_id " +
                    "         LEFT JOIN m_address address on mca.address_id = address.id " +
                    "         LEFT JOIN m_code_value province_cv on address.state_province_id = province_cv.id " +
                    "         LEFT JOIN m_code_value marital_cv on ad_info.marital_status = marital_cv.id " +
                    "         LEFT JOIN m_code_value gender_cv on mc.gender_cv_id = gender_cv.id " +
                    "         LEFT JOIN " +
                    "    ( " +
                    "        SELECT " +
                    "            loan_id, " +
                    "            MIN(transaction_date) AS first_repayment_date " +
                    "        FROM " +
                    "            m_loan_transaction " +
                    "        WHERE " +
                    "            transaction_type_enum = 2 " +
                    "        GROUP BY " +
                    "            loan_id " +
                    "    ) AS first_repayments " +
                    "ON " +
                    "    l.id = first_repayments.loan_id " +
                    "LEFT JOIN " +
                    "    m_loan_transaction mlt " +
                    "ON " +
                    "    l.id = mlt.loan_id " +
                    "    AND first_repayments.first_repayment_date = mlt.transaction_date " +
                    " " +
                    "where l.loan_status_id IN (300,600,601,700) " +
                    "  AND mlt.transaction_type_enum = 2";
        }

        @Override
        public TransUnionRwandaConsumerCreditData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amount");
            final String loan_id = rs.getString("loan_id");
            final String client_id = rs.getString("client_id");

            return new TransUnionRwandaConsumerCreditData();
        }
    }
}
