package com.nap_blog.controller;

import com.nap_blog.entity.BlogInfo;
import com.nap_blog.vo.Result;
import com.nap_blog.service.BlogInfoService;
import com.nap_blog.vo.query.BloggerInfoEditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BlogInfoController {
    @Autowired
    BlogInfoService blogInfoService;
    @GetMapping("/admin/blogger")
    public Result getBloggerAdminInfo() {
        return Result.success(blogInfoService.getBloggerInfo());
    }

    @PutMapping("/admin/blogger/edit")
    public Result editBlogInfo(@RequestBody BloggerInfoEditQuery bloggerInfoEditQuery) {
        blogInfoService.editBlogInfoBatch(bloggerInfoEditQuery);
        return Result.success();
    }
    @GetMapping("/admin")
    public Result getHomePageInfo() {
        return Result.success(blogInfoService.getHomePageInfo());
    }

    @GetMapping("/api/archive")
    public Result getBlogArchive() {
        return Result.success(blogInfoService.getArchiveArticle());
    }

    @GetMapping("/api/blogger")
    public Result getBloggerFontInfo() {
        return Result.success(blogInfoService.getBloggerInfo());
    }
}
