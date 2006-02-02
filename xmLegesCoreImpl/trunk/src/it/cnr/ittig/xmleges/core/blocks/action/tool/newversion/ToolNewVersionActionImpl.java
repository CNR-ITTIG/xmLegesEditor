package it.cnr.ittig.xmleges.core.blocks.action.tool.newversion;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.tool.newversion.ToolNewVersionAction;
import it.cnr.ittig.xmleges.core.services.version.Version;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.tool.newversion.ToolNewVersionAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra nell'ActionManager l'azione
 * <code>tool.newversion</code>.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.version.Version:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>tool.newversion: descrizione dell'azione come specificato nell'ActionManager;</li>
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
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.blocks.action.ActionManagerImpl
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ToolNewVersionActionImpl extends AbstractAction implements ToolNewVersionAction, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	Version version;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		version = (Version) serviceManager.lookup(Version.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("tool.version", this);
	}

	// ////////////////////////////////////////// ToolNewVersionAction Interface
	public void doCheckNewVersion() {
		version.update();
	}

	// ////////////////////////////////////////////////// AbstractAction Extends
	public void actionPerformed(ActionEvent e) {
		doCheckNewVersion();
	}
}
