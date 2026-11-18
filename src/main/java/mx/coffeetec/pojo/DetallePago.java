package mx.coffeetec.pojo;

import java.math.BigDecimal;

/** POJO de la tabla public.detalle_pago. */
public class DetallePago {

    private Long idDetallePago;
    private Long idPago;
    private String metodo;
    private BigDecimal monto;
    private String referencia;

    public DetallePago() {
    }

    public Long getIdDetallePago() {
        return idDetallePago;
    }

    public void setIdDetallePago(Long idDetallePago) {
        this.idDetallePago = idDetallePago;
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

}
