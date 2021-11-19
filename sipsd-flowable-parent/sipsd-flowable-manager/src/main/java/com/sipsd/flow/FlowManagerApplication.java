package com.sipsd.flow;

import com.sipsd.flow.config.ApplicationConfiguration;
import com.sipsd.flow.constant.FlowConstant;
import com.sipsd.flow.servlet.AppDispatcherServletConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author : chengtg
 * @title: : FlowManagerApplication
 * @projectName : flowable
 * @description: 启动类
 * @date : 2019/11/1313:34
 */
@Import({
        ApplicationConfiguration.class,
        AppDispatcherServletConfiguration.class
})
@EnableScheduling
@MapperScan(FlowConstant.MAPPER_SCAN)
@ComponentScan(basePackages = {"com.sipsd.flow","org.flow"})
@EnableTransactionManagement
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Slf4j
public class FlowManagerApplication {

    public static void main(String[] args) throws UnknownHostException {
        
        ConfigurableApplicationContext application = SpringApplication.run(FlowManagerApplication.class, args);

        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        log.info("\n----------------------------------------------------------\n\t" +
            "Application flowable is running! Access URLs:\n\t" +
            "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
            "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
            "DOC-UI: \thttp://" + ip + ":" + port + path + "/doc.html\n" +
            "----------------------------------------------------------");

      }
}
