package it.cnr.ittig.xmleges.editor.blocks.action.annessi;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.editor.services.action.annessi.AnnessiAction;
import it.cnr.ittig.xmleges.editor.services.dom.annessi.Annessi;
import it.cnr.ittig.xmleges.editor.services.form.annessi.AnnessiForm;
import it.cnr.ittig.xmleges.editor.services.template.Template;
import it.cnr.ittig.xmleges.editor.services.template.TemplateException;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.EventObject;
import java.util.Properties;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.annessi.AnnessiAction</code>.</h1>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class AnnessiActionImpl implements AnnessiAction, EventManagerListener, Loggable, Serviceable, Configurable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;

	AbstractAction annAction = new annessiAction();

	Template template;

	Annessi annessi;

	AnnessiForm annessiForm;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		template = (Template) serviceManager.lookup(Template.class);
		annessi = (Annessi) serviceManager.lookup(Annessi.class);
		annessiForm = (AnnessiForm) serviceManager.lookup(AnnessiForm.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.annessi.annesso", annAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		annAction.setEnabled(false);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		annAction.setEnabled(!documentManager.isEmpty());

	}

	// ////////////////////////////////////////////// AnnessiAction Interface
	public void doNewAnnesso() {
		logger.info("annessi Action");

		annessiForm.openForm();
		if (annessiForm.isOKClicked()) {
			if (annessiForm.isEsterno())
				annessi.setAnnessoEsterno(annessiForm.getDenominazione(), annessiForm.getTitolo(), annessiForm.getPreambolo());
			if (annessiForm.isInternoFile())
				annessi.setAnnessoInterno(annessiForm.getDenominazione(), annessiForm.getTitolo(), annessiForm.getPreambolo(), annessiForm.getSelectedFile());
			if (annessiForm.isInternoTemplate())
				annessi.setAnnessoInterno(annessiForm.getDenominazione(), annessiForm.getTitolo(), annessiForm.getPreambolo(), getAnnessoTemplate(annessiForm
						.getTemplate()));
		}
	}

	// ///////////////////////////////////////////////// Azioni
	public class annessiAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doNewAnnesso();
		}
	}

	private File getAnnessoTemplate(String templateName) {

		File annessoTemplate = null;

		Properties p = new Properties();
		p.put("DOCTYPE", "<!DOCTYPE NIR SYSTEM \"" + documentManager.getGrammarName().trim() + "\">");
		try {
			annessoTemplate = template.getNirTemplate(templateName, p);
			logger.info("UtilityFile.copyTemplate");
		} catch (TemplateException ex) {
			logger.error("Errore nell'apertura del template" + ex.toString());
		}
		return (annessoTemplate);
	}

}
