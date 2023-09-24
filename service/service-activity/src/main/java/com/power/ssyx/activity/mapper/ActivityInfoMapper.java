package com.power.ssyx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.activity.ActivityInfo;
import com.power.ssyx.model.activity.ActivityRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author power
 * @description 针对表【activity_info(活动表)】的数据库操作Mapper
 * @createDate 2023-08-21 22:53:23
 * @Entity com.power.ssyx.activity.domain.ActivityInfo
 */
@Mapper
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    List<Long> selectSkuIdListExist(@Param("ids") List<Long> ids);

    List<ActivityRule> findActivityRules(@Param("skuId") Long skuId);
}




