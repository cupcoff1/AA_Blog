package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.NotesCreateRequest;
import com.javaee.blog.dto.vo.NotesVO;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Notes;
import com.javaee.blog.entity.NotesTags;
import com.javaee.blog.entity.Tags;
import com.javaee.blog.mapper.NotesMapper;
import com.javaee.blog.mapper.NotesTagsMapper;
import com.javaee.blog.mapper.TagsMapper;
import com.javaee.blog.service.NotesService;
import com.javaee.blog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements NotesService {

    private final NotesMapper notesMapper;
    private final TagsMapper tagsMapper;
    private final NotesTagsMapper notesTagsMapper;

    @Override
    public List<NotesVO> list(String keyword, String tagSlug) {
        return queryList(keyword, tagSlug, null);
    }

    @Override
    public List<NotesVO> list(String keyword, String tagSlug, int limit) {
        return queryList(keyword, tagSlug, limit);
    }

    private List<NotesVO> queryList(String keyword, String tagSlug, Integer limit) {
        LambdaQueryWrapper<Notes> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notes::getPublishedAt);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Notes::getTitle, keyword).or().like(Notes::getContent, keyword));
        }
        if (tagSlug != null && !tagSlug.isBlank()) {
            Tags tag = tagsMapper.selectOne(new LambdaQueryWrapper<Tags>().eq(Tags::getSlug, tagSlug));
            if (tag == null) return Collections.emptyList();
            List<NotesTags> links = notesTagsMapper.selectList(
                    new LambdaQueryWrapper<NotesTags>().eq(NotesTags::getTagId, tag.getId()));
            List<Long> ids = links.stream().map(NotesTags::getNotesId).collect(Collectors.toList());
            if (ids.isEmpty()) return Collections.emptyList();
            wrapper.in(Notes::getId, ids);
        }
        if (limit != null) wrapper.last("LIMIT " + limit);
        List<Notes> notes = notesMapper.selectList(wrapper);
        return notes.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(NotesCreateRequest request) {
        Notes note = new Notes();
        note.setTitle(request.getTitle());
        note.setSlug(generateSlug(request.getTitle()));
        note.setContent(request.getContent());
        note.setPublishedAt(LocalDateTime.now());
        notesMapper.insert(note);

        handleTags(note.getId(), request.getTagIds(), request.getNewTags());
    }

    @Override
    @Transactional
    public void update(Long id, NotesCreateRequest request) {
        Notes note = notesMapper.selectById(id);
        if (note == null) return;
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        notesMapper.updateById(note);

        notesTagsMapper.delete(new LambdaQueryWrapper<NotesTags>().eq(NotesTags::getNotesId, id));
        handleTags(id, request.getTagIds(), request.getNewTags());
    }

    @Override
    public NotesVO getById(Long id) {
        Notes note = notesMapper.selectById(id);
        if (note == null) return null;
        return toVO(note);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        notesTagsMapper.delete(new LambdaQueryWrapper<NotesTags>().eq(NotesTags::getNotesId, id));
        notesMapper.deleteById(id);
    }

    // ==================== 私有方法 ====================

    private NotesVO toVO(Notes note) {
        NotesVO vo = new NotesVO();
        vo.setId(note.getId());
        vo.setTitle(note.getTitle());
        vo.setSlug(note.getSlug());
        vo.setContent(note.getContent());
        vo.setPublishedAt(note.getPublishedAt());
        vo.setTags(getTagsByNoteId(note.getId()));
        return vo;
    }

    private List<TagVO> getTagsByNoteId(Long noteId) {
        List<NotesTags> links = notesTagsMapper.selectList(
                new LambdaQueryWrapper<NotesTags>().eq(NotesTags::getNotesId, noteId));
        if (links.isEmpty()) return Collections.emptyList();

        List<Long> tagIds = links.stream().map(NotesTags::getTagId).collect(Collectors.toList());
        List<Tags> tags = tagsMapper.selectBatchIds(tagIds);
        return tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            vo.setId(tag.getId());
            vo.setName(tag.getName());
            vo.setSlug(tag.getSlug());
            return vo;
        }).collect(Collectors.toList());
    }

    private void handleTags(Long noteId, List<Long> tagIds, List<String> newTags) {
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                NotesTags link = new NotesTags();
                link.setNotesId(noteId);
                link.setTagId(tagId);
                notesTagsMapper.insert(link);
            }
        }
        if (newTags != null) {
            for (String name : newTags) {
                Tags tag = tagsMapper.selectOne(
                        new LambdaQueryWrapper<Tags>().eq(Tags::getName, name));
                if (tag == null) {
                    tag = new Tags();
                    tag.setName(name);
                    tag.setSlug(SlugUtil.toSlug(name));
                    tagsMapper.insert(tag);
                }
                NotesTags link = new NotesTags();
                link.setNotesId(noteId);
                link.setTagId(tag.getId());
                notesTagsMapper.insert(link);
            }
        }
    }

    private String generateSlug(String title) {
        String slug = SlugUtil.toSlug(title);
        Long count = notesMapper.selectCount(new LambdaQueryWrapper<Notes>().eq(Notes::getSlug, slug));
        if (count > 0) slug += "-" + (count + 1);
        return slug;
    }
}
