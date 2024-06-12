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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "summary")
public class SummaryData {

    private CrbAccountsSummaryData bouncedCheques;
    private CrbAccountsSummaryData bouncedCheques180Days;
    private CrbAccountsSummaryData bouncedCheques90Days;
    private CrbAccountsSummaryData bouncedCheques365Days;
    private CrbAccountsSummaryData fraudulentCases;
    private String lastBouncedChequeDate;
    private String lastCreditApplicationDate;
    private String lastFraudDate;
    private String lastNPAListingDate;
    private String lastPAListingDate;
    private String lastInsurancePolicyDate;
    private CrbAccountsSummaryData npaAccounts;
    private CrbAccountsSummaryData openAccounts;
    private CrbAccountsSummaryData paAccounts;
    private CrbAccountsSummaryData paAccountsWithDh;
    private CrbAccountsSummaryData closedAccounts;
    private CrbAccountsSummaryData creditApplications;
    private CrbAccountsSummaryData creditHistory;
    private CrbAccountsSummaryData enquiries31to60Days;
    private CrbAccountsSummaryData enquiries61to90Days;
    private CrbAccountsSummaryData enquiries91Days;
    private CrbAccountsSummaryData enquiriesLast30Days;
    private CrbAccountsSummaryData paClosedAccounts;
    private CrbAccountsSummaryData paClosedAccountsWithDh;
    private CrbAccountsSummaryData insurancePolicies;
    private CrbAccountsSummaryData npaOpenAccounts;
    private CrbAccountsSummaryObjData npaTotalValueList;
    private CrbAccountsSummaryData paOpenAccounts;
    private CrbAccountsSummaryData paOpenAccountsWithDh;
    private CrbAccountsSummaryData npaClosedAccounts;

    @XmlElement(name = "bouncedCheques")
    public CrbAccountsSummaryData getBouncedCheques() {
        return bouncedCheques;
    }

    public void setBouncedCheques(CrbAccountsSummaryData bouncedCheques) {
        this.bouncedCheques = bouncedCheques;
    }

    @XmlElement(name = "bouncedCheques180Days")
    public CrbAccountsSummaryData getBouncedCheques180Days() {
        return bouncedCheques180Days;
    }

    public void setBouncedCheques180Days(CrbAccountsSummaryData bouncedCheques180Days) {
        this.bouncedCheques180Days = bouncedCheques180Days;
    }

    @XmlElement(name = "bouncedCheques365Days")
    public CrbAccountsSummaryData getBouncedCheques365Days() {
        return bouncedCheques365Days;
    }

    public void setBouncedCheques365Days(CrbAccountsSummaryData bouncedCheques365Days) {
        this.bouncedCheques365Days = bouncedCheques365Days;
    }

    @XmlElement(name = "fraudulentCases")
    public CrbAccountsSummaryData getFraudulentCases() {
        return fraudulentCases;
    }

    public void setFraudulentCases(CrbAccountsSummaryData fraudulentCases) {
        this.fraudulentCases = fraudulentCases;
    }

    @XmlElement(name = "lastBouncedChequeDate")
    public String getLastBouncedChequeDate() {
        return lastBouncedChequeDate;
    }

    public void setLastBouncedChequeDate(String lastBouncedChequeDate) {
        this.lastBouncedChequeDate = lastBouncedChequeDate;
    }

    @XmlElement(name = "lastCreditApplicationDate")
    public String getLastCreditApplicationDate() {
        return lastCreditApplicationDate;
    }

    public void setLastCreditApplicationDate(String lastCreditApplicationDate) {
        this.lastCreditApplicationDate = lastCreditApplicationDate;
    }

    @XmlElement(name = "lastFraudDate")
    public String getLastFraudDate() {
        return lastFraudDate;
    }

    public void setLastFraudDate(String lastFraudDate) {
        this.lastFraudDate = lastFraudDate;
    }

    @XmlElement(name = "lastNPAListingDate")
    public String getLastNPAListingDate() {
        return lastNPAListingDate;
    }

    public void setLastNPAListingDate(String lastNPAListingDate) {
        this.lastNPAListingDate = lastNPAListingDate;
    }

    @XmlElement(name = "lastPAListingDate")
    public String getLastPAListingDate() {
        return lastPAListingDate;
    }

    public void setLastPAListingDate(String lastPAListingDate) {
        this.lastPAListingDate = lastPAListingDate;
    }

    @XmlElement(name = "npaAccounts")
    public CrbAccountsSummaryData getNpaAccounts() {
        return npaAccounts;
    }

    public void setNpaAccounts(CrbAccountsSummaryData npaAccounts) {
        this.npaAccounts = npaAccounts;
    }

    @XmlElement(name = "openAccounts")
    public CrbAccountsSummaryData getOpenAccounts() {
        return openAccounts;
    }

    public void setOpenAccounts(CrbAccountsSummaryData openAccounts) {
        this.openAccounts = openAccounts;
    }

    @XmlElement(name = "paAccounts")
    public CrbAccountsSummaryData getPaAccounts() {
        return paAccounts;
    }

    public void setPaAccounts(CrbAccountsSummaryData paAccounts) {
        this.paAccounts = paAccounts;
    }

    @XmlElement(name = "paAccountsWithDh")
    public CrbAccountsSummaryData getPaAccountsWithDh() {
        return paAccountsWithDh;
    }

    public void setPaAccountsWithDh(CrbAccountsSummaryData paAccountsWithDh) {
        this.paAccountsWithDh = paAccountsWithDh;
    }

    @XmlElement(name = "bouncedCheques90Days")
    public CrbAccountsSummaryData getBouncedCheques90Days() {
        return bouncedCheques90Days;
    }

    public void setBouncedCheques90Days(CrbAccountsSummaryData bouncedCheques90Days) {
        this.bouncedCheques90Days = bouncedCheques90Days;
    }

    @XmlElement(name = "closedAccounts")
    public CrbAccountsSummaryData getClosedAccounts() {
        return closedAccounts;
    }

    public void setClosedAccounts(CrbAccountsSummaryData closedAccounts) {
        this.closedAccounts = closedAccounts;
    }

    @XmlElement(name = "creditApplications")
    public CrbAccountsSummaryData getCreditApplications() {
        return creditApplications;
    }

    public void setCreditApplications(CrbAccountsSummaryData creditApplications) {
        this.creditApplications = creditApplications;
    }

    @XmlElement(name = "creditHistory")
    public CrbAccountsSummaryData getCreditHistory() {
        return creditHistory;
    }

    public void setCreditHistory(CrbAccountsSummaryData creditHistory) {
        this.creditHistory = creditHistory;
    }

    @XmlElement(name = "enquiries31to60Days")
    public CrbAccountsSummaryData getEnquiries31to60Days() {
        return enquiries31to60Days;
    }

    public void setEnquiries31to60Days(CrbAccountsSummaryData enquiries31to60Days) {
        this.enquiries31to60Days = enquiries31to60Days;
    }

    @XmlElement(name = "enquiries61to90Days")
    public CrbAccountsSummaryData getEnquiries61to90Days() {
        return enquiries61to90Days;
    }

    public void setEnquiries61to90Days(CrbAccountsSummaryData enquiries61to90Days) {
        this.enquiries61to90Days = enquiries61to90Days;
    }

    @XmlElement(name = "enquiries91Days")
    public CrbAccountsSummaryData getEnquiries91Days() {
        return enquiries91Days;
    }

    public void setEnquiries91Days(CrbAccountsSummaryData enquiries91Days) {
        this.enquiries91Days = enquiries91Days;
    }

    @XmlElement(name = "enquiriesLast30Days")
    public CrbAccountsSummaryData getEnquiriesLast30Days() {
        return enquiriesLast30Days;
    }

    public void setEnquiriesLast30Days(CrbAccountsSummaryData enquiriesLast30Days) {
        this.enquiriesLast30Days = enquiriesLast30Days;
    }

    @XmlElement(name = "paClosedAccounts")
    public CrbAccountsSummaryData getPaClosedAccounts() {
        return paClosedAccounts;
    }

    public void setPaClosedAccounts(CrbAccountsSummaryData paClosedAccounts) {
        this.paClosedAccounts = paClosedAccounts;
    }

    @XmlElement(name = "paClosedAccountsWithDh")
    public CrbAccountsSummaryData getPaClosedAccountsWithDh() {
        return paClosedAccountsWithDh;
    }

    public void setPaClosedAccountsWithDh(CrbAccountsSummaryData paClosedAccountsWithDh) {
        this.paClosedAccountsWithDh = paClosedAccountsWithDh;
    }

    @XmlElement(name = "insurancePolicies")
    public CrbAccountsSummaryData getInsurancePolicies() {
        return insurancePolicies;
    }

    public void setInsurancePolicies(CrbAccountsSummaryData insurancePolicies) {
        this.insurancePolicies = insurancePolicies;
    }

    @XmlElement(name = "lastInsurancePolicyDate")
    public String getLastInsurancePolicyDate() {
        return lastInsurancePolicyDate;
    }

    public void setLastInsurancePolicyDate(String lastInsurancePolicyDate) {
        this.lastInsurancePolicyDate = lastInsurancePolicyDate;
    }

    @XmlElement(name = "npaOpenAccounts")
    public CrbAccountsSummaryData getNpaOpenAccounts() {
        return npaOpenAccounts;
    }

    public void setNpaOpenAccounts(CrbAccountsSummaryData npaOpenAccounts) {
        this.npaOpenAccounts = npaOpenAccounts;
    }

    @XmlElement(name = "npaTotalValueList")
    public CrbAccountsSummaryObjData getNpaTotalValueList() {
        return npaTotalValueList;
    }

    public void setNpaTotalValueList(CrbAccountsSummaryObjData npaTotalValueList) {
        this.npaTotalValueList = npaTotalValueList;
    }

    @XmlElement(name = "paOpenAccounts")
    public CrbAccountsSummaryData getPaOpenAccounts() {
        return paOpenAccounts;
    }

    public void setPaOpenAccounts(CrbAccountsSummaryData paOpenAccounts) {
        this.paOpenAccounts = paOpenAccounts;
    }

    @XmlElement(name = "paOpenAccountsWithDh")
    public CrbAccountsSummaryData getPaOpenAccountsWithDh() {
        return paOpenAccountsWithDh;
    }

    public void setPaOpenAccountsWithDh(CrbAccountsSummaryData paOpenAccountsWithDh) {
        this.paOpenAccountsWithDh = paOpenAccountsWithDh;
    }

    @XmlElement(name = "npaClosedAccounts")
    public CrbAccountsSummaryData getNpaClosedAccounts() {
        return npaClosedAccounts;
    }

    public void setNpaClosedAccounts(CrbAccountsSummaryData npaClosedAccounts) {
        this.npaClosedAccounts = npaClosedAccounts;
    }

    @Override
    public String toString() {
        return "SummaryData{" + "bouncedCheques=" + bouncedCheques + ", bouncedCheques180Days=" + bouncedCheques180Days
                + ", bouncedCheques365Days=" + bouncedCheques365Days + ", fraudulentCases=" + fraudulentCases + ", lastBouncedChequeDate='"
                + lastBouncedChequeDate + '\'' + ", lastCreditApplicationDate='" + lastCreditApplicationDate + '\'' + ", lastFraudDate='"
                + lastFraudDate + '\'' + ", lastNPAListingDate='" + lastNPAListingDate + '\'' + ", lastPAListingDate='" + lastPAListingDate
                + '\'' + ", npaAccounts=" + npaAccounts + ", openAccounts=" + openAccounts + ", paAccounts=" + paAccounts
                + ", paAccountsWithDh=" + paAccountsWithDh + '}';
    }
}
