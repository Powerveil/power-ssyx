package com.power.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.BeanCopyUtils;
import com.power.ssyx.model.product.SkuAttrValue;
import com.power.ssyx.model.product.SkuImage;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.model.product.SkuPoster;
import com.power.ssyx.product.mapper.SkuInfoMapper;
import com.power.ssyx.product.service.SkuAttrValueService;
import com.power.ssyx.product.service.SkuImageService;
import com.power.ssyx.product.service.SkuInfoService;
import com.power.ssyx.product.service.SkuPosterService;
import com.power.ssyx.vo.product.SkuInfoQueryVo;
import com.power.ssyx.vo.product.SkuInfoVo;
import io.jsonwebtoken.lang.Collections;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.List;
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
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuPosterService skuPosterService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private SkuInfoService skuInfoServiceProxy;

//    @PostConstruct // TODO 待优化，暂时先这样，有点冗余，初步考虑用内部类，然后用单例模式获取（还没有执行这个想法）
//    public void init() {
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
        SkuInfo skuInfo = this.getById(id);
        SkuInfoVo skuInfoVo = BeanCopyUtils.copyBean(skuInfo, SkuInfoVo.class);
        // 商品属性
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getAttrValueList(id);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        // 商品图片
        List<SkuImage> skuImageList = skuImageService.getSkuImageList(id);
        skuInfoVo.setSkuImagesList(skuImageList);
        // 商品海报
        List<SkuPoster> skuPosterList = skuPosterService.getSkuPosterList(id);
        skuInfoVo.setSkuPosterList(skuPosterList);
        return Result.ok(skuInfoVo);
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
        if (count(queryWrapper) > 0) {
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
            // 保存sku商品属性、图片、海报
            skuInfoServiceProxy = ((SkuInfoService) AopContext.currentProxy());
            skuInfoServiceProxy.saveSkuOthers(skuInfoVo);
            return Boolean.TRUE;
        });
        return Result.ok(null);
    }

    @Override
    public Result updateSkuInfoById(SkuInfoVo skuInfoVo) {
        // Id不能为空
        if (Objects.isNull(skuInfoVo.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // sku信息名需要存在
        String skuInfoName = skuInfoVo.getSkuName();
        if (!StringUtils.hasText(skuInfoName)) {
            return Result.build(null, ResultCodeEnum.CATEGORY_NAME_IS_BLANK);
        }
        // 数据库不能有相同的商品分类名
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfo::getSkuName, skuInfoName);
        SkuInfo one = getOne(queryWrapper);
        if (!Objects.isNull(one) && !skuInfoVo.getId().equals(one.getId())) {
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

            // 先删除在添加
            skuInfoServiceProxy.deleteSkuOthersBySkuId(skuInfoVo.getId());
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
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteSkuInfoByIds(List<Long> ids) {
        transactionTemplate.execute((status) -> {
            this.removeByIds(ids);
            skuInfoServiceProxy = ((SkuInfoService) AopContext.currentProxy());
            skuInfoServiceProxy.deleteSkuOthersBySkuIds(ids);
            return Boolean.TRUE;
        });
        return Result.ok("批量删除商品分类成功");
    }
}




