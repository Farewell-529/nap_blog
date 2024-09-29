package com.nap_blog.controller;

import com.nap_blog.vo.query.TagsQuery;
import com.nap_blog.vo.Result;
import com.nap_blog.entity.Tags;
import com.nap_blog.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TagsController {
    @Autowired
    TagsService tagsService;

    @GetMapping("/admin/tags/list")
    public Result tagsList(TagsQuery tagsQuery) {
        return Result.success(tagsService.listTags(tagsQuery));
    }

    @GetMapping("/tags/list")
    public Result tagsAllList() {
        return Result.success(tagsService.list());
    }

    @GetMapping("/admin/tags/{id}")
    public Result getTagsById(@PathVariable("id") Long id) {
        return Result.success(tagsService.getById(id));
    }

    @PostMapping("/admin/tags")
    public Result saveTags(@RequestBody Tags tags) {
        tagsService.saveTags(tags);
        return Result.success();
    }

    @PutMapping("/admin/tags")
    public Result updateTags(@RequestBody Tags tags) {
        tagsService.updateTags(tags);
        return Result.success();
    }

    @DeleteMapping("/admin/tags/delete")
    public Result deleteTags(@RequestBody List<Integer> ids) {
        tagsService.deleteTags(ids);
        return Result.success();
    }

    @GetMapping("/tags/admin/list/{articleId}")
    public Result tagsListByArticleId(@PathVariable("articleId") Long articleId) {
        return Result.success(tagsService.getTagListByArticleId(articleId));
    }

    @GetMapping("/api/tags/countsList")
    public Result getTagsCountsList() {
        return Result.success(tagsService.getTagsCountsList());
    }
}
