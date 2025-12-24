package com.creepvocab.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creepvocab.common.result.Result;
import com.creepvocab.entity.User;
import com.creepvocab.service.UserService;
import com.creepvocab.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@Tag(name = "Auth", description = "Authentication API")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Value("${wx.appid}")
    private String appid;
    
    @Value("${wx.secret}")
    private String secret;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Operation(summary = "WeChat Login")
    @PostMapping("/wx-login")
    public Result<LoginVO> wxLogin(@RequestBody WxLoginDTO dto) {
        String code = dto.getCode();
        if (code == null || code.isEmpty()) {
            return Result.error(400, "Code is required");
        }

        // 1. Call WeChat API
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        
        String response = HttpUtil.get(url);
        log.info("WeChat Login Response: {}", response);
        
        JSONObject json = JSONUtil.parseObj(response);
        String openid = json.getStr("openid");
        
        if (openid == null) {
            return Result.error(500, "Failed to get openid: " + json.getStr("errmsg"));
        }

        // 2. Find or Create User
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        boolean isNewUser = false;
        
        if (user == null) {
            isNewUser = true;
            user = new User();
            user.setOpenid(openid);
            user.setWxSessionKey(json.getStr("session_key"));
            user.setNickname("单词侠" + RandomUtil.randomNumbers(4));
            user.setAvatar("/static/default_avatar.png");
            user.setCoins(0);
            user.setStreak(0);
            user.setWordPower(0);
            user.setLevel(1);
            user.setWinCount(0);
            user.setTotalBattles(0);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userService.save(user);
        } else {
            // Update session key for existing user
            user.setWxSessionKey(json.getStr("session_key"));
            userService.updateById(user);
        }

        // 3. Generate Token (7 days)
        // Adjust JwtUtil if needed, assuming generateToken takes String subject
        String token = jwtUtil.generateToken(user.getId().toString());
        
        // 4. Build Response
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setIsNewUser(isNewUser);
        userInfo.setPhone(user.getPhone());
        userInfo.setCoins(user.getCoins());
        userInfo.setStreak(user.getStreak());
        userInfo.setLevel(user.getLevel());
        userInfo.setWordPower(user.getWordPower());
        userInfo.setTotalBattles(user.getTotalBattles());
        userInfo.setWinCount(user.getWinCount());
        userInfo.setNeedBindPhone(user.getPhone() == null || user.getPhone().isEmpty());
        
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserInfo(userInfo);
        
        return Result.success(vo);
    }

    @Operation(summary = "Bind Phone Number")
    @PostMapping("/bind-phone")
    public Result<UserInfo> bindPhone(@RequestBody BindPhoneDTO dto) {
        Long userId = com.creepvocab.utils.SecurityUtils.getUserId();
        User user = userService.getById(userId);
        
        if (user == null) {
            return Result.error(404, "User not found");
        }
        
        try {
            String phoneNumberStr = com.creepvocab.utils.WxDecryptUtil.decrypt(dto.getEncryptedData(), user.getWxSessionKey(), dto.getIv());
            JSONObject json = JSONUtil.parseObj(phoneNumberStr);
            String phoneNumber = json.getStr("phoneNumber");
            
            // Check if phone already bound
            User existing = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phoneNumber));
            if (existing != null && !existing.getId().equals(userId)) {
                return Result.error(400, "Phone number already bound to another account");
            }
            
            user.setPhone(phoneNumber);
            // Update nickname: 单词侠 + last 4 digits
            if (phoneNumber.length() >= 4) {
                user.setNickname("单词侠" + phoneNumber.substring(phoneNumber.length() - 4));
            }
            userService.updateById(user);
            
            // Return updated info
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setPhone(user.getPhone());
            userInfo.setCoins(user.getCoins());
            userInfo.setStreak(user.getStreak());
            userInfo.setLevel(user.getLevel());
            userInfo.setWordPower(user.getWordPower());
            userInfo.setTotalBattles(user.getTotalBattles());
            userInfo.setWinCount(user.getWinCount());
            userInfo.setNeedBindPhone(false);
            
            return Result.success(userInfo);
            
        } catch (Exception e) {
            log.error("Decrypt phone failed", e);
            return Result.error(500, "Failed to decrypt phone number");
        }
    }

    @Data
    public static class WxLoginDTO {
        private String code;
    }
    
    @Data
    public static class BindPhoneDTO {
        private String encryptedData;
        private String iv;
    }

    @Data
    public static class LoginVO {
        private String token;
        private UserInfo userInfo;
    }
    
    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
        private Boolean isNewUser;
        private String phone;
        private Integer coins;
        private Integer streak;
        private Integer level;
        private Integer wordPower;
        private Integer totalBattles;
        private Integer winCount;
        private Boolean needBindPhone;
    }
}
