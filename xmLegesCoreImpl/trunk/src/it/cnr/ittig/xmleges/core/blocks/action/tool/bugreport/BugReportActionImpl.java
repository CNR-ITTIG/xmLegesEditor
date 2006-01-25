package it.cnr.ittig.xmleges.core.blocks.action.tool.bugreport;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.tool.bugreport.BugReportAction;
import it.cnr.ittig.xmleges.core.services.bugreport.BugReport;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.tool.bugreport.BugReportAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra nell'ActionManager l'azione <code>tool.bugtracer</code>
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.ui.UtilUI:1.0</li>
 * <li>dom4j:dom4j:1.5.2</li>
 * <li>jaxen:jaxen:1.1-beta-4</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>tool.xpatheval: descrizione dell'azione come specificato nell'ActionManager</li>
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
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.blocks.action.ActionManagerImpl
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class BugReportActionImpl extends AbstractAction implements BugReportAction, Loggable, Serviceable, Initializable {
	Logger logger;

	BugReport bugTracer;

	ActionManager actionManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		bugTracer = (BugReport) serviceManager.lookup(BugReport.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("tool.bugreport", this);
	}

	// ///////////////////////////////////////////// BugTracerAction Interface
	public void doBugReport() {
		bugTracer.openForm();
	}

	public void actionPerformed(ActionEvent e) {
		doBugReport();
	}

}
