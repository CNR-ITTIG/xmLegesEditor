package it.cnr.ittig.xmleges.editor.blocks.form.metaedit;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.services.form.metaedit.MetaEditForm;
import it.cnr.ittig.xmleges.editor.services.util.metaedit.ModelloDA;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.metaedit.MetaEditForm</code>.</h1>
 * <h1>Permette la marcatura semantica secondo il modello DISPOSIZIONI-ARGOMENTI</h1>
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
 * @author <a href="mailto:turchi@ittig.cnr.it">Fabrizio Turchi </a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class MetaEditFormImpl implements MetaEditForm, Loggable, Serviceable, Initializable{

	Logger logger;

	Form form;
	
	UtilUI utilUI;
	
	I18n i18n;

	UtilMsg utilMsg;
	
	ModelloDA modelloDA;

	DtdRulesManager dtdRulesManager;
	
	UtilRulesManager utilRulesManager;

	DocumentManager documentManager;
	
	
	
	
	String nomeFile = "";
	
	boolean isOKclicked;
	
	String idPartizione;
	
	Node[] disposizioniOnDoc; 
	
	
	
	private JTextField textFieldPartition;
	private JTree treePartizioni, treeDisposizioni, treeArgomenti;
	private JComboBox comboKeywords;
	private JButton buttonPiu, buttonMeno, buttonAccept, buttonReject;
	private Document  keywords;
	
	static String DISPOSIZIONE     = "disposizione";
	static String ARGOMENTO		="argomento";
	static String TAG_KEYWORDS	="dsp:keywords";
	static String TAG_KEYWORD	="dsp:keyword";
	
	// IT
	//static String F_XML_KEYWORDS 	 = "DisposizioniKeywords.xml";
	
	// EN
	static String F_XML_KEYWORDS 	 = "DisposizioniKeywords_EN.xml";
	
	

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		modelloDA = (ModelloDA) serviceManager.lookup(ModelloDA.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		
		form.setMainComponent(this.getClass().getResourceAsStream("MetaEdit.jfrm"));
		form.setSize(900, 600);
		form.setName("editor.form.metaedit");
		form.setHelpKey("help.contents.form.annessi");
		
		textFieldPartition   = (JTextField)form.getComponentByName( "editor.form.metaedit.textfield.partition" );
		treePartizioni   = (JTree)form.getComponentByName( "editor.form.metaedit.tree.marking" );
		treeDisposizioni = (JTree)form.getComponentByName( "editor.form.metaedit.tree.classes" );
	    treeArgomenti	 = (JTree)form.getComponentByName( "editor.form.metaedit.tree.arguments" );
	    comboKeywords	 = (JComboBox)form.getComponentByName( "editor.form.metaedit.combo.keywords" );
	    buttonPiu		 = (JButton) form.getComponentByName("editor.form.metaedit.button.addkey");
	    buttonMeno		 = (JButton) form.getComponentByName("editor.form.metaedit.button.removekey");
	    buttonAccept	 = (JButton) form.getComponentByName("editor.form.metaedit.button.accept");
	    buttonReject	 = (JButton) form.getComponentByName("editor.form.metaedit.button.reject");

	    
	    // Buttons   +   -
	    
	    AbstractAction buttonPiuAction = new ButtonPiuAction();
	    AbstractAction buttonMenoAction = new ButtonMenoAction();
	    AbstractAction buttonAcceptAction = new ButtonAcceptAction();
	    AbstractAction buttonRejectAction = new ButtonRejectAction();
	    
	    
	    utilUI.applyI18n("editor.form.metaedit.button.piu", buttonPiuAction);
	    utilUI.applyI18n("editor.form.metaedit.button.meno", buttonMenoAction);
	    
	    buttonMeno.setAction(buttonMenoAction);
	    buttonPiu.setAction(buttonPiuAction);
	    
	    buttonAccept.setAction(buttonAcceptAction);
	    buttonReject.setAction(buttonRejectAction);
	    
	    //Usa un cell renderer per le keywords ?
        KeywordsListCellRenderer  renderer = new KeywordsListCellRenderer();
        renderer.setI18n(i18n);
       
        comboKeywords.setRenderer(renderer);
	  
	    
	    treeDisposizioni.addTreeSelectionListener(
	    		new TreeSelectionListener() { 
	    			public void valueChanged(TreeSelectionEvent event) {
	    				Object[] path = event.getPath().getPath();
	    				Element eNode = (Element) path[path.length - 1];
	    				if (eNode.getNodeName() == DISPOSIZIONE)
	    				  fillTreeArguments(eNode);
	    				  expandAll(treeArgomenti);
	    			}
	    		});  
	    
	    treePartizioni.addTreeSelectionListener(
	    		new TreeSelectionListener() { 
	    			public void valueChanged(TreeSelectionEvent event) {
	    				Object[] path = event.getPath().getPath();
	    				Element eNode = (Element) path[path.length - 1];
	    				if (modelloDA.isDisposizione(eNode.getNodeName())){
	    					expandTreeDisposizioni(eNode);
	    					fillTreeArguments(eNode);
	    					expandAll(treeArgomenti);
	    				}
	    			}
	    }); 
	    
	    
	    fillTreeDisposizioni();
	    fillComboKeywords();
	    
	}
	
	

	public void openForm() {	
		
		textFieldPartition.setText(idPartizione);
		collapseAll(treeDisposizioni);
		fillTreePartition();
		
		
		form.showDialog();
		
		if (form.isOk()) {			
			isOKclicked = true;

		} else
			isOKclicked = false;

	}
	
	

	public boolean isOKClicked() {
		return isOKclicked;
	}
	

	//////////////////////////////////////////////////////////////////////////////////////////
    // 								FABRIZIO	
	//////////////////////////////////////////////////////////////////////////////////////////
	

	
	private void fillTreePartition(){
		treePartizioni.setModel(null);
	    Node root=documentManager.getDocumentAsDom().createElement(idPartizione);
		
		if(disposizioniOnDoc != null && disposizioniOnDoc.length>0){
			for(int i=0;i<disposizioniOnDoc.length;i++){
				root.appendChild(disposizioniOnDoc[i]);
			}
		}
		
		treePartizioni.setModel(new DomPartitionTreeModel((root)));
		treePartizioni.setCellRenderer(new DomPartitionTreeCellRenderer());
				
		collapseAll(treeDisposizioni);
		emptyTreeArguments();
		
		if(root.hasChildNodes())
			treePartizioni.setSelectionRow(1);
	}
	
	
	
	private void expandTreeDisposizioni(Element node){
		collapseAll(treeDisposizioni);
		treeDisposizioni.setSelectionPath(new TreePath(modelloDA.getClassificationPathFor(node.getNodeName())));
	}
	
	
	
	public void collapseAll(JTree tree) {
	    int row = tree.getRowCount() - 1;
	    while (row > 0) { // fino al primo livello invece che alla root
	      tree.collapseRow(row);
	      row--;
	      }
	 }
	
	public void expandAll(JTree tree) {
	    int row = 0;
	    while (row <tree.getRowCount()) { 
	      tree.expandRow(row);
	      row++;
	      }
	 }
	
	

	
	private void fillTreeArguments(Element e) {
		
		treeArgomenti.setModel(null);
		
		// CASO POPOLAMENTO ALBERO ARGUMENTS DA SELEZIONE ALBERO DISPOSIZIONI   (AGGIUNTA)
		
		if (e.getNodeName() == DISPOSIZIONE) {	
			treeArgomenti.setModel(new DomArgumentTreeModel((getArgumentsTemplate(e))));
			treeArgomenti.setCellRenderer(new DomArgumentTreeCellRenderer());
		}  
		
		//CASO POPOLAMENTO ALBERO ARGUMENTS DA SELEZIONE ALBERO PARTIZIONI   (MODIFICA)
		
		else if(modelloDA.isDisposizione(e.getNodeName())){
			treeArgomenti.setModel(new DomArgumentTreeModel(e));
			treeArgomenti.setCellRenderer(new DomArgumentTreeCellRenderer());
		}
		else
			emptyTreeArguments();
	}

	
	private Node getArgumentsTemplate(Node node){
		
		// FIXME qui ci sarebbe da forzare il template che ha il tag dsp:pos
		Node newNode = utilRulesManager.getNodeTemplate(UtilDom.getAttributeValueAsString(node, "value"));	
		return newNode;
	}
	
	

	public void emptyTreeArguments() {
		treeArgomenti.setModel(null);
	}
	
	
	public void fillTreeDisposizioni() {
		treeDisposizioni.setModel(new DomDATreeModel((modelloDA.getModelloDA())));
		treeDisposizioni.setCellRenderer(new DomDATreeCellRenderer());
	}
		  

	public void fillComboKeywords() {
		keywords = UtilXml.readXML(this.getClass().getResourceAsStream(F_XML_KEYWORDS));
		Element rootKw = keywords.getDocumentElement();
		NodeList nlKw = rootKw.getChildNodes();
		for (int i=0; i<nlKw.getLength();i++) {		
			Node nKw = nlKw.item(i); 
			if (nKw.getNodeType() == nKw.ELEMENT_NODE) 
				comboKeywords.addItem(nKw);
		}
	}
	
	
	
	////////////////////////////////////////////////////////////////////////
	//
	//	 Visualizzazione del modello DA
    //
	////////////////////////////////////////////////////////////////////////

	class DomDATreeModel  implements TreeModel { 
		public DomDATreeModel(Document doc) { this.doc = doc; } 
		public Object getRoot() { return doc.getDocumentElement(); } 




		public int getChildCount(Object parent) { 			
			int childCount = 0;
			Node node = (Node) parent; 
			NodeList list = node.getChildNodes(); 
			for(int i=0; i<list.getLength();i++){
				if(!((Element)list.item(i)).getTagName().trim().equals(ARGOMENTO))
					childCount++;
			}		
			return childCount;
		} 

		//http://www.velocityreviews.com/forums/t131224-filtered-jtree.html

		public Object getChild(Object parent, int index) { 
			Node node = (Node) parent; 
			NodeList list = node.getChildNodes(); 
			int pos = 0;
			for(int i = 0, cnt = 0; i < getChildCount(node); i++) {
				if(!((Element)list.item(i)).getTagName().trim().equals(ARGOMENTO)){
					if (cnt++ == index) {
						pos = i;
						break;
					}
				}
			}
			return list.item(pos);
		}


		public int getIndexOfChild(Object parent, Object child) { 
			Node node = (Node) parent; 
			NodeList list = node.getChildNodes(); 
			for (int i = 0; i < list.getLength(); i++) 
				if (getChild(node, i) == child) 
					return i; 
			return -1; 
		} 

		public boolean isLeaf(Object node) { return getChildCount(node) == 0; } 
		public void valueForPathChanged(TreePath path, Object newValue) {} 
		public void addTreeModelListener(TreeModelListener l) {} 
		public void removeTreeModelListener(TreeModelListener l) {} 

		private Document doc;
		
	} 
	
	
	
	/*-----------------------------------------------------------------------------
	 * Questo TreeCellReneder imposta le modalità di visualizzazione dei nodi/elementi del 
	 * documento XML
	 * ----------------------------------------------------------------------------*/
	class DomDATreeCellRenderer extends DefaultTreeCellRenderer { 
		public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value,selected, expanded, leaf, row, hasFocus);
			Node node = (Node) value;
			if (node instanceof Element) {
				Element e = (Element) node;
				if (e.getTagName().trim().toLowerCase() == ARGOMENTO) { 
					// qui non dovrebbe arrivarci piu
					setText("");
					setEnabled(false); // non visualizzo i nodi che si riferiscono agli argomenti!!!
				}
				/*--- elemento con nome disposizione */		  
				else  {
					if (e.getTagName().trim().toLowerCase() == DISPOSIZIONE) {
						setText(i18n.getTextFor("dom."+e.getAttribute("value")));
						//setIcon(i18n.getIconFor("editor.form.metaedit.provision.icon"));
					}
					else
						setText(i18n.getTextFor("dom."+e.getNodeName()));	
				}
			}
			else {
				setText("");
				setEnabled(false); // non visualizzo i nodi che non sono elementi !!!
			}
			return this; 
		}
	}
	  
	////////////////////////////////////////////////////////////////////////
	//
	//	 Visualizzazione dell'albero partizione--disposizioni
    //
	////////////////////////////////////////////////////////////////////////
	
	class DomPartitionTreeModel  implements TreeModel { 
		public DomPartitionTreeModel(Node root) { this.root = root; } 
		public Object getRoot() { return root;} 


		public int getChildCount(Object parent) { 			
			int childCount = 0;
			Node node = (Node) parent; 
			NodeList list = node.getChildNodes(); 
			for(int i=0; i<list.getLength();i++){
				if(show(list.item(i)))
					childCount++;
			}		
			return childCount;
		} 


		public Object getChild(Object parent, int index) { 
			Node node = (Node) parent; 
			NodeList list = node.getChildNodes(); 
			int pos = 0;
			for(int i = 0, cnt = 0; i < getChildCount(node); i++) {
				// visualizza tutto meno che gli argomenti
				if(show(list.item(i))){
					if (cnt++ == index) {
						pos = i;
						break;
					}
				}
			}
			return list.item(pos);
		}
		
		private boolean show(Node node){
				
			String tagName = ((Element)node).getTagName().trim();
			return !(modelloDA.isArgomento(tagName) || tagName.startsWith("dsp:pos")) ;
			
		}


		public int getIndexOfChild(Object parent, Object child) { 
			Node node = (Node) parent; 
			NodeList list = node.getChildNodes(); 
			for (int i = 0; i < list.getLength(); i++) 
				if (getChild(node, i) == child) 
					return i; 
			return -1; 
		} 

		public boolean isLeaf(Object node) { return getChildCount(node) == 0; } 
		public void valueForPathChanged(TreePath path, Object newValue) {} 
		public void addTreeModelListener(TreeModelListener l) {} 
		public void removeTreeModelListener(TreeModelListener l) {} 

		private Node root;
		
	} 
	
	
	
	/*-----------------------------------------------------------------------------
	 * Questo TreeCellReneder imposta le modalità di visualizzazione dei nodi/elementi del 
	 * documento XML costituito da partizione come root e  disposizioni come figli
	 * ----------------------------------------------------------------------------*/
	class DomPartitionTreeCellRenderer extends DefaultTreeCellRenderer { 
		public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value,selected, expanded, leaf, row, hasFocus);
			Node node = (Node) value;
			if(i18n.getTextFor("dom."+node.getNodeName()).startsWith("dom")){
				setText(node.getNodeName());			
			}else{
				setText(i18n.getTextFor("dom."+node.getNodeName()));
				//setIcon(i18n.getIconFor("editor.form.metaedit.provision.icon"));
			}
			return this; 
		}
	}
	
	
	
	
	////////////////////////////////////////////////////////////////////////
	//
	//	 Visualizzazione dell'albero ARGOMENTI
    //
	////////////////////////////////////////////////////////////////////////
	
	class DomArgumentTreeModel  implements TreeModel { 
		public DomArgumentTreeModel(Node root) { this.root = root; } 
		public Object getRoot() { return root;} 
		
 		
		public int getChildCount(Object parent) { 			
			int childCount = 0;
			Node node = (Node) parent; 
			NodeList list = node.getChildNodes(); 
			for(int i=0; i<list.getLength();i++){
				if(!((Element)list.item(i)).getTagName().trim().startsWith("dsp:pos"))
					childCount++;
			}		
			return childCount;
		} 

		
		public Object getChild(Object parent, int index) { 
		Node node = (Node) parent; 
		NodeList list = node.getChildNodes(); 
		int pos = 0;
		int err = 0;
		for(int i = 0; i < list.getLength(); i++) {
			// visualizza tutto meno che dsp:pos
			if(((Element)list.item(i)).getTagName().trim().startsWith("dsp:pos")){
				err++;
			}
			if(i==index+err){
				pos=i;
				break;
			}		
		}
			return list.item(pos);
		}

		

		public int getIndexOfChild(Object parent, Object child) { 
			Node node = (Node) parent; 
			NodeList list = node.getChildNodes(); 
			for (int i = 0; i < list.getLength(); i++) 
				if (getChild(node, i) == child) 
					return i; 
			return -1; 
		} 

		public boolean isLeaf(Object node) { return getChildCount(node) == 0; } 
		public void valueForPathChanged(TreePath path, Object newValue) {} 
		public void addTreeModelListener(TreeModelListener l) {} 
		public void removeTreeModelListener(TreeModelListener l) {} 
		
		Node root;
		
	} 
	
	
	
	
	class DomArgumentTreeCellRenderer extends DefaultTreeCellRenderer { 
		public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value,selected, expanded, leaf, row, hasFocus);
			Node node = (Node) value;
			
			if (modelloDA.isDisposizione(node.getNodeName().trim().toLowerCase())) { 	
				setText(i18n.getTextFor("editor.form.metaedit.arguments.for.text")+" "+(i18n.getTextFor("dom."+node.getNodeName()).startsWith("dom")?node.getNodeName():i18n.getTextFor("dom."+node.getNodeName())));
			} 
			else if (node.getNodeName().trim().toLowerCase() == TAG_KEYWORD) {
				setText(UtilDom.getAttributeValueAsString(node, "valore"));
				//setIcon(i18n.getIconFor("editor.form.metaedit.keyword.icon"));
			}else if (node.getNodeName().trim().toLowerCase() == TAG_KEYWORDS) {
				setText(i18n.getTextFor("dom."+node.getNodeName()).startsWith("dom")?node.getNodeName():i18n.getTextFor("dom."+node.getNodeName()));
			}
			else{
				setText(i18n.getTextFor("dom."+node.getNodeName()).startsWith("dom")?node.getNodeName():i18n.getTextFor("dom."+node.getNodeName()));
				//setIcon(i18n.getIconFor("editor.form.metaedit.argument.icon"));
			}

			return this; 
		}
	}
	
	
	protected class ButtonPiuAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			
			Node selectedNode =  (Node) treeArgomenti.getLastSelectedPathComponent();
			
    		if (selectedNode == null) return;

    		if (selectedNode.getParentNode() == null) return;
    		
    		Node parentNode = selectedNode.getParentNode();
    		String kw = UtilDom.getAttributeValueAsString((Node)comboKeywords.getSelectedItem(), "Name");
    		Node newKeyword = documentManager.getDocumentAsDom().createElement(TAG_KEYWORD);
    		UtilDom.setAttributeValue(newKeyword, "valore", kw);
    		
    		if(selectedNode.getNodeName().equals(TAG_KEYWORDS) || parentNode.getNodeName().equals(TAG_KEYWORDS)) {  
    		// sono sul nodo keywords
    		  if(selectedNode.getNodeName().equals(TAG_KEYWORDS)) {
    			if (CheckKwDuplicate(selectedNode, kw)) {
    				utilMsg.msgInfo(i18n.getTextFor("editor.form.metaedit.msg.error.key.text")+" \"" + kw + "\" "+i18n.getTextFor("editor.form.metaedit.msg.error.alreadypresent.text"), "editor.form.metaedit.msg.error.duplicated.text");
    			  return;
    			}
    			else
    			  selectedNode.appendChild(newKeyword);
    		  }
    		  // altrimenti sono già su un nodo keyword valore 
    		  else if(parentNode.getNodeName().equals(TAG_KEYWORDS)) {
    			if (CheckKwDuplicate(parentNode, kw)) {
    				utilMsg.msgInfo(i18n.getTextFor("editor.form.metaedit.msg.error.key.text")+" \"" + kw + "\" "+i18n.getTextFor("editor.form.metaedit.msg.error.alreadypresent.text"), "editor.form.metaedit.msg.error.duplicated.text");
   				  return;
    			}
    			else
    			  parentNode.appendChild(newKeyword);
    		  }
    		}
    		else {
    		  Node firstChild = selectedNode.getFirstChild();
    		  if (firstChild!=null && firstChild.getNodeName().equals(TAG_KEYWORDS)) {
    			if (CheckKwDuplicate(firstChild, kw)) {
    			  utilMsg.msgInfo(i18n.getTextFor("editor.form.metaedit.msg.error.key.text")+" \"" + kw + "\" "+i18n.getTextFor("editor.form.metaedit.msg.error.alreadypresent.text"), "editor.form.metaedit.msg.error.duplicated.text");
    			  return;
    			}
      			else
      			  firstChild.appendChild(newKeyword);
    		  }else{
    			  Node newKeywords = documentManager.getDocumentAsDom().createElement(TAG_KEYWORDS);
    			  newKeywords.appendChild(newKeyword);
    			  selectedNode.appendChild(newKeywords);
    		  }
    		}
    		treeArgomenti.updateUI(); //reload per l'aggiornamento della visualizzazione
 			expandAll(treeArgomenti);
		}
		
		private boolean CheckKwDuplicate(Node Kws, String value) {
			NodeList kwChildren = Kws.getChildNodes();
			boolean retValue = false;
			for (int i=0; i<kwChildren.getLength(); i++) {
				if (UtilDom.getAttributeValueAsString(kwChildren.item(i), "valore").equals(value)) {
				  retValue = true;
				  break;
				}
			}
			return retValue;
			
		}
	}  
	
	
	
	protected class ButtonMenoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Node selectedNode =  (Node) treeArgomenti.getLastSelectedPathComponent();

    		if (selectedNode == null) return;
    		
    		if(selectedNode.getNodeName().equals(TAG_KEYWORD)) {
    		  Node parent = selectedNode.getParentNode();
    		  parent.removeChild(selectedNode);
    		  if(parent.getChildNodes().getLength()==0)
    			  parent.getParentNode().removeChild(parent);
    		  treeArgomenti.updateUI();
    		}
		}
	}  
	
	
	
	
	protected class ButtonAcceptAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			System.err.println("Button ACCEPT pressed");
			TreePath selectionPath = treeDisposizioni.getSelectionPath(); 
			Node selectedNode = (Node) selectionPath.getLastPathComponent();
			Node rootDisp = documentManager.getDocumentAsDom().createElement(UtilDom.getAttributeValueAsString(selectedNode, "value"));
						
			Node rootArg  = (Node) treeArgomenti.getModel().getRoot();
			NodeList children = rootArg.getChildNodes();
			
		
			for(int i=0; i<children.getLength(); i++) {		
				// setta il dsp:pos
				if(children.item(i).getNodeName().startsWith("dsp:pos"))		
						UtilDom.setAttributeValue(children.item(i), "xlink:href", idPartizione);
				
				rootDisp.appendChild(children.item(i).cloneNode(true));
			}
						
			
			
			
			/*------------------------------------------------------------------------------------------------- 
			 * se si tratta di una marcatura esistente,  la sostituisco al corrispondente nodo di treePartizioni
			 * altrimenti aggiungo un nodo figlio alla root di treePartizioni 
			 *------------------------------------------------------------------------------------------------*/
			Node rootPartizioni = (Node) treePartizioni.getModel().getRoot();
			children = rootPartizioni.getChildNodes();
			String marcaturaCorrente = UtilDom.getAttributeValueAsString(selectedNode, "value");
			Node oldChild = null;
			for (int i=0; i<children.getLength(); i++) {
				if (children.item(i).getNodeName().equalsIgnoreCase(marcaturaCorrente)) {
					oldChild = children.item(i);
					break;
				}
			} 
			if (oldChild != null) {
				((Node)treePartizioni.getModel().getRoot()).replaceChild(rootDisp, oldChild);
			}
			else 
				((Node)treePartizioni.getModel().getRoot()).appendChild(rootDisp);
			
			treePartizioni.updateUI(); // necessario?
			System.out.println(UtilDom.getAttributeValueAsString(selectedNode, "value"));
			
		}
		
	}  
	
	
	
	protected class ButtonRejectAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			
			System.err.println("Button REJECT pressed");
			TreePath selectionPath = treePartizioni.getSelectionPath(); 
			Node selectedNode = (Node) selectionPath.getLastPathComponent();
			if (selectedNode.getParentNode() == null)
				utilMsg.msgInfo("Selezionare la marcatura semantica da eliminare", "Marcatura semantica non selezionata");
			else {
				Node root = (Node) treePartizioni.getModel().getRoot();
				root.removeChild(selectedNode);
				treePartizioni.updateUI();
			}			
	}
		
	}  
	
	//////////////////////////////////////////////////////////////////////////////////////////
    // 								
	//////////////////////////////////////////////////////////////////////////////////////////
	
	

	
	public void setIdPartizione(String idPartizione) {
		this.idPartizione = idPartizione;
		
	}
	
	
	public Node[] getDisposizioni() {
		Node rootDisp = (Node) treePartizioni.getModel().getRoot();
		NodeList children = rootDisp.getChildNodes();
		if (children.getLength() > 0) {
			Vector disposizioni = new Vector();
			for (int i=0; i<children.getLength(); i++) {
				disposizioni.add(children.item(i));
			}
			Node[] disposizioniOnPartition = new Node[disposizioni.size()];
			disposizioni.copyInto(disposizioniOnPartition);
			return disposizioniOnPartition;
		}
		else
			return null;
	}


	public void setDisposizioni(Node[] disposizioni) {
		this.disposizioniOnDoc = disposizioni;		
	}

	
	
}
