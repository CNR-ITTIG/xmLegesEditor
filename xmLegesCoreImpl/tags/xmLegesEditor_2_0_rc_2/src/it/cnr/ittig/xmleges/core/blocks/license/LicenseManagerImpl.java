package it.cnr.ittig.xmleges.core.blocks.license;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormException;
import it.cnr.ittig.xmleges.core.services.license.LicenseManager;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;

import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.license.LicenseManager</code>.</h1>
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
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @see
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class LicenseManagerImpl implements LicenseManager, Loggable, Serviceable {
	Logger logger;

	PreferenceManager preferenceManager;

	Form form;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		form = (Form) serviceManager.lookup(Form.class);
	}

	// /////////////////////////////////////////////////////// License Interface
	public boolean checkLicense(String key, String text) {
		Properties props = preferenceManager.getPreferenceAsProperties(getClass().getName());
		if (props.containsKey(key) && props.get(key).equals("accepted"))
			return true;
		try {
			form.setMainComponent(getClass().getResourceAsStream("License.jfrm"));
			form.setCustomButtons(new String[] { "license.form.continue" });
			form.setName("license.form");
			form.setSize(500, 400);
			((JLabel) form.getComponentByName("license.form.version")).setText(key);
			((JEditorPane) form.getComponentByName("license.form.license")).setText(text);
			form.showDialog();
			JCheckBox check = (JCheckBox) form.getComponentByName("license.form.accept");
			boolean accept = check.isSelected();
			props.setProperty(key, accept ? "accepted" : "rejected");
			preferenceManager.setPreference(getClass().getName(), props);
			return accept;
		} catch (FormException ex) {
			logger.fatalError(ex.toString(), ex);
			System.exit(-1);
			return false;
		}
	}
}
