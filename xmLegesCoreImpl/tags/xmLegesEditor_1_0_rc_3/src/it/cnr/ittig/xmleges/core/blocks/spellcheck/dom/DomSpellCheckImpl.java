package it.cnr.ittig.xmleges.core.blocks.spellcheck.dom;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.DomEdit;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheck;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheckWord;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheck;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckEvent;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckWord;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.util.EventObject;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementazione del servizio per la gestione del controllo ortografico del testo di un
 * documento DOM.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class DomSpellCheckImpl implements DomSpellCheck, Loggable, Serviceable, Initializable, EventManagerListener {

	Logger logger;

	SpellCheck spellCheck;

	EventManager eventManager = null;

	boolean enabled = false;

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		spellCheck = (SpellCheck) serviceManager.lookup(SpellCheck.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
	}

	public synchronized void manageEvent(EventObject event) {
		logger.debug("manageEvent di DomSpellCheck");
		if (this.enabled) {
			if (event instanceof DocumentOpenedEvent) {
				DocumentOpenedEvent e = (DocumentOpenedEvent) event;
				Node nirNode = e.getDocument().getElementsByTagName("NIR").item(0);
				logger.debug("Document Opened in DomSpellCheck");
				DomSpellCheckEvent scEvt = new DomSpellCheckEvent(e.getSource(), spellCheck(nirNode));
				if (scEvt.hasWords()) {
					logger.debug(" spellCheck not null in DocumentOpened: fired event");
					eventManager.fireEvent(scEvt);
				}
			}

			if (event instanceof DocumentChangedEvent) {
				logger.debug("documentChangedEvent in DomSpellCheck");
				DocumentChangedEvent e = (DocumentChangedEvent) event;

				for (int i = 0; i < e.getTransaction().getEdits().length; i++) {
					if (e.getTransaction().getEdits()[i].getType() == DomEdit.CHAR_NODE_MODIFIED
							|| e.getTransaction().getEdits()[i].getType() == DomEdit.NODE_INSERTED) {
						logger.debug("CHAR_NODE MODIFIED or NODE_INSERTED");
						DomSpellCheckEvent scEvt = new DomSpellCheckEvent(e.getSource(), spellCheck(e.getTransaction().getEdits()[i].getNode()));
						if (scEvt.hasWords()) {
							logger.debug(" spellCheck not null: fired event");
							eventManager.fireEvent(scEvt);
						}
					}
				}
			}
		}
	}

	public Vector getTextNodes(Node node, Vector nodes) {

		if (node.getNodeType() == Node.TEXT_NODE)
			nodes.add(node);

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				getTextNodes(list.item(i), nodes);
			}
		}

		return nodes;
	}

	public SpellCheck getSpellCheck() {
		return spellCheck;
	}

	public DomSpellCheckWord[] spellCheck(Node node) {
		if (node != null) {
			Vector childNodes = new Vector();
			Vector ret = new Vector();
			childNodes = getTextNodes(node, childNodes);
			for (int i = 0; i < childNodes.size(); i++) {
				SpellCheckWord[] spellCheckWords = spellCheck.spellCheck(((Node) childNodes.get(i)).getNodeValue());
				if (spellCheckWords.length > 0)
					for (int j = 0; j < spellCheckWords.length; j++)
						ret.add(new DomSpellCheckWordImpl(spellCheckWords[j], (Node) childNodes.get(i)));
			}
			if (ret.size() > 0) {
				DomSpellCheckWord[] domWords = new DomSpellCheckWord[ret.size()];
				ret.copyInto(domWords);
				return (domWords);
			}
			return (null);
		}
		return null;
	}

	public DomSpellCheckWord[] spellCheck(Node node, int start, int end) {
		Vector ret = new Vector();
		if (UtilDom.isTextNode(node)) {
			SpellCheckWord[] spellCheckWords = spellCheck.spellCheck(node.getNodeValue().trim().substring(start, end).trim());
			if (spellCheckWords.length > 0)
				for (int j = 0; j < spellCheckWords.length; j++)
					ret.add(new DomSpellCheckWordImpl(spellCheckWords[j], node));
			if (ret.size() > 0) {
				DomSpellCheckWord[] domWords = new DomSpellCheckWord[ret.size()];
				ret.copyInto(domWords);
				return (domWords);
			}
		}
		return (null);
	}

}
