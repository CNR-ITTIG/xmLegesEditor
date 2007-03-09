package it.cnr.ittig.xmleges.core.blocks.panes.corepanes;

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
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.panes.corepanes.CoreXsltPanes;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.DeleteNextPrevAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.InsertBreakAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane;
import it.cnr.ittig.xmleges.core.services.panes.xslts.Xslts;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;

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
 * @see
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class CoreXsltPanesImpl implements CoreXsltPanes, Loggable, Serviceable, Configurable, Initializable, InsertBreakAction, DeleteNextPrevAction {
	Logger logger;

	Frame frame;

	Xslts xslts;

	ServiceManager serviceManager;

	DocumentManager documentManager;

	DtdRulesManager dtdRulesManager;

	UtilRulesManager utilRulesManager;

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
		xslts = (Xslts) serviceManager.lookup(Xslts.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
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
	}

	// ////////////////////////////////////////// DeleteNextPrevAction Interface
	public int backspaceOnStart(Node curr, Node prev) {
		return -1;
	}

	public int deleteOnEnd(Node curr, Node next) {
		return -1;
	}
}
