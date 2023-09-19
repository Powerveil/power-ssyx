package com.power.ssyx.contants;

/**
 * @author Powerveil
 * @Date 2023/8/14 23:40
 */
public class SystemConstants {

    /**
     * 审核通过
     */
    public static final Integer CHECK_PASS = 1;
    /**
     * 未审核
     */
    public static final Integer CHECK_NOT_PASS = 0;

    /**
     * 上架
     */
    public static final Integer PUBLISH_PASS = 1;
    /**
     * 下架
     */
    public static final Integer PUBLISH_NOT_PASS = 0;

    /**
     * 是新人
     */
    public static final Integer IS_NEW_PERSON = 1;
    /**
     * 不是新人
     */
    public static final Integer IS_NOT_NEW_PERSON = 0;


    /**
     * shequ-activity数据库中coupon_range表中类型为商品
     */
    public static final Integer RANGE_TYPE_IS_SKU = 1;
    /**
     * shequ-activity数据库中coupon_range表中类型为分类
     */
    public static final Integer RANGE_TYPE_IS_CATEGORY = 2;

    /**
     * shequ-user数据库中user_delivery表中is_default为默认 1为默认
     */
    public static final Integer USER_DELIVERY_IS_NOT_DEFAULT = 0;
    public static final Integer USER_DELIVERY_IS_DEFAULT = 1;


}
