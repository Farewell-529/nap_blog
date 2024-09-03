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
public class Friend {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String friendName;
    private String url;
    private String avatarUrl;
    private String description;
    private Date createDate;
    private Date updateDate;
}
