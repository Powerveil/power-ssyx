package com.power.ssyx.product;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Powerveil
 * @Date 2023/8/13 0:16
 */
@SpringBootTest
@Slf4j
//@RunWith(value = {ServiceProductApplication.class})
public class Test01 {
    @Test
    public void test01() {
        log.info("你好世界");
        String fileName = "aaa.jpg";
//        Date date = new Date();
//        int year = date.getYear();
//        int month = date.getMonth();
//        int day = date.getDay();
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        log.info("currentDate={}", currentDate);

        // 获取当前年份、月份和日期
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        fileName = uuid + fileName;

//        String filePath = "/" + year + "/" + month + "/" + day + "/" + fileName;
        String filePath = year + "/" + month + "/" + day + "/" + fileName;
        log.info("filePath={}", filePath);
    }
}
