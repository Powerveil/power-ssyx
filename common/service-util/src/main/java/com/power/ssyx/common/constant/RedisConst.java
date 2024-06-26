package com.power.ssyx.common.constant;

/**
 * @author Powerveil
 * @Date 2023/9/14 18:13
 * Redis常量配置类
 * set name admin
 */
public class RedisConst {

    public static final String SKUKEY_PREFIX = "sku:";
    public static final String SKUKEY_SUFFIX = ":info";
    //单位：秒
    public static final long SKUKEY_TIMEOUT = 24 * 60 * 60L;
    // 定义变量，记录空对象的缓存过期时间 缓存穿透key的过期时间
    public static final long SKUKEY_TEMPORARY_TIMEOUT = 10 * 60L;
    public static final long IMAGE_UPLOAD_BLACKLIST_TIMEOUT = 2 * 60L;

    //单位：秒 尝试获取锁的最大等待时间
    public static final long SKULOCK_EXPIRE_PX1 = 1;
    //单位：秒 锁的持有时间
    public static final long SKULOCK_EXPIRE_PX2 = 1;
    public static final String SKULOCK_SUFFIX = ":lock";

    public static final String USER_KEY_PREFIX = "user:";
    public static final String USER_CART_KEY_SUFFIX = ":cart";
    public static final long USER_CART_EXPIRE = 60 * 60 * 24L; // 24小时
    public static final String STOCK_INFO = "stock:info:";
    public static final String ORDER_REPEAT = "order:repeat:";

    //用户登录
    public static final String USER_LOGIN_KEY_PREFIX = "user:login:";
    public static final String ADMIN_LOGIN_KEY_PREFIX = "admin:login:";
    //    public static final String userinfoKey_suffix = ":info";
    public static final int USERKEY_TIMEOUT = 2;
    public static final String ORDER_SKU_MAP = "order:sku:";
    public static final String ORDER_TEMP_SKU_MAP = "order:temp:sku:";
    // 订单支付超时时间
    public static final long ORDER_TEMP_SKU_EXPIRE = 60 * 60 * 24L; // 24小时

    //秒杀商品前缀
    public static final String SECKILL_TIME_MAP = "seckill:time:map";
    public static final String SECKILL_SKU_MAP = "seckill:sku:map";
    public static final String SECKILL_SKU_LIST = "seckill:sku:list:";
    public static final String SECKILL_USER_MAP = "seckill:user:map:";
    public static final String SECKILL_ORDERS_USERS = "seckill:orders:users";
    public static final String SECKILL_STOCK_PREFIX = "seckill:stock:";
    public static final String SECKILL_USER = "seckill:user:";
    //用户锁定时间 单位：秒
    public static final int SECKILL__TIMEOUT = 60 * 60 * 1;


    // 更新商品热度key
    public static final String HOT_SCORE_KEY = "hotScore";

    // 更新商品热度value前缀
    public static final String SKU_ID_KEY_PREFIX = "skuId:";

    public static final String FILE_UPLOAD_BLACK_LIST_KEY_PREFIX = "upload:blacklist:";
}
