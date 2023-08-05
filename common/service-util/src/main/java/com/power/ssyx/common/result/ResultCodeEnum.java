package com.power.ssyx.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 * @author Powerveil
 * @Date 2023/7/22 17:11
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),

    ORDER_PRICE_ERROR(210, "订单商品价格变化"),
    ORDER_STOCK_FALL(204, "订单库存锁定失败"),
    CREATE_ORDER_FAIL(210, "创建订单失败"),

    COUPON_GET(220, "优惠券已经领取"),
    COUPON_LIMIT_GET(221, "优惠券已发放完毕"),

    URL_ENCODE_ERROR(216, "URL编码失败"),
    ILLEGAL_CALLBACK_REQUEST_ERROR(217, "非法回调请求"),
    FETCH_ACCESSTOKEN_FAILD(218, "获取accessToken失败"),
    FETCH_USERINFO_ERROR(219, "获取用户信息失败"),


    SKU_LIMIT_ERROR(230, "购买个数不能大于限购个数"),
    REGION_OPEN(240, "该区域已开通"),
    REGION_NO_OPEN(240, "该区域未开通"),

    ROLE_NAME_IS_BLANK(270, "角色名称不嫩为空"),
    ROLE_IS_EXIST(271, "角色名称已存在，不能使用该角色名称"),
    USERNAME_IS_BLANK(275, "用户名不嫩为空"),
    ADMIN_IS_EXIST(276, "用户名已存在，不能使用该用户名"),
    ID_IS_NULL(277, "Id不能为空"),


    CATEGORY_NAME_IS_BLANK(285, "商品分类名称不嫩为空"),
    CATEGORY_IS_EXIST(286, "商品分类名称已存在，不能使用该商品分类名称");

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
