package com.nap_blog.controller;

import com.nap_blog.entity.Comments;
import com.nap_blog.service.CommentsService;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.CommentsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentsController {
    @Autowired
    CommentsService commentsService;

    @GetMapping("/api/comments/list")
    public Result commentsList(@RequestParam Integer articleId){
        return Result.success(commentsService.getCommentsList(articleId));
    }

    @PostMapping("/api/comments/save")
    public Result saveComments(@RequestBody Comments comments){
        commentsService.saveComments(comments);
        return Result.success();
    }

    @GetMapping("/admin/comments/list")
    public Result commentsListAdmin( CommentsQuery commentsQuery){
        return Result.success(commentsService.getCommentsListAdmin(commentsQuery));
    }

    @PostMapping("/admin/comments/save")
    public Result saveCommentsAdmin(@RequestBody Comments comments){
        return commentsService.saveComments(comments)?Result.success():Result.error("数据不匹配");
    }
    @PutMapping("/admin/comments/update")
    public Result updateComments(@RequestBody Comments comments){
        commentsService.updateComments(comments);
        return Result.success();
    }
    @DeleteMapping("/admin/comments/delete")
    public Result deleteComments(@RequestBody List<Integer> ids){
        commentsService.deleteComments(ids);
        return Result.success();
    }
}
