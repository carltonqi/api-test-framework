package com.merico.inftest.cases;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class BeforeOrAfterInfo extends StepCaseInfo {

    /*@JSONField(ordinal = 1)
    private List<MafkaInfoAndExtend> mafkas;*/

    @JSONField(ordinal = 2)
    private List<RequestInfoAndAssert> requests;

    @JSONField(ordinal = 3, name = "dbasserts")
    private List<DBAssertInfo> dbAssertInfos;

   /* @Override
    public List<MafkaInfoAndExtend> getMafkas() {
        return mafkas;
    }

    public void setMafkas(List<MafkaInfoAndExtend> mafkas) {
        this.mafkas = mafkas;
    }
*/
    @Override
    public List<RequestInfoAndAssert> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestInfoAndAssert> requests) {
        this.requests = requests;
    }

    @Override
    public List<DBAssertInfo> getDbAssertInfos() {
        return dbAssertInfos;
    }

    public void setDbAssertInfos(List<DBAssertInfo> dbAssertInfos) {
        this.dbAssertInfos = dbAssertInfos;
    }
}
