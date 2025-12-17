package com.bookstore.controller;

import com.bookstore.dto.request.TopicRequest;
import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.response.TopicResponse;
import com.bookstore.entity.Topic;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicRepository topicRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getAllTopics() {
        List<TopicResponse> topics = topicRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Danh sách topics", topics));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TopicResponse>>> searchTopics(@RequestParam String keyword) {
        List<TopicResponse> topics = topicRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Kết quả tìm kiếm", topics));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TopicResponse>> createTopic(@RequestBody TopicRequest request) {
        if (topicRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Topic đã tồn tại");
        }
        Topic topic = new Topic();
        topic.setName(request.getName().trim());
        topic.setDescription(request.getDescription());

        return new ResponseEntity<>(new ApiResponse<>(true, "Tạo thành công", toResponse(topicRepository.save(topic))), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TopicResponse>> updateTopic(@PathVariable Long id, @RequestBody TopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!topic.getName().equalsIgnoreCase(request.getName().trim()) &&
                topicRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Tên topic đã trùng");
        }

        topic.setName(request.getName().trim());
        topic.setDescription(request.getDescription());

        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thành công", toResponse(topicRepository.save(topic))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTopic(@PathVariable Long id) {
        if (!topicRepository.existsById(id)) {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        topicRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa thành công", null));
    }

    private TopicResponse toResponse(Topic topic) {
        TopicResponse res = new TopicResponse();
        res.setId(topic.getId());
        res.setName(topic.getName());
        res.setDescription(topic.getDescription());
        return res;
    }
}
