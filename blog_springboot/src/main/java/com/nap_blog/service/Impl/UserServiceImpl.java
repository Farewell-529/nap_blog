package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.UserAuth;
import com.nap_blog.mapper.UserMapper;
import com.nap_blog.service.UserService;
import com.nap_blog.vo.response.UserInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserAuth> implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public UserInfoRes getUserInfoFront(Long userId) {
        UserAuth userAuth = userMapper.selectById(userId);
        return UserInfoRes.builder()
                .id(userAuth.getId())
                .username(userAuth.getUsername())
                .avatar(userAuth.getAvatar())
                .bio(userAuth.getBio())
                .motto(userAuth.getMotto())
                .build();
    }

}
