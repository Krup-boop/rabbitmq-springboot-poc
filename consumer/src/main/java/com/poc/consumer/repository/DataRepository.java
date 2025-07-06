package com.poc.consumer.repository;

import com.poc.consumer.entity.TenantDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<TenantDataEntity, Long> {

    List<TenantDataEntity> findByTenantId(String tenantId);

    List<TenantDataEntity> findByTenantIdAndCreatedAtBetween(
            String tenantId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT COUNT(t) FROM TenantDataEntity t WHERE t.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(t) FROM TenantDataEntity t WHERE t.processedAt >= :since")
    long countProcessedSince(@Param("since") LocalDateTime since);

    boolean existsByMessageId(String messageId);
}