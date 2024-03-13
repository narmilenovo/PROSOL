package com.example.dynamic.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.dynamic.configuration.FileStorageProperties;
import com.example.dynamic.exceptions.FileStorageException;
import com.example.dynamic.exceptions.MyFileNotFoundException;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	public FileStorageService(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public String storeFile(MultipartFile file, String formName, Long id) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// 2. File Size Limit
			long fileSizeLimit = 10L * 1024 * 1024; // 10MB
			if (file.getSize() > fileSizeLimit) {
				throw new IllegalArgumentException("File size exceeds the limit of " + fileSizeLimit);
			}
			// Create a user's subdirectory under the target location
			Path formDirectory = this.fileStorageLocation.resolve(formName);
			Path idDirectory = formDirectory.resolve(id.toString());
			Files.createDirectories(idDirectory);

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = idDirectory.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Resource loadFileAsResource(String formName, Long id, String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(formName).resolve(id.toString()).resolve(fileName)
					.normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	public byte[] createZipArchive(List<Resource> resources) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos)) {

			for (Resource resource : resources) {
				String fileName = resource.getFilename();
				zos.putNextEntry(new ZipEntry(fileName));
				IOUtils.copy(resource.getInputStream(), zos);
				zos.closeEntry();
			}

			zos.finish();
			return baos.toByteArray();
		} catch (IOException e) {
			throw new Exception("Error creating ZIP archive: " + e.getMessage());
		}
	}

	public boolean delete(String formName, Long id, String filename) throws Exception {
		try {
			Path userDirectory = this.fileStorageLocation.resolve(formName).resolve(id.toString()).normalize();
			Path file = userDirectory.resolve(filename).normalize();

			boolean deletedFile = Files.deleteIfExists(file);
			if (deletedFile && Files.exists(userDirectory) && Files.isDirectory(userDirectory)) {
				if (Files.list(userDirectory).count() == 0) {
					Files.delete(userDirectory);
				}
			}

			return deletedFile;
		} catch (IOException e) {
			throw new Exception("Error: " + e.getMessage());
		}
	}
}
