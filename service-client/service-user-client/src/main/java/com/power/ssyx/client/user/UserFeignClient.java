package com.power.ssyx.client.user;

import com.power.ssyx.vo.user.LeaderAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Powerveil
 * @Date 2023/9/20 18:14
 */
@FeignClient(value = "service-user")
public interface UserFeignClient {

    // 根据userId查询提货点和团长信息
    @GetMapping("/api/user/leader/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId);
}
