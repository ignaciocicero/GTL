package ar.com.textillevel.gui.modulos.odt.gui;

import java.awt.Frame;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.FWJTable;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.textillevel.entidades.documentos.remito.RemitoEntrada;
import ar.com.textillevel.entidades.enums.ETipoProducto;
import ar.com.textillevel.entidades.gente.Cliente;
import ar.com.textillevel.entidades.ventas.ProductoArticulo;
import ar.com.textillevel.entidades.ventas.articulos.Articulo;
import ar.com.textillevel.entidades.ventas.articulos.Color;
import ar.com.textillevel.entidades.ventas.articulos.TipoArticulo;
import ar.com.textillevel.entidades.ventas.productos.ProductoTenido;
import ar.com.textillevel.facade.api.remote.ClienteFacadeRemote;
import ar.com.textillevel.gui.modulos.odt.gui.tenido.JDialogEditarFormula;
import ar.com.textillevel.gui.modulos.odt.gui.tenido.PersisterFormulaHandler;
import ar.com.textillevel.gui.modulos.odt.impresion.EFormaImpresionODT;
import ar.com.textillevel.gui.modulos.odt.impresion.ImprimirODTHandler;
import ar.com.textillevel.gui.util.GenericUtils;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.entidades.maquinas.TipoMaquina;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.CreadorFormulasVisitor;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.tenido.FormulaTenidoCliente;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.tenido.TenidoTipoArticulo;
import ar.com.textillevel.modulos.odt.entidades.maquinas.procesos.InstruccionProcedimiento;
import ar.com.textillevel.modulos.odt.entidades.maquinas.procesos.InstruccionProcedimientoPasadas;
import ar.com.textillevel.modulos.odt.entidades.maquinas.procesos.ProcedimientoTipoArticulo;
import ar.com.textillevel.modulos.odt.entidades.maquinas.procesos.ProcesoTipoMaquina;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoTipoProductoODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.PasoSecuenciaODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.ProcedimientoODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.SecuenciaODT;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;
import ar.com.textillevel.modulos.odt.facade.api.remote.TipoMaquinaFacadeRemote;
import ar.com.textillevel.util.GTLBeanFactory;

public class PanTablaFormulasTenido extends PanelTablaFormula<FormulaTenidoCliente> {

	private static final long serialVersionUID = 1L;

	private static final Integer CANT_COLS = 6;
	private static final Integer COL_COLOR = 0;
	private static final Integer COL_TIPO_ARTICULO = 1;
	private static final Integer COL_NOMBRE = 2;
	private static final Integer COL_CODIGO = 3;
	private static final Integer COL_VERIFICADA = 4;
	private static final Integer COL_OBJ = 5;

	private PanTablaQuimicos panQuimicos;
	
	private Color colorFixed;
	private TipoArticulo tipoArticuloFixed;

	public PanTablaFormulasTenido(Frame owner, PersisterFormulaHandler persisterFormulaHandler) {
		super(owner, ETipoProducto.TENIDO, persisterFormulaHandler);
	}

	public void ocultarBotones() {
		getBotonEliminar().setVisible(false);
		if(getBotonModificar()!=null){
			getBotonModificar().setVisible(false);
		}
	}

	@Override
	protected void botonModificarPresionado(int filaSeleccionada) {
		edicionFormula(filaSeleccionada);
	}

	@Override
	protected FWJTable construirTabla() {
		FWJTable tabla = new FWJTable(0, CANT_COLS);
		tabla.setStringColumn(COL_COLOR, "COLOR", 180, 180, true);
		tabla.setStringColumn(COL_TIPO_ARTICULO, "TIPO DE ART�CULO", 180, 180, true);
		tabla.setStringColumn(COL_NOMBRE, "NOMBRE DE LA F�RMULA", 150, 150, true);
		tabla.setStringColumn(COL_CODIGO, "C�DIGO", 70, 70, true);
		tabla.setCheckColumn(COL_VERIFICADA, "VERIFICADA", 90, true);		
		tabla.setStringColumn(COL_OBJ, "", 0, 0, true);
		return tabla;
	}

	@Override
	protected void agregarElemento(FormulaTenidoCliente elemento) {
		Object[] row = new Object[CANT_COLS];
		row[COL_NOMBRE] = elemento.getNombre();
		row[COL_TIPO_ARTICULO] = elemento.getTipoArticulo().getNombre();
		row[COL_COLOR] = elemento.getColor().getNombre();
		row[COL_CODIGO] = elemento.getCodigoFormula();
		row[COL_VERIFICADA] = calcVerificada(elemento.getVerificada());
		row[COL_OBJ] = elemento;
		getTabla().addRow(row);
	}

	@Override
	protected void dobleClickTabla(int filaSeleccionada) {
		edicionFormula(filaSeleccionada);
	}

	private void edicionFormula(int filaSeleccionada) {
		FormulaTenidoCliente formula = getElemento(filaSeleccionada);
		JDialogEditarFormula dialogo = new JDialogEditarFormula(owner, formula, modoConsulta);
		GuiUtil.centrarEnPadre(dialogo);
		dialogo.setVisible(true);
		if(dialogo.isAcepto()) {
			getTabla().setValueAt(formula.getNombre(), filaSeleccionada, COL_NOMBRE);
			getTabla().setValueAt(formula.getColor(), filaSeleccionada, COL_COLOR);
			getTabla().setValueAt(formula.getTipoArticulo().getNombre(), filaSeleccionada, COL_TIPO_ARTICULO);
			getTabla().setValueAt(formula, filaSeleccionada, COL_OBJ);
			panQuimicos.setTenidos(formula.getTenidosComponentes());
			persisterFormulaHandler.addFormulaParaGrabar(tipoProducto, formula);			
		}
	}

	@Override
	protected void filaTablaSeleccionada() {
		int selectedRow = getTabla().getSelectedRow();
		if(selectedRow == -1) {
			panQuimicos.limpiar();
		} else {
			FormulaTenidoCliente formula = getElemento(selectedRow);
			panQuimicos.setTenidos(formula.getTenidosComponentes());
		}
	}

	@Override
	protected FormulaTenidoCliente getElemento(int fila) {
		if(fila == -1) {
			return null;
		} else {
			return (FormulaTenidoCliente)getTabla().getValueAt(fila, COL_OBJ);
		}
	}

	@Override
	protected String validarElemento(int fila) {
		return null;
	}

	@Override
	public boolean validarAgregar() {
		FormulaTenidoCliente formula = new FormulaTenidoCliente();
		formula.setColor(colorFixed);
		if(tipoArticuloFixed != null) {
			formula.setTipoArticulo(tipoArticuloFixed);
			List<TipoArticulo> tiposArticulos = new ArrayList<TipoArticulo>();
			if(tipoArticuloFixed.getTiposArticuloComponentes().isEmpty()) {
				tiposArticulos.add(tipoArticuloFixed);
			} else {
				tiposArticulos.addAll(tipoArticuloFixed.getTiposArticuloComponentes());
			}
			for(TipoArticulo ta : tiposArticulos) {
				TenidoTipoArticulo tta = new TenidoTipoArticulo();
				tta.setTipoArticulo(ta);
				formula.getTenidosComponentes().add(tta);
			}
		}
		
		JDialogEditarFormula dialogo = new JDialogEditarFormula(owner, formula, false);
		GuiUtil.centrarEnPadre(dialogo);
		dialogo.setVisible(true);
		if(dialogo.isAcepto()) {
			agregarElemento(dialogo.getFormula());
			persisterFormulaHandler.addFormulaParaGrabar(tipoProducto, dialogo.getFormula());			
		}
		return false;
	}

	@Override
	public FormulaTenidoCliente getFormulaElegida() {
		if(getTabla().getSelectedRow()!=-1){
			return getElemento(getTabla().getSelectedRow());
		}
		return null;
	}

	@Override
	public void agregarFormula(FormulaTenidoCliente formula) {
		agregarElemento(formula);
	}

	public void setPanQuimicos(PanTablaQuimicos panQuimicos) {
		this.panQuimicos = panQuimicos;
	}

	public void setColorFixed(Color colorFixed) {
		this.colorFixed = colorFixed;
	}

	public void setTipoArticuloFixed(TipoArticulo tipoArticuloFixed) {
		this.tipoArticuloFixed = tipoArticuloFixed;
	}

	@Override
	public void imprimirFormula(FormulaTenidoCliente formula) throws IOException {
		boolean ok = false;
		do {
			String input = JOptionPane.showInputDialog(owner, "Ingrese el total de kilos:", "Imprimir F�rmula", JOptionPane.INFORMATION_MESSAGE);
			if (input == null) {
				break;
			}
			if (input.trim().length()==0 || !GenericUtils.esNumerico(input)) {
				FWJOptionPane.showErrorMessage(owner, "Ingreso incorrecto", "error");
			} else {
				ok = true;				
				OrdenDeTrabajo odtDummy = getODTDummy(formula, Integer.valueOf(input));
				if (odtDummy == null) {
					ok = true;
					break;
				}
				String[] values = {"ODT", "MUESTRA"};
				Object selected = JOptionPane.showInputDialog(null, "ES ODT O MUESTRA?", "Opciones", JOptionPane.DEFAULT_OPTION, null, values, "ODT");
				if (selected != null ){//null if the user cancels. 
				    String selectedString = selected.toString();
				    ImprimirODTHandler impHandler = new ImprimirODTHandler(odtDummy, owner, EFormaImpresionODT.RESUMEN_ARTIULOS, formula, selectedString.equalsIgnoreCase(values[1]));
					impHandler.imprimir();
				}
			}
		} while(!ok);
	}

	private OrdenDeTrabajo getODTDummy(FormulaTenidoCliente formula, Integer totalKG) {
		OrdenDeTrabajo odt = new OrdenDeTrabajo();
		odt.setCodigo("20100902");

		Integer idCliente = formula.getCliente()!=null?formula.getCliente().getId():1;
		Cliente cliente = GTLBeanFactory.getInstance().getBean2(ClienteFacadeRemote.class).getById(idCliente);
		
		SecuenciaODT secuencia = new SecuenciaODT();
		PasoSecuenciaODT unicoPaso = new PasoSecuenciaODT();
		int mask = TipoMaquinaFacadeRemote.MASK_PROCESOS | TipoMaquinaFacadeRemote.MASK_SUBPROCESOS | TipoMaquinaFacadeRemote.MASK_INSTRUCCIONES;
		TipoMaquina tipo = GTLBeanFactory.getInstance().getBean2(TipoMaquinaFacadeRemote.class).getByIdEager(ESectorMaquina.SECTOR_HUMEDO.getId().intValue(), mask);
		unicoPaso.setSector(tipo);
		secuencia.getPasos().add(unicoPaso);
		ProcedimientoODT unicoSubroceso = new ProcedimientoODT();
		unicoSubroceso.setNombre("SUBPROCESO_PRUEBA");

		ProcesoTipoMaquina unicoProceso = new ProcesoTipoMaquina();
		unicoProceso.setNombre("PROCESO_PRUEBA");

		unicoPaso.setSubProceso(unicoSubroceso);
		unicoPaso.setProceso(unicoProceso);

		odt.setSecuenciaDeTrabajo(secuencia);

		ProductoArticulo pa = new ProductoArticulo();
		pa.setColor(formula.getColor());
		ProductoTenido p = new ProductoTenido();
		pa.setProducto(p);

		Articulo art = new Articulo();
		art.setNombre("ART_PRUEBA");
		pa.setArticulo(art);
		odt.setProductoArticulo(pa);

		RemitoEntrada reDummy = new RemitoEntrada();
		reDummy.setPesoTotal(new BigDecimal(totalKG));
		reDummy.setNroRemito(10);
		reDummy.setCliente(cliente);
		odt.setRemito(reDummy);
		
		TipoArticulo ta = formula.getTipoArticulo();
		InstruccionProcedimientoPasadas instruccionProcedimiento = null;
		instruccionProcedimiento = buscarDescrude(tipo, ta, instruccionProcedimiento);
		
		if (instruccionProcedimiento != null) {
			Float litros = null;
			boolean ok = false;
			do {
				String input = JOptionPane.showInputDialog(owner, "Ingrese el total de litros:", "Imprimir F�rmula", JOptionPane.INFORMATION_MESSAGE);
				if (input == null) {
					return null;
				}
				if (input.trim().length()==0 || !GenericUtils.esNumerico(input)) {
					FWJOptionPane.showErrorMessage(owner, "Ingreso incorrecto", "error");
				} else {
					ok = true;
					litros = Float.valueOf(input);
				}
			} while(!ok);
			
			
			ExplotadorInstrucciones explotador = new ExplotadorInstrucciones(odt, litros);
			instruccionProcedimiento.accept(explotador);
			InstruccionProcedimientoODT instruccionExplotada = explotador.getInstruccionExplotada();
			unicoSubroceso.getPasos().add(instruccionExplotada);
		}
		
		CreadorFormulasVisitor visitor = new CreadorFormulasVisitor(odt);
		formula.accept(visitor);

		InstruccionProcedimientoTipoProductoODT ipODT = new InstruccionProcedimientoTipoProductoODT();
		ipODT.setFormula(visitor.getFormulaExplotada());
		ipODT.setTipoProducto(ETipoProducto.TENIDO);
		unicoSubroceso.getPasos().add(ipODT);
		return odt;
	}

	private InstruccionProcedimientoPasadas buscarDescrude(TipoMaquina tipo, TipoArticulo ta, InstruccionProcedimientoPasadas instruccionProcedimiento) {
		for(ProcesoTipoMaquina ptm : tipo.getProcesos()) {
			if (ptm.getNombre().toLowerCase().indexOf("descrude") != -1) {
				for (ProcedimientoTipoArticulo pta : ptm.getProcedimientos()) {
					if (pta.getTipoArticulo().equals(ta)) {
						for (InstruccionProcedimiento instruccionProcedimiento2 : pta.getPasos()) {
							if (instruccionProcedimiento2 instanceof InstruccionProcedimientoPasadas) {
								instruccionProcedimiento = (InstruccionProcedimientoPasadas) instruccionProcedimiento2;
							}
						}
					}
				}
			}
		}
		return instruccionProcedimiento;
	}

	@Override
	protected int getColVerificada() {
		return COL_VERIFICADA;
	}

}