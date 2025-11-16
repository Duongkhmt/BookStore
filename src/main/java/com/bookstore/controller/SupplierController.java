package com.bookstore.controller;

import com.bookstore.dto.request.CreateSupplierRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.SupplierResponse;
import com.bookstore.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody CreateSupplierRequest request) {
        SupplierResponse response = supplierService.createSupplier(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Tạo nhà cung cấp thành công", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers() {
        List<SupplierResponse> responses = supplierService.getAllSuppliers();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy danh sách nhà cung cấp thành công", responses)
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getActiveSuppliers() {
        List<SupplierResponse> responses = supplierService.getActiveSuppliers();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy danh sách NCC đang hoạt động thành công", responses)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable Long id) {
        SupplierResponse response = supplierService.getSupplierById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy thông tin nhà cung cấp thành công", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody CreateSupplierRequest request) {
        SupplierResponse response = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cập nhật nhà cung cấp thành công", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Xóa nhà cung cấp thành công", null)
        );
    }
}
