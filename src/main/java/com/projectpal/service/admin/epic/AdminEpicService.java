package com.projectpal.service.admin.epic;

import com.projectpal.entity.Epic;

public interface AdminEpicService {

	Epic findEpicById(long epicId);

	void deleteEpic(long epicId);

}
