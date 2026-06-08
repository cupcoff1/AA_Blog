package com.javaee.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.dto.vo.StickyNoteVO;
import com.javaee.blog.entity.StickyNote;
import com.javaee.blog.mapper.StickyNoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StickyNoteServiceImpl implements StickyNoteService {

    private final StickyNoteMapper mapper;

    @Override
    public List<StickyNoteVO> list() {
        return mapper.selectList(new LambdaQueryWrapper<StickyNote>().orderByDesc(StickyNote::getCreatedAt))
                .stream().map(n -> {
                    StickyNoteVO vo = new StickyNoteVO();
                    vo.setId(n.getId());
                    vo.setContent(n.getContent());
                    vo.setColor(n.getColor());
                    vo.setRotate(n.getRotate() != null ? n.getRotate() : 0);
                    vo.setCustom(true);
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    public void create(StickyNoteCreateRequest req) {
        StickyNote note = new StickyNote();
        note.setContent(req.getContent());
        note.setColor(req.getColor());
        note.setRotate(req.getRotate() != null ? req.getRotate() : new Random().nextInt(7) - 3);
        mapper.insert(note);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
