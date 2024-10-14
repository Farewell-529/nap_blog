package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.nap_blog.entity.UserAuth;
import com.nap_blog.vo.response.UserInfoRes;


public interface UserService extends IService<UserAuth> {
    UserInfoRes getUserInfoFront(Long userId);



}
