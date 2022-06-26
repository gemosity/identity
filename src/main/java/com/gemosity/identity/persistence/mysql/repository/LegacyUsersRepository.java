package com.gemosity.identity.persistence.mysql.repository;

import com.gemosity.identity.persistence.mysql.domain.cms_v1.LegacyCmsUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegacyUsersRepository extends CrudRepository<LegacyCmsUser, Long> {
}
