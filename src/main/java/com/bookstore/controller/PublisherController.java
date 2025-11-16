package com.bookstore.controller;

import com.bookstore.dto.request.CreatePublisherRequest;
import com.bookstore.dto.request.UpdatePublisherRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.PublisherResponse;
import com.bookstore.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PublisherResponse>>> getAllPublishers() {
        List<PublisherResponse> publishers = publisherService.findAllPublishers();
        ApiResponse<List<PublisherResponse>> response = new ApiResponse<>(true, "Lấy danh sách nhà xuất bản thành công", publishers);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<PublisherResponse>> createPublisher(@Valid @RequestBody CreatePublisherRequest request) {
        PublisherResponse newPublisher = publisherService.createPublisher(request);
        ApiResponse<PublisherResponse> response = new ApiResponse<>(true, "Thêm nhà xuất bản thành công", newPublisher);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        ApiResponse<?> response = new ApiResponse<>(true, "Xóa nhà xuất bản ID: " + id + " thành công", null);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PublisherResponse>> updatePublisher(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePublisherRequest request) {
        PublisherResponse updatedPublisher = publisherService.updatePublisher(id, request);
        ApiResponse<PublisherResponse> response = new ApiResponse<>(
                true, "Cập nhật nhà xuất bản thành công", updatedPublisher);
        return ResponseEntity.ok(response);
    }
}
