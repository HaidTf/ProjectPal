package com.projectpal.service.admin.user;

import com.projectpal.entity.User;

public interface AdminUserService {

	User findUserById(long userId);

	void deleteUser(long userId);

}
