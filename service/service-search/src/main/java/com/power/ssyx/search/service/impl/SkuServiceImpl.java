package com.power.ssyx.search.service.impl;

import com.power.ssyx.client.activity.ActivityFeignClient;
import com.power.ssyx.client.product.ProductFeignClient;
import com.power.ssyx.common.auth.AuthContextHolder;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.enums.SkuType;
import com.power.ssyx.model.product.Category;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.model.search.SkuEs;
import com.power.ssyx.search.repository.SkuRepository;
import com.power.ssyx.search.service.SkuService;
import com.power.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:31
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Result upperSku(Long skuId) {
        // 1.通过远程调用，根据skuId获取相关信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (Objects.isNull(skuInfo)) {
            return Result.fail(null);
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        // 2.获取数据封装SkuEs对象
        SkuEs skuEs = new SkuEs();

        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());

        // TODO 下面是原来的普通善品的操作
        skuEs.setSkuType(0);
        skuEs.setPrice(skuInfo.getPrice().doubleValue());
        skuEs.setStock(skuInfo.getStock());
        skuEs.setSale(skuInfo.getSale());
        skuEs.setPerLimit(skuInfo.getPerLimit());
        if (skuInfo.getSkuType() == SkuType.COMMON.getCode()) { // 普通商品
//            skuEs.setSkuType(0);
//            skuEs.setPrice(skuInfo.getPrice().doubleValue());
//            skuEs.setStock(skuInfo.getStock());
//            skuEs.setSale(skuInfo.getSale());
//            skuEs.setPerLimit(skuInfo.getPerLimit());
        } else {
            //TODO 待完善-秒杀商品

        }
        // 3.调用方法添加ES
        SkuEs save = skuRepository.save(skuEs);

        return Result.ok(null);
    }

    @Override
    public Result lowerSku(Long skuId) {
        skuRepository.deleteById(skuId);
        return null;
    }

    @Override
    public List<SkuEs> findHotSkuList() {
        // find read get开头
        // 关联条件关键字
        // 0代表第一页
        Pageable pageable = PageRequest.of(0, 10);
        Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(pageable);
        List<SkuEs> skuEsList = pageModel.getContent();
        return skuEsList;
    }

    @Override
    public Result search(Integer page, Integer limit, SkuEsQueryVo skuEsQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        // 1.想SkuEsQueryVo设置wareId，当前登录用户的仓库id
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());
        Page<SkuEs> pageModel = null;
        // 2.调用SkuRepository方法，根据springData命名规则定义方法，进行条件查询
        String keyword = skuEsQueryVo.getKeyword();
        ////  判断keyword是否为空，如果为空，根据仓库id + 分类id查询
        ////                     如果不为空，根据仓库id + 分类id查询 + keyword进行查询
        if (StringUtils.hasText(keyword)) {
            pageModel = skuRepository.findByCategoryIdAndWareIdAndKeywordContaining(skuEsQueryVo.getCategoryId(),
                    skuEsQueryVo.getWareId(),
                    keyword,
                    pageable);
        } else {
            pageModel = skuRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId(),
                    skuEsQueryVo.getWareId(),
                    pageable);
        }
        // 3.查询商品参加优惠活动
        List<SkuEs> skuEsList = pageModel.getContent();
        if (!CollectionUtils.isEmpty(skuEsList)) {
            List<Long> skuIdList = skuEsList.stream().map(SkuEs::getId).collect(Collectors.toList());
            // 根据skuId列表远程调用，调用service-activity里面的接口得到数据
            // 返回Map<Long,List<String>>
            //// map集合key就是skuId值，Long类型
            //// map集合value是List集合，sku参与活动里面可以有多个规则
            //// 比如有活动；中秋节满减活动
            //// 一个活动可以有多个规则
            //// 中秋节满减活动有两个规则：满20元减1元，满58元减5元
            Map<Long, List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);

            // 封装获取数据到skuEs里面 ruleList里面
            if (!Objects.isNull(skuIdToRuleListMap)) {
                skuEsList.forEach(skuEs -> skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId())));
            }
        }


        return Result.ok(pageModel);
    }

    // 更新商品热度
    @Override
    public Boolean incrHotScore(Long skuId) {
        // redis把欧尼数据，每次+1
        String value = "skuId:" + skuId;
        Double hotScore =
                redisTemplate.opsForZSet().incrementScore(RedisConst.HOT_SCORE_KEY,
                        RedisConst.SKU_ID_KEY_PREFIX,
                        1);
        // 规则
        if (hotScore % 10 == 0) {
            // 更新es
            Optional<SkuEs> optional = skuRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            skuRepository.save(skuEs);
        }
        return true;
    }
}
