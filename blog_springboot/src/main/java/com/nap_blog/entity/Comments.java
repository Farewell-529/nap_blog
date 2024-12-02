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
    private String targetType;
    private Integer targetId;
    private Integer userId;
    private Integer pid;
    private Integer replyId;
    private Integer status;
    private String ip;
    private String name;
    private String content;
    private String email;
    private String avatar;
    private String url;
    private Integer isAuthor;
    private Date createDate;
    @TableField(exist = false)
    private List<Comments> childComments;
}
