package ar.com.fwcommon.templates.modulo.model.listeners;

import java.util.EventListener;

/**
 * Listener que escucha cuando una acci�n fue ejecutada
 * 
 * 
 */
public interface AccionExecutedListener extends EventListener {
	
	public void accionExcecuted(AccionExecutedEvent e);
}
