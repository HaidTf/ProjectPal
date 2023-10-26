package com.projectpal.service.epic;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;

public interface EpicService {

	public Epic findEpicById(long epicId);

	public Epic findEpicByIdAndProject(long epicId, Project project);

	public List<Epic> findEpicsByProjectAndProgressFromDbOrCache(Project project, Set<Progress> progress, Sort sort);

	public List<Epic> findEpicsByProjectAndProgressFromDb(Project project, Set<Progress> progress, Sort sort);

	public void createEpic(Project project, Epic epic);

	public void updateDescription(long epicId, String description);

	public void updatePriority(long epicId, int priority);

	public void updateProgress(long epicId, Progress progress);

	public void deleteEpic(long epicId);

	public void sort(List<Epic> epics, Sort sort);

}
