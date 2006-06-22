package it.cnr.ittig.xmleges.core.blocks.help;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.help.Help;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.services.version.Version;

import javax.swing.JLabel;

/**
 * Implementazione del servizio it.cnr.ittig.xmleges.editor.services.help.Help. Questa implementazione
 * utilizza il servizio I18n per recuperare la pagina html. Ad esempio se si desidera
 * l'help sui menu che risiede nel file ./help/menu.html allora occorre invocare il metodo
 * <code>helpOn("help.menu")</code> e impostare nel file i18n:
 * 
 * <pre>
 *   help.menu=./help/menu.html
 * </pre>
 * 
 * file da i18n *
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class HelpImpl implements Help, Loggable, Serviceable {
	Logger logger;

	I18n i18n;

	UtilUI utilUI;

	Form aboutForm;

	Form helpForm;

	Version version;

	HelpDialog helpDialog;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		aboutForm = (Form) serviceManager.lookup(Form.class);
		helpForm = (Form) serviceManager.lookup(Form.class);
		version = (Version) serviceManager.lookup(Version.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		helpForm.setMainComponent(getClass().getResourceAsStream("Help.jfrm"));
		helpForm.setCustomButtons(null);
		helpForm.setName("help.panel");
		helpForm.setSize(800, 600);
		helpDialog = new HelpDialog(i18n, helpForm);
		aboutForm.setMainComponent(getClass().getResourceAsStream("About.jfrm"));
		aboutForm.setCustomButtons(new String[] { "generic.close" });
		aboutForm.setName("help.about");
		JLabel versionLbl = (JLabel) aboutForm.getComponentByName("help.about.version");
		versionLbl.setText(version.getVersion());
	}

	// ////////////////////////////////////////////////////////// Help Interface
	public void helpOn(String key) {
		try {
			if (!helpForm.hasMainComponent() || !aboutForm.hasMainComponent())
				initialize();
			if (!helpForm.isDialogVisible())
				helpForm.showDialog((FormClosedListener) null);
			// helpDialog.setDocument("file:///home/mirco/n/NirEditor/help/index.html");
			helpDialog.setDocument(i18n.getTextFor(key));
		} catch (Exception ex) {
			logger.error("Error opening help for key: " + key, ex);
		}

	}

	public boolean hasKey(String key) {
		return(!i18n.getTextFor(key).equals(key));
	}

	public void about() {
		try {
			if (!helpForm.hasMainComponent() || !aboutForm.hasMainComponent())
				initialize();
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		aboutForm.showDialog();
	}
}
