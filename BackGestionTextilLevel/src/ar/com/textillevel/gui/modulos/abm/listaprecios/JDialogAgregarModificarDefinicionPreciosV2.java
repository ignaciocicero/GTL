package ar.com.textillevel.gui.modulos.abm.listaprecios;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.clarin.fwjava.componentes.CLJNumericTextField;
import ar.clarin.fwjava.componentes.CLJOptionPane;
import ar.clarin.fwjava.componentes.CLJTextField;
import ar.clarin.fwjava.util.GuiUtil;
import ar.com.textillevel.entidades.enums.ETipoProducto;
import ar.com.textillevel.entidades.ventas.cotizacion.DefinicionPrecio;
import ar.com.textillevel.entidades.ventas.cotizacion.GrupoTipoArticulo;
import ar.com.textillevel.facade.api.remote.TipoArticuloFacadeRemote;
import ar.com.textillevel.gui.util.GenericUtils;
import ar.com.textillevel.util.GTLBeanFactory;

public abstract class JDialogAgregarModificarDefinicionPreciosV2 extends JDialog {

	private static final long serialVersionUID = 1317620079501375084L;

	private JButton btnAceptar;
	private JButton btnCancelar;
	private CLJTextField txtTipoProducto;
	private JButton btnAgregar;
	private CLJNumericTextField txtAnchoInicial;
	private CLJNumericTextField txtAnchoFinal;
	private JComboBox cmbTipoArticulo;
	private CLJNumericTextField txtPrecio;

	private ETipoProducto tipoProducto;
	private boolean acepto;
	private DefinicionPrecio definicion;
	private TipoArticuloFacadeRemote tipoArticuloFacade;
	
	public JDialogAgregarModificarDefinicionPreciosV2(Frame padre, ETipoProducto tipoProducto) {
		super(padre);
		setDefinicion(new DefinicionPrecio());
		setTipoProducto(tipoProducto);
		setUpComponentes();
		setUpScreen();
	}
	
	public JDialogAgregarModificarDefinicionPreciosV2(Frame padre, ETipoProducto tipoProducto, DefinicionPrecio definicionAModificar) {
		super(padre);
		setDefinicion(definicionAModificar);
		setTipoProducto(tipoProducto);
		setUpComponentes();
		setUpScreen();
	}

	private void setUpScreen() {
		setTitle("Agregar/modificar definici�n de precios para - " + getTipoProducto().getDescripcion().toUpperCase());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(600, 600));
		setModal(true);
		setResizable(false);
		GuiUtil.centrar(this);
	}

	private void setUpComponentes() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				salir();
			}
		});
		
		JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panelSur.add(getBtnAceptar());
		panelSur.add(getBtnCancelar());
		
		add(getPanelNorte(), BorderLayout.NORTH);
		PanelTablaRango<? extends GrupoTipoArticulo> tablaCentral = createPanelTabla();
		if (tablaCentral != null) {
			add(tablaCentral, BorderLayout.CENTER);
		}
		add(panelSur, BorderLayout.SOUTH);
	}
	
	private JPanel getPanelNorte() {
		JPanel panelNorte = new JPanel(new GridBagLayout());
		panelNorte.add(new JLabel("Tipo de producto: "), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 1, 1, 0, 0));
		panelNorte.add(getTxtTipoProducto(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 5, 1, 1, 1));
		
		panelNorte.add(new JLabel("Ancho inicial: "), GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 1, 1, 0, 0));
		panelNorte.add(getTxtAnchoInicial(), GenericUtils.createGridBagConstraints(1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1, 1, 1, 1));

		panelNorte.add(new JLabel("Ancho final: "), GenericUtils.createGridBagConstraints(2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 1, 1, 0, 0));
		panelNorte.add(getTxtAnchoFinal(), GenericUtils.createGridBagConstraints(3, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1, 1, 1, 1));

		panelNorte.add(new JLabel("Art�culo: "), GenericUtils.createGridBagConstraints(4, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 1, 1, 0, 0));
		panelNorte.add(getCmbTipoArticulo(), GenericUtils.createGridBagConstraints(5, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1, 1, 1, 1));
		
		JPanel panelEspecifico = createPanelDatosEspecificos();
		if (panelEspecifico != null) {
			panelNorte.add(panelEspecifico, GenericUtils.createGridBagConstraints(0, 2, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 6,1,1,1));
		}
		panelNorte.add(new JLabel("Precio: "), GenericUtils.createGridBagConstraints(0, 3, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 1, 1, 0, 0));
		panelNorte.add(getTxtPrecio(), GenericUtils.createGridBagConstraints(1, 3, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 2, 1, 1, 1));
		panelNorte.add(getBtnAgregar(), GenericUtils.createGridBagConstraints(3, 3, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 2, 1, 1, 1));
		return panelNorte;
	}

	protected abstract JPanel createPanelDatosEspecificos();
	protected abstract PanelTablaRango<? extends GrupoTipoArticulo> createPanelTabla();

	public JButton getBtnAceptar() {
		if (btnAceptar == null) {
			btnAceptar = new JButton("Aceptar");
			btnAceptar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				}
			});
		}
		return btnAceptar;
	}
	
	public boolean isAcepto() {
		return acepto;
	}

	public void setAcepto(boolean acepto) {
		this.acepto = acepto;
	}

	public DefinicionPrecio getDefinicion() {
		return definicion;
	}

	public void setDefinicion(DefinicionPrecio definicion) {
		this.definicion = definicion;
	}

	private void salir() {
		int ret = CLJOptionPane.showQuestionMessage(this, "Va a salir sin grabar, desea continuar?", "Alta de cheque");
		if (ret == CLJOptionPane.YES_OPTION) {
			setAcepto(false);
			dispose();
		}
	}
	
	public JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					salir();
				}
			});
		}
		return btnCancelar;
	}

	public CLJTextField getTxtTipoProducto() {
		if (txtTipoProducto == null) {
			txtTipoProducto = new CLJTextField(getTipoProducto().getDescripcion().toUpperCase());
			txtTipoProducto.setEditable(false);
			txtTipoProducto.setPreferredSize(new Dimension(300, 20));
			txtTipoProducto.setSize(new Dimension(300, 20));
		}
		return txtTipoProducto;
	}

	public ETipoProducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(ETipoProducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public JButton getBtnAgregar() {
		if (btnAgregar == null) {
			btnAgregar = new JButton("Agregar");
		}
		return btnAgregar;
	}

	public CLJNumericTextField getTxtAnchoInicial() {
		if (txtAnchoInicial == null) {
			txtAnchoInicial = new CLJNumericTextField(0, 10);
			txtAnchoInicial.setSize(100, 20);
		}
		return txtAnchoInicial;
	}

	public CLJNumericTextField getTxtAnchoFinal() {
		if (txtAnchoFinal == null) {
			txtAnchoFinal = new CLJNumericTextField(0, 10);
		}
		return txtAnchoFinal;
	}

	public JComboBox getCmbTipoArticulo() {
		if (cmbTipoArticulo == null) {
			cmbTipoArticulo = new JComboBox();
			GuiUtil.llenarCombo(cmbTipoArticulo, getTipoArticuloFacade().getAllTipoArticulos(), true);
		}
		return cmbTipoArticulo;
	}

	public CLJNumericTextField getTxtPrecio() {
		if (txtPrecio == null) {
			txtPrecio = new CLJNumericTextField(0, 10);
		}
		return txtPrecio;
	}
	
	public TipoArticuloFacadeRemote getTipoArticuloFacade() {
		if (tipoArticuloFacade == null) {
			tipoArticuloFacade = GTLBeanFactory.getInstance().getBean2(TipoArticuloFacadeRemote.class);
		}
		return tipoArticuloFacade;
	}
}