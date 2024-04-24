package com.example.plantservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import com.example.plantservice.PlantServiceApplicationTest;
import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.mapping.PlantMapper;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.util.ExcelFileHelper;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
@Import(PlantServiceApplicationTest.class)
class PlantServiceImplTest {
	@Mock
	private PlantRepo plantRepository;
	@Mock
	private PlantMapper plantMapper;
	@Mock
	private ExcelFileHelper excelFileHelper;
	@Mock
	private DynamicClient dynamicClient;

	@InjectMocks
	PlantServiceImpl plantService;

	String plantName1;
	String plantCode1;
	PlantRequest plant1;

	String plantName2;
	String plantCode2;
	PlantRequest plant2;

	Map<String, Object> plantData = new HashMap<String, Object>();
	Plant plant;

	HttpServletResponse mockResponse = mock(HttpServletResponse.class);

	@BeforeEach
	void setUp() throws Exception {
		plantService = new PlantServiceImpl(plantRepository, plantMapper, excelFileHelper, dynamicClient);

		plantName1 = "codasol";
		plantCode1 = "001";
		plant1 = new PlantRequest(plantCode1, plantName1, true, new HashMap<>());

		plantName1 = "muscat";
		plantCode1 = "002";
		plantData.put("description", "this is dynamic field");
		plant2 = new PlantRequest(plantCode2, plantName2, true, plantData);

		plant = new Plant();

	}

	@Test
	void testSavePlantWithoutDynamicField()
			throws IllegalAccessException, AlreadyExistsException, ResourceNotFoundException {
		// Mocking behavior
		when(plantRepository.existsByPlantCodeAndPlantName(anyString(), anyString())).thenReturn(false);
		when(plantMapper.mapToPlant(any(PlantRequest.class))).thenReturn(plant);
		when(plantRepository.save(any(Plant.class))).thenReturn(plant);

		// Invoking the method
		plantService.savePlant(plant1);

		// Verifying interactions
		verify(plantRepository, times(1)).save(plant);

	}

	@Test
	void testSavePlantWithDynamicField()
			throws IllegalAccessException, AlreadyExistsException, ResourceNotFoundException {
		// Given
		when(plantRepository.existsByPlantCodeAndPlantName(plantCode2, plantName2)).thenReturn(false);
		when(plantMapper.mapToPlant(any(PlantRequest.class))).thenReturn(plant);
		plant.setDynamicFields(plantData);
		when(dynamicClient.checkFieldNameInForm(anyString(), anyString())).thenReturn(true); // Mocking the field
																								// existence
		// When
		plantService.savePlant(plant2);

		// Then
		verify(plantRepository).save(plant);
	}

	@Test
	void testGetPlantById() throws ResourceNotFoundException {
		// Given
		when(plantRepository.findById(anyLong())).thenReturn(Optional.of(plant)); // Mocking plant retrieval by ID
		// When
		plantService.getPlantById(anyLong());

		// Then
		verify(plantRepository).findById(anyLong());
	}

	@Test
	void testGetPlantById_ThrowsException() {
		// Given
		when(plantRepository.findById(anyLong())).thenReturn(Optional.empty()); // Mocking plant retrieval by ID to
																				// return
																				// empty optional

		assertThrows(ResourceNotFoundException.class, () -> plantService.getPlantById(anyLong()));
		verify(plantRepository).findById(anyLong());
	}

	@Test
	void testGetPlantByName() throws ResourceNotFoundException {
		when(plantRepository.findByPlantName(anyString())).thenReturn(Optional.of(plant));

		// When
		plantService.getPlantByName(anyString());

		// Then
		verify(plantRepository).findByPlantName(anyString());
	}

	@Test
	void testGetPlantByName_ThrowsException() {
		// Given
		when(plantRepository.findByPlantName(anyString())).thenReturn(Optional.empty());
		// empty optional

		assertThrows(ResourceNotFoundException.class, () -> plantService.getPlantByName(anyString()));
		verify(plantRepository).findByPlantName(anyString());
	}

	@Test
	void testGetAllPlants() {
		// Given
		plantService.getAllPlants();

		verify(plantRepository).findAllByOrderByIdAsc();

	}

	@Test
	void testUpdatePlant() throws ResourceNotFoundException, AlreadyExistsException {
		// Given
		Long plantId = 1L;
		Plant existingPlant = new Plant();
		existingPlant.setId(plantId);
		existingPlant.setPlantCode("001");
		existingPlant.setPlantName("Original Plant");
		existingPlant.setPlantStatus(false);
		existingPlant.setDynamicFields(new HashMap<>());

		when(plantRepository.findById(anyLong())).thenReturn(Optional.of(existingPlant));
		when(plantRepository.existsByPlantCodeAndPlantNameAndIdNot(anyString(), anyString(), anyLong()))
				.thenReturn(false);

		// When
		plantService.updatePlant(plantId, plant1);

		// Then
		verify(plantRepository).save(existingPlant);
	}

	@Test
	void testUpdateBulkStatusPlantId() throws ResourceNotFoundException {
		// Given
		List<Long> plantIds = List.of(1L, 2L, 3L); // Example list of plant IDs
		List<Plant> existingPlants = new ArrayList<>();
		for (Long id : plantIds) {
			Plant plant = new Plant();
			plant.setId(id);
			plant.setPlantStatus(false); // Assuming initial status is false
			existingPlants.add(plant);
		}

		when(plantRepository.findAllById(plantIds)).thenReturn(existingPlants); // Mocking plant retrieval by IDs

		// When
		plantService.updateBulkStatusPlantId(plantIds);

		// Then
		verify(plantRepository).saveAll(existingPlants); // Verify that saveAll method was called with correct plants
															// list size
	}

	@Test
	void testUpdateStatusUsingPlantId() throws ResourceNotFoundException {
		// Given
		Long plantId = 1L;
		Plant existingPlant = new Plant();
		existingPlant.setId(plantId);
		existingPlant.setPlantStatus(false); // Assuming initial status is false

		when(plantRepository.findById(anyLong())).thenReturn(Optional.of(existingPlant)); // Mocking plant retrieval by
																							// ID

		// When
		plantService.updateStatusUsingPlantId(anyLong());

		// Then
		verify(plantRepository).save(existingPlant); // Verify that save method was called with correct plant
		// Assuming plant status should be toggled
		assertThat(existingPlant.getPlantStatus()).isTrue();
	}

	@Test
	void testUpdateStatusUsingPlantId_NotFound() {
		when(plantRepository.findById(anyLong())).thenReturn(Optional.empty()); // Mocking plant retrieval by ID to
																				// return

		// empty optional
		assertThrows(ResourceNotFoundException.class, () -> plantService.updateStatusUsingPlantId(anyLong()));
		verify(plantRepository).findById(anyLong());
	}

	@Test
	void testDeletePlant() throws ResourceNotFoundException {
		// Given

		when(plantRepository.findById(anyLong())).thenReturn(Optional.of(plant));
		// When
		plantService.deletePlant(anyLong());
		// Then
		verify(plantRepository).delete(plant);

	}

	@Test
	void testDeleteBatchPlant() throws ResourceNotFoundException {
		// Given
		List<Long> plantIds = List.of(1L, 2L, 3L);
		List<Plant> existingPlants = new ArrayList<>();
		for (Long id : plantIds) {
			Plant plant = new Plant();
			plant.setId(id);
			existingPlants.add(plant);
		}
		when(plantRepository.findAllById(plantIds)).thenReturn(existingPlants);

		plantService.deleteBatchPlant(plantIds);

		verify(plantRepository).deleteAll(existingPlants);
	}

	@Test
	void testDownloadTemplate() throws IOException {

		// When
		plantService.downloadTemplate(mockResponse);

		// Then
		verify(excelFileHelper).exportTemplate((mockResponse), // Verify that the exportTemplate method is called with
																// the mockResponse
				("Plant"), // Verify that sheetName is "Plant"
				(PlantRequest.class), // Verify that the class is PlantRequest.class
				("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), // Verify the contentType
				(".xlsx"), // Verify the extension
				("Plant_") // Verify the prefix
		);
	}
}
