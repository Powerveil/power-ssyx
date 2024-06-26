package com.power.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.BeanCopyUtils;
import com.power.ssyx.contants.SystemConstants;
import com.power.ssyx.model.product.SkuAttrValue;
import com.power.ssyx.model.product.SkuImage;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.model.product.SkuPoster;
import com.power.ssyx.mq.constant.MqConst;
import com.power.ssyx.mq.service.RabbitService;
import com.power.ssyx.product.mapper.SkuInfoMapper;
import com.power.ssyx.product.service.SkuAttrValueService;
import com.power.ssyx.product.service.SkuImageService;
import com.power.ssyx.product.service.SkuInfoService;
import com.power.ssyx.product.service.SkuPosterService;
import com.power.ssyx.vo.product.SkuInfoQueryVo;
import com.power.ssyx.vo.product.SkuInfoVo;
import com.power.ssyx.vo.product.SkuStockLockVo;
import io.jsonwebtoken.lang.Collections;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author power
 * @description 针对表【sku_info(sku信息)】的数据库操作Service实现
 * @createDate 2023-08-05 15:19:29
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuPosterService skuPosterService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private SkuInfoService skuInfoServiceProxy;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

//    @PostConstruct // TODO 待优化，暂时先这样，有点冗余，初步考虑用内部类，然后用单例模式获取（还没有执行这个想法）
//    public void init() throws InterruptedException {
//        Thread.sleep(10000);
//        skuInfoServiceProxy = ((SkuInfoService) AopContext.currentProxy());
//    }

    @Override
    public Result getPageList(Integer page, Integer limit, SkuInfoQueryVo skuInfoQueryVo) {
        Page<SkuInfo> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();

        String keyword = skuInfoQueryVo.getKeyword();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        String skuType = skuInfoQueryVo.getSkuType();

        // 根据名字模糊查询
        queryWrapper.like(StringUtils.hasText(keyword), SkuInfo::getSkuName, keyword);
        queryWrapper.eq(!Objects.isNull(categoryId), SkuInfo::getCategoryId, categoryId);
        queryWrapper.eq(!Objects.isNull(skuType), SkuInfo::getSkuType, skuType);

        IPage<SkuInfo> result = baseMapper.selectPage(pageParam, queryWrapper);
        return Result.ok(result);
    }

    @Override
    public Result get(Long id) {
//        SkuInfo skuInfo = this.getById(id);
//        SkuInfoVo skuInfoVo = BeanCopyUtils.copyBean(skuInfo, SkuInfoVo.class);
//        // 商品属性
//        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getAttrValueList(id);
//        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
//        // 商品图片
//        List<SkuImage> skuImageList = skuImageService.getSkuImageList(id);
//        skuInfoVo.setSkuImagesList(skuImageList);
//        // 商品海报
//        List<SkuPoster> skuPosterList = skuPosterService.getSkuPosterList(id);
//        skuInfoVo.setSkuPosterList(skuPosterList);
//        return Result.ok(skuInfoVo);
        return Result.ok(this.getSkuInfoVo(id));
    }

    @Override
    public Result saveSkuInfo(SkuInfoVo skuInfoVo) {
        // TODO 一定要使用DTO接受数据！！！
        skuInfoVo.setId(null);// 不能这样做
        // sku信息名需要存在
        String skuInfoName = skuInfoVo.getSkuName();
        if (!StringUtils.hasText(skuInfoName)) {
            return Result.build(null, ResultCodeEnum.CATEGORY_NAME_IS_BLANK);
        }
        // 数据库不能有相同的sku信息名
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfo::getSkuName, skuInfoName);
        if (this.count(queryWrapper) > 0) {
            return Result.build(null, ResultCodeEnum.CATEGORY_IS_EXIST);
        }
        // TODO 判断条件
//        if (Objects.isNull(skuAttrValueList) ||
//            Objects.isNull(skuImagesList) ||
//            Objects.isNull(skuPosterList)) {
//            return Result.build(null, ResultCodeEnum.CATEGORY_IS_EXIST);
//        }

        SkuInfo skuInfo = BeanCopyUtils.copyBean(skuInfoVo, SkuInfo.class);

        transactionTemplate.execute((status) -> {
            // 添加sku信息表
            this.save(skuInfo);
            // 重新设置skuId
            skuInfoVo.setId(skuInfo.getId());
            // 保存sku商品属性、图片、海报
            skuInfoServiceProxy = ((SkuInfoService) AopContext.currentProxy());
            skuInfoServiceProxy.saveSkuOthers(skuInfoVo);
            return Boolean.TRUE;
        });
        return Result.ok(null);
    }

    @Override
    public Result updateSkuInfoById(SkuInfoVo skuInfoVo) {
        Long skuId = skuInfoVo.getId();
        // Id不能为空
        if (Objects.isNull(skuId)) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // 查询现在的上下架状态，如果是处于上架状态，不允许修改
        SkuInfo skuInfo1 = this.getById(skuId);
        if (SystemConstants.PUBLISH_PASS.equals(skuInfo1.getPublishStatus())) {
            return Result.build(null, ResultCodeEnum.SKU_CURRENT_PUBLISHED_ERROR);
        }
        // sku信息名需要存在
        String skuInfoName = skuInfoVo.getSkuName();
        if (!StringUtils.hasText(skuInfoName)) {
            return Result.build(null, ResultCodeEnum.CATEGORY_NAME_IS_BLANK);
        }
        // 数据库不能有相同的商品分类名
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfo::getSkuName, skuInfoName);
        SkuInfo one = this.getOne(queryWrapper);
        if (!Objects.isNull(one) && !skuId.equals(one.getId())) {
            return Result.build(null, ResultCodeEnum.CATEGORY_IS_EXIST);
        }

        SkuInfo skuInfo = BeanCopyUtils.copyBean(skuInfoVo, SkuInfo.class);

        transactionTemplate.execute((status) -> {
            // 修改sku基本信息
            this.updateById(skuInfo);
            // TODO 这里看怎么说了，到底是分开还是一起，有待讨论

            // 修改商品属性
            // 修改商品图片
            // 修改海报信息
            skuInfoServiceProxy = ((SkuInfoService) AopContext.currentProxy());

            // 先删除再添加
            skuInfoServiceProxy.deleteSkuOthersBySkuId(skuId);
            skuInfoServiceProxy.saveSkuOthers(skuInfoVo);
            return Boolean.TRUE;
        });

        return Result.ok("更新商品分类成功");
    }

    @Override
    public Result deleteSkuInfoById(Long id) {
        transactionTemplate.execute((status -> {
            this.removeById(id);
            skuInfoServiceProxy = ((SkuInfoService) AopContext.currentProxy());
            skuInfoServiceProxy.deleteSkuOthersBySkuId(id);
            // 发送MQ通知ES删除数据
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                    MqConst.ROUTING_GOODS_LOWER,
                    id);
            return Boolean.TRUE;
        }));
        return Result.ok("删除商品分类成功");
    }

    // 保存sku的其他信息
    public boolean saveSkuOthers(SkuInfoVo skuInfoVo) {
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        Long skuId = skuInfoVo.getId();

        transactionTemplate.execute((status -> {
            if (!Collections.isEmpty(skuAttrValueList)) {
                for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                    skuAttrValue.setSkuId(skuId);
                }
                skuAttrValueService.saveBatch(skuAttrValueList);
            }
            // TODO 待完善 sort字段 前台表单中没有填写sort字段的操作 stu_image表中的sort字段
            if (!Collections.isEmpty(skuImagesList)) {
                for (SkuImage skuImage : skuImagesList) {
                    skuImage.setSkuId(skuId);
                }
                skuImageService.saveBatch(skuImagesList);
            }

            if (!Collections.isEmpty(skuPosterList)) {
                for (SkuPoster skuPoster : skuPosterList) {
                    skuPoster.setSkuId(skuId);
                }
                skuPosterService.saveBatch(skuPosterList);
            }
            return Boolean.TRUE;
        }));
        return true;
    }

    @Override
    public boolean deleteSkuOthersBySkuId(Long skuId) {
        transactionTemplate.execute((status -> {
            skuAttrValueService.deleteBySkuId(skuId);
            skuImageService.deleteBySkuId(skuId);
            skuPosterService.deleteBySkuId(skuId);
            return Boolean.TRUE;
        }));
        return true;
    }

    @Override
    public boolean deleteSkuOthersBySkuIds(List<Long> skuIds) {
        transactionTemplate.execute((status -> {
            skuAttrValueService.deleteBySkuIds(skuIds);
            skuImageService.deleteBySkuIds(skuIds);
            skuPosterService.deleteBySkuIds(skuIds);
            return Boolean.TRUE;
        }));
        return true;
    }

    @Override
    public Result check(Long id, Integer status) {
        transactionTemplate.execute((status1) -> {
            // 如果审核未通过 强制下架 每次调用保证数据库的数据是正确的 先下架够更新审核状态，保证上下架接口不出现意外的失败
            if (SystemConstants.CHECK_NOT_PASS.equals(status)) {
                skuInfoMapper.publish(id, SystemConstants.PUBLISH_NOT_PASS);
            }
            int ss = skuInfoMapper.check(id, status);
            return Boolean.TRUE;//TODO 所有transactionTemplate.execute内中的异常需要自行处理
        });
        return Result.ok(null);
    }

    @Override
    public Result publish(Long skuId, Integer status) {
        SkuInfo skuInfo = this.getById(skuId);
        if (SystemConstants.CHECK_NOT_PASS.equals(skuInfo.getCheckStatus())) return Result.fail("审核通过才能继续操作");
        int ss = skuInfoMapper.publish(skuId, status);
        if (SystemConstants.PUBLISH_PASS.equals(status)) { // 上架
            // 整合mq吧数据同步到es里面
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                    MqConst.ROUTING_GOODS_UPPER,
                    skuId);
        } else { // 下架
            // 整合mq吧数据同步到es里面
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                    MqConst.ROUTING_GOODS_LOWER,
                    skuId);
        }
        return Result.ok(null);
    }

    @Override
    public Result isNewPerson(Long id, Integer status) {
        int ss = skuInfoMapper.isNewPerson(id, status);
        return Result.ok(null);
    }

    @Override
    public List<SkuInfo> getSkuListByIds(List<Long> ids) {
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuInfo::getId, ids);
        List<SkuInfo> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SkuInfo::getSkuName, keyword);
        List<SkuInfo> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public List<SkuInfo> findNewPersonSkuInfoList() {
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfo::getIsNewPerson, SystemConstants.IS_NEW_PERSON);
        queryWrapper.eq(SkuInfo::getPublishStatus, SystemConstants.PUBLISH_PASS);
        queryWrapper.orderByDesc(SkuInfo::getStock); // 库存排序
        queryWrapper.last("limit 2");
        return this.list(queryWrapper);
    }

    @Override
    public SkuInfoVo getSkuInfoVo(Long skuId) {
        // 根据skuId查询skuInfo
        SkuInfo skuInfo = this.getById(skuId);
        SkuInfoVo skuInfoVo = BeanCopyUtils.copyBean(skuInfo, SkuInfoVo.class);
        // 商品属性
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getAttrValueList(skuId);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        // 商品图片
        List<SkuImage> skuImageList = skuImageService.getSkuImageList(skuId);
        skuInfoVo.setSkuImagesList(skuImageList);
        // 商品海报
        List<SkuPoster> skuPosterList = skuPosterService.getSkuPosterList(skuId);
        skuInfoVo.setSkuPosterList(skuPosterList);

        return skuInfoVo;
    }

    // 验证和锁定库存
    @Override
    public Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo) {
        // 1.判断skuStockLockVoList集合是否为空
        if (Collections.isEmpty(skuStockLockVoList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        // 2.遍历skuStockLockVoList得到每个商品，验证库存并锁定库存，具备原子性
        for (SkuStockLockVo skuStockLockVo : skuStockLockVoList) {
            this.checkLock(skuStockLockVo);
        }
        // 3.只要有一个商品锁定失败，所有锁定成功的商品都解锁 TODO 以后有操作 休息sleep
        boolean flag = skuStockLockVoList.stream()
                .anyMatch(skuStockLockVo -> !skuStockLockVo.getIsLock());
        if (flag) {
            // 所有锁定成功的商品都解锁
            skuStockLockVoList.stream().filter(SkuStockLockVo::getIsLock)
                    .forEach(skuStockLockVo -> baseMapper.unlockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum()));
            // 返回失败状态
            return Boolean.FALSE;
        }
        // 4.如果所有商品都锁定成功了，redis缓存相关数据，为了方便后面的解锁和减库存
        redisTemplate.opsForValue().set(RedisConst.STOCK_INFO + orderNo, skuStockLockVoList);
        return Boolean.TRUE;
    }

    // 解锁取消库存

    // 扣减库存成功，更新订单状态
    @Override
    public void minusStokc(String orderNo) {
        String key = RedisConst.STOCK_INFO + orderNo;
        // 从redis获取锁定库存信息
        List<SkuStockLockVo> skuStockLockVoList =
                (List<SkuStockLockVo>) redisTemplate.opsForValue().get(key);
        if (Collections.isEmpty(skuStockLockVoList)) {
            return;
        }
        // 所有锁定成功的商品都解锁
        skuStockLockVoList.stream()
                .forEach(skuStockLockVo -> baseMapper.minusStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum()));
        // 删除redis订单库存数据
        redisTemplate.delete(key);
    }

    // 解锁并取消库存
    @Override
    public Boolean unlockStockAndCancel(Map<Long, Integer> map, String orderNo) {
        // 1.解锁并取消库存
        String key = RedisConst.STOCK_INFO + orderNo;
        map.forEach((skuId, skuNum) -> baseMapper.unlockStock(skuId, skuNum));
        // 删除redis订单库存数据
        redisTemplate.delete(key);
        return Boolean.TRUE;
    }

    /**
     * 检查库存并锁定
     *
     * @param skuStockLockVo
     */
    private void checkLock(SkuStockLockVo skuStockLockVo) {
        // 获取锁
        // 公平锁
        // 是防止两台JVM机器都来加锁（普通加锁只会在当前JVM机器上生效）
        RLock rLock = this.redissonClient
                .getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());
        // 加锁
        rLock.lock();
        try {
            // 验证库存
            SkuInfo skuInfo =
                    baseMapper.checkStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            // 判断没有满足条件商品，设置isLock值false，返回
            if (Objects.isNull(skuInfo)) {
                skuStockLockVo.setIsLock(false);
                return;
            }
            // 有满足条件商品
            // 锁定库存:update
            Integer rows = baseMapper.lockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            if (rows == 1) {
                // 锁定成功
                skuStockLockVo.setIsLock(true);
            } else {
                skuStockLockVo.setIsLock(false);
            }
        } finally {
            // 解锁
            rLock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteSkuInfoByIds(List<Long> ids) {
        transactionTemplate.execute((status) -> {
            this.removeByIds(ids);
            skuInfoServiceProxy = ((SkuInfoService) AopContext.currentProxy());
            skuInfoServiceProxy.deleteSkuOthersBySkuIds(ids);
            // 发送MQ 通知ES 方案一：分多次发送MQ
//            for (Long id : ids) {
//                rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
//                        MqConst.ROUTING_GOODS_LOWER,
//                        id);
//            }

            // 方案二：一次发送多个id

            // https://www.toutiao.com/article/6763634702705230344/?&source=m_redirect
            String idsStr = org.apache.commons.lang3.StringUtils.join(ids, ",");

            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                    MqConst.ROUTING_GOODS_LOWER_STR_IDS,
                    idsStr);

            return Boolean.TRUE;
        });
        return Result.ok("批量删除商品分类成功");
    }
}




