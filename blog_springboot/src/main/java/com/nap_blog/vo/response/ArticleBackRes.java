package com.nap_blog.vo.response;

import com.nap_blog.entity.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleBackRes {
    private Long id;
    //标题
    private String title;
    //所属分类
    private String categoryName;
    //标签
    private List<Tags> tagsList;
    //创建日期
    private Date createDate;
    //更新日期
    private Date updateDate;
    //发布状态  (0代表未发布，1代表已发布)
//    private Integer status;
}
