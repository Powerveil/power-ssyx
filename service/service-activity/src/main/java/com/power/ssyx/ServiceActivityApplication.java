package com.power.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Powerveil
 * @Date 2023/8/21 22:48
 */
@SpringBootApplication
@EnableDiscoveryClient
//@EnableFeignClients
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true) // 注意这里的 exposeProxy 设置为 true
@EnableFeignClients(basePackages = {"com.power.ssyx"})
public class ServiceActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceActivityApplication.class, args);
    }
}
