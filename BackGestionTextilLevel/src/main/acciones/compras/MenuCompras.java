package main.acciones.compras;

import java.awt.Frame;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import ar.com.fwcommon.templates.main.menu.FWJMenu;

public class MenuCompras extends FWJMenu{

	private static final long serialVersionUID = -3979378334806048624L;
	
	private JMenuItem menuItemAgregarRemitoEntrada;
	private FWJMenu menuConsultas;
	private Frame frame;

	public MenuCompras(Frame frame) {
		super("Compras", 'S');
		this.frame = frame;
		setMnemonic(KeyEvent.VK_S);
		add(getMenuItemAgregarRemitoEntradaProveedor());
		add(getMenuConsultas());
	}
	
	public FWJMenu getMenuConsultas(){
		if(menuConsultas == null){
			menuConsultas = new FWJMenu("Consultas");
			JMenuItem menuConsultarRemitoEntradaProveedor = new JMenuItem(new ConsultarRemitoEntradaProveedorAction(frame));
			menuConsultarRemitoEntradaProveedor.setText("Consultar Remito Entrada Proveedor");
			menuConsultas.add(menuConsultarRemitoEntradaProveedor);
		}
		return menuConsultas;
	}

	private JMenuItem getMenuItemAgregarRemitoEntradaProveedor() {
		if(menuItemAgregarRemitoEntrada == null) {
			menuItemAgregarRemitoEntrada = new JMenuItem(new AgregarRemitoEntradaProveedorAction(frame));
			menuItemAgregarRemitoEntrada.setText("Agregar Remito de Entrada de proveedor");
		}
		return menuItemAgregarRemitoEntrada;
	}

}
