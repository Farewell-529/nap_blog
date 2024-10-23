package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.BlogInfo;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.BloggerInfoEditQuery;
import com.nap_blog.vo.response.ArchiveArticleRes;
import com.nap_blog.vo.response.BlogInfoRes;
import com.nap_blog.vo.response.BloggerInfoRes;
import com.nap_blog.vo.response.HomePageInfoRes;

import java.util.List;

public interface BlogInfoService extends IService<BlogInfo> {
    BloggerInfoRes getBloggerInfo();

    void editBlogInfoBatch(BloggerInfoEditQuery bloggerInfoEditQuery);
    void editBlogInfo(BlogInfo blogInfo);
    HomePageInfoRes getHomePageInfo();
    PageResult<ArchiveArticleRes> getArchiveArticle();
}
