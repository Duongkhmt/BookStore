package com.bookstore.controller;

import com.bookstore.dto.request.TagRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.TagResponse;
import com.bookstore.entity.Tag;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags() {
        List<TagResponse> tags = tagRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Danh sách tags", tags));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TagResponse>>> searchTags(@RequestParam String keyword) {
        // Lưu ý: TagRepository cần có hàm findByNameContainingIgnoreCase
        List<TagResponse> tags = tagRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Kết quả tìm kiếm", tags));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> createTag(@RequestBody TagRequest request) {
        if (tagRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Tag đã tồn tại");
        }
        Tag tag = new Tag();
        tag.setName(request.getName().trim());
        tag.setTagType(request.getTagType());
        tag.setDescription(request.getDescription());

        return new ResponseEntity<>(new ApiResponse<>(true, "Tạo thành công", toResponse(tagRepository.save(tag))), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> updateTag(@PathVariable Long id, @RequestBody TagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Tag không tồn tại"));

        if (!tag.getName().equalsIgnoreCase(request.getName().trim()) &&
                tagRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Tên tag đã trùng");
        }

        tag.setName(request.getName().trim());
        tag.setTagType(request.getTagType());
        tag.setDescription(request.getDescription());

        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thành công", toResponse(tagRepository.save(tag))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTag(@PathVariable Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Tag không tồn tại");
        }
        tagRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa thành công", null));
    }

    private TagResponse toResponse(Tag tag) {
        TagResponse res = new TagResponse();
        res.setId(tag.getId());
        res.setName(tag.getName());
        res.setTagType(tag.getTagType());
        res.setDescription(tag.getDescription());
        return res;
    }
}