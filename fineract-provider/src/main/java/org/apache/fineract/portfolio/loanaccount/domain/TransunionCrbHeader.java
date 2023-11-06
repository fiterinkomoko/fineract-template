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
package org.apache.fineract.portfolio.loanaccount.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableWithUTCDateTimeCustom;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.data.HeaderData;

@Data
@Entity
@Table(name = "m_transunion_crb_header")
public class TransunionCrbHeader extends AbstractAuditableWithUTCDateTimeCustom {

    private static final long serialVersionUID = 9181640245194392646L;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client clientId;
    @Column(name = "crb_name")
    private String crbName;
    @Column(name = "pdf_id")
    private String pdfId;
    @Column(name = "product_display_name")
    private String productDisplayName;
    @Column(name = "report_date")
    private String reportDate;
    @Column(name = "report_type")
    private String reportType;
    @Column(name = "request_no")
    private String requestNo;
    @Column(name = "requester")
    private String requester;

    public TransunionCrbHeader() {}

    public TransunionCrbHeader(Client clientId, HeaderData headerData) {
        this.clientId = clientId;
        this.crbName = headerData.getCrbName();
        this.pdfId = headerData.getPdfId();
        this.productDisplayName = headerData.getProductDisplayName();
        this.reportDate = headerData.getReportDate();
        this.reportType = headerData.getReportType();
        this.requestNo = headerData.getRequestNo();
        this.requester = headerData.getRequester();
    }
}
