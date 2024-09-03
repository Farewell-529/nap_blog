package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.vo.Result;
import com.nap_blog.entity.User;
import com.nap_blog.mapper.UserMapper;
import com.nap_blog.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LoginServiceImpl extends ServiceImpl<UserMapper, User> implements LoginService {
    @Override
    public User login(User user) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword());
        return this.getOne(lqw);
    }

    @Override
    public Result register(User user) {
        log.info("传过来的数据{}",user);
        List<User> list = this.list();
        //判断数据库中是否有相同的账号名
        boolean result = list.stream().anyMatch(item -> item.getUsername().equalsIgnoreCase(user.getUsername()));
        if(result){
          return Result.error("账号名已存在");
        }
        if(user.getUsername()==null|| user.getUsername().length()<3){
            return Result.error("账号名必须大于3位数");
        }
        if (user.getPassword()==null||user.getPassword().length() < 6) {
            return Result.error("密码必须大于8位数");
        }
//        if (!user.getPassword().matches(".*[a-zA-Z]+.*")) {
//            return Result.error("密码必须包含字母");
//        }

        return this.save(user)?Result.success("注册成功"):Result.error();
    }

}
