package com.outlier.mapper;

import com.outlier.dto.MachineDTO;
import com.outlier.model.Machine;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Bartosz Wieloch on 13/07/2024.
 * All copyrights reserved to Bartosz Wieloch.
 */

@Component
public class MachineMapper {

    @Autowired
    ModelMapper modelMapper;

    public MachineDTO mapToDTO(Machine machine) {
        return MachineDTO.builder()
                .id(machine.getId())
                .age(machine.getAge())
                .build();
    }

    public Machine mapToEntity(MachineDTO machineDTO) {
        return modelMapper.map(machineDTO, Machine.class);
    }
}
