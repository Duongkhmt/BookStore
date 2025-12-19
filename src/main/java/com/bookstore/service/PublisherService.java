package com.bookstore.service;

import com.bookstore.dto.request.CreatePublisherRequest;
import com.bookstore.dto.request.UpdatePublisherRequest;
import com.bookstore.dto.response.PublisherResponse;
import com.bookstore.entity.Publisher;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.PublisherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository; // Inject thêm

    public PublisherService(PublisherRepository publisherRepository, BookRepository bookRepository) {
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    private PublisherResponse toResponse(Publisher publisher) {
        PublisherResponse response = new PublisherResponse();
        response.setId(publisher.getId());
        response.setName(publisher.getName());
        response.setAddress(publisher.getAddress());
        response.setPhone(publisher.getPhone());
        response.setBookCount(bookRepository.countByPublisherId(publisher.getId()));
        return response;
    }

    @Transactional(readOnly = true)
    public Page<PublisherResponse> findAllPublishers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return publisherRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public PublisherResponse createPublisher(CreatePublisherRequest request) {
        if (publisherRepository.existsByName(request.getName())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA, "Nhà xuất bản đã tồn tại.");
        }

        Publisher publisher = new Publisher();
        publisher.setName(request.getName());
        publisher.setAddress(request.getAddress());
        publisher.setPhone(request.getPhone());

        return toResponse(publisherRepository.save(publisher));
    }

    public void deletePublisher(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy Nhà xuất bản ID: " + id);
        }
        // Thao tác xóa
        publisherRepository.deleteById(id);
    }
    @Transactional
    public PublisherResponse updatePublisher(Long id, UpdatePublisherRequest request) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy Nhà xuất bản ID: " + id));

        String newName = request.getName().trim();

        // Kiểm tra trùng tên (trừ chính publisher hiện tại)
        if (!publisher.getName().equalsIgnoreCase(newName) &&
                publisherRepository.existsByName(newName)) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                    "Tên nhà xuất bản đã tồn tại.");
        }

        publisher.setName(newName);
        if (request.getAddress() != null) {
            publisher.setAddress(request.getAddress().trim());
        }
        if (request.getPhone() != null) {
            publisher.setPhone(request.getPhone().trim());
        }

        return toResponse(publisherRepository.save(publisher));
    }
}