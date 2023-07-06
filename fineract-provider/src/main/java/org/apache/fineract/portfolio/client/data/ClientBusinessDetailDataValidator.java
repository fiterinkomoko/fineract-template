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

package org.apache.fineract.portfolio.client.data;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ClientBusinessDetailDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientBusinessDetailDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                ClientApiCollectionConstants.CLIENT_BUSINESS_DETAIL_CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiCollectionConstants.CLIENT_RESOURCE_NAME);

        final String businessTypeParameterName = "businessType";
        final Long businessType = this.fromApiJsonHelper.extractLongNamed(businessTypeParameterName, element);
        baseDataValidator.reset().parameter(businessTypeParameterName).value(businessType).notNull().integerGreaterThanZero();

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }

        LocalDate businessCreationDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.BUSINESS_CREATION_DATE, element);
        baseDataValidator.reset().parameter(ClientApiConstants.BUSINESS_CREATION_DATE).value(businessCreationDate).notNull();

        final String sourceOfCapitalParameterName = "sourceOfCapital";
        final Long sourceOfCapital = this.fromApiJsonHelper.extractLongNamed(sourceOfCapitalParameterName, element);
        baseDataValidator.reset().parameter(sourceOfCapitalParameterName).value(sourceOfCapital).notNull().integerGreaterThanZero();

        final BigDecimal startingCapital = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("startingCapital", element);
        baseDataValidator.reset().parameter("startingCapital").value(startingCapital).notNull().positiveAmount();

        final Long totalEmployee = this.fromApiJsonHelper.extractLongNamed("totalEmployee", element);
        baseDataValidator.reset().parameter("totalEmployee").value(totalEmployee).notNull().integerGreaterThanZero();

        final BigDecimal businessRevenue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("businessRevenue", element);
        baseDataValidator.reset().parameter("businessRevenue").value(businessRevenue).notNull().positiveAmount();

        final BigDecimal averageMonthlyRevenue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("averageMonthlyRevenue", element);
        baseDataValidator.reset().parameter("averageMonthlyRevenue").value(averageMonthlyRevenue).notNull().positiveAmount();

        final String reasonForBestMonthParameterName = "reasonForBestMonth";
        final String reasonForBestMonth = this.fromApiJsonHelper.extractStringNamed(reasonForBestMonthParameterName, element);
        baseDataValidator.reset().parameter(reasonForBestMonthParameterName).value(reasonForBestMonth).notNull().notExceedingLengthOf(500);

        final String bestMonthParameterName = "bestMonth";
        final String bestMonth = this.fromApiJsonHelper.extractStringNamed(bestMonthParameterName, element);
        baseDataValidator.reset().parameter(bestMonthParameterName).value(bestMonth).notNull().notExceedingLengthOf(12);

        final String reasonForWorstMonthParameterName = "reasonForWorstMonth";
        final String reasonForWorstMonth = this.fromApiJsonHelper.extractStringNamed(reasonForWorstMonthParameterName, element);
        baseDataValidator.reset().parameter(reasonForWorstMonthParameterName).value(reasonForWorstMonth).notNull()
                .notExceedingLengthOf(500);

        final String worstMonthParameterName = "worstMonth";
        final String worstMonth = this.fromApiJsonHelper.extractStringNamed(worstMonthParameterName, element);
        baseDataValidator.reset().parameter(worstMonthParameterName).value(worstMonth).notNull().notExceedingLengthOf(12);

        final Long numberOfPurchase = this.fromApiJsonHelper.extractLongNamed("numberOfPurchase", element);
        baseDataValidator.reset().parameter("numberOfPurchase").value(numberOfPurchase).notNull().integerGreaterThanZero();

        final String purchaseFrequencyParameterName = "purchaseFrequency";
        final String purchaseFrequency = this.fromApiJsonHelper.extractStringNamed(purchaseFrequencyParameterName, element);
        baseDataValidator.reset().parameter(purchaseFrequencyParameterName).value(purchaseFrequency).notNull().notExceedingLengthOf(500);

        final BigDecimal lastPurchaseAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("lastPurchaseAmount", element);
        baseDataValidator.reset().parameter("lastPurchaseAmount").value(lastPurchaseAmount).notNull().positiveAmount();

        final BigDecimal businessAssetAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("businessAssetAmount", element);
        baseDataValidator.reset().parameter("businessAssetAmount").value(businessAssetAmount).notNull().positiveAmount();

        final BigDecimal amountAtCash = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountAtCash", element);
        baseDataValidator.reset().parameter("amountAtCash").value(amountAtCash).notNull().positiveAmount();

        final BigDecimal amountAtSaving = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountAtSaving", element);
        baseDataValidator.reset().parameter("amountAtSaving").value(amountAtSaving).notNull().positiveAmount();

        final BigDecimal amountAtInventory = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountAtInventory", element);
        baseDataValidator.reset().parameter("amountAtInventory").value(amountAtInventory).notNull().positiveAmount();

        final BigDecimal fixedAssetCost = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("fixedAssetCost", element);
        baseDataValidator.reset().parameter("fixedAssetCost").value(fixedAssetCost).notNull().positiveAmount();

        final BigDecimal totalInTax = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalInTax", element);
        baseDataValidator.reset().parameter("totalInTax").value(totalInTax).notNull().positiveAmount();

        final BigDecimal totalInTransport = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalInTransport", element);
        baseDataValidator.reset().parameter("totalInTransport").value(totalInTransport).notNull().positiveAmount();

        final BigDecimal totalInRent = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalInRent", element);
        baseDataValidator.reset().parameter("totalInRent").value(totalInRent).notNull().positiveAmount();

        final BigDecimal totalInCommunication = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalInCommunication", element);
        baseDataValidator.reset().parameter("totalInCommunication").value(totalInCommunication).notNull().positiveAmount();

        final String otherExpenseParameterName = "otherExpense";
        if (this.fromApiJsonHelper.parameterExists(otherExpenseParameterName, element)) {
            final String otherExpense = this.fromApiJsonHelper.extractStringNamed(otherExpenseParameterName, element);
            baseDataValidator.reset().parameter(otherExpenseParameterName).value(otherExpense).ignoreIfNull().notExceedingLengthOf(500);
        }

        final String otherExpenseAmountParameterName = "otherExpenseAmount";
        if (this.fromApiJsonHelper.parameterExists(otherExpenseAmountParameterName, element)) {
            final BigDecimal otherExpenseAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(otherExpenseAmountParameterName,
                    element);
            baseDataValidator.reset().parameter(otherExpenseAmountParameterName).value(otherExpenseAmount).ignoreIfNull().positiveAmount();
        }

        final BigDecimal totalUtility = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("totalUtility", element);
        baseDataValidator.reset().parameter("totalUtility").value(totalUtility).notNull().positiveAmount();

        final String totalWorkerSalaryParameterName = "totalWorkerSalary";
        if (this.fromApiJsonHelper.parameterExists(totalWorkerSalaryParameterName, element)) {
            final BigDecimal totalWorkerSalary = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(totalWorkerSalaryParameterName,
                    element);
            baseDataValidator.reset().parameter(totalWorkerSalaryParameterName).value(totalWorkerSalary).ignoreIfNull().positiveAmount();
        }
        final String totalWageParameterName = "totalWage";
        if (this.fromApiJsonHelper.parameterExists(totalWageParameterName, element)) {
            final BigDecimal totalWage = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(totalWageParameterName, element);
            baseDataValidator.reset().parameter(totalWageParameterName).value(totalWage).ignoreIfNull().positiveAmount();
        }

        final String societyName = "society";
        if (this.fromApiJsonHelper.parameterExists(societyName, element)) {
            final BigDecimal society = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(societyName, element);
            baseDataValidator.reset().parameter(societyName).value(society).ignoreIfNull().positiveAmount();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }

}
