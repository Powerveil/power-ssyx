package com.power.ssyx.home.service;

import com.power.ssyx.common.result.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Powerveil
 * @Date 2023/9/20 17:48
 */
public interface HomeService {
    Result homeData(HttpServletRequest request);
}
