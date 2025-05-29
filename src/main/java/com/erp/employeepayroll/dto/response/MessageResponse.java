package com.erp.employeepayroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for responding with Message details.
 * Messages are typically generated internally, so no request DTO is needed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeFirstName;
    private String employeeLastName;
    private String messageContent;
    private Integer month;
    private Integer year;
    private LocalDate sentDate;
}
