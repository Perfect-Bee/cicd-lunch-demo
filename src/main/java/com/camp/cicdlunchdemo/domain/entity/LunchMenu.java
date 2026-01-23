package com.camp.cicdlunchdemo.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "lunch_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class LunchMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private Category category;

    @Column(nullable = false)
    private int spicyLevel;

    @Column(nullable = false)
    private int weight;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @Builder
    public LunchMenu(String name, String description, Category category, int spicyLevel, int weight) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.spicyLevel = spicyLevel;
        this.weight = weight;
    }

    public void update(String name, String description, Category category, int spicyLevel, int weight) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.spicyLevel = spicyLevel;
        this.weight = weight;
    }
}
