package com.merico.inftest.cases;

import java.io.Serializable;
import java.util.List;

public abstract class StepCaseInfo implements CaseInfo, Serializable {

    public abstract List<RequestInfoAndAssert> getRequests();

    public abstract List<DBAssertInfo> getDbAssertInfos();

//    public abstract List<MafkaInfoAndExtend> getMafkas();
}
