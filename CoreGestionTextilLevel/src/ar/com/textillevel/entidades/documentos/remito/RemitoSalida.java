package ar.com.textillevel.entidades.documentos.remito;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

import ar.com.fwcommon.util.DateUtil;
import ar.com.textillevel.entidades.documentos.factura.Factura;
import ar.com.textillevel.entidades.documentos.factura.proveedor.CorreccionFacturaProveedor;
import ar.com.textillevel.entidades.documentos.remito.proveedor.ItemRemitoSalidaProveedor;
import ar.com.textillevel.entidades.enums.ETipoRemitoSalida;
import ar.com.textillevel.entidades.gente.Proveedor;
import ar.com.textillevel.entidades.ventas.IProductoParaODT;
import ar.com.textillevel.entidades.ventas.articulos.DibujoEstampado;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;

@Entity
@DiscriminatorValue(value="SAL")
public class RemitoSalida extends Remito implements Serializable {

	private static final long serialVersionUID = 1863941363490686977L;

	private BigDecimal porcentajeMerma;
	private Integer nroFactura;
	private Integer nroOrden;
	private List<OrdenDeTrabajo> odts;
	private List<ItemRemitoSalidaProveedor> items;
	private List<CorreccionFacturaProveedor> correccionesProvGeneradas; //Correcciones de proveedor generadas automaticamente al ingresar un remito de salida 
	private Proveedor proveedor;
	private Integer idTipoRemitoSalida;
	private Boolean anulado;
	private Factura factura;
	private List<DibujoEstampado> dibujoEstampados;
	private Integer nroSucursal;
	private Boolean entregado;
	private Timestamp fechaHoraEntregado;
	private String terminalEntrega;
	private Boolean controlado;
	private String terminalControl;

	public RemitoSalida() {
		this.odts = new ArrayList<OrdenDeTrabajo>();
		this.dibujoEstampados = new ArrayList<DibujoEstampado>();
		this.items = new ArrayList<ItemRemitoSalidaProveedor>();
		this.correccionesProvGeneradas = new ArrayList<CorreccionFacturaProveedor>();
		setPesoTotal(new BigDecimal(0));
	}

	@Column(name="A_PORCE_MERMA")
	public BigDecimal getPorcentajeMerma() {
		return porcentajeMerma;
	}
	
	public void setPorcentajeMerma(BigDecimal porcentajeMerma) {
		this.porcentajeMerma = porcentajeMerma;
	}

	@Column(name="A_NRO_FACTURA", nullable=true)
	public Integer getNroFactura() {
		return nroFactura;
	}

	public void setNroFactura(Integer nroFactura) {
		this.nroFactura = nroFactura;
	}

	@ManyToMany
	@JoinTable(name = "T_REMITO_SALIDA_ODT", 
			joinColumns = { @JoinColumn(name = "F_REMITO_SALIDA_P_ID") }, 
			inverseJoinColumns = { @JoinColumn(name = "F_ODT_P_ID") })
	public List<OrdenDeTrabajo> getOdts() {
		return odts;
	}

	public void setOdts(List<OrdenDeTrabajo> odts) {
		this.odts = odts;
	}

	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="F_REMITO_SAL_PROV_P_ID")
	@Cascade(value=org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public List<ItemRemitoSalidaProveedor> getItems() {
		return items;
	}

	public void setItems(List<ItemRemitoSalidaProveedor> items) {
		this.items = items;
	}

	@ManyToMany
	@JoinTable(name = "T_REM_SAL_CORREC_FACT_PROV_ASOC", 
			joinColumns = { @JoinColumn(name = "F_REMITO_SALIDA_P_ID") }, 
			inverseJoinColumns = { @JoinColumn(name = "F_CORRECC_P_ID") })
	public List<CorreccionFacturaProveedor> getCorreccionesProvGeneradas() {
		return correccionesProvGeneradas;
	}

	public void setCorreccionesProvGeneradas(List<CorreccionFacturaProveedor> correccionesProvGeneradas) {
		this.correccionesProvGeneradas = correccionesProvGeneradas;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="F_PROV_P_ID", nullable=true)
	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	@Column(name = "A_ID_TIPO_REM_SALIDA", nullable = true)
	public Integer getIdTipoRemitoSalida() {
		return idTipoRemitoSalida;
	}

	public void setIdTipoRemitoSalida(Integer idTipoRemitoSalida) {
		this.idTipoRemitoSalida = idTipoRemitoSalida;
	}

	public void setTipoRemitoSalida(ETipoRemitoSalida tipoRemitoSalida) {
		if (tipoRemitoSalida == null) {
			this.setIdTipoRemitoSalida(null);
		}
		setIdTipoRemitoSalida(tipoRemitoSalida.getId());
	}

	@Transient
	public ETipoRemitoSalida getTipoRemitoSalida() {
		if (getIdTipoRemitoSalida() == null) {
			return null;
		}
		return ETipoRemitoSalida.getById(getIdTipoRemitoSalida());
	}

	@Column(name = "A_ANULADO", nullable = true)
	public Boolean getAnulado() {
		return anulado;
	}

	public void setAnulado(Boolean anulado) {
		this.anulado = anulado;
	}

	@Column(name = "A_NROORDEN", nullable = true)
	public Integer getNroOrden() {
		return nroOrden;
	}

	public void setNroOrden(Integer nroOrden) {
		this.nroOrden = nroOrden;
	}

	@Transient
	public List<IProductoParaODT> getProductoList() {
		Set<IProductoParaODT> productoSet = new HashSet<IProductoParaODT>();
		for(OrdenDeTrabajo odt : getOdts()) {
			productoSet.add(odt.getIProductoParaODT());
		}
		return new ArrayList<IProductoParaODT>(productoSet);
	}

	@Override
	@Transient
	public void recalcularOrdenes() {
		Collections.sort(getPiezas(), new Comparator<PiezaRemito> () {
			public int compare(PiezaRemito o1, PiezaRemito o2) {
				return o1.getOrdenPieza().compareTo(o2.getOrdenPieza());
			}
		});
	}

	@Transient
	public Integer getCantidadPiezas() {
		return getPiezas().size();
	}

	@Transient
	public Integer getCantidadPiezasParaEstimarTubos() {
		int cant = 0;
		for(PiezaRemito pr : getPiezas()) {
			cant += (pr.getMetros() == null || pr.getMetros().equals(BigDecimal.ZERO)) ? 0 : 1; //para los tubos no se cuentan las piezas con metros == 0 
		}
		return cant;
	}

	@Override
	@Transient
	public BigDecimal getTotalMetros() {
		BigDecimal totalMetros = new BigDecimal(0);
		for(PiezaRemito pieza : getPiezas()) {
			totalMetros = totalMetros.add(pieza.getMetros());
		}
		return totalMetros;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="F_FACTURA_P_ID",insertable=false, updatable=false)
	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="F_REMITO_SALIDA_P_ID", nullable=true)
	public List<DibujoEstampado> getDibujoEstampados() {
		return dibujoEstampados;
	}

	public void setDibujoEstampados(List<DibujoEstampado> dibujoEstampados) {
		this.dibujoEstampados = dibujoEstampados;
	}
	
	@Column(name = "A_NRO_SUCURSAL", nullable = true)
	public Integer getNroSucursal() {
		return nroSucursal;
	}

	public void setNroSucursal(Integer nroSucursal) {
		this.nroSucursal = nroSucursal;
	}

	@Override
	public String toString(){
		return "Remito Salida N� " + getNroRemito() +". Fecha: " + DateUtil.dateToString(getFechaEmision());
	}

	@Column(name = "A_ENTREGADO", nullable = true)
	public Boolean getEntregado() {
		return entregado;
	}

	public void setEntregado(Boolean entregado) {
		this.entregado = entregado;
	}

	@Column(name = "A_CONTROLADO", nullable = true)
	public Boolean getControlado() {
		return controlado;
	}

	public void setControlado(Boolean controlado) {
		this.controlado = controlado;
	}
	
	@Column(name = "A_FECHA_HORA_ENTREGADO", nullable = true)
	public Timestamp getFechaHoraEntregado() {
		return fechaHoraEntregado;
	}

	public void setFechaHoraEntregado(Timestamp fechaHoraEntregado) {
		this.fechaHoraEntregado = fechaHoraEntregado;
	}
	
	@Column(name = "A_TERMINAL_ENTREGA", nullable = true)
	public String getTerminalEntrega() {
		return terminalEntrega;
	}

	public void setTerminalEntrega(String terminalEntrega) {
		this.terminalEntrega = terminalEntrega;
	}

	@Column(name = "A_TERMINAL_CONTROL", nullable = true)
	public String getTerminalControl() {
		return terminalControl;
	}

	public void setTerminalControl(String terminalControl) {
		this.terminalControl = terminalControl;
	}

	@Transient
	public List<PiezaRemito> getPiezasImprimibles() {
		List<PiezaRemito> piezasImprimibles = new ArrayList<PiezaRemito>();
		for(PiezaRemito pr : getPiezas()) {
			if(pr.getMetros() != null && pr.getMetros().floatValue() > 0f) { // s�lo se imprimen las piezas cuyos metros != 0
				piezasImprimibles.add(pr);
			}
		}
		return piezasImprimibles;
	}
	
	@Transient
	public int getCantidadDePiezasImprimibles() {
		return getPiezasImprimibles().size();
	}

}