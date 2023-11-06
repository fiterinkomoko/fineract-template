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

@XmlRootElement(name = "scoreOutput")
public class ScoreOutputData {

    private String grade;
    private String positiveScore;
    private String probability;
    private String reasonCodeAARC1;
    private String reasonCodeAARC2;
    private String reasonCodeAARC3;
    private String reasonCodeAARC4;

    @XmlElement(name = "grade")
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @XmlElement(name = "positiveScore")
    public String getPositiveScore() {
        return positiveScore;
    }

    public void setPositiveScore(String positiveScore) {
        this.positiveScore = positiveScore;
    }

    @XmlElement(name = "probability")
    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    @XmlElement(name = "reasonCodeAARC1")
    public String getReasonCodeAARC1() {
        return reasonCodeAARC1;
    }

    public void setReasonCodeAARC1(String reasonCodeAARC1) {
        this.reasonCodeAARC1 = reasonCodeAARC1;
    }

    @XmlElement(name = "reasonCodeAARC2")
    public String getReasonCodeAARC2() {
        return reasonCodeAARC2;
    }

    public void setReasonCodeAARC2(String reasonCodeAARC2) {
        this.reasonCodeAARC2 = reasonCodeAARC2;
    }

    @XmlElement(name = "reasonCodeAARC3")
    public String getReasonCodeAARC3() {
        return reasonCodeAARC3;
    }

    public void setReasonCodeAARC3(String reasonCodeAARC3) {
        this.reasonCodeAARC3 = reasonCodeAARC3;
    }

    @XmlElement(name = "reasonCodeAARC4")
    public String getReasonCodeAARC4() {
        return reasonCodeAARC4;
    }

    public void setReasonCodeAARC4(String reasonCodeAARC4) {
        this.reasonCodeAARC4 = reasonCodeAARC4;
    }

    @Override
    public String toString() {
        return "ScoreOutputData{" + "grade='" + grade + '\'' + ", positiveScore='" + positiveScore + '\'' + ", probability='" + probability
                + '\'' + ", reasonCodeAARC1='" + reasonCodeAARC1 + '\'' + ", reasonCodeAARC2='" + reasonCodeAARC2 + '\''
                + ", reasonCodeAARC3='" + reasonCodeAARC3 + '\'' + ", reasonCodeAARC4='" + reasonCodeAARC4 + '\'' + '}';
    }
}
