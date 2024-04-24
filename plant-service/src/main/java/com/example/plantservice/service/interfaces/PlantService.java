package com.example.plantservice.service.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface PlantService {

	PlantResponse savePlant(@Valid PlantRequest plantRequest)
			throws ResourceNotFoundException, AlreadyExistsException, IllegalAccessException;

	List<PlantResponse> saveAllPlant(@Valid List<PlantRequest> plantRequests)
			throws AlreadyExistsException, ResourceNotFoundException;

	PlantResponse getPlantById(Long plantId) throws ResourceNotFoundException;

	PlantResponse getPlantByName(String name) throws ResourceNotFoundException;

	List<Plant> findAll();

	List<PlantResponse> getAllPlants();

	PlantResponse updatePlant(Long id, PlantRequest plantRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	PlantResponse updateStatusUsingPlantId(Long id) throws ResourceNotFoundException;

	List<PlantResponse> updateBulkStatusPlantId(List<Long> id) throws ResourceNotFoundException;

	void deletePlant(Long id) throws ResourceNotFoundException;

	void deleteBatchPlant(List<Long> ids) throws ResourceNotFoundException;

	List<Map<String, Object>> convertPlantListToMap(List<Plant> plant);

	void downloadTemplate(HttpServletResponse response) throws IOException;

	void importExcelSave(MultipartFile file) throws IOException, ExcelFileException, AlreadyExistsException;

	void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException;

}
