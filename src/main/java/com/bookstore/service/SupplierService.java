package com.bookstore.service;

import com.bookstore.dto.request.CreateSupplierRequest;
import com.bookstore.dto.response.SupplierResponse;
import com.bookstore.entity.Supplier;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.ReceiptRepository;
import com.bookstore.repository.SupplierRepository;
import com.bookstore.service.helper.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final EntityMapper mapper;
    private final ReceiptRepository receiptRepository;

    // Helper map dữ liệu kèm thống kê
    private SupplierResponse toResponseWithStats(Supplier supplier) {
        SupplierResponse response = mapper.toSupplierResponse(supplier);
        // Gọi SQL Count và Sum cực nhanh
        response.setReceiptCount(receiptRepository.countBySupplierId(supplier.getId()));
        response.setTotalImportAmount(receiptRepository.sumTotalImportBySupplierId(supplier.getId()));
        return response;
    }

    @Transactional
    public SupplierResponse createSupplier(CreateSupplierRequest request) {
        log.info("Creating supplier: {}", request.getName());

        if (supplierRepository.existsByName(request.getName())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                    "Nhà cung cấp đã tồn tại: " + request.getName());
        }

        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setDescription(request.getDescription());
        supplier.setIsActive(true);

        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Supplier created successfully with ID: {}", savedSupplier.getId());

        return mapper.toSupplierResponse(savedSupplier);
    }

    // --- SỬA LOGIC NÀY: Trả về Page ---
    @Transactional(readOnly = true)
    public Page<SupplierResponse> getAllSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return supplierRepository.findAll(pageable)
                .map(this::toResponseWithStats); // Map kèm thống kê
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> getActiveSuppliers() {
        List<Supplier> suppliers = supplierRepository.findByIsActiveTrueOrderByName();
        return suppliers.stream()
                .map(mapper::toSupplierResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy nhà cung cấp với ID: " + id));
        return mapper.toSupplierResponse(supplier);
    }

    @Transactional
    public SupplierResponse updateSupplier(Long id, CreateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy nhà cung cấp với ID: " + id));

        if (!supplier.getName().equals(request.getName()) &&
                supplierRepository.existsByName(request.getName())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                    "Nhà cung cấp đã tồn tại: " + request.getName());
        }

        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setDescription(request.getDescription());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return mapper.toSupplierResponse(updatedSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy nhà cung cấp với ID: " + id));
        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }
}