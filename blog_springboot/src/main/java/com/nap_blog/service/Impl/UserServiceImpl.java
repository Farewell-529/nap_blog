package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.User;
import com.nap_blog.mapper.UserMapper;
import com.nap_blog.service.UserService;
import com.nap_blog.vo.response.UserInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public UserInfoRes getUserInfoFront(Long userId) {
        User user = userMapper.selectById(userId);
        return UserInfoRes.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .motto(user.getMotto())
                .build();
    }

}
