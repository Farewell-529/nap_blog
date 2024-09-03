package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Img;

import java.util.List;

public interface ImgService extends IService<Img> {
    void saveImg(Long articleId,String fileName);

    void deleteImg(List<Integer> articleIds);
}
