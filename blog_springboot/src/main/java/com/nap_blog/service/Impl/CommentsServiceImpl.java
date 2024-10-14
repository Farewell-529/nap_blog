package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Article;
import com.nap_blog.entity.Comments;
import com.nap_blog.mapper.ArticleMapper;
import com.nap_blog.mapper.CommentsMapper;
import com.nap_blog.service.CommentsService;
import com.nap_blog.service.EmailService;
import com.nap_blog.utils.AvatarUtil;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.CommentsQuery;
import com.nap_blog.vo.response.CommentsBackRes;
import com.nap_blog.vo.response.CommentsRes;
import jakarta.mail.MessagingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {
    @Autowired
    CommentsMapper commentsMapper;
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    EmailService emailService;
    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.subject.defaultSubject}")
    private String defaultSubject;

    @Value("${email.subject.replySubject}")
    private String replySubject;

    @Value("${email.content.defaultContent}")
    private String defaultContentTemplate;

    @Value("${email.content.replyContent}")
    private String replyContentTemplate;

    @Value("${email.content.parentContent}")
    private String parentContentTemplate;

    @Override
    public List<CommentsRes> getCommentsList(String targetType, Integer articleId) {
        Map<Long, String> commentsMap = commentsMapper.selectList(null).stream().collect(
                Collectors.toMap(Comments::getId, Comments::getName)
        );
        List<Comments> allComments = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getTargetType, targetType)
                .eq(Comments::getTargetId, articleId));

        List<CommentsRes> rootComments = allComments.stream().filter(item -> item.getPid() == -1)
                .map(comment -> CommentsRes.builder()
                        .id(comment.getId())
                        .pid(comment.getPid())
                        .replyName(comment.getReplyId() != null ? commentsMap.get(comment.getReplyId().longValue()) : null)
                        .avatar(comment.getAvatar())
                        .content(comment.getContent())
                        .name(comment.getName())
                        .userId(comment.getUserId())
                        .createDate(comment.getCreateDate())
                        .build()
                ).toList();

        rootComments.forEach(rootComment -> {
            rootComment.setChildComments(allComments.stream()
                    .filter(comment -> rootComment.getId().equals(comment.getPid().longValue()))
                    .map(comment -> CommentsRes.builder()
                            .id(comment.getId())
                            .pid(comment.getPid())
                            .replyName(comment.getReplyId() != null ? commentsMap.get(comment.getReplyId().longValue()) : null)
                            .avatar(comment.getAvatar())
                            .content(comment.getContent())
                            .name(comment.getName())
                            .userId(comment.getUserId())
                            .createDate(comment.getCreateDate())
                            .build())
                    .toList());
        });
        return rootComments;
    }

    @Override
    public PageResult<CommentsBackRes> getCommentsListAdmin(CommentsQuery commentsQuery) {
        //查询标签数量
        long total = this.count();
        if (total == 0) {
            return new PageResult<>();
        }
        LambdaQueryWrapper<Comments> lqw = new LambdaQueryWrapper<>();
        if (commentsQuery.getKeyword() != null && !commentsQuery.getKeyword().trim().isEmpty()) {
            lqw.like(Comments::getContent, commentsQuery.getKeyword());
        }
        lqw.eq(Comments::getTargetType, commentsQuery.getTargetType());
        int current = (commentsQuery.getCurrent() == null) ? 1 : commentsQuery.getCurrent();
        int size = (commentsQuery.getSize() == null) ? 10 : commentsQuery.getSize();
        // 创建分页对象
        Page<Comments> page = new Page<>(current, size);
        Map<Long, String> articleMap = articleMapper.selectList(null).stream().collect(
                Collectors.toMap(Article::getId, Article::getTitle)
        );
        Map<Long, String> commentsMap = commentsMapper.selectList(null).stream().collect(
                Collectors.toMap(Comments::getId, Comments::getName)
        );
        List<CommentsBackRes> commentsList = commentsMapper.selectList(page, lqw).stream().map(comment -> {
            CommentsBackRes commentsBackRes = new CommentsBackRes();
            BeanUtils.copyProperties(comment, commentsBackRes);
            commentsBackRes.setArticleName(articleMap.get(comment.getTargetId().longValue()));
            commentsBackRes.setReplyName(comment.getReplyId() != null ? commentsMap.get(comment.getReplyId().longValue()) : null);
            return commentsBackRes;
        }).toList();
        return new PageResult<>(commentsList, total);
    }

    @Override
    public void updateComments(Comments comments) {
        List<Comments> childComments = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getPid, comments.getId()));
        childComments.forEach(childComment -> {
            childComment.setTargetType(comments.getTargetType());
            childComment.setTargetId(comments.getTargetId());
            commentsMapper.updateById(childComment);
        });
        commentsMapper.updateById(comments);
    }

    private boolean isInvalidComment(Comments comments) {
        return
                comments.getTargetId() == null ||
                        comments.getTargetType() == null || comments.getTargetType().isEmpty() ||
                        comments.getContent() == null || comments.getContent().isEmpty() ||
                        comments.getEmail() == null || comments.getEmail().isEmpty() ||
                        comments.getUrl() == null || comments.getUrl().isEmpty() ||
                        comments.getName() == null || comments.getName().isEmpty();
    }

    @Override
    public void deleteComments(List<Integer> ids) {
        // 查询所有子评论
        List<Comments> childComments = commentsMapper.selectList(
                new LambdaQueryWrapper<Comments>()
                        .in(Comments::getPid, ids)
        );

        // 批量删除子评论
        if (!childComments.isEmpty()) {
            List<Long> childCommentIds = childComments.stream()
                    .map(Comments::getId)
                    .collect(Collectors.toList());
            commentsMapper.deleteBatchIds(childCommentIds);
        }

        // 查询所有回复了当前删除评论的评论
        List<Comments> replyComments = commentsMapper.selectList(
                new LambdaQueryWrapper<Comments>()
                        .in(Comments::getReplyId, ids)
        );
        // 批量更新，将 reply_id 设为 -1
        if (!replyComments.isEmpty()) {
            replyComments.forEach(replyComment -> {
                replyComment.setReplyId(-1);
                commentsMapper.updateById(replyComment);
            });
        }
        commentsMapper.deleteBatchIds(ids);
    }

    @Override
    public boolean saveComments(Comments comments) throws MessagingException {
        //检查评论信息是否有空
        if (isInvalidComment(comments)) {
            return false;
        }
        //初始化添加评论的信息
        initializeComment(comments);
        commentsMapper.insert(comments);
        String articleTitle = articleMapper.selectById(comments.getTargetId()).getTitle();
        //给自己发送邮箱
        sendDefaultEmail(comments, articleTitle);
        //被回复者的信息
        Comments replyComment = commentsMapper.selectOne(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getId, comments.getReplyId()));
        //父评论信息
        Comments parentComment = commentsMapper.selectOne(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getId, comments.getPid()));
        if (replyComment != null) {
            sendReplyEmail(replyComment.getEmail(), comments, articleTitle);
        }

        if (parentComment != null) {
            // 确保不重复发送给同一个人
            if (replyComment == null || !replyComment.getEmail().equals(parentComment.getEmail())) {
                sendParentCommentEmail(parentComment.getEmail(), comments, articleTitle);
            }
        }
        return true;
    }

    private void initializeComment(Comments comments) {
        comments.setCreateDate(new Date());
        try {
            comments.setAvatar(AvatarUtil.getGravatarUrl(comments.getEmail()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("头像生成失败", e);
        }
        comments.setTargetId(Optional.ofNullable(comments.getTargetId()).orElse(-1));
        comments.setPid(Optional.ofNullable(comments.getPid()).orElse(-1));
        comments.setReplyId(Optional.ofNullable(comments.getReplyId()).orElse(-1));
    }

    private void sendDefaultEmail(Comments comments, String articleTitle) throws MessagingException {
        String defaultContent = String.format(defaultContentTemplate, articleTitle,
                comments.getName(), comments.getContent(), comments.getEmail(), comments.getUrl());
        emailService.sendEmail(fromEmail,
                String.format(defaultSubject, articleTitle),
                defaultContent, fromEmail);

    }

    private void sendReplyEmail(String replyCommentEmail, Comments comments, String articleTitle) throws MessagingException {

        String replyContent = String.format(replyContentTemplate, articleTitle,
                comments.getName(), comments.getContent());

        emailService.sendEmail(replyCommentEmail,
                String.format(replySubject, articleTitle),
                replyContent,
                fromEmail);
    }

    private void sendParentCommentEmail(String parentCommentEmail, Comments comments, String articleTitle) throws MessagingException {


        String parentContent = String.format(parentContentTemplate, articleTitle,
                comments.getName(), comments.getContent());

        emailService.sendEmail(parentCommentEmail,
                String.format(replySubject, articleTitle),
                parentContent,
                fromEmail);
    }
}
