package com.outlier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bartosz Wieloch on 13/07/2024.
 * All copyrights reserved to Bartosz Wieloch.
 */

@Getter
@Setter
@Builder
public class MachineDTO {

    @NotNull(message = "ID must not be null")
    @Positive(message = "ID must be a positive number")
    private Long id;

    @NotBlank(message = "Age must not be blank")
    private String age;
}
