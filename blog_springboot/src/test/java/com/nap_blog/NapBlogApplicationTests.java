package com.nap_blog;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nap_blog.entity.ArticleTags;
import com.nap_blog.entity.Blocked;
import com.nap_blog.service.*;
import com.nap_blog.utils.IpUtil;
import com.nap_blog.utils.JwtUtils;
import com.nap_blog.utils.SensitiveWordUtil;
import com.nap_blog.vo.query.VisitsQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Slf4j
@SpringBootTest
class MybatisTests {
    @Autowired
    private TagsService tagsService;
    @Autowired
    private ArticleService articleService;

    @Autowired
    ImgService imgService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;
    @Autowired
    VisitsService visitsService;
    @Autowired
    BlockedService blockedService;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Test
    void test1() {
        String rowPassword="156746861117";
        String encode = passwordEncoder.encode("15674686117");
        System.out.println(encode);
    }


    @Test
    void test2() throws Exception {
        String result = SensitiveWordUtil.replaceSensitiveWord("五星红旗迎风飘扬，毛主席的画像屹立在天安门前");
        log.info(result);
    }

    @Test
    void test3() {
        String ip = "0:0:0:0:0:0:0:1";
        String ipRegion = IpUtil.getIpRegion(ip);
        log.info("ip:{}", IpUtil.getIpRegion("222.85.9.53"));
    }

}

