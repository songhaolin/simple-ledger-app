package com.ledger.controller;

import com.ledger.model.Ledger;
import com.ledger.service.LedgerService;
import com.ledger.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账本控制器
 */
@RestController
@RequestMapping("/ledgers")
public class LedgerController {

    private final LedgerService ledgerService;
    private final JwtUtil jwtUtil;

    public LedgerController(LedgerService ledgerService, JwtUtil jwtUtil) {
        this.ledgerService = ledgerService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取账本列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Ledger>>> getLedgers(@RequestHeader("Authorization") String authHeader) {
        // 提取Token
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserIdFromToken(token);

        // 获取账本列表
        List<Ledger> ledgers = ledgerService.getLedgersByUserId(userId);

        return ApiResponse.success(ledgers);
    }

    /**
     * 创建账本
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createLedger(@RequestBody Map<String, Object> request, @RequestHeader("Authorization") String authHeader) {
        // 提取Token
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserIdFromToken(token);

        // 获取参数
        String name = (String) request.get("name");
        String type = (String) request.get("type");
        Integer budget = request.get("budget") != null ? ((Number) request.get("budget")).intValue() : null;

        // 创建账本
        Ledger ledger = ledgerService.createLedger(userId, name, type, budget);

        // 包装为ApiResponse
        Map<String, Object> data = new HashMap<>();
        data.put("ledgerId", ledger.getId());

        return ApiResponse.success(data);
    }
}
