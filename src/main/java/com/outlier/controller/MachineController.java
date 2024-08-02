package com.outlier.controller;

import com.outlier.dto.MachineDTO;
import com.outlier.dto.ResponseDTO;
import com.outlier.service.MachineService;
import com.outlier.validation.ValidationUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * Created by Bartosz Wieloch on 13/07/2024.
 * All copyrights reserved to Bartosz Wieloch.
 */

@RestController
@RequestMapping("${api.base.url}")
@Validated
public class MachineController {

    @Autowired
    private MachineService machineService;

    /**
     * Endpoint to detect outliers in machine ages.
     *
     * @param machines  List of machines with their ages to be validated.
     * @param threshold The threshold for Z-score to determine outliers, optional (default 2).
     * @return A ResponseDTO containing a message and a list of detected outliers.
     */
    @PostMapping("/detect-outliers")
    public ResponseDTO<List<MachineDTO>> detectOutliers(@Valid @RequestBody List<MachineDTO> machines,
                                                        @RequestParam(required = false) Double threshold) {

        ValidationUtil.validateThreshold(threshold);

        List<MachineDTO> validatedMachines = machineService.validateAges(machines, threshold);
        String message = validatedMachines.isEmpty() ? "No outliers detected" : "Outliers detected successfully";

        return new ResponseDTO<>(message, validatedMachines);
    }
}
