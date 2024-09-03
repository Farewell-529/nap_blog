package com.nap_blog.vo.query;

import lombok.Data;

@Data
public class FriendQuery extends PageQuery{
    //搜索内容
    private String keyword;
}
