package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Friend;
import com.nap_blog.entity.Tags;
import com.nap_blog.service.CommentsService;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.FriendQuery;
import com.nap_blog.mapper.FriendMapper;
import com.nap_blog.service.FriendService;
import com.nap_blog.vo.response.CommentsRes;
import com.nap_blog.vo.response.FriendInfo;
import com.nap_blog.vo.response.FriendsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {
    @Autowired
    FriendMapper friendMapper;
    @Autowired
    CommentsService commentsService;
    @Override
    public PageResult<Friend> listFriend(FriendQuery friendQuery) {
        long total = this.count();
        if (total == 0) {
            return new PageResult<>();
        }
        LambdaQueryWrapper<Friend> lqw = new LambdaQueryWrapper<>();
        if (friendQuery.getKeyword() != null&&!friendQuery.getKeyword().trim().isEmpty()) {
            lqw.like(Friend::getFriendName, friendQuery.getKeyword());
        }
        if (friendQuery.getCreateDate() != null&&!friendQuery.getCreateDate().trim().isEmpty()) {
            LocalDate localDate = LocalDate.parse(friendQuery.getCreateDate());
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime startOfNextDay = localDate.plusDays(1).atStartOfDay();
            lqw.ge(Friend::getCreateDate,startOfDay);
            lqw.lt(Friend::getCreateDate,startOfNextDay);
        }
        int current = (friendQuery.getCurrent() == null) ? 1 : friendQuery.getCurrent();
        int size = (friendQuery.getSize() == null) ? 10 : friendQuery.getSize();
        //创建分页对象
        Page<Friend> page = new Page<>(current, size);
        return new PageResult<>(this.list(page, lqw), total);
    }

    @Override
    public void saveFriend(Friend friend) {
        friend.setCreateDate(new Date());
        friend.setUpdateDate(new Date());
        friendMapper.insert(friend);
    }

    @Override
    public void updateFriend(Friend friend) {
        friend.setUpdateDate(new Date());
        friendMapper.updateById(friend);
    }

    @Override
    public void deleteFriend(List<Integer> ids) {
        friendMapper.deleteBatchIds(ids);
    }

    @Override
    public FriendsRes listFriendFront() {
        List<CommentsRes> commentsList = commentsService.getCommentsList("friends", 0);
        return FriendsRes.builder().friendList(listFriendInFOFront()).commentsList(commentsList).build();
    }

    @Override
    public List<FriendInfo> listFriendInFOFront() {
        List<Friend> friends = friendMapper.selectList(null);
        return friends.stream().map(friend ->
                FriendInfo.builder()
                        .id(friend.getId())
                        .friendName(friend.getFriendName()) 
                        .description(friend.getDescription())
                        .avatarUrl(friend.getAvatarUrl())
                        .url(friend.getUrl())
                        .build()
        ).toList();
    }
}
