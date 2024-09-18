package com.nap_blog.vo.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.nap_blog.entity.Comments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentsBackRes {
    private Long id;

    private Integer articleId;

    private Integer userId;

    private Integer pid;

    private String target;

    private String name;

    private String content;

    private String email;

    private String avatar;

    private String url;

    private String articleName;

    private Date createDate;

}
