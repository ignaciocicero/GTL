package ar.com.fwcommon.componentes;

import java.util.EventListener;

/**
 * Clase que define que el formato que se est� ingresando no es v�lido
 * 
 * 
 */
public interface InvalidTextFormatListener extends EventListener {
	public void invalidTextFormat(InvalidTextFormatEvent e);
}
