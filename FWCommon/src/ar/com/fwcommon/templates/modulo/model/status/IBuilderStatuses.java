package ar.com.fwcommon.templates.modulo.model.status;

/**
 * Construye los statuses que tendr� un determinado modelo
 * 
 * 
 * 
 * @param <T> Elementos que recibir�n los Statuses
 */
public interface IBuilderStatuses<T> {

	/**
	 * Construye los Statuses
	 * 
	 * @param idModel Identificador del modelo para el que se generan los statuses
	 * @return Acciones construidas
	 */
	public Statuses<T> construirStatuses(int idModel);
}
