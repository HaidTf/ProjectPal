package com.projectpal.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.InternalServerErrorException;
import com.projectpal.exception.ResourceNotFoundException;

//This class is not suitable for production and is implemented just for the sake of reducing the complexity resulted by the integration with third party storage services

@Service
public class FileStorageService {

	private String storageLocation;

	public String storeFile(MultipartFile file, long parentId) {

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			if (fileName.contains(".."))
				throw new BadRequestException("invalid characters found");

			String parentDirectoryPath = storageLocation + "/" + parentId;

			Files.createDirectories(Path.of(parentDirectoryPath));

			Path filePath = Path.of(parentDirectoryPath, fileName);

			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			return fileName;

		} catch (IOException ex) {
			throw new InternalServerErrorException("could not store file");
		}

	}

	public Resource loadFile(String fileName, long parentId) {

		try {
			String parentDirectoryPath = storageLocation + "/" + parentId;

			Path filePath = Path.of(parentDirectoryPath, fileName);

			Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists()) {
				return resource;
			} else {
				throw new ResourceNotFoundException("file not found");
			}
		} catch (MalformedURLException ex) {
			throw new BadRequestException("invalid file path");
		}
	}

	public void deleteFile(String fileName, long parentId) {

		try {
			String parentDirectoryPath = storageLocation + "/" + parentId;

			Path filePath = Path.of(parentDirectoryPath, fileName);

			Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists()) {
				Files.delete(filePath);
			} else {
				throw new ResourceNotFoundException("file not found");
			}
		} catch (MalformedURLException ex) {
			throw new BadRequestException("invalid file path");
		} catch (IOException ex) {
			throw new InternalServerErrorException("Error deleting file");
		}

	}

}
