package com.akshay.notionlite.blocks.repo;

import com.akshay.notionlite.blocks.model.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlockRepository extends JpaRepository<BlockEntity, UUID> {
    List<BlockEntity> findByPageIdOrderByPositionAsc(UUID pageId);

    @Query("select max(b.position) from BlockEntity b where b.pageId = :pageId")
    Optional<BigDecimal> maxPosition(UUID pageId);

    @Query("select min(b.position) from BlockEntity b where b.pageId = :pageId")
    Optional<BigDecimal> minPosition(UUID pageId);
}
