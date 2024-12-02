package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.User;
import com.nap_blog.mapper.UserMapper;
import com.nap_blog.service.UserService;
import com.nap_blog.utils.EmailUtil;
import com.nap_blog.utils.JwtUtils;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.EditAccountQuery;
import com.nap_blog.vo.query.ForgetPasswordQuery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    EmailUtil emailUtil;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Override
    public Result login(User user) {
        User selectOne = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        boolean matches = passwordEncoder.matches(user.getPassword(), selectOne.getPassword());
        if(!matches){
            return Result.error("用户名密码错误");
        }
        String token = JwtUtils.createToken(selectOne);//jwt包含了用户当前登录的信息
        log.info("令牌是{}", token);
        return Result.success((Object) token);
    }


    @Override
    public Result sendEmailValidateCodeForForgetPassword(ForgetPasswordQuery forgetPasswordQuery) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, forgetPasswordQuery.getUsername()));
        if (user == null) {
            return Result.error("没有这个账号");
        }
        redisTemplate.opsForValue().set("forgetUsername", forgetPasswordQuery.getUsername());
        if (!Objects.equals(user.getEmail(), forgetPasswordQuery.getEmail())) {
            return Result.error("查询不到该邮箱");
        }
    return sendValidateCode(forgetPasswordQuery.getEmail());
    }

    @Override
    public Result validateOldEmail(String email) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (user == null) {
            return Result.error("没有这个邮箱");
        }
           return sendValidateCode(email);
    }

    @Override
    public Result validateNewEmail(String email) {
        if(email.isEmpty()){
            return  Result.error("邮箱不能为空");
        }
        // 正则表达式
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        // 使用 Pattern.matches() 检查 email 是否匹配正则
        if (!Pattern.matches(emailRegex, email)) {
            return  Result.error("这好像不是个邮箱");
        }
        return sendValidateCode(email);
    }

    @Override
    public Result editEmail(String email,HttpServletRequest request) {
        User user = userMapper.selectById((long) request.getAttribute("userId"));
        user.setUpdateDate(new Date());
        user.setEmail(email);
        userMapper.updateById(user);
        return Result.success("修改成功");
    }

    @Override
    public Result verifyCode(String inputCode,String toEmail) {
        // 从 Redis 中获取存储的验证码
        String storedCode =  redisTemplate.opsForValue().get("VerificationCode_"+toEmail);
        // 从 Redis 中获取验证码的存储时间戳
        String storedTime = redisTemplate.opsForValue().get("LastSendTime_"+toEmail);

        // 如果验证码或时间戳为 null，则返回错误信息
        if (storedCode == null || storedTime == null) {
            System.out.println(toEmail);
            return Result.error("验证码已过期，请重新获取");
        }
        // 校验验证码是否正确
        if (!storedCode.equals(inputCode)) {
            return Result.error("验证码不正确");
        }

        // 验证成功，清除 Redis 中的验证码和时间戳
        redisTemplate.delete("VerificationCode_"+toEmail);
        redisTemplate.delete("LastSendTime_"+toEmail);
        return Result.success("验证成功");
    }

    @Override
    public Result resetPassword(String resetPassword) {
        String forgetUsername = redisTemplate.opsForValue().get("forgetUsername");
        if (forgetUsername == null) {
            return Result.error("用户名信息已过期");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, forgetUsername));

        if (user == null) {
            return Result.error("用户不存在");
        }
        String encode = passwordEncoder.encode(resetPassword);
        user.setPassword(encode);
        user.setUpdateDate(new Date());
        userMapper.updateById(user);
        redisTemplate.delete("forgetUsername");
        return Result.success("密码重置成功");
    }

    @Override
    public Result checkPassword(String password, HttpServletRequest request) {
        User user = userMapper.selectById((long) request.getAttribute("userId"));
        boolean matches = passwordEncoder.matches(password,user.getPassword());
        if (!matches) {
            return Result.error("密码不正确");
        }
        return Result.success("密码正确");
    }

    @Override
    public Result editPassword(EditAccountQuery editAccountQuery, HttpServletRequest request) {
        User user = userMapper.selectById((long) request.getAttribute("userId"));
        if (editAccountQuery.getPassword() != null&&!editAccountQuery.getPassword().isEmpty()) {
            if (editAccountQuery.getPassword().length() < 6) {
                return Result.error("密码不少于六位数");
            }
            String encode = passwordEncoder.encode(editAccountQuery.getPassword());
            user.setPassword(encode);
        }
        user.setUpdateDate(new Date());
        user.setUsername(editAccountQuery.getUsername());
        userMapper.updateById(user);
        return Result.success("修改成功");
    }

    @Override
    public Result sendValidateCode(String toEmail) {
        String verifyTime = redisTemplate.opsForValue().get("LastSendTime_"+toEmail);
        long currentTIme = System.currentTimeMillis();
        if (verifyTime != null) {
            long lastTime = Long.parseLong(verifyTime);
            long time = currentTIme - lastTime;
            if (time < 60000) {
                long remainingSeconds = 60 - (time / 1000);
                return Result.success("验证码已发送，请在" + remainingSeconds + "秒后重试");
            }
        }
        Random random = new Random();
        // 生成六位数的验证码，范围是 100000 到 999999
        String code = String.valueOf(100000 + random.nextInt(900000));
        redisTemplate.opsForValue().set("LastSendTime_"+toEmail, String.valueOf(currentTIme));
        redisTemplate.opsForValue().set("VerificationCode_"+toEmail, code);
        // 设置验证码的有效期为1分钟（60秒）
        redisTemplate.expire("VerificationCode_"+toEmail, 1, TimeUnit.MINUTES);
        redisTemplate.expire("LastSendTime_"+toEmail, 1, TimeUnit.MINUTES);
        //发送验证码
        emailUtil.sendVerificationCodeEmail(code, toEmail);
        return Result.success("验证码已发送,注意查看");
    }

    @Override
    public Result register(User user) {
        log.info("传过来的数据{}", user);
        List<User> list = userMapper.selectList(null);
        //判断数据库中是否有相同的账号名
        boolean result = list.stream().anyMatch(item -> item.getUsername().equalsIgnoreCase(user.getUsername()));
        if (result) {
            return Result.error("账号名已存在");
        }
        if (user.getUsername() == null || user.getUsername().length() < 3) {
            return Result.error("账号名必须大于3位数");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return Result.error("密码必须大于6位数");
        }
        user.setCreateDate(new Date());
        return userMapper.insert(user) == 1 ? Result.success("注册成功") : Result.error();
    }
}
