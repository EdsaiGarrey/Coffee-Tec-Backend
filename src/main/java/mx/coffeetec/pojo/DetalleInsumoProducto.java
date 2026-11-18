package mx.coffeetec.pojo;

import java.math.BigDecimal;

/** POJO de la tabla public.detalle_insumo_producto. */
public class DetalleInsumoProducto {

    private Long idProducto;
    private Long idInsumo;
    private BigDecimal cantidadNecesaria;

    public DetalleInsumoProducto() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Long getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(Long idInsumo) {
        this.idInsumo = idInsumo;
    }

    public BigDecimal getCantidadNecesaria() {
        return cantidadNecesaria;
    }

    public void setCantidadNecesaria(BigDecimal cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }

}
