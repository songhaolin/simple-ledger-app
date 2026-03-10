package com.ledger.repository;

import com.ledger.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户仓库
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * 根据手机号查询用户
     */
    User findByPhone(String phone);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
}
