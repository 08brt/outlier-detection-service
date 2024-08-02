package com.outlier;

import com.outlier.dto.MachineDTO;
import com.outlier.model.Machine;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Bartosz Wieloch on 13/07/2024.
 * All copyrights reserved to Bartosz Wieloch.
 */

@SpringBootTest
@AutoConfigureMockMvc
class MachineTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String API_PATH = "/api/v1/";

	private final List<String> outOfBoundList = new ArrayList<>();

	private final Random random = new Random();

	@Test
	@DisplayName("Test Invalid Request Body - Missing ID Field")
	void invalidRequestBody() throws Exception {
		List<MachineDTO> machineDTOs = new ArrayList<>();
		machineDTOs.add(MachineDTO.builder()
				.age("7 years")
				.build());

		mockMvc.perform(post(API_PATH + "machines/detect-outliers")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(machineDTOs)))
				.andExpect(status().isBadRequest())
				.andReturn();
	}

	@Test
	@DisplayName("Test Invalid Request Parameter - Threshold 0 (Validation is 1-10)")
	void invalidRequestParameter() throws Exception {
		List<MachineDTO> machineDTOs = new ArrayList<>();
		machineDTOs.add(MachineDTO.builder()
				.id(1L)
				.age("7 years")
				.build());

		mockMvc.perform(post(API_PATH + "machines/detect-outliers")
						.param("threshold", "0")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(machineDTOs)))
				.andExpect(status().isBadRequest())
				.andReturn();
	}

	@Test
	@DisplayName("Test Machine Ages - Detect Outliers")
	void testMachineAges() throws Exception {
        List<Machine> machines = generateTestData();
		// Convert to MachineDTO
		List<MachineDTO> machineDTOs = machines.stream()
				.map(machine -> MachineDTO.builder()
                        .id(machine.getId())
                        .age(machine.getAge())
                        .build())
				.toList();

		// Perform the API request
		MvcResult result = mockMvc.perform(post(API_PATH + "machines/detect-outliers")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(machineDTOs)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())))
				.andReturn();

		// Extract response content
		JsonNode responseJson = objectMapper.readTree(result.getResponse().getContentAsString());
		JsonNode dataNode = responseJson.get("data");

		// Convert data node to List<MachineDTO>
		List<MachineDTO> responseDTOs = objectMapper.readValue(dataNode.toString(), new TypeReference<>() {});
		List<String> responseAges = responseDTOs.stream()
				.map(MachineDTO::getAge)
				.toList();

		assertTrue(responseAges.containsAll(outOfBoundList));
	}

	/**
	 * Generates a list of test data for machines with random ages
	 * The number of machines generated is a random number
	 *
	 * @return A list of `Machine` objects with randomly generated ages.
	 */
	private List<Machine> generateTestData() {
		List<Machine> machines = new ArrayList<>();
		int size = random.nextInt(1000);

		for (long i = 1; i <= size; i++) {
			String age = generateRandomAge(i, size);
			Machine machine = new Machine();
			machine.setId(i);
			machine.setAge(age);
			machines.add(machine);
		}

		return machines;
	}

	/**
	 * Generates a random age string for a machine
	 * This method generates a random age string, with a certain percentage (5%) of the records being out-of-bound
	 * Out-of-bound ages are randomly set between 90 and 99 years
	 * Normal ages are randomly set as days, months, or years
	 *
	 * @param id ID of the current machine
	 * @param numMachines The total number of machines.
	 * @return A string representing the age of the machine.
	 */
	private String generateRandomAge(long id, int numMachines) {
		int numOutOfBound = (int) Math.ceil(numMachines * 0.05);

		// Generate out of bound records
		if (id <= numOutOfBound) {
			String val = (90 + random.nextInt(10)) + " years";
			outOfBoundList.add(val);
			return val;
		}

		// Generate normal ages
		return switch (random.nextInt(3)) {
			case 0 -> (1 + random.nextInt(30)) + " days";
			case 1 -> (1 + random.nextInt(11)) + " months";
			default -> (1 + random.nextInt(10)) + " years";
		};
	}
}