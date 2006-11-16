package it.cnr.ittig.xmleges.editor.blocks.form.vigenza;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.evento.EventoForm;
import it.cnr.ittig.xmleges.editor.services.form.vigenza.VigenzaForm;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextArea;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.vigenza.VigenzaForm</code>.</h1>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class VigenzaFormImpl implements VigenzaForm, FormVerifier, Loggable, Serviceable, Initializable {

	Logger logger;

	Form form;
	
	String tipoDTD;
	
	VigenzaEntity vigenza;
	
	EventoForm eventoiniziovigoreform;
	
	EventoForm eventofinevigoreform;
	
	JComboBox vigenzaStatus;

	JList vigenzeList;

	JTextArea textArea;
	
	Node activeNode;
	
	String sel_text;
	
	String errorMessage = "";
	
	DocumentManager documentManager;
	
	
	

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventoiniziovigoreform = (EventoForm) serviceManager.lookup(EventoForm.class);
		eventofinevigoreform = (EventoForm) serviceManager.lookup(EventoForm.class);
		
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("SelezioneVigenzaForm.jfrm"));
		form.setName("editor.selezionevigenzatesto");
		textArea = (JTextArea) form.getComponentByName("editor.selezionevigenzatesto.testo");
		
		form.replaceComponent("editor.selezionevigenza.eventoiniziovigore", eventoiniziovigoreform.getAsComponent());
		form.replaceComponent("editor.selezionevigenza.eventofinevigore", eventofinevigoreform.getAsComponent());

		vigenzaStatus = (JComboBox) form.getComponentByName("editor.selezionevigenza.status");
		vigenzaStatus.addItem("--");
		vigenzaStatus.addItem("omissis");
		vigenzaStatus.addItem("abrogato");
		vigenzaStatus.addItem("annullato");
		vigenzaStatus.addItem("sospeso");
		vigenzaStatus.setSelectedItem(null);
	}

	
//	public Evento getInizioEfficacia() {
//		
//		return eventoiniziovigoreform.getEvento();
//	}
//	public Evento getFineEfficacia() {
//			
//		return eventofinevigoreform.getEvento();
//	}
public Evento getInizioVigore() {
		
		return eventoiniziovigoreform.getEvento();
	}
	public Evento getFineVigore() {
			
		return eventofinevigoreform.getEvento();
	}


	

	public String getStatus() {
		if(vigenzaStatus.getSelectedItem().equals("--"))
			return null;
		else
			return (String)vigenzaStatus.getSelectedItem();
	}
	
	/////////////////////////////////////////////////////////////////////////

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean verifyForm() {
		
		
		//iniziovigore obbligatorio		
		//poi se esiste anche la fine allora lo status è obbligatorio
		//quindi posso fare le ricerche per iniziovigore
		//messaggio se premo senza fare nulla segnalazione del fatto
		boolean isvalid=true;
		isvalid = (eventoiniziovigoreform.getEvento()!=null)||(eventofinevigoreform.getEvento()!=null)||
											( (vigenzaStatus.getSelectedItem()!=null)&&(!vigenzaStatus.getSelectedItem().equals("--")));
		
		if(!isvalid){
			//tutti i campi sono vuoti ma il msg non si vede perche return true;
			errorMessage = "editor.selezionevigenza.msg.err.eliminavigenza";
			return true;
		}else{//solo se esiste la finevigore lo status è obbligatorio
			
			isvalid=eventoiniziovigoreform.getEvento()!=null;
			if(!isvalid){
				errorMessage = "editor.selezionevigenza.msg.err.iniziovigorevuoto";				
				return false;				
			}
			
			if(eventofinevigoreform.getEvento()!=null){
				isvalid=(vigenzaStatus.getSelectedItem()!=null)&&(!vigenzaStatus.getSelectedItem().equals("--"));
			
				if(!isvalid){
					errorMessage = "editor.selezionevigenza.msg.err.statusvuoto";				
					return false;				
				}
			}
			
			Evento eventoiniziovigore=eventoiniziovigoreform.getEvento();
			Evento eventofinevigore=eventofinevigoreform.getEvento();
			
			Document doc = documentManager.getDocumentAsDom();
			NodeList eventiList = doc.getElementsByTagName("evento");
			
			isvalid=eventiList.getLength()>0;
			
			if(!isvalid){
				errorMessage="editor.selezionevigenza.msg.err.nessunevento";
				eventoiniziovigoreform.setEvento(null);
				eventofinevigoreform.setEvento(null);
				return false;
			}
			
			
			String id, data, tipo, fonte=null;
			boolean isvalidInizio=false;
			boolean isvalidFine;
			if(eventofinevigore!=null){
				isvalidFine=false;
			}else isvalidFine=true;

			
			for (int i = 0; i < eventiList.getLength();i++) {
				Node eventoNode = eventiList.item(i);
				id = eventoNode.getAttributes().getNamedItem("id") != null ? eventoNode.getAttributes().getNamedItem("id").getNodeValue() : null;				
				data = eventoNode.getAttributes().getNamedItem("data") != null ? eventoNode.getAttributes().getNamedItem("data").getNodeValue() : null;
				tipo = eventoNode.getAttributes().getNamedItem("tipo") != null ? eventoNode.getAttributes().getNamedItem("tipo").getNodeValue() : null;
				Node nodeFonte = eventoNode.getAttributes().getNamedItem("fonte");
				if (nodeFonte != null) {
					fonte = nodeFonte.getNodeValue();					
				}
				
				isvalidInizio = isvalidInizio || (id.equals(eventoiniziovigore.getId())
					&& data.equals(eventoiniziovigore.getData())
					&& tipo.equals(eventoiniziovigore.getTipoEvento())
					&& fonte.equals(eventoiniziovigore.getFonte().getId()));
				
				
				if(eventofinevigore!=null){
					isvalidFine = isvalidFine || (id.equals(eventofinevigore.getId())
					&& data.equals(eventofinevigore.getData())
					&& tipo.equals(eventofinevigore.getTipoEvento())
					&& fonte.equals(eventofinevigore.getFonte().getId()));
		
				}
				
			}
			if(!isvalidInizio){
				errorMessage = "editor.selezionevigenza.msg.err.iniziovigoremod";
				eventoiniziovigoreform.setEvento(null);
				return false;				
			}
			if(!isvalidFine){
				errorMessage = "editor.selezionevigenza.msg.err.finevigoremod";
				eventofinevigoreform.setEvento(null);
				return false;				
			}
			
		}
		
		return true;

	}

	public void setTipoDTD(String tipoDTD) {
		this.tipoDTD = tipoDTD;
		
	}

	public void setInizioVigore(Evento iniziovigore) {
		if(iniziovigore!=null && iniziovigore.getId()!=null)
			eventoiniziovigoreform.setEvento(iniziovigore);
		else
			eventoiniziovigoreform.setEvento(null);
	}

	public void setFineVigore(Evento finevigore) {
		if(finevigore!=null && finevigore.getId()!=null)
			eventofinevigoreform.setEvento(finevigore);
		else
			eventofinevigoreform.setEvento(null);
	}
	public void setStatus(String status) {
		if(status!=null && !status.trim().equals("")){
			vigenzaStatus.setSelectedItem(status);
		}else
			vigenzaStatus.setSelectedItem(null);
	}

	public VigenzaEntity getVigenza() {
		String stato=null;
		if(vigenzaStatus.getSelectedItem()!=null && !vigenzaStatus.getSelectedItem().equals("--"))
			stato=(String)vigenzaStatus.getSelectedItem();
		return new VigenzaEntity(activeNode, eventoiniziovigoreform.getEvento(),
				eventofinevigoreform.getEvento(),stato,sel_text);
	}

	public void setVigenza(VigenzaEntity vigenza) {
		eventoiniziovigoreform.setEvento(vigenza.getEInizioVigore());
		eventofinevigoreform.setEvento(vigenza.getEFineVigore());
		vigenzaStatus.setSelectedItem(vigenza.getStatus());
		
	}

	public boolean openForm(Node attivo) {
		activeNode=attivo;
		textArea.setText(sel_text);

		form.setSize(600, 350);
		form.addFormVerifier(this);
		form.showDialog();
		return form.isOk();
	}

	public void setTestoselezionato(String testo) {
		sel_text=testo;
		
	}
//	public void setInizioEfficacia(Evento inizioefficacia) {
//	if(inizioefficacia!=null && inizioefficacia.getId()!=null){
//		eventoinizioform.setEvento(inizioefficacia);
//			
//	}else{
//		eventoinizioform.setEvento(null);
//	}
//	
//}





//public void setFineEfficacia(Evento fineefficacia) {
//	if(fineefficacia!=null && fineefficacia()!=null){
//		eventofineform.setEvento(fineefficacia);
//			
//	}else{
//		eventofineform.setEvento(null);
//	}
//	
//}

//	public Evento getInizioEfficacia() {
//		return null;
//	}
//
//	public Evento getFineEfficacia() {
//		return null;
//	}

	

	

	
}
