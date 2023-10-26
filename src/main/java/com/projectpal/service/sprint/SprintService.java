package com.projectpal.service.sprint;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.enums.Progress;

public interface SprintService {

	public Sprint findSprintById(long sprintId);

	public Sprint findSprintByIdAndproject(long sprintId, Project project);

	public List<Sprint> findSprintsByProjectAndProgressFromDbOrCache(Project project, Set<Progress> progress,
			Sort sort);

	public List<Sprint> findSprintsByProjectAndProgressFromDb(Project project, Set<Progress> progress, Sort sort);

	public void createSprint(Project project, Sprint sprint);

	public void updateStartDate(long sprintId, LocalDate date);

	public void updateEndDate(long sprintId, LocalDate date);

	public void updateDescription(long sprintId, String description);

	public void updateProgress(long sprintId, Progress progress);

	public void deleteSprint(long sprintId);

	public void sort(List<Sprint> sprints, Sort sort);

}
