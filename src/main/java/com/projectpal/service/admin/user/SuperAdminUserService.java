package com.projectpal.service.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.projectpal.entity.User;

public interface SuperAdminUserService {

	Page<User> findAllAdmins(Pageable pageable);

	void promoteUserToAdmin(Long id);

	void demoteAdmin(Long adminId);

}
