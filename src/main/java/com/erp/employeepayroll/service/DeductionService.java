package com.erp.employeepayroll.service;

import com.erp.employeepayroll.dto.request.DeductionRequest;
import com.erp.employeepayroll.dto.response.DeductionResponse;
import com.erp.employeepayroll.exception.ResourceNotFoundException;
import com.erp.employeepayroll.model.Deduction;
import com.erp.employeepayroll.repository.DeductionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing deduction types and their percentages.
 */
@Service
public class DeductionService {

    private final DeductionRepository deductionRepository;

    public DeductionService(DeductionRepository deductionRepository) {
        this.deductionRepository = deductionRepository;
    }

    /**
     * Creates a new deduction record.
     * @param request DeductionRequest DTO.
     * @return DeductionResponse DTO of the created deduction.
     * @throws RuntimeException if deduction name or code already exists.
     */
    @Transactional
    public DeductionResponse createDeduction(DeductionRequest request) {
        if (deductionRepository.existsByDeductionName(request.getDeductionName())) {
            throw new RuntimeException("Deduction with name '" + request.getDeductionName() + "' already exists.");
        }
        if (deductionRepository.existsByCode(request.getCode())) { // Assuming code is also unique
            throw new RuntimeException("Deduction with code '" + request.getCode() + "' already exists.");
        }

        Deduction deduction = new Deduction();
        deduction.setCode(request.getCode());
        deduction.setDeductionName(request.getDeductionName());
        deduction.setPercentage(request.getPercentage() / 100.0); // Store as decimal (e.g., 0.05 for 5%)

        Deduction savedDeduction = deductionRepository.save(deduction);
        return mapToResponse(savedDeduction);
    }

    /**
     * Retrieves a deduction by its ID.
     * @param id The ID of the deduction.
     * @return DeductionResponse DTO.
     * @throws ResourceNotFoundException if deduction is not found.
     */
    public DeductionResponse getDeductionById(Long id) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction" +id));
        return mapToResponse(deduction);
    }

    /**
     * Retrieves all deductions.
     * @return List of DeductionResponse DTOs.
     */
    public List<DeductionResponse> getAllDeductions() {
        List<Deduction> deductions = deductionRepository.findAll();
        return deductions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing deduction record.
     * @param code The Code of the deduction to update.
     * @param request DeductionRequest DTO with updated information.
     * @return DeductionResponse DTO of the updated deduction.
     * @throws ResourceNotFoundException if deduction is not found.
     * @throws RuntimeException if updated deduction name or code is taken by another deduction.
     */
    @Transactional
    public DeductionResponse updateDeduction(String code, DeductionRequest request) {
        Deduction deduction = deductionRepository.findByCode(code);

        // Check if name changed and if new name is taken by another deduction
        if (!deduction.getDeductionName().equalsIgnoreCase(request.getDeductionName()) &&
                deductionRepository.existsByDeductionName(request.getDeductionName())) {
            throw new RuntimeException("Deduction with name '" + request.getDeductionName() + "' already exists for another record.");
        }
        // Check if code changed and if new code is taken by another deduction
        if (!deduction.getCode().equalsIgnoreCase(request.getCode()) &&
                deductionRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Deduction with code '" + request.getCode() + "' already exists for another record.");
        }

        deduction.setCode(request.getCode());
        deduction.setDeductionName(request.getDeductionName());
        deduction.setPercentage(request.getPercentage() / 100.0); // Store as decimal

        Deduction updatedDeduction = deductionRepository.save(deduction);
        return mapToResponse(updatedDeduction);
    }

    /**
     * Deletes a deduction by its ID.
     * @param id The ID of the deduction to delete.
     * @throws ResourceNotFoundException if deduction is not found.
     */
    @Transactional
    public void deleteDeduction(Long id) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction"+id));
        deductionRepository.delete(deduction);
    }

    // Helper method to map Deduction entity to DeductionResponse DTO
    private DeductionResponse mapToResponse(Deduction deduction) {
        return DeductionResponse.builder()
                .id(deduction.getId())
                .code(deduction.getCode())
                .deductionName(deduction.getDeductionName())
                .percentage(deduction.getPercentage() * 100.0) // Return as percentage (e.g., 5.0 for 5%)
                .build();
    }
}