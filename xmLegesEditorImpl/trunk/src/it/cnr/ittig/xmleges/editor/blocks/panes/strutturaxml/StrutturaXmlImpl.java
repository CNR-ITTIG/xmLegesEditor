package it.cnr.ittig.xmleges.editor.blocks.panes.strutturaxml;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.Pane;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.panes.tree.TreePane;
import it.cnr.ittig.xmleges.core.services.panes.tree.TreePaneCellRenderer;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.panes.strutturaxml.StrutturaXml;

import java.awt.Component;

import javax.swing.Icon;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.strutturaxml.StrutturaXml</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * Configura il pannello strutturaXml
 * <h1>Configurazione</h1>
 * La configurazione ha ilseguente tag:
 * <ul>
 * <li><code>&lt;name&gt;</code>: che specifica la chiave i18n per il nome del pane; </li>
 * </ul>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.panes.tree.TreePane:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.panes.tree.TreePane:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.i18n.I18n:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>editor.panes.strutturaxml.testo</code>: chiave per l'icona di testo;</li>
 * <li><code>editor.panes.strutturaxml.commento</code>: chiave per l'icona di commento;</li>
 * <li><code>editor.panes.strutturaxml.errore</code>: chiave per l'icona di errore.</li>
 * </ul>
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class StrutturaXmlImpl implements Pane, StrutturaXml, TreePaneCellRenderer, Serviceable, Configurable, Initializable {
	Frame frame;

	TreePane treePane;

	I18n i18n;

	int size = 20;

	Icon iTesto;

	Icon iCommento;

	Icon iErrore;

	Rinumerazione rinumerazione;

	DocumentManager documentManager;

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		treePane = (TreePane) serviceManager.lookup(TreePane.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		treePane.setName(configuration.getChild("name").getValue());
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		frame.addPane(this, false);
		treePane.addRenderer(this);
		iTesto = i18n.getIconFor("editor.panes.strutturaxml.testo");
		iCommento = i18n.getIconFor("editor.panes.strutturaxml.commento");
		iErrore = i18n.getIconFor("editor.panes.strutturaxml.errore");
	}

	// ///////////////////////////////////////// TreePaneCelleRenderer Interface
	public boolean canRender(Node node) {
		if (node == null)
			return false;
		short type = node.getNodeType();
		return type == Node.ELEMENT_NODE || type == Node.CDATA_SECTION_NODE || type == Node.TEXT_NODE || type == Node.COMMENT_NODE
				|| type == Node.PROCESSING_INSTRUCTION_NODE || type == Node.ATTRIBUTE_NODE;
	}

	public String getText(Node node) {
		return getNodeSummary(node);
	}

	public boolean hasIcon(Node node) {
		short type = node.getNodeType();
		return type == Node.CDATA_SECTION_NODE || type == Node.TEXT_NODE || type == Node.COMMENT_NODE || type == Node.PROCESSING_INSTRUCTION_NODE;
	}

	public Icon getIcon(Node node, boolean sel, boolean expanded, boolean leaf) {
		switch (node.getNodeType()) {
		case Node.CDATA_SECTION_NODE:
		case Node.TEXT_NODE:
			return iTesto;
		case Node.COMMENT_NODE:
			return iCommento;
		case Node.PROCESSING_INSTRUCTION_NODE:
			return iErrore;
		default:
			return null;
		}
	}

	protected String getNodeSummary(Node node) {
		if (node == null)
			return "null";
		String ret = "";
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			String name = node.getNodeName();
			if ("el".equals(name) || "en".equals(name) || "ep".equals(name) || "comma".equals(name) || "articolo".equals(name) || "sezione".equals(name)
					|| "capo".equals(name) || "titolo".equals(name) || "parte".equals(name) || "libro".equals(name)) {
				if ("comma".equals(name))
					ret = "comma ";
				else if ("el".equals(name))
					ret = "lettera ";
				else if ("en".equals(name))
					ret = "numero ";
				else if ("ep".equals(name))
					ret = "punto ";
				String num = UtilDom.getRecursiveTextNode(UtilDom.findDirectChild(node, "num")).trim();
				if (num.length() > 0)
					ret += num;
				String rubrica = UtilDom.getTextNode(UtilDom.findDirectChild(node, "rubrica")).trim();
				if (rubrica.length() > 0)
					ret += " - " + rubrica;
				if (ret.length() == 0)
					ret = name;
			} else if ("annesso".equals(name)) {
				ret = i18n.getTextFor("dom." + node.getNodeName());
				Node testata = UtilDom.findDirectChild(node, "testata");
				ret += " - " + UtilDom.getRecursiveTextNode(UtilDom.findDirectChild(testata, "denAnnesso"));
			}

			// else if ("h:p".equals(name))
			// ret = "?";

			else
				ret = i18n.getTextFor("dom." + node.getNodeName());
			break;
		case Node.CDATA_SECTION_NODE:
		case Node.TEXT_NODE:
		case Node.COMMENT_NODE:
			ret = node.getNodeValue();
			break;
		case Node.PROCESSING_INSTRUCTION_NODE:
			ret = node.getNodeName() + ":" + node.getNodeValue();
			break;
		case Node.ATTRIBUTE_NODE:
			ret = node.getNodeName() + "=" + node.getNodeValue();
			break;
		}
		return restrict(ret);
	}

	protected String restrict(String value) {
		treePane.getPaneAsComponent().getWidth();
		if (value.length() > size)
			return (value.substring(0, size) + "...");
		return value;
	}

	public boolean canCopy() {
		return treePane.canCopy();
	}

	public boolean canCut() {
		return treePane.canCut();
	}

	public boolean canDelete() {
		return treePane.canDelete();
	}

	public boolean canFind() {
		return treePane.canFind();
	}

	public boolean canPaste() {
		return treePane.canPaste();
	}

	public boolean canPasteAsText() {
		return treePane.canPasteAsText();
	}

	public boolean canPrint() {
		return treePane.canPrint();
	}

	public void copy() throws PaneException {
		treePane.copy();
	}

	public void cut() throws PaneException {
		try {
			EditTransaction t = documentManager.beginEdit();
			treePane.cut();
			rinumerazione.aggiorna(documentManager.getDocumentAsDom());
			documentManager.commitEdit(t);
		} catch (DocumentManagerException ex) {
		}
	}

	public void delete() throws PaneException {
		try {
			EditTransaction t = documentManager.beginEdit();
			treePane.delete();
			rinumerazione.aggiorna(documentManager.getDocumentAsDom());
			documentManager.commitEdit(t);
		} catch (DocumentManagerException ex) {
		}
	}

	public void paste() throws PaneException {
		try {
			EditTransaction t = documentManager.beginEdit();
			treePane.paste();
			rinumerazione.aggiorna(documentManager.getDocumentAsDom());
			documentManager.commitEdit(t);
		} catch (DocumentManagerException ex) {
		}
	}

	public void pasteAsText() throws PaneException {
		try {
			EditTransaction t = documentManager.beginEdit();
			treePane.pasteAsText();
			rinumerazione.aggiorna(documentManager.getDocumentAsDom());
			documentManager.commitEdit(t);
		} catch (DocumentManagerException ex) {
		}
	}

	public Component getComponentToPrint() {
		return treePane.getComponentToPrint();
	}

	public FindIterator getFindIterator() {
		return treePane.getFindIterator();
	}

	public String getName() {
		return treePane.getName();
	}

	public Component getPaneAsComponent() {
		return treePane.getPaneAsComponent();
	}

	public void reload() {
		treePane.reload();
	}
}
