package com.nap_blog.vo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class VisitsQuery extends PageQuery{
    private String browser;
    private String ip;
    private String city;
    private String visitTime;
    private String fromUrl;
}
