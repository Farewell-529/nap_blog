package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Article;
import com.nap_blog.entity.Comments;
import com.nap_blog.mapper.ArticleMapper;
import com.nap_blog.mapper.CommentsMapper;
import com.nap_blog.service.CommentsService;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.CommentsQuery;
import com.nap_blog.vo.response.CommentsBackRes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {
    @Autowired
    CommentsMapper commentsMapper;
    @Autowired
    ArticleMapper articleMapper;

    @Override
    public List<Comments> getCommentsList(Integer articleId) {
        List<Comments> allComments = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getArticleId, articleId));
        List<Comments> rootComments = allComments.stream().filter(item -> item.getPid() == -1).toList();

        rootComments.forEach(rootComment -> {
            rootComment.setChildComments(allComments.stream()
                    .filter(comment -> comment.getPid() != null && rootComment.getId().equals(comment.getPid().longValue()))
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
        int current = (commentsQuery.getCurrent() == null) ? 1 : commentsQuery.getCurrent();
        int size = (commentsQuery.getSize() == null) ? 10 : commentsQuery.getSize();
        // 创建分页对象
        Page<Comments> page = new Page<>(current, size);
        Map<Long, String> articleMap = articleMapper.selectList(null).stream().collect(
                Collectors.toMap(Article::getId, Article::getTitle)
        );
        List<CommentsBackRes> commentsList = commentsMapper.selectList(page, lqw).stream().map(comment->{
            CommentsBackRes commentsBackRes=new CommentsBackRes();
            BeanUtils.copyProperties(comment,commentsBackRes);
            commentsBackRes.setArticleName(articleMap.get(comment.getArticleId().longValue()));
            return commentsBackRes;
        }).toList();
        return new PageResult<>(commentsList, total);
    }

    @Override
    public boolean saveComments(Comments comments) {
        if (comments.getArticleId() == null || comments.getContent() == null
                || comments.getEmail() == null || comments.getUrl() == null || comments.getName() == null) {
            return false;
        }
        comments.setCreateDate(new Date());
        commentsMapper.insert(comments);
        return true;
    }

    @Override
    public void deleteComments(List<Integer> ids) {
        List<Comments> childComments = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                .in(Comments::getPid, ids));
        childComments.forEach(childComment->{
            commentsMapper.deleteById(childComment);
        });
        commentsMapper.deleteBatchIds(ids);
    }

    @Override
    public void updateComments(Comments comments) {
        List<Comments> childComments = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getPid, comments.getId()));
        childComments.forEach(childComment->{
            childComment.setArticleId(comments.getArticleId());
            commentsMapper.updateById(childComment);
        });
        commentsMapper.updateById(comments);
    }
}
