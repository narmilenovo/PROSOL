package com.example.batchservice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final JobLauncher jobLauncher;
	private final Job job;

	@PostMapping(value = "/uploadDataToUser", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	public String postMethodName(@RequestPart MultipartFile file) {
		String tempStorage = createDirectory();
		try {
			String originalFileName = file.getOriginalFilename();
			String filePath = tempStorage + originalFileName;
			System.out.println("File Path: " + filePath); // Debugging statement
			File fileToImport = new File(filePath);

			// Save the file
			file.transferTo(fileToImport);

			JobParameters jobParameters = new JobParametersBuilder().addString("fullPathFileName", filePath)
					.addLong("startAt", System.currentTimeMillis()).toJobParameters();

			// Run the job
			JobExecution execution = jobLauncher.run(job, jobParameters);

			if (execution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED)) {
				// delete the file from the TEMP_STORAGE
				Files.deleteIfExists(Paths.get(filePath));
			}
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException | IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String createDirectory() {
		// Get the application's context path
		String contextPath = System.getProperty("user.dir");

		// Specify the directory name
		String directoryName = "Files\\";

		// Create the directory path
		String directoryPath = contextPath + File.separator + directoryName;

		// Create the directory
		File directory = new File(directoryPath);

		// Check if the directory exists, if not, create it
		if (!directory.exists()) {
			boolean created = directory.mkdirs(); // Create the directory and any necessary parent directories
			if (created) {
				System.out.println("Directory created successfully: " + directory.getAbsolutePath());
			} else {
				System.err.println("Failed to create directory: " + directory.getAbsolutePath());
				// You can throw an exception here or handle the failure accordingly
			}
		} else {
			System.out.println("Directory already exists: " + directory.getAbsolutePath());
		}
		return directoryPath;
	}
}
