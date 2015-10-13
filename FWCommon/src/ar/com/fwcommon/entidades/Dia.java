package ar.com.fwcommon.entidades;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_Dia")
public class Dia implements Serializable, Comparable {

	private static final long serialVersionUID = 7738572070089606247L;

	private int idDia;
	private String nombre;
	private boolean habil;
	private int nroDia;

	/**
	 * Devuelve el <b>id</b> del d�a.
	 * 
	 * @return idDia
	 */
	@Id
	@GeneratedValue
	@Column(name = "P_IdDia")
	public int getIdDia() {
		return idDia;
	}

	/**
	 * Setea el <b>id</b> del d�a.
	 * 
	 * @param idDia
	 */
	public void setIdDia(int idDia) {
		this.idDia = idDia;
	}

	/**
	 * Devuelve el <b>nombre</b> del d�a.
	 * 
	 * @return nombre
	 */
	@Column(name = "A_Nombre")
	public String getNombre() {
		return nombre;
	}

	/**
	 * Setea el <b>nombre</b> del d�a.
	 * 
	 * @param nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Devuelve <b>true</b> si el d�a es h�bil.
	 * 
	 * @return habil
	 */
	@Column(name = "A_DiaHabil")
	public boolean isHabil() {
		return habil;
	}

	/**
	 * Setea el flag de d�a h�bil.
	 * 
	 * @param habil
	 */
	public void setHabil(boolean habil) {
		this.habil = habil;
	}

	/**
	 * Devuelve el <b>n�mero de d�a</b> asociado al d�a.
	 * 
	 * @return nroDia
	 */
	@Column(name = "A_NumeroDia")
	public int getNroDia() {
		return nroDia;
	}

	/**
	 * Setea el <b>n�mero de d�a</b> asociado al d�a.
	 * 
	 * @param nroDia
	 */
	public void setNroDia(int nroDia) {
		this.nroDia = nroDia;
	}

	/** Sobreescritura del m�todo toString de Object */
	public String toString() {
		return nombre;
	}

	/** Sobreescritura del m�todo compareTo de Object */
	public int compareTo(Object aThat) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		if(this == aThat)
			return EQUAL;
		// Cast para checkear el tipo
		final Dia that = (Dia)aThat;
		// Comparaci�n por nro. de d�a
		if(this.nroDia < that.nroDia)
			return BEFORE;
		if(this.nroDia > that.nroDia)
			return AFTER;
		return EQUAL;
	}

	/** Sobreescritura del m�todo equals de Object */
	public boolean equals(Object dia) {
		boolean resultado = false;
		if(!(dia instanceof Dia))
			return resultado;
		if(dia == null)
			return resultado;
		else
			return resultado = ((nombre.equals(((Dia)dia).getNombre())) && (nroDia == ((Dia)dia).nroDia));
	}

	public int hashCode() {
		return idDia << 3;
	}
}