package com.nap_blog.vo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentsBackRes {
    private Long id;
    private Integer targetId;
    private Integer userId;
    private Integer pid;
    private String parentName;
    private String replyName;
    private String name;
    private String content;
    private String email;
    private String avatar;
    private String url;
    private String articleName;
    private Date createDate;
}
