package com.javaee.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.entity.HeroQuote;
import com.javaee.blog.mapper.HeroQuoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeroQuoteService {

    private final HeroQuoteMapper mapper;

    public List<HeroQuote> list() {
        return mapper.selectList(new LambdaQueryWrapper<HeroQuote>().orderByDesc(HeroQuote::getCreatedAt));
    }

    public void create(String content, String author, String source) {
        HeroQuote q = new HeroQuote();
        q.setContent(content);
        q.setAuthor(author != null ? author : "");
        q.setSource(source != null ? source : "");
        mapper.insert(q);
    }

    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
