package com.nap_blog.controller;

import com.nap_blog.entity.BloggerInfo;
import com.nap_blog.entity.User;
import com.nap_blog.vo.Result;
import com.nap_blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/user")
    public Result getUserInfo(HttpServletRequest request){
        Long id = (Long)request.getAttribute("userId");
        return Result.success(userService.getById(id));
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
    public Result editUser(@RequestBody User user){
        user.setUpdateDate(new Date());
        return userService.updateById(user)?Result.success():Result.error();
    }
}
