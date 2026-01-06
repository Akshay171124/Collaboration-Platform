package com.akshay.notionlite.blocks.service;

import com.akshay.notionlite.blocks.model.BlockEntity;
import com.akshay.notionlite.blocks.repo.BlockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class BlockOrderService {

    private static final BigDecimal ONE = new BigDecimal("1");
    private static final int SCALE = 20;

    private final BlockRepository blockRepo;

    public BlockOrderService(BlockRepository blockRepo) {
        this.blockRepo = blockRepo;
    }

    @Transactional(readOnly = true)
    public BigDecimal computeNewPosition(UUID pageId, UUID afterBlockId, UUID beforeBlockId) {
        BigDecimal afterPos = null;
        BigDecimal beforePos = null;

        if (afterBlockId != null) {
            BlockEntity after = blockRepo.findById(afterBlockId)
                    .orElseThrow(() -> new IllegalArgumentException("afterBlockId not found"));
            assertSamePage(pageId, after);
            afterPos = after.getPosition();
        }

        if (beforeBlockId != null) {
            BlockEntity before = blockRepo.findById(beforeBlockId)
                    .orElseThrow(() -> new IllegalArgumentException("beforeBlockId not found"));
            assertSamePage(pageId, before);
            beforePos = before.getPosition();
        }

        // Insert at end
        if (afterPos != null && beforePos == null) {
            return afterPos.add(ONE);
        }

        // Insert at beginning
        if (afterPos == null && beforePos != null) {
            return beforePos.subtract(ONE);
        }

        // Insert between two known neighbors
        if (afterPos != null) {
            if (beforePos != null) {
                if (afterPos.compareTo(beforePos) >= 0) {
                    throw new IllegalArgumentException("Invalid neighbors: after >= before");
                }
                return afterPos.add(beforePos).divide(new BigDecimal("2"), SCALE, RoundingMode.HALF_UP);
            }
        }

        // No neighbors provided -> append to end of page
        BigDecimal max = blockRepo.maxPosition(pageId).orElse(new BigDecimal("0"));
        return max.add(ONE);
    }

    private void assertSamePage(UUID pageId, BlockEntity block) {
        if (!block.getPageId().equals(pageId)) {
            throw new IllegalArgumentException("Block does not belong to the page");
        }
    }
}
