package ar.com.textillevel.gui.modulos.odt.columnas;

import ar.com.fwcommon.templates.modulo.model.tabla.ColumnaString;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.util.ODTCodigoHelper;

public class ColumnaCodigoODT extends ColumnaString<OrdenDeTrabajo>{

	public ColumnaCodigoODT() {
		super("C�digo");
	}

	@Override
	public String getValor(OrdenDeTrabajo item) {
		return ODTCodigoHelper.getInstance().formatCodigo(item.getCodigo());
	}
}
