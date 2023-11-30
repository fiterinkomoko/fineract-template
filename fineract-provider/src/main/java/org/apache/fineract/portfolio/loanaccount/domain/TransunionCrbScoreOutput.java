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
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanaccount.data.ScoreOutputData;

@Data
@Entity
@Table(name = "m_transunion_crb_score_output")
public class TransunionCrbScoreOutput extends AbstractPersistableCustom {

    private static final long serialVersionUID = 9181640245194392646L;
    @ManyToOne
    @JoinColumn(name = "header_id", nullable = true)
    private TransunionCrbHeader headerId;
    @Column(name = "grade")
    private String grade;
    @Column(name = "positive_score")
    private String positiveScore;
    @Column(name = "possibility")
    private String probability;
    @Column(name = "reason_code_aarc1")
    private String reasonCodeAARC1;
    @Column(name = "reason_code_aarc2")
    private String reasonCodeAARC2;
    @Column(name = "reason_code_aarc3")
    private String reasonCodeAARC3;
    @Column(name = "reason_code_aarc4")
    private String reasonCodeAARC4;

    public TransunionCrbScoreOutput() {}

    public TransunionCrbScoreOutput(TransunionCrbHeader headerId, ScoreOutputData scoreOutputData) {
        this.headerId = headerId;
        this.grade = scoreOutputData.getGrade();
        this.positiveScore = scoreOutputData.getPositiveScore();
        this.probability = scoreOutputData.getProbability();
        this.reasonCodeAARC1 = scoreOutputData.getReasonCodeAARC1();
        this.reasonCodeAARC2 = scoreOutputData.getReasonCodeAARC2();
        this.reasonCodeAARC3 = scoreOutputData.getReasonCodeAARC3();
        this.reasonCodeAARC4 = scoreOutputData.getReasonCodeAARC4();
    }
}
