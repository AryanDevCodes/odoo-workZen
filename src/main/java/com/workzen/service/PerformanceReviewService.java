package com.workzen.service;

import com.workzen.dto.PerformanceReviewDTO;
import com.workzen.entity.Employee;
import com.workzen.entity.PerformanceReview;
import com.workzen.entity.PerformanceReview.ReviewStatus;
import com.workzen.repository.EmployeeRepository;
import com.workzen.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceReviewService {

  private final PerformanceReviewRepository performanceReviewRepository;
  private final EmployeeRepository employeeRepository;

  @Transactional
  public PerformanceReviewDTO createPerformanceReview(PerformanceReviewDTO dto) {
    Employee employee = employeeRepository.findById(dto.getEmployeeId())
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

    Employee reviewer = employeeRepository.findById(dto.getReviewerId())
        .orElseThrow(() -> new RuntimeException("Reviewer not found with id: " + dto.getReviewerId()));

    // Validate review period
    if (dto.getReviewPeriodStart().isAfter(dto.getReviewPeriodEnd())) {
      throw new RuntimeException("Review period start date cannot be after end date");
    }

    // Calculate overall rating if not provided
    Double overallRating = dto.getOverallRating();
    if (overallRating == null && dto.getTechnicalSkillsRating() != null) {
      overallRating = calculateOverallRating(dto);
    }

    PerformanceReview review = PerformanceReview.builder()
        .employee(employee)
        .reviewer(reviewer)
        .reviewPeriodStart(dto.getReviewPeriodStart())
        .reviewPeriodEnd(dto.getReviewPeriodEnd())
        .overallRating(overallRating)
        .technicalSkillsRating(dto.getTechnicalSkillsRating())
        .communicationRating(dto.getCommunicationRating())
        .teamworkRating(dto.getTeamworkRating())
        .leadershipRating(dto.getLeadershipRating())
        .punctualityRating(dto.getPunctualityRating())
        .strengths(dto.getStrengths())
        .areasForImprovement(dto.getAreasForImprovement())
        .goals(dto.getGoals())
        .reviewerComments(dto.getReviewerComments())
        .employeeComments(dto.getEmployeeComments())
        .status(ReviewStatus.DRAFT)
        .build();

    PerformanceReview saved = performanceReviewRepository.save(review);
    return convertToDTO(saved);
  }

  @Transactional(readOnly = true)
  public PerformanceReviewDTO getPerformanceReviewById(Long id) {
    PerformanceReview review = performanceReviewRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Performance review not found with id: " + id));
    return convertToDTO(review);
  }

  @Transactional(readOnly = true)
  public Page<PerformanceReviewDTO> getPerformanceReviewsByEmployee(Long employeeId, Pageable pageable) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return performanceReviewRepository.findByEmployee(employee, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public Page<PerformanceReviewDTO> getPerformanceReviewsByReviewer(Long reviewerId, Pageable pageable) {
    Employee reviewer = employeeRepository.findById(reviewerId)
        .orElseThrow(() -> new RuntimeException("Reviewer not found with id: " + reviewerId));
    return performanceReviewRepository.findByReviewer(reviewer, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public List<PerformanceReviewDTO> getPendingReviews(Long reviewerId) {
    Employee reviewer = employeeRepository.findById(reviewerId)
        .orElseThrow(() -> new RuntimeException("Reviewer not found with id: " + reviewerId));
    return performanceReviewRepository.findPendingReviews(reviewer, ReviewStatus.DRAFT)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public PerformanceReviewDTO submitReview(Long id) {
    PerformanceReview review = performanceReviewRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Performance review not found with id: " + id));

    if (review.getStatus() != ReviewStatus.DRAFT) {
      throw new RuntimeException("Review is not in draft status");
    }

    review.setStatus(ReviewStatus.SUBMITTED);
    review.setReviewDate(LocalDate.now());

    PerformanceReview saved = performanceReviewRepository.save(review);
    return convertToDTO(saved);
  }

  @Transactional
  public PerformanceReviewDTO completeReview(Long id) {
    PerformanceReview review = performanceReviewRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Performance review not found with id: " + id));

    if (review.getStatus() != ReviewStatus.SUBMITTED) {
      throw new RuntimeException("Review must be submitted before completion");
    }

    review.setStatus(ReviewStatus.COMPLETED);

    PerformanceReview saved = performanceReviewRepository.save(review);
    return convertToDTO(saved);
  }

  @Transactional
  public PerformanceReviewDTO acknowledgeReview(Long id, String employeeComments) {
    PerformanceReview review = performanceReviewRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Performance review not found with id: " + id));

    if (review.getStatus() != ReviewStatus.COMPLETED) {
      throw new RuntimeException("Review must be completed before acknowledgment");
    }

    review.setStatus(ReviewStatus.ACKNOWLEDGED);
    review.setEmployeeComments(employeeComments);

    PerformanceReview saved = performanceReviewRepository.save(review);
    return convertToDTO(saved);
  }

  @Transactional
  public PerformanceReviewDTO updateReview(Long id, PerformanceReviewDTO dto) {
    PerformanceReview review = performanceReviewRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Performance review not found with id: " + id));

    if (review.getStatus() != ReviewStatus.DRAFT) {
      throw new RuntimeException("Can only update draft reviews");
    }

    if (dto.getTechnicalSkillsRating() != null) {
      review.setTechnicalSkillsRating(dto.getTechnicalSkillsRating());
    }
    if (dto.getCommunicationRating() != null) {
      review.setCommunicationRating(dto.getCommunicationRating());
    }
    if (dto.getTeamworkRating() != null) {
      review.setTeamworkRating(dto.getTeamworkRating());
    }
    if (dto.getLeadershipRating() != null) {
      review.setLeadershipRating(dto.getLeadershipRating());
    }
    if (dto.getPunctualityRating() != null) {
      review.setPunctualityRating(dto.getPunctualityRating());
    }
    if (dto.getStrengths() != null) {
      review.setStrengths(dto.getStrengths());
    }
    if (dto.getAreasForImprovement() != null) {
      review.setAreasForImprovement(dto.getAreasForImprovement());
    }
    if (dto.getGoals() != null) {
      review.setGoals(dto.getGoals());
    }
    if (dto.getReviewerComments() != null) {
      review.setReviewerComments(dto.getReviewerComments());
    }

    // Recalculate overall rating
    review.setOverallRating(calculateOverallRating(dto));

    PerformanceReview saved = performanceReviewRepository.save(review);
    return convertToDTO(saved);
  }

  @Transactional(readOnly = true)
  public Double getAverageRating(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    Double avgRating = performanceReviewRepository.getAverageRating(employee);
    return avgRating != null ? avgRating : 0.0;
  }

  private Double calculateOverallRating(PerformanceReviewDTO dto) {
    int count = 0;
    double sum = 0.0;

    if (dto.getTechnicalSkillsRating() != null) {
      sum += dto.getTechnicalSkillsRating();
      count++;
    }
    if (dto.getCommunicationRating() != null) {
      sum += dto.getCommunicationRating();
      count++;
    }
    if (dto.getTeamworkRating() != null) {
      sum += dto.getTeamworkRating();
      count++;
    }
    if (dto.getLeadershipRating() != null) {
      sum += dto.getLeadershipRating();
      count++;
    }
    if (dto.getPunctualityRating() != null) {
      sum += dto.getPunctualityRating();
      count++;
    }

    return count > 0 ? sum / count : null;
  }

  private PerformanceReviewDTO convertToDTO(PerformanceReview review) {
    return PerformanceReviewDTO.builder()
        .id(review.getId())
        .employeeId(review.getEmployee().getId())
        .employeeName(review.getEmployee().getFullName())
        .employeeIdString(review.getEmployee().getEmployeeId())
        .reviewerId(review.getReviewer().getId())
        .reviewerName(review.getReviewer().getFullName())
        .reviewPeriodStart(review.getReviewPeriodStart())
        .reviewPeriodEnd(review.getReviewPeriodEnd())
        .overallRating(review.getOverallRating())
        .technicalSkillsRating(review.getTechnicalSkillsRating())
        .communicationRating(review.getCommunicationRating())
        .teamworkRating(review.getTeamworkRating())
        .leadershipRating(review.getLeadershipRating())
        .punctualityRating(review.getPunctualityRating())
        .strengths(review.getStrengths())
        .areasForImprovement(review.getAreasForImprovement())
        .goals(review.getGoals())
        .reviewerComments(review.getReviewerComments())
        .employeeComments(review.getEmployeeComments())
        .status(review.getStatus())
        .reviewDate(review.getReviewDate())
        .build();
  }
}
