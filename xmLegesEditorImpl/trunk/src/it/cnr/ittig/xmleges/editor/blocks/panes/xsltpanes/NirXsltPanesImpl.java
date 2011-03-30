package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpanes;

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
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.DeleteNextPrevAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.InsertBreakAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.KeyTypedAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.partizioni.Partizioni;
import it.cnr.ittig.xmleges.editor.services.panes.xsltpanes.NirXsltPanes;
import it.cnr.ittig.xmleges.editor.services.panes.xslts.NirXslts;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.xsltpanes.NirXsltPanes</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>frame</li>
 * <li>nir-panes-xslts</li>
 * <li>document-manager</li>
 * <li>util-rules-manager</li>
 * <li>selection-manager</li>
 * <li>nir-util-dom</li>
 * <li>editor-dom-partizioni</li>
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class NirXsltPanesImpl implements NirXsltPanes, Loggable, Serviceable, Configurable, Initializable, InsertBreakAction, DeleteNextPrevAction, KeyTypedAction {
	Logger logger;

	Frame frame;

	NirXslts xslts;

	ServiceManager serviceManager;

	DocumentManager documentManager;

	RulesManager rulesManager;

	UtilRulesManager utilRulesManager;

	SelectionManager selectionManager;

	NirUtilDom nirUtilDom;

	Partizioni partizioni;

	Configuration configuration;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		this.serviceManager = serviceManager;
		frame = (Frame) serviceManager.lookup(Frame.class);
		xslts = (NirXslts) serviceManager.lookup(NirXslts.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		partizioni = (Partizioni) serviceManager.lookup(Partizioni.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		this.configuration = configuration;
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		Configuration[] panes = configuration.getChildren("pane");

		for (int i = 0; i < panes.length; i++) {
			String name = "???";
			try {
				// <pane name="editor.panes.intestazione">
				// <xslt name="intestazione"/>
				// <css name="intestazione" />
				// </pane>
				name = panes[i].getAttribute("name", "noname");
				String xslt = null;
				Vector css = new Vector();
				Configuration[] c = panes[i].getChildren();
				for (int j = 0; j < c.length; j++) {
					String n = c[j].getAttribute("name");
					if (c[j].getName().equals("xslt"))
						xslt = n;
					else
						css.addElement(n);
				}
				logger.debug("Adding '" + name + "' to frame...");
				if (logger.isDebugEnabled()) {
					logger.debug("name=" + name);
					logger.debug("xslt=" + xslt);
					logger.debug("css=" + css.toString());
				}
				XsltPane xsltPane = (XsltPane) serviceManager.lookup(XsltPane.class);
				xsltPane.setName(name);
				if (css.size() > 0) {
					xsltPane.set(xslts.getXslt(xslt), xslts.getCss((String) css.get(0)), null);
					for (int j = 1; j < css.size(); j++)
						xsltPane.addStyleSheet(xslts.getCss((String) css.get(j)));
				} else
					xsltPane.set(xslts.getXslt(xslt), null, null);
				xsltPane.setInsertBreakAction(this);
				xsltPane.setDeleteNextPrevAction(this);
				xsltPane.setKeyTypedAction(this);
				frame.addPane(xsltPane, false);
				logger.debug("Adding '" + name + "' to frame OK");
			} catch (ConfigurationException ex) {
				logger.error(ex.toString(), ex);
				logger.debug("Adding '" + name + "' to frame KO");
			}

		}
	}

	// ///////////////////////////////////////////// InsertBreakAction Interface
	public void insertBreak(Node node, int start, int end) {
		if (node == null)
			return;

		String nodeName = node.getNodeName();		
		

		if(node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && node.getNodeValue().startsWith("<rif")) {   // processing instruction di tipo rif (rif incompleti)
			return;
		}else if ("h:p".equals(node.getParentNode().getNodeName()) || "h:li".equals(node.getParentNode().getNodeName())) {
			String text = null;
			Node parent = node.getParentNode();
			if (UtilDom.isTextNode(node)) {
				text = node.getNodeValue();
				nodeName = parent.getNodeName();
			}
			if (start > 0) {
				if (utilRulesManager.canInsertAfter(parent, nodeName)) {
					EditTransaction tr = null;
					try {
						tr = documentManager.beginEdit(this);
						Node n = utilRulesManager.getNodeTemplate(node.getOwnerDocument(), nodeName);
						UtilDom.insertAfter(n, parent);
						if (text != null && start < text.length() && rulesManager.queryTextContent(n)) {
							Node[] nexts = UtilDom.getAllNextSibling(node);
							Node t = node.getOwnerDocument().createTextNode(text.substring(start));
							n.appendChild(t);
							node.setNodeValue(text.substring(0, start));
							for (int i = 0; i < nexts.length; i++)
								n.appendChild(nexts[i]);
						}
						documentManager.commitEdit(tr);
						selectionManager.setActiveNode(this, n);
					} catch (Exception ex) {
						logger.error(ex.toString(), ex);
						documentManager.rollbackEdit(tr);
					}
				}
			} else if (utilRulesManager.canInsertBefore(parent, nodeName)) {
				EditTransaction tr = null;
				try {
					tr = documentManager.beginEdit(this);
					Node n = utilRulesManager.getNodeTemplate(nodeName);
					parent.getParentNode().insertBefore(n, parent);
					documentManager.commitEdit(tr);
					selectionManager.setActiveNode(this, n);
				} catch (Exception ex) {
					logger.error(ex.toString(), ex);
					documentManager.rollbackEdit(tr);
				}
			}
		} else {
			Node container = nirUtilDom.getParentContainer(node);
			String contName = container.getNodeName();
			if ("comma".equals(contName) || "el".equals(contName) || "en".equals(contName) || "ep".equals(contName)) {
			String contained = node.getNodeValue();
			Node[] nexts = UtilDom.getAllNextSibling(node);
			int azione = partizioni.canInsertNuovaPartizione(node, container);
			if (azione != -1) {
				Node n = partizioni.nuovaPartizione(node, contName, azione);
				EditTransaction tr = null;
				// n: nuovo nodo; node: nodo di partenza
				try { // gestione spezzamento testo
					tr = documentManager.beginEdit();
					if (UtilDom.findDirectChild(n, "corpo") != null
							&& (node.getParentNode().getNodeName().equals("corpo") || node.getParentNode().getNodeName().equals("alinea"))) { // sono
						// dentro
						// un
						// corpo
						if (contained != null) {
							Node corpoParent = node.getParentNode();
							node.setNodeValue(contained.substring(0, start));
							UtilDom.removeEmptyText(corpoParent);
							if (n != null) {
								Node newCorpo = UtilDom.findDirectChild(n, "corpo");
								UtilDom.setTextNode(newCorpo, contained.substring(start));
								for (int i = 0; i < nexts.length; i++)
									newCorpo.appendChild(nexts[i]);
								UtilDom.removeEmptyText(n);
							}
						}
					}
					documentManager.commitEdit(tr);
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					documentManager.rollbackEdit(tr);
				}
				if (n != null) {
					Node corpo = UtilDom.findDirectChild(n, "corpo");
					if (corpo != null)
						selectionManager.setActiveNode(this, corpo);
					else
						selectionManager.setActiveNode(this, n);
				}
			}
		} 
	}
	}

	// ////////////////////////////////////////// DeleteNextPrevAction Interface
	
	public boolean canBackSpaceOnStart(Node curr, Node prev) {
		
		if(curr.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && curr.getNodeValue().startsWith("<rif")) {
			return false;
		}
		return true;
	}
	
	
	public int backspaceOnStart(Node curr, Node prev) {
		
		EditTransaction tr = null;
		try {
			Node currPar = curr.getParentNode();
			Node prevPar = prev.getParentNode();
			if (rulesManager.queryCanDelete(currPar, curr) && rulesManager.queryCanInsertAfter(prevPar, prev, curr)) {
				tr = documentManager.beginEdit();
				int cursor = prev.getNodeValue().length();
				UtilDom.insertAfter(curr, prev);
				UtilDom.mergeTextNodes(prevPar);
				if (!currPar.hasChildNodes() && rulesManager.queryCanDelete(currPar.getParentNode(), currPar))
					currPar.getParentNode().removeChild(currPar);
				documentManager.commitEdit(tr);
				selectionManager.setSelectedText(this, prev, cursor, cursor);
				return cursor;
			}

		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			documentManager.rollbackEdit(tr);
		}
		return -1;
	}
	
	
	public boolean canDeleteOnEnd(Node curr, Node next) {
		
		if(curr.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && curr.getNodeValue().startsWith("<rif")) {
			return false;
		}
		return true;
		
	}

	public int deleteOnEnd(Node curr, Node next) {
		EditTransaction tr = null;
		try {
			Node currPar = curr.getParentNode();
			Node nextPar = next.getParentNode();
			if (rulesManager.queryCanDelete(nextPar, next) && rulesManager.queryCanInsertAfter(currPar, curr, next)) {
				tr = documentManager.beginEdit();
				int cursor = curr.getNodeValue().length();
				UtilDom.insertAfter(next, curr);
				UtilDom.mergeTextNodes(curr.getParentNode());
				if (!nextPar.hasChildNodes() && rulesManager.queryCanDelete(nextPar.getParentNode(), nextPar))
					nextPar.getParentNode().removeChild(nextPar);
				documentManager.commitEdit(tr);
				selectionManager.setSelectedText(this, curr, cursor, cursor);
				return cursor;
			}
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			documentManager.rollbackEdit(tr);
		}
		return -1;
	}

	
	
	/**
	 * previene l'editing di processing instruction di tipo rif (tiferimenti incompleti)
	 */
	public boolean canKeyTyped(Node node, int start, int end) {    
		if(node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && node.getNodeValue().startsWith("<rif")) {
			return false;
		}
		return true;
	}

}
