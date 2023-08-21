package com.power.ssyx.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Powerveil
 * @Date 2023/8/21 22:48
 */
@SpringBootApplication
@EnableDiscoveryClient
//@EnableFeignClients
@EnableFeignClients(basePackages = {"com.power.ssyx"})
public class ServiceActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceActivityApplication.class, args);
    }
}
