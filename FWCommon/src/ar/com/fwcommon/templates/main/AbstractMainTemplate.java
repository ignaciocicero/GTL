package ar.com.fwcommon.templates.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import ar.com.fwcommon.boss.BossError;
import ar.com.fwcommon.componentes.FWCursor;
import ar.com.fwcommon.componentes.FWJOptionPane;
import ar.com.fwcommon.componentes.MDIDesktopPane;
import ar.com.fwcommon.componentes.MenuVentana;
import ar.com.fwcommon.componentes.error.FWException;
import ar.com.fwcommon.templates.GuiForm;
import ar.com.fwcommon.templates.main.login.DialogoLogin;
import ar.com.fwcommon.templates.main.login.EmptyLoginManager;
import ar.com.fwcommon.templates.main.login.ILoginManager;
import ar.com.fwcommon.util.GuiUtil;
import ar.com.fwcommon.util.ImageUtil;
import ar.com.fwcommon.util.MiscUtil;

@SuppressWarnings("serial")
public abstract class AbstractMainTemplate<T extends ILoginManager> extends JFrame {

	protected int idAplicacion;
	protected String version;
	protected FWDesktopPane desktop;
	protected DialogoLogin dialogoLogin;
	protected T loginManager;
	protected String logoLogin;
	protected int cantMaxIntentosFallidosLogin;
	protected int intentosFallidosLogin;
	protected JMenuBar menuBar;
	protected MenuVentana menuVentana;
	protected static Frame frameInstance;
	protected static final int ANCHO_DESKTOP = 1014;
	protected static final int ALTO_DESKTOP = 728;
	protected static final int CANT_MAX_INTENTOS_FALLIDOS_LOGIN = 3;

	/** M�todo constructor 
	 * @throws FWException */
	protected AbstractMainTemplate() throws FWException {
		this(null, ANCHO_DESKTOP, ALTO_DESKTOP);
	}

	/**
	 * M�todo constructor.
	 * @param titulo El t�tulo de la ventana.
	 * @throws FWException 
	 */
	protected AbstractMainTemplate(String titulo) throws FWException {
		this(titulo, ANCHO_DESKTOP, ALTO_DESKTOP);
	}

	/**
	 * M�todo constructor.
	 * @param titulo El t�tulo de la ventana.
	 * @param ancho El ancho de la ventana en pixeles.
	 * @param alto El alto de la ventana en pixeles.
	 * @throws FWException 
	 */
	protected AbstractMainTemplate(String titulo, int ancho, int alto) throws FWException {
		preConstruccion();
		construirFrame(ancho, alto, titulo);
		frameInstance = this;
		postConstruccion();
	}

	/**
	 * M�todo constructor.
	 * @param idAplicacion El id de la aplicaci�n.
	 * @param version La versi�n de la aplicaci�n.
	 * @throws FWException 
	 */
	protected AbstractMainTemplate(int idAplicacion, String version) throws FWException {
		this(idAplicacion, null, version);
	}

	/**
	 * M�todo constructor.
	 * @param idAplicacion El id de la aplicaci�n.
	 * @param titulo El t�tulo de la ventana.
	 * @param version La versi�n de la aplicaci�n.
	 * @throws FWException 
	 */
	protected AbstractMainTemplate(int idAplicacion, String titulo, String version) throws FWException {
		this.idAplicacion = idAplicacion;
		this.version = version;
		this.cantMaxIntentosFallidosLogin = CANT_MAX_INTENTOS_FALLIDOS_LOGIN;
		preConstruccion();
		construirFrame(ANCHO_DESKTOP, ALTO_DESKTOP, titulo);
		frameInstance = this;
		postConstruccion();
	}

	/**
	 * M�todo para crear el manager de login (una clase que implemente ILoginManager).
	 * @return T Una instancia de ILoginManager.
	 * ar.com.fwcommon.templates.main.login.ILoginManager
	 */
	protected abstract T crearLoginManager();

	/** Inicializa la ventana principal y la muestra */
	public void iniciarAplicacion() {
		setVisible(true);
		GuiUtil.centrar(AbstractMainTemplate.this);
		verDialogoLogin();
	}

	/** M�todo que se ejecuta <b>antes</b> de la construcci�n de la ventana */
	protected void preConstruccion() {
	}

	/** M�todo que se ejecuta <b>despu�s</b> de la construcci�n de la ventana 
	 * @throws FWException */
	protected void postConstruccion() throws FWException {
	}

	/** M�todo que se ejecuta inmediatamente despu�s de que el usuario se loguea a la aplicaci�n 
	 * @throws FWException */
	protected void postLogin() throws FWException {
	}

	/** M�todo que se ejecuta inmediatamente antes de que se cierre la ventana */
	protected void preSalir() {
	}

	/** M�todo que cierra todas las ventanas internas mas la ventana principal y luego sale de la aplicaci�n */
	protected void salir() {
		preSalir();
		desktop.cerrarVentanas();
		System.exit(0);
	}

	/**
	 * Muestra la ventana de login a la aplicaci�n.
	 * Este m�todo instancia el manager de login quien valida el usuario y password ingresados
	 * por el usuario en la ventana de login y muestra la respuesta de dicha validaci�n.
	 * Dependiendo de esta respuesta se habilita o deshabilita la barra de men� de la ventana
	 * principal.
	 */
	protected void verDialogoLogin() {
		loginManager = crearLoginManager();
		if (loginManager instanceof EmptyLoginManager) {
			try {
				intentosFallidosLogin = 0;
				habilitarMenu(true);
				postLogin();
			} catch(FWException e) {
				BossError.gestionarError(e);
			}
			return;
		}
		
		habilitarMenu(false);
		if(dialogoLogin == null) {
			dialogoLogin = new DialogoLogin(this, System.getProperty("usuario"));
			if(logoLogin != null) {
				dialogoLogin.setLogo(logoLogin);
			}
		}
		GuiUtil.centrar(dialogoLogin);
		dialogoLogin.setVisible(true);
		String usuario = dialogoLogin.getUsuario();
		String password = dialogoLogin.getPassword();
		FWCursor.startWait(this);
		if(!dialogoLogin.isCancelado()) {
			try {
				if(loginManager == null) {
					loginManager = crearLoginManager();
				}
				boolean respuesta = loginManager.login(usuario, password);
				if(respuesta) {
					intentosFallidosLogin = 0;
					System.setProperty("usuario", usuario);
					habilitarMenu(true);
					dialogoLogin.setPassword(null);
					dialogoLogin.setUsuario(null);
					postLogin();
				} else {
					FWJOptionPane.showWarningMessage(this, "Debe ingresar un usuario y contrase�a v�lidos.", dialogoLogin.getTitle());
					dialogoLogin.setPassword(null);
					intentosFallidosLogin++;
					gestionarIntentosFallidosLogin();
					verDialogoLogin();
				}
			} catch(FWException e) {
				intentosFallidosLogin = 0;
				BossError.gestionarError(e);
				verDialogoLogin();
			}
		} else {
			habilitarMenu(true);
		}
		FWCursor.endWait(this);
	}

	/**
	 * Aplica el look & feel a la aplicaci�n.
	 * Debe llamarse antes de hacer la llamada al m�todo <b>iniciarAplicacion()</b> para
	 * que se aplique el laf en su totalidad.
	 * @param laf El look & feel a aplicar.
	 * @throws Exception Si no se pudo encontrar o aplicar el l&f.
	 */
	public static void aplicarLookAndFeel(String laf) throws Exception {
		GuiUtil.setLookAndFeel(laf);
	}

	/**
	 * Aplica un tema de SkinLF a la aplicaci�n.
	 * Debe llamarse antes de hacer la llamada al m�todo <b>iniciarAplicacion()</b> para
	 * que se aplique el laf en su totalidad.
	 * @param themepack El tema a aplicar.
	 * @throws Exception Si no se pudo aplicar el tema.
	 */
	public static void aplicarSkinLookAndFeel(String themepack) throws Exception {
		GuiUtil.setSkinLookAndFeelThemepack(themepack);
	}

	/**
	 * Setea el �cono de la ventana principal.
	 * @param iconoVentana El icono a setear en la ventana principal.
	 */
	public void setIconoVentana(String iconoVentana) {
		GuiUtil.setFrameIcon(this, iconoVentana);
	}

	/**
	 * Setea el logo al di�logo de login.
	 * @param loginLogo El logo a setear en el di�logo de login.
	 */
	public void setLogoLogin(String loginLogo) {
		this.logoLogin = loginLogo;
	}

	/**
	 * Devuelve el desktop de la ventana principal.
	 * @return desktop El desktop del frame principal.
	 * ar.com.fwcommon.templates.main.AbstractMainTemplate.CLDesktopPane
	 */
	protected FWDesktopPane getDesktop() {
		return desktop;
	}

	/**
	 * Devuelve el men� <b>Ventana</b>.
	 * @return menuVentana
	 * ar.com.fwcommon.componentes.MenuVentana
	 */
	protected MenuVentana getMenuVentana() {
		return menuVentana;
	}

	/**
	 * Agrega un men� a la barra de men� de la ventana principal.
	 * @param menu El men� a agregar.
	 */
	protected void agregarMenu(JMenu menu) {
		menuBar.add(menu);
	}

	/**
	 * Agrega un men� a la barra de men� de la ventana principal en la posici�n indicada.
	 * @param menu El men� a agregar a la ventana principal.
	 * @param pos La posici�n en la que se agrega el men� con respecto a los dem�s menues.
	 */
	protected void agregarMenu(JMenu menu, int pos) {
		Vector<JMenu> menues = new Vector<JMenu>();
		Component[] components = menuBar.getComponents();
		for(Component c : components) {
			if(c instanceof JMenu) {
				menues.add((JMenu)c);
			}
		}
		menuBar.removeAll();
		menues.add(pos, menu);
		agregarMenues(menues);
	}

	/**
	 * Agrega los menues a la barra de men� de la ventana principal.
	 * @param menues La lista de menues a agregar.
	 */
	protected void agregarMenues(Collection<JMenu> menues) {
		for(JMenu menu : menues) {
			agregarMenu(menu);
		}
	}

	/**
	 * Elimina un men� de la barra de men� de la ventana principal.
	 * @param menu El men� a eliminar.
	 */
	protected void removerMenu(JMenu menu) {
		menuBar.remove(menu);
	}

	/**
	 * Agrega una im�gen de fondo al desktop de la ventana principal.
	 * @param bgImage La ruta completa a la im�gen.
	 */
	protected void setBackgroundImage(String bgImage) {
		desktop.setBackgroundImage(bgImage);
	}

	/**
	 * Crea un <b>tray icon</b> para la aplicaci�n.
	 * @param icono El icono para mostrar en el area de notificaciones.
	 * @param descripcion La descripci�n que se muestra al activarse.
	 * @return trayIcon El �cono creado.
	 */
	public TrayIcon crearTrayIcon(String icono, String descripcion) {
		TrayIcon trayIcon = null;
		if(MiscUtil.isWindowsXP()) {
			Icon imgIcon = ImageUtil.loadIcon(icono);
			trayIcon = new TrayIcon(imgIcon, descripcion);
			trayIcon.setIconAutoSize(true);
			trayIcon.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					setExtendedState(JFrame.MAXIMIZED_BOTH);
					toBack();
				}
			});
			SystemTray.getDefaultSystemTray().addTrayIcon(trayIcon);
		}
		return trayIcon;
	}

	/**
	 * Devuelve la <b>versi�n</b> de la aplicaci�n.
	 * @return version La versi�n de la aplicaci�n.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Setea la <b>versi�n</b> de la aplicaci�n.
	 * @param version La versi�n de la aplicaci�n.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Devuelve la cantidad establecida de intentos fallidos de logueo permitidos.
	 * @return cantIntentosFallidosLogin La cantidad de intentos fallidos permitidos.
	 */
	public int getCantMaxIntentosFallidosLogin() {
		return cantMaxIntentosFallidosLogin;
	}

	/**
	 * Setea la cantidad de intentos fallidos de logueo permitidos.
	 * @param cantMaxIntentosFallidosLogin La cantidad de intentos fallidos permitidos.
	 */
	public void setCantMaxIntentosFallidosLogin(int cantMaxIntentosFallidosLogin) {
		this.cantMaxIntentosFallidosLogin = cantMaxIntentosFallidosLogin;
	}

	/**
	 * Devuelve una instancia del Frame.
	 * Util para pasar por par�metro a los di�logos como owner.
	 * @return frameInstance La instancia de frame.
	 */
	public static Frame getFrameInstance() {
		return frameInstance;
	}

	/* Construye gr�ficamente la ventana principal */
	private void construirFrame(int ancho, int alto, String titulo) {
		//Props
		setTitle(titulo);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new VentanaListener());
		//Desktop
		desktop = new FWDesktopPane();
		desktop.setPreferredSize(new Dimension(ancho, alto));
		getContentPane().add(desktop, BorderLayout.CENTER);
		//Menu Bar
		setJMenuBar(crearMenuBar());
		//
		pack();
	}

	/* Crea la barra de men� de la ventana principal */
	private JMenuBar crearMenuBar() {
		menuBar = new JMenuBar();
		menuVentana = new MenuVentana(desktop);
		agregarMenu(menuVentana);
		return menuBar;
	}

	/* Habilita/Deshabilita la barra de men� de la ventana principal */
	private void habilitarMenu(boolean b) {
		for(int i = 0; i < menuBar.getMenuCount(); i++) {
			JMenu menu = menuBar.getMenu(i);
			menu.setEnabled(b);
		}
	}

	/* Manejo de m�ltiples intentos fallidos de login */
	private void gestionarIntentosFallidosLogin() {
		if(intentosFallidosLogin >= cantMaxIntentosFallidosLogin) {
			final String oldTitle = dialogoLogin.getTitle();
			dialogoLogin.setTitle(oldTitle + " - Aguarde un momento...");
			GuiUtil.setEstadoPanel(dialogoLogin.getLayeredPane(), false);
			Thread t = new Thread() {
				public void run() {
					MiscUtil.sleep(intentosFallidosLogin * 1000 * 10);
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								dialogoLogin.setTitle(oldTitle);
								GuiUtil.setEstadoPanel(dialogoLogin.getLayeredPane(), true);
							}
						});
					} catch(Exception e) {
					}
				}
			};
			t.start();
		}
	}

	/* Listener de eventos de la ventana principal */
	class VentanaListener extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			salir();
		}
	}

	/* Clase para el manejo del desktop pane */
	protected class FWDesktopPane extends MDIDesktopPane {
		/** Cierra todas las ventanas (InternalFrame) abiertas */
		public boolean cerrarVentanas() {
			JInternalFrame frames[] = getAllFrames();
			for(int i = 0; i < frames.length; i++) {
				GuiForm form = (GuiForm)frames[i];
				if(form.botonSalirPresionado()) {
					form.dispose();
				} else {
					return false;
				}
			}
			return true;
		}

		/** Setea una im�gen de fondo en el desktop del frame principal */
		public void setBackgroundImage(String bgImage) {
			JLabel lbl = new JLabel(ImageUtil.loadIcon(bgImage));
			lbl.setBounds(0, 0, getWidth(), getHeight());
			add(lbl, JDesktopPane.FRAME_CONTENT_LAYER, 0);
		}
	}

}