package com.nap_blog.controller;

import com.nap_blog.utils.JwtUtils;
import com.nap_blog.vo.Result;
import com.nap_blog.entity.User;
import com.nap_blog.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User e = loginService.login(user);
        //登录成功，返回下发令牌
        if (e != null) {
            String token = JwtUtils.createToken(e);//jwt包含了用户当前登录的信息
            log.info("令牌是{}", token);
            return Result.success(token);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        request.removeAttribute("userId");
        return request.getAttribute("userId") == null ? Result.success() : Result.error();
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return loginService.register(user);
    }
}
