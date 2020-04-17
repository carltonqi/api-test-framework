package com.merico.inftest.mysql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.merico.inftest.response.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DBUtils {

    private static Logger logger = LoggerFactory.getLogger(DBUtils.class);

    /**
     * 执行sql查询 结果返回jsonArray
     * 每行数据包装成一个对象 列名为key 只每列的值为value 例如返回数据如下：
     * |  id| name | age |
     * |  1 | 张三  | 18  |
     * 返回json数据为[{"id":1,"name":"张三","age":18}]
     * 如果执行失败 抛出异常
     *
     * @param sql
     * @param dbName
     * @return
     * @throws SQLException
     */
    public static Response executSql(String sql, String dbName) throws SQLException {

        DataSourcePools dataSourcePool = new DataSourcePools(dbName);
        //sql需要区分查询sql 和 非查询sql
        sql = sql.trim();
        List<String> sqlList = Splitter.on(";")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(sql);

        Connection connection = null;

        List<Map<String, Object>> sqlResult = Lists.newArrayList();

        try {
            connection = dataSourcePool.getConnection();
            if ((sqlList.size() == 1) & (sql.startsWith("select") || sql.startsWith("SELECT"))) {
                //执行单个查询sql
                sqlResult = query(sql, connection);
            } else {
                //执行多sql或非查询sql
                sqlResult = excute(sqlList, connection);
            }

            String jsonString = JSON.toJSONString(sqlResult, SerializerFeature.WriteMapNullValue);
            return new Response(jsonString);

        } finally {
            close(connection);
        }

    }

    /**
     * 执行多sql
     *
     * @param sqlList
     * @param connection
     * @return
     * @throws SQLException
     */
    private static List<Map<String, Object>> excute(List<String>  sqlList, Connection connection) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();

            for (String s : sqlList) {
                statement.addBatch(s);
            }
            int[] ints = statement.executeBatch();
            Map<String, Object> result = Maps.newLinkedHashMap();
            result.put("result", ints);
            return Arrays.asList(result);
        } finally {
            close(statement);
        }

    }

    /**
     * 执行query 查询
     *
     * @param sql
     * @param connection
     * @return
     * @throws SQLException
     */
    private static List<Map<String, Object>> query(String sql, Connection connection) throws SQLException {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount(); //查询出的字段个数
            List<Map<String, Object>> reulst = Lists.newArrayList();

            while (resultSet.next()) {
                reulst.add(getRowData(resultSet, columnCount));
            }

            close(resultSet);
            return reulst;
        } finally {
            close(statement);
        }
    }

    /**
     * 读取一条数据  放入map其中 key为列名称 value为该列的值
     *
     * @param resultSet
     * @param columnCount
     * @return
     * @throws SQLException
     */
    private static Map<String, Object> getRowData(ResultSet resultSet, int columnCount) throws SQLException {
        Map<String, Object> rowMap = Maps.newLinkedHashMap();

        for (int i = 1; i <= columnCount; i++) {
            rowMap.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(i));
        }

        return rowMap;
    }

    private static void close(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (SQLException e) {
            logger.error("close statement error", e);
        }
    }

    private static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error("Result close error! ", e);
            }
        }
    }

    private static void close(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("connection close error!", e);
        }
    }

}
