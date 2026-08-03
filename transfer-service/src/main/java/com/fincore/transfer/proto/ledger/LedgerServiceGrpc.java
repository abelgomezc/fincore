package com.fincore.transfer.proto.ledger;

/**
 * Stub simplificado para LedgerService (reemplaza código generado por proto).
 *
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
public class LedgerServiceGrpc {

    public static LedgerServiceBlockingStub newBlockingStub(Object channel) {
        return new LedgerServiceBlockingStub();
    }

    public static class LedgerServiceBlockingStub {

        public CrearAsientoResponse crearAsiento(CrearAsientoRequest request) {
            CrearAsientoResponse response = new CrearAsientoResponse();
            response.setExito(true);
            response.setMensaje("Asiento creado");
            return response;
        }

        public ObtenerAsientoResponse obtenerAsiento(ObtenerAsientoRequest request) {
            ObtenerAsientoResponse response = new ObtenerAsientoResponse();
            response.setExito(true);
            return response;
        }

        public VerificarEquibradoResponse verificarEquilibrio(VerificarEquibradoRequest request) {
            VerificarEquibradoResponse response = new VerificarEquibradoResponse();
            response.setEstaEquilibrado(true);
            return response;
        }
    }
}
