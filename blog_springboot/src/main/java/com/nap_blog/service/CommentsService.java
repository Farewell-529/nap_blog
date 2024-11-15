package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Comments;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.CommentsQuery;
import com.nap_blog.vo.response.CommentsBackRes;
import com.nap_blog.vo.response.CommentsRes;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CommentsService extends IService<Comments> {

    List<CommentsRes> getCommentsList(String targetType,Integer targetId);

    PageResult<CommentsBackRes> getCommentsListAdmin(CommentsQuery commentsQuery);
    Result saveComments(Comments comments, HttpServletRequest request) throws MessagingException;

    void deleteComments(List<Integer> ids);

    void updateComments(Comments comments);

}
