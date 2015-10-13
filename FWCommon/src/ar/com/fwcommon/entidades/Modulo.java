package ar.com.fwcommon.entidades;

import java.io.Serializable;

public class Modulo implements Comparable<Modulo>, Serializable {

	private static final long serialVersionUID = -3481044403699313547L;

	private int idModulo;
    private String nombre;
    private String ubicacion;
    private Integer orden;
    private boolean action;
    //private transient Integer idGrupoModulos;//temporalmente transient
    public String icono;
    public static final int MODULO_NUEVO = 0;
    public static final int MODULO_ANULADO = -1;
    public static final int MODULO_SEPARADOR = -2;

    /** M�todo constructor */
    public Modulo() {
    }

    /**
     * M�todo constructor.
     * @param idModulo El nro. de id del m�dulo.
     * @param nombre El nombre del m�dulo.
     */
    public Modulo(int idModulo, String nombre) {
        this.idModulo = idModulo;
        this.nombre = nombre;
    }

    /**
     * M�todo constructor.
     * @param idModulo El nro. de id del m�dulo.
     * @param nombre El nombre del m�dulo.
     * @param ubicacion La clase asociada al m�dulo (debe incluir el package y no incluir el .class).
     */
    public Modulo(int idModulo, String nombre, String ubicacion) {
        this.idModulo = idModulo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    /**
     * M�todo constructor.
     * @param idModulo El nro. de id del m�dulo.
     * @param nombre El nombre del m�dulo.
     * @param ubicacion La clase asociada al m�dulo (debe incluir el package y no incluir el .class).
     * @param orden La ubicaci�n del m�dulo en el men�.
     */
    public Modulo(int idModulo, String nombre, String ubicacion, int orden, boolean action) {
        this.idModulo = idModulo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.orden = orden;
        this.action = action;
    }

    /**
     * Devuelve el <b>id</b> del m�dulo.
     * @return idModulo
     */
    public int getIdModulo() {
        return idModulo;
    }

    /**
     * Setea el <b>id</b> del m�dulo.
     * @param idModulo
     */
    public void setIdModulo(int idModulo) {
        this.idModulo = idModulo;
    }

    /**
     * Devuelve el <b>nombre</b> del m�dulo.
     * @return nombre
     */
    public String getNombre() { 
        return nombre; 
    }

    /**
     * Setea el <b>nombre</b> del m�dulo.
     * @param nombre
     */
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    /**
     * Retorna la <b>clase</b> asociada al m�dulo (debe incluir el package y no incluir el .class).
     * @return ubicacion
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * Setea la <b>clase</b> asociada al m�dulo (debe incluir el package y no incluir el .class).
     * @param ubicacion
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * Obtiene el <b>orden</b> del m�dulo en el men� <b>M�dulos</b>.
     * @return orden
     */
    public Integer getOrden() {
        return orden;
    }

    /**
     * Setea el <b>orden</b> del m�dulo en el men� <b>M�dulos</b>.
     * @param orden
     */
    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    /**
     * Obtiene el <b>�cono</b> del m�dulo en el men� <b>M�dulos</b>.
     * @return icono
     */
    public String getIcono() {
    	return icono;
    }

    /**
     * Setea el <b>�cono</b> del m�dulo en el men� <b>M�dulos</b>.
     * @param icono
     */
    public void setIcono(String icono) {
    	this.icono = icono;
    }

    public static Modulo crearModuloSeparador() {
    	Modulo sep = new Modulo(MODULO_SEPARADOR, String.valueOf(MODULO_SEPARADOR), String.valueOf(MODULO_SEPARADOR));
    	sep.setOrden(MODULO_SEPARADOR);
    	return sep;
    }

	public boolean isSeparador() {
		return getOrden()!= null && getOrden() == Modulo.MODULO_SEPARADOR;
	}

    /** Retorna el nombre del m�dulo */
    public String toString () {
        return getNombre() ;
    }

    /** Sobreescritura del metodo equals */
    public boolean equals(Object obj) {
        if((obj != null) && (obj instanceof Modulo)) {
            return (this.getIdModulo() == ((Modulo)obj).getIdModulo());
        }
        return false;
    }

	public int compareTo(Modulo modulo) {
		return getOrden().compareTo(modulo.getOrden());
	}

	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	/*
	public Integer getIdGrupoModulos() {
		return idGrupoModulos;
	}

	
	public void setIdGrupoModulos(Integer idGrupoModulos) {
		this.idGrupoModulos = idGrupoModulos;
	}
	*/
}