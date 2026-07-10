package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.StickyNoteCreateRequest;
import com.javaee.blog.dto.vo.StickyNoteVO;
import com.javaee.blog.entity.StickyNote;
import com.javaee.blog.mapper.StickyNoteMapper;
import com.javaee.blog.service.StickyNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StickyNoteServiceImpl implements StickyNoteService {

    private final StickyNoteMapper mapper;

    @Override
    public List<StickyNoteVO> list(String currentUser, String source) {
        LambdaQueryWrapper<StickyNote> wrapper = new LambdaQueryWrapper<StickyNote>().orderByDesc(StickyNote::getCreatedAt);
        if ("admin".equals(source)) {
            wrapper.and(w -> w.isNull(StickyNote::getAuthorName).or().eq(StickyNote::getAuthorName, ""));
        } else if ("guest".equals(source)) {
            wrapper.isNotNull(StickyNote::getAuthorName).ne(StickyNote::getAuthorName, "");
        }
        return mapper.selectList(wrapper)
                .stream().map(n -> {
                    StickyNoteVO vo = new StickyNoteVO();
                    vo.setId(n.getId());
                    vo.setContent(n.getContent());
                    vo.setColor(n.getColor());
                    vo.setRotate(n.getRotate() != null ? n.getRotate() : 0);
                    vo.setCategory(n.getCategory() != null ? n.getCategory() : "to_aa");
                    vo.setAuthorName(n.getAuthorName() != null ? n.getAuthorName() : "");
                    vo.setAuthorAvatar(n.getAuthorAvatar() != null ? n.getAuthorAvatar() : "");
                    vo.setOwn(n.getAuthorName() != null && n.getAuthorName().equals(currentUser));
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    public void create(StickyNoteCreateRequest req, String authorName, String authorAvatar) {
        StickyNote note = new StickyNote();
        note.setContent(req.getContent());
        note.setColor(req.getColor());
        note.setRotate(req.getRotate() != null ? req.getRotate() : ThreadLocalRandom.current().nextInt(7) - 3);
        note.setCategory(req.getCategory() != null ? req.getCategory() : "to_aa");
        note.setAuthorName(authorName);
        note.setAuthorAvatar(authorAvatar);
        mapper.insert(note);
    }

    @Override
    public boolean delete(Long id, String requester, boolean isAdmin) {
        if (isAdmin) return mapper.deleteById(id) > 0;
        if (requester == null) return false;
        // 单条 DELETE + WHERE 条件，消除 TOCTOU 竞态窗口
        int rows = mapper.delete(new LambdaQueryWrapper<StickyNote>()
                .eq(StickyNote::getId, id)
                .eq(StickyNote::getAuthorName, requester));
        return rows > 0;
    }
}
