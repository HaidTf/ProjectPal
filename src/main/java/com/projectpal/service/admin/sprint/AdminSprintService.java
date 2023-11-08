package com.projectpal.service.admin.sprint;

import com.projectpal.entity.Sprint;

public interface AdminSprintService {

	Sprint findSprintById(long sprintId);

	void deleteSprint(long sprintId);

}
