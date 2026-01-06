package com.akshay.notionlite.pages.service;

import com.akshay.notionlite.common.ApiException;
import com.akshay.notionlite.pages.model.PageEntity;
import com.akshay.notionlite.pages.repo.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PageService {

    private final PageRepository pageRepo;

    public PageService(PageRepository pageRepo) {
        this.pageRepo = pageRepo;
    }

    @Transactional
    public PageEntity create(UUID workspaceId, UUID creatorId, String title) {
        if (title == null || title.isBlank()) throw ApiException.badRequest("title required");

        PageEntity p = new PageEntity();
        p.setWorkspaceId(workspaceId);
        p.setTitle(title.trim());
        p.setCreatedBy(creatorId);
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());

        return pageRepo.save(p);
    }

    @Transactional(readOnly = true)
    public List<PageEntity> list(UUID workspaceId) {
        return pageRepo.findByWorkspaceIdOrderByUpdatedAtDesc(workspaceId);
    }

    @Transactional(readOnly = true)
    public PageEntity get(UUID pageId) {
        return pageRepo.findById(pageId).orElseThrow(() -> ApiException.notFound("Page not found"));
    }

    @Transactional
    public PageEntity updateTitle(UUID pageId, String title) {
        if (title == null || title.isBlank()) throw ApiException.badRequest("title required");
        PageEntity p = get(pageId);
        p.setTitle(title.trim());
        p.setUpdatedAt(Instant.now());
        return pageRepo.save(p);
    }

    @Transactional
    public void delete(UUID pageId) {
        PageEntity p = get(pageId);
        pageRepo.delete(p);
    }
}
