package com.example.requestitemservice.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.requestitemservice.configuration.FileStorageProperties;
import com.example.requestitemservice.exceptions.FileStorageException;

@Service
public class FileUploadUtil {

	private final Path fileStorageLocation;

	public FileUploadUtil(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception e) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					e);
		}
	}

	public String storeFile(MultipartFile file, Long id) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			// Check if the file is empty
			if (file.isEmpty()) {
				throw new FileStorageException("Failed to store empty file " + fileName);
			}
			long fileSizeLimit = 10L * 1024 * 1024; // 10mb
			if (file.getSize() > fileSizeLimit) {
				throw new FileStorageException("File size exceeds the limit of " + fileSizeLimit);
			}

			Path idDirectory = this.fileStorageLocation.resolve(id.toString());
			Files.createDirectories(idDirectory);

			Path targetLocation = idDirectory.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException e) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
		}
	}

	public Resource loadFileAsResource(String fileName, Long id) {
		try {
			Path idDirectory = this.fileStorageLocation.resolve(id.toString()).normalize();
			Path filePath = idDirectory.resolve(fileName).normalize();

			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileStorageException("File not found " + fileName);
			}

		} catch (MalformedURLException e) {
			throw new FileStorageException("File not found " + fileName, e);
		}
	}

	public boolean deleteDir(String fileName, Long id) {
		try {
			Path idDirectory = this.fileStorageLocation.resolve(id.toString()).normalize();
			Path file = idDirectory.resolve(fileName).normalize();

			// Delete the file
			boolean deletedFile = Files.deleteIfExists(file);

			// Check if the directory exists and is empty, then delete it
			try (Stream<Path> directoryStream = Files.list(idDirectory)) {
				if (Files.isDirectory(idDirectory) && directoryStream.count() == 0) {
					Files.delete(idDirectory);
				}
			}

			return deletedFile;
		} catch (IOException e) {
			throw new FileStorageException("Error: " + e.getMessage());
		}
	}

	public boolean deleteFile(String fileName, Long id) {
		try {
			Path idDirectory = this.fileStorageLocation.resolve(id.toString()).normalize();
			Path file = idDirectory.resolve(fileName).normalize();

			// Check if the file exists before proceeding
			if (Files.exists(file)) {
				Files.delete(file);
				return true;
			} else {
				throw new FileStorageException("File not found: " + fileName);
			}

		} catch (IOException e) {
			throw new FileStorageException(
					"Error deleting file " + fileName + " from the Id directory: " + e.getMessage());
		}
	}

}
