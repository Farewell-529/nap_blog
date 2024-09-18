package com.nap_blog.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveArticleRes {
    private long id;
    private String articleTitle;
    private Date createDate;
    private String categoryName;
}
