package ar.com.textillevel.modulos.odt.dao.api.local;

import java.sql.Date;
import java.util.List;

import javax.ejb.Local;

import ar.com.fwcommon.dao.api.local.DAOLocal;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.maquinas.Maquina;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;
import ar.com.textillevel.modulos.odt.to.ODTTO;

@Local
public interface OrdenDeTrabajoDAOLocal extends DAOLocal<OrdenDeTrabajo, Integer> {

	/**
	 * Devuelve una lista de ODTs (para el cliente) que no est�n asociadas a los remitos. 
	 * @param idCliente
	 * @return lista de ODTs (para el cliente) que no est�n asociadas a los remitos.
	 */
	public List<OrdenDeTrabajo> getOdtNoAsociadasByClient(Integer idCliente);

	public List<OrdenDeTrabajo> getOdtEagerByRemitoList(Integer idRemito);

	/**
	 * Devuelve una lista de ODTs asociadas al remito de entrada cuyo id es <code>idRemitoEntrada</code>
	 * @param idRemitoEntrada
	 * @return Una lista de ODTs asociadas al remito de entrada cuyo id es <code>idRemitoEntrada</code>
	 */
	public List<OrdenDeTrabajo> getODTAsociadas(Integer idRemitoEntrada);

	public OrdenDeTrabajo getByIdEager(Integer idODT);

	public List<OrdenDeTrabajo> getByIdsEager(List<Integer> ids);
	
	public List<OrdenDeTrabajo> getOrdenesDeTrabajo(Date fechaDesde, Date fechaHasta, Cliente cliente, EEstadoODT... estado);

	public List<ODTTO> getAllODTTOByParams(Date fechaDesde, Date fechaHasta, Cliente cliente, Integer idTipoMaquina, Integer idProducto, boolean conProductoParcial, EEstadoODT... estado);

	public List<OrdenDeTrabajo> getAllEnProcesoByTipoMaquina(Date fechaDesde, Date fechaHasta, Cliente cliente, Integer idTipoMaquina);

	public Short getUltimoOrdenMaquina(Maquina maquina);

	public OrdenDeTrabajo getByMaquinaYOrden(Integer maquinaActual, Short ordenEnMaquina);
	
	public void updateODT(OrdenDeTrabajo odt);

	public void borrarSecuencia(OrdenDeTrabajo ordenDeTrabajo);

	public void flush();
	
	public OrdenDeTrabajo getODTEagerByCodigo(String codigo);

	public List<OrdenDeTrabajo> getOrdenesDeTrabajoSinSalida(Date fechaDesde, Date fechaHasta);

	public List<OrdenDeTrabajo> getODTsEnMaquina(Maquina maquinaActual);

	public List<OrdenDeTrabajo> getAllNoFinalizadasBySector(ESectorMaquina sector);

}