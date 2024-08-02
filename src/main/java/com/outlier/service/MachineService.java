package com.outlier.service;

import com.outlier.dto.MachineDTO;
import com.outlier.mapper.MachineMapper;
import com.outlier.model.Machine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Bartosz Wieloch on 13/07/2024.
 * All copyrights reserved to Bartosz Wieloch.
 */

@Service
@Slf4j
public class MachineService {

    @Autowired
    private MachineMapper mapper;

    private static final double DEFAULT_THRESHOLD = 2.0;

    /**
     * Validates the machine ages for any outliers
     * @param machineDTOs Machine DTO passed from the API containing ID and AGE
     * @param threshold Customisable limit for the Z-Score calculation (default 2)
     * @return MachineDTOs with outliers age
     */
    public List<MachineDTO> validateAges(List<MachineDTO> machineDTOs, Double threshold) {
        List<Machine> machines = machineDTOs.stream()
                .map(mapper::mapToEntity)
                .toList();

        double finalThreshold = threshold != null ? threshold : DEFAULT_THRESHOLD;

        List<Machine> outliers = findOutliers(machines, finalThreshold);

        return outliers.stream()
                .map(mapper::mapToDTO)
                .toList();
    }

    /**
     * Returns outliers using Z-Score which is a 'Outlier Detection Techniques'
     * Calculates the mean and standard deviation
     * Then identifies outliers via Z-Score
     * @param machines MachineDTO list converted into Machine entity list
     * @param threshold Predefined limit for the Z-score calculation
     * @return MachineDTOs with anomaly age
     */
    private List<Machine> findOutliers(List<Machine> machines, double threshold) {
        // Convert machine ages to days and store them in a map
        Map<Machine, Double> machineToAgeInDays = machines.stream()
                .collect(Collectors.toMap(machine -> machine, machine -> convertAgeToDays(machine.getAge())));

        // Calculate mean and standard deviation
        DescriptiveStatistics stats = new DescriptiveStatistics();
        machineToAgeInDays.values().forEach(stats::addValue);
        double mean = stats.getMean();
        double standardDeviation = stats.getStandardDeviation();

        // Identify outliers using Z-Score method
        return machineToAgeInDays.entrySet().stream()
                .filter(entry -> {
                    double ageInDays = entry.getValue();
                    double zScore = (ageInDays - mean) / standardDeviation;
                    boolean isOutlier = Math.abs(zScore) > threshold;
                    log.info("Id: {} Age: {}, Z-Score: {}, Outlier: {}", entry.getKey().getId(), entry.getKey().getAge(), zScore, isOutlier);
                    return isOutlier;
                })
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Converts value of age (month / months & year / years) into value of days
     * @param ageString Age is stored as a string in the form of ‘<number> <time unit>’, e.g. ‘1 year’, ’16 months’.
     * @return Age converted into days
     */
    private double convertAgeToDays(String ageString) {
        String[] parts = ageString.split(" ");
        int number = Integer.parseInt(parts[0]);
        String unit = parts[1].toLowerCase();

        return switch (unit) {
            case "day", "days" -> number;
            case "week", "weeks" -> number * 7;
            case "month", "months" -> number * 30;
            case "year", "years" -> number * 365;
            default -> {
                log.error("Unknown time unit: {} value skipped", unit);
                yield 0;
            }
        };
    }
}
