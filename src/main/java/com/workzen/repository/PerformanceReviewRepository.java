package com.workzen.repository;

import com.workzen.entity.Employee;
import com.workzen.entity.PerformanceReview;
import com.workzen.entity.PerformanceReview.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {

  List<PerformanceReview> findByEmployee(Employee employee);

  List<PerformanceReview> findByReviewer(Employee reviewer);

  List<PerformanceReview> findByEmployeeAndStatus(Employee employee, ReviewStatus status);

  @Query("SELECT pr FROM PerformanceReview pr WHERE pr.employee = :employee AND " +
      "pr.reviewPeriodStart <= :date AND pr.reviewPeriodEnd >= :date")
  List<PerformanceReview> findActiveReviews(@Param("employee") Employee employee,
      @Param("date") LocalDate date);

  @Query("SELECT pr FROM PerformanceReview pr WHERE pr.reviewer = :reviewer AND " +
      "pr.status = :status ORDER BY pr.reviewPeriodEnd DESC")
  List<PerformanceReview> findPendingReviews(@Param("reviewer") Employee reviewer,
      @Param("status") ReviewStatus status);

  Page<PerformanceReview> findByEmployee(Employee employee, Pageable pageable);

  Page<PerformanceReview> findByReviewer(Employee reviewer, Pageable pageable);

  @Query("SELECT AVG(pr.overallRating) FROM PerformanceReview pr WHERE " +
      "pr.employee = :employee AND pr.status = 'COMPLETED'")
  Double getAverageRating(@Param("employee") Employee employee);
}
