package ar.com.textillevel.gui.modulos.remitoentrada.cabecera;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ar.com.fwcommon.componentes.FWJNumericTextField;
import ar.com.fwcommon.templates.modulo.cabecera.Cabecera;
import ar.com.fwcommon.util.DateUtil;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.textillevel.entidades.documentos.remito.enums.ESituacionODTRE;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.entidades.ventas.productos.Producto;
import ar.com.textillevel.facade.api.remote.ProductoFacadeRemote;
import ar.com.textillevel.gui.util.GenericUtils;
import ar.com.textillevel.gui.util.controles.PanelDatePicker;
import ar.com.textillevel.gui.util.panels.PanSelectorEntityCliente;
import ar.com.textillevel.util.GTLBeanFactory;

public class CabeceraRemitoEntrada extends Cabecera<ModeloCabeceraRemitoEntrada> {

	private static final long serialVersionUID = 4798710431798792144L;

	private ModeloCabeceraRemitoEntrada model;
	private PanelDatePicker panelFechaDesde;
	private PanelDatePicker panelFechaHasta;
	
	private JRadioButton rbtBuscarRemitoPorFiltros;
	private JRadioButton rbtBuscarRemitoPorNro;

	private JPanel panFiltros;
	private PanSelectorEntityCliente panSelectorCliente;
	
	private JPanel panProductos; 
	private JComboBox cmbProductos;
	private JComboBox cmbSituacionODT;
	private FWJNumericTextField txtNroRemito;
	private ProductoFacadeRemote productoFacade;
	
	
	private ProductoFacadeRemote getProductoFacade() {
		if(productoFacade == null) {
			productoFacade = GTLBeanFactory.getInstance().getBean2(ProductoFacadeRemote.class);
		}
		return productoFacade;
	}

	public CabeceraRemitoEntrada() {
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("B�squeda"));
		GridBagConstraints gc = GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 1, 1, 1, 0.5);
		add(getPanFiltros(), gc);
		gc = GenericUtils.createGridBagConstraints(0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,5,5,5), 1, 1, 0, 0.5);
		add(getPanBusquedaPorNro(), gc);
		ButtonGroup bgOpcionProceso = new ButtonGroup();
		bgOpcionProceso.add(getRbtBuscarRemitoPorFiltros());
		bgOpcionProceso.add(getRbtBuscarRemitoPorNro());
	}

	private JPanel getPanFiltros() {
		if(panFiltros == null) {
			panFiltros = new JPanel();
			panFiltros.setLayout(new GridBagLayout());
			GridBagConstraints gc = GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 1, 1, 0, 0);
			panFiltros.add(getRbtBuscarRemitoPorFiltros(), gc);
			gc = GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 1, 1, 0, 0);
			panFiltros.add(getPanBusquedaFecha(), gc);
			gc = GenericUtils.createGridBagConstraints(2, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1, 1, 1, 1);
			panFiltros.add(getPanSelectorCliente(), gc);
			gc = GenericUtils.createGridBagConstraints(3, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 1, 1, 0, 0);
			panFiltros.add(getPanProductos(), gc);
		}
		return panFiltros;
	}

	private JPanel getPanProductos() {
		if(panProductos == null) {
			panProductos = new JPanel();
			panProductos.setLayout(new FlowLayout(FlowLayout.CENTER,5,2));
			panProductos.add(new JLabel("PRODUCTO:"));
			panProductos.add(getCmbProductos());
			panProductos.add(new JLabel("ODT:"));
			panProductos.add(getCmbSituacionODT());
			
		}
		return panProductos;
	}
	
	private JComboBox getCmbProductos() {
		if(cmbProductos == null) {
			cmbProductos = new JComboBox();
			GuiUtil.llenarCombo(cmbProductos, getProductoFacade().getAllOrderByName(), true);
			cmbProductos.insertItemAt("TODOS", 0);
			cmbProductos.setSelectedIndex(0);
			cmbProductos.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						notificar();
					}
				}
			});
			
		}
		return cmbProductos;
	}

	private JComboBox getCmbSituacionODT() {
		if(cmbSituacionODT == null) {
			cmbSituacionODT = new JComboBox();
			GuiUtil.llenarCombo(cmbSituacionODT, Arrays.asList(ESituacionODTRE.values()), true);
			cmbSituacionODT.insertItemAt("TODOS", 0);
			cmbSituacionODT.setSelectedIndex(0);
			cmbSituacionODT.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						notificar();
					}
				}
			});
			
		}
		return cmbSituacionODT;
	}

	@Override
	public ModeloCabeceraRemitoEntrada getModel() {
		if (model == null) {
			model = new ModeloCabeceraRemitoEntrada();
		}
		Date fechaDesde = getPanelFechaDesde().getDate();
		Date fechaHasta = getPanelFechaHasta().getDate();
		model.setFechaDesde(fechaDesde!=null?new java.sql.Date(fechaDesde.getTime()):null);
		model.setFechaHasta(fechaHasta!=null?DateUtil.getManiana(new java.sql.Date(fechaHasta.getTime())):null);
		if(getCmbProductos().getSelectedIndex() == 0) {
			model.setProducto(null);
		} else {
			model.setProducto((Producto)getCmbProductos().getSelectedItem());
		}
		if(getCmbSituacionODT().getSelectedIndex() == 0) {
			model.setSituacionODT(null);;
		} else {
			model.setSituacionODT((ESituacionODTRE)getCmbSituacionODT().getSelectedItem());
		}
		return model;
	}

	private PanelDatePicker getPanelFechaDesde() {
		if(panelFechaDesde == null){
			panelFechaDesde = new PanelDatePicker() {
				private static final long serialVersionUID = 1L;

				public void accionBotonCalendarioAdicional() {
					notificar();
				}
			};
			panelFechaDesde.setCaption("Fecha desde: ");
			panelFechaDesde.setSelectedDate(DateUtil.restarDias(DateUtil.getHoy(), 30));
		}
		return panelFechaDesde;
	}

	private PanelDatePicker getPanelFechaHasta() {
		if(panelFechaHasta == null){
			panelFechaHasta = new PanelDatePicker() {
				
				private static final long serialVersionUID = 1L;

				public void accionBotonCalendarioAdicional() {
					notificar();
				}
			};
			panelFechaHasta.setCaption("Fecha hasta: ");
			panelFechaHasta.setSelectedDate(DateUtil.getHoy());
		}
		return panelFechaHasta;
	}

	private JPanel getPanBusquedaFecha() {
		JPanel pan = new JPanel();
		pan.setLayout(new FlowLayout(FlowLayout.CENTER,5,2));
		pan.add(getPanelFechaDesde());
		pan.add(getPanelFechaHasta());
		return pan;
	}

	private PanSelectorEntityCliente getPanSelectorCliente() {
		if(panSelectorCliente == null) {
			
			panSelectorCliente = new PanSelectorEntityCliente() {

				private static final long serialVersionUID = 1L;

				@Override
				public void entitySelected(Cliente entity) {
					model.setCliente(entity);
					notificar();
				}
			
			};
		}
		return panSelectorCliente;
	}
	
	
	private JRadioButton getRbtBuscarRemitoPorFiltros() {
		if(rbtBuscarRemitoPorFiltros == null) {
			rbtBuscarRemitoPorFiltros = new JRadioButton("Por Filtros");
			rbtBuscarRemitoPorFiltros.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					setEnabledBusquedaPorFiltroComponents(true);
					setEnabledBusquedaPorNro(false);
					model.setBuscarPorFiltros(true);
					notificar();
				}

			});

			rbtBuscarRemitoPorFiltros.setSelected(true);
		}
		return rbtBuscarRemitoPorFiltros;
	}

	private JPanel getPanBusquedaPorNro() {
		JPanel pan = new JPanel();
		pan.setLayout(new FlowLayout(FlowLayout.CENTER,5,2));
		pan.add(getRbtBuscarRemitoPorNro());
		pan.add(getTxtNroRemito());
		return pan;
	}
	
	private JRadioButton getRbtBuscarRemitoPorNro() {
		if(rbtBuscarRemitoPorNro == null) {
			rbtBuscarRemitoPorNro = new JRadioButton("Por N�mero de Remito");
			rbtBuscarRemitoPorNro.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					setEnabledBusquedaPorFiltroComponents(false);
					setEnabledBusquedaPorNro(true);
					model.setBuscarPorFiltros(false);
					getPanSelectorCliente().clear();
					getTxtNroRemito().requestFocus();
					notificar();
				}

			});
			
		}
		return rbtBuscarRemitoPorNro;
	}

	private void setEnabledBusquedaPorNro(boolean enabled) {
		getTxtNroRemito().setEnabled(enabled);
	}

	private void setEnabledBusquedaPorFiltroComponents(boolean enabled) {
		getPanSelectorCliente().setEnabledOperations(enabled);
		getPanelFechaDesde().setEnabled(enabled);
		getPanelFechaHasta().setEnabled(enabled);
		getCmbProductos().setEnabled(enabled);
	}

	private FWJNumericTextField getTxtNroRemito() {
		if(txtNroRemito == null) {
			txtNroRemito = new FWJNumericTextField(0, Long.MAX_VALUE);
				
			txtNroRemito.addKeyListener(new KeyListener() {
				
				public void keyTyped(KeyEvent e) {
				}
				
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						Integer value = getTxtNroRemito().getValue();
						model.setNroRemito(value);
						notificar();
					}
				}
				
				public void keyPressed(KeyEvent e) {
				}

			});
			
			txtNroRemito.setPreferredSize(new Dimension(70, 20));
			txtNroRemito.setEnabled(false);
		}
		return txtNroRemito;
	}

}