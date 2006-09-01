package it.cnr.ittig.xmleges.core.blocks.form;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.form.FormException;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.help.Help;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.threads.ThreadManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.clipboard.UtilClipboard;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import com.jeta.forms.components.panel.FormPanel;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.Form</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione usa le form generate da <a
 * href="http://www.jetaware.com">Abeille Form Designer </a> quindi &egrave;
 * necessario avere nel classpath la libreria <code>formsrt.jar</code>.<br>
 * Se &egrave; usato il metodo <code>setName(name)</code>, il titolo della
 * form &egrave; internazionalizzato chiedendo a <code>I18n</code> il titolo
 * <code>name.text</code> e l'icona <code>name.icon</code>.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.util.ui.UtilUI:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.ui.UtilMsg:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.i18n.I18n:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.help.Help:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>form.text</code>: titolo di default della form;</li>
 * <li><code>form.icon</code>: icona di default della form;</li>
 * <li><code>form.button.help</code>: pulsato per attivare l'help
 * configurato tramite <code>UtilUi.applyI18n</code></li>
 * <li><code>form.button.ok</code>: pulsato di conferma configurato tramite
 * <code>UtilUi.applyI18n</code></li>
 * <li><code>form.button.cancel</code>: pulsato di annulla configurato
 * tramite <code>UtilUi.applyI18n</code></li>
 * <li>tutti i componenti grafici supportati da <code>UtilUi.applyI18n</code>
 * sono internazionalizzati.</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @see it.cnr.ittig.xmleges.core.services.i18n.I18n
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FormImpl implements Form, Loggable, Serviceable, Initializable {
	
	Logger logger;

	I18n i18n;

	UtilUI utilUi;

	UtilMsg utilMsg;

	Help help;

	static Component defaultOwner = null;

	JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

	FormPanel form;

	FormPanel mainPanel;

	JDialog dialog;

	boolean dialogChanged = true;

	// TODO configurazione
	// Dimension size = new Dimension(500, 400);
	Dimension size = null;

	String title = null;

	Icon icon = null;

	String helpKey = null;

	JButton helpButton = null;

	boolean buttons = false;

	JButton defaultButtons = null;

	int buttonPressed = 2; // default cancel

	boolean waiting = false;

	boolean resizable = false;

	Vector verifiers = new Vector();

	FormClosedListener listener;

	FormClosedListener helpFormlistener;

	ThreadManager threadManager;

	CutCopyPasteMouseAdapter cutCopyPasteMouseAdapter = new CutCopyPasteMouseAdapter();

	JPopupMenu popupMenu;

	CutAction cutAction = new CutAction();

	CopyAction copyAction = new CopyAction();

	PasteAction pasteAction = new PasteAction();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
		utilUi = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		help = (Help) serviceManager.lookup(Help.class);
		threadManager = (ThreadManager) serviceManager
				.lookup(ThreadManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		threadManager.execute(new Runnable() {
			public void run() {
				try {
					synchronized (this) {
						form = new FormPanel(getClass().getResourceAsStream(
								"Form.jfrm"));
					}
				} catch (Exception ex) {
					logger.error("Error loading 'Form.jfrm'.", ex);
				}
			}
		});
		popupMenu = new JPopupMenu();
		popupMenu.add(utilUi.applyI18n("form.popup.cut", cutAction));
		popupMenu.add(utilUi.applyI18n("form.popup.copy", copyAction));
		popupMenu.add(utilUi.applyI18n("form.popup.paste", pasteAction));
		// form = new
		// FormPanel(this.getClass().getResourceAsStream("Form.jfrm"));
		helpButton = new JButton(utilUi.applyI18n("form.button.help",
				new HelpButtonAction()));
	}

	// ////////////////////////////////////////////////////////// Form Interface
	public void setDefaultOwner(Component owner) {
		defaultOwner = owner;
	}

	public void setMainComponent(InputStream desc) throws FormException {
		try {
			mainPanel = new FormPanel(desc);
			dialogChanged = true;
		} catch (com.jeta.forms.gui.common.FormException ex) {
			throw new FormException(ex.toString());
		}
	}

	public void setMainComponent(Component comp) throws FormException {
		// TODO setMainComponent
		logger.warn("TODO");
		throw new FormException("Not yet implemented.");
		// mainPanel = new FormPanel(null);
		// replaceComponent("form.panel", comp);
	}

	public boolean hasMainComponent() {
		return mainPanel != null;
	}

	public void setSize(int width, int height) {
		size = new Dimension(width, height);
		dialogChanged = true;
	}

	public void setName(String name) {
		this.title = i18n.getTextFor(name + ".text");
		this.icon = i18n.getIconFor(name + ".icon");
		dialogChanged = true;
	}

	public void setHelpKey(String key) {
		this.helpKey = key;
		dialogChanged = true;
	}

	public void setHelpKey(String key, FormClosedListener helpFormlistener) {
		this.helpKey = key;		
		this.helpFormlistener = helpFormlistener;
		dialogChanged = true;
	}

	public void showDialog() {
		showDialog(defaultOwner);
	}

	public void showDialog(boolean modal) {
		if (modal)
			showDialog();
		else
			showDialog((FormClosedListener) null);
	}

	public void showDialog(Component owner) {
				
		if (dialogChanged)
		  dialog = buildDialog(owner);
		this.listener = null;
		dialog.setModal(true);
		dialog.validate();

		//Se la finestra dell'Help è aperta bisogna ricostruirla
		if (help != null && help.isVisible()) 
			help.getHelpForm().rebuildHelp(helpFormlistener, getAsComponent());
		
		dialog.show();	    		
	}

	public void showDialog(FormClosedListener listener) {
		showDialog(listener, defaultOwner);
	}

	public void showDialog(FormClosedListener listener, Component owner) {
		
		dialog = buildDialog(owner);
		
		this.listener = listener;
		dialog.setModal(false);
		dialog.validate();
		dialog.show();
	}

	public void rebuildHelp(FormClosedListener listener, Component owner) {
					
		Rectangle r = dialog.getBounds();
		close();
		dialog = buildDialog(owner);
		dialog.setBounds(r);
				
		this.listener = listener;
		dialog.setModal(false);
		dialog.validate();
		dialog.show();
	}
	
	public boolean isDialogVisible() {
		return dialog != null && dialog.isVisible();
	}

	public void setDialogWaiting(boolean waiting) {
		this.waiting = waiting;
		if (dialog != null) {
			Component c = dialog.getGlassPane();
			if (c != null) {
				c.addMouseListener(new MouseAdapter() {
				});
				c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				c.setVisible(waiting);
			}
		}
	}

	public boolean isDialogWaiting() {
		return this.waiting;
	}

	public void setDialogResizable(boolean resizable) {
		this.resizable = resizable;
		if (dialog != null)
			dialog.setResizable(resizable);
	}

	public boolean isDialogResizable() {
		return this.resizable;
	}

	public Component getAsComponent() {
		applyI18n();
		if (size != null)
			mainPanel.setSize(size);
		return mainPanel;
	}

	public Component getAsComponentWithDecor() {
		applyI18n();
		if (size != null)
			getForm().setSize(size);
		return form;
	}
	
	public Component getComponentByName(String compName) {
		return mainPanel.getFormAccessor().getComponentByName(compName);
		// return getComponentByName(compName, mainPanel);
	}

	protected Component getComponentByName(String compName, Container container) {
		Component[] cs = container.getComponents();
		for (int i = 0; i < cs.length; i++)
			if (compName.equals(cs[i].getName()))
				return cs[i];
			else if (cs[i] instanceof Container) {
				Component c = getComponentByName(compName, (Container) cs[i]);
				if (c != null)
					return c;
			}
		return null;
	}
	
	public void replaceComponent(String compName, InputStream desc)
			throws FormException {
		try {
			mainPanel.getFormAccessor().replaceBean(compName,
					new FormPanel(desc));
		} catch (com.jeta.forms.gui.common.FormException ex) {
			throw new FormException(ex.toString());
		}
	}

	public void replaceComponent(String compName, Component comp)
			throws FormException {
		mainPanel.getFormAccessor().replaceBean(compName, comp);
	}

	public void setCustomButtons(String[] text) {
		buttons = true;
		btnPanel.removeAll();
		defaultButtons = null;
		if (text != null)
			for (int i = 0; i < text.length; i++) {
				Action action = utilUi.applyI18n(text[i], new MyButtonAction(
						i + 1));
				JButton btn = new JButton(action);
				btnPanel.add(btn);
				if (i == 0)
					defaultButtons = btn;
			}
		btnPanel.setVisible(false);
		btnPanel.setVisible(true);
	}

	public boolean isOk() {
		return buttonPressed == 1;
	}

	public boolean isCancel() {
		return buttonPressed == 2;
	}

	public int getPressedButton() {
		return buttonPressed;
	}

	public void close() {
		
		boolean aggiornaHelp = false;
		//Se l'Help è aperto e sono su una modale lo ricostruisco per la generale
		if (dialog.isModal() &&  help != null && help.isVisible())
			aggiornaHelp = true;
		
		if (listener != null)			
			listener.formClosed();
		dialog.setVisible(false);
		
		if (aggiornaHelp)
			// TODO NON è detto per la generale...ci potrebbe essere un altra (modale) di mezzo !!!!
			help.getHelpForm().rebuildHelp(null, defaultOwner);
	}

	public void addFormVerifier(FormVerifier verifier) {
		if (!verifiers.contains(verifier))
			verifiers.addElement(verifier);
	}

	public void removeFormVerifier(FormVerifier verifier) {
		verifiers.removeElement(verifier);
	}

	public void removeAllFormVerifier() {
		verifiers.removeAllElements();
	}

	public void addCutCopyPastePopupMenu(String name) {
		addCutCopyPastePopupMenu(getComponentByName(name));
	}

	public void addCutCopyPastePopupMenu(Component comp) {
		if (comp != null)
			comp.addMouseListener(cutCopyPasteMouseAdapter);
	}

	// /////////////////////////////////////////////////////// Protected Mothods
	protected void applyI18n() {
		applyI18n((JComponent) mainPanel);
	}

	protected void applyI18n(JComponent comp) {
		Component[] cs = comp.getComponents();
		for (int i = 0; i < cs.length; i++) {
			try {
				JComponent c = (JComponent) cs[i];
				utilUi.applyI18n(c);
				applyI18n(c);
			} catch (ClassCastException ex) {
			} catch (ConcurrentModificationException ex) {
			}
		}
	}

	protected void setButtonPressed(int buttonPressed) {
		if (buttonPressed == 1 && !verifyForm())
			return;
		this.buttonPressed = buttonPressed;
		close();
		// dialog.setVisible(false);
	}

	protected boolean verifyForm() {
		verifiers.elements();
		for (Enumeration en = verifiers.elements(); en.hasMoreElements();)
			try {
				FormVerifier verifier = (FormVerifier) en.nextElement();
				if (!verifier.verifyForm()) {
					utilMsg.msgError(dialog, verifier.getErrorMessage());
					return false;
				}
			} catch (Exception ex) {
				utilMsg.msgError(dialog, "form.verifier.error");
				return false;
			}
		return true;
	}

	protected JDialog buildDialog(Component owner) {
		JDialog newDialog = null;
		// cerco padre che sia Frame o Dialog
		Component myOwner = owner;
		while (myOwner != null && !(myOwner instanceof Frame)
				&& !(myOwner instanceof Dialog))
			myOwner = myOwner.getParent();
		if (myOwner != null) {
			if (myOwner instanceof Frame)
				newDialog = new JDialog((Frame) myOwner);
			else
				newDialog = new JDialog((Dialog) myOwner);
		}
		if (newDialog == null)
			newDialog = new JDialog();
		newDialog.getContentPane().setLayout(new BorderLayout());
		newDialog.getContentPane().add(getAsComponentWithDecor(),
				BorderLayout.CENTER);
		if (!buttons)
			setCustomButtons(new String[] { "form.button.ok",
					"form.button.cancel" });
		if (buttons && defaultButtons != null)
			newDialog.getRootPane().setDefaultButton(defaultButtons);
		newDialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);
		try {
			getForm().getFormAccessor().replaceBean("form.panel", mainPanel);
			if (title == null)
				setName("form");
			JLabel lblTitle = (JLabel) getForm().getComponentByName(
					"form.title");
			lblTitle.setText(title);
			JLabel lblIcon = (JLabel) getForm().getComponentByName("form.icon");
			lblIcon.setIcon(icon);
			if (help.hasKey(helpKey))
				getForm().getFormAccessor("form.top").replaceBean("form.help",
						helpButton);
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		if (title != null)
			newDialog.setTitle(title);
		newDialog.pack();
		if (size != null)
			newDialog.setSize(size);
		newDialog.setResizable(resizable);
		if (myOwner != null)
			newDialog.setLocationRelativeTo(defaultOwner);
		newDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				close();
			}
		});

		dialogChanged = false;
		return newDialog;
	}

	protected FormPanel getForm() {
		if (form == null)
			try {
				initialize();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		return form;
	}

	/**
	 * Azione per i pulsanti della form di dialogo.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class MyButtonAction extends AbstractAction {
		int n;

		public MyButtonAction(int n) {
			this.n = n;
		}

		public void actionPerformed(ActionEvent e) {
			logger.debug("action:" + e.getActionCommand());
			setButtonPressed(n);
		}
	}

	/**
	 * Azione per attivare l'help.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class HelpButtonAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			logger.debug("Call help on: " + helpKey);
			help.helpOnForm(helpKey, helpFormlistener, getAsComponent());	
		}
	}

	/**
	 * Azione per tagliare la selezione dai componenti.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class CutAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (popupComponent != null)
				popupComponent.cut();
		}

	}

	/**
	 * Azione per copiare la selezione dai componenti.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class CopyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (popupComponent != null)
				popupComponent.copy();
		}
	}

	/**
	 * Azione per incollare testo nei componenti.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class PasteAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (popupComponent != null) {
				if (UtilClipboard.hasString())
					popupComponent.paste();
				else
					popupComponent.replaceSelection(UtilDom
							.getRecursiveTextNode(UtilClipboard.getAsNode()));
			}
		}
	}

	JTextComponent popupComponent;

	protected class CutCopyPasteMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				try {
					popupComponent = (JTextComponent) e.getComponent();
					cutAction
							.setEnabled(popupComponent.getSelectedText() != null);
					copyAction
							.setEnabled(popupComponent.getSelectedText() != null);
					pasteAction.setEnabled(UtilClipboard.hasString()
							|| UtilClipboard.hasNode());
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				} catch (Exception ex) {
					cutAction.setEnabled(false);
					copyAction.setEnabled(false);
					pasteAction.setEnabled(false);
				}
			}
		}
	}
}
