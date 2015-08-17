package ar.com.textillevel.gui.modulos.abm.listaprecios.estampado;

import java.awt.Dialog;

import ar.clarin.fwjava.componentes.CLJTable;
import ar.com.textillevel.entidades.ventas.cotizacion.GrupoTipoArticulo;
import ar.com.textillevel.entidades.ventas.cotizacion.GrupoTipoArticuloBaseEstampado;
import ar.com.textillevel.entidades.ventas.cotizacion.PrecioBaseEstampado;
import ar.com.textillevel.entidades.ventas.cotizacion.RangoAnchoArticuloEstampado;
import ar.com.textillevel.entidades.ventas.cotizacion.RangoCantidadColores;
import ar.com.textillevel.entidades.ventas.cotizacion.RangoCoberturaEstampado;
import ar.com.textillevel.gui.modulos.abm.listaprecios.PanelTablaRango;

public class PanelTablaRangoEstampado extends PanelTablaRango<RangoAnchoArticuloEstampado> {

	private static final long serialVersionUID = 4588180157146398987L;

	private static final int CANT_COLS = 7;
	private static final int COL_ANCHO = 0;
	private static final int COL_TIPO_ARTICULO = 1;
	private static final int COL_BASE = 2;
	private static final int COL_CANT_COLORES = 3;
	private static final int COL_COBERTURA = 4;
	private static final int COL_PRECIO = 5;
	private static final int COL_OBJ = 6;
	
	public PanelTablaRangoEstampado(Dialog parent) {
		super(parent);
		agregarBotonModificar();
	}

	@Override
	protected CLJTable construirTabla() {
		CLJTable tabla = new CLJTable(0, CANT_COLS);
		tabla.setStringColumn(COL_ANCHO, "ANCHO", 80, 80, false);
		tabla.setStringColumn(COL_TIPO_ARTICULO, "TIPO DE ARTICULO", 150, 150, false);
		tabla.setStringColumn(COL_BASE, "BASE", 120, 120, false);
		tabla.setStringColumn(COL_CANT_COLORES, "CANT. COLORES", 110, 110, false);
		tabla.setStringColumn(COL_COBERTURA, "COBERTURA", 90, 90, false);
		tabla.setFloatColumn(COL_PRECIO, "PRECIO", 70, true);
		tabla.setStringColumn(COL_OBJ, "", 0, 0, true);
		tabla.setHeaderAlignment(COL_ANCHO, CLJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_TIPO_ARTICULO, CLJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_BASE, CLJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_PRECIO, CLJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_COBERTURA, CLJTable.CENTER_ALIGN);
		tabla.setHeaderAlignment(COL_CANT_COLORES, CLJTable.CENTER_ALIGN);
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
						row[COL_OBJ] = elemento;
						row[COL_ANCHO] = elemento.toString();
						row[COL_TIPO_ARTICULO] = grupoEstampado.getTipoArticulo().toString();
						row[COL_BASE] = precioBase.getGama().toString();
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
		return (RangoAnchoArticuloEstampado) getTabla().getValueAt(fila, COL_OBJ);
	}

	@Override
	protected String validarElemento(int fila) {
		return null;
	}

	@Override
	public boolean validarQuitar() {
		return true;
	}
	
	@Override
	protected void botonAgregarPresionado() {

	}

	@Override
	public boolean validarNuevoRegistro() {
		// TODO Auto-generated method stub
		return false;
	}

}