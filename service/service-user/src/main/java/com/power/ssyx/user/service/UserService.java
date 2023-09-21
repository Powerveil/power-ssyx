package com.power.ssyx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.user.User;
import com.power.ssyx.vo.user.LeaderAddressVo;

/**
 * @author power
 * @description 针对表【user(会员表)】的数据库操作Service
 * @createDate 2023-09-18 21:38:03
 */
public interface UserService extends IService<User> {

    Result loginWx(String code);

    LeaderAddressVo getLeaderAddressVoByUserId(Long userId);
}
