package com.nap_blog.controller;

import com.nap_blog.entity.Comments;
import com.nap_blog.service.CommentsService;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.CommentsQuery;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentsController {
    @Autowired
    CommentsService commentsService;

    @GetMapping("/api/comments/list")
    public Result commentsList(@RequestParam String targetType,Integer targetId){
        return Result.success(commentsService.getCommentsList(targetType,targetId));
    }

    @PostMapping("/api/comments/save")
    public Result saveComments(@RequestBody Comments comments,HttpServletRequest request) throws MessagingException {
        return commentsService.saveComments(comments,request);
    }

    @GetMapping("/admin/comments/list")
    public Result commentsListAdmin( CommentsQuery commentsQuery){
        return Result.success(commentsService.getCommentsListAdmin(commentsQuery));
    }

    @PostMapping("/admin/comments/save")
    public Result saveCommentsAdmin(@RequestBody Comments comments, HttpServletRequest request) throws MessagingException {
        return commentsService.saveComments(comments,request);
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
