package com.power.ssyx.common.auth;

import com.power.ssyx.vo.user.UserLoginVo;

/**
 * @author Powerveil
 * @Date 2023/9/19 22:21
 * 获取登录用户信息类
 */
public class AuthContextHolder {
    // 用户id
    private static ThreadLocal<Long> userId = new ThreadLocal<>();
    // 用户仓库id
    private static ThreadLocal<Long> wareId = new ThreadLocal<>();

    // 用户信息对象
    private static ThreadLocal<UserLoginVo> userLoginVo = new ThreadLocal<>();

    // userId操作的方法
    public static void setUserId(Long _userId) {
        userId.set(_userId);
    }

    public static Long getUserId() {
        return userId.get();
    }

    // wareId操作的方法
    public static void setWareId(Long _wareId) {
        wareId.set(_wareId);
    }

    public static Long getWareId() {
        return wareId.get();
    }

    // UserLoginVo操作的方法
    public static void setUserLoginVo(UserLoginVo _userLoginVo) {
        userLoginVo.set(_userLoginVo);
    }

    public static UserLoginVo getUserLoginVo() {
        return userLoginVo.get();
    }

    public static void clear() {
        userId.remove();
        wareId.remove();
        userLoginVo.remove();
    }
}
