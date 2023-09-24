package com.power.ssyx.client.activity;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author Powerveil
 * @Date 2023/9/24 23:34
 */
@FeignClient(value = "service-activity")
public interface ActivityFeignClient {

    @ApiOperation(value = "根据skuId列表获取促销信息")
    @PostMapping("/api/activity/inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);
}
