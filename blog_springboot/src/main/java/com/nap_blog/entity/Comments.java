package com.nap_blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {
    @TableId(type = IdType.AUTO)
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

    private Date createDate;
    @TableField(exist = false)
    private List<Comments> childComments;
}
