package com.javaee.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.javaee.blog.dto.request.NoteCreateRequest;
import com.javaee.blog.dto.vo.NoteVO;
import com.javaee.blog.dto.vo.TagVO;
import com.javaee.blog.entity.Note;
import com.javaee.blog.entity.association.NoteTags;
import com.javaee.blog.entity.Tag;
import com.javaee.blog.mapper.NoteMapper;
import com.javaee.blog.mapper.NoteTagsMapper;
import com.javaee.blog.mapper.TagMapper;
import com.javaee.blog.service.NoteService;
import com.javaee.blog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;
    private final TagMapper tagMapper;
    private final NoteTagsMapper noteTagsMapper;

    // ==================== 私有方法 ====================

    /**
     * 列表查询核心逻辑。动态拼接 WHERE + 批量查 tag 避免 N+1。
     * @param limit null = 不限，非 null = LIMIT 子句
     */
    private List<NoteVO> queryList(String keyword, String tagSlug, Integer limit) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Note::getPublishedAt);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Note::getTitle, keyword).or().like(Note::getContent, keyword));
        }
        if (tagSlug != null && !tagSlug.isBlank()) {
            Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(t -> t.getSlug(), tagSlug));
            if (tag == null) return Collections.emptyList();
            List<NoteTags> links = noteTagsMapper.selectList(
                    new LambdaQueryWrapper<NoteTags>().eq(NoteTags::getTagId, tag.getId()));
            List<Long> ids = links.stream().map(NoteTags::getNotesId).collect(Collectors.toList());
            if (ids.isEmpty()) return Collections.emptyList();
            wrapper.in(Note::getId, ids);
        }
        if (limit != null) wrapper.last("LIMIT " + limit);
        List<Note> notes = noteMapper.selectList(wrapper);
        Map<Long, List<TagVO>> tagMap = batchGetTags(notes.stream().map(Note::getId).collect(Collectors.toList()));
        return notes.stream().map(n -> toVO(n, tagMap.getOrDefault(n.getId(), Collections.emptyList()))).collect(Collectors.toList());
    }

    /** 批量查询多个笔记的标签，2 次 DB 查询替代 N 次单条查询 */
    private Map<Long, List<TagVO>> batchGetTags(List<Long> noteIds) {
        if (noteIds.isEmpty()) return Collections.emptyMap();
        List<NoteTags> links = noteTagsMapper.selectList(
                new LambdaQueryWrapper<NoteTags>().in(NoteTags::getNotesId, noteIds));
        if (links.isEmpty()) return Collections.emptyMap();
        List<Long> tagIds = links.stream().map(NoteTags::getTagId).distinct().collect(Collectors.toList());
        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        Map<Long, TagVO> tagVoMap = tags.stream().collect(Collectors.toMap(Tag::getId, TagVO::from));
        Map<Long, List<TagVO>> result = new HashMap<>();
        for (NoteTags link : links) {
            TagVO vo = tagVoMap.get(link.getTagId());
            if (vo != null) result.computeIfAbsent(link.getNotesId(), k -> new ArrayList<>()).add(vo);
        }
        return result;
    }

    private NoteVO toVO(Note note, List<TagVO> tags) {
        NoteVO vo = new NoteVO();
        vo.setId(note.getId());
        vo.setTitle(note.getTitle());
        vo.setSlug(note.getSlug());
        vo.setContent(note.getContent());
        vo.setPublishedAt(note.getPublishedAt());
        vo.setTags(tags);
        return vo;
    }


    private List<TagVO> getTagsByNoteId(Long noteId) {
        List<NoteTags> links = noteTagsMapper.selectList(
                new LambdaQueryWrapper<NoteTags>().eq(NoteTags::getNotesId, noteId));
        if (links.isEmpty()) return Collections.emptyList();
        List<Long> tagIds = links.stream().map(NoteTags::getTagId).collect(Collectors.toList());
        return tagMapper.selectBatchIds(tagIds).stream().map(TagVO::from).collect(Collectors.toList());
    }

    /** 维护笔记-标签关联。已有标签直接关联，新标签批量查不存在则创建再关联 */
    private void handleTags(Long noteId, List<Long> tagIds, List<String> newTags) {
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                NoteTags link = new NoteTags();
                link.setNotesId(noteId);
                link.setTagId(tagId);
                noteTagsMapper.insert(link);
            }
        }
        if (newTags != null && !newTags.isEmpty()) {
            List<Tag> existing = tagMapper.selectList(
                    new LambdaQueryWrapper<Tag>().in(t -> t.getName(), newTags));
            Set<String> existingNames = existing.stream().map(t -> t.getName()).collect(Collectors.toSet());
            for (String name : newTags) {
                Tag tag;
                if (existingNames.contains(name)) {
                    tag = existing.stream().filter(t -> t.getName().equals(name)).findFirst()
                            .orElseThrow(() -> new IllegalStateException("标签数据异常"));
                } else {
                    tag = new Tag();
                    tag.setName(name);
                    tag.setSlug(SlugUtil.toSlug(name));
                    tagMapper.insert(tag);
                    existingNames.add(name);
                }
                NoteTags link = new NoteTags();
                link.setNotesId(noteId);
                link.setTagId(tag.getId());
                noteTagsMapper.insert(link);
            }
        }
    }

    // ==================== 公开方法 ====================

    /**
     * 前台笔记列表（全量）。
     * @param keyword 标题/正文关键词搜索
     * @param tagSlug 标签 slug 筛选
     */
    @Override
    public List<NoteVO> list(String keyword, String tagSlug) {
        return queryList(keyword, tagSlug, null);
    }

    /** 前台笔记列表（限制条数），供首页使用 */
    @Override
    public List<NoteVO> list(String keyword, String tagSlug, int limit) {
        return queryList(keyword, tagSlug, limit);
    }

    /** 后台创建笔记。slug 自动生成，发布日期取当前时间 */
    @Override
    @Transactional
    public void create(NoteCreateRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setSlug(SlugUtil.toUniqueSlug(request.getTitle()));
        note.setContent(request.getContent());
        note.setPublishedAt(LocalDateTime.now());
        noteMapper.insert(note);
        handleTags(note.getId(), request.getTagIds(), request.getNewTags());
    }

    /** 后台更新笔记。不存在时抛 NoSuchElementException */
    @Override
    @Transactional
    public void update(Long id, NoteCreateRequest request) {
        Note note = noteMapper.selectById(id);
        if (note == null) throw new NoSuchElementException("笔记不存在");
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        noteMapper.updateById(note);
        noteTagsMapper.delete(new LambdaQueryWrapper<NoteTags>().eq(NoteTags::getNotesId, id));
        handleTags(id, request.getTagIds(), request.getNewTags());
    }

    /** 后台获取笔记详情（供编辑页加载）。null = 不存在 */
    @Override
    public NoteVO getById(Long id) {
        Note note = noteMapper.selectById(id);
        if (note == null) return null;
        return toVO(note, getTagsByNoteId(note.getId()));
    }

    /** 后台删除笔记 */
    @Override
    @Transactional
    public void delete(Long id) {
        noteTagsMapper.delete(new LambdaQueryWrapper<NoteTags>().eq(NoteTags::getNotesId, id));
        noteMapper.deleteById(id);
    }
}
