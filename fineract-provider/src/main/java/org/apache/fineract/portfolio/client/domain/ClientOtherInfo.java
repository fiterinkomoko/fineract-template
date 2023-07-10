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
package org.apache.fineract.portfolio.client.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;

@Entity
@Table(name = "m_client_other_info")
public class ClientOtherInfo extends AbstractPersistableCustom {

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @ManyToOne
    @JoinColumn(name = "strata_cv_id", nullable = false)
    private CodeValue strata;
    @ManyToOne
    @JoinColumn(name = "year_arrived_in_country_cv_id", nullable = false)
    private CodeValue yearArrivedInHostCountry;

    @ManyToOne
    @JoinColumn(name = "nationality_cv_id", nullable = false)
    private CodeValue nationality;

    @Column(name = "number_of_children")
    private Integer numberOfChildren;

    @Column(name = "number_of_dependents")
    private Integer numberOfDependents;

    @Column(name = "co_signors")
    private String coSignors;

    @Column(name = "guarantor")
    private String guarantor;

    public ClientOtherInfo() {}

    public ClientOtherInfo(Client client, CodeValue strata, CodeValue yearArrivedInHostCountry, CodeValue nationality,
            Integer numberOfChildren, Integer numberOfDependents, String coSignors, String guarantor) {
        this.client = client;
        this.strata = strata;
        this.yearArrivedInHostCountry = yearArrivedInHostCountry;
        this.nationality = nationality;
        this.numberOfChildren = numberOfChildren;
        this.numberOfDependents = numberOfDependents;
        this.coSignors = coSignors;
        this.guarantor = guarantor;

    }

    public static ClientOtherInfo createNew(JsonCommand command, Client client, final CodeValue strata, final CodeValue nationality,
            final CodeValue yearArrivedInHostCountry) {

        final Integer numberOfChildren = command.integerValueOfParameterNamed(ClientApiConstants.numberOfChildren);
        final Integer numberOfDependents = command.integerValueOfParameterNamed(ClientApiConstants.numberOfDependents);
        final String coSignors = command.stringValueOfParameterNamed(ClientApiConstants.coSignors);
        final String guarantor = command.stringValueOfParameterNamed(ClientApiConstants.guarantor);
        return new ClientOtherInfo(client, strata, yearArrivedInHostCountry, nationality, numberOfChildren, numberOfDependents, coSignors,
                guarantor);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInLongParameterNamed(ClientApiConstants.strataIdParamName, strataId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.strataIdParamName);
            actualChanges.put(ClientApiConstants.strataIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.nationalityIdParamName, nationalityId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.nationalityIdParamName);
            actualChanges.put(ClientApiConstants.nationalityIdParamName, newValue);
        }

        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.numberOfChildren, this.numberOfChildren)) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.numberOfChildren);
            actualChanges.put(ClientApiConstants.numberOfChildren, newValue);
            this.numberOfChildren = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.numberOfDependents, this.numberOfDependents)) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.numberOfDependents);
            actualChanges.put(ClientApiConstants.numberOfDependents, newValue);
            this.numberOfDependents = newValue;
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.yearArrivedInHostCountry, yearArrivedInHostCountryId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.yearArrivedInHostCountry);
            actualChanges.put(ClientApiConstants.yearArrivedInHostCountry, newValue);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.coSignors, this.coSignors)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.coSignors);
            actualChanges.put(ClientApiConstants.coSignors, newValue);
            this.coSignors = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.guarantor, this.guarantor)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.guarantor);
            actualChanges.put(ClientApiConstants.guarantor, newValue);
            this.guarantor = StringUtils.defaultIfEmpty(newValue, null);
        }
        return actualChanges;
    }

    private Long strataId() {
        Long strata = null;
        if (this.strata != null) {
            strata = this.strata.getId();
        }
        return strata;
    }

    private Long nationalityId() {
        Long nationality = null;
        if (this.nationality != null) {
            nationality = this.nationality.getId();
        }
        return nationality;
    }

    private Long yearArrivedInHostCountryId() {
        Long yearArrivedInHostCountry = null;
        if (this.yearArrivedInHostCountry != null) {
            yearArrivedInHostCountry = this.yearArrivedInHostCountry.getId();
        }
        return yearArrivedInHostCountry;
    }

    public void setStrata(CodeValue strata) {
        this.strata = strata;
    }

    public void setNationality(CodeValue nationality) {
        this.nationality = nationality;
    }

    public void setYearArrivedInHostCountry(CodeValue yearArrivedInHostCountry) {
        this.yearArrivedInHostCountry = yearArrivedInHostCountry;
    }

    public Long clientId() {
        return this.client.getId();
    }

}
