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
public class Blocked {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ip;
    private String blockReason;
    private Integer status;
    private Date createDate;
}
