package ar.com.textillevel.modulos.odt.dao.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import ar.com.fwcommon.dao.impl.GenericDAO;
import ar.com.fwcommon.util.NumUtil;
import ar.com.textillevel.entidades.enums.ETipoProducto;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.entidades.ventas.ProductoArticulo;
import ar.com.textillevel.entidades.ventas.articulos.Articulo;
import ar.com.textillevel.entidades.ventas.materiaprima.Pigmento;
import ar.com.textillevel.entidades.ventas.materiaprima.Quimico;
import ar.com.textillevel.entidades.ventas.materiaprima.anilina.Anilina;
import ar.com.textillevel.modulos.odt.dao.api.local.OrdenDeTrabajoDAOLocal;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;
import ar.com.textillevel.modulos.odt.entidades.maquinas.Maquina;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.DoEagerFormulaExplotadaVisitor;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.explotaciones.FormulaEstampadoClienteExplotada;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.explotaciones.FormulaTenidoClienteExplotada;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.explotaciones.fw.MateriaPrimaCantidadExplotada;
import ar.com.textillevel.modulos.odt.entidades.maquinas.procesos.InstruccionProcedimiento;
import ar.com.textillevel.modulos.odt.entidades.maquinas.procesos.InstruccionProcedimientoPasadas;
import ar.com.textillevel.modulos.odt.entidades.maquinas.procesos.InstruccionProcedimientoTipoProducto;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoPasadasODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoTipoProductoODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.PasoSecuenciaODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.SecuenciaODT;
import ar.com.textillevel.modulos.odt.enums.EAvanceODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;
import ar.com.textillevel.modulos.odt.to.ODTTO;

@Stateless
@SuppressWarnings("unchecked")
public class OrdenDeTrabajoDAO extends GenericDAO<OrdenDeTrabajo, Integer> implements OrdenDeTrabajoDAOLocal {

	public List<OrdenDeTrabajo> getOdtNoAsociadasByClient(Integer idCliente) {
//		Query query = getEntityManager().createQuery(
//						"SELECT DISTINCT odt " +
//						"FROM OrdenDeTrabajo odt " +
//						"JOIN FETCH odt.piezas podt " +
//						"WHERE odt.remito.cliente.id = :idCliente AND " +
//						"NOT EXISTS (" +
//						"SELECT 1 " +
//						"FROM PiezaRemito pr " +
//						"WHERE podt IN ELEMENTS(pr.piezasPadreODT))"
//						);
		Query query = getEntityManager().createQuery(
				"SELECT odt " +
				"FROM OrdenDeTrabajo odt " +
				"WHERE odt.remito.cliente.id = :idCliente AND " +
				"NOT EXISTS (" +
				"SELECT 1 " +
				"FROM RemitoSalida rs " +
				"WHERE odt IN ELEMENTS(rs.odts) AND rs.cliente.id = :idCliente)"
				);
		query.setParameter("idCliente", idCliente);
		List<OrdenDeTrabajo> resultList = query.getResultList();

		for(OrdenDeTrabajo odt : resultList) {
			for(PiezaODT podt2 : odt.getPiezas()) {
				podt2.getPiezasSalida().size();
			}
		}
		return resultList;
	}

	public List<OrdenDeTrabajo> getOdtEagerByRemitoList(Integer idRemito) {
		Query query = getEntityManager().createQuery("SELECT odt " +
													 "FROM OrdenDeTrabajo odt " +
													 "WHERE odt.remito.id = :idRemito ");
		query.setParameter("idRemito", idRemito);
		List<OrdenDeTrabajo> resultList = query.getResultList();
		for(OrdenDeTrabajo odt : resultList) {
			odt = getByIdEager(odt.getId());
		}
		return resultList;
	}

	/* (non-Javadoc)
	 * @see ar.com.textillevel.dao.api.local.OrdenDeTrabajoDAOLocal#getODTAsociadas(java.lang.Integer)
	 */
	public List<OrdenDeTrabajo> getODTAsociadas(Integer idRemitoEntrada) {
		Query query = getEntityManager().createQuery("FROM OrdenDeTrabajo odt " +
													 "WHERE odt.remito.id = :idRemitoEntrada");
		query.setParameter("idRemitoEntrada", idRemitoEntrada);
		List<OrdenDeTrabajo> resultList = query.getResultList();
		return resultList;
	}

	public OrdenDeTrabajo getByIdEager(Integer idODT) {
		OrdenDeTrabajo odt = getById(idODT);
		if(odt.getMaquinaActual() != null) {
			odt.getMaquinaActual().getTipoMaquina().getSectorMaquina();
		}
		for(PiezaODT pieza : odt.getPiezas()) {
			if(pieza.getPiezasSalida() != null) {
				pieza.getPiezasSalida().size();
			}
		}
		if(odt.getRemito() != null) {
			odt.getRemito().getPiezas().size();
		}
		if(odt.getSecuenciaDeTrabajo()!=null){
			odt.getSecuenciaDeTrabajo().getPasos().size();
			for(PasoSecuenciaODT ps : odt.getSecuenciaDeTrabajo().getPasos()){
				ps.getSubProceso().getPasos().size();

				doEagerIntruccionesODT(ps.getSubProceso().getPasos());

				ps.getProceso().getInstrucciones().size();

				doEagerIntrucciones(ps.getProceso().getInstrucciones());
				
				ps.getProceso().getNombre();
				ps.getSector().getNombre();
				ps.getSubProceso().getNombre();
				ps.getSubProceso().getTipoArticulo().getNombre();
			}
		}
		
		if(odt.getProductoArticulo() != null){
			Articulo articulo = odt.getProductoArticulo().getArticulo();
			if(articulo!=null && articulo.getTipoArticulo() != null){
				articulo.getTipoArticulo().getNombre();
				if(articulo.getTipoArticulo().getTiposArticuloComponentes()!=null){
					articulo.getTipoArticulo().getTiposArticuloComponentes().size();
				}
			}
			if(odt.getRemito()!=null){
				odt.getRemito().getProductoArticuloList().size();
				for(ProductoArticulo p : odt.getRemito().getProductoArticuloList()){
					if(p.getTipo() == ETipoProducto.ESTAMPADO){
						if(p.getVariante()!=null && p.getVariante().getColores()!=null){
							p.getVariante().getColores().size();
						}
					}else if(p.getTipo() == ETipoProducto.TENIDO){
						if(p.getGamaColor()!= null && p.getGamaColor().getColores() != null){
							p.getGamaColor().getColores().size();
						}
					}
				}
			}
		}
		return odt;
	}

	public List<OrdenDeTrabajo> getByIdsEager(List<Integer> ids) {
		List<OrdenDeTrabajo> lista = new ArrayList<OrdenDeTrabajo>();
		for (Integer id : ids) {
			lista.add(getByIdEager(id));
		}
		return lista;
	}

	private void doEagerIntruccionesODT(List<InstruccionProcedimientoODT> instrucciones) {
		for(InstruccionProcedimientoODT i : instrucciones){
			if(i instanceof InstruccionProcedimientoPasadasODT){
				((InstruccionProcedimientoPasadasODT)i).getQuimicosExplotados().size();
			}else if(i instanceof InstruccionProcedimientoTipoProductoODT){
				InstruccionProcedimientoTipoProductoODT itp = (InstruccionProcedimientoTipoProductoODT)i;
				if(itp.getFormula()!=null){
					DoEagerFormulaExplotadaVisitor visitor = new DoEagerFormulaExplotadaVisitor(); 
					itp.getFormula().accept(visitor);
				}
				itp.getTipoArticulo().getNombre();
				itp.getTipoArticulo().getTiposArticuloComponentes().size();
			}
		}		
	}

	private void doEagerIntrucciones(List<InstruccionProcedimiento> instrucciones){
		for(InstruccionProcedimiento i : instrucciones){
			if(i instanceof InstruccionProcedimientoPasadas){
				((InstruccionProcedimientoPasadas)i).getQuimicos().size();
			}else if(i instanceof InstruccionProcedimientoTipoProducto){
				InstruccionProcedimientoTipoProducto itp = (InstruccionProcedimientoTipoProducto)i;
				itp.getTipoArticulo().getNombre();
				itp.getTipoArticulo().getTiposArticuloComponentes().size();
			}
		}		
	}	
	
	public List<OrdenDeTrabajo> getOrdenesDeTrabajo(Date fechaDesde, Date fechaHasta, Cliente cliente, EEstadoODT... estado) {
		String hql = " SELECT odt FROM OrdenDeTrabajo odt LEFT JOIN FETCH odt.remito LEFT JOIN FETCH odt.remito.cliente LEFT JOIN FETCH odt.productoArticulo WHERE 1=1 "+
					 (estado!=null && estado.length > 0 && estado[0] != null ?" AND odt.idEstadoODT IN (:idEstadoODT) ":" ")+
					 (fechaDesde!=null?" AND odt.fechaODT >= :fechaDesde  ":" ")+
					 (fechaHasta!=null?" AND odt.fechaODT <= :fechaHasta  ":" ") +
					 (cliente!=null?" AND odt.remito.cliente.id = :idCliente ":" ") +
					 " ORDER BY odt.fechaODT DESC ";
		Query q = getEntityManager().createQuery(hql);
		if(estado!=null && estado.length > 0 && estado[0] != null){
			ImmutableList<Integer> idsEstados = FluentIterable.from(Arrays.asList(estado))
					.filter(Predicates.notNull())
					.transform(new Function<EEstadoODT, Integer>() {
						@Override
						public Integer apply(EEstadoODT estado) {
							return estado.getId();
						}
					}).toList();
			q.setParameter("idEstadoODT", idsEstados);
		}
		if(fechaDesde!=null){
			q.setParameter("fechaDesde", fechaDesde);
		}
		if(fechaHasta!=null){
			q.setParameter("fechaHasta", fechaHasta);
		}
		if(cliente!=null){
			q.setParameter("idCliente", cliente.getId());
		}
		return q.getResultList();
	}

	public List<ODTTO> getAllODTTOByParams(Date fechaDesde, Date fechaHasta, Cliente cliente, Integer idTipoMaquina, Integer idProducto, boolean conProductoParcial, EEstadoODT... estado) {
		String hql = " SELECT new ar.com.textillevel.modulos.odt.to.ODTTO(odt.id, odt.codigo, odt.remito.id, odt.idEstadoODT, odt.secuenciaDeTrabajo.id, odt.remito.cliente, odt.productoArticulo, odt.productoParcial, odt.ordenEnMaquina, maq, odt.remito.pesoTotal, odt.idAvance, odt.fechaPorComenzarUltSector, odt.fechaEnProcesoUltSector, odt.fechaFinalizadoUltSector, SUM(pr.metros)) "
				 + " FROM OrdenDeTrabajo odt "
				 + " LEFT JOIN odt.maquinaActual maq "
				 + " JOIN odt.remito re "
				 + " JOIN odt.remito.cliente cl "
				 + " LEFT JOIN odt.productoArticulo pa "
				 + " JOIN odt.piezas podt "
				 + " JOIN podt.piezaRemito pr "
				 + " WHERE 1=1 " +
				 (fechaDesde!=null? " AND odt.fechaODT >= :fechaDesde ":" ")+
				 (fechaHasta!=null? " AND odt.fechaODT <= :fechaHasta" : " ")+
				 (idTipoMaquina!=null? " AND odt.maquinaActual.tipoMaquina.id = :idTipoMaquina" : " ")+
				 (cliente!=null?" AND cl.id = :idCliente ":" ") +
				 (idProducto!=null?" AND (pa.producto.id = :idProducto OR  odt.productoParcial.producto.id = :idProducto) ":" ") +
				 (conProductoParcial ? " AND odt.productoParcial.producto is NOT NULL " : " ") + 
				 (estado!=null && estado.length > 0 && estado[0] != null ?" AND odt.idEstadoODT IN (:idEstadoODT) ":" ")+
				 " GROUP BY odt.id, odt.codigo, odt.remito.id, odt.remito.cliente, odt.productoArticulo, odt.productoParcial, odt.ordenEnMaquina, odt.maquinaActual.id, odt.remito.pesoTotal, odt.idAvance, odt.fechaPorComenzarUltSector, odt.fechaEnProcesoUltSector, odt.fechaFinalizadoUltSector " +
				 " ORDER BY odt.id DESC";	
		Query q = getEntityManager().createQuery(hql);
		if(estado!=null && estado.length > 0 && estado[0] != null){
			ImmutableList<Integer> idsEstados = FluentIterable.from(Arrays.asList(estado))
					.filter(Predicates.notNull())
					.transform(new Function<EEstadoODT, Integer>() {
						@Override
						public Integer apply(EEstadoODT estado) {
							return estado.getId();
						}
					}).toList();
			q.setParameter("idEstadoODT", idsEstados);
		}
		if(fechaDesde!=null){
			q.setParameter("fechaDesde", fechaDesde);
		}
		if(fechaHasta!=null){
			q.setParameter("fechaHasta", fechaHasta);
		}
		if(cliente!=null){
			q.setParameter("idCliente", cliente.getId());
		}
		if(idTipoMaquina != null) {
			q.setParameter("idTipoMaquina", idTipoMaquina);
		}
		if(idProducto != null) {
			q.setParameter("idProducto", idProducto);
		}
		return q.getResultList();
	}

	public List<OrdenDeTrabajo> getAllEnProcesoByTipoMaquina(Date fechaDesde, Date fechaHasta, Cliente cliente, Integer idTipoMaquina) {
		String hql = " SELECT odt FROM OrdenDeTrabajo odt WHERE odt.maquinaActual.tipoMaquina.id = :idTipoMaquina  "+
					 (fechaDesde!=null? " AND odt.fechaODT >= :fechaDesde ":" ")+
					 (fechaHasta!=null? " AND odt.fechaODT <= :fechaHasta" : " ")+
					 (cliente!=null?" AND odt.remito.cliente.id = :idCliente ":" ");
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("idTipoMaquina", idTipoMaquina);
		if (fechaDesde != null) {
			q.setParameter("fechaDesde", fechaDesde);
		}
		if (fechaHasta != null) {
			q.setParameter("fechaHasta", fechaHasta);
		}
		if (cliente != null) {
			q.setParameter("idCliente", cliente.getId());
		}
		return q.getResultList();
	}

	public Short getUltimoOrdenMaquina(Maquina maquina) {
		String hql = " SELECT MAX(odt.ordenEnMaquina) FROM OrdenDeTrabajo odt WHERE odt.maquinaActual.id = :idMaquina ";
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("idMaquina", maquina.getId());
		Integer ret = NumUtil.toInteger(q.getSingleResult());
		return ret==null?0:ret.shortValue();
	}

	public OrdenDeTrabajo getByMaquinaYOrden(Integer idMaquina, Short ordenEnMaquina) {
		String hql = " SELECT odt FROM OrdenDeTrabajo odt WHERE odt.maquinaActual.id = :idMaquina AND odt.ordenEnMaquina = :ordenEnMaquina ";
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("ordenEnMaquina", ordenEnMaquina);
		q.setParameter("idMaquina", idMaquina);
		List<OrdenDeTrabajo> odts = q.getResultList();
		if(odts == null || odts.isEmpty() || odts.size()>1){
			throw new RuntimeException("Inconstencia en los ordenes de las odt");
		}
		return odts.get(0);
	}
	
	public void updateODT(OrdenDeTrabajo odt){
		getHibernateSession().update(odt);
	}

	public void borrarSecuencia(OrdenDeTrabajo ordenDeTrabajo) {
		Integer idSecuencia = ordenDeTrabajo.getSecuenciaDeTrabajo().getId();
		SecuenciaODT sec = getEntityManager().find(SecuenciaODT.class, idSecuencia);
		Set<Integer> idsPasos = new HashSet<Integer>();
		Set<Integer> idsProcesos = new HashSet<Integer>();
		Set<Integer> idsInstrucciones = new HashSet<Integer>();
		Set<Integer> idsMatPrimEx = new HashSet<Integer>();
		Set<Integer> idsFormEx = new HashSet<Integer>();
		Set<Integer> idsMPEAnilina = new HashSet<Integer>();
		Set<Integer> idsMPEQuimico = new HashSet<Integer>();
		Set<Integer> idsMPEPigmento = new HashSet<Integer>();
		
		
		for(PasoSecuenciaODT ps : sec.getPasos()){
			idsPasos.add(ps.getId());
			idsProcesos.add(ps.getSubProceso().getId());
			for(InstruccionProcedimientoODT ip : ps.getSubProceso().getPasos()){
				idsInstrucciones.add(ip.getId());
				if(ip instanceof InstruccionProcedimientoTipoProductoODT){
					InstruccionProcedimientoTipoProductoODT ipta = (InstruccionProcedimientoTipoProductoODT)ip;
					if(ipta.getFormula()!=null){
						if(ipta.getFormula() instanceof FormulaEstampadoClienteExplotada){
							FormulaEstampadoClienteExplotada fee = (FormulaEstampadoClienteExplotada)ipta.getFormula();
							for(MateriaPrimaCantidadExplotada<Pigmento> mpc : fee.getPigmentos()){
								idsMPEPigmento.add(mpc.getId());
							}
							for(MateriaPrimaCantidadExplotada<Quimico> mpc : fee.getQuimicos()){
								idsMPEQuimico.add(mpc.getId());
							}
						}else if(ipta.getFormula() instanceof FormulaTenidoClienteExplotada){
							FormulaTenidoClienteExplotada fee = (FormulaTenidoClienteExplotada)ipta.getFormula();
							for(MateriaPrimaCantidadExplotada<Anilina> mpc : fee.getMateriasPrimas()){
								idsMPEAnilina.add(mpc.getId());
							}
						}
						idsFormEx.add(ipta.getFormula().getId());
					}
				}else if(ip instanceof InstruccionProcedimientoPasadasODT){
					InstruccionProcedimientoPasadasODT ipp = (InstruccionProcedimientoPasadasODT)ip;
					if(ipp.getQuimicosExplotados()!=null && !ipp.getQuimicosExplotados().isEmpty()){
						for(MateriaPrimaCantidadExplotada<Quimico> mp : ipp.getQuimicosExplotados()){
							idsMatPrimEx.add(mp.getId());
						}
					}
				}
			}
		}
		
		/*
		 * delete from t_paso_seceuncia_odt;
update t_orden_de_trabajo set f_secuencia_p_id = null;
delete from t_secuencia_odt;
delete from t_map_prima_explotada;
delete from t_instruccion_procedimiento where f_proc_odt_p_id is not null;
delete from t_procedimiento_odt;
		 */
		
		
		getEntityManager().createNativeQuery("delete from t_paso_seceuncia_odt where f_secuencia_p_id = :idSecuencia ").setParameter("idSecuencia", idSecuencia).executeUpdate();
		getEntityManager().flush();
		ordenDeTrabajo.setSecuenciaDeTrabajo(null);
		ordenDeTrabajo = save(ordenDeTrabajo);
		getEntityManager().flush();
		getEntityManager().createQuery(" DELETE FROM SecuenciaODT s WHERE s.id = :idSecuencia ").setParameter("idSecuencia", idSecuencia).executeUpdate();
		getEntityManager().flush();
		if(!idsMatPrimEx.isEmpty()){
			getEntityManager().createNativeQuery("delete from t_map_prima_explotada where p_id in (:idsMpExp)").setParameter("idsMpExp", idsMatPrimEx).executeUpdate();
			getEntityManager().flush();
		}
		if(!idsMPEPigmento.isEmpty()){
			getEntityManager().createNativeQuery("delete from t_map_prima_explotada where p_id in (:idsMPEPigmento)").setParameter("idsMPEPigmento", idsMPEPigmento).executeUpdate();
			getEntityManager().flush();
		}
		if(!idsMPEQuimico.isEmpty()){
			getEntityManager().createNativeQuery("delete from t_map_prima_explotada where p_id in (:idsMPEQuimico)").setParameter("idsMPEQuimico", idsMPEQuimico).executeUpdate();
			getEntityManager().flush();
		}
		
		if(!idsMatPrimEx.isEmpty()){
			getEntityManager().createNativeQuery("delete from t_map_prima_explotada where p_id in (:idsMPEAnilina)").setParameter("idsMPEAnilina", idsMPEAnilina).executeUpdate();
			getEntityManager().flush();
		}
		
		if(!idsFormEx.isEmpty()){
			getEntityManager().createNativeQuery("delete from T_FORMULA_TENIDO_EXPLOTADA where p_id in (:idsFormEx)").setParameter("idsFormEx", idsFormEx).executeUpdate();
			getEntityManager().flush();
		}
		if(!idsFormEx.isEmpty()){
			getEntityManager().createNativeQuery("delete from T_FORMULA_ESTAMPADO_EXPLOTADA where p_id in (:idsFormEx)").setParameter("idsFormEx", idsFormEx).executeUpdate();
			getEntityManager().flush();
		}
		getEntityManager().createNativeQuery("delete from t_instruccion_procedimiento where p_id in (:idsInstrucciones)").setParameter("idsInstrucciones", idsInstrucciones).executeUpdate();
		getEntityManager().flush();
		getEntityManager().createNativeQuery("delete from t_procedimiento_odt where p_id in (:idsProcesos)").setParameter("idsProcesos", idsProcesos).executeUpdate();
		getEntityManager().flush();
	}

	public void flush() {
		getEntityManager().flush();
	}

	public OrdenDeTrabajo getODTEagerByCodigo(String codigo) {
		Query query = getEntityManager().createQuery(" SELECT odt " +
													 " FROM OrdenDeTrabajo odt " +
													 " WHERE odt.codigo = :codigo");
		query.setParameter("codigo", codigo);
		List<OrdenDeTrabajo> resultList = query.getResultList();
		if(resultList.isEmpty()) {
			return null;
		} else if(resultList.size() > 1) {
			throw new IllegalArgumentException("Existe m�s de una ODT con c�digo " + codigo);
		} else {
			OrdenDeTrabajo odt = resultList.get(0);
			return getByIdEager(odt.getId());
		}
	}

	public List<OrdenDeTrabajo> getOrdenesDeTrabajoSinSalida(Date fechaDesde,Date fechaHasta) {
		String hql = " SELECT odt FROM OrdenDeTrabajo odt WHERE 1=1 "+
				 (fechaDesde!=null?" AND odt.fechaODT >= :fechaDesde  ":" ")+
				 (fechaHasta!=null?" AND odt.fechaODT <= :fechaHasta  ":" ")+
				 (" AND NOT EXISTS(SELECT 1 FROM odt.piezas podt WHERE podt.piezasSalida IS NOT EMPTY) ");
		Query q = getEntityManager().createQuery(hql);
		if(fechaDesde!=null){
			q.setParameter("fechaDesde", fechaDesde);
		}
		if(fechaHasta!=null){
			q.setParameter("fechaHasta", fechaHasta);
		}
		return q.getResultList();
	}

	public List<OrdenDeTrabajo> getODTsEnMaquina(Maquina maquina) {
		String hql = " SELECT odt " +
				     " FROM OrdenDeTrabajo odt " + 
				     " WHERE  odt.maquinaActual = :maquina AND odt.ordenEnMaquina IS NOT NULL " +
				     " ORDER BY odt.ordenEnMaquina ";
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("maquina", maquina);
		return q.getResultList();
	}

	@Override
	public List<OrdenDeTrabajo> getAllNoFinalizadasBySector(ESectorMaquina sector) {
		String hql = "SELECT odt " +
			     " FROM OrdenDeTrabajo odt " +
			     " JOIN odt.maquinaActual maq " + 
			     " WHERE maq.tipoMaquina.idTipoSector = :idSector AND odt.idAvance IS NOT NULL AND odt.idAvance != :idAvanceFinalizado" +
			     " ORDER BY odt.ordenEnMaquina ";
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("idSector", sector.getId());
		q.setParameter("idAvanceFinalizado", EAvanceODT.FINALIZADO.getId());
		return q.getResultList();
	}

}