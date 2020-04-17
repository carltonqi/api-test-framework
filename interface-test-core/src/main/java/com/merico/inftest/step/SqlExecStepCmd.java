package com.merico.inftest.step;

import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.cases.DBAssertInfo;
import com.merico.inftest.context.Context;
import com.merico.inftest.mysql.DBUtils;
import com.merico.inftest.response.Response;

import org.apache.commons.lang3.StringUtils;

public class SqlExecStepCmd extends StepCmd {

    private String sql;
    private String dbName;

    public SqlExecStepCmd(String dbName, String sql) {
        this.dbName = dbName;
        this.sql = sql;
    }

    @Override
    public Response doExcute(CaseInfo caseInfo, Response preResponse, Context context) throws Throwable {
        if (StringUtils.isBlank(sql)) {
            return preResponse;
        }
        Response response = DBUtils.executSql(sql, dbName);
        logger.info("sql common success! dbname:{} ,sql:{}, response:{} ", dbName, sql, response.toString());
        return response;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
