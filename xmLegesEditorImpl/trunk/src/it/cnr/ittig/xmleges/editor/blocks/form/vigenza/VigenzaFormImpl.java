package it.cnr.ittig.xmleges.editor.blocks.form.vigenza;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.evento.EventoForm;
import it.cnr.ittig.xmleges.editor.services.form.vigenza.VigenzaForm;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextArea;

import org.w3c.dom.Node;

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


	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventoiniziovigoreform = (EventoForm) serviceManager.lookup(EventoForm.class);
		eventofinevigoreform = (EventoForm) serviceManager.lookup(EventoForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("SelezioneVigenzaForm.jfrm"));
		form.setName("editor.selezionevigenzatesto");
		textArea = (JTextArea) form.getComponentByName("editor.selezionevigenzatesto.testo");
		
		form.replaceComponent("editor.selezionevigenza.eventoiniziovigore", eventoiniziovigoreform.getAsComponent());
		form.replaceComponent("editor.selezionevigenza.eventofinevigore", eventofinevigoreform.getAsComponent());

		vigenzaStatus = (JComboBox) form.getComponentByName("editor.selezionevigenza.status");
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
		return (String)vigenzaStatus.getSelectedItem();
	}
	
	/////////////////////////////////////////////////////////////////////////

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean verifyForm() {
		// TODO Auto-generated method stub
		return false;
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
		return new VigenzaEntity(eventoiniziovigoreform.getEvento(),
				eventofinevigoreform.getEvento(),(String)vigenzaStatus.getSelectedItem(),sel_text);
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
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public Evento getFineEfficacia() {
//		return null;
//	}

	

	

	
}
