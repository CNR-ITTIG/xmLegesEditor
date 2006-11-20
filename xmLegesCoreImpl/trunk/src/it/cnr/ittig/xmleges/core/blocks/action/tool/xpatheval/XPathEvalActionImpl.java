package it.cnr.ittig.xmleges.core.blocks.action.tool.xpatheval;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.tool.xpatheval.XPathEvalAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.dom4j.swing.DocumentTreeModel;
import org.dom4j.swing.LeafTreeNode;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.tool.xpatheval.XPathEvalAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra nell'ActionManager l'azione <code>tool.xpatheval</code>
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
public class XPathEvalActionImpl implements XPathEvalAction, EventManagerListener, TreeSelectionListener, ListSelectionListener, Loggable, Serviceable,
		Initializable {
	Logger logger;

	ActionManager actionManager;

	DocumentManager documentManager;

	EventManager eventManager;

	UtilUI utilUi;

	Form form;

	JTextField xpathField;

	JTree xpathTree;

	JList xpathList;

	DOMReader domReader = new DOMReader();

	Document document;

	DocumentTreeModel treeModel;

	DefaultListModel listModel = new DefaultListModel();

	XPathAction evalAction;

	Hashtable nodeToLeaf = new Hashtable(1000);

	boolean evaluating = false;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		form = (Form) serviceManager.lookup(Form.class);
		utilUi = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		evalAction = new XPathAction();
		actionManager.registerAction("tool.xpatheval", evalAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(getClass().getResourceAsStream("XPathEval.jfrm"));
		form.setName("action.tool.xpatheval.form");
		
		//TODO verificare la necessità di questo HELP
		form.setHelpKey("help.contents.form.xpatheval");
		
		form.setSize(700, 600);
		Action btnAction = utilUi.applyI18n("action.tool.xpatheval.eval", new EvalButtonAction());
		xpathField = (JTextField) form.getComponentByName("action.tool.xpatheval.xpath");
		xpathField.setAction(btnAction);
		xpathTree = (JTree) form.getComponentByName("action.tool.xpatheval.xpathtree");
		xpathTree.addTreeSelectionListener(this);
		AbstractButton btn = (AbstractButton) form.getComponentByName("action.tool.xpatheval.eval");
		btn.setAction(btnAction);
		xpathList = (JList) form.getComponentByName("action.tool.xpatheval.xpathlist");
		xpathList.setModel(listModel);
		xpathList.addListSelectionListener(this);
		enableActions();
	}

	// ///////////////////////////////////////////// XPathEvalAction Interface
	public void doXPathEval() {
		document = domReader.read(documentManager.getDocumentAsDom());
		treeModel = new DocumentTreeModel(document);
		xpathTree.setModel(treeModel);
		mapNodeToLeaf((LeafTreeNode) treeModel.getRoot());
		form.showDialog();
	}

	protected void mapNodeToLeaf(LeafTreeNode node) {
		nodeToLeaf.put(node.getXmlNode(), node);
		Enumeration en = node.children();
		while (en.hasMoreElements())
			mapNodeToLeaf((LeafTreeNode) en.nextElement());
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		enableActions();
	}

	// /////////////////////////////////////// TreeSelectionListener Interface
	public void valueChanged(TreeSelectionEvent e) {
		if (!evaluating && !xpathTree.isSelectionEmpty()) {
			Object node = xpathTree.getLastSelectedPathComponent();
			if (node instanceof LeafTreeNode) {
				LeafTreeNode leaf = (LeafTreeNode) node;
				xpathField.setText(leaf.getXmlNode().getUniquePath());
			}
		}
	}

	// /////////////////////////////////////// ListSelectionListener Interface
	public void valueChanged(ListSelectionEvent e) {
		if (!evaluating && !xpathList.isSelectionEmpty()) {
			String xpath = xpathList.getSelectedValue().toString();
			xpathField.setText(xpath);
			selectNodeOnTree(xpath);
			xpathList.clearSelection();
		}
	}

	protected void enableActions() {
		evalAction.setEnabled(!documentManager.isEmpty());
	}

	protected class XPathAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doXPathEval();
		}
	}

	protected class EvalButtonAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (selectNodeOnTree(xpathField.getText()) && !listModel.contains(xpathField.getText()))
				listModel.addElement(xpathField.getText());
		}
	}

	protected synchronized boolean selectNodeOnTree(String xpath) {
		boolean ret = false;
		evaluating = true;
		try {
			List list = document.selectNodes(xpath);
			Iterator it = list.iterator();
			TreeSelectionModel slm = xpathTree.getSelectionModel();
			slm.clearSelection();
			while (it.hasNext()) {
				try {
					Node node = (Node) it.next();
					logger.info("node:" + node);
					TreeNode treeNode = (TreeNode) nodeToLeaf.get(node);
					TreeNode[] path = treeModel.getPathToRoot(treeNode);
					TreePath selPath = new TreePath(path);
					logger.info("dom4j leaf:" + nodeToLeaf.get(node));
					logger.info("PathToRoot:" + path);
					logger.info("TreePath:" + selPath);
					slm.addSelectionPath(selPath);
					ret = true;
				} catch (Exception ex) {
					logger.error(ex.toString(), ex);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		evaluating = false;
		return ret;
	}
}
