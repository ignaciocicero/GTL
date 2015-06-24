package ar.com.textillevel.facade.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ar.clarin.fwjava.componentes.error.validaciones.ValidacionException;
import ar.clarin.fwjava.componentes.error.validaciones.ValidacionExceptionSinRollback;
import ar.clarin.fwjava.util.DateUtil;
import ar.com.textillevel.dao.api.local.DocumentoContableDAOLocal;
import ar.com.textillevel.dao.api.local.FacturaDAOLocal;
import ar.com.textillevel.dao.api.local.ParametrosGeneralesDAOLocal;
import ar.com.textillevel.dao.api.local.RemitoSalidaDAOLocal;
import ar.com.textillevel.entidades.config.ConfiguracionNumeracionFactura;
import ar.com.textillevel.entidades.config.NumeracionFactura;
import ar.com.textillevel.entidades.config.ParametrosGenerales;
import ar.com.textillevel.entidades.documentos.factura.DocumentoContableCliente;
import ar.com.textillevel.entidades.enums.EEstadoImpresionDocumento;
import ar.com.textillevel.entidades.enums.EPosicionIVA;
import ar.com.textillevel.entidades.enums.ETipoFactura;
import ar.com.textillevel.excepciones.EValidacionException;
import ar.com.textillevel.facade.api.local.DocumentoContableFacadeLocal;
import ar.com.textillevel.facade.api.remote.DocumentoContableFacadeRemote;
import ar.com.textillevel.modulos.fe.ConfiguracionAFIPHolder;
import ar.com.textillevel.modulos.fe.cliente.responses.FERecuperaLastCbteResponse;
import ar.com.textillevel.modulos.fe.connector.AFIPConnector;
import ar.com.textillevel.modulos.fe.connector.DatosRespuestaAFIP;

@Stateless
public class DocumentoContableFacade implements DocumentoContableFacadeLocal, DocumentoContableFacadeRemote {

	@EJB
	private ParametrosGeneralesDAOLocal paramGeneralesDAO;

	@EJB
	private FacturaDAOLocal facturaDAO;

	@EJB
	private DocumentoContableDAOLocal docContableDAO;

	@EJB
	private RemitoSalidaDAOLocal remitoSalidaDAO;

	public synchronized Integer getProximoNroDocumentoContable(EPosicionIVA posIva) {
		if(ConfiguracionAFIPHolder.getInstance().isHabilitado()) {
			try {
				FERecuperaLastCbteResponse ultimoNroCompAut = AFIPConnector.getInstance().getUltimoComprobante(paramGeneralesDAO.getParametrosGenerales().getNroSucursal(), 1);
				return new Integer(ultimoNroCompAut.getCbteNro()) + 1;
			} catch (RemoteException e) {
				return getProximoNroFactura(posIva);
			}
		} else {
			return getProximoNroFactura(posIva);
		}
	}

	public Integer getProximoNroFactura(EPosicionIVA posIva) {
		ParametrosGenerales parametrosGenerales = paramGeneralesDAO.getParametrosGenerales();
		Integer proximoNumeroDeFactura = getLastNumeroFactura(posIva.getTipoFactura());
		if (proximoNumeroDeFactura == null) {
			ConfiguracionNumeracionFactura configuracionFactura = parametrosGenerales.getConfiguracionFacturaByTipoFactura(posIva.getTipoFactura());
			if (configuracionFactura == null) {
				throw new RuntimeException("Falta configurar el n�mero de comienzo de factura en los par�metros generales.");
			}
			NumeracionFactura numeracionActual = configuracionFactura.getNumeracionActual(DateUtil.getHoy());
			if(numeracionActual != null){
				proximoNumeroDeFactura = numeracionActual.getNroDesde();
			}else{
				throw new RuntimeException("No hay una configuracion de numeros de factura vigente para " + DateUtil.dateToString(DateUtil.getHoy()));
			}
			Integer ultimaFacturaRecibo = remitoSalidaDAO.getUltimoNumeroFactura(posIva);
			if(ultimaFacturaRecibo!=null){
				proximoNumeroDeFactura = Math.max(proximoNumeroDeFactura, ultimaFacturaRecibo);
			}
		} else {
			ConfiguracionNumeracionFactura configuracionFactura = parametrosGenerales.getConfiguracionFacturaByTipoFactura(posIva.getTipoFactura());
			if (configuracionFactura == null) {
				throw new RuntimeException("Falta configurar el n�mero de comienzo de factura en los par�metros generales.");
			}
			NumeracionFactura numeracionActual = configuracionFactura.getNumeracionActual(DateUtil.getHoy());
			Integer numeroDesdeConfigurado = null;
			if(numeracionActual != null){
				numeroDesdeConfigurado = numeracionActual.getNroDesde() - 1; //para hacer + 1 despues
			}else{
				throw new RuntimeException("No hay una configuracion de numeros de factura vigente para " + DateUtil.dateToString(DateUtil.getHoy()));
			}
			Integer ultimaFacturaRecibo = remitoSalidaDAO.getUltimoNumeroFactura(posIva);
			if(ultimaFacturaRecibo == null){
				ultimaFacturaRecibo = numeroDesdeConfigurado;
			}
			//lastNroFactura = lastNroFactura.equals(ultimaFacturaRecibo)?lastNroFactura+1:Math.max(lastNroFactura, ultimaFacturaRecibo+1);
			proximoNumeroDeFactura = getMaximo(proximoNumeroDeFactura,numeroDesdeConfigurado,ultimaFacturaRecibo) + 1;
		}
		return proximoNumeroDeFactura;
	}

	private Integer getLastNumeroFactura(ETipoFactura tipoFactura){
		return facturaDAO.getLastNumeroFactura(tipoFactura);
	}

	private Integer getMaximo(Integer... numeros){
		Integer max = 0;
		for(Integer n : numeros){
			if(n > max){
				max = n;
			}
		}
		return max;
	}

	@SuppressWarnings("unchecked")
	public <D extends DocumentoContableCliente> D autorizarDocumentoContableAFIP(D docContable) throws ValidacionExceptionSinRollback, ValidacionException {
		if(ConfiguracionAFIPHolder.getInstance().isHabilitado()) {
			try {
				DatosRespuestaAFIP respAFIP = AFIPConnector.getInstance().autorizarDocumento(docContable, paramGeneralesDAO.getParametrosGenerales().getNroSucursal(), 1);
				boolean autorizada = respAFIP.isAutorizada(); 
				if(autorizada) {
					docContable.setCaeAFIP(respAFIP.getCae());
					docContable.setEstadoImpresion(EEstadoImpresionDocumento.AUTORIZADO_AFIP);
				}
				docContable.setObservacionesAFIP(respAFIP.getObservaciones() == null ? null : respAFIP.getObservaciones().substring(0, DocumentoContableCliente.LONG_OBS_AFIP-1));
				docContable = (D)docContableDAO.save(docContable);
				if(!autorizada) {
					List<String> msg = new ArrayList<String>();
					msg.add(docContable.getObservacionesAFIP());
					throw new ValidacionExceptionSinRollback(EValidacionException.DOCUMENTO_CONTABLE_NO_SE_PUDO_AUTORIZAR_AFIP.getInfoValidacion(), msg); 
				}
			} catch (RemoteException e) {
				List<String> msg = new ArrayList<String>();
				msg.add(e.getMessage());
				throw new ValidacionException(EValidacionException.DOCUMENTO_CONTABLE_NO_SE_PUDO_AUTORIZAR_AFIP.getInfoValidacion(), msg); 
			}
		}
		return docContable;
	}

	public void checkAutorizacionAFIP(DocumentoContableCliente docContable) throws ValidacionException {
		if(ConfiguracionAFIPHolder.getInstance().isHabilitado() && docContable.getCaeAFIP() != null && (docContable.getEstadoImpresion() == EEstadoImpresionDocumento.AUTORIZADO_AFIP || docContable.getEstadoImpresion() == EEstadoImpresionDocumento.IMPRESO)) {
			throw new ValidacionException(EValidacionException.DOCUMENTO_CONTABLE_NO_SE_PUDO_AUTORIZAR_AFIP.getInfoValidacion());
		}
	}

	public List<DocumentoContableCliente> getDocumentosContablesSinCAE() {
		return docContableDAO.getAllSinCAE();
	}

	public void autorizarMultiplesDocumentosAFIP(List<DocumentoContableCliente> documentos) throws ValidacionExceptionSinRollback, ValidacionException {
		for(DocumentoContableCliente doc : documentos) {
			autorizarDocumentoContableAFIP(doc);
		}
	}
}