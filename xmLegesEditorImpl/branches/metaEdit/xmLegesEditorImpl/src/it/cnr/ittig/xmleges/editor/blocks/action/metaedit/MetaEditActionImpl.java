package it.cnr.ittig.xmleges.editor.blocks.action.metaedit;

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
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.metaedit.MetaEditAction;
import it.cnr.ittig.xmleges.editor.services.dom.metaedit.MetaEdit;
import it.cnr.ittig.xmleges.editor.services.form.metaedit.MetaEditForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

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
public class MetaEditActionImpl implements MetaEditAction, EventManagerListener, Loggable, Serviceable, Configurable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;
	
	SelectionManager selectionManager;

	DocumentManager documentManager;

	AbstractAction metaEditAction = new metaEditAction();

	MetaEditForm metaEditForm;
	
	MetaEdit metaEdit;
	
	Node activeNode;
	
	NirUtilDom nirUtilDom;


	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		metaEdit = (MetaEdit) serviceManager.lookup(MetaEdit.class);
		metaEditForm = (MetaEditForm) serviceManager.lookup(MetaEditForm.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.metaedit.edit", metaEditAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		metaEditAction.setEnabled(false);		
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
		}
		enableActions();
	}

	// ////////////////////////////////////////////// MetaEditAction Interface
	public void doMetaEdit() {
		
		String idPartizione = getIdPartizione(activeNode);
		
		logger.info("metaEdit Action");
		
		
		Node[] onDocument = metaEdit.getDAFromDocument(idPartizione);
		
		if(onDocument == null || onDocument.length==0)
			System.err.println("NO META MARKUP FOR: "+idPartizione);
		else{
			for(int i=0; i<onDocument.length;i++){
				System.err.println("META MARKUP FOR: "+idPartizione+" "+onDocument[i].getNodeName());
				System.err.println("PASSING:  \n"+UtilDom.domToString(onDocument[i],true,"  "));
			}
		}

		metaEditForm.setIdPartizione(idPartizione);
		metaEditForm.setDisposizioni(onDocument);
		metaEditForm.openForm();
		if (metaEditForm.isOKClicked()) {
			Node[] onPartition = metaEditForm.getDisposizioni();
			
			if(onPartition== null || onPartition.length==0)
				System.err.println("NO META MARKUP FOR: "+idPartizione);
			else{
				for(int i=0; i<onPartition.length;i++){
					System.err.println("META MARKUP FOR: "+idPartizione+" "+onPartition[i].getNodeName());
					System.err.println("PASSING:  \n"+UtilDom.domToString(onPartition[i],true,"  "));
				}
			}
				System.err.println("--------->  metaEditForm   OK");
		}
		
	}
	
	
	private String getIdPartizione(Node node){
		String id = null;
		Node container = nirUtilDom.getContainer(node);
		if(container!=null)
			id = UtilDom.getAttributeValueAsString(container, "id");
	    return id;
	}
	
	protected void enableActions() {
		metaEditAction.setEnabled(!documentManager.isEmpty());
		if (activeNode == null) {
			metaEditAction.setEnabled(false);
		} else {
			metaEditAction.setEnabled(true);
		}
	}

	// ///////////////////////////////////////////////// Azioni
	public class metaEditAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doMetaEdit();
		}
	}


}
