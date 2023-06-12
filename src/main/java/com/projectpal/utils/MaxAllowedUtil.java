package com.projectpal.utils;

import com.projectpal.exception.ConflictException;

public class MaxAllowedUtil {

	public static void checkMaxAllowedOfEpic(int number) {
		if(number>20)
			throw new ConflictException("maximum number of epics reached");
	}
	
	public static void checkMaxAllowedOfSprint(int number) {
		if(number>20)
			throw new ConflictException("maximum number of sprints reached");
	}
	
	public static void checkMaxAllowedOfUserStory(int number) {
		if(number>25)
			throw new ConflictException("maximum number of userStories reached");
	}
	
	public static void checkMaxAllowedOfTask(int number) {
		if(number>25)
			throw new ConflictException("maximum number of tasks reached");
	}
	
}
