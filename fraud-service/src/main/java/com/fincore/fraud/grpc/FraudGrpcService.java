package com.fincore.fraud.grpc;

import com.fincore.fraud.proto.fraud.EvaluacionFraudeRequest;
import com.fincore.fraud.proto.fraud.EvaluacionFraudeResponse;
import com.fincore.fraud.proto.fraud.FraudServiceGrpc;
import com.fincore.fraud.service.FraudEvaluationService;
import com.fincore.fraud.service.FraudEvaluationService.EvaluacionResultado;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.math.BigDecimal;

/**
 * Servicio gRPC del motor antifraude.
 *
 * Recepción de solicitudes de evaluación de fraude desde transfer-service.
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
@GRpcService
@Slf4j
public class FraudGrpcService extends FraudServiceGrpc.FraudServiceImplBase {

    private final FraudEvaluationService fraudEvaluationService;

    public FraudGrpcService(FraudEvaluationService fraudEvaluationService) {
        this.fraudEvaluationService = fraudEvaluationService;
    }

    @Override
    public void evaluarTransferencia(EvaluacionFraudeRequest request,
                                     io.grpc.stub.StreamObserver<EvaluacionFraudeResponse> responseObserver) {
        log.info("Solicitud de evaluación de fraude: transferencia={}, cuentaOrigen={}",
                request.getIdTransferencia(), request.getIdCuentaOrigen());

        EvaluacionResultado resultado = fraudEvaluationService.evaluarTransferencia(
                request.getIdTransferencia(),
                request.getIdCuentaOrigen(),
                new BigDecimal(request.getMonto()),
                request.getIpOrigen(),
                request.getDispositivo(),
                request.getTraceId(),
                request.getIdCliente(),
                request.getNumeroCuentaDestino()
        );

        EvaluacionFraudeResponse.Builder builder = EvaluacionFraudeResponse.newBuilder();
        builder.setScore(resultado.getScore());
        builder.setDecision(resultado.getDecision().name());
        builder.setMensaje(resultado.getMensaje());
        builder.addAllReglasActivadas(resultado.getReglasActivadas());
        builder.setTiempoEvaluacionMs(resultado.getTiempoEvaluacionMs());

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
