package com.app.config;

import com.app.dao.*;
import com.app.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;

/**
 * Created by adenau on 8/7/16.
 */
@Configuration
@ComponentScan(basePackages = {"com.app.controller"})
@EnableWebMvc
public class AppConfig {
    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPassword;

    @Value("${coord.processed}")
    private String coordProcessedTableName;
    @Value("${axis.processed}")
    private String axisProcessedTableName;
    @Value("${coord.raw}")
    private String coordRawTableName;
    @Value("${axis.raw}")
    private String axisRawTableName;

    @Bean
    public String coordProcessedTableName() {
        return coordProcessedTableName;
    }

    @Bean
    public String coordRawTableName() {
        return coordRawTableName;
    }

    @Bean
    public String axisProcessedTableName() {
        return axisProcessedTableName;
    }

    @Bean
    public String axisRawTableName() {
        return axisRawTableName;
    }

    @Bean
    public InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public static PropertyPlaceholderConfigurer connectionProperties() {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ClassPathResource[] resources = new ClassPathResource[ ] {
                new ClassPathResource("properties.properties")
        };
        ppc.setLocations( resources );
        ppc.setIgnoreUnresolvablePlaceholders( true );
        ppc.setSearchSystemEnvironment(true);
        return ppc;
    }

    //    -------------------------------DATABASE--------------------------------------------------
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");

        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setJdbcTemplate(jdbcTemplate());
        return userDao;
    }

    @Bean
    public CoordinateDao CoordinateDao() {
        CoordinateDao coordinateDao = new CoordinateDao();
        coordinateDao.setJdbcTemplate(jdbcTemplate());
        return coordinateDao;
    }

    @Bean
    public AxisDao axisDao() {
        AxisDao axisDao = new AxisDao();
        axisDao.setJdbcTemplate(jdbcTemplate());
        return axisDao;
    }

    @Bean
    public ObstacleDao obstacleDao() {
        ObstacleDao obstacleDao = new ObstacleDao();
        obstacleDao.setJdbcTemplate(jdbcTemplate());
        return obstacleDao;
    }

//    -------------------------------SERVICE--------------------------------------------------
    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public CoordinateService coordinateService() {
        return new CoordinateService();
    }

    @Bean
    public AxisService axisService() {
        return new AxisService();
    }

    @Bean
    public ObstacleService obstacleService() {
        return new ObstacleService();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver=new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        return resolver;
    }

}