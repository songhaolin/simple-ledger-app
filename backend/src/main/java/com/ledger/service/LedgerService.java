package com.ledger.service;

import com.ledger.model.Ledger;
import com.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账本服务
 */
@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    /**
     * 获取用户的所有账本
     */
    public List<Ledger> getUserLedgers(String userId) {
        return ledgerRepository.findByOwnerIdOrMembersUserId(userId, userId);
    }
}
