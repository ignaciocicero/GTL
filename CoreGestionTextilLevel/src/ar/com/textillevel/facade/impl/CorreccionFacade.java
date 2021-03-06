package ar.com.textillevel.facade.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.annotation.IgnoreDependency;

import ar.com.fwcommon.auditoria.evento.enumeradores.EnumTipoEvento;
import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.componentes.error.validaciones.ValidacionException;
import ar.com.fwcommon.componentes.error.validaciones.ValidacionExceptionSinRollback;
import ar.com.textillevel.dao.api.local.ChequeDAOLocal;
import ar.com.textillevel.dao.api.local.CorreccionDAOLocal;
import ar.com.textillevel.dao.api.local.CorreccionFacturaProveedorDAOLocal;
import ar.com.textillevel.dao.api.local.FacturaDAOLocal;
import ar.com.textillevel.dao.api.local.PagoOrdenDePagoDAOLocal;
import ar.com.textillevel.dao.api.local.ParametrosGeneralesDAOLocal;
import ar.com.textillevel.dao.api.local.RemitoEntradaDibujoDAOLocal;
import ar.com.textillevel.entidades.cheque.Cheque;
import ar.com.textillevel.entidades.documentos.factura.CorreccionFactura;
import ar.com.textillevel.entidades.documentos.factura.Factura;
import ar.com.textillevel.entidades.documentos.factura.NotaCredito;
import ar.com.textillevel.entidades.documentos.factura.NotaDebito;
import ar.com.textillevel.entidades.documentos.factura.proveedor.CorreccionFacturaProveedor;
import ar.com.textillevel.entidades.documentos.factura.proveedor.NotaDebitoProveedor;
import ar.com.textillevel.entidades.documentos.to.CorreccionFacturaMobTO;
import ar.com.textillevel.entidades.enums.ETipoCorreccionFactura;
import ar.com.textillevel.excepciones.EValidacionException;
import ar.com.textillevel.facade.api.local.CorreccionFacadeLocal;
import ar.com.textillevel.facade.api.local.CorreccionFacturaProveedorFacadeLocal;
import ar.com.textillevel.facade.api.local.CuentaFacadeLocal;
import ar.com.textillevel.facade.api.local.DocumentoContableFacadeLocal;
import ar.com.textillevel.facade.api.remote.AuditoriaFacadeLocal;
import ar.com.textillevel.facade.api.remote.CorreccionFacadeRemote;

@Stateless
public class CorreccionFacade implements CorreccionFacadeLocal, CorreccionFacadeRemote {

	@EJB
	private CorreccionDAOLocal correccionDao;
	
	@EJB
	private CorreccionFacturaProveedorDAOLocal correccionProveedorDAO;
	
	@EJB
	private CuentaFacadeLocal cuentaFacade;
	
	@EJB
	@IgnoreDependency
	private ChequeDAOLocal chequeDao;
	
	@EJB
	@IgnoreDependency
	private CorreccionFacturaProveedorFacadeLocal correccionProvedorFacade;
	
	@EJB
	@IgnoreDependency
	private PagoOrdenDePagoDAOLocal pagoOrdenDePagoDAO;

	@EJB
	private DocumentoContableFacadeLocal docContableFacade; 

	@EJB
	private AuditoriaFacadeLocal<CorreccionFactura> auditoriaFacade;

	@EJB
	private AuditoriaFacadeLocal<CorreccionFacturaProveedor> auditoriaFacade2;
	
	@EJB
	private ParametrosGeneralesDAOLocal parametrosGeneralesDAO;
	
	@EJB
	private FacturaDAOLocal facturaDAO;
	
	@EJB
	private RemitoEntradaDibujoDAOLocal reDibujoDAO;
	
	public CorreccionFactura guardarCorreccionYGenerarMovimiento(CorreccionFactura correccion, String usuario) throws ValidacionException, ValidacionExceptionSinRollback {
		correccion = guardarYGenerarMovimientoInterno(correccion);
		auditoriaFacade.auditar(usuario, "Actualizaci�n de nota de "+ correccion.getTipo().getDescripcion() + " N�: " + correccion.getNroFactura() + " Monto " + correccion.getMontoTotal().doubleValue(), EnumTipoEvento.ALTA, correccion);
		return docContableFacade.autorizarDocumentoContableAFIP(correccion);
	}

	private CorreccionFactura guardarYGenerarMovimientoInterno(CorreccionFactura correccion) {
		correccion.setNroSucursal(parametrosGeneralesDAO.getParametrosGenerales().getNroSucursal());
		correccion = correccionDao.save(correccion);
		if (correccion.getTipo() == ETipoCorreccionFactura.NOTA_DEBITO) {
			correccion = cuentaFacade.crearMovimientoDebe((NotaDebito) correccion);
		} else {
			cuentaFacade.crearMovimientoHaber((NotaCredito) correccion);
		}
		return correccion;
	}

	public CorreccionFactura actualizarCorreccion(CorreccionFactura correccion) throws FWException {
		return correccionDao.save(correccion);
		//auditoriaFacade.auditar(usuario, "Actualizaci�n de nota de "+correccion.getTipo().getDescripcion() +" N�: " + correccion.getNroCorreccion(), EnumTipoEvento.MODIFICACION, correccion);
	}

	public CorreccionFactura getCorreccionByNumero(Integer idNumero, ETipoCorreccionFactura tipoCorreccion, Integer nroSucursal) {
		return correccionDao.getCorreccionByNumero(idNumero, tipoCorreccion, nroSucursal);
	}

	public List<NotaCredito> getNotaCreditoPendienteUsarList(Integer idCliente) {
		return correccionDao.getNotaCreditoPendienteUsarList(idCliente, parametrosGeneralesDAO.getParametrosGenerales().getNroSucursal());
	}

	public void cambiarVerificacionCorreccion(CorreccionFactura correccion, boolean verificada, String usrName) {
		correccion.setVerificada(verificada);
		if(verificada){
			correccion.setUsuarioVerificador(usrName);
		}
		correccion = correccionDao.save(correccion);
		auditoriaFacade.auditar(usrName, "Cambio de verificaci�n nota de "+correccion.getTipo().getDescripcion() +" N�: ", EnumTipoEvento.MODIFICACION, correccion);
	}

	public NotaCredito getNotaDeCreditoByFacturaRelacionada(Factura factura) {
		return correccionDao.getNotaDeCreditoByFacturaRelacionada(factura);
	}

	public CorreccionFactura editarCorreccion(CorreccionFactura correccion, String usuario) throws ValidacionException {
		docContableFacade.checkAutorizacionAFIP(correccion);		
		eliminarCorreccionInterno(correccion);
		correccion = guardarYGenerarMovimientoInterno(correccion);
		auditoriaFacade.auditar(usuario, "Edici�n de nota de "+correccion.getTipo().getDescripcion() +" N�: " + correccion.getNroFactura(), EnumTipoEvento.MODIFICACION, correccion);
		return correccion;
	}

	public void anularCorreccion(CorreccionFactura correccion, String usrName) throws FWException, ValidacionException {
		docContableFacade.checkAutorizacionAFIP(correccion);
		correccion = getCorreccionById(correccion.getId());
		if(correccion instanceof NotaDebito){
			NotaDebito notaDebito = (NotaDebito)correccion;
			if(correccionDao.notaDebitoSeUsaEnRecibo(notaDebito)){
				throw new ValidacionException(EValidacionException.NOTA_DEBITO_SE_USA_EN_RECIBO.getInfoValidacion());
			}
			
			cuentaFacade.borrarMovimientoNotaDebitoCliente((NotaDebito)correccion);

			//Rollback del cheque. Paso de rechazado al estado anterior
			if(notaDebito.getChequeRechazado()!=null){
				Cheque chequeAPonerEnCartera = notaDebito.getChequeRechazado();
				chequeAPonerEnCartera.setEstadoCheque(notaDebito.getEstadoAnteriorCheque());
				chequeDao.save(chequeAPonerEnCartera);
			}
		}else{
			NotaCredito notaCredito = (NotaCredito) correccion;
//			if(notaCredito.getFacturasRelacionadas().size()>0){
//				throw new ValidacionException(EValidacionException.NOTA_CREDITO_TIENE_FACTURAS_RELACIONADAS.getInfoValidacion());
//			}
			if(correccionDao.notaCreditoSeUsaEnRecibo(notaCredito)){
				throw new ValidacionException(EValidacionException.NOTA_CREDITO_SE_USA_EN_RECIBO.getInfoValidacion());
			}
			cuentaFacade.borrarMovimientoNotaCreditoCliente(notaCredito);
		}
		correccion.setAnulada(true);
		correccionDao.save(correccion);
		auditoriaFacade.auditar(usrName, "Anulaci�n de nota de "+correccion.getTipo().getDescripcion() +" N�: ", EnumTipoEvento.MODIFICACION, correccion);
	}
	
	public void eliminarCorreccion(CorreccionFactura correccion, String usrName) throws FWException, ValidacionException {
		docContableFacade.checkAutorizacionAFIP(correccion);
		correccion = getCorreccionById(correccion.getId());
		
		if(correccion instanceof NotaDebito){
			NotaDebito notaDebito = (NotaDebito)correccion;
			if(notaDebito.getChequeRechazado()!=null){
				Cheque c =notaDebito.getChequeRechazado();
				c.setEstadoCheque(notaDebito.getEstadoAnteriorCheque());
				chequeDao.save(c);
				CorreccionFacturaProveedor ndp = correccionProvedorFacade.obtenerNotaDeDebitoByCheque(c);
				if(ndp!=null){
					//correccionProvedorFacade.borrarCorreccionFacturaProveedor(ndp, usrName);
					if(pagoOrdenDePagoDAO.existsNotaDebitoProvEnPagoOrdenDePago((NotaDebitoProveedor)ndp)) {
						throw new ValidacionException(EValidacionException.NOTA_DEBITO_PROV_EXISTE_EN_ORDEN_DE_PAGO.getInfoValidacion());
					}
					cuentaFacade.borrarMovimientoNotaDebitoProveedor((NotaDebitoProveedor)ndp);
					correccionProveedorDAO.removeById(ndp.getId());
					auditoriaFacade2.auditar(usrName, "borrado de nota de d�bito de proveedor "+" N�: " + ndp.getNroCorreccion(), EnumTipoEvento.BAJA, ndp);
				}
			}
		}
		
		correccion = eliminarCorreccionInterno(correccion);
		auditoriaFacade.auditar(usrName, "Eliminaci�n de nota de "+correccion.getTipo().getDescripcion() +" N�: " + correccion.getNroFactura(), EnumTipoEvento.BAJA, correccion);
	}

	private CorreccionFactura eliminarCorreccionInterno(CorreccionFactura correccion) throws ValidacionException {
		docContableFacade.checkAutorizacionAFIP(correccion);
		correccion = getCorreccionById(correccion.getId()); 
		if(correccion instanceof NotaCredito){
			/*
			if(((NotaCredito)correccion).getFacturasRelacionadas().size()>0){
				throw new ValidacionException(EValidacionException.NOTA_CREDITO_TIENE_FACTURAS_RELACIONADAS.getInfoValidacion());
			}
			*/
			if(correccionDao.notaCreditoSeUsaEnRecibo((NotaCredito)correccion)){
				throw new ValidacionException(EValidacionException.NOTA_CREDITO_SE_USA_EN_RECIBO.getInfoValidacion());
			}
			cuentaFacade.borrarMovimientoNotaCreditoCliente((NotaCredito)correccion);
		}else{
			if(correccionDao.notaDebitoSeUsaEnRecibo((NotaDebito)correccion)){
				throw new ValidacionException(EValidacionException.NOTA_DEBITO_SE_USA_EN_RECIBO.getInfoValidacion());
			}
			cuentaFacade.borrarMovimientoNotaDebitoCliente((NotaDebito)correccion);
		}
		correccionDao.removeById(correccion.getId());
		return correccion;
	}

	public List<NotaDebito> getNotasDebitoByCheque(Cheque cheque) {
		return correccionDao.getNotasDebitoByCheque(cheque);
	}

	public CorreccionFacturaMobTO getCorreccionMobById(Integer idCorreccion) {
		CorreccionFactura cf = correccionDao.getCorreccionById(idCorreccion);
		return new CorreccionFacturaMobTO(cf);
	}

	public CorreccionFacturaMobTO getCorreccionMobByNumero(Integer idNumero, ETipoCorreccionFactura tipoCorreccion, Integer nroSucursal) {
		CorreccionFactura cf = correccionDao.getCorreccionByNumero(idNumero, tipoCorreccion, nroSucursal);
		return new CorreccionFacturaMobTO(cf);
	}

	public CorreccionFactura getCorreccionById(Integer idCorreccion) {
		return correccionDao.getCorreccionById(idCorreccion);
	}

}