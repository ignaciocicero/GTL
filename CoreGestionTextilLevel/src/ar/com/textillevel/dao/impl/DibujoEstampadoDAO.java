package ar.com.textillevel.dao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import ar.com.fwcommon.dao.impl.GenericDAO;
import ar.com.textillevel.dao.api.local.DibujoEstampadoDAOLocal;
import ar.com.textillevel.entidades.ventas.articulos.DibujoEstampado;
import ar.com.textillevel.entidades.ventas.articulos.VarianteEstampado;

@Stateless
public class DibujoEstampadoDAO extends GenericDAO<DibujoEstampado, Integer> implements DibujoEstampadoDAOLocal {

	@SuppressWarnings("unchecked")
	public DibujoEstampado getByIdEager(Integer idDibujoEstampado) {
		Query query = getEntityManager().createQuery("FROM DibujoEstampado AS de " +
				 									 "LEFT JOIN FETCH de.variantes var " +
				 									 "WHERE de.id = :idDibujoEstampado");
		query.setParameter("idDibujoEstampado", idDibujoEstampado);
		List<DibujoEstampado> resultList = query.getResultList();
		if(resultList.isEmpty()) {
			return null;
		}
		DibujoEstampado dibujoEstampado = (DibujoEstampado)resultList.get(0);
		for(VarianteEstampado variante : dibujoEstampado.getVariantes()) {
			variante.getColores().size();
		}
		return dibujoEstampado;
	}

	public boolean existsNroDibujo(Integer idDibujo, Integer nro) {
		Query query = getEntityManager().createQuery("SELECT 1 FROM DibujoEstampado AS de " +
													 "WHERE de.id != :idDibujo AND de.nroDibujo = :nro");
		query.setParameter("idDibujo", idDibujo == null ? -1 : idDibujo);
		query.setParameter("nro", nro);
		return !query.getResultList().isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DibujoEstampado> getByNroCliente(Integer nroCliente) {
		Query query = getEntityManager().createQuery(" SELECT de FROM DibujoEstampado de " +
													 " LEFT JOIN FETCH de.variantes var " +
													 " LEFT JOIN FETCH de.cliente cl " +
													 (nroCliente != null ? " WHERE cl.nroCliente = :nroCliente" : "") +
													 " ORDER BY de.nroDibujo ASC ");
		if (nroCliente != null) {
			query.setParameter("nroCliente", nroCliente);
		}
		List<DibujoEstampado> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		for (DibujoEstampado de : resultList) {
			for (VarianteEstampado variante : de.getVariantes()) {
				variante.getColores().size();
			}
		}
		return resultList;
	}

}