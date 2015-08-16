package ar.com.textillevel.gui.modulos.abm.listaprecios.tenido;

import java.awt.Dialog;

import ar.clarin.fwjava.componentes.CLJTable;
import ar.com.textillevel.entidades.ventas.cotizacion.GrupoTipoArticuloGama;
import ar.com.textillevel.gui.modulos.abm.listaprecios.PanelTablaRango;

public class PanelTablaRangoTenido extends PanelTablaRango<GrupoTipoArticuloGama> {

	private static final long serialVersionUID = -6110511633595669633L;

	private static final int CANT_COLS = 2;
	private static final int COL_TIPO_ARTICULO = 0;
	private static final int COL_OBJ = 1;
	
	public PanelTablaRangoTenido(Dialog parent) {
		super(parent);
		agregarBotonModificar();
	}

	@Override
	protected CLJTable construirTabla() {
		CLJTable tabla = new CLJTable(0, CANT_COLS);
		tabla.setStringColumn(COL_TIPO_ARTICULO, "TIPO DE ARTICULO", 200, 200, true);
		tabla.setStringColumn(COL_OBJ, "", 0, 0, true);
		tabla.setHeaderAlignment(COL_TIPO_ARTICULO, CLJTable.CENTER_ALIGN);
		tabla.setAlignment(COL_TIPO_ARTICULO, CLJTable.CENTER_ALIGN);
		return tabla;
	}

	@Override
	protected void agregarElemento(GrupoTipoArticuloGama elemento) {
		Object[] row = new Object[CANT_COLS];
		row[COL_TIPO_ARTICULO] = elemento.getTipoArticulo().getNombre();
		row[COL_OBJ] = elemento;
		getTabla().addRow(row);
	}

	@Override
	protected GrupoTipoArticuloGama getElemento(int fila) {
		return (GrupoTipoArticuloGama) getTabla().getValueAt(fila, COL_OBJ);
	}

	@Override
	protected String validarElemento(int fila) {
		return null;
	}

	@Override
	public boolean validarAgregar() {
		JDialogAgregarModificarPrecioGama dialog = new JDialogAgregarModificarPrecioGama(parent);
		dialog.setVisible(true);
		return false;
	}
	
	@Override
	public boolean validarQuitar() {
		return true;
	}
	
	@Override
	protected void botonAgregarPresionado() {

	}
}