package com.nap_blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Visits {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ip;
    private String browser;
    private String city;
    private String fromUrl;
    private Date visitTime;
}
