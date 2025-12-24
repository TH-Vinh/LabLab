package com.example.springmvc.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource; // Import mới
import org.springframework.core.env.Environment; // Import mới
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:app.properties")
@ComponentScan(
        basePackages = "com.example.springmvc",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                value = org.springframework.stereotype.Controller.class
        )
)
@EnableJpaRepositories(basePackages = "com.example.springmvc.repository")
public class AppConfig {

    @Autowired
    private Environment env; // 2. Biến môi trường để lấy giá trị

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(env.getRequiredProperty("db.url"));
        dataSource.setUsername(env.getRequiredProperty("db.username"));
        dataSource.setPassword(env.getRequiredProperty("db.password"));

        // --- TỐI ƯU HÓA HIỆU NĂNG ---

        // 1. Số lượng kết nối (Cloud free tier thường giới hạn, đừng set cao quá)
        dataSource.setMinimumIdle(2); // Giữ ít nhất 2 kết nối rảnh
        dataSource.setMaximumPoolSize(10); // Tối đa 10 kết nối (đủ cho dev)

        // 2. Timeout (Giảm xuống để fail nhanh nếu mạng lag)
        dataSource.setConnectionTimeout(20000); // 20 giây chờ kết nối
        dataSource.setIdleTimeout(300000);      // 5 phút rảnh thì đóng bớt
        dataSource.setMaxLifetime(1800000);     // 30 phút refresh kết nối 1 lần

        // 3. CACHE (Cực quan trọng để tăng tốc Query)
        // Giúp lưu lại các câu lệnh SQL đã biên dịch, lần sau chạy sẽ cực nhanh
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource.addDataSourceProperty("useLocalSessionState", "true");
        dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");
        dataSource.addDataSourceProperty("cacheResultSetMetadata", "true");
        dataSource.addDataSourceProperty("maintainTimeStats", "false");

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.example.springmvc.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");
        return properties;
    }
}