package com.alanden.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.alanden.entity.AuthorityEntity;
import com.alanden.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

/*
 * 使用者
 */
@Getter
@Setter
public class UserDTO {
	private long id;
	private String username;	
	private List<String> authorities;

	// 從UserEntity轉換為UserDTO
	public static UserDTO fromEntity(UserEntity user) {
		UserDTO dto = new UserDTO();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setAuthorities(
				user.getAuthorities().stream()
				.map(AuthorityEntity::getAuthority)
				.collect(Collectors.toList()));
		return dto;
	}
}