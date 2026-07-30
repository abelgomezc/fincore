package com.fincore.transferservice.repository;

import com.fincore.transferservice.domain.entity.HistorialEstadoTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEstadoTransferenciaRepository extends JpaRepository<HistorialEstadoTransferencia, Long> {

    List<HistorialEstadoTransferencia> findByTransferenciaIdOrderByFechaCambioAsc(Long transferenciaId);
}