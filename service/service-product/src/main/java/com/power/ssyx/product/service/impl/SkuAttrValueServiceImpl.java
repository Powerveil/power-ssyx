package com.power.ssyx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.model.product.SkuAttrValue;
import com.power.ssyx.product.mapper.SkuAttrValueMapper;
import com.power.ssyx.product.service.SkuAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author power
 * @description 针对表【sku_attr_value(spu属性值)】的数据库操作Service实现
 * @createDate 2023-08-05 15:19:30
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
        implements SkuAttrValueService {

    @Override
    public List<SkuAttrValue> getAttrValueList(Long skuId) {
        LambdaQueryWrapper<SkuAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuAttrValue::getSkuId, skuId);
        queryWrapper.orderByAsc(SkuAttrValue::getSort);
        return this.list(queryWrapper);
    }

    @Override
    public boolean deleteBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuAttrValue::getSkuId, skuId);
        return this.remove(queryWrapper);
    }

    @Override
    public boolean deleteBySkuIds(List<Long> skuIds) {
        LambdaQueryWrapper<SkuAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuAttrValue::getSkuId, skuIds);
        return this.remove(queryWrapper);
    }
}




