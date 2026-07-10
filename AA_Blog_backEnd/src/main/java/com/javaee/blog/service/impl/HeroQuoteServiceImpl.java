package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.HeroQuoteCreateRequest;
import com.javaee.blog.dto.vo.HeroQuoteVO;
import com.javaee.blog.entity.HeroQuote;
import com.javaee.blog.mapper.HeroQuoteMapper;
import com.javaee.blog.service.HeroQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeroQuoteServiceImpl implements HeroQuoteService {

    private final HeroQuoteMapper mapper;

    @Override
    public List<HeroQuoteVO> list() {
        return mapper.selectList(new LambdaQueryWrapper<HeroQuote>().orderByDesc(HeroQuote::getCreatedAt))
                .stream().map(q -> {
                    HeroQuoteVO vo = new HeroQuoteVO();
                    vo.setId(q.getId());
                    vo.setContent(q.getContent());
                    vo.setAuthor(q.getAuthor());
                    vo.setSource(q.getSource());
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(HeroQuoteCreateRequest request) {
        HeroQuote q = new HeroQuote();
        q.setContent(request.getContent());
        q.setAuthor(request.getAuthor() != null ? request.getAuthor() : "");
        q.setSource(request.getSource() != null ? request.getSource() : "");
        mapper.insert(q);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
