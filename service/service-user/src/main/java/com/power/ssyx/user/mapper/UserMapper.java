package com.power.ssyx.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.user.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author power
 * @description 针对表【user(会员表)】的数据库操作Mapper
 * @createDate 2023-09-18 21:38:03
 * @Entity com.power.ssyx.domain.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




