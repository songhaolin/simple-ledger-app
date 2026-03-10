package com.ledger.controller;

import com.ledger.model.Ledger;
import com.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 账本控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ledgers")
public class LedgerController {

    private final LedgerService ledgerService;

    /**
     * 获取用户的所有账本
     */
    @GetMapping
    public Response<List<Ledger>> getLedgers(HttpServletRequest request) {
        // 从请求属性中获取UserId（JWT拦截器已验证）
        String userId = (String) request.getAttribute("userId");
        
        List<Ledger> ledgers = ledgerService.getUserLedgers(userId);
        return Response.success(ledgers);
    }
}
