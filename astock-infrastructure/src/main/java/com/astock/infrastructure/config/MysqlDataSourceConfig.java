package com.astock.infrastructure.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import javax.sql.DataSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * MySQL 数据源与 MyBatis-Plus 会话工厂配置。
 *
 * <p>本配置显式约束 Mapper 只扫描带 {@link Mapper} 注解的接口，避免普通领域接口、Repository
 * 抽象、Engine 抽象被 MyBatis 误注册成 Mapper Bean，从根源上消除接口 Bean 与实现类 Bean 的重复注入问题。</p>
 */
@Configuration
@MapperScan(
        basePackages = "com.astock",
        annotationClass = Mapper.class,
        sqlSessionFactoryRef = "mysqlSqlSessionFactory",
        sqlSessionTemplateRef = "mysqlSqlSessionTemplate"
)
public class MysqlDataSourceConfig {

    /**
     * 读取 spring.datasource.mysql 前缀下的 MySQL 连接属性。
     *
     * @return MySQL 数据源属性
     */
    @Primary
    @Bean(name = "mysqlDataSourceProperties")
    @ConfigurationProperties("spring.datasource.mysql")
    public DataSourceProperties mysqlDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 主业务库数据源，供 MyBatis-Plus 和普通 JDBC 查询共用。
     *
     * @return MySQL 数据源
     */
    @Primary
    @Bean(name = "mysqlDataSource")
    public DataSource mysqlDataSource(@Qualifier("mysqlDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * MyBatis-Plus 使用的 MySQL SqlSessionFactory。
     *
     * @param mysqlDataSource MySQL 数据源
     * @return 绑定 MySQL 的 MyBatis SqlSessionFactory
     * @throws Exception MyBatis-Plus 工厂初始化异常
     */
    @Primary
    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource mysqlDataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(mysqlDataSource);
        return factoryBean.getObject();
    }

    /**
     * MyBatis Mapper 代理使用的 MySQL SqlSessionTemplate。
     *
     * @param mysqlSqlSessionFactory MySQL SqlSessionFactory
     * @return MySQL SqlSessionTemplate
     */
    @Primary
    @Bean(name = "mysqlSqlSessionTemplate")
    public SqlSessionTemplate mysqlSqlSessionTemplate(
            @Qualifier("mysqlSqlSessionFactory") SqlSessionFactory mysqlSqlSessionFactory) {
        return new SqlSessionTemplate(mysqlSqlSessionFactory);
    }

    /**
     * 保留给复杂 SQL 与跨表读模型使用的 NamedParameterJdbcTemplate。
     *
     * @param dataSource MySQL 数据源
     * @return 命名参数 JDBC 模板
     */
    @Primary
    @Bean(name = "mysqlNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate mysqlNamedParameterJdbcTemplate(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
