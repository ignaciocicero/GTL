package ar.com.fwcommon.templates.modulo.model.listeners;

import java.util.EventListener;

/**
 * Interfaz del un Listener para recibir notificaciones. Este listener indica
 * que ha cambiado el estado de alg�n filtro
 * 
 * 
 */
public interface FilterChangeListener extends EventListener {
	
	/**
	 * Invocado cuando un filtro cambi� su valor
	 */
	public void filterChange(FilterChangeEvent e);
}
