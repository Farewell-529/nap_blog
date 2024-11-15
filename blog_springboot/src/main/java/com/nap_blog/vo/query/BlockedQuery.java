package com.nap_blog.vo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BlockedQuery extends PageQuery{
    private String keyword;
    private String createDate;
}
