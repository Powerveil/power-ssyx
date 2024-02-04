//package com.power.ssyx.product.job;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.cache.RedisCache;
//import org.springframework.data.redis.core.Cursor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ScanOptions;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//
///**
// * @ClassName UpdateBlackListJob
// * @Description 更新redis文件上传黑名单
// * @Author Powerveil
// * @Date 2024/2/4 21:39
// * @Version 1.0
// */
//@Component
//@Slf4j
//public class UpdateBlackListJob {
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//
//    // 注解表示每隔12小时执行一次
//    @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
//    public void deleteExpiredUploadBlacklistKeys() throws UnsupportedEncodingException {
//// 使用 SCAN 命令进行迭代
//        ScanOptions options = ScanOptions.scanOptions().match("upload:blacklist:*").build();
//        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);
//
//        while (cursor.hasNext()) {
//            byte[] rawKey = cursor.next();
//            String key = new String(rawKey, String.valueOf(redisTemplate.getStringSerializer()));
//
//            // 检查键是否过期
//            Long ttl = redisTemplate.getExpire(key);
//
//            // 如果过期时间小于等于0，表示键已过期
//            if (ttl != null && ttl <= 0) {
//                // 删除过期键
//                redisTemplate.delete(key);
//                System.out.println("Deleted expired key: " + key);
//            }
//        }
//    }
//}
