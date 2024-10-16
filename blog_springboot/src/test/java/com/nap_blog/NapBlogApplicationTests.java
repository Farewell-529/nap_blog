package com.nap_blog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nap_blog.entity.ArticleTags;
import com.nap_blog.service.*;
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

    }

}

