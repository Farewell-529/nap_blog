package com.nap_blog.vo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentsQuery extends PageQuery{
    private Integer articleId;
    private String targetType;
    private String keyword;
}
