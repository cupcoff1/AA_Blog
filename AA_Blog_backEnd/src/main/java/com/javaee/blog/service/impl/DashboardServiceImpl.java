package com.javaee.blog.service.impl;

import com.javaee.blog.dto.vo.*;
import com.javaee.blog.mapper.*;
import com.javaee.blog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AboutService aboutService;
    private final BlogService blogService;
    private final NotesService notesService;
    private final ProjectsService projectsService;
    private final CommentService commentService;
    private final BlogMapper blogMapper;
    private final NotesMapper notesMapper;
    private final ProjectsMapper projectsMapper;
    private final CommentMapper commentMapper;
    private final TagsMapper tagsMapper;
    private final BlogTagsMapper blogTagsMapper;
    private final NotesTagsMapper notesTagsMapper;
    private final ProjectsTagsMapper projectsTagsMapper;

    @Override
    public HomeVO getHome() {
        HomeVO vo = new HomeVO();
        vo.setAbout(aboutService.get());

        vo.setLatestBlogs(blogService.list(null, null, 5));
        vo.setLatestNotes(notesService.list(null, null, 5));
        vo.setLatestProjects(projectsService.list(6));

        return vo;
    }

    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setBlogCount(blogMapper.selectCount(null));
        vo.setNoteCount(notesMapper.selectCount(null));
        vo.setProjectCount(projectsMapper.selectCount(null));
        vo.setCommentCount(commentMapper.selectCount(null));

        vo.setRecentComments(commentService.adminList(5));

        vo.setTags(buildTagStats());
        return vo;
    }

    private List<TagStatVO> buildTagStats() {
        List<com.javaee.blog.entity.Tags> tags = tagsMapper.selectList(null);
        if (tags.isEmpty()) return Collections.emptyList();

        Map<Long, Long> blogCounts = toTagCountMap(blogTagsMapper.countByTag());
        Map<Long, Long> noteCounts = toTagCountMap(notesTagsMapper.countByTag());
        Map<Long, Long> projectCounts = toTagCountMap(projectsTagsMapper.countByTag());

        return tags.stream().map(tag -> {
            TagStatVO stat = new TagStatVO();
            stat.setName(tag.getName());
            stat.setSlug(tag.getSlug());
            stat.setBlogCount(blogCounts.getOrDefault(tag.getId(), 0L).intValue());
            stat.setNoteCount(noteCounts.getOrDefault(tag.getId(), 0L).intValue());
            stat.setProjectCount(projectCounts.getOrDefault(tag.getId(), 0L).intValue());
            return stat;
        }).collect(Collectors.toList());
    }

    private Map<Long, Long> toTagCountMap(List<Map<String, Object>> rows) {
        Map<Long, Long> map = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Long tagId = ((Number) row.get("tag_id")).longValue();
            Long cnt = ((Number) row.get("cnt")).longValue();
            map.put(tagId, cnt);
        }
        return map;
    }
}
