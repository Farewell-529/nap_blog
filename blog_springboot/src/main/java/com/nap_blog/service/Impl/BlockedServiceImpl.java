package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Blocked;
import com.nap_blog.entity.Comments;
import com.nap_blog.mapper.BlockedMapper;
import com.nap_blog.service.BlockedService;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.BlockedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BlockedServiceImpl extends ServiceImpl<BlockedMapper, Blocked> implements BlockedService {
    @Autowired
    BlockedMapper blockedMapper;

    @Override
    public PageResult<Blocked> getBlockedList(BlockedQuery blockedQuery) {
        //获得黑名单总数
        long total = this.count();
        if (total == 0) {
            return new PageResult<>();
        }
        LambdaQueryWrapper<Blocked> lqw = new LambdaQueryWrapper<>();
        //判断是否又查询条件
        if (blockedQuery.getKeyword() != null && !blockedQuery.getKeyword().trim().isEmpty()) {
            lqw.like(Blocked::getIp, blockedQuery.getKeyword());
        }
        if (blockedQuery.getCreateDate() != null && !blockedQuery.getCreateDate().trim().isEmpty()) {
            LocalDate localDate = LocalDate.parse(blockedQuery.getCreateDate());
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime startOfNextDay = localDate.plusDays(1).atStartOfDay();
            lqw.ge(Blocked::getCreateDate, startOfDay);
            lqw.lt(Blocked::getCreateDate, startOfNextDay);
        }
        int current = (blockedQuery.getCurrent() == null) ? 1 : blockedQuery.getCurrent();
        int size = (blockedQuery.getSize() == null) ? 10 : blockedQuery.getSize();
        //创造分页对象
        Page<Blocked> page = new Page<>(current, size);
        List<Blocked> blockedList = blockedMapper.selectList(page,lqw);
        return new PageResult<>(blockedList,total);
    }

    @Override
    public Result saveBlocked(Blocked blocked) {
        Blocked isExist = this.checkBlocked(blocked.getIp());
        if (!Objects.isNull(isExist)) {
            return Result.success("该ip已经拉黑了");
        }
        blocked.setCreateDate(new Date());
        blocked.setStatus(blocked.getStatus()==null?1:blocked.getStatus());
        blockedMapper.insert(blocked);
        return  Result.success();
    }

    @Override
    public void editBlocked(Blocked blocked) {
        blockedMapper.updateById(blocked);
    }

    @Override
    public void deleteBlockedByIds(List<Integer> ids) {
        blockedMapper.deleteBatchIds(ids);
    }

    public Blocked checkBlocked(String ip){
        List<Blocked> blockedList = blockedMapper.selectList(null);
        return blockedList.stream()
                .filter(val -> Objects.equals(val.getIp(), ip))
                .findFirst() // 找到第一个匹配的元素
                .orElse(null);
    }
}
