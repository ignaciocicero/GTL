package ar.com.textillevel.gui.modulos.abm.listaprecios.tenido;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.taglibs.string.util.StringW;

import ar.clarin.fwjava.componentes.CLJOptionPane;
import ar.clarin.fwjava.util.GuiUtil;
import ar.com.textillevel.entidades.enums.ETipoProducto;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.entidades.ventas.articulos.GamaColorCliente;
import ar.com.textillevel.entidades.ventas.articulos.TipoArticulo;
import ar.com.textillevel.entidades.ventas.cotizacion.DefinicionPrecio;
import ar.com.textillevel.entidades.ventas.cotizacion.GrupoTipoArticuloGama;
import ar.com.textillevel.entidades.ventas.cotizacion.PrecioGama;
import ar.com.textillevel.entidades.ventas.cotizacion.RangoAncho;
import ar.com.textillevel.entidades.ventas.cotizacion.RangoAnchoArticuloTenido;
import ar.com.textillevel.facade.api.remote.GamaColorClienteFacadeRemote;
import ar.com.textillevel.gui.modulos.abm.listaprecios.JDialogAgregarModificarDefinicionPrecios;
import ar.com.textillevel.gui.modulos.abm.listaprecios.PanelTablaRango;
import ar.com.textillevel.gui.util.GenericUtils;
import ar.com.textillevel.gui.util.controles.LinkableLabel;
import ar.com.textillevel.util.GTLBeanFactory;

public class JDialogAgregarModificarDefinicionPreciosTenido extends JDialogAgregarModificarDefinicionPrecios<RangoAnchoArticuloTenido> {

	private static final long serialVersionUID = -6851805146971694269L;
	
	private JComboBox cmbGama;
	private LinkableLabel linkableLabelEditarGamaCliente;
	private JPanel panelDatosPropios;
	
	private List<GamaColorCliente> gamas;
	private GamaColorClienteFacadeRemote gamaClienteFacade;
	private PrecioGama precioGamaSiendoEditado;
	
	public JDialogAgregarModificarDefinicionPreciosTenido(Frame padre, Cliente cliente, ETipoProducto tipoProducto) {
		super(padre, cliente, tipoProducto);
		gamas = getGamaClienteFacade().getByCliente(getCliente().getId());
		if (gamas == null || gamas.isEmpty()) {
			CLJOptionPane.showWarningMessage(this, "El cliente no cuenta con gamas definidas. Debe ingresarlas.", "Advertencia");
			JDialogAgregarModificarGamaColorCliente d = new JDialogAgregarModificarGamaColorCliente(this, getCliente());
			d.setVisible(true);
			if (d.isAcepto()) {
				gamas = getGamaClienteFacade().getByCliente(getCliente().getId());
			} else {
				CLJOptionPane.showWarningMessage(this, StringW.wordWrap("No se puede dar de alta la lista de precios para te�ido si tener definidas las gamas cliente."), "Advertencia");
				setAcepto(false);
				dispose();
			}
		}
		GuiUtil.llenarCombo(getCmbGama(), getGamas(), true);
		setAcepto(true);
	}

	public JDialogAgregarModificarDefinicionPreciosTenido(Frame padre, Cliente cliente, ETipoProducto tipoProducto, DefinicionPrecio definicionAModificar) {
		this(padre, cliente, tipoProducto);
		super.setDefinicion(definicionAModificar);
	}
	
	@Override
	protected JPanel createPanelDatosEspecificos() {
		if (panelDatosPropios == null) {
			panelDatosPropios = new JPanel(new GridBagLayout());
			panelDatosPropios.add(new JLabel("Gama: "), GenericUtils.createGridBagConstraints(0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 1, 1, 0, 0));
			panelDatosPropios.add(getCmbGama(), GenericUtils.createGridBagConstraints(1, 0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1, 1, 1, 1));
			panelDatosPropios.add(getLinkableLabelEditarGamaCliente(), GenericUtils.createGridBagConstraints(2, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1, 1, 1, 1));
		}
		return panelDatosPropios;
	}

	public JComboBox getCmbGama() {
		if (cmbGama == null) {
			cmbGama = new JComboBox();
			GuiUtil.llenarCombo(cmbGama, getGamas(), true);
		}
		return cmbGama;
	}

	@Override
	protected PanelTablaRango<RangoAnchoArticuloTenido> createPanelTabla(JDialogAgregarModificarDefinicionPrecios<RangoAnchoArticuloTenido> parent) {
		return new PanelTablaRangoTenido(parent);
	}

	public GamaColorClienteFacadeRemote getGamaClienteFacade() {
		if (gamaClienteFacade == null) {
			gamaClienteFacade = GTLBeanFactory.getInstance().getBean2(GamaColorClienteFacadeRemote.class);
		}
		return gamaClienteFacade;
	}

	public List<GamaColorCliente> getGamas() {
		return gamas;
	}

	public LinkableLabel getLinkableLabelEditarGamaCliente() {
		if (linkableLabelEditarGamaCliente == null) {
			linkableLabelEditarGamaCliente = new LinkableLabel("Editar gama") {
				
				private static final long serialVersionUID = -1762575664503960563L;

				@Override
				public void labelClickeada(MouseEvent e) {
					if (getCmbGama().getSelectedItem() != null) {
						JDialogAgregarModificarGamaColorCliente d = new JDialogAgregarModificarGamaColorCliente(JDialogAgregarModificarDefinicionPreciosTenido.this, getCliente());
						d.setVisible(true);
						if (d.isAcepto()) {
							gamas = getGamaClienteFacade().getByCliente(getCliente().getId());
							getCmbGama().removeAllItems();
							GuiUtil.llenarCombo(getCmbGama(), getGamas(), true);
						}
							
					}
				}
			};
		}
		return linkableLabelEditarGamaCliente;
	}

	public void setPrecioGamaSiendoEditado(PrecioGama precioGamaSiendoEditado, boolean modoEdicion) {
		this.precioGamaSiendoEditado = precioGamaSiendoEditado;

		setModoEdicion(modoEdicion);

		getCmbGama().setSelectedItem(precioGamaSiendoEditado.getGamaCliente());

		GrupoTipoArticuloGama grupoTipoArticuloBase = precioGamaSiendoEditado.getGrupoTipoArticuloGama();
		getCmbTipoArticulo().setSelectedItem(grupoTipoArticuloBase.getTipoArticulo());

		RangoAnchoArticuloTenido rangoAnchoArticulo = grupoTipoArticuloBase.getRangoAnchoArticuloTenido();
		getTxtAnchoInicial().setText(rangoAnchoArticulo.getAnchoMinimo().toString());
		getTxtAnchoFinal().setText(rangoAnchoArticulo.getAnchoMaximo().toString());
	}
	
	@Override
	protected void botonAgregarPresionado() {

		if(precioGamaSiendoEditado != null) {
			precioGamaSiendoEditado.deepRemove();
		}

		//Definicion
		DefinicionPrecio definicion = getDefinicion();
		if(definicion == null) {
			setDefinicion(new DefinicionPrecio());
		}
		//Rango
		Float min = Float.valueOf(getTxtAnchoInicial().getText());
		Float max = Float.valueOf(getTxtAnchoFinal().getText());
		Float exacto = null;
		if(getChkAnchoExacto().isSelected()) {
			exacto = Float.valueOf(getTxtAnchoExacto().getText());
		}
		RangoAnchoArticuloTenido rango = (RangoAnchoArticuloTenido)definicion.getRango(min, max, exacto);
		if(rango == null) {
			rango = new RangoAnchoArticuloTenido();
			rango.setAnchoExacto(exacto);
			rango.setAnchoMinimo(min);
			rango.setAnchoMaximo(max);
			getDefinicion().getRangos().add(rango);
			rango.setDefinicionPrecio(definicion);
		}
		
		//Grupo
		TipoArticulo ta = (TipoArticulo)getCmbTipoArticulo().getSelectedItem();
		GrupoTipoArticuloGama grupo = rango.getGrupo(ta);
		if(grupo == null) {
			grupo = new GrupoTipoArticuloGama();
			grupo.setTipoArticulo(ta);
			rango.getGruposGama().add(grupo);
			grupo.setRangoAnchoArticuloTenido(rango);
		}
		Float precio = Float.valueOf(getTxtPrecio().getText());
		GamaColorCliente gcc = (GamaColorCliente) getCmbGama().getSelectedItem();
		PrecioGama pg = grupo.getPrecioGama(gcc);
		if (pg == null) {
			pg = new PrecioGama();
			pg.setGamaCliente(gcc);
			pg.setGamaDefault(gcc.getGamaOriginal());
			pg.setGrupoTipoArticuloGama(grupo);
			pg.setPrecio(precio);
			grupo.getPrecios().add(pg);
		}
		
		List<RangoAnchoArticuloTenido> rangosList = new ArrayList<RangoAnchoArticuloTenido>();
		for(RangoAncho r : getDefinicion().getRangos()) {
			rangosList.add((RangoAnchoArticuloTenido)r);
		}
		//agrego a la tabla
		getTablaRango().limpiar();
		getTablaRango().agregarElementos(rangosList);
	}

	@Override
	protected boolean validar() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void setModoEdicionExtended(boolean modoEdicion) {
		GuiUtil.setEstadoPanel(createPanelDatosEspecificos(), modoEdicion);
	}

	@Override
	protected void limpiarDatosExtended() {
		getCmbGama().setSelectedIndex(-1);
	}

	@Override
	protected void botonAgregarOrCancelarPresionado() {
		this.precioGamaSiendoEditado = null;
	}

}