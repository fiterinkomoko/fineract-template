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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaConsumerVerificationData;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaCorporateVerificationData;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaCrbReportData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransUnionCrbConsumerVerificationReadPlatformServiceImpl implements TransUnionCrbConsumerVerificationReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public TransUnionRwandaConsumerVerificationData retrieveConsumerToBeVerifiedToTransUnion(Long clientId) {
        final ConsumerCreditMapper mapper = new ConsumerCreditMapper();
        final String sql = "SELECT " + mapper.schema() + " order by cl.id ";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId });
    }

    @Override
    public TransUnionRwandaCorporateVerificationData retrieveCorporateToBeVerifiedToTransUnion(Long clientId) {
        final CorporateCreditMapper mapper = new CorporateCreditMapper();
        final String sql = "SELECT " + mapper.schema() + " order by cl.id ";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId });
    }

    @Override
    public TransUnionRwandaCrbReportData fetchCrbReportForTransUnion(Integer loanId) {
        final clientCrbReportMapper mapper = new clientCrbReportMapper();
        final String sql = "SELECT " + mapper.schema() + " order by h.id DESC LIMIT 1 ";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { loanId });
    }

    private static final class ConsumerCreditMapper implements RowMapper<TransUnionRwandaConsumerVerificationData> {

        public String schema() {
            final StringBuilder sql = new StringBuilder();

            sql.append(" cl.id                                     as id, " + "       cl.firstname                              AS name1, "
                    + "       cl.lastname                               AS name2, "
                    + "       cl.middlename                             AS name3, "
                    + "       other_info.national_identification_number AS nationalID, "
                    + "       other_info.passport_number                AS passportNo " + " FROM m_client cl "
                    + " LEFT JOIN m_client_other_info other_info on cl.id = other_info.client_id "
                    + " WHERE cl.id = ? AND cl.status_enum = 300 AND cl.legal_form_enum = 1 ");
            return sql.toString();
        }

        @Override
        public TransUnionRwandaConsumerVerificationData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Integer id = rs.getInt("id");
            final String name1 = rs.getString("name1");
            final String name2 = rs.getString("name2");
            final String name3 = rs.getString("name3");
            final String nationalID = rs.getString("nationalID");
            final String passportNo = rs.getString("passportNo");

            return new TransUnionRwandaConsumerVerificationData(id, name1, name2, name3, nationalID, passportNo);

        }
    }

    private static final class CorporateCreditMapper implements RowMapper<TransUnionRwandaCorporateVerificationData> {

        public String schema() {
            final StringBuilder sql = new StringBuilder();

            sql.append(" cl.id                                     as id, " + " cl.fullname                              AS companyName, "
                    + " mcnp.incorp_no                           AS companyRegNo " + " FROM m_client cl "
                    + " INNER JOIN  m_client_non_person mcnp on cl.id = mcnp.client_id "
                    + " WHERE cl.id = ? AND cl.status_enum = 300 AND cl.legal_form_enum = 2");
            return sql.toString();
        }

        @Override
        public TransUnionRwandaCorporateVerificationData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Integer id = rs.getInt("id");
            final String companyName = rs.getString("companyName");
            final String companyRegNo = rs.getString("companyRegNo");

            return new TransUnionRwandaCorporateVerificationData(id, companyName, companyRegNo);

        }
    }

    private static final class clientCrbReportMapper implements RowMapper<TransUnionRwandaCrbReportData> {

        public String schema() {
            final StringBuilder sql = new StringBuilder();

            sql.append("    h.id                      AS id, " + "       h.crb_name                AS crbName, "
                    + "       h.pdf_id                  AS pdfId, " + "       h.product_display_name    AS productDisplayName, "
                    + "       h.report_date             AS reportDate, " + "       h.report_type             AS reportType, "
                    + "       h.request_no              AS requestNo, " + "       h.requester               AS requester, "
                    + "       h.created_on_utc          AS createdOnUtc, " + "       mtcpp.crn                 AS personalCrn, "
                    + "       mtcpp.date_of_birth       AS dateOfBirth, " + "       mtcpp.full_name           AS fullName, "
                    + "       mtcpp.gender              AS gender, " + "       mtcpp.health_insurance_no AS healthInsuranceNo, "
                    + "       mtcpp.marital_status      AS maritalStatus, " + "       mtcpp.national_id         AS nationalId, "
                    + "       mtcpp.other_names         AS otherNames, " + "       mtcpp.salutation          AS salutation, "
                    + "       mtcpp.surname             AS surname, " + "       mtccp.crn                 AS corporateCrn, "
                    + "       mtccp.company_reg_no      AS companyRegNo, " + "       mtccp.company_name        AS companyName, "
                    + "       mtcso.grade               AS grade, " + "       mtcso.positive_score      AS positiveScore, "
                    + "       mtcso.possibility         AS possibility, " + "       mtcso.reason_code_aarc1   AS reasonCodeAarc1, "
                    + "       mtcso.reason_code_aarc2   AS reasonCodeAarc2, " + "       mtcso.reason_code_aarc3   AS reasonCodeAarc3, "
                    + "       mtcso.reason_code_aarc4   AS reasonCodeAarc4, " + "       c.legal_form_enum         AS clientType "
                    + " FROM m_transunion_crb_header h " + "         INNER JOIN m_loan l on h.loan_id = l.id "
                    + "         INNER JOIN m_client c on h.client_id = c.id "
                    + "         LEFT JOIN m_transunion_crb_corporate_profile mtccp on h.id = mtccp.header_id "
                    + "         LEFT JOIN m_transunion_crb_personal_profile mtcpp on h.id = mtcpp.header_id "
                    + "         LEFT JOIN m_transunion_crb_score_output mtcso on h.id = mtcso.header_id " + " WHERE h.loan_id = ? ");
            return sql.toString();
        }

        @Override
        public TransUnionRwandaCrbReportData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Integer id = rs.getInt("id");
            final String crbName = rs.getString("crbName");
            final String pdfId = rs.getString("pdfId");
            final String productDisplayName = rs.getString("productDisplayName");
            final String reportDate = rs.getString("reportDate");
            final String reportType = rs.getString("reportType");
            final String requestNo = rs.getString("requestNo");
            final String requester = rs.getString("requester");
            final LocalDate createdOnDate = JdbcSupport.getLocalDate(rs, "createdOnUtc");
            final String personalCrn = rs.getString("personalCrn");
            final String dateOfBirth = rs.getString("dateOfBirth");
            final String fullName = rs.getString("fullName");
            final String gender = rs.getString("gender");
            final String healthInsuranceNo = rs.getString("healthInsuranceNo");
            final String maritalStatus = rs.getString("maritalStatus");
            final String nationalId = rs.getString("nationalId");
            final String otherNames = rs.getString("otherNames");
            final String salutation = rs.getString("salutation");
            final String surname = rs.getString("surname");
            final String corporateCrn = rs.getString("corporateCrn");
            final String companyRegNo = rs.getString("companyRegNo");
            final String companyName = rs.getString("companyName");
            final String grade = rs.getString("grade");
            final String positiveScore = rs.getString("positiveScore");
            final String possibility = rs.getString("possibility");
            final String reasonCodeAarc1 = rs.getString("reasonCodeAarc1");
            final String reasonCodeAarc2 = rs.getString("reasonCodeAarc2");
            final String reasonCodeAarc3 = rs.getString("reasonCodeAarc3");
            final String reasonCodeAarc4 = rs.getString("reasonCodeAarc4");
            final Integer clientType = rs.getInt("clientType");

            return new TransUnionRwandaCrbReportData(id, crbName, pdfId, productDisplayName, reportDate, reportType, requestNo, requester,
                    createdOnDate, personalCrn, dateOfBirth, fullName, gender, healthInsuranceNo, maritalStatus, nationalId, otherNames,
                    salutation, surname, corporateCrn, companyRegNo, companyName, grade, positiveScore, possibility, reasonCodeAarc1,
                    reasonCodeAarc2, reasonCodeAarc3, reasonCodeAarc4, clientType);

        }
    }
}
