package ar.com.textillevel.dao.api.local;

import javax.ejb.Local;

import ar.com.fwcommon.dao.api.local.DAOLocal;
import ar.com.textillevel.entidades.cheque.Cheque;
import ar.com.textillevel.entidades.documentos.pagopersona.OrdenDePagoAPersona;

@Local
public interface OrdenDePagoPersonaDAOLocal extends DAOLocal<OrdenDePagoAPersona, Integer>{

	public Integer getUltimoNumeroOrden();

	public OrdenDePagoAPersona getOrdenByNro(Integer nroOrden);
	
	public OrdenDePagoAPersona getByCheque(Cheque ch);

}
