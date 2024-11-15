package com.nap_blog.controller;

import com.nap_blog.service.UserService;
import com.nap_blog.utils.JwtUtils;
import com.nap_blog.vo.Result;
import com.nap_blog.entity.User;
import com.nap_blog.vo.query.ForgetPasswordQuery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
      return userService.login(user);
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        request.removeAttribute("userId");
        return request.getAttribute("userId") == null ? Result.success() : Result.error();
    }

    @GetMapping("/validateEmail")
    public Result validateEmail(ForgetPasswordQuery forgetPasswordQuery) {
       return userService.validateEmail(forgetPasswordQuery);
    }

    @GetMapping("/validateCode")
    public Result validateCode(@RequestParam("code") String code) {
        return userService.verifyCode(code);
    }

    @PutMapping("/resetPassword")
    public Result validateEmail(@RequestBody String newPassword) {
        return userService.resetPassword(newPassword);
    }

    @GetMapping("/checkPassword")
    public Result checkPassword(String password,HttpServletRequest request) {
        return userService.checkPassword(password,request);
    }


}
