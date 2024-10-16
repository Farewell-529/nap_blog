package com.nap_blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloggerInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;


    private String avatar;

    private String motto;

    private String bio;

    private Date updateAt;

    private String githubUrl;

    private String xUrl;
}
