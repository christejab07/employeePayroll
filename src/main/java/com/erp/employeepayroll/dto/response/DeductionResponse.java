package com.erp.employeepayroll.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for responding with Deduction details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductionResponse {
    private Long id;
    private String code;
    private String deductionName;
    private Double percentage;
}