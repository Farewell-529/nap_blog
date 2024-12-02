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

    Result sendEmailValidateCodeForForgetPassword(ForgetPasswordQuery forgetPasswordQuery);

    Result validateOldEmail(String email);

    Result validateNewEmail(String email);

    Result editEmail(String email,HttpServletRequest request);
    Result verifyCode(String inputCode,String toEmail);
    Result resetPassword(String resetPassword);

    Result checkPassword(String password, HttpServletRequest request);

    Result editPassword(EditAccountQuery editAccountQuery, HttpServletRequest request);

    Result sendValidateCode(String toEmail);
}
