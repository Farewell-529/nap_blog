package com.nap_blog.controller;

import com.nap_blog.vo.Result;
import com.nap_blog.entity.UserAuth;
import com.nap_blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/user")
    public Result getUserInfo(HttpServletRequest request){
        Long id = (Long)request.getAttribute("userId");
        return userService.getById(id)!=null?Result.success(userService.getById(id)):Result.error("登录过期");
    }
//    @GetMapping("/api/user/about")
//    public Result getUserBio(){
//        User user = userService.getById(1);
//        return Result.success(user.getBio());
//    }
    @GetMapping("/api/user")
    public Result getUserInfoFront(){
        return Result.success(userService.getUserInfoFront(1L));
    }
    @PutMapping("/user")
    public Result editUser(@RequestBody UserAuth userAuth){
        userAuth.setUpdateAt(System.currentTimeMillis());
        userService.updateById(userAuth);
        return userService.updateById(userAuth)?Result.success():Result.error();
    }
}
