package ar.com.textillevel.gui.modulos.odt.gui.procedimientos;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.com.fwcommon.util.StringUtil;
import ar.com.textillevel.entidades.enums.ETipoProducto;
import ar.com.textillevel.entidades.enums.EUnidad;
import ar.com.textillevel.entidades.ventas.materiaprima.Formulable;
import ar.com.textillevel.entidades.ventas.materiaprima.MateriaPrima;
import ar.com.textillevel.entidades.ventas.materiaprima.anilina.Anilina;
import ar.com.textillevel.facade.api.remote.MateriaPrimaFacadeRemote;
import ar.com.textillevel.gui.util.GenericUtils;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.explotaciones.FormulaEstampadoClienteExplotada;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.explotaciones.FormulaTenidoClienteExplotada;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.explotaciones.fw.IFormulaClienteExplotadaVisitor;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.explotaciones.fw.MateriaPrimaCantidadExplotada;
import ar.com.textillevel.modulos.odt.entidades.maquinas.formulas.tenido.FormulaTenidoCliente;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.IInstruccionProcedimiento;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.IInstruccionProcedimientoODTVisitor;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoPasadasODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoTextoODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.InstruccionProcedimientoTipoProductoODT;
import ar.com.textillevel.modulos.odt.entidades.secuencia.odt.PasoSecuenciaODT;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;
import ar.com.textillevel.util.GTLBeanFactory;

public class InstruccionProcedimientoRenderer {

	
	private static MateriaPrimaFacadeRemote materiaPrimaFacade;
	
	private static MateriaPrimaFacadeRemote getMateriaPrimaFacade() {
		if(materiaPrimaFacade == null) {
			materiaPrimaFacade = GTLBeanFactory.getInstance().getBean2(MateriaPrimaFacadeRemote.class);
		}
		return materiaPrimaFacade;
	}

	public static interface FiltroInstrucciones{
		public boolean esValida(IInstruccionProcedimiento instruccion);
	}

	public static String renderInstruccionASHTML(IInstruccionProcedimiento elemento) {
		return renderInstruccionASHTML(elemento, false);
	}

	public static String renderInstruccionesASHTML(List<InstruccionProcedimientoODT> instrucciones, boolean explotadas, FiltroInstrucciones filtro) {
		String html = "";
		for(InstruccionProcedimientoODT ins : instrucciones){
			if(filtro == null || filtro.esValida(ins)){
				String htmlInstruccion = renderInstruccionASHTML(ins, explotadas).replaceAll("<html>", "");
				if(ins instanceof InstruccionProcedimientoTipoProductoODT && ((InstruccionProcedimientoTipoProductoODT)ins).getTipoProducto() == ETipoProducto.ESTAMPADO && ((InstruccionProcedimientoTipoProductoODT)ins).getFormula() == null){
					InstruccionProcedimientoTipoProductoODT itp = (InstruccionProcedimientoTipoProductoODT) ins;
					String descripcion = "<b>"+itp.getTipoProducto().getDescripcion().toUpperCase()+"</b>" + " " + itp.getTipoArticulo().getNombre().toUpperCase();
					htmlInstruccion = htmlInstruccion.replace(descripcion, "VER HOJA ESTAMPADO");
				}
				htmlInstruccion = htmlInstruccion.replaceAll("</html>", "");
				/* ESTE SUBRAYADO NO SALE BIEN EN LA IMPRESION... POR LO MENOS CON PDF CREATOR */
				htmlInstruccion = htmlInstruccion.replaceAll("</u>", "");
				htmlInstruccion = htmlInstruccion.replaceAll("<u>", "");
				htmlInstruccion += "<br>";
				html += htmlInstruccion;
			}
		}
		return "<html>" + html + "</html>";
	}
	
	public static String renderObservacionesInstruccionesASHTML(List<InstruccionProcedimientoODT> instrucciones, FiltroInstrucciones filtroInstrucciones) {
		String html = "";
		for(IInstruccionProcedimiento ins : instrucciones){
			if(!StringUtil.isNullOrEmpty(ins.getObservaciones()) && (filtroInstrucciones == null || filtroInstrucciones.esValida(ins))){
				html += "* " + ins.getObservaciones() + "<br>";
			}
		}
		if(StringUtil.isNullOrEmpty(html)){
			return "";
		}
		return "<html>" + html + "</html>";
	}
	
	public static String getDescripcionInstruccion(IInstruccionProcedimiento instruccion) {
		return instruccion.getDescrSimple();
	}

	public static String getDescripcionDetalladaInstruccion(IInstruccionProcedimiento instruccion) {
		return instruccion.getDescrDetallada();
	}

	public static String renderInstruccionASHTML(IInstruccionProcedimiento elemento, boolean explotada) {
		String descripcion = "";
		if (elemento instanceof InstruccionProcedimientoPasadasODT) {
			if (((InstruccionProcedimientoPasadasODT) elemento).getAccion() != null) {
				descripcion += "<b>" + ((InstruccionProcedimientoPasadasODT) elemento).getAccion().getNombre() + "</b><br>";
			}
		}
		if (explotada) {
			String[] descrArray = getDescripcionDetalladaInstruccion(elemento).split(" ");
			descripcion += "<b>"+descrArray[0].toUpperCase()+"</b> " + (descrArray.length > 1 ? StringUtil.getCadena(Arrays.asList(descrArray).subList(1, descrArray.length), " ") : "");
		} else {
			String[] descrArray = getDescripcionInstruccion(elemento).split(" ");
			descripcion += "<b>"+descrArray[0].toUpperCase()+"</b> " + (descrArray.length > 1 ? StringUtil.getCadena(Arrays.asList(descrArray).subList(1, descrArray.length), " ") : "");
		}
		
		descripcion = descripcion.replace("con", "<br>* ");
		descripcion = descripcion.replace(", ", "<br>* ");
		descripcion = descripcion.replace(" y ", "<br>* ");

		Pattern p = Pattern.compile("\\d{1,3}\\,\\d+");
		Matcher m = p.matcher(descripcion);
		while(m.find()){
			String texto = m.group();
			descripcion = descripcion.replace(texto, GenericUtils.formatAsSuperIndex(texto,true));
		}
		return "<html>" + descripcion + "</html>";
	}

	public static String getResumenSectorHTML(ESectorMaquina sector, List<PasoSecuenciaODT> pasos, boolean mostrarOtrasAnilinasConMismoColorIndex, boolean horizontal) {
		String html = "";
		
		for(PasoSecuenciaODT p : pasos){
			if(p.getSector().getSectorMaquina() != sector){
				continue;
			}
			for(InstruccionProcedimientoODT ip : p.getSubProceso().getPasos()){
				ResumenHTMLProductosInstruccionVisitor v = new InstruccionProcedimientoRenderer.ResumenHTMLProductosInstruccionVisitor(mostrarOtrasAnilinasConMismoColorIndex, horizontal);
				ip.accept(v);
				html += v.getResumenHTML();
			}
		}
		
		return "<html>"+html+"</html>";
	}
	
	public static String getResumenQuimicos(List<PasoSecuenciaODT> pasos) {
		String html = "";
		for (PasoSecuenciaODT p : pasos) {
			for (InstruccionProcedimientoODT ip : p.getSubProceso().getPasos()) {
				ResumenHTMLQuimicosVisitor v = new InstruccionProcedimientoRenderer.ResumenHTMLQuimicosVisitor();
				ip.accept(v);
				html += v.getResumenHTML();
			}
		}
		return "<html>"+html+"</html>";
	}
	
	public static String getResumenAlgodon(List<PasoSecuenciaODT> pasos, boolean mostrarAnilinasConMismoColorIndex) {
		return getResumenTipoArituclo("A", pasos, mostrarAnilinasConMismoColorIndex);
	}
	
	public static String getResumenPoliester(List<PasoSecuenciaODT> pasos, boolean mostrarOtrasAnilinasConMismoColorIndex) {
		return getResumenTipoArituclo("P", pasos, mostrarOtrasAnilinasConMismoColorIndex);
	}
	
	private static String getResumenTipoArituclo(String sigla, List<PasoSecuenciaODT> pasos, boolean mostrarOtrasAnilinasConMismoColorIndex) {
		String html = "";
		for(PasoSecuenciaODT p : pasos){
			for(InstruccionProcedimientoODT ip : p.getSubProceso().getPasos()){
				ResumenHTMLTipoProductosInstruccionVisitor v = new InstruccionProcedimientoRenderer.ResumenHTMLTipoProductosInstruccionVisitor(sigla, mostrarOtrasAnilinasConMismoColorIndex);
				ip.accept(v);
				html += v.getResumenHTML();
			}
		}
		
		return "<html>"+html+"</html>";
	}
	
	private static class ResumenHTMLQuimicosVisitor implements IInstruccionProcedimientoODTVisitor {

		private String resumenHTML = "";
		
		public void visit(InstruccionProcedimientoPasadasODT instruccion) {
			if(instruccion.getQuimicosExplotados() != null && !instruccion.getQuimicosExplotados().isEmpty()){
				setResumenHTML(generarDescripcionProductosHTML(instruccion.getQuimicosExplotados(), null, false, false));
				return;
			}
			setResumenHTML("");
		}

		public void visit(InstruccionProcedimientoTextoODT instruccion) {
		}

		public void visit(InstruccionProcedimientoTipoProductoODT instruccion) {
			if(instruccion.getFormula() == null){
				setResumenHTML("");
				return;
			}
			ResumenHTMLQuimicosFormulaVisitor v = new ResumenHTMLQuimicosFormulaVisitor();
			instruccion.getFormula().accept(v);
			setResumenHTML(v.getResumenHTML());
		}

		public String getResumenHTML() {
			return resumenHTML;
		}
		
		public void setResumenHTML(String resumenHTML) {
			this.resumenHTML = resumenHTML;
		}
	}
	
	private static class ResumenHTMLTipoProductosInstruccionVisitor implements IInstruccionProcedimientoODTVisitor {

		private String resumenHTML = "";
		private String sigla;
		private boolean mostrarOtrasMPConMismoColorIndex;
		
		public ResumenHTMLTipoProductosInstruccionVisitor(String sigla, boolean mostrarOtrasMPConMismoColorIndex) {
			this.sigla = sigla;
			this.mostrarOtrasMPConMismoColorIndex = mostrarOtrasMPConMismoColorIndex;
		}

		public void visit(InstruccionProcedimientoPasadasODT instruccion) {
		}

		public void visit(InstruccionProcedimientoTextoODT instruccion) {
		}

		public void visit(InstruccionProcedimientoTipoProductoODT instruccion) {
			if(instruccion.getFormula() == null){
				setResumenHTML("");
				return;
			}
			ResumenHTMLTipoProductosFormulaVisitor v = new ResumenHTMLTipoProductosFormulaVisitor(sigla, mostrarOtrasMPConMismoColorIndex);
			instruccion.getFormula().accept(v);
			setResumenHTML(v.getResumenHTML());
		}

		public String getResumenHTML() {
			return resumenHTML;
		}
		
		public void setResumenHTML(String resumenHTML) {
			this.resumenHTML = resumenHTML;
		}
	}
	
	private static class ResumenHTMLProductosInstruccionVisitor implements IInstruccionProcedimientoODTVisitor {

		private String resumenHTML;
		private boolean mostrarOtrasMPConMismoColorIndex;
		private boolean horizontal;
		
		public ResumenHTMLProductosInstruccionVisitor(boolean mostrarOtrasMPConMismoColorIndex, boolean horizontal) {
			this.mostrarOtrasMPConMismoColorIndex = mostrarOtrasMPConMismoColorIndex;
			this.horizontal = horizontal;
		}
		
		public ResumenHTMLProductosInstruccionVisitor(boolean mostrarOtrasMPConMismoColorIndex) {
			this(mostrarOtrasMPConMismoColorIndex, false);
		}
		
		public void visit(InstruccionProcedimientoPasadasODT instruccion) {
			if(instruccion.getQuimicosExplotados() != null && !instruccion.getQuimicosExplotados().isEmpty()){
				setResumenHTML(generarDescripcionProductosHTML(instruccion.getQuimicosExplotados(), null, mostrarOtrasMPConMismoColorIndex, horizontal));
				return;
			}
			setResumenHTML("");
		}

		public void visit(InstruccionProcedimientoTextoODT instruccion) {
			setResumenHTML("");
		}

		public void visit(InstruccionProcedimientoTipoProductoODT instruccion) {
			setResumenHTML("");
//			if(instruccion.getFormula() == null){
//				setResumenHTML("");
//				return;
//			}
//			ResumenHTMLProductosFormulaVisitor v = new ResumenHTMLProductosFormulaVisitor(mostrarOtrasMPConMismoColorIndex);
//			instruccion.getFormula().accept(v);
//			setResumenHTML(v.getResumenHTML());
		}

		public String getResumenHTML() {
			return resumenHTML;
		}
		
		public void setResumenHTML(String resumenHTML) {
			this.resumenHTML = resumenHTML;
		}
	}
	
	private static <T extends Formulable> String generarDescripcionProductosHTML(List<MateriaPrimaCantidadExplotada<T>> materiasPrimasExplotadas, String siglaAFiltrar, boolean mostrarOtrasMPConMismoColorIndex, boolean horizontal){
		String html = horizontal ? "<table><tbody><tr>" : "";
		for(MateriaPrimaCantidadExplotada<T> mp : materiasPrimasExplotadas){
			if (siglaAFiltrar == null || (siglaAFiltrar != null && mp.getTipoArticulo().getSigla().startsWith(siglaAFiltrar))){
				Float cantidad = mp.getMateriaPrimaCantidadDesencadenante().getCantidad();
				String proporcion = (cantidad < 1f ? GenericUtils.getDecimalFormat2().format(cantidad) : cantidad) + " " + (siglaAFiltrar != null ? mp.getMateriaPrimaCantidadDesencadenante().getUnidad().getDescripcion():"");
//				if(mp.getMateriaPrimaCantidadDesencadenante().getMateriaPrima() instanceof Anilina){
//					html += "* " + proporcion + " - " + mp.getMateriaPrimaCantidadDesencadenante().getDescripcion() + ": " + GenericUtils.getDecimalFormat3().format(mp.getCantidadExplotada()) + " " + mp.getMateriaPrimaCantidadDesencadenante().getMateriaPrima().getUnidad().getDescripcion() + "<br>";
//				}else{
				Float cantidadExplotada = mp.getCantidadExplotada();
				EUnidad unidad = mp.getMateriaPrimaCantidadDesencadenante().getMateriaPrima().getUnidad();
				String descripcion = unidad.getDescripcion();
				if (cantidadExplotada < 1 && unidad == EUnidad.KILOS) {
					descripcion = descripcion.replace("KG", "GR");
				}
				
				if(horizontal) {
					html += "<td>";
				}
				
				String prefijo = "*&nbsp;" + proporcion + "&nbsp;&nbsp;||&nbsp;&nbsp;";
				String pad = "";
				for (int i = 0; i<prefijo.length()/2;i++){
					pad += "&nbsp;";
				}
				String cantidadExplotadaFormateada = "<span style='font-size:17px;'>" + GenericUtils.getDecimalFormat3().format(cantidadExplotada).replace(".", ",").replace(",", "</span>,<span style='font-size:15px; vertical-align: super;'>") + "</span>";
				// formato: <span style='font-size:17px;'>PARTE ENTERA</span>,<sup style='font-size:15px;'>PARTE DECIMAL</sup>
				html += prefijo + mp.getMateriaPrimaCantidadDesencadenante().getDescripcion()
					 + appendAnilinasConMismoColorIndex("&nbsp;&nbsp;&nbsp;", mostrarOtrasMPConMismoColorIndex, (MateriaPrima)mp.getMateriaPrimaCantidadDesencadenante().getMateriaPrima()) 	
					 + "<br>" + pad + "<b>"
					 + cantidadExplotadaFormateada + " "
					 + descripcion
					 + "</b>";
				html += horizontal ? "</td>" : "<br><br>";
//				}
			}
		}
		return html + (horizontal ?"</tr></tbody></table>" : "");
	}
	
	private static String appendAnilinasConMismoColorIndex(String leftPadding, boolean mostrarOtrasAnilinasConMismoColorIndex, MateriaPrima mp) {
		if(!mostrarOtrasAnilinasConMismoColorIndex || !(mp instanceof Anilina)) {
			return "";
		} else {
			List<Anilina> otrasAnilinas = getMateriaPrimaFacade().getOtrasAnilinasByMismoColorIndex((Anilina)mp);
			if(otrasAnilinas.isEmpty()) {
				return "";
			} else {
				return leftPadding+"<br>" + StringUtil.getCadena(otrasAnilinas, leftPadding+"<br>");
			}
		}
	}

	private static class ResumenHTMLQuimicosFormulaVisitor implements IFormulaClienteExplotadaVisitor{

		private String resumenHTML = "";
		
		public void visit(FormulaEstampadoClienteExplotada fece) {
			if(fece.getPigmentos()!=null && !fece.getPigmentos().isEmpty()){
				setResumenHTML(getResumenHTML() + generarDescripcionProductosHTML(fece.getPigmentos(), null, false, false));
			}
			if(fece.getQuimicos()!= null && !fece.getQuimicos().isEmpty()){
				setResumenHTML(getResumenHTML() + generarDescripcionProductosHTML(fece.getQuimicos(), null, false, false));
			}
		}

		public void visit(FormulaTenidoClienteExplotada ftce) {
		}
		
		public String getResumenHTML() {
			return resumenHTML;
		}

		public void setResumenHTML(String resumenHTML) {
			this.resumenHTML = resumenHTML;
		}
	}
	
	private static class ResumenHTMLProductosFormulaVisitor implements IFormulaClienteExplotadaVisitor{

		private String resumenHTML = "";
		boolean mostrarOtrasMPConMismoColorIndex;
		
		private ResumenHTMLProductosFormulaVisitor(boolean mostrarOtrasMPConMismoColorIndex) {
			this.mostrarOtrasMPConMismoColorIndex = mostrarOtrasMPConMismoColorIndex;
		}

		public void visit(FormulaEstampadoClienteExplotada fece) {
			if(fece.getPigmentos()!=null && !fece.getPigmentos().isEmpty()){
				setResumenHTML(getResumenHTML() + generarDescripcionProductosHTML(fece.getPigmentos(), null, mostrarOtrasMPConMismoColorIndex, false));
			}
			if(fece.getQuimicos()!= null && !fece.getQuimicos().isEmpty()){
				setResumenHTML(getResumenHTML() + generarDescripcionProductosHTML(fece.getQuimicos(), null, mostrarOtrasMPConMismoColorIndex, false));
			}
		}

		public void visit(FormulaTenidoClienteExplotada ftce) {
			if(ftce.getMateriasPrimas()!=null && !ftce.getMateriasPrimas().isEmpty()){
				setResumenHTML(getResumenHTML() + generarDescripcionProductosHTML(ftce.getMateriasPrimas(), null, mostrarOtrasMPConMismoColorIndex, false));
			}
		}
		
		public String getResumenHTML() {
			return resumenHTML;
		}

		public void setResumenHTML(String resumenHTML) {
			this.resumenHTML = resumenHTML;
		}
	}
	
	private static class ResumenHTMLTipoProductosFormulaVisitor implements IFormulaClienteExplotadaVisitor{

		private String resumenHTML = "";
		private String sigla = "";
		private boolean mostrarOtrasMPConMismoColorIndex;
		
		public ResumenHTMLTipoProductosFormulaVisitor(String sigla, boolean mostrarOtrasMPConMismoColorIndex) {
			this.sigla = sigla;
			this.mostrarOtrasMPConMismoColorIndex = mostrarOtrasMPConMismoColorIndex;
		}

		public void visit(FormulaEstampadoClienteExplotada fece) {
		}

		public void visit(FormulaTenidoClienteExplotada ftce) {
			if (ftce.getFormulaDesencadenante() instanceof FormulaTenidoCliente){
				if(ftce.getMateriasPrimas()!=null && !ftce.getMateriasPrimas().isEmpty()){
					setResumenHTML(getResumenHTML() + generarDescripcionProductosHTML(ftce.getMateriasPrimas(), this.sigla, mostrarOtrasMPConMismoColorIndex, false));
				}
			}
		}
		
		public String getResumenHTML() {
			return resumenHTML;
		}

		public void setResumenHTML(String resumenHTML) {
			this.resumenHTML = resumenHTML;
		}
	}

}
