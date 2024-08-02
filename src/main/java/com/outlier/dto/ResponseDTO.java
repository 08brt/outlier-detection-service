package com.outlier.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bartosz Wieloch on 13/07/2024.
 * All copyrights reserved to Bartosz Wieloch.
 */

@Getter
@Setter
public class ResponseDTO<T> {
    private String message;
    private T data;

    public ResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }
}