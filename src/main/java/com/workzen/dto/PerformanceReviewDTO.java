package com.workzen.dto;

import com.workzen.entity.PerformanceReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewDTO {
  private Long id;
  private Long employeeId;
  private String employeeName;
  private String employeeIdString;
  private Long reviewerId;
  private String reviewerName;
  private LocalDate reviewPeriodStart;
  private LocalDate reviewPeriodEnd;
  private Double overallRating;
  private Double technicalSkillsRating;
  private Double communicationRating;
  private Double teamworkRating;
  private Double leadershipRating;
  private Double punctualityRating;
  private String strengths;
  private String areasForImprovement;
  private String goals;
  private String reviewerComments;
  private String employeeComments;
  private PerformanceReview.ReviewStatus status;
  private LocalDate reviewDate;
}
