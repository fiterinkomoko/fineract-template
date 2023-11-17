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
import lombok.RequiredArgsConstructor;
import org.apache.fineract.portfolio.loanaccount.data.CrbKenyaMetropolRequestData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetropolCrbReadPlatformServiceImpl implements MetropolCrbReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public CrbKenyaMetropolRequestData fetchIdentityVerificationDetails(Integer loanId) {
        final identityVerificationCreditMapper mapper = new identityVerificationCreditMapper();
        final String sql = "SELECT " + mapper.schema() + " order by idty.id DESC LIMIT 1 ";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { loanId });
    }

    private static final class identityVerificationCreditMapper implements RowMapper<CrbKenyaMetropolRequestData> {

        public String schema() {
            final StringBuilder sql = new StringBuilder();

            sql.append("    idty.client_id      AS clientId, " + "       idty.loan_id        AS loanId, "
                    + "       idty.id             AS id, " + "       idty.citizenship    AS citizenShip, "
                    + "       idty.clan           AS clan, " + "       idty.date_of_birth  AS dateOfBirth, "
                    + "       idty.date_of_death  AS dateOfDeath, " + "       idty.date_of_issue  As dateOfIssue, "
                    + "       idty.ethnic_group   AS ethnicGroup, " + "       idty.family         AS family, "
                    + "       idty.first_name     AS firstName, " + "       idty.gender         AS gender, "
                    + "       identity_number     AS identityNumber, " + "       CASE "
                    + "           WHEN idty.identity_type = '001' THEN 'National ID' "
                    + "           WHEN idty.identity_type = '002' THEN 'Passport' "
                    + "           WHEN idty.identity_type = '003' THEN 'Service ID' "
                    + "           WHEN idty.identity_type = '004' THEN 'Alien Registration' "
                    + "           WHEN idty.identity_type = '005' THEN 'Company/Business Registration' " + "           ELSE 'Unknown Type' "
                    + "           END             AS identityType, " + "       idty.last_name      AS lastName, "
                    + "       idty.occupation     AS occupation, " + "       idty.other_name     AS otherName, "
                    + "       idty.place_of_birth AS placeOfBirth, " + "       idty.place_of_death AS placeOfDeath, "
                    + "       idty.place_of_live  AS placeOfLive, " + "       idty.reg_office     AS refOffice, "
                    + "       idty.serial_number  AS serialNumber, " + "       idty.trx_id         AS trxId, "
                    + "       idty.created_on_utc As createdOn " + " FROM m_metropol_crb_identity_report idty "
                    + "         INNER JOIN m_client mc on idty.client_id = mc.id "
                    + "         INNER JOIN m_loan ml on idty.loan_id = ml.id " + "         WHERE idty.loan_id = ? ");
            return sql.toString();
        }

        @Override
        public CrbKenyaMetropolRequestData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Integer id = rs.getInt("id");
            final Integer clientId = rs.getInt("clientId");
            final Integer loanId = rs.getInt("loanId");
            final String citizenShip = rs.getString("citizenShip");
            final String clan = rs.getString("clan");
            final String dateOfBirth = rs.getString("dateOfBirth");
            final String dateOfDeath = rs.getString("dateOfDeath");
            final String dateOfIssue = rs.getString("dateOfIssue");
            final String ethnicGroup = rs.getString("ethnicGroup");
            final String family = rs.getString("family");
            final String firstName = rs.getString("firstName");
            final String gender = rs.getString("gender");
            final String identityNumber = rs.getString("identityNumber");
            final String identityType = rs.getString("identityType");
            final String lastName = rs.getString("lastName");
            final String occupation = rs.getString("occupation");
            final String otherName = rs.getString("otherName");
            final String placeOfBirth = rs.getString("placeOfBirth");
            final String placeOfDeath = rs.getString("placeOfDeath");
            final String placeOfLive = rs.getString("placeOfLive");
            final String refOffice = rs.getString("refOffice");
            final String serialNumber = rs.getString("serialNumber");
            final String trxId = rs.getString("trxId");
            final String createdOn = rs.getString("createdOn");

            return new CrbKenyaMetropolRequestData(id, clientId, loanId, citizenShip, clan, dateOfBirth, dateOfDeath, dateOfIssue,
                    ethnicGroup, family, firstName, gender, identityNumber, identityType, lastName, occupation, otherName, placeOfBirth,
                    placeOfDeath, placeOfLive, refOffice, serialNumber, trxId, createdOn);

        }
    }
}
