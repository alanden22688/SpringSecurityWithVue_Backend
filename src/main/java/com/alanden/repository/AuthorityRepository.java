package com.alanden.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alanden.entity.AuthorityEntity;

/*
 * 用於存取「權限」
 */
@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

    @Query("SELECT a.authority FROM AuthorityEntity a WHERE a.user.username = :username")
    List<String> findAuthoritiesByUsername(String username);
}