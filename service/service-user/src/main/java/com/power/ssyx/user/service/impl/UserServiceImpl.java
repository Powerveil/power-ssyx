package com.power.ssyx.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.common.constant.RedisConst;
import com.power.ssyx.common.exception.SsyxException;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.BeanCopyUtils;
import com.power.ssyx.common.utils.JwtHelper;
import com.power.ssyx.contants.SystemConstants;
import com.power.ssyx.enums.UserType;
import com.power.ssyx.model.user.Leader;
import com.power.ssyx.model.user.User;
import com.power.ssyx.model.user.UserDelivery;
import com.power.ssyx.user.mapper.LeaderMapper;
import com.power.ssyx.user.mapper.UserDeliveryMapper;
import com.power.ssyx.user.mapper.UserMapper;
import com.power.ssyx.user.service.UserService;
import com.power.ssyx.user.utils.ConstantPropertiesUtil;
import com.power.ssyx.user.utils.HttpClientUtils;
import com.power.ssyx.vo.user.LeaderAddressVo;
import com.power.ssyx.vo.user.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author power
 * @description 针对表【user(会员表)】的数据库操作Service实现
 * @createDate 2023-09-18 21:38:03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserDeliveryMapper userDeliveryMapper;
    @Autowired
    private LeaderMapper leaderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Result loginWx(String code) {
        // 1.得到微信返回code临时票据值
        // 2.拿着code + 小程序id + 小程序密钥 请求微信接口服务
        //// 使用HttpClient工具请求
        // 小程序id
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        // 小程序密钥
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;
        // get请求
        // 拼接请求地址+参数
        // TODO 以后更换http调用方式
        StringBuffer url = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");
        String tokenUrl = String.format(url.toString(),
                wxOpenAppId,
                wxOpenAppSecret,
                code);
        // HttpClient发送get请求
        String result = null;
        try {
            result = HttpClientUtils.get(tokenUrl);
        } catch (Exception e) {
            throw new SsyxException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        // 3.请求微信接口服务，返回两个值 session_key 和 openId
        //// openId是你的微信唯一标识
        // TODO 以后使用JsonPath
        JSONObject jsonObject = JSONObject.parseObject(result);
        String sessionKey = jsonObject.getString("session_key");
        String openId = jsonObject.getString("openid");
        // 4.添加微信用户信息到数据库里面
        //// 操作user表
        //// 判断是否是第一次使用微信授权登录：如何判断？openId
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenId, openId);
        User user = getOne(queryWrapper);
        if (Objects.isNull(user)) {
            // TODO 新用户没有必要查询后面的操作，需要改进
            user = new User();
            user.setOpenId(openId);
            user.setNickName(openId);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            //TODO 事务 this
            save(user);
        }
        // 5.根据userId查询提货点和团长信息
        //// 提货点 user表 user_delivery表
        //// 团长   leader表
        LeaderAddressVo leaderAddressVo = getLeaderAddressVoByUserId(user.getId());
        // 6.使用Jwt工具根据userId和userName生成token字符串
        String token = JwtHelper.createToken(user.getId(), user.getNickName());
        // 7.获取当前登录用户信息，放到Redis里面，设置有效时间
        UserLoginVo userLoginVo = this.getUserLoginVo(user.getId());
        // 8.需要数据封装到map返回
        String key = RedisConst.USER_LOGIN_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue()
                .set(key, userLoginVo, RedisConst.USERKEY_TIMEOUT, TimeUnit.HOURS);// TODO 时间太长，以后肯定要改
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("token", token);
        map.put("leaderAddressVo", leaderAddressVo);
        return Result.ok(map);
    }

    private UserLoginVo getUserLoginVo(Long userId) {
        User user = getById(userId);
        // TODO 考虑是否使用BeanCopyUtils
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setUserId(userId);
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setPhotoUrl(user.getPhotoUrl());
        userLoginVo.setIsNew(user.getIsNew());
        userLoginVo.setOpenId(user.getOpenId());

        UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>()
                        .eq(UserDelivery::getUserId, userId)
                        .eq(UserDelivery::getIsDefault, SystemConstants.USER_DELIVERY_IS_DEFAULT)
        );
        if (!Objects.isNull(userDelivery)) {
            userLoginVo.setLeaderId(userDelivery.getLeaderId());
            userLoginVo.setWareId(userDelivery.getWareId());
        } else {
            // TODO 考虑是否合理
            userLoginVo.setLeaderId(1L);
            userLoginVo.setWareId(1L);
        }

        return userLoginVo;
    }

    @Override
    public LeaderAddressVo getLeaderAddressVoByUserId(Long userId) {
        // 根据userId查询用户默认的团长id
        UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>()
                        .eq(UserDelivery::getUserId, userId)
                        .eq(UserDelivery::getIsDefault, SystemConstants.USER_DELIVERY_IS_DEFAULT)
        );
        if (Objects.isNull(userDelivery)) {
            return null;
        }
        // 拿着上面查询团长id查询leader表查询其他信息
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());
        // 封装数据到LeaderAddressVo
        LeaderAddressVo leaderAddressVo = BeanCopyUtils.copyBean(leader, LeaderAddressVo.class);
        leaderAddressVo.setUserId(userId);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());

        return leaderAddressVo;
    }
}




