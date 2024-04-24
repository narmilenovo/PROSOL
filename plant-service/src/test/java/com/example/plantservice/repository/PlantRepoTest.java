package com.example.plantservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.plantservice.PlantServiceApplicationTest;
import com.example.plantservice.entity.Plant;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(PlantServiceApplicationTest.class)
class PlantRepoTest {

	@Autowired
	PlantRepo plantRepository;

	String plantName1;
	String plantCode1;
	Plant plant1;

	String plantName2;
	String plantCode2;
	Plant plant2;

	@BeforeEach
	void setUp() {
		plantCode1 = "001";
		plantName1 = "codasol";
		plant1 = new Plant(null, plantCode1, plantName1, true, new HashMap<>());

		plantCode2 = "002";
		plantName2 = "muscat";
		Map<String, Object> plantData = new HashMap<String, Object>();
		plantData.put("description", "this is dynamic field");
		plant2 = new Plant(null, plantCode2, plantName2, true, plantData);
	}

	@Test
	void testFindByPlantName() {

		Plant savedPlant = plantRepository.save(plant1);

		Plant foundPlant = plantRepository.findByPlantName(savedPlant.getPlantName()).get();

		assertThat(foundPlant).isEqualTo(savedPlant);
	}

	@Test
	void testFindByPlantNameNotFound() {

		Plant foundPlant = plantRepository.findByPlantName(plantName1).orElse(null);
		assertThat(foundPlant).isNull();
	}

	@Test
	void testFindById() {

		Plant savedPlant = plantRepository.save(plant1);

		Plant foundPlant = plantRepository.findById(savedPlant.getId()).get();

		assertEquals(savedPlant, foundPlant);

	}

	@Test
	void testFindByIdNotFound() {

		Long id = 1L;

		Plant foundPlant = plantRepository.findById(id).orElse(null);

		assertThat(foundPlant).isNull();

	}

	@Test
	void testExistsByPlantCodeAndPlantName() {

		plantRepository.save(plant1);

		boolean exists = plantRepository.existsByPlantCodeAndPlantName(plantCode1, plantName1);

		assertThat(exists).isTrue();
	}

	@Test
	void testDoesNotExistsByPlantCodeAndPlantName() {

		boolean exists = plantRepository.existsByPlantCodeAndPlantName(plantCode1, plantName1);

		assertThat(exists).isFalse();
	}

	@Test
	void testExistsByPlantCodeAndPlantNameAndIdNot() {
		// Given
		Long id = 2L;
		plant1 = plantRepository.save(plant1);
		// When
		boolean exists = plantRepository.existsByPlantCodeAndPlantNameAndIdNot(plantCode1, plantName1, id);

		// Then
		assertThat(exists).isTrue();

	}

	@Test
	void testFindAllByOrderByIdAsc() {
		// Given
		plantRepository.saveAll(List.of(plant1, plant2));

		// When
		List<Plant> plants = plantRepository.findAllByOrderByIdAsc();

		// Then
		assertEquals(2, plants.size());
		assertEquals(plant1.getId(), plants.get(0).getId());
		assertEquals(plant2.getId(), plants.get(1).getId());

	}

}
