package com.nap_blog.vo.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Builder
public class TagsBackRes {
    private Long id;
    private Integer useCount;
    private String tagsName;
    private Date createDate;
}
