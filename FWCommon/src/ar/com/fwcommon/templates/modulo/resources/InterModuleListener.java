package ar.com.fwcommon.templates.modulo.resources;

import java.awt.event.ActionListener;

/**
 * Listener soportado por el {@link InterModuleMediator} 
 * 
 * 
 */
public interface InterModuleListener extends ActionListener {

	/**
	 * Dice si la acci�n debe dispararse o no
	 * @param clazz Clase para la que se est� ejecutando el evento
	 * @return <code>true</code> si es aplicable. <code>false</code> en caso contrario.
	 */
	public boolean isAplicableFor(Class<?> clazz);
}
