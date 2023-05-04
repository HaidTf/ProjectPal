package com.projectpal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.repository.InvitationRepository;

@RestController
@RequestMapping("/invitation")
public class InvitationController {

	@Autowired
	public InvitationController(InvitationRepository invitationRepo) {
		this.invitationRepo = invitationRepo;
	}

	private final InvitationRepository invitationRepo;
}
