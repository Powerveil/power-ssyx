package com.power.ssyx.common.auth;

import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.JwtHelper;
import com.power.ssyx.vo.user.UserLoginVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author Powerveil
 * @Date 2023/9/19 22:28
 */
public class UserLoginInterceptor implements HandlerInterceptor {

    private RedisTemplate redisTemplate;

    public UserLoginInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        this.getUserLoginVo(request);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthContextHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void getUserLoginVo(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("token");

        // 判断token不为空
        if (Objects.isNull(token)) {
            throw new SsyxException(ResultCodeEnum.LOGIN_AUTH);
        }

        // 从token获取userId
        Long userId = null;
        try {
            userId = JwtHelper.getUserId(token);
        } catch (Exception e) {
//            throw new SsyxException();
        }

        // 根据userId到Redis获取用户信息
        String key = RedisConst.USER_LOGIN_KEY_PREFIX + userId;
        UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue().get(key);
        if (!Objects.isNull(userLoginVo)) {
            // 获取数据放到ThreadLocal里面
            AuthContextHolder.setUserLoginVo(userLoginVo);
            AuthContextHolder.setWareId(userLoginVo.getWareId());
            AuthContextHolder.setUserId(userId);
        }

    }
}
