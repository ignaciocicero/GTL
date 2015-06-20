package ar.com.textillevel.modulos.fe;

import java.rmi.RemoteException;

import ar.clarin.fwjava.componentes.error.CLException;
import ar.com.textillevel.facade.api.remote.FacturaFacadeRemote;
import ar.com.textillevel.modulos.fe.cliente.responses.CbteTipoResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.DocTipoResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.DummyResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.IvaTipoResponse;
import ar.com.textillevel.modulos.fe.cliente.responses.MonedaResponse;
import ar.com.textillevel.modulos.fe.connector.AFIPConnector;
import ar.com.textillevel.util.GTLBeanFactory;

public class TestAFIP {

	static {
		System.setProperty("textillevel.fe.habilitada", "si");
		System.setProperty("textillevel.fe.endpointAutenticacion", "https://wsaahomo.afip.gov.ar/ws/services/LoginCms");
		System.setProperty("textillevel.fe.endpointNegocio", "https://wswhomo.afip.gov.ar/wsfev1/service.asmx");
		System.setProperty("textillevel.fe.servicio", "wsfe");
		System.setProperty("textillevel.fe.destinoServicio", "cn=wsaahomo,o=afip,c=ar,serialNumber=CUIT 33693450239");
		System.setProperty("textillevel.fe.keyStore", "D:\\dev\\dev_ws\\salem.p12");
		System.setProperty("textillevel.fe.keyStoreSigner", "Diego Salem");
		System.setProperty("textillevel.fe.keyStorePass", "soloio");
		System.setProperty("textillevel.fe.duracion", "3600000");
		System.setProperty("textillevel.fe.cuitEmpresa","30709129186");
		System.getProperties().setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		System.getProperties().setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
		System.getProperties().setProperty("cltimezone","GMT-3");
		System.getProperties().setProperty("java.naming.provider.url", "localhost:1099");
	}

	public static void main(String[] args) {
		AuthAFIPData authData = ConfiguracionAFIPHolder.getInstance().getAuthData();
		System.out.println("HASH: " + authData.getHash());
		System.out.println("TOKEN: " + authData.getToken());
		try {
			DocTipoResponse tiposDoc = AFIPConnector.getInstance().getTiposDoc();
			DummyResponse funciona = AFIPConnector.getInstance().informeEstadoServicio();
			CbteTipoResponse tiposComprobante = AFIPConnector.getInstance().getTiposComprobante();
			MonedaResponse tiposMoneda = AFIPConnector.getInstance().getTiposMoneda();
			IvaTipoResponse tiposIVA = AFIPConnector.getInstance().getTiposIVA();
			System.out.println("a");
			GTLBeanFactory.getInstance().getBean(FacturaFacadeRemote.class).pruebaAutorizar();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}