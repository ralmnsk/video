package ralmnsk.video.dao;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@Import(SettingsConfiguration.class)
public class HibernateConfiguration {

    private final SettingsConfiguration settingsConfig;

    public HibernateConfiguration(SettingsConfiguration settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    @Bean
    public DataSource dataSource() {
        final DataSourceSettings datasourseSettings = settingsConfig.datasourseSettings();

        final HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(datasourseSettings.getUrl());
        hikariDataSource.setUsername(datasourseSettings.getUser());
        hikariDataSource.setPassword(datasourseSettings.getPassword());
        hikariDataSource.setDriverClassName(datasourseSettings.getDriver());
        hikariDataSource.setMaximumPoolSize(20);
        return hikariDataSource;
    }

    @Bean
    public LocalSessionFactoryBean entityManagerFactory() {
        final LocalSessionFactoryBean sf = new LocalSessionFactoryBean();
        sf.setDataSource(dataSource());
        sf.setPackagesToScan("ralmnsk.video.model");
        sf.setHibernateProperties(settingsConfig.hibernateProperties());

        return sf;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

}