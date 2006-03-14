package it.cnr.ittig.xmleges.editor.blocks.panes.attributi;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributesPane;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.interni.RinviiInterniForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.panes.attributi.AttributiPane;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.attributi.AttributiPane</code>.
 * </h1>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class AttributiPaneImpl implements AttributiPane, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Frame frame;

	AttributesPane attributesPane;

	UrnForm urnForm;

	RinviiInterniForm rinviiInterni;

	SelectionManager selectionManager;

	DocumentManager documentManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		attributesPane = (AttributesPane) serviceManager.lookup(AttributesPane.class);
		urnForm = (UrnForm) serviceManager.lookup(UrnForm.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		rinviiInterni = (RinviiInterniForm) serviceManager.lookup(RinviiInterniForm.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		attributesPane.addEditor(new DataAttributeEditor());
		attributesPane.addEditor(new UrnAttributeEditor(urnForm));
		attributesPane.addEditor(new InternalRifAttributeEditor(rinviiInterni, documentManager, selectionManager, attributesPane));
		attributesPane.setName("editor.panes.attributi");
		frame.addPane(attributesPane, false);
	}

}
