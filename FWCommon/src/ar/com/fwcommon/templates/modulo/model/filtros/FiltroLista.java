package ar.com.fwcommon.templates.modulo.model.filtros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Filtro de una lista de selecci�n m�ltiple
 * 
 * 
 * 
 *
 * @param <T> Elemento que se desea filtrar
 * @param <E> Elementos que se colocar�n en la lista
 */
public abstract class FiltroLista<T, E> extends Filtro<T, List<E>> {

	private List<E> valoresSeleccionables;

	public FiltroLista(String nombre) {
		super(nombre);
	}

	/**
	 * Devuelve los valores entre los que podr� seleccionar el usuario
	 * 
	 * @return Lista inmutable de valores que podr� seleccionar el usuario
	 */
	public List<E> getValoresSeleccionables() {
		if(valoresSeleccionables == null) {
			valoresSeleccionables = Collections.emptyList();
		}
		return valoresSeleccionables;
	}

	/**
	 * Establece los valores entre los que podr� seleccionar el usuario
	 * 
	 * @param Lista de valores que podr� seleccionar el usuario
	 */
	public void setValoresLista(List<? extends E> valores) {
		this.valoresSeleccionables = new ArrayList<E>();
		this.valoresSeleccionables.addAll(valores);
		this.valoresSeleccionables = Collections.unmodifiableList(this.valoresSeleccionables);
		fireModelChangeListener(EVENT_TYPE_VALUES_CHANGE);
	}
	
	/* (non-Javadoc)
	 * ar.com.fwcommon.templates.modulo.model.filtros.Filtro#getDefaultValue()
	 */
	@Override
	protected List<E> getDefaultValue() {
		return Collections.emptyList();
	}
}