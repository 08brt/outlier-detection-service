package com.outlier.validation;

/**
 * Created by Bartosz Wieloch on 13/07/2024.
 * All copyrights reserved to Bartosz Wieloch.
 */

public class ValidationUtil {
    public static void validateThreshold(Double threshold) {
        if (threshold != null && (threshold < 1 || threshold > 10)) {
            throw new IllegalArgumentException("Threshold must be between 1 and 10");
        }
    }
}
