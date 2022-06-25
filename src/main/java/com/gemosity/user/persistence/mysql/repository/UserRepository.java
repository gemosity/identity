package com.gemosity.user.persistence.mysql.repository;

import com.gemosity.user.persistence.mysql.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
