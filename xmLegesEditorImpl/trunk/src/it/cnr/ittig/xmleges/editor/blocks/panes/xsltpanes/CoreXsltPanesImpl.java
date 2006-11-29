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
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.DeleteNextPrevAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.InsertBreakAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.panes.xsltpanes.CoreXsltPanes;
import it.cnr.ittig.xmleges.editor.services.panes.xslts.NirXslts;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.xsltpanes.NirXsltPanes</code>.</h1>
 * <h1>Descrizione</h1>
 * ...
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere i seguenti tag:
 * <ul>
 * <li><code>&lt;pane&gt;</code>: che specifica il nome del pane, contiene il tag:
 * <ul>
 * <li><code>&lt;xslt&gt;</code>: specifica l'xsl da utilizzare per il pane.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.services.manager.ServiceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li> 
 * <li>it.cnr.ittig.xmleges.editor.services.panes.xslts.NirXslts:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.selection.SelectionManager:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuno
 *  
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
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @see it.cnr.ittig.xmleges.editor.services.document.DocumentManager
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.services.preference.PreferenceManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class CoreXsltPanesImpl implements CoreXsltPanes, Loggable, Serviceable, Configurable, Initializable, InsertBreakAction, DeleteNextPrevAction {
	Logger logger;

	Frame frame;

	NirXslts xslts;

	ServiceManager serviceManager;

	DocumentManager documentManager;

	DtdRulesManager dtdRulesManager;

	SelectionManager selectionManager;

	Configuration configuration;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		this.serviceManager = serviceManager;
		frame = (Frame) serviceManager.lookup(Frame.class);
		
		
		//TODO ??? ???? ?????
		xslts = (NirXslts) serviceManager.lookup(NirXslts.class);
		
		
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
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

		//ridefinire l'azione per InsertBreakAction

	}

	// ////////////////////////////////////////// DeleteNextPrevAction Interface
	public int backspaceOnStart(Node curr, Node prev) {
		EditTransaction tr = null;
		try {
			Node currPar = curr.getParentNode();
			Node prevPar = prev.getParentNode();
			if (dtdRulesManager.queryCanDelete(currPar, curr) && dtdRulesManager.queryCanInsertAfter(prevPar, prev, curr)) {
				tr = documentManager.beginEdit();
				int cursor = prev.getNodeValue().length();
				UtilDom.insertAfter(curr, prev);
				UtilDom.mergeTextNodes(prevPar);
				if (!currPar.hasChildNodes() && dtdRulesManager.queryCanDelete(currPar.getParentNode(), currPar))
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

	public int deleteOnEnd(Node curr, Node next) {
		EditTransaction tr = null;
		try {
			Node currPar = curr.getParentNode();
			Node nextPar = next.getParentNode();
			if (dtdRulesManager.queryCanDelete(nextPar, next) && dtdRulesManager.queryCanInsertAfter(currPar, curr, next)) {
				tr = documentManager.beginEdit();
				int cursor = curr.getNodeValue().length();
				UtilDom.insertAfter(next, curr);
				UtilDom.mergeTextNodes(curr.getParentNode());
				if (!nextPar.hasChildNodes() && dtdRulesManager.queryCanDelete(nextPar.getParentNode(), nextPar))
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
}
