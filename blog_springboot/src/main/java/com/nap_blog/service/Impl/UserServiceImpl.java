package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.BloggerInfo;
import com.nap_blog.entity.User;
import com.nap_blog.mapper.UserInfoMapper;
import com.nap_blog.mapper.UserMapper;
import com.nap_blog.service.UserService;
import com.nap_blog.vo.response.UserInfoRes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public UserInfoRes getUserInfoBack(Long userId) {
        User user = userMapper.selectById(userId);
        UserInfoRes userInfoRes = new UserInfoRes();
        // 使用 BeanUtils 拷贝相同属性
        BeanUtils.copyProperties(user, userInfoRes);
        return userInfoRes;
    }

    @Override
    public UserInfoRes getUserInfoFront(Long userId) {
        User user = userMapper.selectById(userId);
        UserInfoRes userInfoRes = new UserInfoRes();
        // 使用 BeanUtils 拷贝相同属性
        BeanUtils.copyProperties(user, userInfoRes);
        return userInfoRes;
    }

}
