package com.workzen.controller;

import com.workzen.dto.ApiResponse;
import com.workzen.dto.PerformanceReviewDTO;
import com.workzen.service.PerformanceReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance-reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PerformanceReviewController {

  private final PerformanceReviewService performanceReviewService;

  @PostMapping
  public ResponseEntity<ApiResponse<PerformanceReviewDTO>> createPerformanceReview(
      @Valid @RequestBody PerformanceReviewDTO dto) {
    try {
      PerformanceReviewDTO review = performanceReviewService.createPerformanceReview(dto);
      return ResponseEntity.ok(ApiResponse.success("Performance review created successfully", review));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PerformanceReviewDTO>> getPerformanceReviewById(@PathVariable Long id) {
    try {
      PerformanceReviewDTO review = performanceReviewService.getPerformanceReviewById(id);
      return ResponseEntity.ok(ApiResponse.success(review));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}")
  public ResponseEntity<ApiResponse<Page<PerformanceReviewDTO>>> getPerformanceReviewsByEmployee(
      @PathVariable Long employeeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<PerformanceReviewDTO> reviews = performanceReviewService.getPerformanceReviewsByEmployee(
          employeeId, pageable);
      return ResponseEntity.ok(ApiResponse.success(reviews));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/reviewer/{reviewerId}")
  public ResponseEntity<ApiResponse<Page<PerformanceReviewDTO>>> getPerformanceReviewsByReviewer(
      @PathVariable Long reviewerId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<PerformanceReviewDTO> reviews = performanceReviewService.getPerformanceReviewsByReviewer(
          reviewerId, pageable);
      return ResponseEntity.ok(ApiResponse.success(reviews));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/reviewer/{reviewerId}/pending")
  public ResponseEntity<ApiResponse<List<PerformanceReviewDTO>>> getPendingReviews(
      @PathVariable Long reviewerId) {
    try {
      List<PerformanceReviewDTO> reviews = performanceReviewService.getPendingReviews(reviewerId);
      return ResponseEntity.ok(ApiResponse.success(reviews));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/{id}/submit")
  public ResponseEntity<ApiResponse<PerformanceReviewDTO>> submitReview(@PathVariable Long id) {
    try {
      PerformanceReviewDTO review = performanceReviewService.submitReview(id);
      return ResponseEntity.ok(ApiResponse.success("Review submitted successfully", review));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<PerformanceReviewDTO>> completeReview(@PathVariable Long id) {
    try {
      PerformanceReviewDTO review = performanceReviewService.completeReview(id);
      return ResponseEntity.ok(ApiResponse.success("Review completed successfully", review));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/{id}/acknowledge")
  public ResponseEntity<ApiResponse<PerformanceReviewDTO>> acknowledgeReview(
      @PathVariable Long id,
      @RequestParam(required = false) String employeeComments) {
    try {
      PerformanceReviewDTO review = performanceReviewService.acknowledgeReview(id, employeeComments);
      return ResponseEntity.ok(ApiResponse.success("Review acknowledged successfully", review));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<PerformanceReviewDTO>> updateReview(
      @PathVariable Long id,
      @Valid @RequestBody PerformanceReviewDTO dto) {
    try {
      PerformanceReviewDTO review = performanceReviewService.updateReview(id, dto);
      return ResponseEntity.ok(ApiResponse.success("Review updated successfully", review));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}/average-rating")
  public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long employeeId) {
    try {
      Double avgRating = performanceReviewService.getAverageRating(employeeId);
      return ResponseEntity.ok(ApiResponse.success(avgRating));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}
