package com.nap_blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String avatar;

    private String motto;

    private String bio;

    private Long updateAt;

    private String githubUrl;

    private String tgUrl;

    private String xUrl;
}
