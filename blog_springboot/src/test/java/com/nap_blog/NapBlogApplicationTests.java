package com.nap_blog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import com.nap_blog.entity.ArticleTags;
import com.nap_blog.service.*;
import com.nap_blog.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class MybatisTests {
    @Autowired
    private TagsService tagsService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    LoginService loginService;
    @Autowired
    ImgService imgService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;

    @Test
    void test1() {
        userService.getUserInfoBack(1L);
    }


    @Test
    void test2() {
        LambdaQueryWrapper<ArticleTags> lqw=new LambdaQueryWrapper<>();
        lqw.groupBy(ArticleTags::getTagsId);
    }

    @Test
    void test3() {
        String ip = "0:0:0:0:0:0:0:1";
        String ipRegion = IpUtil.getIpRegion(ip);
        log.info("ip:{}",IpUtil.getIpRegion("222.85.9.53"));
    }

}

