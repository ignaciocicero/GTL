package ar.com.textillevel.facade.api.local;

import java.util.List;

import javax.ejb.Local;

import ar.com.fwcommon.componentes.error.validaciones.ValidacionException;
import ar.com.fwcommon.componentes.error.validaciones.ValidacionExceptionSinRollback;
import ar.com.textillevel.entidades.documentos.factura.Factura;
import ar.com.textillevel.entidades.ventas.articulos.DibujoEstampado;

@Local
public interface FacturaFacadeLocal {

	public Factura guardarFacturaYGenerarMovimiento(Factura factura, List<DibujoEstampado> dibujos, String usuario) throws ValidacionException, ValidacionExceptionSinRollback;
	public Factura getByNroFacturaConItems(Integer nroFactura, Integer nroSucursal);
	public Factura getByIdEager(Integer id);

}
