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
    public Result get(Integer id) {
        SkuInfo skuInfo = this.getById(id);
        return Result.ok(skuInfo);
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

        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();

        // TODO
//        if (Objects.isNull(skuAttrValueList) ||
//            Objects.isNull(skuImagesList) ||
//            Objects.isNull(skuPosterList)) {
//            return Result.build(null, ResultCodeEnum.CATEGORY_IS_EXIST);
//        }


        SkuInfo skuInfo = BeanCopyUtils.copyBean(skuInfoVo, SkuInfo.class);

        transactionTemplate.execute((status) -> {
            // 添加sku信息表
            this.save(skuInfo);
            Long id = skuInfo.getId();
            if (!Collections.isEmpty(skuAttrValueList)) {
                for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                    skuAttrValue.setSkuId(id);
                }
                skuAttrValueService.saveBatch(skuAttrValueList);
            }

            if (!Collections.isEmpty(skuImagesList)) {
                for (SkuImage skuImage : skuImagesList) {
                    skuImage.setSkuId(id);
                }
                skuImageService.saveBatch(skuImagesList);
            }

            if (!Collections.isEmpty(skuPosterList)) {
                for (SkuPoster skuPoster : skuPosterList) {
                    skuPoster.setSkuId(id);
                }
                skuPosterService.saveBatch(skuPosterList);
            }

            return Boolean.TRUE;
        });
        return Result.ok(null);
    }

    @Override
    public Result updateSkuInfoById(SkuInfo skuInfo) {
        // Id不能为空
        if (Objects.isNull(skuInfo.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        // sku信息名需要存在
        String skuInfoName = skuInfo.getSkuName();
        if (!StringUtils.hasText(skuInfoName)) {
            return Result.build(null, ResultCodeEnum.CATEGORY_NAME_IS_BLANK);
        }
        // 数据库不能有相同的商品分类名
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfo::getSkuName, skuInfoName);
        SkuInfo one = getOne(queryWrapper);
        if (!one.getId().equals(skuInfo.getId())) {
            return Result.build(null, ResultCodeEnum.CATEGORY_IS_EXIST);
        }
        if (this.updateById(skuInfo)) {
            return Result.ok(null);
        }
        return Result.fail("更新商品分类失败");
    }

    @Override
    public Result deleteSkuInfoById(Integer id) {
        if (this.removeById(id)) {
            return Result.ok(null);
        }
        return Result.fail("删除商品分类失败");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteSkuInfoByIds(List<Long> ids) {
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除商品分类失败");
    }
}




