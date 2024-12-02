package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Article;
import com.nap_blog.entity.Blocked;
import com.nap_blog.entity.Comments;
import com.nap_blog.entity.Tags;
import com.nap_blog.mapper.ArticleMapper;
import com.nap_blog.mapper.CommentsMapper;
import com.nap_blog.service.BlockedService;
import com.nap_blog.service.CommentsService;
import com.nap_blog.service.EmailService;
import com.nap_blog.utils.AvatarUtil;
import com.nap_blog.utils.EmailUtil;
import com.nap_blog.utils.SensitiveWordUtil;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.CommentsQuery;
import com.nap_blog.vo.response.CommentsBackRes;
import com.nap_blog.vo.response.CommentsRes;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    BlockedService blockedService;
    @Autowired
    EmailUtil emailUtil;

    @Override
    public List<CommentsRes> getCommentsList(String targetType, Integer articleId) {
        Map<Long, String> commentsMap = commentsMapper.selectList(null).stream().collect(
                Collectors.toMap(Comments::getId, Comments::getName)
        );
        List<Comments> allComments = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getTargetType, targetType)
                .eq(Comments::getTargetId, articleId)
                .eq(Comments::getStatus,1));

        List<CommentsRes> rootComments = allComments.stream().filter(item -> item.getPid() == -1)
                .map(comment -> CommentsRes.builder()
                        .id(comment.getId())
                        .pid(comment.getPid())
                        .replyName(comment.getReplyId() != null ? commentsMap.get(comment.getReplyId().longValue()) : null)
                        .avatar(comment.getAvatar())
                        .content(comment.getContent())
                        .name(comment.getName())
                        .userId(comment.getUserId())
                        .isAuthor(comment.getIsAuthor())
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
        if (commentsQuery.getCreateDate() != null && !commentsQuery.getCreateDate().trim().isEmpty()) {
            LocalDate localDate = LocalDate.parse(commentsQuery.getCreateDate());
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime startOfNextDay = localDate.plusDays(1).atStartOfDay();
            lqw.ge(Comments::getCreateDate,startOfDay);
            lqw.lt(Comments::getCreateDate,startOfNextDay);
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
            commentsBackRes.setStatus(comment.getStatus());
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
    public Result saveComments(Comments comments, HttpServletRequest request) throws MessagingException {
        Blocked isExist = blockedService.checkBlocked(request.getRemoteAddr());
        if(isExist.getStatus()==1){
            return Result.error("你违反了评论规则，暂时无法评论");
        }
        //检查评论信息是否有空
        if (isInvalidComment(comments)) {
            return Result.error("有未填项");
        }
        if(comments.getContent().length()>500){
            return Result.error("最多评论500字");
        }
        //初始化添加评论的信息
        initializeComment(comments,request);

        commentsMapper.insert(comments);
        String articleTitle = articleMapper.selectById(comments.getTargetId()).getTitle();
        //给自己发送邮箱
        emailUtil.sendDefaultCommentEmail(comments, articleTitle);
        //被回复者的信息
        Comments replyComment = commentsMapper.selectOne(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getId, comments.getReplyId()));
        //父评论信息
        Comments parentComment = commentsMapper.selectOne(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getId, comments.getPid()));
        if (replyComment != null) {
            emailUtil.sendReplyCommentEmail(replyComment.getEmail(), comments, articleTitle);
        }

        if (parentComment != null) {
            // 确保不重复发送给同一个人
            if (replyComment == null || !replyComment.getEmail().equals(parentComment.getEmail())) {
                emailUtil.sendParentCommentEmail(parentComment.getEmail(), comments, articleTitle);
            }
        }
        return Result.success("评论成功");
    }

    private void initializeComment(Comments comments,HttpServletRequest request) {
        comments.setCreateDate(new Date());
        try {
            comments.setAvatar(AvatarUtil.getGravatarUrl(comments.getEmail()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("头像生成失败", e);
        }
        if(comments.getStatus()==null)comments.setStatus(1);
        comments.setTargetId(Optional.ofNullable(comments.getTargetId()).orElse(-1));
        comments.setPid(Optional.ofNullable(comments.getPid()).orElse(-1));
        comments.setReplyId(Optional.ofNullable(comments.getReplyId()).orElse(-1));
        comments.setIp(request.getRemoteAddr());
        comments.setContent(SensitiveWordUtil.replaceSensitiveWord(comments.getContent()));
    }


}
