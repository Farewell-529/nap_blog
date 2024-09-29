package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Comments;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.CommentsQuery;
import com.nap_blog.vo.response.CommentsBackRes;
import com.nap_blog.vo.response.CommentsRes;
import jakarta.mail.MessagingException;

import java.util.List;

public interface CommentsService extends IService<Comments> {

    List<CommentsRes> getCommentsList(String targetType,Integer targetId);

    PageResult<CommentsBackRes> getCommentsListAdmin(CommentsQuery commentsQuery);
    boolean saveComments(Comments comments) throws MessagingException;

    void deleteComments(List<Integer> ids);

    void updateComments(Comments comments);

}
