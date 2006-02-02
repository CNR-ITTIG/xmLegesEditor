package it.cnr.ittig.xmleges.core.blocks.dom.comment;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dom.comment.Comment;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.comment.Comment</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>document-manager</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuna
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
 * @see
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public class CommentImpl implements Comment, Loggable, Serviceable {
	Logger logger;

	DocumentManager documentManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	public boolean canInsertComment(Node node, int start, int end) {
		return node != null && node.getNodeType() != Node.COMMENT_NODE;
	}

	public Node insertComment(Node node, int start, int end) {
		if (!canInsertComment(node, start, end))
			return null;
		Node c = node.getOwnerDocument().createComment("");
		insertNode(node, c, start, end);
		return c;
	}

	public boolean canInsertPI(Node node, int start, int end) {
		return node != null && node.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE;
	}

	public Node insertPI(Node node, String name, int start, int end) {
		if (!canInsertPI(node, start, end))
			return null;
		Node c = node.getOwnerDocument().createProcessingInstruction(name, "");
		insertNode(node, c, start, end);
		return c;
	}

	protected void insertNode(Node node, Node toIns, int start, int end) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (UtilDom.isTextNode(node)) {
				String text = node.getNodeValue();
				if (start <= 0) // nessuna posizione o all'inizio
					node.getParentNode().insertBefore(toIns, node);
				else if (start == text.length()) //
					UtilDom.insertAfter(toIns, node);
				else {
					node.setNodeValue(text.substring(0, start).trim());
					UtilDom.insertAfter(toIns, node);
					if (start != end) // testo selezione in toIns
						toIns.setNodeValue(text.substring(start, end).trim());
					Node dopo = node.cloneNode(false);
					dopo.setNodeValue(text.substring(end).trim());
					UtilDom.insertAfter(dopo, toIns);
				}
			} else {
				if (node.getNextSibling() != null)
					node.getParentNode().insertBefore(toIns, node.getNextSibling());
				else
					node.getParentNode().appendChild(toIns);
			}

			documentManager.commitEdit(tr);
		} catch (DocumentManagerException ex) {
			logger.error(ex.toString(), ex);
		}
	}

}
