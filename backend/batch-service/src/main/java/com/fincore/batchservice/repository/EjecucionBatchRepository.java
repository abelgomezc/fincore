package com.fincore.batchservice.repository;

import com.fincore.batchservice.entity.EjecucionBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjecucionBatchRepository extends JpaRepository<EjecucionBatch, Long> {
}
