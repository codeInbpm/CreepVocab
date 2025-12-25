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

    private JSONObject getSession(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        String response = HttpUtil.get(url);
        log.info("WeChat Session Response: {}", response);
        return JSONUtil.parseObj(response);
    }

    @Operation(summary = "WeChat Login")
    @PostMapping("/wx-login")
    public Result<LoginVO> wxLogin(@RequestBody WxLoginDTO dto) {
        String code = dto.getCode();
        if (code == null || code.isEmpty()) {
            return Result.error(400, "Code is required");
        }

        JSONObject json = getSession(code);
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
            user.setHintCards(5); // Default hints for new users
            userService.save(user);
        } else {
            // Update session key for existing user
            user.setWxSessionKey(json.getStr("session_key"));
            userService.updateById(user);
        }

        // 3. Generate Token (7 days)
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
        // Fix: getHintCards might be null for old data
        userInfo.setHintCards(user.getHintCards() == null ? 0 : user.getHintCards());
        
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
            // Using Manual Utils
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
            userInfo.setHintCards(user.getHintCards() == null ? 0 : user.getHintCards());
            
            return Result.success(userInfo);
            
        } catch (Exception e) {
            log.error("Decrypt phone failed", e);
            return Result.error(500, "Failed to decrypt phone number");
        }
    }

    @Operation(summary = "WeChat Login with Phone")
    @PostMapping("/wx-login-phone")
    public Result<LoginVO> wxLoginPhone(@RequestBody WxPhoneLoginDTO dto) {
        try {
            // 1. JS Code -> Session (Manual)
            JSONObject sessionJson = getSession(dto.getCode());
            String openid = sessionJson.getStr("openid");
            String sessionKey = sessionJson.getStr("session_key"); // Note case for Hutool JSON might need verification, usually snake_case from wx
            
            if (openid == null || sessionKey == null) {
                 return Result.error(500, "Failed to get session from WeChat: " + sessionJson.toString());
            }
            
            // 2. Decrypt Phone (Manual Utils)
            String phoneNumberStr = com.creepvocab.utils.WxDecryptUtil.decrypt(dto.getEncryptedData(), sessionKey, dto.getIv());
            JSONObject json = JSONUtil.parseObj(phoneNumberStr);
            String phoneNumber = json.getStr("phoneNumber");
            
            if (phoneNumber == null) {
                return Result.error(400, "Failed to retrieve phone number");
            }
            
            // 3. Find/Create User
            // First try to find by OpenID
            User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
            boolean isNewUser = false;
            
            if (user == null) {
                // If not found by OpenID, try to find by Phone
                user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phoneNumber));
                
                if (user != null) {
                    // Merging: Found by phone, update openid
                    user.setOpenid(openid);
                } else {
                    // New User
                    isNewUser = true;
                    user = new User();
                    user.setOpenid(openid);
                    user.setPhone(phoneNumber);
                    user.setNickname("单词侠" + phoneNumber.substring(phoneNumber.length() - 4));
                    user.setAvatar("/static/default_avatar.png");
                    user.setCoins(0);
                    user.setStreak(0);
                    user.setWordPower(0);
                    user.setLevel(1);
                    user.setWinCount(0);
                    user.setTotalBattles(0);
                    user.setHintCards(5);
                    user.setCreateTime(LocalDateTime.now());
                }
            } else {
                // Found by OpenID, ensure phone is updated
                user.setPhone(phoneNumber);
            }
            
            // Update session key and timestamp
            user.setWxSessionKey(sessionKey);
            user.setUpdateTime(LocalDateTime.now());
            
            if (isNewUser) {
                userService.save(user);
            } else {
                userService.updateById(user);
            }
            
            // 4. Generate Token
            String token = jwtUtil.generateToken(user.getId().toString());
            
            // 5. Response
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
            userInfo.setNeedBindPhone(false); // Phone is definitely bound now
            userInfo.setHintCards(user.getHintCards() == null ? 0 : user.getHintCards());
            
            LoginVO vo = new LoginVO();
            vo.setToken(token);
            vo.setUserInfo(userInfo);
            
            return Result.success(vo);
            
        } catch (Exception e) {
            log.error("WX Phone Login failed", e);
            return Result.error(500, "Login failed: " + e.getMessage());
        }
    }

    @Data
    public static class WxLoginDTO {
        private String code;
    }
    
    @Data
    public static class WxPhoneLoginDTO {
        private String code;
        private String encryptedData;
        private String iv;
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
        private Integer hintCards;
    }
}
