package ar.com.textillevel.gui.modulos.cheques.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import org.apache.taglibs.string.util.StringW;

import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.FWJTextField;
import ar.com.fwcommon.componentes.FWTxtComboBoxBusqueda;
import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.util.DateUtil;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.fwcommon.util.ImageUtil;
import ar.com.fwcommon.util.NumUtil;
import ar.com.fwcommon.util.StringUtil;
import ar.com.textillevel.entidades.cheque.Banco;
import ar.com.textillevel.entidades.cheque.Cheque;
import ar.com.textillevel.entidades.cheque.NumeracionCheque;
import ar.com.textillevel.entidades.config.ParametrosGenerales;
import ar.com.textillevel.entidades.enums.EEstadoCheque;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.facade.api.remote.BancoFacadeRemote;
import ar.com.textillevel.facade.api.remote.ChequeFacadeRemote;
import ar.com.textillevel.facade.api.remote.ClienteFacadeRemote;
import ar.com.textillevel.facade.api.remote.ParametrosGeneralesFacadeRemote;
import ar.com.textillevel.gui.util.GenericUtils;
import ar.com.textillevel.gui.util.componentes.JDialogBusquedaAndSeleccionItems;
import ar.com.textillevel.gui.util.controles.LinkableLabel;
import ar.com.textillevel.gui.util.controles.PanelDatePicker;
import ar.com.textillevel.gui.util.controles.intellisense.JDialogIntellisense;
import ar.com.textillevel.gui.util.controles.intellisense.ValorSeleccionadoData;
import ar.com.textillevel.gui.util.controles.intellisense.ValorSeleccionadoListener;
import ar.com.textillevel.gui.util.dialogs.JDialogSeleccionarCliente;
import ar.com.textillevel.gui.util.dialogs.JDialogSeleccionarCliente.EModoDialogo;
import ar.com.textillevel.util.GTLBeanFactory;

public class JDialogAgregarCheque extends JDialog {

	private static final long serialVersionUID = 2011134895322286079L;

	private JTextField txtNroCuenta;
	private JTextField txtBanco;
	private PanelDatePicker fechaDeposito;
	private PanelDatePicker fechaEntrada;
	private FWJTextField txtImporteCheque;
	private FWJTextField txtNroCliente;
	private FWJTextField txtNroCheque;
	private FWJTextField txtNumeracionCheque;
	private JComboBox cmbCapitalOInterior;
	
	private LinkableLabel lblElegirCliente;
	private Frame padre;
	private JButton btnGrabar;
	private JButton btnSalir;
	private JButton btnAceptarAgregar;
	private JPanel panelBotones;
	private JPanel panelGeneral;
	private JPanel panCliente;
	private JPanel panBanco;
	private JFormattedTextField txtCUIT;
	private ComboBusquedaClienteByCodigo comboBusquedaCliente;
	private ComboBusquedaBanco comboBusquedaBanco;
	private JDialogIntellisense dialogIntellisense;
	private LinkableLabel lblElegirBanco;
	private JButton btnEscanearCheque;
	
	private Cheque cheque;
	private boolean acepto;
	private boolean consulta;
	private Cliente cliente;
	private Banco banco;
	private BancoFacadeRemote bancoFacade;
	private ChequeFacadeRemote chequeFacade;
	private ClienteFacadeRemote clienteFacade;
	private boolean modificacion;
	private boolean agregaOtro;
	private boolean paraAgregar;
	private Set<String> cuits = getClienteFacade().getCuits();

	public JDialogAgregarCheque(Frame padre) {
		super(padre);
		setPadre(padre);
		setUpComponentes();
		setCheque(new Cheque());
		if(getCheque().getNumeracion()==null){
			setNumeracionCheque();
			getTxtNumeracionCheque().setText(""+getCheque().getNumeracion().getLetra()+getCheque().getNumeracion().getNumero());
		}
		setUpScreen();
	}

	public JDialogAgregarCheque(Frame padre, Cliente cliente) {
		super(padre);
		setPadre(padre);
		setUpComponentes();
		Cheque cheque2 = new Cheque();
		cheque2.setCliente(cliente);
		setCheque(cheque2);
		setCliente(cliente);
		getLblelegirCliente().setVisible(false);
		getLblelegirBanco().setVisible(false);		
		getTxtNroCliente().setText(String.valueOf(cliente.getNroCliente()));
		if(getCheque().getNumeracion()==null){
			setNumeracionCheque();
			getTxtNumeracionCheque().setText(""+getCheque().getNumeracion().getLetra()+getCheque().getNumeracion().getNumero());
		}
		if(getCliente().getNroCliente().equals(1)){
			GuiUtil.setEstadoPanel(getPanelElegirCliente(),false);
		}
		setUpScreen();
	}

	public JDialogAgregarCheque(Frame padre, Cheque cheque, boolean isConsulta, boolean paraAgregar) {
		super(padre);
		setCheque(cheque);
		setParaAgregar(paraAgregar);
		setConsulta(isConsulta);
		if(!isConsulta){
			setModificacion(true);
		}
		setPadre(padre);
		setUpComponentes();
		if(isConsulta){
			GuiUtil.setEstadoPanel(getPanelGeneral(), false);
			getLblelegirCliente().setVisible(false);
			getLblelegirBanco().setVisible(false);
			getBtnGrabar().setEnabled(false);
		}
		if(isParaAgregar()){
			setNumeracionCheque();
			getTxtNumeracionCheque().setText(""+getCheque().getNumeracion().getLetra()+getCheque().getNumeracion().getNumero());
			getTxtImporteCheque().setText("");
			getTxtNroCheque().setText("");
			getTxtBanco().setText("");
			getTxtCUIT().setText("");
			getCheque().setId(null);
		}
		setCliente(getCheque().getCliente());
		setUpScreen();
	}
	
	public JDialogAgregarCheque(Frame padre, Cheque cheque, boolean isConsulta, boolean paraAgregar, Cliente cliente) {
		super(padre);
		setCheque(cheque);
		setParaAgregar(paraAgregar);
		setConsulta(isConsulta);
		if(!isConsulta){
			setModificacion(true);
		}
		setPadre(padre);
		setUpComponentes();
		if(isConsulta){
			GuiUtil.setEstadoPanel(getPanelGeneral(), false);
			getLblelegirCliente().setVisible(false);
			getLblelegirBanco().setVisible(false);
			getBtnGrabar().setEnabled(false);
		}
		if(isParaAgregar()){
			setNumeracionCheque();
			getTxtNumeracionCheque().setText(""+getCheque().getNumeracion().getLetra()+getCheque().getNumeracion().getNumero());
			getTxtImporteCheque().setText("");
			getTxtNroCheque().setText("");
			getTxtCUIT().setText("");
			getCheque().setId(null);
		}
		setCliente(cliente);
		if(getCliente().getNroCliente().equals(1)){
			getLblelegirCliente().setVisible(false);
			GuiUtil.setEstadoPanel(getPanelElegirCliente(),false);
		}
		setUpScreen();
	}

	public JDialogAgregarCheque(Frame padre, Cliente cliente, boolean noPermitirElegirCliente) {
		super(padre);
		setPadre(padre);
		setUpComponentes();
		Cheque cheque2 = new Cheque();
		cheque2.setCliente(cliente);
		setCheque(cheque2);
		setCliente(cliente);
		getLblelegirCliente().setVisible(false);
		getLblelegirBanco().setVisible(false);		
		getTxtNroCliente().setText(String.valueOf(cliente.getNroCliente()));
		if(getCheque().getNumeracion()==null){
			setNumeracionCheque();
			getTxtNumeracionCheque().setText(""+getCheque().getNumeracion().getLetra()+getCheque().getNumeracion().getNumero());
		}
		if(noPermitirElegirCliente) {
			restringirCliente(cliente);
		}
		setUpScreen();
	}

	private void restringirCliente(Cliente cliente2) {
		setCliente(cliente2);
		getTxtNroCliente().setText(String.valueOf(cheque.getCliente().getNroCliente()));
		getLblelegirCliente().setVisible(false);
		GuiUtil.setEstadoPanel(getComboBusquedaCliente(), false);
	}
	
	public JDialogAgregarCheque(Frame padre, Cheque cheque, boolean isConsulta, boolean paraAgregar, boolean noPermitirElegirCliente) {
		super(padre);
		setCheque(cheque);
		setParaAgregar(paraAgregar);
		setConsulta(isConsulta);
		setModificacion(!isConsulta && !paraAgregar);
		setPadre(padre);
		setUpComponentes();
		if(isConsulta){
			GuiUtil.setEstadoPanel(getPanelGeneral(), false);
			getLblelegirCliente().setVisible(false);
			getLblelegirBanco().setVisible(false);
			getBtnGrabar().setEnabled(false);
		}
		if(isParaAgregar()){
			setNumeracionCheque();
			getTxtNumeracionCheque().setText(""+getCheque().getNumeracion().getLetra()+getCheque().getNumeracion().getNumero());
			getTxtImporteCheque().setText("");
			getTxtNroCheque().setText("");
			getTxtCUIT().setText("");
			getCheque().setId(null);
		}
		setCliente(getCheque().getCliente());
		if(noPermitirElegirCliente) {
			restringirCliente(cheque.getCliente());		
		}
		setUpScreen();
	}

	private void setUpScreen() {
		setSize(new Dimension(590, 460));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("Alta de cheque");
		GuiUtil.centrar(this);
		setResizable(true);
		setModal(true);
		setVisible(true);
	}

	private void setUpComponentes() {
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				salir();
			}
		});
		
		add(getPanelGeneral(), BorderLayout.CENTER);
		add(getPanelBotones(),BorderLayout.SOUTH);
	}

	private JPanel getPanelBotones(){
		if(panelBotones == null){
			panelBotones = new JPanel();
			panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
			panelBotones.add(getBtnGrabar());
			if((!isConsulta() && !isModificacion()) || isParaAgregar()){
				panelBotones.add(getBtnAceptarAgregar());
			}
			panelBotones.add(getBtnSalir());
		}
		return panelBotones;
	}
	
	private JPanel getPanelGeneral(){
		if(panelGeneral == null){
			panelGeneral = new JPanel();
			panelGeneral.setLayout(new GridBagLayout());
			panelGeneral.add(new JLabel("N�mero interno: "), GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 5), 1, 1, 0, 0));
			panelGeneral.add(getTxtNumeracionCheque(),  GenericUtils.createGridBagConstraints(1, 0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 5), 2, 1, 1, 0));
			panelGeneral.add(getFechaEntrada(),  GenericUtils.createGridBagConstraints(0, 1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 2, 1, 1, 0));
			panelGeneral.add(getBtnEscanearCheque(),  GenericUtils.createGridBagConstraints(2, 1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 5, 5), 1, 1, 1, 0));
			panelGeneral.add(getPanelElegirCliente(), GenericUtils.createGridBagConstraints(0, 2,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 4, 1, 1, 0));
			
			panelGeneral.add(getPanelElegirBanco(),  GenericUtils.createGridBagConstraints(0, 3,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 4, 1, 1, 0));
			
			/*
			panelGeneral.add(new JLabel("Banco: "),  GenericUtils.createGridBagConstraints(0, 3,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelGeneral.add(getTxtBanco(), GenericUtils.createGridBagConstraints(1, 3,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panelGeneral.add(getComboBusquedaBanco(), GenericUtils.createGridBagConstraints(2, 3,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			*/
			
			
			panelGeneral.add(new JLabel("CUIT/DNI: "), GenericUtils.createGridBagConstraints(0, 4,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelGeneral.add(getTxtCUIT(),  GenericUtils.createGridBagConstraints(1, 4,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panelGeneral.add(new JLabel("N�mero: "), GenericUtils.createGridBagConstraints(0, 5,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelGeneral.add(getTxtNroCheque(),  GenericUtils.createGridBagConstraints(1, 5,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panelGeneral.add(new JLabel("Cuenta: "), GenericUtils.createGridBagConstraints(0, 6,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelGeneral.add(getTxtNroCuenta(),  GenericUtils.createGridBagConstraints(1, 6,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panelGeneral.add(getFechaDeposito(),  GenericUtils.createGridBagConstraints(0, 7,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 5), 4, 1, 1, 0));
			panelGeneral.add(new JLabel("Importe: "), GenericUtils.createGridBagConstraints(0, 8,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelGeneral.add(getTxtImporteCheque(),  GenericUtils.createGridBagConstraints(1, 8,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 1, 0));
			panelGeneral.add(new JLabel("Capital o interior: "), GenericUtils.createGridBagConstraints(0, 9,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 1, 1, 0, 0));
			panelGeneral.add(getCmbCapitalOInterior(),  GenericUtils.createGridBagConstraints(1, 9,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 1, 1, 0.1, 0));
		}
		return panelGeneral;
	}
	
	private JPanel getPanelElegirCliente(){
		if(panCliente == null){
			panCliente = new JPanel();
			panCliente.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
			panCliente.add(new JLabel("Cliente N�: "));
			panCliente.add(getTxtNroCliente());
			panCliente.add(getComboBusquedaCliente());
			panCliente.add(getLblelegirCliente());
		}
		return panCliente;
	}
	
	private JPanel getPanelElegirBanco(){
		if(panBanco == null){
			panBanco = new JPanel();
			panBanco.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
			panBanco.add(new JLabel("Banco: "));
			panBanco.add(getTxtBanco());
			panBanco.add(getComboBusquedaBanco());
			panBanco.add(getLblelegirBanco());
		}
		return panBanco;
	}

	
	public Cheque getCheque() {
		return cheque;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public boolean isAcepto() {
		return acepto;
	}

	public void setAcepto(boolean acepto) {
		this.acepto = acepto;
	}

	private JTextField getTxtBanco() {
		if (txtBanco == null) {
			txtBanco = new JTextField();
			txtBanco.setEditable(false);
			txtBanco.setPreferredSize(new Dimension(160, 20));
			if(getCheque()!=null && getCheque().getBanco() != null){
				txtBanco.setText(getCheque().getBanco().getNombre());
				this.banco = getCheque().getBanco();
			}
		}
		return txtBanco;
	}

	private JButton getBtnEscanearCheque() {
		if (btnEscanearCheque == null) {
			btnEscanearCheque = new JButton("     Escanear cheque", ImageUtil.loadIcon("ar/com/textillevel/imagenes/b_buscar.png"));
			btnEscanearCheque.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JDialogEscanearCheque d = new JDialogEscanearCheque(JDialogAgregarCheque.this);
					if(d.isAcepto()) {
						String codigoEscaneado = d.getCodigoEscaneado();
						getComboBusquedaBanco().buscar(InterpreteCodigoCheque.getCodigoBanco(codigoEscaneado));
						getTxtNroCheque().setText(InterpreteCodigoCheque.getNroCheque(codigoEscaneado));
						getTxtNroCuenta().setText(InterpreteCodigoCheque.getNroCuenta(codigoEscaneado));
						getComboBusquedaCliente().foco();
					}
				}
			});
		}
		return btnEscanearCheque;
	}

	public JTextField getTxtNroCuenta() {
		if (txtNroCuenta == null) {
			txtNroCuenta = new JTextField();
			if(getCheque()!=null && getCheque().getNroCuenta() != null) {
				txtNroCuenta.setText(getCheque().getNroCuenta());
			}
		}
		return txtNroCuenta;
	}
	
	private BancoFacadeRemote getBancoFacade() {
		if (bancoFacade == null) {
			bancoFacade = GTLBeanFactory.getInstance().getBean2(BancoFacadeRemote.class);
		}
		return bancoFacade;
	}

	private PanelDatePicker getFechaDeposito() {
		if (fechaDeposito == null) {
			fechaDeposito = new PanelDatePicker();
			fechaDeposito.setCaption("Fecha deposito:");
			if(getCheque()!=null){
				fechaDeposito.setSelectedDate(getCheque().getFechaDeposito());
			}
		}
		return fechaDeposito;
	}

	private FWJTextField getTxtImporteCheque() {
		if (txtImporteCheque == null) {
			txtImporteCheque = new FWJTextField();
			if(getCheque() != null){
				txtImporteCheque.setText(String.valueOf(getCheque().getImporte().doubleValue()));
			}
		}
		return txtImporteCheque;
	}

	private LinkableLabel getLblelegirCliente() {
		if (lblElegirCliente == null) {
			lblElegirCliente = new LinkableLabel("Elegir cliente") {

				private static final long serialVersionUID = 580819185565135378L;

				@Override
				public void labelClickeada(MouseEvent e) {
					if (e.getClickCount() == 1) {
						JDialogSeleccionarCliente dialogSeleccionarCliente = new JDialogSeleccionarCliente(getPadre(), EModoDialogo.MODO_ID);
						GuiUtil.centrar(dialogSeleccionarCliente);
						dialogSeleccionarCliente.setVisible(true);
						Cliente clienteElegido = dialogSeleccionarCliente.getCliente();
						if (clienteElegido != null) {
							setCliente(clienteElegido);
							getTxtNroCliente().setText(String.valueOf(clienteElegido.getNroCliente()));
							getComboBusquedaCliente().reset();
						}
					}
				}
			};
		}
		return lblElegirCliente;
	}

	private LinkableLabel getLblelegirBanco() {
		if (lblElegirBanco == null) {
			lblElegirBanco = new LinkableLabel("Elegir Banco") {

				private static final long serialVersionUID = 580819185565135378L;

				@Override
				public void labelClickeada(MouseEvent e) {
					if (e.getClickCount() == 1) {
						JDialogBusquedaAndSeleccionItems<Banco> jdialog = new JDialogBusquedaAndSeleccionItems<Banco>(padre, "Elegir Bancos", "Banco", getBancoFacade().getAllOrderByName()) {

							private static final long serialVersionUID = 1L;
							
						};
						jdialog.setVisible(true);
						Banco bancoElegido = jdialog.getSelectedItem();
						if(bancoElegido != null) {
							banco = bancoElegido;
							getTxtBanco().setText(bancoElegido.getNombre());
							getComboBusquedaBanco().reset();
						}
					}
				}
			};
		}
		return lblElegirBanco;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Frame getPadre() {
		return padre;
	}

	public void setPadre(Frame padre) {
		this.padre = padre;
	}

	private FWJTextField getTxtNroCliente() {
		if (txtNroCliente == null) {
			txtNroCliente = new FWJTextField();
			txtNroCliente.setEditable(false);
			txtNroCliente.setPreferredSize(new Dimension(160, 20));
			if(getCheque()!=null){
				txtNroCliente.setText(String.valueOf(getCheque().getCliente().getNroCliente()));
			}
		}
		return txtNroCliente;
	}
	
	private JFormattedTextField getTxtCUIT() {
		if(txtCUIT == null){
			try {
				txtCUIT = new JFormattedTextField(new MaskFormatter("##-########-#"));
				txtCUIT.setFocusLostBehavior(JFormattedTextField.PERSIST);
				dialogIntellisense = new JDialogIntellisense(JDialogAgregarCheque.this);

				dialogIntellisense.addValorSeleccionadoActionListener(new ValorSeleccionadoListener() {
					public void onSelectedValue(ValorSeleccionadoData event) {
						txtCUIT.setValue(event.getValor());
						try {
							txtCUIT.commitEdit();
						} catch (ParseException e1) {
						}
						dialogIntellisense.setVisible(false);
					}
				});
				txtCUIT.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						try {
							txtCUIT.commitEdit();
						} catch (ParseException e1) {}
					}
				});
				txtCUIT.addKeyListener(new KeyAdapter() {

					@Override
					public void keyReleased(KeyEvent e) {
						if(Character.isDigit(e.getKeyChar()) || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
							List<String> cuits = getCuitsCandidatos();
							if(cuits.size() == 1 && txtCUIT.getText().trim().length() == 13) {
								dialogIntellisense.setVisible(false);
							} else {
								dialogIntellisense.ubicar(txtCUIT, cuits.size());
								dialogIntellisense.displaySugerencias(cuits);
								dialogIntellisense.setVisible(true);
								txtCUIT.requestFocus();
								txtCUIT.setCaretPosition(calcularPosicionCursor(txtCUIT.getText()));
								dialogIntellisense.resetSeleccion();
							}
						} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
							dialogIntellisense.updateSelectedLabel();
						}
					}

					private List<String> getCuitsCandidatos() {
						List<String> cuits = new ArrayList<String>();
						for (String c : getCuits()) {
							if (c.replaceAll("-", "").startsWith(txtCUIT.getText().replace("-", "").trim())) {
								cuits.add(c);
							}
						}
						return cuits;
					}

					private int calcularPosicionCursor(String texto) {
						int textoSinGuiones = texto.replaceAll("-", "").trim().length();
						if(textoSinGuiones <2) {
							return textoSinGuiones;
						}else if(textoSinGuiones >= 2 && textoSinGuiones<11) {
							return textoSinGuiones + 1;
						} else {
							return textoSinGuiones + 2;
						}
					}

				});

				if(getCheque()!=null){
					txtCUIT.setText(getCheque().getCuit());
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return txtCUIT;
	}

	private JButton getBtnGrabar() {
		if (btnGrabar == null) {
			btnGrabar = new JButton("Grabar");
			btnGrabar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					grabarCheque(false);
				}
			});
		}
		return btnGrabar;
	}

	private void grabarCheque(boolean agregaOtro) {
		if(validar()){
			capturarDatos();
			setAcepto(true);
			setAgregaOtro(agregaOtro);
			dispose();
		}
	}

	private boolean validar() {
		if(getCliente() == null){
			FWJOptionPane.showErrorMessage(this, "Debe seleccionar a un cliente", "Error");
			getLblelegirCliente().requestFocus();
			return false;
		}
		
		if(banco == null) {
			FWJOptionPane.showErrorMessage(this, "Debe elegir un Banco.", "Error");
			return false;
		}
		
		if(getTxtCUIT().getText().trim().length() < 13) {
			FWJOptionPane.showErrorMessage(this, "Debe completar el CUIT.","Error");
			getTxtCUIT().requestFocus();
			return false;
		}
		
		if(getTxtNroCheque().getText().trim().length() == 0){
			FWJOptionPane.showErrorMessage(this, "Debe ingresar el n�mero de cheque", "Error");
			getTxtNroCheque().requestFocus();
			return false;
		}

		if(!GenericUtils.esNumerico(getTxtNroCheque().getText())){
			FWJOptionPane.showErrorMessage(this, "El n�mero de cheque no puede tener letras.","Error");
			getTxtNroCheque().requestFocus();
			return false;
		}
		
		if(getTxtNroCuenta().getText().trim().length() == 0){
			FWJOptionPane.showErrorMessage(this, "Debe ingresar el n�mero de cuenta", "Error");
			getTxtNroCuenta().requestFocus();
			return false;
		}
		
		if(getFechaDeposito().getDate()==null){
			FWJOptionPane.showErrorMessage(this, "Debe ingresar la fecha de deposito", "Error");
			getFechaDeposito().requestFocus();
			return false;
		}
		
		if(getTxtImporteCheque().getText().trim().length()==0){
			FWJOptionPane.showErrorMessage(this, "Debe ingresar el importe", "Error");
			getTxtImporteCheque().requestFocus();
			return false;
		}
		
		if(!GenericUtils.esNumerico(getTxtImporteCheque().getText())){
			FWJOptionPane.showErrorMessage(this, "El importe de cheque no puede tener letras.","Error");
			getTxtImporteCheque().requestFocus();
			return false;
		}
		
		if(getFechaEntrada().getDate()==null){
			FWJOptionPane.showErrorMessage(this, "Debe ingresar la fecha de entrada", "Error");
			getFechaEntrada().requestFocus();
			return false;
		}
		
		Date fEntrada = getFechaEntrada().getDate();
		Date fDeposito = getFechaDeposito().getDate();
		if(!fEntrada.before(DateUtil.sumarDias(fDeposito, 30))){
			FWJOptionPane.showErrorMessage(this, "La fecha de entrada debe ser menor al menos en 30 d�as a la fecha de deposito", "Error");
			getFechaEntrada().requestFocus();
			return false;
		}

		if( (isModificacion() == false || isParaAgregar()) && getChequeFacade().getChequeByNumeroCuitYBanco(getTxtNroCheque().getText().trim(),
				getTxtCUIT().getText().trim(), banco)!=null){
			FWJOptionPane.showErrorMessage(this, "Ya existe un cheque con el n�mero ingresado", "Error");
			getTxtNroCheque().requestFocus();
			return false;
		}
		return true;
	}

	private void capturarDatos() {
		getCheque().setBanco(banco);
		getCheque().setEstadoCheque(EEstadoCheque.PENDIENTE_COBRAR);
		getCheque().setFechaDeposito(new java.sql.Date(getFechaDeposito().getDate().getTime()));
		getCheque().setImporte(new BigDecimal(getTxtImporteCheque().getText().trim().replace(',','.')));
		getCheque().setNumero(getTxtNroCheque().getText().trim());
		getCheque().setCliente(getCliente());
		getCheque().setCuit(getTxtCUIT().getText().trim());
		getCheque().setFechaEntrada(new java.sql.Date(getFechaEntrada().getDate().getTime()));
		getCheque().setCapitalOInterior((Character)getCmbCapitalOInterior().getSelectedItem());
		getCheque().setNroCuenta(getTxtNroCuenta().getText().trim());
	}

	private JButton getBtnSalir() {
		if (btnSalir == null) {
			btnSalir = new JButton("Salir");
			btnSalir.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					salir();
				}
			});
		}
		return btnSalir;
	}

	private void salir() {
		if(!isConsulta()){
			int ret = FWJOptionPane.showQuestionMessage(this, "Va a salir sin grabar, desea continuar?", "Alta de cheque");
			if (ret == FWJOptionPane.YES_OPTION) {
				setAcepto(false);
				dispose();
			}
		}else{
			dispose();
		}
	}

	private FWJTextField getTxtNroCheque() {
		if (txtNroCheque == null) {
			txtNroCheque = new FWJTextField();
			if(getCheque()!=null){
				txtNroCheque.setText(getCheque().getNumero());
			}
		}
		return txtNroCheque;
	}
	
	private ChequeFacadeRemote getChequeFacade(){
		if(chequeFacade == null){
			chequeFacade = GTLBeanFactory.getInstance().getBean2(ChequeFacadeRemote.class);
		}
		return chequeFacade;
	}

	private FWJTextField getTxtNumeracionCheque() {
		if (txtNumeracionCheque == null) {
			txtNumeracionCheque = new FWJTextField();
			txtNumeracionCheque.setEditable(false);
			txtNumeracionCheque.setPreferredSize(new Dimension(100, 20));
			if(getCheque()!=null){
				getTxtNumeracionCheque().setText(""+getCheque().getNumeracion().getLetra()+getCheque().getNumeracion().getNumero());
			}
		}
		return txtNumeracionCheque;
	}

	private PanelDatePicker getFechaEntrada() {
		if (fechaEntrada == null) {
			fechaEntrada = new PanelDatePicker();
			fechaEntrada.setCaption("Fecha entrada:");
			if(getCheque()!=null){
				fechaEntrada.setSelectedDate(getCheque().getFechaEntrada());
			}
		}
		return fechaEntrada;
	}

	private JComboBox getCmbCapitalOInterior() {
		if(cmbCapitalOInterior == null){
			cmbCapitalOInterior = new JComboBox();
			cmbCapitalOInterior.addItem(new Character('C'));
			cmbCapitalOInterior.addItem(new Character('I'));
			if(getCheque()!=null){
				cmbCapitalOInterior.setSelectedItem(getCheque().getCapitalOInterior());
			}
		}
		return cmbCapitalOInterior;
	}
	
	private void setNumeracionCheque() {
		ParametrosGeneralesFacadeRemote pgr = GTLBeanFactory.getInstance().getBean2(ParametrosGeneralesFacadeRemote.class);
		NumeracionCheque nc = new NumeracionCheque();
		ParametrosGenerales parametrosGenerales = pgr.getParametrosGenerales();
		if(parametrosGenerales==null){
			FWJOptionPane.showErrorMessage(this, "Faltan configurar los parametros generales", "Error");
			dispose();
		}
		Character letra = parametrosGenerales.getNumeracionCheque().getLetra();
		Integer proximoNumeroInternoCheque = getChequeFacade().getUltimoNumeroInternoCheque(letra);
		nc.setLetra(letra);
		nc.setNumero(proximoNumeroInternoCheque==null?parametrosGenerales.getNumeracionCheque().getNumero():proximoNumeroInternoCheque+1);
		getCheque().setNumeracion(nc);
	}

	
	public boolean isConsulta() {
		return consulta;
	}

	
	public void setConsulta(boolean consulta) {
		this.consulta = consulta;
	}

	
	public boolean isModificacion() {
		return modificacion;
	}

	
	public void setModificacion(boolean modificacion) {
		this.modificacion = modificacion;
	}

	public JButton getBtnAceptarAgregar() {
		if(btnAceptarAgregar==null){
			btnAceptarAgregar = new JButton("Aceptar/Agregar otro");
			btnAceptarAgregar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					grabarCheque(true);
				}
			});
		}
		return btnAceptarAgregar;
	}
	
	private class ComboBusquedaClienteByCodigo extends FWTxtComboBoxBusqueda<Cliente> {

		private static final long serialVersionUID = -8069636605971687535L;

		public void foco() {
			searchField.requestFocus();
		}
		
		@Override
		protected List<Cliente> buscar(String text) {
			List<Cliente> clientes = new ArrayList<Cliente>();
			try {
				Cliente cliente = getClienteFacade().getClienteByNumero(Integer.valueOf(text));
				if(cliente!=null){
					setCliente(cliente);
					getTxtNroCliente().setText(String.valueOf(cliente.getNroCliente()));
					clientes.add(cliente);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return clientes;
		}

		@Override
		protected boolean realizarBusqueda(String text) {
			if (StringUtil.isNullOrEmptyString(text)) {
				return false;
			}
			if (!NumUtil.esNumerico(text)) {
				FWJOptionPane.showWarningMessage(this, StringW.wordWrap("Debe ingresar s�lo n�meros"), "Error");
				return false;
			}
			return true;
		}

		@Override
		public void noHayResultado() {
			FWJOptionPane.showInformationMessage(this, "No se encontraron resultados para la b�squeda.", "Informaci�n");
			setCliente(null);
			getTxtNroCliente().setText("");
		}
	}

	private class ComboBusquedaBanco extends FWTxtComboBoxBusqueda<Banco> {

		private static final long serialVersionUID = 1L;

		public void buscar(int codigoBanco) {
			Banco banco = getBancoFacade().getBancoByCodigo(codigoBanco);
			if(banco!=null){
				searchField.setText(String.valueOf(codigoBanco));
				JDialogAgregarCheque.this.banco = banco;
				getTxtBanco().setText(banco.getNombre());
			}
		}
		
		@Override
		protected List<Banco> buscar(String text) throws FWException {
			List<Banco> bancos = new ArrayList<Banco>();
			Banco banco = getBancoFacade().getBancoByCodigo(Integer.valueOf(text));
			if(banco!=null){
				JDialogAgregarCheque.this.banco = banco;
				getTxtBanco().setText(banco.getNombre());
				bancos.add(banco);
			}
			return bancos;
		}

		@Override
		protected boolean realizarBusqueda(String text) {
			if (StringUtil.isNullOrEmptyString(text)) {
				return false;
			}
			if (!NumUtil.esNumerico(text)) {
				FWJOptionPane.showWarningMessage(this, StringW.wordWrap("Debe ingresar s�lo n�meros"), "Error");
				return false;
			}
			return true;
		}

		@Override
		public void noHayResultado() {
			FWJOptionPane.showInformationMessage(this, "No se encontraron resultados para la b�squeda.", "Informaci�n");
			getTxtBanco().setText("");
			JDialogAgregarCheque.this.banco = null;			
		}

	}

	public boolean isAgregaOtro() {
		return agregaOtro;
	}

	public void setAgregaOtro(boolean agregaOtro) {
		this.agregaOtro = agregaOtro;
	}

	public boolean isParaAgregar() {
		return paraAgregar;
	}

	public void setParaAgregar(boolean paraAgregar) {
		this.paraAgregar = paraAgregar;
	}
	
	private ClienteFacadeRemote getClienteFacade() {
		if(clienteFacade == null){
			clienteFacade = GTLBeanFactory.getInstance().getBean2(ClienteFacadeRemote.class);
		}
		return clienteFacade;
	}
	
	private ComboBusquedaClienteByCodigo getComboBusquedaCliente() {
		if(comboBusquedaCliente == null){
			comboBusquedaCliente = new ComboBusquedaClienteByCodigo();
		}
		return comboBusquedaCliente;
	}

	private ComboBusquedaBanco getComboBusquedaBanco() {
		if(comboBusquedaBanco == null) {
			comboBusquedaBanco = new ComboBusquedaBanco();
		}
		return comboBusquedaBanco; 
	}

	private Set<String> getCuits() {
		return cuits;
	}

	private static class InterpreteCodigoCheque {
		
		public static int getCodigoBanco(String codigo) {
			return Integer.valueOf(codigo.substring(0, 3));
		}

		public static int getSucursal(String codigo) {
			return Integer.valueOf(codigo.substring(3, 6));
		}

		public static int getCodigoPostal(String codigo) {
			return Integer.valueOf(codigo.substring(6,10));
		}

		public static String getNroCheque(String codigo) {
			return codigo.substring(10, 18);
		}

		public static String getNroCuenta(String codigo) {
			return codigo.substring(18);
		}
	}
}