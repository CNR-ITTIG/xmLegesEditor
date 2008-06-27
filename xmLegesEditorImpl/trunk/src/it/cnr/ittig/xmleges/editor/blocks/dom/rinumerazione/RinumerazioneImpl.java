package it.cnr.ittig.xmleges.editor.blocks.dom.rinumerazione;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.document.DocumentBeforeInitUndoAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;
import it.cnr.ittig.xmleges.core.services.panes.problems.ProblemsPane;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione</code>.
 * </h1>
 * <h1>Descrizione</h1>
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class RinumerazioneImpl implements Rinumerazione, DocumentBeforeInitUndoAction, Loggable, Serviceable, Startable, Initializable {
	Logger logger;

	AggiornaIdFrozenLaw aggiornaIdFrozenLaw;

	AggiornaNumerazioneAndLink aggiornaNumerazioneAndLink;

	DocumentManager documentManager;

	RulesManager rulesManager;

	PreferenceManager preferenceManager;
	
	ProblemsPane problemsPane;

	NirUtilDom nirUtilDom;

	NirUtilUrn nirUtilUrn;

	boolean renum = false;

	String tipo;
	
	private Vector idStatus= new Vector();
	
	

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		problemsPane = (ProblemsPane) serviceManager.lookup(ProblemsPane.class);
	}

	public void start() throws Exception {
	}

	public void stop() throws Exception {
		Properties props = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		props.setProperty("ndr", tipo);
		preferenceManager.setPreference(this.getClass().getName(), props);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		documentManager.addBeforeInitUndoAction(this);
		Properties props = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		if (props.containsKey("ndr")) {
			tipo = props.get("ndr").toString().trim();
			logger.debug("tipo: " + tipo);
		} else {
			tipo = "cardinale";
			props.setProperty("ndr", tipo);
			preferenceManager.setPreference(this.getClass().getName(), props);
			logger.debug("tipo: " + tipo);
		}
		aggiornaNumerazioneAndLink = new AggiornaNumerazioneAndLink(this);
	}

	// ///////////////////////////////////////////////// Rinumerazione Interface
	public void aggiorna(Document document) {
		
		Node root=document.getElementsByTagName("NIR").item(0);
		
		Vector removed = getRemoved(document);
		if(removed!=null && removed.size()>0){
			for(int i = 0; i<removed.size();i++){
				System.out.println("removed "+removed.elementAt(i));				
			}
			
			addIdProblems(root, removed);
				
			
			aggiornaNumerazioneAndLink.setRemovedIDs(removed);
			
		}
		logger.debug("rinumerazione START");
		
		try{
			if (renum){
				aggiornaNumerazioneAndLink.aggiornaNum(document);
			}
			else{				
				aggiornaNumerazioneAndLink.aggiornaID(document);
			}
			logger.debug("rinumerazione END");
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("rinumerazione non applicabile");
		}

		this.idStatus=new Vector();
		
		setIdStatus(root);
		
	}

	/**
	 * Fa un confronto fra gli id dello stato precedente e quelli dell'attuale e restituisce il vettore degli id rimossi
	 * @param document
	 * @return
	 */
	private Vector getRemoved(Document document) {
		Vector removed = new Vector();
		Vector oldIDs=getIdStatus();
		
		Node root = document.getElementsByTagName("NIR").item(0);
		if (oldIDs==null || oldIDs.size()==0 || root == null)
			return null;
		
		
		Vector nonUpdatedIDs = new Vector();
		getCurrentStatus(root,nonUpdatedIDs);
				
		for(int i = 0; i<oldIDs.size();i++){
			if(!nonUpdatedIDs.contains(oldIDs.elementAt(i))){
				removed.add(oldIDs.elementAt(i));
			}
		}
		
		return removed;
	}

	public void setRinumerazione(boolean renum) {
		this.renum = renum;
	}

	public boolean isRinumerazione() {
		return this.renum;
	}

	protected Logger getLogger() {
		return this.logger;
	}

	protected RulesManager getDtdRulesManager() {
		return this.rulesManager;
	}

	public String getRinumerazioneNdr() {
		return this.tipo;
	}

	public void setRinumerazioneNdr(String tipo) {
		this.tipo = tipo;
	}

	// ////////////////////////////////// DocumentBeforeInitUndoAction Interface
	public boolean beforeInitUndo(Document dom) {
		boolean isRenum = isRinumerazione();
		setRinumerazione(false);
		aggiorna(dom);
		setRinumerazione(isRenum);
		return true;
	}

	public Vector getIdStatus() {
		return idStatus;
	}

	/**
	 * restituisce un vector con gli id presenti sul dom
	 * @param node: nodo analizzato
	 * @param status: vettore in uscita con gli id
	 */
	public void getCurrentStatus(Node node, Vector status) {
		if (node == null)		
			return;
			
		String idValue = UtilDom.findAttribute(node, "id");
		if(!idValue.equals(""))
			status.add(idValue);
				
		NodeList figliNodo = node.getChildNodes();

		for (int i = 0; i < figliNodo.getLength(); i++) {
			Node figlio = figliNodo.item(i);
			getCurrentStatus(figlio, status);
		}
		
		
	}
	public void setIdStatus(Node node) {
		
				
		if (node == null)		
			return;
			
		String idValue = UtilDom.findAttribute(node, "id");
		if(!idValue.equals(""))
			this.idStatus.add(idValue);
				
		NodeList figliNodo = node.getChildNodes();

		for (int i = 0; i < figliNodo.getLength(); i++) {
			Node figlio = figliNodo.item(i);
			setIdStatus(figlio);
		}
		
	}
	
	protected void addIdProblems(Node node, Vector removed) {
		
		if (node == null || removed==null || removed.size()==0)
			return;

		
		NamedNodeMap attrList = node.getAttributes();
		if (attrList != null){
			for (int j = 0; j < attrList.getLength(); j++) {
				Attr attributo = (Attr) (attrList.item(j));
				if(removed.contains(attributo.getValue())){
					problemsPane.addProblem(new IdProblemImpl(node,Problem.WARNING,attributo.getName(),attributo.getValue()));
					
				}
				
				if(attributo.getValue().startsWith("#") && removed.contains( attributo.getValue().substring(1) ) ){
					problemsPane.addProblem(new IdProblemImpl(node,Problem.WARNING,attributo.getName(),attributo.getValue()));
				}
			}
		}
					
				
		NodeList figliNodo = node.getChildNodes();

		for (int i = 0; i < figliNodo.getLength(); i++) {
			Node figlio = figliNodo.item(i);
			addIdProblems(figlio, removed);
		}
}

}
