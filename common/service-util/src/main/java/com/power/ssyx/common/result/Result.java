package com.power.ssyx.common.result;

import lombok.Data;

/**
 * @author Powerveil
 * @Date 2023/7/22 17:12
 */
@Data
public class Result<T> {
    // 状态码
    private Integer code;
    // 信息
    private String message;
    // 数据
    private T data;

    // 构造私有化
    private Result() {
    }

    // 设置数据,返回对象的方法
    public static <T> Result<T> build(T data, ResultCodeEnum resultCode) {
        // 创建Result对象，设置值，返回对象
        Result<T> result = new Result<>();
        // 判断返回结果中是否有数据
        result.setData(data);
        // 设置其他值
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        // 返回设置值之后的对象
        return result;
    }

    // 成功的方法
    public static <T> Result<T> ok(T data) {
        // 创建Result对象，设置值，返回对象
        return build(data, ResultCodeEnum.SUCCESS);
    }

    // 失败的方法
    public static <T> Result<T> fail(T data) {
        // 创建Result对象，设置值，返回对象
        return build(data, ResultCodeEnum.FAIL);
    }


    public static <T> Result<T> build(T data, Integer code, String message) {
        // 创建Result对象，设置值，返回对象
        Result<T> result = new Result<>();
        // 判断返回结果中是否有数据
        result.setData(data);
        // 设置其他值
        result.setCode(code);
        result.setMessage(message);
        // 返回设置值之后的对象
        return result;
    }


}
