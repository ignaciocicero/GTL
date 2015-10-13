package ar.com.fwcommon.templates.main.skin;

import java.awt.Color;
import java.awt.Font;

/**
 * Interface para crear un decorador para un skin.
 * Util para definir propiedades gr�ficas extra, por ejemplo a trav�s
 * de la clase javax.swing.UIManager.
 */
public interface ISkinDecorator {

	/**
	 * Inicializa el decorador del skin.
	 * En �ste m�todo pueden definirse propiedades gr�ficas extra a trav�s de la
	 * clase UIManager.
	 * javax.swing.UIManager
	 */
	public abstract void init();

	/**
	 * @return La tipograf�a por defecto.
	 * java.awt.Font
	 */
	public abstract Font getDefaultFont();

	/**
	 * @return La tipograf�a alternativa.
	 * java.awt.Font
	 */
	public abstract Font getSecondaryFont();

	/**
	 * @return El �cono de la ventana.
	 */
	public abstract String getIconoVentana();

	/**
	 * @return La im�gen que se muestra en el cuadro de login.
	 */
	public abstract String getIconoLogin();

	/**
	 * @return El color de la barra de t�tulo de las ventanas.
	 * java.awt.Color
	 */
	public abstract Color getColorBarraTituloVentana();

	/**
	 * @return El color de fondo de las ventanas.
	 * java.awt.Color
	 */
	public abstract Color getColorFondoVentana();

	/**
	 * @return El color de fondo de los paneles.
	 * java.awt.Color
	 */
	public abstract Color getColorFondoPanel();

	/**
	 * @return El color por defecto de los componentes en su estado normal.
	 * java.awt.Color
	 */
	public abstract Color getColorComponenteNormal();

	/**
	 * @return El color por defecto de los componentes en estado rollover.
	 * java.awt.Color
	 */
	public abstract Color getColorComponenteRollover();

	/**
	 * @return El color por defecto de los componentes en estado deshabilitado.
	 * java.awt.Color
	 */
	public abstract Color getColorComponenteDeshabilitado();

	/**
	 * @return El color por defecto de los componentes en estado seleccionado.
	 * java.awt.Color
	 */
	public abstract Color getColorComponenteSeleccionado();

	/**
	 * @return El color de los encabezados de las tablas.
	 * java.awt.Color
	 */
	public abstract Color getColorHeaderTabla();

	/**
	 * @return El color de los �tems (de una lista o �rbol por ejemplo) en su estado normal.
	 * java.awt.Color
	 */
	public abstract Color getColorItemNormal();

	/**
	 * @return El color de los �tems (de una lista o �rbol por ejemplo) en su estado resaltado.
	 * java.awt.Color
	 */
	public abstract Color getColorItemResaltado();

	/**
	 * @return El color interno de las cajas de texto (textfield, textarea, etc.).
	 * java.awt.Color
	 */
	public abstract Color getColorCajaTexto();

}