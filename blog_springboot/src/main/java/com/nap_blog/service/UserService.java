package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.nap_blog.entity.User;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.EditAccountQuery;
import com.nap_blog.vo.query.ForgetPasswordQuery;
import jakarta.servlet.http.HttpServletRequest;


public interface UserService extends IService<User> {

    Result login (User user);

    Result register(User user);

    Result validateEmail(ForgetPasswordQuery forgetPasswordQuery);
    Result verifyCode(String inputCode);
    Result resetPassword(String resetPassword);

    Result checkPassword(String password, HttpServletRequest request);

    Result editPassword(EditAccountQuery editAccountQuery, HttpServletRequest request);
}
