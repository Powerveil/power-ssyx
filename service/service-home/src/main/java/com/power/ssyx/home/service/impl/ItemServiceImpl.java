package com.power.ssyx.home.service.impl;

import com.power.ssyx.client.activity.ActivityFeignClient;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.client.search.SkuFeignClient;
import com.power.ssyx.common.auth.AuthContextHolder;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.home.service.ItemService;
import com.power.ssyx.vo.product.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Powerveil
 * @Date 2023/10/6 11:33
 */
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private SkuFeignClient skuFeignClient;

    @Override
    public Result index(Long skuId) {
        Long userId = AuthContextHolder.getUserId();
        Map<String, Object> map = new HashMap<>();

        // skuId查询
        CompletableFuture<SkuInfoVo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 远程调用该获取sku数据
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(skuId);
            map.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;
        }, threadPoolExecutor);

        // sku对应优惠卷信息
        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            // 远程调用获取优惠卷信息
            Map<String, Object> activityMap = activityFeignClient.findActivityAndCoupon(skuId, userId);
            map.putAll(activityMap);
        }, threadPoolExecutor);

        // 更新商品热度
        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            // 远程调用更新热度
            skuFeignClient.incrHotScore(skuId);
        }, threadPoolExecutor);

        // 任务组合
        CompletableFuture.allOf(skuInfoCompletableFuture,
                activityCompletableFuture,
                hotCompletableFuture).join();

        log.info("map:{}", map);
        return Result.ok(map);
    }
}
