package it.cnr.ittig.xmleges.core.blocks.form.filetextfield;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextField;
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextFieldListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.filetextfield.FileTextField</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * Nessuna.
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileTextFieldImpl implements FileTextField, Loggable, Serviceable, Initializable, KeyListener, ActionListener {
	Logger logger;

	Form form;

	JTextField field;

	boolean readonly = false;

	boolean directory = false;

	Color defColor;

	JFileChooser fileChooser = new JFileChooser();

	Vector listeners = new Vector();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("FileTextField.jfrm"));
		field = (JTextField) form.getComponentByName("form.filetextfield.file");
		field.addKeyListener(this);
		defColor = field.getForeground();
		AbstractButton btn = (AbstractButton) form.getComponentByName("form.filetextfield.btn");
		btn.setText("");
		btn.addActionListener(this);
		setSelectDirectory(false);
	}

	// //////////////////////////////////////////////////// CommonForm Interface
	public Component getAsComponent() {
		return form.getAsComponent();
	}

	// ///////////////////////////////////////////////// FileTextField Interface
	public void set(String name) {
		field.setText(name);
		if (checkFile())
			fire(new File(name));
	}

	public void set(File file) {
		set(file.getAbsolutePath());
	}

	public void setFileFilter(FileFilter fileFilter) {
		fileChooser.setFileFilter(fileFilter);
	}

	public void setCheckReadOnly(boolean ro) {
		this.readonly = ro;
	}

	public boolean isCheckReadOnly() {
		return this.readonly;
	}

	public void setSelectDirectory(boolean directory) {
		this.directory = directory;
		if (directory)
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		else
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	public boolean isSelectDirectory() {
		return this.directory;
	}

	public File getFile() {
		return checkFile() ? new File(field.getText()) : null;
	}

	public void addFileTextFieldListener(FileTextFieldListener listener) {
		if (!listeners.contains(listener))
			listeners.addElement(listener);
	}

	public void removeFileTextFieldListener(FileTextFieldListener listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}

	public void removeAllFileTextFieldListener() {
		listeners.clear();
	}

	// /////////////////////////////////////////////////// KeyListener Interface
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (checkFile())
			fire(new File(field.getText()));
	}

	public void keyTyped(KeyEvent e) {
	}

	// //////////////////////////////////////////////// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		fileChooser.setSelectedFile(new File(field.getText()));
		if (fileChooser.showOpenDialog(field) == JFileChooser.APPROVE_OPTION)
			set(fileChooser.getSelectedFile());
	}

	protected boolean checkFile() {
		File f = new File(field.getText());
		boolean check = f.exists() && f.canRead();
		if (readonly && !f.canWrite())
			check = false;
		if (!directory && !f.isFile())
			check = false;
		field.setForeground(check ? defColor : Color.RED);
		return check;
	}

	protected void fire(File f) {
		for (Enumeration en = listeners.elements(); en.hasMoreElements();)
			((FileTextFieldListener) en.nextElement()).fileSelected(this, f);
	}
}
