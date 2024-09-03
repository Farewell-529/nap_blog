package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.nap_blog.entity.User;
import com.nap_blog.vo.response.UserInfoRes;


public interface UserService extends IService<User> {
    UserInfoRes getUserInfoFront(Long userId);



}
