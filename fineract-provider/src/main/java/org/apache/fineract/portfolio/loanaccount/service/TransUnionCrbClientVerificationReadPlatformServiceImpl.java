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
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaClientVerificationData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransUnionCrbClientVerificationReadPlatformServiceImpl implements TransUnionCrbClientVerificationReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public TransUnionRwandaClientVerificationData retrieveClientToBeVerifiedToTransUnion(Integer clientId) {
        final ConsumerCreditMapper mapper = new ConsumerCreditMapper();
        final String sql = "SELECT " + mapper.schema() + " order by cl.id ";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId });
    }

    private static final class ConsumerCreditMapper implements RowMapper<TransUnionRwandaClientVerificationData> {

        public String schema() {
            final StringBuilder sql = new StringBuilder();

            sql.append(" cl.id                                     as id, " + "       cl.firstname                              AS name1, "
                    + "       cl.lastname                               AS name2, "
                    + "       cl.middlename                             AS name3, "
                    + "       other_info.national_identification_number AS nationalID, "
                    + "       other_info.passport_number                AS passportNo, "
                    + "       cl.date_of_birth                          AS dateOfBirth " + " FROM m_client cl "
                    + " LEFT JOIN m_client_other_info other_info on cl.id = other_info.client_id "
                    + " WHERE cl.id = ? AND cl.status = 300 ");
            return sql.toString();
        }

        @Override
        public TransUnionRwandaClientVerificationData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Integer id = rs.getInt("id");
            final String name1 = rs.getString("name1");
            final String name2 = rs.getString("name2");
            final String name3 = rs.getString("name3");
            final String nationalID = rs.getString("nationalID");
            final String passportNo = rs.getString("passportNo");
            final LocalDate dateOfBirth = JdbcSupport.getLocalDate(rs, "dateOfBirth");

            return new TransUnionRwandaClientVerificationData(id, name1, name2, name3, nationalID, passportNo, dateOfBirth.toString());

        }
    }
}
