package com.power.ssyx.home.service.impl;

import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.client.search.SkuFeignClient;
import com.power.ssyx.client.user.UserFeignClient;
import com.power.ssyx.common.auth.AuthContextHolder;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.home.service.HomeService;
import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.model.search.SkuEs;
import com.power.ssyx.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Powerveil
 * @Date 2023/9/20 17:48
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SkuFeignClient skuFeignClient;

    @Override
    public Result homeData(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId();
        Map<String, Object> map = new HashMap<>();

        // 1.根据userId获取当前登录语句提货地址信息
        //   远程调用service-user模块接口获取需要数据
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        map.put("leaderAddressVo", leaderAddressVo);
        // 2.获取所有分类
        //   远程调用service-product模块接口
        List<Category> categoryList = productFeignClient.findAllCategoryList();
        map.put("categoryList", categoryList);

        // 3.获取新人专享商品
        //   远程调用service-product模块接口
        List<SkuInfo> newPersonSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
        map.put("newPersonSkuInfoList", newPersonSkuInfoList);
        // 4.获取爆款商品
        //   远程调用service-search模块接口
        //   hotScore 热门评分降序排序
        List<SkuEs> hotSkuList = skuFeignClient.findHotSkuList();
        map.put("hotSkuList", hotSkuList);
        // 5.封装获取数据到map集合，返回
        return Result.ok(map);
    }
}
