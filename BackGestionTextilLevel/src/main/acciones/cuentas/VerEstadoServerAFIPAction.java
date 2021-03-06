package main.acciones.cuentas;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.error.validaciones.ValidacionException;
import ar.com.textillevel.facade.api.remote.DocumentoContableFacadeRemote;
import ar.com.textillevel.facade.api.remote.ParametrosGeneralesFacadeRemote;
import ar.com.textillevel.gui.acciones.JDialogEstadoServerAFIP;
import ar.com.textillevel.util.GTLBeanFactory;

public class VerEstadoServerAFIPAction implements Action {

	private final Frame frame;

	public VerEstadoServerAFIPAction(Frame frame) {
		this.frame = frame;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	public Object getValue(String key) {
		return null;
	}

	public boolean isEnabled() {
		return true;
	}

	public void putValue(String key, Object value) {

	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

	public void setEnabled(boolean b) {

	}

	public void actionPerformed(ActionEvent e) {
		try{
			Integer nroSucursal = GTLBeanFactory.getInstance().getBean2(ParametrosGeneralesFacadeRemote.class).getParametrosGenerales().getNroSucursal();
			new JDialogEstadoServerAFIP(frame, GTLBeanFactory.getInstance().getBean2(DocumentoContableFacadeRemote.class).getEstadoServidorAFIP(nroSucursal)).setVisible(true);
		}catch(ValidacionException vle){
			FWJOptionPane.showErrorMessage(frame, vle.getMensajeError(), "Error");
		}
	}

}
