package it.cnr.ittig.xmleges.core.blocks.action.help;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.help.HelpAction;
import it.cnr.ittig.xmleges.core.services.help.Help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.action.help.HelpAction</code>.
 * Questa implementazione registra nell'ActionManager le azioni <code>help.contents</code>
 * e <code>help.about</code>.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * 
 */
public class HelpActionImpl implements HelpAction, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	Help help;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		help = (Help) serviceManager.lookup(Help.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("help.contents", new ContentsAction());
		actionManager.registerAction("help.about", new AboutAction());
	}

	// //////////////////////////////////////////////////// HelpAction Interface
	public void doAbout() {
		logger.debug("Call About");
		help.about();
	}

	public void doContents() {
		logger.debug("Call Help");
		help.helpOn("help.contents.browser");
	}

	/**
	 * Azione per l'apertura delle informazione sul programma.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class AboutAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doAbout();
		}
	}

	/**
	 * Azione per l'apertura dell'aiuto.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */

	protected class ContentsAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doContents();
		}
	}

}
