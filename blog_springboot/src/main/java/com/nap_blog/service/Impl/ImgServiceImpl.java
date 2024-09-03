package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Img;
import com.nap_blog.mapper.ImgMapper;
import com.nap_blog.service.ImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;

@Service
public class ImgServiceImpl extends ServiceImpl<ImgMapper, Img> implements ImgService {
    @Autowired
    ImgMapper imgMapper;

    @Override
    public void saveImg(Long articleId, String fileName) {
        Img img = new Img();
        img.setArticleId(articleId);
        img.setImgName(fileName);
        img.setCreateDate(new Date());
        imgMapper.insert(img);
    }

    @Override
    public void deleteImg(List<Integer> articleIds) {
        LambdaQueryWrapper<Img> lqw = new LambdaQueryWrapper<>();
        lqw.in(Img::getArticleId,articleIds);
        String pre = "C:\\Users\\Farewell\\Desktop\\Blog\\upload\\img\\";
        List<String> list = this.list(lqw).stream()
                .map(img -> pre + img.getImgName())
                .toList();
        list.forEach(item -> {
            File file = new File(item);
            file.delete();
        });
        imgMapper.delete(lqw);
    }
}
