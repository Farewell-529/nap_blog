package com.nap_blog.controller;

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
    @GetMapping("/admin/user")
    public Result getUserAccountById(HttpServletRequest request){
        Long id = (Long)request.getAttribute("userId");
        return Result.success(userService.getById(id));
    }
    @PutMapping("/admin/user/edit")
    public Result editUser(@RequestBody User user){
        user.setUpdateDate(new Date());
        return userService.updateById(user)?Result.success():Result.error();
    }
}
