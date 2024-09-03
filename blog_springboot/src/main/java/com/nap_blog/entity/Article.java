package com.nap_blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    //标题
    private String title;
    //内容
    private String content;
    //用户id
    private Long userId;
    //所属分类id
    private Integer categoryId;
    //创建日期
    private Date createDate;
    //更新日期
    private Date updateDate;
    //文章字数
    private Integer wordCount;
    //发布状态  (0代表未发布，1代表已发布)
    private Integer status;
    //浏览量
    private Integer viewCount;
    @TableField(exist = false)
    //多对多TagsList
    private List<Tags> tagsList;
}
