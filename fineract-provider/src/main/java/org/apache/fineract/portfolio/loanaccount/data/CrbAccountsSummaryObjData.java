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

public class CrbAccountsSummaryObjData {

    private String allSectors;
    private String mySector;
    private String otherSectors;

    public String getAllSectors() {
        return allSectors;
    }

    public void setAllSectors(String allSectors) {
        this.allSectors = allSectors;
    }

    public String getMySector() {
        return mySector;
    }

    public void setMySector(String mySector) {
        this.mySector = mySector;
    }

    public String getOtherSectors() {
        return otherSectors;
    }

    public void setOtherSectors(String otherSectors) {
        this.otherSectors = otherSectors;
    }

    @Override
    public String toString() {
        return "CrbAccountsSummaryData{" + "allSectors=" + allSectors + ", mySector=" + mySector + ", otherSectors=" + otherSectors + '}';
    }
}
