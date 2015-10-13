package ar.com.fwcommon;
/**
 * Truco del Ancla.
 * Clase �til para la carga de recursos (archivos) a trav�s de Java Web Start.
 * Ej.: Carga de una im�gen
 * 
 * ClassLoader cl = new AnchorTrick().getClass().getClassLoader();
 * ImageIcon imagen;
 * try {
 * 		imagen = new ImageIcon(cl.getResource("C:/icono.png"));
 * } catch(Exception e) {
 * 		e.printStackTrace();
 * }
 * 
 * @version 1.0
 */
public class AnchorTrick {

	public AnchorTrick() {
	}

}