package com.richey.gtbankapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fraud",uniqueConstraints = @UniqueConstraint(columnNames = "transaction_id"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fraud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudCheck riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudStatus status;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime date;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<FraudReason> reasons;  // which rules fired

    @Enumerated(EnumType.STRING)
    private  FraudDecision decision;

    private LocalDateTime evaluatedAt;

}
