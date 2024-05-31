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

//    @ParameterizedTest
//    @CsvSource({"1,2,3", "3,4,7"})
//    public void testAdd(int a, int b, int c) {
//        long begin = System.currentTimeMillis();
//        Caculator caculator = new Caculator();
//        int add = caculator.add(a, b);
//        assert add == c;
//        long end = System.currentTimeMillis();
//        assert end - begin < 100;
//    }
//
//    @ParameterizedTest
//    @CsvSource({"2,1,1", "5,3,2"})
//    public void testSubstract(int a, int b, int c) {
//        Caculator caculator = new Caculator();
//        int subtract = caculator.subtract(a, b);
//        assert subtract == c;
//    }
//
//    @ParameterizedTest
//    @CsvSource({"2,1,2", "2,3,4"})
//    public void testMultiply(int a, int b, double c) {
//        Caculator caculator = new Caculator();
//        double multiply = caculator.multiply(a, b);
//        assert multiply == c;
//    }
//
//    @ParameterizedTest
//    @CsvSource({"2,1,2", "6,3,2"})
//    public void testDivide(int a, int b, double c) throws Exception {
//        Caculator caculator = new Caculator();
//        double divide = caculator.divide(a, b);
//        assert divide == c;
//    }


}
