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
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.services.form.metaedit.MetaEditForm;
import it.cnr.ittig.xmleges.editor.services.util.metaedit.ModelloDA;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
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
	private DefaultMutableTreeNode dispRoot, argChild;	 
	  
	static String DISPOSIZIONE     = "disposizione";
	static String ARGOMENTO		="argomento";
	static String F_XML_KEYWORDS 	 = "DisposizioniKeywords.xml";
	
	

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
	  
	    
	    treeDisposizioni.addTreeSelectionListener(
	    		new TreeSelectionListener() { 
	    			public void valueChanged(TreeSelectionEvent event) {
	    				Object[] path = event.getPath().getPath();
	    				Element eNode = (Element) path[path.length - 1];
	    				if (eNode.getNodeName() == DISPOSIZIONE)
	    					fillTreeArguments(eNode);
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
	
	
	private void fillTreeArguments(Element e) {
		
		// CASO POPOLAMENTO ALBERO ARGUMENTS DA SELEZIONE ALBERO DISPOSIZIONI   (AGGIUNTA)
		
		if (e.getNodeName() == DISPOSIZIONE) {		
			
			NodeList eList = e.getChildNodes();
			dispRoot = new DefaultMutableTreeNode("Argomenti" + " " + i18n.getTextFor("dom."+e.getAttribute("value")));
			for(int i=0; i<eList.getLength(); i++) {
				Node n = eList.item(i);
				if (n.getNodeType() == n.ELEMENT_NODE) {
					Element eDisp = (Element) eList.item(i);
					argChild = new DefaultMutableTreeNode(i18n.getTextFor("dom."+eDisp.getAttribute("value"))); 
					dispRoot.add(argChild);
				}
			}
			DefaultTreeModel treeModel = new DefaultTreeModel(dispRoot);
			treeArgomenti.setModel(treeModel);
		}  
		
		//CASO POPOLAMENTO ALBERO ARGUMENTS DA SELEZIONE ALBERO PARTIZIONI   (MODIFICA)
		
		else if(modelloDA.isDisposizione(e.getNodeName())){
			
			dispRoot = new DefaultMutableTreeNode("Argomenti" + " " + i18n.getTextFor("dom."+e.getNodeName()));
			fillRecursiveTreeArguments(e,dispRoot);

			DefaultTreeModel treeModel = new DefaultTreeModel(dispRoot);
			treeArgomenti.setModel(treeModel);
		
		}
		else
			emptyTreeArguments();
	}

	
	// Problema: uniformare visualizzazione Argomenti in AGGIUNTA E IN MODIFICA
	// SI PUO RIFARE MEGLIO: qui ci arrivo con un dom dal documento; devo saltare il nodo keywords e prendere il valore dall'attributo di keyword
	// si potrebbe usare un DomTreeModel anche per gli argomenti ?
	private void fillRecursiveTreeArguments(Element e, DefaultMutableTreeNode root){
		DefaultMutableTreeNode child;
		NodeList eList = e.getChildNodes();
		for(int i=0; i<eList.getLength(); i++) {
			if(!eList.item(i).getNodeName().equals("dsp:keywords")){
				if(!eList.item(i).getNodeName().equals("dsp:keyword"))
					child = new DefaultMutableTreeNode(i18n.getTextFor("dom."+eList.item(i).getNodeName()));
				else
					child = new DefaultMutableTreeNode(UtilDom.getAttributeValueAsString(eList.item(i),"valore"));
				root.add(child);
			}else 
				child = root;
			fillRecursiveTreeArguments((Element)eList.item(i), child);
		}
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
			if (nKw.getNodeType() == nKw.ELEMENT_NODE) {
				Element eKw = (Element) nKw;
				comboKeywords.addItem(eKw.getAttribute("Name"));
			}
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
						setForeground(Color.darkGray);
						setText(i18n.getTextFor("dom."+e.getAttribute("value")));
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
				if(!modelloDA.isArgomento(((Element)list.item(i)).getTagName().trim()))
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
				if(!modelloDA.isArgomento(((Element)list.item(i)).getTagName().trim())){
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
			setText(i18n.getTextFor("dom."+node.getNodeName()).startsWith("dom")?node.getNodeName():i18n.getTextFor("dom."+node.getNodeName()));
			return this; 
		}
	}
	
	
	  
	protected class ButtonPiuAction extends AbstractAction {
		
	
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeArgomenti.getLastSelectedPathComponent();

    		if (selectedNode == null) return;

    		if (selectedNode.getParent() == null) return;

    		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
    		DefaultMutableTreeNode rootArgs = (DefaultMutableTreeNode) treeArgomenti.getModel().getRoot();

    		String kw = (String) comboKeywords.getSelectedItem();
    		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(kw);
    		DefaultTreeModel model = (DefaultTreeModel) treeArgomenti.getModel(); 
    		if (selectedNode.getParent() == rootArgs) {
    			int selectedIndex = selectedNode.getIndex(selectedNode);
    			model.insertNodeInto(newNode, selectedNode, selectedIndex + 1);
    		}
    		else { 
    			/*---------------------------------------------------------------*
    			 *  si tratta di una keyword e si aggiunge al nodo parent, ossia  *
    			 *  all'argomento padre											 * 
    			 *----------------------------------------------------------------*/			
    			int selectedIndex = parent.getIndex(parent);
    			model.insertNodeInto(newNode, parent, selectedIndex + 1);
    		}
		}
	}  
	
	
	
	protected class ButtonMenoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeArgomenti.getLastSelectedPathComponent();

    		if (selectedNode == null) return;

    		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
    		DefaultMutableTreeNode rootArgs = (DefaultMutableTreeNode) treeArgomenti.getModel().getRoot();

    		if (parent == null) return;

    		if (parent.getParent() == rootArgs && selectedNode.isLeaf()) {
    			int selectedIndex = selectedNode.getIndex(selectedNode);
    			DefaultTreeModel model = (DefaultTreeModel) treeArgomenti.getModel(); 
    			model.removeNodeFromParent(selectedNode);
    		}
		}
	}  
	
	
	
	
	protected class ButtonAcceptAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			System.err.println("Button ACCEPT pressed");
			
			
			// qui:  leggere il contenuto dell'albero argomenti; creare un nodo dom di interscambio del tipo: 
			
			
			
//			<dsp:termine_dominio implicita="no">
//			  <dsp:definiendum>
//			    <dsp:keywords>
//			      <dsp:keyword valore="dato personale">
//			      </dsp:keyword>
//			    </dsp:keywords>
//			  </dsp:definiendum>
//			  <dsp:definiens>
//			    <dsp:keywords>
//			      <dsp:keyword valore="informazione">
//			      </dsp:keyword>
//			      <dsp:keyword valore="associazione">
//			      </dsp:keyword>
//			    </dsp:keywords>
//			  </dsp:definiens>
//			</dsp:termine_dominio>
			
			
			
			// avente come root il nome della disposizione e come figli gli argomenti messi sotto keywords-keyword
			
			// alternativamente: metter un domTreeModel anche sugli argomenti in modo che il nodo dom fatto in quel modo  e' gia' pronto 
			
			// aggiungere (o sostituire se ce n'e' gia' uno dello stesso tipo) nell'array Node[] disposizioniOnDoc
			
			// chiamare fillTreePartition()
			
		}
		
	}  
	
	
	
	protected class ButtonRejectAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			System.err.println("Button REJECT pressed");
		}
		
	}  
	
	//////////////////////////////////////////////////////////////////////////////////////////
    // 								
	//////////////////////////////////////////////////////////////////////////////////////////
	
	

	
	public void setIdPartizione(String idPartizione) {
		this.idPartizione = idPartizione;
		
	}
	
	
	public Node[] getDisposizioni() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setDisposizioni(Node[] disposizioni) {
		this.disposizioniOnDoc = disposizioni;		
	}

	
	
}
