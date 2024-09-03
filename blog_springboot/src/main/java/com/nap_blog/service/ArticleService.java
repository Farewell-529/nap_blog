package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Article;
import com.nap_blog.vo.query.ArticleQuery;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.ArticleBackRes;
import com.nap_blog.vo.response.ArticleBackInfoRes;
import com.nap_blog.vo.response.ArticleInfoRes;
import com.nap_blog.vo.response.ArticleRes;

import java.util.List;

public interface ArticleService extends IService<Article> {
    void saveArticle(Article article);

    void publishArticle(Article Article);

    void publishArticleList(List<Integer> articleIds);

    ArticleBackInfoRes getArticleByIdVO(Long id);

    PageResult<ArticleBackRes> listArticleBackVO(ArticleQuery articleQuery);

    PageResult<ArticleRes> listArticleVO(ArticleQuery articleQuery);

    void updateArticle(Article article);

    void deleteArticle(List<Integer> articleIds);

    PageResult<ArticleBackRes> draftListArticleBackVO(ArticleQuery articleQuery);

    void draftToPublishById(Long id);

    void articleListToDraft( List<Integer> articleIds);
    ArticleInfoRes getArticleByIdFont(Long id);

}