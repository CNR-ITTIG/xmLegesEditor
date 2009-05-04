package it.cnr.ittig.xmleges.editor.blocks.form.repository;

import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.editor.services.form.repository.RepositoryForm;


/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.repository.RepositoryForm</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
 * @version 1.0
 */

public class RepositoryFormImpl implements RepositoryForm, Loggable, Serviceable, Initializable, Startable {
	
	Logger logger;
	Form form;
	PreferenceManager preferenceManager;

	boolean lastAttiva = false;
	String lastRepoHost = null;
	String lastRepoUser = null;
	String lastRepoPass = null;
	
	JCheckBox abilita;
	JTextField host;
	JTextField user;
	JPasswordField password;
	JLabel etiabilita;
	JLabel etihost;
	JLabel etiuser;
	JLabel etiPassword;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Repository.jfrm"));
		form.setSize(450, 200);
		form.setName("editor.form.repository");
		
		form.setHelpKey("help.contents.form.repository");
		
		etiabilita = (JLabel) form.getComponentByName("editor.form.repository.etiabilita");
		etihost = (JLabel) form.getComponentByName("editor.form.repository.etihost");
		etiuser = (JLabel) form.getComponentByName("editor.form.repository.etiuser");
		etiPassword = (JLabel) form.getComponentByName("editor.form.repository.etipassword");
		abilita = (JCheckBox) form.getComponentByName("editor.form.repository.abilita");
		host = (JTextField) form.getComponentByName("editor.form.repository.host");
		user = (JTextField) form.getComponentByName("editor.form.repository.user");
		password = (JPasswordField) form.getComponentByName("editor.form.repository.password");
		
		Properties p = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		try {
			lastAttiva = p.getProperty("lastattiva").equalsIgnoreCase("true");
			lastRepoHost = p.getProperty("lastrepohost");
			lastRepoUser = p.getProperty("lastrepouser");
		} catch (Exception ex) {}
		
		abilita.setSelected(lastAttiva);
		host.setText(lastRepoHost);
		user.setText(lastRepoUser);

	}

	// /////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {}

	public void stop() throws Exception {

		logger.debug("saving last host/user per il repository delle norme");
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			if (abilita.isSelected())
				p.setProperty("lastattiva", "true");
			else
				p.setProperty("lastattiva", "false");
			p.setProperty("lastrepohost", host.getText());
			p.setProperty("lastrepouser", user.getText());
		} catch (Exception ex) {}
		preferenceManager.setPreference(getClass().getName(), p);
		logger.debug("saving OK");
	}

	public String getHost() {
		return host.getText();
	}

	public String getLogin() {
		return user.getText();
	}

	public String getPassword() {
		return password.getText();
	}

	public boolean isAttivo() {
		return abilita.isEnabled();
	}

	public void openForm() {
		form.showDialog();
	}
}
