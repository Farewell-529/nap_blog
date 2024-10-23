package com.nap_blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String infoKey;
    private String value;
    private Date createDate;
    private Date updateDate;
}
