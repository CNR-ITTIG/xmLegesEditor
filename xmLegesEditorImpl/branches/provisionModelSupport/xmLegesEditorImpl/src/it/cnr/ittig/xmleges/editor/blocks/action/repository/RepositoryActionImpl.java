package it.cnr.ittig.xmleges.editor.blocks.action.repository;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.editor.services.action.repository.RepositoryAction;
import it.cnr.ittig.xmleges.editor.services.form.repository.RepositoryForm;

/**
 * Servizio per l'attivazione di un repository di norme.
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
 */
public class RepositoryActionImpl implements RepositoryAction, Loggable, Serviceable, Initializable {

	Logger logger;
	RepositoryForm repositoryForm;
	AbstractAction repositoryAction = new repositoryAction();
	ActionManager actionManager;
	
	public void doRepository() {
		repositoryForm.openForm();
	}

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		repositoryForm = (RepositoryForm) serviceManager.lookup(RepositoryForm.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
	}

	public void initialize() throws Exception {
		actionManager.registerAction("tool.repository", repositoryAction);	
		repositoryAction.setEnabled(true);
	}
	
	// /////////////////////////////////////////////// Azioni
	public class repositoryAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doRepository();
		}
	}
}
