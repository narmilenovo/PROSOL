package com.example.plantservice.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<U> {
    @CreatedBy
    @Column(nullable = false, updatable = false)
    protected U createdBy;

    @LastModifiedBy
    @Column(insertable = false)
    protected U updatedBy;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MM yyyy hh:mm a")
    @Column(nullable = false, updatable = false)
    protected Date createdAt;


    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MM yyyy hh:mm a")
    @Column(insertable = false)
    protected Date updatedAt;
}