package com.nap_blog.controller;

import com.nap_blog.entity.Article;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.ArticleQuery;
import com.nap_blog.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @GetMapping("/admin/articles/list")
    public Result listArticleBackVO(ArticleQuery articleQuery) {
        return Result.success(articleService.listArticleBackVO(articleQuery));
    }

    @GetMapping("/admin/articles/all/list")
    public Result listArticleAllBackVO() {
        return Result.success(articleService.listArticleAllBackVO());
    }

    @GetMapping("/admin/articles/{id}")
    public Result getArticleByIdVO(@PathVariable("id") Long id) {
        return Result.success(articleService.getArticleByIdVO(id));
    }

    @PostMapping("/admin/articles/save")
    public Result saveArticle(@RequestBody Article article) {
        articleService.saveArticle(article);
        return Result.success();
    }

    @PutMapping("/admin/articles/publish")
    public Result publishArticle(@RequestBody Article article) {
        articleService.publishArticle(article);
        return Result.success();
    }

    @PutMapping("/admin/articles/list/publish")
    public Result publishArticleList(@RequestBody List<Integer> articleIds) {
        articleService.publishArticleList(articleIds);
        return Result.success();
    }

    @PutMapping("/admin/articles/update")
    public Result updateArticle(@RequestBody Article article) {
        articleService.updateArticle(article);
        return  Result.success();
    }

    @DeleteMapping("/admin/articles/delete")
    public Result deleteArticle(@RequestBody List<Integer> articleIds) {
        articleService.deleteArticle(articleIds);
        return Result.success();
    }

    @GetMapping("/admin/articles/draft/list")
    public Result draftListArticleBackVO(ArticleQuery articleQuery) {
        return Result.success(articleService.draftListArticleBackVO(articleQuery));
    }

    @PutMapping("/admin/articles/list/draft")
    public Result articleListToDraft(@RequestBody Integer articleId) {
        articleService.articleToDraft(articleId);
        return Result.success();
    }

    @PutMapping("/admin/articles/draft/{id}")
    public Result draftToPublishById(@PathVariable("id") Long id) {
        articleService.draftToPublishById(id);
        return Result.success();

    }
    @GetMapping("/api/articles/list")
    public Result listArticleFont(ArticleQuery articleQuery) {
        return Result.success(articleService.listArticleVO(articleQuery));
    }
    @GetMapping("/api/articles/{id}")
    public Result getArticleByIdFont(@PathVariable("id") Long id) {
        return Result.success(articleService.getArticleByIdFont(id));
    }

}
