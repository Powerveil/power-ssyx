package com.power.ssyx.product;

import com.power.ssyx.ServiceProductApplication;
import com.power.ssyx.product.mapper.SkuInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Powerveil
 * @Date 2023/8/13 0:16
 */
@SpringBootTest(classes = {ServiceProductApplication.class})
@Slf4j
@RunWith(SpringRunner.class)
public class Test01 {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

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

    @Test
    public void test02() {
        int check = skuInfoMapper.check(14L, 1);
        log.info("更改是否成功:{}", check);
    }
}
