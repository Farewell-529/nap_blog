package com.nap_blog.vo.response;

import com.nap_blog.entity.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleInfoRes {
    private Long id;
    //标题
    private String title;
    //内容
    private String content;
    //所属分类名
    private String categoryName;

    //所属标签
    private List<Tags> tags;
    //创建日期
    private Date createDate;
    //更新日期
    private Date updateDate;
    //浏览量
    private Integer viewCount;
}
