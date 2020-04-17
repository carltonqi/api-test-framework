package com.merico.inftest.cases;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

public class TestCaseInfoNew extends StepCaseInfo {

    @JSONField(ordinal = 1)
    private String id;

    @JSONField(ordinal = 2)
    private String desc;

    @JSONField(ordinal = 3)
    private String tag;

    @JSONField(ordinal = 4)
    private String database;

    @JSONField(ordinal = 5)
    private BeforeOrAfterInfo beforeCase;

    /*@JSONField(ordinal = 6)
    private List<MafkaInfoAndExtend> mafkas;*/

    @JSONField(ordinal = 7)
    private List<RequestInfoAndAssert> requests;

    @JSONField(ordinal = 8, name = "dbasserts")
    private List<DBAssertInfo> dbAssertInfos;

    @JSONField(ordinal = 9)
    private BeforeOrAfterInfo afterCase;

    @JSONField(ordinal = 10)
    private List<Map<String, Object>> datasets;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    /*@Override
    public List<MafkaInfoAndExtend> getMafkas() {
        return mafkas;
    }

    public void setMafkas(List<MafkaInfoAndExtend> mafkas) {
        this.mafkas = mafkas;
    }*/

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

    public BeforeOrAfterInfo getBeforeCase() {
        return beforeCase;
    }

    public void setBeforeCase(BeforeOrAfterInfo beforeCase) {
        this.beforeCase = beforeCase;
    }

    public BeforeOrAfterInfo getAfterCase() {
        return afterCase;
    }

    public void setAfterCase(BeforeOrAfterInfo afterCase) {
        this.afterCase = afterCase;
    }

    public List<Map<String, Object>> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Map<String, Object>> datasets) {
        this.datasets = datasets;
    }

}
