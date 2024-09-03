package com.nap_blog.controller;

import com.nap_blog.vo.Result;
import com.nap_blog.service.BlogInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogInfoController {
    @Autowired
    BlogInfoService blogInfoService;

    @GetMapping("/admin")
    public Result getBlogBackInfo() {
        return Result.success(blogInfoService.getBlogBackInfo());
    }
}
