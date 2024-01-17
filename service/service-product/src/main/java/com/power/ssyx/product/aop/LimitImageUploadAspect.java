package com.power.ssyx.product.aop;

import com.github.benmanes.caffeine.cache.*;
import com.power.ssyx.annotation.SystemLimit;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.contants.SystemLimitConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Powerveil
 * @Date 2024/1/17 17:33
 */
@Component
@Aspect
@Slf4j
public class LimitImageUploadAspect {

    // TODO 现在key比较单一，而且需要工具类方便管理缓存
    // key：用户id
    // value：最近图片上传次数
    private Cache<Long, Integer> userIdCache =
            Caffeine.newBuilder()
                    .maximumSize(30000)
                    .recordStats()
//                        .expireAfterWrite(5, TimeUnit.SECONDS)
                    .expireAfter(new Expiry<Long, Integer>() {
                        @Override
                        public long expireAfterCreate(Long key, Integer value, long currentTime) {
                            return currentTime;
                        }

                        @Override
                        public long expireAfterUpdate(Long key, Integer value, long currentTime, @NonNegative long currentDuration) {
                            return currentDuration;
                        }

                        @Override
                        public long expireAfterRead(Long key, Integer value, long currentTime, @NonNegative long currentDuration) {
                            return currentDuration;
                        }
                    })

                    .removalListener(new RemovalListener<Long, Integer>() {
                        @Override
                        public void onRemoval(@Nullable Long key, @Nullable Integer value, RemovalCause removalCause) {
//                                System.out.println("移除了key：" + key + " value：" + value + " cause：" + removalCause);
                            log.info("移除了key：{} value：{} cause：{}", key, value, removalCause);
                        }
                    })
                    .build();


    // key：IP
    // value：最近图片上传次数
    private Cache<String, Integer> IPCache =
            Caffeine.newBuilder()
                    .maximumSize(3)
                    .recordStats()
//                        .expireAfterWrite(5, TimeUnit.SECONDS)
                    .expireAfter(new Expiry<String, Integer>() {
                        @Override
                        public long expireAfterCreate(String key, Integer value, long currentTime) {
                            return currentTime;
                        }

                        @Override
                        public long expireAfterUpdate(String key, Integer value, long currentTime, @NonNegative long currentDuration) {
                            return currentDuration;
                        }

                        @Override
                        public long expireAfterRead(String key, Integer value, long currentTime, @NonNegative long currentDuration) {
                            return currentDuration;
                        }
                    })

                    .removalListener(new RemovalListener<String, Integer>() {
                        @Override
                        public void onRemoval(@Nullable String key, @Nullable Integer value, RemovalCause removalCause) {
//                                System.out.println("移除了key：" + key + " value：" + value + " cause：" + removalCause);
                            log.info("移除了key：{} value：{} cause：{}", key, value, removalCause);
                        }
                    })
                    .build();

    @Pointcut("@annotation(com.power.ssyx.annotation.SystemLimit)")
    public void pt() {

    }


    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object ret;
        try {
            handleBefore(joinPoint);
            ret = joinPoint.proceed();
            handleAfter(ret);
        } finally {
            // 结束后换行
            log.info("=======End=======" + System.lineSeparator());
        }

        return ret;
    }

    private void handleAfter(Object ret) {
        // 打印出参
//        log.info("Response       : {}", JSON.toJSONString(ret));
    }

    private void handleBefore(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        // 获取被增强方法上的注解对象
        SystemLimit systemLimit = getSystemLimit(joinPoint);

        String ip = request.getRemoteHost();

        // 检查用户最近上传文件的次数
//        Long userId = AuthContextHolder.getUserId();
//        Integer recentFileUploadCount = IPCache.getIfPresent(userId);
//
//        // 设置时间数
//        int timeCount = 6;
//
//        if (Objects.isNull(recentFileUploadCount)) {
//            // 初始值为0
//            cache.policy().expireVariably()
//                    .ifPresent(policy -> policy.put(userId, 0, timeCount, TimeUnit.SECONDS));
//        } else if (recentFileUploadCount < 40) {
//            // 最近上传次数+1
//            cache.policy().expireVariably()
//                    .ifPresent(policy -> policy.put(userId, recentFileUploadCount + 1, timeCount, TimeUnit.SECONDS));
//        } else {
//            throw new SsyxException(ResultCodeEnum.IMAGE_UPLOAD_LIMIT);
//        }

        // 检查ip最近上传文件的次数
        Integer recentFileUploadCount = IPCache.getIfPresent(ip);

        // 设置时间数
        int timeCount = SystemLimitConstants.RECENT_IMAGE_TIME_COUNT;

        if (Objects.isNull(recentFileUploadCount)) {
            // 初始值为0
            IPCache.policy().expireVariably()
                    .ifPresent(policy -> policy.put(ip, 1, timeCount, TimeUnit.SECONDS));
        } else if (recentFileUploadCount < SystemLimitConstants.RECENT_IMAGE_LIMIT) {
            // 最近上传次数+1
            IPCache.policy().expireVariably()
                    .ifPresent(policy -> policy.put(ip, recentFileUploadCount + 1, timeCount, TimeUnit.SECONDS));
        } else {
            throw new SsyxException(ResultCodeEnum.IMAGE_UPLOAD_LIMIT);
        }


        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}", request.getRequestURI());
        // 打印描述信息
        log.info("BusinessName   : {}", systemLimit.businessName());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), ((MethodSignature) joinPoint.getSignature()).getName());
        // 打印请求的 IP
        log.info("IP             : {}", ip);
        // 打印请求入参
//        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    private SystemLimit getSystemLimit(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        SystemLimit annotation = methodSignature.getMethod().getAnnotation(SystemLimit.class);
        return annotation;
    }
}
