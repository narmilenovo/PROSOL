package com.example.usersettings.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedBy
    @Column(updatable = false)
    protected String createdBy;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP) // insert both time and date.
    protected Date createdAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    protected List<UpdateAuditHistory> updateAuditHistories = new ArrayList<>();

    public void updateAuditHistory(List<AuditFields> auditFields) {
        UpdateAuditHistory auditHistory = new UpdateAuditHistory();
        auditHistory.setUpdatedBy(auditHistory.getUpdatedBy());
        auditHistory.setUpdatedAt(auditHistory.getUpdatedAt());

        auditHistory.setAuditFields(auditFields);
        this.updateAuditHistories.add(auditHistory);
    }
}