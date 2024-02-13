package com.power.ssyx.product.job;

import com.power.ssyx.common.constant.RedisConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @ClassName UpdateBlackListJob
 * @Description 定时任务删除redis文件上传黑名单中的key
 * @Author Powerveil
 * @Date 2024/2/4 21:39
 * @Version 1.0
 */
@Component
@Slf4j
public class UpdateBlackListJob {

    @Autowired
    private RedisTemplate redisTemplate;


    // 注解表示 每天中午12点触发
    @Scheduled(cron = "0 0 12 * * ?")
    public void deleteExpiredUploadBlacklistKeys() {
        // 暂时没有特定规则,就是定时删除所有的key
        Set<String> keys =
                redisTemplate.keys(RedisConst.FILE_UPLOAD_BLACK_LIST_KEY_PREFIX + "*");
        redisTemplate.delete(keys);
//        // 暂时没有特定规则,就是定时删除所有的key
//        ScanOptions options = ScanOptions.scanOptions().match(RedisConst.FILE_UPLOAD_BLACK_LIST_KEY_PREFIX + "*").build(); // 匹配指定前缀的所有键
//        Set<String> keys = redisTemplate.keys(RedisConst.FILE_UPLOAD_BLACK_LIST_KEY_PREFIX + "*"); // 获取匹配的所有键
//        if (keys != null && !keys.isEmpty()) {
//            for (String key : keys) {
//                redisTemplate.delete(key); // 逐个删除键
//            }
//        }
    }
}
