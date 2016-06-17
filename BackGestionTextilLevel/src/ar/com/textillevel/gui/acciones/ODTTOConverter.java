package ar.com.textillevel.gui.acciones;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import ar.com.textillevel.entidades.documentos.remito.PiezaRemito;
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.documentos.remito.to.DetalleRemitoEntradaNoFacturado;
import ar.com.textillevel.entidades.enums.ETipoProducto;
import ar.com.textillevel.entidades.ventas.ProductoArticulo;
import ar.com.textillevel.facade.api.remote.ArticuloFacadeRemote;
import ar.com.textillevel.facade.api.remote.ClienteFacadeRemote;
import ar.com.textillevel.facade.api.remote.CondicionDeVentaFacadeRemote;
import ar.com.textillevel.facade.api.remote.PrecioMateriaPrimaFacadeRemote;
import ar.com.textillevel.facade.api.remote.ProductoArticuloFacadeRemote;
import ar.com.textillevel.facade.api.remote.ProveedorFacadeRemote;
import ar.com.textillevel.facade.api.remote.RemitoEntradaFacadeRemote;
import ar.com.textillevel.facade.api.remote.TarimaFacadeRemote;
import ar.com.textillevel.gui.acciones.odtwsclient.OdtEagerTO;
import ar.com.textillevel.gui.acciones.odtwsclient.PasoSecuenciaODTTO;
import ar.com.textillevel.gui.acciones.odtwsclient.PiezaODTTO;
import ar.com.textillevel.gui.acciones.odtwsclient.PiezaRemitoTO;
import ar.com.textillevel.gui.acciones.odtwsclient.RemitoEntradaTO;
import ar.com.textillevel.gui.acciones.odtwsclient.SecuenciaODTTO;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.PiezaODT;
import ar.com.textillevel.modulos.odt.entidades.maquinas.TipoMaquina;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.PasoSecuenciaODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.SecuenciaODT;
import ar.com.textillevel.modulos.odt.enums.EAvanceODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;
import ar.com.textillevel.modulos.odt.facade.api.remote.MaquinaFacadeRemote;
import ar.com.textillevel.modulos.odt.facade.api.remote.TipoMaquinaFacadeRemote;
import ar.com.textillevel.util.GTLBeanFactory;

public final class ODTTOConverter {

	private static final MaquinaFacadeRemote maquinaFacade = GTLBeanFactory.getInstance().getBean2(MaquinaFacadeRemote.class);
	private static final ProductoArticuloFacadeRemote productoArticuloFacade = GTLBeanFactory.getInstance().getBean2(ProductoArticuloFacadeRemote.class);
	private static final PrecioMateriaPrimaFacadeRemote precioMPFacade = GTLBeanFactory.getInstance().getBean2(PrecioMateriaPrimaFacadeRemote.class);
	private static final ClienteFacadeRemote clienteFacade = GTLBeanFactory.getInstance().getBean2(ClienteFacadeRemote.class);
	private static final ProveedorFacadeRemote proveedorFacade = GTLBeanFactory.getInstance().getBean2(ProveedorFacadeRemote.class);
	private static final ArticuloFacadeRemote articuloFacade = GTLBeanFactory.getInstance().getBean2(ArticuloFacadeRemote.class);
	private static final CondicionDeVentaFacadeRemote condicionVentaFacade = GTLBeanFactory.getInstance().getBean2(CondicionDeVentaFacadeRemote.class);
	private static final TarimaFacadeRemote tarimaFacade = GTLBeanFactory.getInstance().getBean2(TarimaFacadeRemote.class);
	private static final TipoMaquinaFacadeRemote tipoMaquinaFacade = GTLBeanFactory.getInstance().getBean2(TipoMaquinaFacadeRemote.class);
	private static final RemitoEntradaFacadeRemote remitoEntradaFacade = GTLBeanFactory.getInstance().getBean2(RemitoEntradaFacadeRemote.class);
	
	private ODTTOConverter() {

	}

	public static OrdenDeTrabajo fromTO(OdtEagerTO odtEagerTO) {
		OrdenDeTrabajo odt = new OrdenDeTrabajo();
		odt.setAvance(EAvanceODT.getById(odtEagerTO.getIdAvance()));
		odt.setCodigo(odtEagerTO.getCodigo());
		odt.setEstadoODT(EEstadoODT.getById(odtEagerTO.getIdEstadoODT()));
		odt.setFechaODT(new Timestamp(odtEagerTO.getTimestampFechaODT()));
		odt.setId(null); // Para que quede claro que no quiero ID del otro lado porque se tiene que persistir de cero
		odt.setOrdenEnMaquina(odtEagerTO.getOrdenEnMaquina());
		if (odtEagerTO.getIdMaquinaActual() != null) {
			odt.setMaquinaActual(maquinaFacade.getByIdEager(odtEagerTO.getIdMaquinaActual()));
		}
		if (odtEagerTO.getIdMaquinaPrincipal() != null) {
			odt.setMaquinaPrincipal(maquinaFacade.getByIdEager(odtEagerTO.getIdMaquinaPrincipal()));
		}
		if (odtEagerTO.getIdProductoArticulo() != null) {
			odt.setProductoArticulo(productoArticuloFacade.getById(odtEagerTO.getIdProductoArticulo()));
		}
		if (odtEagerTO.getPiezas() != null && odtEagerTO.getPiezas().length > 0) {
			List<PiezaODT> piezas = new ArrayList<PiezaODT>();
			for (PiezaODTTO piezaODTTO : odtEagerTO.getPiezas()) {
				piezas.add(piezaODTFromTO(odt, piezaODTTO));
			}
			odt.setPiezas(piezas);
		}

		odt.setSecuenciaDeTrabajo(secuenciaODTFromTO(odt, odtEagerTO.getSecuenciaDeTrabajo()));
		odt.setRemito(remitoEntradaFromTO(odt, odtEagerTO.getRemito()));
		return odt;
	}

	private static RemitoEntrada remitoEntradaFromTO(OrdenDeTrabajo odt, RemitoEntradaTO remitoTO) {
		if (remitoTO == null) {
			return null;
		}
		RemitoEntrada remitoEntrada = new RemitoEntrada();
		remitoEntrada.setId(remitoTO.getId()); // Es el �nico que interesa que sobreviva para despu�s referenciarlo y borrarlo del otro lado!
		remitoEntrada.setNroRemito(remitoTO.getNroRemito());
		remitoEntrada.setAnchoCrudo(remitoTO.getAnchoCrudo());
		remitoEntrada.setAnchoFinal(remitoTO.getAnchoFinal());
		remitoEntrada.setEnPalet(remitoTO.getEnPalet());
		remitoEntrada.setFechaEmision(new Date(remitoTO.getDateFechaEmision()));
		remitoEntrada.setPesoTotal(remitoTO.getPesoTotal());
		if (remitoTO.getIdCliente() != null) {
			remitoEntrada.setCliente(clienteFacade.getById(remitoTO.getIdCliente()));
		}
		if (remitoTO.getIdArticuloStock() != null) {
			remitoEntrada.setArticuloStock(articuloFacade.getById(remitoTO.getIdArticuloStock()));
		}
		if (remitoTO.getIdCondicionDeVenta() != null) {
			remitoEntrada.setCondicionDeVenta(condicionVentaFacade.getById(remitoTO.getIdCondicionDeVenta()));
		}
		if (remitoTO.getIdProveedor() != null) {
			remitoEntrada.setProveedor(proveedorFacade.getById(remitoTO.getIdProveedor()));
		}
		if (remitoTO.getIdPrecioMatPrima() != null) {
			remitoEntrada.setPrecioMatPrima(precioMPFacade.getById(remitoTO.getIdPrecioMatPrima()));
		}
		if (remitoTO.getProductoArticuloIdsList() != null && remitoTO.getProductoArticuloIdsList().length > 0) {
			List<ProductoArticulo> productoArticuloList = new ArrayList<ProductoArticulo>();
			for (Integer productoArticuloId : remitoTO.getProductoArticuloIdsList()) {
				productoArticuloList.add(productoArticuloFacade.getById(productoArticuloId));
			}
			remitoEntrada.setProductoArticuloList(productoArticuloList);
		}
		if (remitoTO.getIdTarima() != null) {
			remitoEntrada.setTarima(tarimaFacade.getById(remitoTO.getIdTarima()));
		}
		if (remitoTO.getPiezas() != null && remitoTO.getPiezas().length > 0) {
			List<PiezaRemito> piezasRemito = new ArrayList<PiezaRemito>();
			for (PiezaRemitoTO piezaRemitoTO : remitoTO.getPiezas()) {
				piezasRemito.add(piezaRemitoFromTO(odt, piezaRemitoTO));
			}
			remitoEntrada.setPiezas(piezasRemito);
		}
		return remitoEntrada;
	}

	private static SecuenciaODT secuenciaODTFromTO(OrdenDeTrabajo odt, SecuenciaODTTO secuenciaODTTO) {
		if (secuenciaODTTO == null) {
			return null;
		}
		SecuenciaODT secuenciaODT = new SecuenciaODT();
		secuenciaODT.setCliente(clienteFacade.getById(secuenciaODTTO.getIdCliente()));
		secuenciaODT.setId(null); // Para que quede claro que no quiero ID del otro lado porque se tiene que persistir de cero
		secuenciaODT.setNombre(secuenciaODTTO.getNombre());
		secuenciaODT.setOdt(odt);
		secuenciaODT.setTipoProducto(ETipoProducto.getById(secuenciaODTTO.getIdTipoProducto()));
		if (secuenciaODTTO.getPasosSecuencia() != null && secuenciaODTTO.getPasosSecuencia().length > 0) {
			List<PasoSecuenciaODT> pasos = new ArrayList<PasoSecuenciaODT>();
			for (PasoSecuenciaODTTO pasoSecuenciaODTTO : secuenciaODTTO.getPasosSecuencia()) {
				PasoSecuenciaODT pasoSecuenciaODT = new PasoSecuenciaODT();
				pasoSecuenciaODT.setId(null); // Para que quede claro que no quiero ID del otro lado porque se tiene que persistir de cero
				pasoSecuenciaODT.setObservaciones(pasoSecuenciaODTTO.getObservaciones());
				TipoMaquina sector = tipoMaquinaFacade.getByIdEager(pasoSecuenciaODTTO.getIdSector(), TipoMaquinaFacadeRemote.MASK_PROCESOS | TipoMaquinaFacadeRemote.MASK_SUBPROCESOS | TipoMaquinaFacadeRemote.MASK_INSTRUCCIONES);
				pasoSecuenciaODT.setSector(sector);
				pasoSecuenciaODT.setProceso(sector.getProcesoById(pasoSecuenciaODTTO.getIdProceso()));
				//creaci�n del subrproceso
//				pasoSecuenciaODT.setSubProceso(subProcesoODTFromTO(pasoSecuenciaODTTO.getSubProceso()));
				//TODO: Se posterga hasta que est�n actualizadas las entidades del WS
				pasos.add(pasoSecuenciaODT);
			}
			secuenciaODT.setPasos(pasos);
		}
		return secuenciaODT;
	}

	private static PiezaODT piezaODTFromTO(OrdenDeTrabajo odt, PiezaODTTO piezaODTTO) {
		if (piezaODTTO == null) {
			return null;
		}
		PiezaODT piezaODT = new PiezaODT();
		piezaODT.setId(null); // Para que quede claro que no quiero ID del otro lado porque se tiene que persistir de cero
		piezaODT.setMetrosStockInicial(piezaODT.getMetrosStockInicial());
		piezaODT.setNroPiezaStockInicial(piezaODT.getNroPiezaStockInicial());
		piezaODT.setOdt(odt);
		if (piezaODTTO.getPiezaRemito() != null) {
			piezaODT.setPiezaRemito(piezaRemitoFromTO(odt, piezaODTTO.getPiezaRemito()));
		}
		List<PiezaRemito> piezasSalida = new ArrayList<PiezaRemito>();
		if (piezaODTTO.getPiezasSalida() != null && piezaODTTO.getPiezasSalida().length > 0) {
			for (PiezaRemitoTO piezaRemitoTO : piezaODTTO.getPiezasSalida()) {
				piezasSalida.add(piezaRemitoFromTO(odt, piezaRemitoTO));
			}
		}
		piezaODT.setPiezasSalida(piezasSalida);
		return piezaODT;
	}

	private static PiezaRemito piezaRemitoFromTO(OrdenDeTrabajo odt, PiezaRemitoTO piezaRemitoTO) {
		if (piezaRemitoTO == null) {
			return null;
		}
		PiezaRemito piezaRemito = new PiezaRemito();
		piezaRemito.setEnSalida(piezaRemitoTO.getEnSalida());
		piezaRemito.setId(null); // Para que quede claro que no quiero ID del otro lado porque se tiene que persistir de cero
		piezaRemito.setMetros(piezaRemitoTO.getMetros());
		piezaRemito.setObservaciones(piezaRemitoTO.getObservaciones());
		piezaRemito.setOrdenPieza(piezaRemitoTO.getOrdenPieza());
		piezaRemito.setOrdenPiezaCalculado(piezaRemitoTO.getOrdenPiezaCalculado());
		piezaRemito.setPiezaSinODT(piezaRemitoTO.getPiezaSinODT());
		if (piezaRemitoTO.getIdPmpDescuentoStock() != null) {
			piezaRemito.setPmpDescuentoStock(precioMPFacade.getById(piezaRemitoTO.getIdPmpDescuentoStock()));
		}
		if (piezaRemitoTO.getPiezaEntrada() != null) {
			piezaRemito.setPiezaEntrada(piezaRemitoFromTO(odt, piezaRemitoTO.getPiezaEntrada())); // OJO, recursividad
		}
		if (piezaRemitoTO.getPiezasPadreODT() != null && piezaRemitoTO.getPiezasPadreODT().length > 0) {
			PiezaODTTO[] piezasPadreODT = piezaRemitoTO.getPiezasPadreODT();
			List<PiezaODT> piezasPadrePODT = new ArrayList<PiezaODT>();
			for (PiezaODTTO p : piezasPadreODT) {
				piezasPadrePODT.add(piezaODTFromTO(odt, p));
			}
			piezaRemito.setPiezasPadreODT(piezasPadrePODT);
		}

		return piezaRemito;
	}

	private static PiezaRemitoTO piezaRemitoTOFromEntity(PiezaRemito piezaRemito) {
		if (piezaRemito == null) {
			return null;
		}
		PiezaRemitoTO piezaRemitoTO = new PiezaRemitoTO();
		piezaRemitoTO.setEnSalida(piezaRemitoTO.getEnSalida());
		piezaRemitoTO.setId(null); // Para que quede claro que no quiero ID del otro lado porque se tiene que persistir de cero
		piezaRemitoTO.setMetros(piezaRemitoTO.getMetros());
		piezaRemitoTO.setObservaciones(piezaRemitoTO.getObservaciones());
		piezaRemitoTO.setOrdenPieza(piezaRemitoTO.getOrdenPieza());
		piezaRemitoTO.setOrdenPiezaCalculado(piezaRemitoTO.getOrdenPiezaCalculado());
		piezaRemitoTO.setPiezaSinODT(piezaRemitoTO.getPiezaSinODT());
		if (piezaRemito.getPmpDescuentoStock() != null) {
			piezaRemitoTO.setIdPmpDescuentoStock(piezaRemito.getPmpDescuentoStock().getId());
		}
		if (piezaRemito.getPiezaEntrada() != null) {
			piezaRemitoTO.setPiezaEntrada(piezaRemitoTOFromEntity(piezaRemito.getPiezaEntrada())); // OJO, recursividad
		}
		if (piezaRemito.getPiezasPadreODT() != null && piezaRemito.getPiezasPadreODT().size() > 0) {
			List<PiezaODT> piezasPadreODT = piezaRemito.getPiezasPadreODT();
			PiezaODTTO[] piezasPadrePODT = new PiezaODTTO[piezasPadreODT.size()];
			int i = 0;
			for (PiezaODT p : piezasPadreODT) {
				piezasPadrePODT[i++] = piezaODTTOFromEntity(p);
			}
			piezaRemitoTO.setPiezasPadreODT(piezasPadrePODT);
		}

		return piezaRemitoTO;
	}
	
	private static PiezaODTTO piezaODTTOFromEntity(PiezaODT piezaODT) {
		if (piezaODT == null) {
			return null;
		}
		PiezaODTTO piezaODTTO = new PiezaODTTO();
		piezaODTTO.setId(null);	// Para que quede claro que no quiero ID del otro lado porque se tiene que persistir de cero
		piezaODTTO.setMetrosStockInicial(piezaODT.getMetrosStockInicial());
		piezaODTTO.setNroPiezaStockInicial(piezaODT.getNroPiezaStockInicial());
//		piezaODTTO.setOdt(odt); // TODO ? ---> TODA LA ODT??????????
		if (piezaODTTO.getPiezaRemito() != null) {
			piezaODTTO.setPiezaRemito(piezaRemitoTOFromEntity(piezaODT.getPiezaRemito()));
		}
		List<PiezaRemitoTO> piezasSalida = new ArrayList<PiezaRemitoTO>();
		if (piezaODT.getPiezasSalida() != null && piezaODT.getPiezasSalida().size() > 0) {
			for (PiezaRemito piezaRemitoTO : piezaODT.getPiezasSalida()) {
				piezasSalida.add(piezaRemitoTOFromEntity(piezaRemitoTO));
			}
		}
		piezaODTTO.setPiezasSalida(piezasSalida.toArray(new PiezaRemitoTO[piezasSalida.size()]));
		return piezaODTTO;
	}

	@SuppressWarnings("unused")
	public static RemitoEntradaTO toRemitoWSTO(DetalleRemitoEntradaNoFacturado elemento) {
		if (true) {
			throw new UnsupportedOperationException();
		}
		RemitoEntrada re = remitoEntradaFacade.getByIdEager(elemento.getId());
		if (re == null) {
			throw new RuntimeException("Remito no encontrado");
		}
		
		RemitoEntradaTO remitoTO = new RemitoEntradaTO();
		remitoTO.setAnchoCrudo(re.getAnchoCrudo());
		remitoTO.setAnchoFinal(re.getAnchoFinal());
		remitoTO.setDateFechaEmision(re.getFechaEmision().getTime());
		remitoTO.setEnPalet(re.getEnPalet());
		remitoTO.setId(null);	// Para que quede claro que no quiero ID del otro lado porque se tiene que persistir de cero
		remitoTO.setIdArticuloStock(re.getArticuloStock().getId());
		remitoTO.setIdCliente(re.getCliente().getId());
		remitoTO.setIdCondicionDeVenta(re.getCondicionDeVenta().getId());
		remitoTO.setIdPrecioMatPrima(re.getPrecioMatPrima().getId());
		remitoTO.setIdProveedor(re.getProveedor().getId());
		remitoTO.setIdTarima(re.getTarima().getId());
		remitoTO.setNroRemito(re.getNroRemito());
		remitoTO.setPesoTotal(re.getPesoTotal());
		remitoTO.setProductoArticuloIdsList(
				FluentIterable.from(re.getProductoArticuloList()).transform(new Function<ProductoArticulo, Integer>() {
					public Integer apply(ProductoArticulo pa) {
						return pa.getId();
					}
				}).toArray(Integer.class));
		remitoTO.setPiezas(FluentIterable.from(re.getPiezas()).transform(new Function<PiezaRemito, PiezaRemitoTO>() {
			public PiezaRemitoTO apply(PiezaRemito pr) {
				return piezaRemitoTOFromEntity(pr);
			}
		}).toArray(PiezaRemitoTO.class));
		return remitoTO;
	}
}
