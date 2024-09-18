package com.nap_blog.service.Impl;

import com.nap_blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl {
    @Autowired
    ArticleService articleService;

}
