package com.nap_blog.vo.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.nap_blog.entity.Comments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsRes {
    private Long id;
    private Integer userId;
    private Integer pid;
    private String replyName;
    private String name;
    private String content;
    private String avatar;
    private Date createDate;
    private List<CommentsRes> childComments;
}
