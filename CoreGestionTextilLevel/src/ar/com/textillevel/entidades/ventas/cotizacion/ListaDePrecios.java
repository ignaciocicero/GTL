package ar.com.textillevel.entidades.ventas.cotizacion;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.iterators.ReverseListIterator;

import ar.com.fwcommon.util.DateUtil;
import ar.com.textillevel.entidades.gente.Cliente;

@Entity
@Table(name = "T_LISTA_DE_PRECIOS")
public class ListaDePrecios implements Serializable {

	private static final long serialVersionUID = 6741984125390784747L;

	private Integer id;
	private Cliente cliente; // si es null, es la lista default
	private List<VersionListaDePrecios> versiones;

	public ListaDePrecios() {
		this.versiones = new ArrayList<VersionListaDePrecios>();
	}

	@Id
	@Column(name = "P_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "F_CLIENTE_P_ID")
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@OneToMany(cascade = {CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name = "F_LISTA_PRECIO_P_ID")
	@org.hibernate.annotations.Cascade(value = {org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	public List<VersionListaDePrecios> getVersiones() {
		return versiones;
	}

	public void setVersiones(List<VersionListaDePrecios> versiones) {
		this.versiones = versiones;
	}

	@Transient
	public VersionListaDePrecios getVersionActual() {
		return getVersionPorFecha(DateUtil.getHoy());
	}

	@Transient
	public VersionListaDePrecios getVersionPorFecha(Date fecha) {
		if (getVersiones() == null || getVersiones().isEmpty()) {
			return null;
		}
		Collections.sort(getVersiones(), new Comparator<VersionListaDePrecios>() {
			public int compare(VersionListaDePrecios o1, VersionListaDePrecios o2) {
				int compFecha = o1.getInicioValidez().compareTo(o2.getInicioValidez());
				return compFecha == 0 ? o1.getId().compareTo(o2.getId()) : compFecha;
			}
		});
		ReverseListIterator iterator = new ReverseListIterator(getVersiones());
		VersionListaDePrecios versionADevolver = null;
		while(iterator.hasNext()) {
			VersionListaDePrecios v = (VersionListaDePrecios) iterator.next();
			if (!v.getInicioValidez().after(fecha)) {
				versionADevolver = v;
				break;
			}
		}
		return versionADevolver;
	}
}
