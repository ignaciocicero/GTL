package ar.com.textillevel.dao.impl;

import javax.ejb.Stateless;
import javax.persistence.Query;

import ar.com.fwcommon.dao.impl.GenericDAO;
import ar.com.textillevel.dao.api.local.ImpuestoItemDAOLocal;
import ar.com.textillevel.entidades.documentos.factura.proveedor.ImpuestoItemProveedor;
import ar.com.textillevel.entidades.enums.ETipoImpuesto;
import ar.com.textillevel.entidades.gente.Provincia;

@Stateless
public class ImpuestoItemDAO extends GenericDAO<ImpuestoItemProveedor, Integer> implements ImpuestoItemDAOLocal {

	public boolean existsOtroImpuestoWithParams(Integer idImpuesto, double porcDescuento, ETipoImpuesto tipoImpuesto, Provincia provincia) {
		Query query = getEntityManager().createQuery("SELECT 1 " +
													 "FROM  ImpuestoItemProveedor imp " +
													 "WHERE imp.id != :idImpuesto AND " +
													 " imp.porcDescuento = :porcDescuento AND" +
													 " imp.idTipoImpuesto = :idTipoImpuesto "+
													 (provincia!=null?" AND imp.provincia.id = :idProvincia ":""));
		query.setParameter("idImpuesto", idImpuesto == null ? -1 : idImpuesto);
		query.setParameter("porcDescuento", porcDescuento);
		query.setParameter("idTipoImpuesto", tipoImpuesto.getId());
		if(provincia !=null){
			query.setParameter("idProvincia", provincia.getId());
		}
		return !query.getResultList().isEmpty();
	}

	public ImpuestoItemProveedor getByTipoYPorcentaje(ETipoImpuesto tipoImpuesto, Double porcentaje) {
		Query query = getEntityManager().createQuery(" SELECT imp FROM ImpuestoItemProveedor imp " +
				 									 " WHERE imp.porcDescuento = :porcDescuento AND" +
				 									 " 		 imp.idTipoImpuesto = :idTipoImpuesto ");
		query.setParameter("porcDescuento", porcentaje);
		query.setParameter("idTipoImpuesto", tipoImpuesto.getId());
		return (ImpuestoItemProveedor) query.getSingleResult();
	}

}
