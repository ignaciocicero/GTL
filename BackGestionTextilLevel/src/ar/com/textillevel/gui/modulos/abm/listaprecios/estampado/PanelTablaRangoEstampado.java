package ar.com.textillevel.gui.modulos.abm.listaprecios.estampado;

import ar.com.fwcommon.componentes.FWJTable;
import ar.com.textillevel.entidades.enums.EUnidad;
import ar.com.textillevel.entidades.ventas.cotizacion.GrupoTipoArticulo;
import ar.com.textillevel.entidades.ventas.cotizacion.GrupoTipoArticuloBaseEstampado;
import ar.com.textillevel.entidades.ventas.cotizacion.PrecioBaseEstampado;
import ar.com.textillevel.entidades.ventas.cotizacion.RangoAnchoArticuloEstampado;
import ar.com.textillevel.entidades.ventas.cotizacion.RangoCantidadColores;
import ar.com.textillevel.entidades.ventas.cotizacion.RangoCoberturaEstampado;
import ar.com.textillevel.gui.modulos.abm.listaprecios.JDialogAgregarModificarDefinicionPrecios;
import ar.com.textillevel.gui.modulos.abm.listaprecios.PanelTablaRango;

public class PanelTablaRangoEstampado extends PanelTablaRango<RangoAnchoArticuloEstampado, RangoCoberturaEstampado> {

	private static final long serialVersionUID = 4588180157146398987L;

	private static final int CANT_COLS = 9;
	private static final int COL_ANCHO = 0;
	private static final int COL_TIPO_ARTICULO = 1;
	private static final int COL_ARTICULO = 2;
	private static final int COL_BASE = 3;
	private static final int COL_DIBUJO = 4;
	private static final int COL_CANT_COLORES = 5;
	private static final int COL_COBERTURA = 6;
	private static final int COL_PRECIO = 7;
	private static final int COL_OBJ = 8;
	
	public PanelTablaRangoEstampado(JDialogAgregarModificarDefinicionPrecios<RangoAnchoArticuloEstampado, RangoCoberturaEstampado> parent) {
		super(parent);
	}

	@Override
	protected FWJTable construirTabla() {
		FWJTable tabla = new FWJTable(0, CANT_COLS);
		tabla.setStringColumn(COL_ANCHO, "ANCHO", 80, 80, true);
		tabla.setStringColumn(COL_TIPO_ARTICULO, "TIPO DE ARTICULO", 150, 150, true);
		tabla.setStringColumn(COL_ARTICULO, "ARTICULO", 150, 150, true);
		tabla.setStringColumn(COL_BASE, "BASE", 120, 120, true);
		tabla.setStringColumn(COL_DIBUJO, "DIBUJO", 120, 120, true);
		tabla.setStringColumn(COL_CANT_COLORES, "CANT. COLORES", 110, 110, true);
		tabla.setStringColumn(COL_COBERTURA, "COBERTURA", 90, 90, true);
		tabla.setFloatColumn(COL_PRECIO, "PRECIO", 70, true);
		tabla.setStringColumn(COL_OBJ, "", 0, 0, true);
		tabla.setHeaderAlignment(COL_ANCHO, FWJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_TIPO_ARTICULO, FWJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_ARTICULO, FWJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_BASE, FWJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_DIBUJO, FWJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_PRECIO, FWJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_COBERTURA, FWJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_CANT_COLORES, FWJTable.CENTER_ALIGN);
		tabla.setSelectionMode(FWJTable.SINGLE_SELECTION);
		tabla.setAllowHidingColumns(false);
		tabla.setAllowSorting(false);
		tabla.setReorderingAllowed(false);
		return tabla;
	}

	@Override
	protected void agregarElemento(RangoAnchoArticuloEstampado elemento) {
		for(GrupoTipoArticulo grupo : elemento.getGruposBase()) {
			GrupoTipoArticuloBaseEstampado grupoEstampado = (GrupoTipoArticuloBaseEstampado)grupo;
			for(PrecioBaseEstampado precioBase : grupoEstampado.getPrecios()) {
				for(RangoCantidadColores rangoCantColores : precioBase.getRangosDeColores()) {
					for(RangoCoberturaEstampado rangoCobertura : rangoCantColores.getRangos()) {
						Object[] row = new Object[CANT_COLS];
						row[COL_OBJ] = rangoCobertura;
						row[COL_ANCHO] = elemento.toStringConUnidad(EUnidad.METROS);
						row[COL_TIPO_ARTICULO] = grupoEstampado.getTipoArticulo().toString();
						row[COL_ARTICULO] = grupoEstampado.getArticulo();
						row[COL_BASE] = precioBase.getGama().toString();
						row[COL_DIBUJO] = precioBase.getDibujo() != null ? precioBase.getDibujo().toString() : null;
						row[COL_CANT_COLORES] = rangoCantColores.toString();
						row[COL_COBERTURA] = "De " + rangoCobertura.getMinimo() + " a " + rangoCobertura.getMaximo();
						row[COL_PRECIO] = rangoCobertura.getPrecio();
						getTabla().addRow(row);
					}
				}
			}
		}
	}

	@Override
	protected RangoAnchoArticuloEstampado getElemento(int fila) {
		return null;
	}

	@Override
	public boolean validarQuitar() {
		int selectedRow = getTabla().getSelectedRow();
		RangoCoberturaEstampado rangoCobertura = (RangoCoberturaEstampado)getTabla().getValueAt(selectedRow, COL_OBJ);
		rangoCobertura.deepRemove();
		return true;
	}

	@Override
	public int getColObj() {
		return COL_OBJ;
	}

}