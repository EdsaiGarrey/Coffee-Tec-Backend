package mx.coffeetec.pojo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** POJO de la tabla public.caja. */
public class Caja {

    private Long idCaja;
    private String nombre;
    private String estado;
    private OffsetDateTime fechaApertura;
    private OffsetDateTime fechaCierre;
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinal;
    private Long idUsuarioApertura;
    private Long idUsuarioCierre;

    public Caja() {
    }

    public Long getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Long idCaja) {
        this.idCaja = idCaja;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public OffsetDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(OffsetDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public OffsetDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(OffsetDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public Long getIdUsuarioApertura() {
        return idUsuarioApertura;
    }

    public void setIdUsuarioApertura(Long idUsuarioApertura) {
        this.idUsuarioApertura = idUsuarioApertura;
    }

    public Long getIdUsuarioCierre() {
        return idUsuarioCierre;
    }

    public void setIdUsuarioCierre(Long idUsuarioCierre) {
        this.idUsuarioCierre = idUsuarioCierre;
    }

}
