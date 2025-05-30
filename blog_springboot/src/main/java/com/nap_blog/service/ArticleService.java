package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Article;
import com.nap_blog.vo.query.ArticleQuery;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.*;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.util.List;

public interface ArticleService extends IService<Article> {
    void saveArticle(Article article);

    void publishArticle(Article article);

    void publishArticleList(List<Integer> articleIds);

    ArticleBackInfoRes getArticleByIdVO(Long id);

    PageResult<ArticleBackRes> listArticleBackVO(ArticleQuery articleQuery);

    List<ArticleSelectRes> listArticleAllBackVO( );


    PageResult<ArticleRes> listArticleVO(ArticleQuery articleQuery,HttpServletRequest httpServletRequest) throws ParseException;

    void updateArticle(Article article);

    void deleteArticle(List<Integer> articleIds);

    PageResult<ArticleBackRes> draftListArticleBackVO(ArticleQuery articleQuery);

    void draftToPublishById(Long id);

    void articleToDraft( Integer articleId);
    ArticleInfoRes getArticleByIdFont(Long id, HttpServletRequest request);

}