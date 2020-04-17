package com.merico.inftest.mysql;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.merico.inftest.commonutils.PropertyUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

public class DataSourcePools implements DataSource {

    private static Logger logger = LoggerFactory.getLogger(DataSourcePools.class);

    private static Map<String, DataSource> DATA_SOURCE_POOL = Maps.newHashMap();

    private DataSource dataSource;

    private String name;

    public String getName() {
        return name;
    }

    public DataSourcePools(String name) {
        this.name = name;
        this.dataSource = getDataSource(name);
    }

    private DataSource getDataSource(String name) {
        DataSource dataSource = DATA_SOURCE_POOL.get(name);
        if (null != dataSource) {
            return dataSource;
        }
        DataSource dataSourceNew = createDataSource(name);
        DATA_SOURCE_POOL.put(name,dataSourceNew);
        return dataSourceNew;
    }

    /**
     * 创建连接池
     * @param name
     * @return
     */
    private DataSource createDataSource(String name) {

        String driver = PropertyUtils.getProperty(name + ".jdbc.driver");
        String url = PropertyUtils.getProperty(name + ".jdbc.url");
        String userName = PropertyUtils.getProperty(name + ".jdbc.username");
        String passworld = PropertyUtils.getProperty(name + ".jdbc.password");


        Preconditions.checkArgument(StringUtils.isNotBlank(driver), "请配置数据库 database ");
        Preconditions.checkArgument(StringUtils.isNotBlank(url),"data "+name + " url is null or empty! please check!");
        Preconditions.checkArgument(StringUtils.isNotBlank(userName),"data "+name + " username is null or empty! please check!");
        Preconditions.checkArgument(StringUtils.isNotBlank(passworld),"data "+name + " passworld is null or empty! please check!");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error("数据库驱动未找到!!请检查{} driver配置是否正确 且引入对应的jar包!",name,e);
            throw new RuntimeException("数据驱动未找到! 请检查!!!!");
        }

        PoolProperties poolProperties = new PoolProperties();

        poolProperties.setDriverClassName(driver);
        poolProperties.setUrl(url);
        poolProperties.setUsername(userName);
        poolProperties.setPassword(passworld);

        poolProperties.setJmxEnabled(true);
        poolProperties.setTestWhileIdle(false);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setTestOnReturn(false);
        poolProperties.setValidationInterval(30000);
        poolProperties.setTimeBetweenEvictionRunsMillis(30000);
        poolProperties.setMaxActive(100);
        poolProperties.setInitialSize(10);
        poolProperties.setMaxWait(10000);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setMinEvictableIdleTimeMillis(30000);
        poolProperties.setMinIdle(10);
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setLogValidationErrors(false);

        //修复有时出现的 添加一下属性 Connection has been abandoned
        poolProperties.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;" +
                "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");

        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setPoolProperties(poolProperties);

        return dataSource;

    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getConnection(username,password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }
}
