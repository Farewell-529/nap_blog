package com.nap_blog.service;

import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.ArchiveArticleRes;
import com.nap_blog.vo.response.BlogBackInfoRes;

import java.util.List;

public interface BlogInfoService {
    BlogBackInfoRes getBlogBackInfo();

    PageResult<ArchiveArticleRes> getArchiveArticle();
}
