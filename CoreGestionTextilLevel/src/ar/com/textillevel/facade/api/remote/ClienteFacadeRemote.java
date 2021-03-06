package ar.com.textillevel.facade.api.remote;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import ar.com.fwcommon.componentes.error.validaciones.ValidacionException;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.entidades.to.ClienteDeudaTO;

@Remote
public interface ClienteFacadeRemote {

	public abstract List<Cliente> getAllOrderByName();
	public abstract Cliente save(Cliente cliente) throws ValidacionException;
	public abstract void remove(Cliente clienteActual);
	public abstract boolean existeNroCliente(Integer idCliente, Integer nroCliente);
	public abstract Cliente getClienteByNumero(Integer nroCliente);
	public abstract List<Cliente> getAllByRazonSocial(String razSoc);
	public abstract List<ClienteDeudaTO> getClientesDeudores();
	public abstract List<ClienteDeudaTO> getClientesConDeudaMayorA(BigDecimal monto);
	public abstract Cliente getById(Integer idCliente);
	public abstract Set<String> getCuits();

}
