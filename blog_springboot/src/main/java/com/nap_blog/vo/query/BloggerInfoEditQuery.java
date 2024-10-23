package com.nap_blog.vo.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloggerInfoEditQuery {
    private String BloggerName;
    private String Avatar;
    private String Bio;
    private String Motto;
    private String Github_url;
    private String X_url;
    private String Bilibili_url;
}
