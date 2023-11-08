package com.projectpal.entity;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class TaskAttachment implements Serializable {

	public TaskAttachment(String fileName, long fileSize, String mimeType) {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.mimeType = mimeType;
	}

	@Transient
	private static final long serialVersionUID = 7L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NotBlank
	@Column(columnDefinition = "VARCHAR(500)", nullable = false)
	private String fileName;

	@Column(nullable = false)
	private long fileSize;

	@Column(columnDefinition = "VARCHAR(100)", nullable = false)
	private String mimeType;

	@Temporal(TemporalType.DATE)
	@CreatedDate
	@Setter(AccessLevel.NONE)
	@Column(nullable = false)
	private LocalDate creationDate;

	@ManyToOne
	@JsonIgnore
	private Task task;

}