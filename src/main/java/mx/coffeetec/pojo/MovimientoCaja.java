package mx.coffeetec.pojo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** POJO de la tabla public.movimiento_caja. */
public class MovimientoCaja {

    private Long idMovimientoCaja;
    private Long idCaja;
    private Long idUsuario;
    private Long idDetallePago;
    private String tipo;
    private String concepto;
    private BigDecimal monto;
    private OffsetDateTime fechaHora;

    public MovimientoCaja() {
    }

    public Long getIdMovimientoCaja() {
        return idMovimientoCaja;
    }

    public void setIdMovimientoCaja(Long idMovimientoCaja) {
        this.idMovimientoCaja = idMovimientoCaja;
    }

    public Long getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Long idCaja) {
        this.idCaja = idCaja;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdDetallePago() {
        return idDetallePago;
    }

    public void setIdDetallePago(Long idDetallePago) {
        this.idDetallePago = idDetallePago;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

}
