package com.richey.gtbankapp.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "fraud",uniqueConstraints = @UniqueConstraint(columnNames = "transaction_id"))
public class Fraud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudStatus status;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime date;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;


}
