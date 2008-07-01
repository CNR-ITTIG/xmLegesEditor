package it.cnr.ittig.xmleges.core.blocks.util.rulesmanager;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.panes.xsltmapper.XsltMapper;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.util.rulesmanager.UtilRulesManager</code>. </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>dtd-rules-manager</li>
 * <li>document-manager</li>
 * <li>util-ui</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
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
 * @version 1.1
 * @author <a href="mailto:mollicone@ittig.cnr.it">Maurizio Mollicone </a>
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public class UtilRulesManagerImpl implements UtilRulesManager, Loggable, Serviceable, Configurable {
	Logger logger;
	
	XsltMapper xsltMapper;

	RulesManager rulesManager;

	DocumentManager documentManager;

	SelectionManager selectionManager;

	UtilUI utilUI;

	I18n i18n;

	Vector excludes = new Vector();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		xsltMapper = (XsltMapper) serviceManager.lookup(XsltMapper.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		if (configuration.hasChild("exclude")) {
			Configuration[] c = configuration.getChild("exclude").getChildren();
			for (int i = 0; i < c.length; i++)
				if (c[i].hasAttribute("name"))
					excludes.addElement(c[i].getAttribute("name"));
		}
	}

	

	// //////////////////////////////////////// UtilRulesManager Interface

	// crea il menu' cumulativo fondendo insertAfter, append, insertBefore
	// questo viene chiamato da right click su tree
	public JMenu createMenuInsert(Node node) {
		JMenu menu = createMenu("utilrulesmanager.menu.insert");
		if (node == null || node.getParentNode() == null)
			return menu;

		Collection coll = getFilteredSortedMenu(getMergedInsertable(node));

		for (Iterator it = coll.iterator(); it.hasNext();) {
			String next = (String) it.next();
			String nodeName = next.substring(0, next.indexOf(','));
			String action = next.substring(next.indexOf(',') + 1);
			JMenuItem menuItem = new JMenuItem(getMenuItemText(nodeName));
			if (action.equals("after"))
				menuItem.addActionListener(new InsertAfterAction(node, nodeName));
			else if (action.equals("append"))
				menuItem.addActionListener(new InsertNodeAction(node, nodeName));
			else
				menuItem.addActionListener(new InsertBeforeAction(node, nodeName));
			menu.add(menuItem);
			menu.setEnabled(true);
		}
		return menu;
	}

	// va popolato con la lista dei nodi inseribili dentro al testo
	public JMenu createMenuInsert(Node node, int start, int end) {

		JMenu menu = createMenu("utilrulesmanager.menu.insert");
		if (node == null || node.getParentNode() == null)
			return menu;

		Collection coll = getFilteredSortedMenu(queryInsertableNodeInText(node, start, end));

		for (Iterator it = coll.iterator(); it.hasNext();) {
			String next = (String) it.next();

			JMenuItem menuItem = new JMenuItem(getMenuItemText(next));
			menuItem.addActionListener(new InsertNodeInTextAction(node, next, start, end, true));
			menu.add(menuItem);
			menu.setEnabled(true);
		}
		return menu;
	}

	public JMenu createMenuInsertBefore(Node node) {
		JMenu menu = createMenu("utilrulesmanager.menu.insertbefore");
		if (node == null || node.getParentNode() == null)
			return menu;
		try {
			Collection coll = getFilteredSortedMenu(rulesManager.queryInsertableBefore(node.getParentNode(), node));

			for (Iterator it = coll.iterator(); it.hasNext();) {
				String next = (String) it.next();
				JMenuItem menuItem = new JMenuItem(getMenuItemText(next));
				menuItem.addActionListener(new InsertBeforeAction(node, next));
				menu.add(menuItem);
				menu.setEnabled(true);
			}
		} catch (RulesManagerException ex) {
		}
		return menu;
	}

	public JMenu createMenuInsertAfter(Node node) {
		JMenu menu = createMenu("utilrulesmanager.menu.insertafter");
		if (node == null || node.getParentNode() == null)
			return menu;
		try {
			Collection coll = getFilteredSortedMenu(rulesManager.queryInsertableAfter(node.getParentNode(), node));

			for (Iterator it = coll.iterator(); it.hasNext();) {
				String next = (String) it.next();
				JMenuItem menuItem = new JMenuItem(getMenuItemText(next));
				menuItem.addActionListener(new InsertAfterAction(node, next));
				menu.add(menuItem);
				menu.setEnabled(true);
			}
		} catch (RulesManagerException ex) {
		}
		return menu;
	}

	public JMenu createMenuInsertNode(Node node) {
		JMenu menu = createMenu("utilrulesmanager.menu.insertnode");
		if (node == null || node.getParentNode() == null)
			return menu;
		try {
			Collection coll = getFilteredSortedMenu(rulesManager.queryAppendable(node));
			for (Iterator it = coll.iterator(); it.hasNext();) {
				String next = (String) it.next();
				JMenuItem menuItem = new JMenuItem(getMenuItemText(next));
				menuItem.addActionListener(new InsertNodeAction(node, next));
				menu.add(menuItem);
				menu.setEnabled(true);
			}
		} catch (RulesManagerException ex) {
		}
		return menu;
	}

	// restituisce una collection di stringhe fatte da: nome_nodo,azione
	// con azione = after|append|before
	// la strategia e' 1 after 2 append 3 before
	protected Collection getMergedInsertable(Node node) {
		Vector ret = new Vector();
		if (node == null || node.getParentNode() == null)
			return ret;
		try {
			Collection coll1 = rulesManager.queryInsertableAfter(node.getParentNode(), node);
			Collection coll2 = rulesManager.queryAppendable(node);
			Collection coll3 = rulesManager.queryInsertableBefore(node.getParentNode(), node);

			for (Iterator it = coll1.iterator(); it.hasNext();) {
				String next = (String) it.next();
				ret.add(next + ",after");
			}

			for (Iterator it = coll2.iterator(); it.hasNext();) {
				String next = (String) it.next();
				if (ret.indexOf(next + ",after") == -1)
					ret.add(next + ",append");
			}

			for (Iterator it = coll3.iterator(); it.hasNext();) {
				String next = (String) it.next();
				if (ret.indexOf(next + ",after") == -1 && ret.indexOf(next + ",append") == -1)
					ret.add(next + ",before");
			}

			return ret;
		} catch (RulesManagerException ex) {
		}
		return null;
	}

	protected Collection getFilteredSortedMenu(Collection coll) {

		Vector sort = new Vector();
		Vector ret = new Vector();
		String nodeName;
		if (coll != null) {
			for (Iterator it = coll.iterator(); it.hasNext();) {
				String next = ((String) it.next());
				if (next.indexOf(',') != -1)
					nodeName = next.substring(0, next.indexOf(','));
				else
					nodeName = next;
				if (!isExcluded(nodeName)) {
					sort.add(getMenuItemText(nodeName) + "," + next);
				}
			}

			Collections.sort(sort);

			for (int i = 0; i < sort.size(); i++)
				ret.add(((String) sort.get(i)).substring(((String) sort.get(i)).indexOf(',') + 1));
		}
		return ret;
	}

	protected boolean isExcluded(String nodeName) {
		if (excludes == null)
			return false;
		if (excludes.contains(nodeName))
			return true;
		for (Iterator it = excludes.iterator(); it.hasNext();) {
			String name = (String) it.next();
			if (nodeName.matches(name))
				return true;
		}
		return false;
	}
	
		
	public boolean orderedInsertChild(Node parent, Node toInsert){
    	Node child = parent.getLastChild();
    	boolean inserted = false;
    	if(parent != null && toInsert != null){
    		// prova ad inserirlo AFTER
    		while (!inserted && child != null) {
        		try {
        			if (rulesManager.queryCanInsertAfter(parent, child, toInsert)) {
        				UtilDom.insertAfter(toInsert, child);
        				inserted = true;
        			}
        			child = child.getPreviousSibling();
        		} catch (RulesManagerException ex) {
        			logger.error(ex.getMessage(), ex);
        			return false;
        		}
        	} //;
    		if(!inserted){
    			// prova ad inserirlo BEFORE
    			child= parent.getFirstChild();
    			if(child != null){
    				try {
            			if (rulesManager.queryCanInsertBefore(parent, child, toInsert)) {
            				parent.insertBefore(toInsert, child);
            				inserted = true;
            			}
            		} catch (RulesManagerException ex) {
            			logger.error(ex.getMessage(), ex);
            			return false;
            		}
    			}
    		}
        	try {
        		// prova ad APPENDerlo
        		if (!inserted && rulesManager.queryCanAppend(parent, toInsert)){
        			parent.appendChild(toInsert);
        			inserted = true;
        		}
        	} catch (RulesManagerException ex) {
        		logger.error(ex.getMessage(), ex);
        		return false;
        	}
    	}
	return inserted;
    }

	public Node encloseTextInTag(Node node, int start, int end, String tagName) {
		return encloseTextInTag(node, start, end, tagName, null);
	}
	

	public Node encloseTextInTag(Node node, int start, int end, String tagName, String NSprefix) {

		Document doc = documentManager.getDocumentAsDom();
		String text = node.getNodeValue();
		Node tag = null;
		Node padre = node.getParentNode();

		if (UtilDom.isTextNode(node) && padre != null) {

			tag = encloseTextInTag(node.getNodeValue().substring(start, end), tagName, NSprefix);
			if (tag != null) {
				if (start == 0 && end == text.length()) {
					padre.replaceChild(tag, node);
				} else if (start == 0 && end < text.length()) {
					padre.insertBefore(tag, node);
					padre.replaceChild(doc.createTextNode(text.substring(end)), node);
				} else if (start > 0 && end < text.length()) {
					padre.insertBefore(doc.createTextNode(text.substring(0, start)), node);
					padre.insertBefore(tag, node);
					padre.replaceChild(doc.createTextNode(text.substring(end)), node);
				} else { // start > 0 && end = text.length
					padre.insertBefore(doc.createTextNode(text.substring(0, start)), node);
					padre.replaceChild(tag, node);
				}
			}
		}
		return tag;
	}

	public boolean queryCanReplaceWith(Node parent, Node child_node, Node new_Node) {
		Vector v = new Vector();
		v.add(new_Node);
		try {
			return (rulesManager.queryCanReplaceWith(parent, child_node, 1, v));
		} catch (RulesManagerException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	public Node encloseTextInTag(String text, String tagName) {
		return encloseTextInTag(text, tagName, null);
	}

	public Node encloseTextInTag(String text, String tagName, String NSprefix) {
		Document doc = documentManager.getDocumentAsDom();
		Element tag = null;
		if (NSprefix != null)
			tag = doc.createElementNS(UtilDom.getNameSpaceURIforElement(doc.getDocumentElement(), NSprefix), tagName);
		else
			tag = doc.createElement(tagName);

		if (text.length() > 0)
			tag.appendChild(doc.createTextNode(text));
		return tag;
	}

	protected JMenu createMenu(String name) {
		JMenu menu = new JMenu();
		menu.setName(name);
		menu.setEnabled(false);
		utilUI.applyI18n(menu);
		return menu;
	}

	protected String getMenuItemText(String next) {
		String i18nNext = i18n.getTextFor("dom." + next);
		if (next.equals(i18nNext))
			return next;
		else
			return i18nNext + " (" + next + ")";
	}

	/**
	 * fornisce un template del nodo minimale corrispondente all'elemento
	 * <code>elem_name</code>
	 * 
	 * @param elem_name nome dell'elemento
	 * @return il nodo di default per l'elemento passato
	 * 
	 * @see it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager#getNodeTemplate(java.lang.String)
	 */
	public Node getNodeTemplate(String elem_name) {
		return getNodeTemplate(documentManager.getDocumentAsDom(), elem_name);
	}

	public Node getNodeTemplate(Document doc, String elem_name) {
		Node newNode = null;
		String templateXml;
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

			// FIXME  ho aggiunto xmlns:cnr  --> verificare se da' noia su documenti non cnr
			
			// FIXME  ho aggiunto come namespace xmlns quello dei ddl; organizzarlo per toglierlo modificando i fogli di stile in modo che non richiedano il namespace settato (come il generico; no match su nir:)
			
			templateXml = "<utilrulesmanager xmlns:h=\"http://www.w3.org/HTML/1998/html4\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:dsp=\"http://www.normeinrete.it/nir/disposizioni/1.0\" xmlns:cnr=\"http://www.cnr.it/provvedimenti/2.1\"  xmlns=\"http://www.normeinrete.it/disegnilegge/1.0\" >"
					+ rulesManager.getDefaultContent(elem_name) + "</utilrulesmanager>";

			domFactory.setValidating(false); // deactivate validation
			domFactory.setNamespaceAware(true);
			Document parsed = (Document) domFactory.newDocumentBuilder().parse(new ByteArrayInputStream(templateXml.getBytes("UTF-8")));
			Node parsed_root = parsed.getDocumentElement().getFirstChild();
			newNode = doc.importNode(parsed_root, true);
			newNode = fillRecursiveRequiredAttributes(newNode);
			domFactory.setValidating(true); // reactivate validation
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		return newNode;
	}
	
	private Node fillRecursiveRequiredAttributes(Node node){
		try{
			
			if(node != null)
				rulesManager.fillRequiredAttributes(node);
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				fillRecursiveRequiredAttributes(list.item(i));
			}
		}
		catch(RulesManagerException ex){
			return node;
		}
		return node;
	}

	public boolean canInsertBefore(Node node, String name) {
		try {
			return rulesManager.queryInsertableBefore(node.getParentNode(), node).contains(name);
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean canInsertAfter(Node node, String name) {
		try {
			Collection coll = rulesManager.queryInsertableAfter(node.getParentNode(), node);
			return coll.contains(name);
		} catch (Exception ex) {
			return false;
		}
	}

	public Collection queryInsertableNodeInText(Node node, int start, int end) {

		// per il momento trascuro start ed end; dovrebbe andare bene lo stesso
		if (UtilDom.isTextNode(node) && node.getNodeValue() != null) {
			try {
				return rulesManager.queryAppendable(node.getParentNode());
			} catch (RulesManagerException e) {
				return null;
			}
		} else {
			try {
				return rulesManager.queryAppendable(node);
			} catch (RulesManagerException ex) {
				return null;
			}
		}
	}

	public boolean insertNodeInText(Node node, int start, int end, Node newNode, boolean replaceSelected) {

		Document doc = documentManager.getDocumentAsDom();
		String text, prima, dopo;

		if (UtilDom.isTextNode(node) && node.getNodeValue() != null) {
			text = node.getNodeValue();

			try {
				if (replaceSelected && rulesManager.queryTextContent(newNode))
					replaceSelected = true;
				else
					replaceSelected = false;
			} catch (RulesManagerException ex) {
				replaceSelected = false;
			}

			if (replaceSelected) {
				prima = text.substring(0, start);
				dopo = text.substring(end);
			} else {
				prima = text.substring(0, end);
				dopo = text.substring(end);
			}

			Node padre = node.getParentNode();

			if (start != end && replaceSelected)
				UtilDom.setTextNode(newNode, text.substring(start, end));

			if (start > 0 && end < text.length()) {
				padre.insertBefore(doc.createTextNode(prima), node);
				padre.insertBefore(newNode, node);
				padre.replaceChild(doc.createTextNode(dopo), node);
			} else if (start == 0 && end == text.length()) {
				padre.replaceChild(newNode, node);
			} else if (start == 0 && end < text.length()) {
				padre.insertBefore(newNode, node);
				padre.replaceChild(doc.createTextNode(dopo), node);
			} else { // start > 0 && end = text.length
				padre.insertBefore(doc.createTextNode(prima), node);
				padre.replaceChild(newNode, node);
			}
			return true;
		} else {
			try {
				if (rulesManager.queryCanAppend(node, newNode)) {
					node.appendChild(newNode);
					return true;
				}
				return false;
			} catch (RulesManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return false;
			}
		}
	}

	/**
	 * Permette di aggiungere l'elemento scelto nel documento come fratello seguente del
	 * nodo considerato
	 */
	class InsertBeforeAction extends AbstractAction {
		private Node node;

		private String newNodeName;

		public InsertBeforeAction(Node node, String newNodeName) {
			this.node = node;
			this.newNodeName = newNodeName;
		}

		public void actionPerformed(ActionEvent e) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				Node parent = node.getParentNode();
				Node child = null;
				Node toSelect = null;
				
				
				if("#PCDATA".equals(newNodeName)){
					child = node.getOwnerDocument().createTextNode(xsltMapper.getI18nNodeText(parent));
					if(!UtilDom.isTextNode(node) && !UtilDom.isTextNode(node.getPreviousSibling())){
						parent.insertBefore(child, node);
						toSelect = child;
					}else{
						toSelect = node.getPreviousSibling()!=null?node.getPreviousSibling():node;
					}
				}else{
					child = getNodeTemplate(newNodeName);
					parent.insertBefore(child, node);
					toSelect = child;
				}
				
				logger.debug("------------------ INSERT BEFORE --------------------");
				
		
				selectionManager.setActiveNode(this, toSelect);
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString(), ex);
			} catch (Exception ex) {
				documentManager.rollbackEdit(tr);
			}
		}

		public String toString() {
			return newNodeName;
		}
	}

	/**
	 * Permette di aggiungere l'elemento scelto nel documento come fratello seguente del
	 * nodo considerato
	 */
	class InsertAfterAction extends AbstractAction {
		private Node node;

		private String newNodeName;

		public InsertAfterAction(Node node, String newNodeName) {
			this.node = node;
			this.newNodeName = newNodeName;
		}

		public void actionPerformed(ActionEvent e) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				Node parent = node.getParentNode();
				Node child = null;
				Node toSelect = null;
				
				if("#PCDATA".equals(newNodeName)){
					child = node.getOwnerDocument().createTextNode(xsltMapper.getI18nNodeText(parent));
			
					if (node.getNextSibling() == null){
							if(!UtilDom.isTextNode(parent.getLastChild())){
								parent.appendChild(child);
								toSelect = child;
							}else{
								toSelect = parent.getLastChild();
							}
					}else{
							if(!UtilDom.isTextNode(node) && !UtilDom.isTextNode(node.getNextSibling())){
								parent.insertBefore(child, node.getNextSibling());
								toSelect = child;
							}else{
								toSelect = node.getNextSibling()!=null?node.getNextSibling():node;
							}
					}	
				}else{
					child = getNodeTemplate(newNodeName);
					if (node.getNextSibling() == null)
						parent.appendChild(child);
					else
						parent.insertBefore(child, node.getNextSibling());
					toSelect = child;
				}
				logger.debug("------------------ INSERT AFTER --------------------");
				
				selectionManager.setActiveNode(this, toSelect);
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString(), ex);
			} catch (Exception ex) {
				documentManager.rollbackEdit(tr);
			}
		}

		public String toString() {
			return newNodeName;
		}
	}

	/**
	 * Permette di inserire l'elemento scelto come figlio del nodo considerato.
	 */
	class InsertNodeAction extends AbstractAction {
		private Node node;

		private String newNodeName;

		public InsertNodeAction(Node node, String newNodeName) {
			this.node = node;
			this.newNodeName = newNodeName;
		}

		public void actionPerformed(ActionEvent e) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				Node child = null;
				if("#PCDATA".equals(newNodeName)){   // caso inserimento #PCDATA
					child=node.getOwnerDocument().createTextNode(xsltMapper.getI18nNodeText(node));
					// inserisce il nodo testo solo se non ce n'e' uno adiacente
					if(!UtilDom.isTextNode(node.getLastChild())){
						node.appendChild(child);
						selectionManager.setActiveNode(this, child);
					}else{
						selectionManager.setActiveNode(this, node.getLastChild());
					}
				}else{
					child =  getNodeTemplate(newNodeName);
					node.appendChild(child);
					selectionManager.setActiveNode(this, child);
				}
				
				logger.debug("------------------  APPEND  --------------------");
				
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString(), ex);
			} catch (Exception ex) {
				documentManager.rollbackEdit(tr);
			}
		}

		public String toString() {
			return newNodeName;
		}
	}

	/**
	 * Permette di aggiungere l'elemento scelto nel documento spezzando il nodo testo
	 * selezionato
	 */
	class InsertNodeInTextAction extends AbstractAction {
		private Node node;

		private String newNodeName;

		private int start, end;

		private boolean replaceSelected;

		public InsertNodeInTextAction(Node node, String newNodeName, int start, int end, boolean replaceSelected) {
			this.node = node;
			this.newNodeName = newNodeName;
			this.start = start;
			this.end = end;
			this.replaceSelected = replaceSelected;
		}

		public void actionPerformed(ActionEvent e) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				Node parent = node.getParentNode();
				Node child = "#PCDATA".equals(newNodeName) ? parent.getOwnerDocument().createTextNode(xsltMapper.getI18nNodeText(parent)) : getNodeTemplate(newNodeName);
				insertNodeInText(node, start, end, child, replaceSelected);
				selectionManager.setActiveNode(this, child);
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString(), ex);
			} catch (Exception ex) {
				documentManager.rollbackEdit(tr);
			}
		}

		public String toString() {
			return newNodeName;
		}
	}

	class EncloseSelectionAction extends AbstractAction {

		private Node node;

		private Node parent;

		private String next;

		private int start;

		private int end;

		public EncloseSelectionAction(Node node, String next, int start, int end) {
			this.parent = node.getParentNode();
			this.node = node;
			this.next = next;
			this.start = start;
			this.end = end;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			logger.debug("+++++++++++++++++ENCLOSE SELECTION ACTION START");
			logger.debug("Node: " + node);
			logger.debug("Parent: " + parent);
			String content = node.getNodeValue();
			logger.debug("Selezione: " + start + ", " + end);
			String firstString = content.trim().substring(0, start);
			String secondString = content.trim().substring(start, end);
			String thirdString = content.trim().substring(end);
			logger.debug("Prima stringa: " + firstString);
			logger.debug("Seconda stringa: " + secondString);
			logger.debug("Terza stringa: " + thirdString);
			Node prec = node.cloneNode(true);
			Node curr = node.cloneNode(true);
			Node succ = node.cloneNode(true);

			prec.setNodeValue(firstString);
			succ.setNodeValue(thirdString);
			curr.setNodeValue(secondString);
			logger.debug("Primo nodo testo: " + prec);
			logger.debug("Ultimo nodo testo: " + succ);
			Node toInsert = getNodeTemplate(next);
			toInsert.appendChild(curr);
			// toInsert.setNodeValue(secondString);
			logger.debug("Nodo da inserire: " + toInsert);
			parent.replaceChild(succ, node);
			parent.insertBefore(toInsert, succ);
			parent.insertBefore(prec, toInsert);
			logger.debug("+++++++++++++++++ENCLOSE SELECTION ACTION END");
		}

	}

	public boolean insertSubTreeInText(Node node, int pos, boolean destructure) {
		return false;
	}
}
