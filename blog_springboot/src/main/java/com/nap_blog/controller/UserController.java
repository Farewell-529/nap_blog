package com.nap_blog.controller;

import com.nap_blog.entity.User;
import com.nap_blog.vo.Result;
import com.nap_blog.service.UserService;
import com.nap_blog.vo.query.EditAccountQuery;
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
    @GetMapping("/admin/checkPassword")
    public Result checkPassword(String password,HttpServletRequest request) {
        return userService.checkPassword(password,request);
    }
    @PutMapping("/admin/editPassword")
    public Result editPassword(@RequestBody EditAccountQuery editAccountQuery, HttpServletRequest request) {
        return userService.editPassword(editAccountQuery,request);
    }
    @PutMapping("/admin/user/edit")
    public Result editUser(@RequestBody User user){
        user.setUpdateDate(new Date());
        return userService.updateById(user)?Result.success():Result.error();
    }
}
