package it.cnr.ittig.xmleges.core.blocks.action.edit.importdom;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.edit.importdom.ImportDomAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextField;
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextFieldListener;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.edit.importdom.ImportDomAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra le azioni <code>edit.undo</code> e
 * <code>edit.redo</code> nell'ActionManager.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.rules.RulesManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.form.Form:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextField:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.blocks.util.ui.UtilUI:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.util.msg.utilMsg:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>edit.importdom</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li><code>generic.close</code>: messaggio di chiusura della maschera;</li>
 * <li><code>help.contents.form.importdom</code>: file di help della maschera.</li>
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
 * @see it.cnr.ittig.xmleges.core.services.document.DocumentManager
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.services.rules.RulesManager
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ImportDomActionImpl implements ImportDomAction, EventManagerListener, FileTextFieldListener, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	DocumentManager documentManager;

	RulesManager rulesManager;

	EventManager eventManager;

	Form form;

	FileTextField fileTextField;

	UtilUI utilUI;

	UtilMsg utilMsg;

	ImportAction importAction;

	JTree src;

	JTree dest;

	DefaultTreeModel destModel;

	DefaultTreeModel srcModel;

	Vector imported = new Vector();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		form = (Form) serviceManager.lookup(Form.class);
		fileTextField = (FileTextField) serviceManager.lookup(FileTextField.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		importAction = new ImportAction();
		actionManager.registerAction("edit.importdom", importAction);
		importAction.setEnabled(!documentManager.isEmpty());

		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);

		form.setMainComponent(getClass().getResourceAsStream("ImportDom.jfrm"));
		form.setSize(600, 500);
		form.setName("edit.importdom.form");

		//TODO verificare necessit� di questo help
		form.setHelpKey("help.contents.form.importdom");
		
		form.replaceComponent("edit.importdom.form.file", fileTextField.getAsComponent());
		fileTextField.addFileTextFieldListener(this);
		fileTextField.setFileFilter(new RegexpFileFilter("XML", ".*\\.xml"));

		src = (JTree) form.getComponentByName("edit.importdom.form.src");
		src.setCellRenderer(new ImportDomTreeCellRenderer(this));
		src.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
		dest = (JTree) form.getComponentByName("edit.importdom.form.dest");

		form.setCustomButtons(new String[] { "generic.close" });

		ImportTransferHandler transfer = new ImportTransferHandler(this);
		src.setTransferHandler(transfer);
		src.setDragEnabled(true);
		dest.setTransferHandler(transfer);
		dest.setDragEnabled(true);

	}

	// ///////////////////////////////////////////// ImportDomAction Interface
	public void doImportDom() {
		destModel = utilUI.createDefaultTreeModel(documentManager.getRootElement());
		dest.setModel(destModel);
		form.showDialog();
	}

	// ///////////////////////////////////////// FileTextFieldListener Interface
	public void fileSelected(FileTextField ftf, File file) {
		imported.clear();
		Document d = UtilXml.readXML(file);
		if (d == null)
			utilMsg.msgError(fileTextField.getAsComponent(), "edit.importdom.msg.noopen");
		else {
			UtilDom.trimAndMergeTextNodes(d, true);
			srcModel = utilUI.createDefaultTreeModel(d.getDocumentElement());
			src.setModel(srcModel);
		}
	}

	public void addImported(Node node) {
		if (!imported.contains(node))
			imported.addElement(node);
	}

	public void addImported(Collection c) {
		for (Iterator it = c.iterator(); it.hasNext();)
			try {
				addImported((Node) it.next());
			} catch (ClassCastException ex) {
			}

	}

	public boolean isImported(Node node) {
		for (Enumeration en = imported.elements(); en.hasMoreElements();)
			if (UtilDom.isAncestor((Node) en.nextElement(), node))
				return true;
		return false;
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		importAction.setEnabled(!documentManager.isEmpty());
	}

	protected class ImportAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doImportDom();
		}
	}

}
