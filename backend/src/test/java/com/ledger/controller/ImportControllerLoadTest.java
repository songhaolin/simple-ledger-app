package com.ledger.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 测试ImportController是否被正确加载
 */
@SpringBootTest
public class ImportControllerLoadTest {

    @Autowired
    private ImportController importController;

    @Test
    public void testImportControllerLoad() {
        assertNotNull(importController, "ImportController should be loaded");
    }
}
