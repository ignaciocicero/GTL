package ar.com.textillevel.gui.modulos.cheques.columnas;

import ar.com.fwcommon.templates.modulo.model.tabla.ColumnaString;
import ar.com.textillevel.entidades.cheque.Cheque;

public class ColumnaNumeracionCheque extends ColumnaString<Cheque>{

	public ColumnaNumeracionCheque() {
		super("Numeraci�n");
		setAncho(80);
	}

	@Override
	public String getValor(Cheque item) {
		return item.getNumeracion().toString();
	}
}
