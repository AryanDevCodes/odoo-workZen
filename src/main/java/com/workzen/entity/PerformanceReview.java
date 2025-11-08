package com.workzen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "performance_reviews")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReview extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id")
    private Employee reviewer;
    
    @Column(name = "review_period_start", nullable = false)
    private LocalDate reviewPeriodStart;
    
    @Column(name = "review_period_end", nullable = false)
    private LocalDate reviewPeriodEnd;
    
    @Column(name = "overall_rating")
    private Double overallRating; // 1-5 scale
    
    @Column(name = "technical_skills_rating")
    private Double technicalSkillsRating;
    
    @Column(name = "communication_rating")
    private Double communicationRating;
    
    @Column(name = "teamwork_rating")
    private Double teamworkRating;
    
    @Column(name = "leadership_rating")
    private Double leadershipRating;
    
    @Column(name = "punctuality_rating")
    private Double punctualityRating;
    
    @Lob
    private String strengths;
    
    @Lob
    @Column(name = "areas_for_improvement")
    private String areasForImprovement;
    
    @Lob
    private String goals;
    
    @Lob
    @Column(name = "reviewer_comments")
    private String reviewerComments;
    
    @Lob
    @Column(name = "employee_comments")
    private String employeeComments;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.DRAFT;
    
    @Column(name = "review_date")
    private LocalDate reviewDate;
    
    public enum ReviewStatus {
        DRAFT("Draft"),
        SUBMITTED("Submitted"),
        COMPLETED("Completed"),
        ACKNOWLEDGED("Acknowledged");
        
        private final String displayName;
        
        ReviewStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
