package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Friend;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.FriendQuery;

import java.util.List;

public interface FriendService extends IService<Friend> {
    PageResult<Friend> listFriend(FriendQuery friendQuery);

    void saveFriend (Friend friend);

    void updateFriend(Friend friend);

    void deleteFriend(List<Integer> ids);
}
