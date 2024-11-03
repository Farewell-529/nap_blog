package com.nap_blog;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nap_blog.entity.ArticleTags;
import com.nap_blog.service.*;
import com.nap_blog.utils.IpUtil;
import com.nap_blog.utils.JwtUtils;
import com.nap_blog.vo.query.VisitsQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

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
    @Autowired
    VisitsService visitsService;

    @Test
    void test1() {
      log.info(visitsService.listVisitsAdmin(new VisitsQuery()).toString());
    }


    @Test
    void test2() throws Exception {
        Map<String, Claim> map = JwtUtils.verifyToken("232");
        log.info("{}", map);
    }

    @Test
    void test3() {
        String ip = "0:0:0:0:0:0:0:1";
        String ipRegion = IpUtil.getIpRegion(ip);
        log.info("ip:{}", IpUtil.getIpRegion("222.85.9.53"));
    }

}

