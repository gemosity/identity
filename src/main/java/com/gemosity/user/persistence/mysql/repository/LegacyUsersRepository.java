package com.gemosity.user.persistence.mysql.repository;

import com.gemosity.user.persistence.mysql.domain.cms_v1.LegacyCmsUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegacyUsersRepository extends CrudRepository<LegacyCmsUser, Long> {
}
