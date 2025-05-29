package com.erp.employeepayroll.controller;

import com.erp.employeepayroll.dto.request.DeductionRequest;
import com.erp.employeepayroll.dto.response.DeductionResponse;
import com.erp.employeepayroll.model.Deduction;
import com.erp.employeepayroll.service.DeductionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deductions")
@RequiredArgsConstructor
@Tag(name = "Deduction Management", description = "APIs for managing deductions")
public class DeductionController {

    private final DeductionService deductionService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<DeductionResponse> createDeduction(@Valid @RequestBody DeductionRequest deduction) {
        return ResponseEntity.ok(deductionService.createDeduction(deduction));
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<DeductionResponse> updateDeduction(@PathVariable String code,
                                                     @Valid @RequestBody DeductionRequest deduction) {
        deduction.setCode(code);
        return ResponseEntity.ok(deductionService.updateDeduction(code, deduction));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<DeductionResponse>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<DeductionResponse> getDeductionById(@PathVariable Long id) {
        return ResponseEntity.ok(deductionService.getDeductionById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteDeduction(@PathVariable Long id) {
        deductionService.deleteDeduction(id);
        return ResponseEntity.noContent().build();
    }
}