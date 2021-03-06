package ar.com.textillevel.modulos.fe.connector;

import java.net.URL;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import ar.com.textillevel.entidades.documentos.factura.DocumentoContableCliente;
import ar.com.textillevel.modulos.fe.AuthAFIPData;
import ar.com.textillevel.modulos.fe.ConfiguracionAFIPHolder;
import ar.com.textillevel.modulos.fe.cliente.ServiceLocator;
import ar.com.textillevel.modulos.fe.cliente.ServiceSoap;
import ar.com.textillevel.modulos.fe.cliente.requests.FEAuthRequest;
import ar.com.textillevel.modulos.fe.cliente.requests.FECAERequest;
import ar.com.textillevel.modulos.fe.cliente.requests.FECompConsultaReq;
import ar.com.textillevel.modulos.fe.cliente.responses.CbteTipoResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.DocTipoResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.DummyResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.FECAEResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.FECompConsultaResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.FERecuperaLastCbteResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.IvaTipoResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.MonedaResponse;

public class AFIPConnector {

	private static final Logger logger = Logger.getLogger(AFIPConnector.class);
	
	private static ServiceSoap servicios;
	private static final AFIPConnector instance = new AFIPConnector();
	public static AFIPConnector getInstance() {
		return instance;
	}
	
	static {
		try{
			if(ConfiguracionAFIPHolder.getInstance().isHabilitado()) {
				ServiceLocator locator = new ServiceLocator();
				servicios = locator.getServiceSoap(new URL(ConfiguracionAFIPHolder.getInstance().getURLNegocio()));
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	public DatosRespuestaAFIP autorizarDocumento(DocumentoContableCliente documento, int nroSucursal, int idTipoComprobanteAFIP) throws RemoteException {
		AuthAFIPData authData = ConfiguracionAFIPHolder.getInstance().getAuthData();
		checkAuthDataAndServiceAFIP(authData);
		FECAERequest request = AFIPConverter.crearRequest(documento, nroSucursal, idTipoComprobanteAFIP);
		FECAEResponse response = servicios.FECAESolicitar(new FEAuthRequest(authData.getToken(), authData.getHash(), authData.getCuitEmpresa()), request);
		return new DatosRespuestaAFIP(response);
	}

	public DocTipoResponse getTiposDoc() throws RemoteException{
		AuthAFIPData authData = ConfiguracionAFIPHolder.getInstance().getAuthData();
		checkAuthDataAndServiceAFIP(authData);
		return servicios.FEParamGetTiposDoc(new FEAuthRequest(authData.getToken(), authData.getHash(), authData.getCuitEmpresa()));
	}

	public CbteTipoResponse getTiposComprobante() throws RemoteException{
		AuthAFIPData authData = ConfiguracionAFIPHolder.getInstance().getAuthData();
		checkAuthDataAndServiceAFIP(authData);
		return servicios.FEParamGetTiposCbte(new FEAuthRequest(authData.getToken(), authData.getHash(), authData.getCuitEmpresa()));
	}
	
	public MonedaResponse getTiposMoneda() throws RemoteException {
		AuthAFIPData authData = ConfiguracionAFIPHolder.getInstance().getAuthData();
		checkAuthDataAndServiceAFIP(authData);
		return servicios.FEParamGetTiposMonedas(new FEAuthRequest(authData.getToken(), authData.getHash(), authData.getCuitEmpresa()));
	}
	
	public IvaTipoResponse getTiposIVA() throws RemoteException {
		AuthAFIPData authData = ConfiguracionAFIPHolder.getInstance().getAuthData();
		checkAuthDataAndServiceAFIP(authData);
		return servicios.FEParamGetTiposIva(new FEAuthRequest(authData.getToken(), authData.getHash(), authData.getCuitEmpresa()));
	}
	
	public DummyResponse informeEstadoServicio() throws RemoteException{
		return servicios.FEDummy();
	}

	public FERecuperaLastCbteResponse getUltimoComprobante(int nroSucursal, int idTipoComprobanteAFIP) throws RemoteException {
		AuthAFIPData authData = ConfiguracionAFIPHolder.getInstance().getAuthData();
		checkAuthDataAndServiceAFIP(authData);
		return servicios.FECompUltimoAutorizado(new FEAuthRequest(authData.getToken(), authData.getHash(), authData.getCuitEmpresa()), nroSucursal, idTipoComprobanteAFIP);
	}
	
	public FECompConsultaResponse consultarDatosDocumentoIngresado(int nroSucursal, int idTipoComprobanteAFIP, int nroComprobante) throws RemoteException {
		AuthAFIPData authData = ConfiguracionAFIPHolder.getInstance().getAuthData();
		checkAuthDataAndServiceAFIP(authData);
		FECompConsultaReq feCompConsReq = new FECompConsultaReq(idTipoComprobanteAFIP, nroComprobante, nroSucursal);
		return servicios.FECompConsultar(new FEAuthRequest(authData.getToken(), authData.getHash(), authData.getCuitEmpresa()), feCompConsReq);
	}
	
	private void checkAuthDataAndServiceAFIP(AuthAFIPData authData) throws RemoteException {
		if(authData == null || servicios==null) {
			throw new RemoteException("");
		}
	}
}
