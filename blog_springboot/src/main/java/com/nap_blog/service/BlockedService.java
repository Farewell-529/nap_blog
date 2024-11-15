package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Blocked;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.BlockedQuery;

import java.util.List;

public interface BlockedService extends IService<Blocked> {
    PageResult<Blocked> getBlockedList(BlockedQuery blockedQuery);

    Result saveBlocked(Blocked blocked);

    void editBlocked(Blocked blocked);

    void deleteBlockedByIds(List<Integer> ids);

    Blocked checkBlocked(String ip);
}
