package com.power.ssyx.sys.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.sys.RegionWare;
import org.mapstruct.Mapper;

/**
 * @author power
 * @description 针对表【region_ware(城市仓库关联表)】的数据库操作Mapper
 * @createDate 2023-08-03 20:48:29
 * @Entity com.power.ssyx.domain.RegionWare
 */
@Mapper
public interface RegionWareMapper extends BaseMapper<RegionWare> {

    int updateStatus(Long id, Integer status);
}




