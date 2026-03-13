package com.ledger.service;

import com.ledger.exception.BusinessException;
import com.ledger.exception.BusinessException.ErrorCodes;
import com.ledger.model.Ledger;
import com.ledger.repository.LedgerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

/**
 * 账本服务
 */
@Service
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    public LedgerService(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    /**
     * 获取用户的所有账本
     */
    public java.util.List<Ledger> getLedgersByUserId(String userId) {
        return ledgerRepository.findByOwnerIdOrMembersUserId(userId, userId);
    }

    /**
     * 创建账本
     */
    public Ledger createLedger(String userId, String name, String type, Integer budget) {
        // 1. 验证参数
        if (name == null || name.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "账本名称不能为空"
            );
        }

        if (!type.equals("personal") && !type.equals("family")) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "账本类型不正确"
            );
        }

        // 2. 创建账本对象
        Ledger ledger = new Ledger();
        ledger.setName(name);
        ledger.setType(type);
        ledger.setOwnerId(userId);
        ledger.setBudget(budget != null ? budget : 0);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new Date());
        ledger.setUpdatedAt(new Date());

        // 3. 添加成员（创建者）
        ArrayList<Ledger.Member> members = new ArrayList<>();
        Ledger.Member owner = new Ledger.Member();
        owner.setUserId(userId);
        owner.setRole("owner");
        owner.setJoinedAt(new Date());
        members.add(owner);

        ledger.setMembers(members);

        // 4. 保存到数据库
        return ledgerRepository.save(ledger);
    }
}
