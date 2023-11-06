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

@XmlRootElement(name = "header")
public class HeaderData {

    private String crbName;
    private String pdfId;
    private String productDisplayName;
    private String reportDate;
    private String reportType;
    private String requestNo;
    private String requester;

    @XmlElement(name = "crbName")
    public String getCrbName() {
        return crbName;
    }

    public void setCrbName(String crbName) {
        this.crbName = crbName;
    }

    @XmlElement(name = "pdfId")
    public String getPdfId() {
        return pdfId;
    }

    public void setPdfId(String pdfId) {
        this.pdfId = pdfId;
    }

    @XmlElement(name = "productDisplayName")
    public String getProductDisplayName() {
        return productDisplayName;
    }

    public void setProductDisplayName(String productDisplayName) {
        this.productDisplayName = productDisplayName;
    }

    @XmlElement(name = "reportDate")
    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    @XmlElement(name = "reportType")
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    @XmlElement(name = "requestNo")
    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    @XmlElement(name = "requester")
    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    @Override
    public String toString() {
        return "HeaderData{" + "crbName='" + crbName + '\'' + ", pdfId='" + pdfId + '\'' + ", productDisplayName='" + productDisplayName
                + '\'' + ", reportDate='" + reportDate + '\'' + ", reportType='" + reportType + '\'' + ", requestNo='" + requestNo + '\''
                + ", requester='" + requester + '\'' + '}';
    }
}
