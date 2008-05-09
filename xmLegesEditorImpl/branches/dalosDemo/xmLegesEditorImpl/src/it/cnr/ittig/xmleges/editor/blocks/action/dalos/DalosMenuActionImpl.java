package it.cnr.ittig.xmleges.editor.blocks.action.dalos;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.help.Help;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.action.dalos.DalosMenuAction;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetListPane;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.allineamento.AllineamentoAction</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>action-manager</li>
 * <li>event-manager</li>
 * <li>selection-manager</li>
 * <li>editor-dom-allineamento</li>
 * <li>editor-dom-tabelle</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>editor.allineamento.oriz.sx</li>
 * <li>editor.allineamento.oriz.dx</li>
 * <li>editor.allineamento.oriz.centro</li>
 * <li>editor.allineamento.oriz.giustificato</li>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class DalosMenuActionImpl implements DalosMenuAction, Loggable, Serviceable, Initializable, EventManagerListener {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;
	
	Node activeNode = null;
	
	Frame frame;
	
	Bars bars;
	
	PreferenceManager preferenceManager;
	
	DocumentManager documentManager;
	
	SelectionManager selectionManager;
	
	KbManager kbManager;
	
	UtilDalos utilDalos;
	
	UtilMsg utilMsg;
	
	Help help;
	
	boolean isDalosShown = false;
	
	SynsetListPane synsetListPane;
	
	AbstractAction showViewAction = new ShowViewAction();
	
	AbstractAction dalosHelpAction = new DalosHelpAction();
	
	AbstractAction checkForwardAction = new CheckAction(true);
	
	AbstractAction checkBackWardAction = new CheckAction(false);

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
		kbManager = (KbManager) serviceManager.lookup(KbManager.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		help = (Help) serviceManager.lookup(Help.class);
		utilDalos = (UtilDalos) serviceManager.lookup(UtilDalos.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		synsetListPane = (SynsetListPane) serviceManager.lookup(SynsetListPane.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		
		actionManager.registerAction("editor.dalos.showview", showViewAction);
		
		for(int i=0;i<utilDalos.getDalosLang().length;i++){
			actionManager.registerAction("editor.dalos.switchlang."+utilDalos.getDalosLang()[i].toLowerCase(), new SwitchLangAction(utilDalos.getDalosLang()[i]));
		}
		
		actionManager.registerAction("editor.dalos.help", dalosHelpAction);
		
		actionManager.registerAction("editor.dalos.check.forward", checkForwardAction);
		actionManager.registerAction("editor.dalos.check.backward", checkBackWardAction);
		checkForwardAction.setEnabled(false);
		checkBackWardAction.setEnabled(false);
		
		
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		
	}

	public void manageEvent(EventObject event) {
		checkForwardAction.setEnabled(!documentManager.isEmpty() && selectionManager.getActiveNode()!=null);
		checkBackWardAction.setEnabled(!documentManager.isEmpty() && selectionManager.getActiveNode()!=null);
	}

	

	public class ShowViewAction extends AbstractAction {
		
		public ShowViewAction() {		
		}

		public boolean canDoAction(Node n) {
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			
			// panels properties
			Properties panelsProps = preferenceManager.getPreferenceAsProperties(frame.getClass().getName());   // le preference di Frame
			Properties oldPanelsProps = (Properties)panelsProps.clone();   // le oldProps sono quelle dell'avvio lette dal file delle pref
			boolean defaultValue = false;
			
			Enumeration en=panelsProps.keys();
							
			while(en.hasMoreElements()){
				String el = en.nextElement().toString();
				if(el.indexOf("show")!=-1){
					if(el.indexOf("dalos")!=-1 || el.indexOf("document")!=-1){   // mostrare tutti i pannelli dalos e quello del documento
						panelsProps.setProperty(el,Boolean.toString(true));
					}
					else
						panelsProps.setProperty(el,Boolean.toString(false));		
				}
			}
			
			
			if(panelsProps.size()==0){      // al primo avvio non esiste il file delle preference
				defaultValue = true;
				panelsProps.setProperty("show.editor.panes.documento", "true");
				panelsProps.setProperty("show.editor.panes.dalos.synsettree", "true");
				panelsProps.setProperty("show.editor.panes.dalos.synsetlist", "true");
				panelsProps.setProperty("show.editor.panes.dalos.synsetdetails", "true");
				panelsProps.setProperty("show.editor.panes.dalos.interlingualrelation", "true");
				panelsProps.setProperty("show.editor.panes.dalos.linguisticrelation", "true");
				panelsProps.setProperty("show.editor.panes.dalos.semanticrelation", "true");
				panelsProps.setProperty("show.editor.panes.dalos.source", "true");
			}
			//////////////////////////////////////
			//
			// BARS properties    ???
			//
			
			Properties barsProps = preferenceManager.getPreferenceAsProperties(bars.getClass().getName());   // le preference di Frame
			Properties oldbarsProps = (Properties)barsProps.clone();
			
			en=barsProps.keys();
							
			while(en.hasMoreElements()){
				String el = en.nextElement().toString();
				if(el.indexOf("check.view")!=-1){
					if(el.indexOf("dalos")!=-1 || el.indexOf("document")!=-1){   // mostrare tutti i pannelli dalos e quello del documento
						barsProps.setProperty(el,Boolean.toString(true));
					}
					else
						barsProps.setProperty(el,Boolean.toString(false));		
				}
			}
			
			
			if(barsProps.size()==0){      // al primo avvio non esiste il file delle preference; va gestito a parte
				defaultValue = true;
				barsProps.setProperty("check.view.pane.editor.panes.documento", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.synsettree", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.synsetlist", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.synsetdetails", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.interlingualrelation", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.linguisticrelation", "true");
				barsProps.setProperty("check.view.pane.editor.panes.dalos.semanticrelation", "true");
				barsProps.setProperty("check.view.pane.editor.panes.panes.dalos.source", "true");
			}
			//
			//
			/////////////////////////////////////////////////////////
			
			
			
			// RELOAD
			if(!isDalosShown){
			   frame.reloadPerspective(panelsProps,false);
			   isDalosShown = true;
			}else{
				frame.reloadPerspective(oldPanelsProps,defaultValue);
				isDalosShown = false;
			}
				
			System.err.println("--------SHOW DALOS VIEW----------");
		}
	}
	
	
	
	public class SwitchLangAction extends AbstractAction {
		String lang;

		public SwitchLangAction(String lang) {
			this.lang = lang;
		}

		public void actionPerformed(ActionEvent e) {
			
			if(utilMsg.msgWarning("editor.dalos.switchlang.msg")){
				if(kbManager.isLangSupported(lang))
					eventManager.fireEvent(new LangChangedEvent(this, true, lang));
				else 
				   utilMsg.msgError("editor.dalos.switchlang.unsupportedlang");
			}
		}
	}
	
	
	
	protected class DalosHelpAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			showDalosHelp();
		}
	}

	private void showDalosHelp() {
		logger.debug("Call Dalos Help");
		help.helpOn("editor.dalos.help.browser");
		//help.helpOnForm("editor.dalos.help.file", null, null);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//   
	//						CHECK LEXICON
	//
	///////////////////////////////////////////////////////////////////////////////
	
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
	
	
	private class DalosWord{
		String word;
		Node node;
		int startOffset;
		
		private DalosWord(String word, int startOffset){
			this.word = word;
			this.startOffset = startOffset;
		}
		
		private int getStartOffset(){
			return this.startOffset;
		}
		
		private String getWord(){
			return this.word;
		}
		
	}
	
	
	private class DalosWordNode{
		String word;
		Node node;
		int startOffset;
		
		private DalosWordNode(Node node, String word, int startOffset){
			this.node=node;
			this.word = word;
			this.startOffset = startOffset;
		}
		
		private Node getNode(){
			return this.node;
		}
		
		private int getStartOffset(){
			return this.startOffset;
		}
		
		private String getWord(){
			return this.word;
		}
		
	}
	
	
	public DalosWord checkLexiconFW(String text, int from){
		if(from<0)
			from=0;
//		if(from>0){   // se parto da meta' parola torna indietro all'inizio della parola
//			from=text.substring(0,from).lastIndexOf(" ");
//		}
		int offset = from;
		text = text.substring(from);
		String[] tokens = text.split(" ");
		Collection results = null;
		for(int i=0; i<tokens.length; i++){
			results=kbManager.search(tokens[i],synsetListPane.getSearchType(),utilDalos.getGlobalLang());
			if(tokens[i].trim().length()>0 && results!=null && ! results.isEmpty()){
				synsetListPane.setSynsetListData(results);
				synsetListPane.setSearchField(tokens[i]);
				return new DalosWord(tokens[i],offset);//from+text.indexOf(tokens[i]));
			}
			offset= offset+tokens[i].length()+1;  // 1 = token separator length
		}
		return null;
	}
	
	
	public DalosWordNode checkLexiconFW(Node node, int from) {
		
		if (node != null) {
			Vector childNodes = new Vector();
			childNodes = getTextNodes(node, childNodes);
			for (int i = 0; i < childNodes.size(); i++) {
				DalosWord dalosWord = checkLexiconFW(((Node) childNodes.get(i)).getNodeValue(),from);
				if (dalosWord!=null) 
					return new DalosWordNode((Node) childNodes.get(i),dalosWord.getWord(),dalosWord.getStartOffset()); 
			}
			return (null);
		}
		return null;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//   
	//						BACKWARD
	//
	///////////////////////////////////////////////////////////////////////////////
	
	
	public DalosWordNode checkLexiconBW(Node node, int from) {
		
		if (node != null) {
				DalosWord dalosWord = checkLexiconBW(node.getNodeValue(),from);
				if (dalosWord!=null) 
					return new DalosWordNode(node,dalosWord.getWord(),dalosWord.getStartOffset()); 
			
			return (null);
		}
		return null;
	}
	
	
	public DalosWord checkLexiconBW(String text, int from){
		if(from<0)
			from=0;

		int offset = from;
		text = text.substring(0,from);
		
		
		if(from!=text.length() || text.lastIndexOf(" ")==from-1)    // parti dal primo spazio non finale
			offset = text.lastIndexOf(" ");
		
		String[] tokens = text.split(" ");
		Collection results = null;
		for(int i=tokens.length-1; i>=0; i--){
			//System.err.println("analyzing token "+i);
			//System.err.println("offset BW ="+offset);
			results=kbManager.search(tokens[i],synsetListPane.getSearchType(),utilDalos.getGlobalLang());
			if(tokens[i].trim().length()>0 && results!=null && ! results.isEmpty()){
				//System.err.println("found word BW "+tokens[i]);
				synsetListPane.setSynsetListData(results);
				synsetListPane.setSearchField(tokens[i]);
				return new DalosWord(tokens[i],offset-tokens[i].length());
			}
			offset= offset-tokens[i].length()-1;  // 1 = token separator length
		}
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//   
	//						
	//
	///////////////////////////////////////////////////////////////////////////////
	
	
	public class CheckAction extends AbstractAction {
		boolean forward;



		public CheckAction(boolean forward) {
			this.forward = forward;
		}
		
		
		
		public void actionPerformed(ActionEvent e) {

			if(this.forward)
				checkForward();
			else
				checkBackWard();	
			
		}
		

		private void checkForward(){
			// NON FUNZIONA IL CHECK DI TESTO SELEZIONATO
			//int start = selectionManager.getTextSelectionStart();
			int end = selectionManager.getTextSelectionEnd();
			activeNode = selectionManager.getActiveNode();
			DalosWordNode dwn = checkLexiconFW(activeNode,end);
			if(dwn!=null){
				selectionManager.setActiveNode(this,dwn.getNode());
				selectionManager.setSelectedText(this,dwn.getNode(),dwn.getStartOffset(), dwn.getStartOffset()+dwn.getWord().length());
			}else{
				//System.err.println("end of node reached: WHERE DO I GO FROM HERE ????");
				Vector childNodes = new Vector();
				Vector v = getTextNodes(documentManager.getRootElement(), childNodes);
				int i = v.indexOf(activeNode);
				if(i<v.size()){
					Node nextNode = (Node) v.get(i+1);
					if(nextNode!=null){
						selectionManager.setActiveNode(this,nextNode);
						selectionManager.setSelectedText(this, nextNode, 0, 0);
						checkForward();
					}	
				}
			}
		}
		
		
		private void checkBackWard(){
			// NON FUNZIONA IL CHECK DI TESTO SELEZIONATO
			int start = selectionManager.getTextSelectionStart();
			//int end = selectionManager.getTextSelectionEnd();
			activeNode = selectionManager.getActiveNode();
			DalosWordNode dwn = checkLexiconBW(activeNode,start);
			if(dwn!=null){
				selectionManager.setActiveNode(this,dwn.getNode());
				selectionManager.setSelectedText(this,dwn.getNode(),dwn.getStartOffset(), dwn.getStartOffset()+dwn.getWord().length());
			}else{
				//System.err.println("end of node reached: WHERE DO I GO FROM HERE ????");
				Vector childNodes = new Vector();
				Vector v = getTextNodes(documentManager.getRootElement(), childNodes);
				int i = v.indexOf(activeNode);
				if(i>0){
					Node nextNode = (Node) v.get(i-1);
					if(nextNode!=null){
						selectionManager.setActiveNode(this,nextNode);
						int end = nextNode.getNodeValue().length()-1;
						selectionManager.setSelectedText(this, nextNode, end, end);
						checkBackWard();
					}
				}
			}
		}

	}

		
	}
	


