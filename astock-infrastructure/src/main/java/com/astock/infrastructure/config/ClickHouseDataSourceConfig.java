package com.astock.infrastructure.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * ClickHouse 分析库数据源配置。
 *
 * <p>ClickHouse 当前只承担分析查询和快照读写，不参与 MyBatis-Plus Mapper 扫描，避免分析库
 * 与业务库的 Mapper 会话工厂边界混淆。</p>
 */
@Configuration
public class ClickHouseDataSourceConfig {

    /**
     * 读取 spring.datasource.clickhouse 前缀下的 ClickHouse 连接属性。
     *
     * @return ClickHouse 数据源属性
     */
    @Bean(name = "clickHouseDataSourceProperties")
    @ConfigurationProperties("spring.datasource.clickhouse")
    public DataSourceProperties clickHouseDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * ClickHouse 数据源。
     *
     * @param properties ClickHouse 数据源属性
     * @return ClickHouse DataSource
     */
    @Bean(name = "clickHouseDataSource")
    public DataSource clickHouseDataSource(
            @Qualifier("clickHouseDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * ClickHouse 查询模板，供快照读取、分析查询与批量写入使用。
     *
     * @param dataSource ClickHouse 数据源
     * @return 命名参数 JDBC 模板
     */
    @Bean(name = "clickHouseNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate clickHouseNamedParameterJdbcTemplate(
            @Qualifier("clickHouseDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
