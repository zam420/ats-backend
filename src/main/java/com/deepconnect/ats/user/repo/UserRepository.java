package com.deepconnect.ats.user.repo;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.common.repo.BaseRepository;

@Repository
public interface UserRepository extends BaseRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    List<User> findByType(Integer type);
    List<User> findByStatus(Integer status);
    List<User> findByIdIn(Set<Long> ids);

    @Modifying
    @Query(value = "delete from ats_user where email=?1",nativeQuery = true)
    void deleteUser(String email);
}
