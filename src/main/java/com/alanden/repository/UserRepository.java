package com.alanden.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alanden.entity.UserEntity;

/*
 * 用於存取「使用者」
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	@Query("SELECT u FROM UserEntity u WHERE u.username = :username")
    Optional<UserEntity> findByUsername(@Param("username") String username);
	
	@Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.authorities WHERE u.username Like %:username%")
	List<UserEntity> findByUsernameWithAuthorities(@Param("username") String username);
}