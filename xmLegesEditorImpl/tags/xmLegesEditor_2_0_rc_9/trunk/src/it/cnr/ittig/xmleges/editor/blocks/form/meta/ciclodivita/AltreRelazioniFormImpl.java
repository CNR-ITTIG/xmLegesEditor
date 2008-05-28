package it.cnr.ittig.xmleges.editor.blocks.form.meta.ciclodivita;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementEvent;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementListener;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MetaDescrittoriForm</code>.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, <a
 *         href="mailto:t.paba@onetech.it">Tommaso Paba</a>
 */
public class AltreRelazioniFormImpl implements CiclodiVitaForm, Loggable, Serviceable, Initializable, ActionListener, FormVerifier {

	Logger logger;

	//   AltreRelazioni.jfrm
	Form form;
	
	//	 Relazioni.jfrm
	Form formRelazioni;

	//   DatiRelazione.jfrm
	Form sottoFormDatiRelazione;
	
	String tipoDTD;

	String tipoDocumento;


	JButton relazioniButton;
	

	JList relazioniList;
	
	Relazione[] relazioniUlteriori;
	
	ListTextField rel_listtextfield;

	JComboBox tagSottoFormDatiRelazione;
	
	JComboBox tagEffettoTipoSFormDatiRelazione;
	
	JLabel labelEffettoTipo;

	UrnForm urnFormRelazioni;
	
	String errorMessage = "";




	/**
	 * Editor per il ListTextField della lista delle relazioni (altre)
	 */
	private class RelListTextFieldEditor implements ListTextFieldEditor, ListTextFieldElementListener {
		Form form;

		Relazione r;

		public RelListTextFieldEditor(Form form) {
			this.form = form;
		}

		public Component getAsComponent() {
			return form.getAsComponent();
		}

		public void elementChanged(ListTextFieldElementEvent e) {
			
			int eventID = e.getID();

			String nomeTag = tagSottoFormDatiRelazione.getSelectedItem().toString();
			Urn urn = urnFormRelazioni.getUrn();

			if (eventID == ListTextFieldElementEvent.ELEMENT_ADD) {

				if (!checkData()) {
					r = null;
				} else {
					if((nomeTag.toString().equals("giurisprudenza"))&&(tagSottoFormDatiRelazione.getSelectedItem()!=null)){
						r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazioni.getUrn().toString(),tagSottoFormDatiRelazione.getSelectedItem().toString());
					}else if((nomeTag.toString().equals("haallegato"))&&(tagSottoFormDatiRelazione.getSelectedItem()!=null)
							||(nomeTag.toString().equals("allegatodi"))&&(tagSottoFormDatiRelazione.getSelectedItem()!=null)){
						r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazioni.getUrn().toString(),tagSottoFormDatiRelazione.getSelectedItem().toString());
					}
					else if(!nomeTag.equals("")){ 
						r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazioni.getUrn().toString());
					}else 
						r=null;
					
					if(r.getTagTipoRelazione().equals("originale"))
						tagSottoFormDatiRelazione.removeItem("originale");
				}
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_MODIFY) {

				if (!checkData()) {
					r = null;
				} else {
					r.setTagTipoRelazione(nomeTag);
					r.setLink(urn.toString());
					if(nomeTag.equals("giurisprudenza")){							
						r.setEffetto_tipoall((String)tagEffettoTipoSFormDatiRelazione.getSelectedItem());
					}else if(nomeTag.equals("haallegato")||nomeTag.equals("allegatodi")){							
						r.setEffetto_tipoall((String)tagEffettoTipoSFormDatiRelazione.getSelectedItem());
					}
					if(r.getTagTipoRelazione().equals("originale"))
						tagSottoFormDatiRelazione.removeItem("originale");
				}
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_REMOVE) {
				if(r.getTagTipoRelazione().equals("originale")){				
					 if(tagSottoFormDatiRelazione.getItemCount()<6)
						 tagSottoFormDatiRelazione.addItem("originale");
				}
				
				r = null;
				tagSottoFormDatiRelazione.setSelectedItem(null);
				tagEffettoTipoSFormDatiRelazione.setSelectedItem(null);
				urnFormRelazioni.setUrn(new Urn());
			}
		}

		public Object getElement() {
			return r;
		}

		public void setElement(Object object) {
			r = (Relazione) object;
			tagSottoFormDatiRelazione.setSelectedItem(r.getTagTipoRelazione());
			try {
				urnFormRelazioni.setUrn(new Urn(r.getLink()));
			} catch (ParseException e) {
			}
			
			if(r.getTagTipoRelazione().equals("giurisprudenza")){
				tagEffettoTipoSFormDatiRelazione.setEnabled(true);
				tagEffettoTipoSFormDatiRelazione.setSelectedItem(r.getEffetto_tipoall());
			}else if(r.getTagTipoRelazione().equals("haallegato")||r.getTagTipoRelazione().equals("allegatodi")){
				tagEffettoTipoSFormDatiRelazione.setEnabled(true);
				tagEffettoTipoSFormDatiRelazione.setSelectedItem(r.getEffetto_tipoall());
			}else{
				tagEffettoTipoSFormDatiRelazione.setSelectedItem(null);
				tagEffettoTipoSFormDatiRelazione.setEnabled(false);
			}
			
			if(r.getTagTipoRelazione().equals("originale")){
				if(tagSottoFormDatiRelazione.getItemCount()<6)
					tagSottoFormDatiRelazione.addItem("originale");
			}else{
		    	

				Relazione[] newRelazioni = getRelazioniTotalefromCdvf();
								
				boolean found=false;
				for(int i =0;i<newRelazioni.length;i++){
					if(newRelazioni[i].getTagTipoRelazione().equals("originale")){						
						tagSottoFormDatiRelazione.removeItem("originale");
						found=true;
						break;
					}			
				}
				if(!found){
					if(tagSottoFormDatiRelazione.getItemCount()<6)
						tagSottoFormDatiRelazione.addItem("originale");
				}
				
				
		    }
			
		}

		public void clearFields() {
			tagSottoFormDatiRelazione.setSelectedItem(null);
			urnFormRelazioni.setUrn(new Urn());
			tagEffettoTipoSFormDatiRelazione.setSelectedItem(null);	
			
			
			Relazione[] newRelazioni = getRelazioniTotalefromCdvf();
			
			
			boolean found=false;
			for(int i =0;i<newRelazioni.length;i++){
				if(newRelazioni[i].getTagTipoRelazione().equals("originale")){					
					tagSottoFormDatiRelazione.removeItem("originale");
					found=true;
					break;
				}			
			}
			if(!found){
				if(tagSottoFormDatiRelazione.getItemCount()<6)
					tagSottoFormDatiRelazione.addItem("originale");
			}
		}

		public boolean checkData() {
			boolean isvalid=true;
			String nomeTag = (String) (tagSottoFormDatiRelazione).getSelectedItem();
			Urn urn = urnFormRelazioni.getUrn();
			
			isvalid=(nomeTag!=null);
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.tiporelvuoto";
				return false;
			}
			isvalid=(urn != null && urn.isValid());		
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.urnvuota";
				return false;
			}
			
					
			if(nomeTag.equals("giurisprudenza")){
					if(tagEffettoTipoSFormDatiRelazione.getSelectedItem()==null){						
						errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.effettovuoto";
						return false;
					}else 
						return true;
					
			}
			else if(nomeTag.equals("haallegato")
						||
						nomeTag.equals("allegatodi")){
					if(tagEffettoTipoSFormDatiRelazione.getSelectedItem()==null){
						errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.tipoallegato";
						return false;
					}
					else 
						return true;					
				
			}
			return isvalid;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public Dimension getPreferredSize() {
			return new Dimension(700, 150);
		}

		
	}

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);		
		formRelazioni = (Form) serviceManager.lookup(Form.class);		
		sottoFormDatiRelazione = (Form) serviceManager.lookup(Form.class);
		urnFormRelazioni = (UrnForm) serviceManager.lookup(UrnForm.class);
		
		rel_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		
		
		form.setMainComponent(getClass().getResourceAsStream("AltreRelazioni.jfrm"));
		
		form.setHelpKey("help.contents.form.ciclodivita");
		
		relazioniList = (JList) form.getComponentByName("editor.form.meta.ciclodivita.relazioni");
			
		form.setName("editor.form.meta.ciclodivita.riepilogo");
		
		sottoFormDatiRelazione.setMainComponent(getClass().getResourceAsStream("DatiRelazione.jfrm"));
		sottoFormDatiRelazione.replaceComponent("editor.form.meta.urn", urnFormRelazioni.getAsComponent());
		
		labelEffettoTipo = (JLabel) sottoFormDatiRelazione.getComponentByName("editor.form.meta.ciclodivita.evento.label.tipoeffetto");
		labelEffettoTipo.setText(" ");
		
		formRelazioni.setMainComponent(getClass().getResourceAsStream("Relazioni.jfrm"));
		formRelazioni.replaceComponent("editor.form.meta.ciclodivita.relazioni.listtextfield", rel_listtextfield.getAsComponent());
		formRelazioni.setName("editor.form.meta.ciclodivita.relazioni");
		formRelazioni.setSize(650, 500);
		

		RelListTextFieldEditor tfe = new RelListTextFieldEditor(sottoFormDatiRelazione);
		rel_listtextfield.setEditor(tfe);
		rel_listtextfield.addListTextFieldElementListener(tfe);

		tagEffettoTipoSFormDatiRelazione = (JComboBox) sottoFormDatiRelazione.getComponentByName("editor.form.meta.ciclodivita.relazione.tipoeffetto");
		tagEffettoTipoSFormDatiRelazione.addItem("normativo");
		tagEffettoTipoSFormDatiRelazione.addItem("implementativo");
		tagEffettoTipoSFormDatiRelazione.addItem("attoallegato");
		tagEffettoTipoSFormDatiRelazione.addItem("allegatointegrante");
		tagEffettoTipoSFormDatiRelazione.addItem("informativo");
		
		tagEffettoTipoSFormDatiRelazione.setEnabled(false);
		
		tagSottoFormDatiRelazione = (JComboBox) sottoFormDatiRelazione.getComponentByName("editor.form.meta.ciclodivita.relazioni.tipo");
		tagSottoFormDatiRelazione.addItem("originale");
		tagSottoFormDatiRelazione.addItem("attiva");
		tagSottoFormDatiRelazione.addItem("passiva");
		tagSottoFormDatiRelazione.addItem("giurisprudenza");
		tagSottoFormDatiRelazione.addItem("haallegato");
		tagSottoFormDatiRelazione.addItem("allegatodi");
		
		tagSottoFormDatiRelazione.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange()==ItemEvent.SELECTED){
					Object selectedTag=tagSottoFormDatiRelazione.getSelectedItem();
					if (selectedTag.equals("giurisprudenza")){
						tagEffettoTipoSFormDatiRelazione.removeAllItems();
						tagEffettoTipoSFormDatiRelazione.addItem("normativo");
						tagEffettoTipoSFormDatiRelazione.addItem("interpretativo");
						tagEffettoTipoSFormDatiRelazione.setSelectedItem(null);
						tagEffettoTipoSFormDatiRelazione.setEnabled(true);
						labelEffettoTipo.setText("Effetto:");
						
					}else if (selectedTag.equals("haallegato")||selectedTag.equals("allegatodi")){
						tagEffettoTipoSFormDatiRelazione.removeAllItems();
						tagEffettoTipoSFormDatiRelazione.addItem("attoallegato");
						tagEffettoTipoSFormDatiRelazione.addItem("allegatointegrante");
						tagEffettoTipoSFormDatiRelazione.addItem("informativo");
						tagEffettoTipoSFormDatiRelazione.setSelectedItem(null);
						tagEffettoTipoSFormDatiRelazione.setEnabled(true);
						labelEffettoTipo.setText("Tipo allegato:");
					}
					
					else {
						tagEffettoTipoSFormDatiRelazione.setSelectedItem(null);
						tagEffettoTipoSFormDatiRelazione.setEnabled(false);
						labelEffettoTipo.setText(" ");
					}
				}
				
			}
			
		});		
		
		
		relazioniButton = (JButton) form.getComponentByName("editor.form.meta.ciclodivita.riepilogo.relazioni_btn");
		
		relazioniButton.addActionListener(this);
	}

	// ////////////////////////////////////////////// CiclodiVitaForm Interface
	public boolean openForm() {
		form.setSize(650, 500);
		form.showDialog();
		return form.isOk();
	}

	public void actionPerformed(ActionEvent e) {				
		if (e.getSource().equals(relazioniButton)) { // RELAZIONI
			Vector v = new Vector();
			if (relazioniUlteriori != null) {
				for (int i = 0; i < relazioniUlteriori.length; i++) {
					v.add(relazioniUlteriori[i]);
				}
			}
			rel_listtextfield.setListElements(v);
	
			formRelazioni.showDialog();
	
			if (formRelazioni.isOk()) {
				relazioniUlteriori = new Relazione[rel_listtextfield.getListElements().size()];
				rel_listtextfield.getListElements().toArray(relazioniUlteriori);
				relazioniList.setListData(relazioniUlteriori);
			}
		}
		
	}

	
	

	public Relazione[] getRelazioniUlteriori() {
		return relazioniUlteriori;
	}

		
	public void setRelazioniUlteriori(Relazione[] relazioniUlteriori) {
		if (relazioniUlteriori != null) {
			this.relazioniUlteriori = relazioniUlteriori;
			relazioniList.setListData(relazioniUlteriori);
		}
	}

	public void setTipoDTD(String tipoDTD) {
		this.tipoDTD = tipoDTD;
	}

	

	

	/**
	 * Restituisce un ID univoco per una nuova relazione.
	 */
	private String calcolaIDRelazione(String nomeTag) {
		
		String prefix = "r";

		if (nomeTag.equals("attiva")) {
			prefix = "ra";
		} else if (nomeTag.equals("passiva")) {
			prefix = "rp";
		} else if (nomeTag.equals("originale")) {
			prefix = "ro";
		} else if (nomeTag.equals("giurisprudenza")) {
			prefix = "rg";
		} else if (nomeTag.equals("haallegato")) {
			prefix = "haa";
		}else if (nomeTag.equals("allegatodi")) {
			prefix = "all";
		}

		String uID = prefix;
		int max = 0;

		// Prendi il massimo degli id nelle relazioni ulteriori
		// (prendi le relazioni dalla ListTextField per includere quelle
		// inserite dall'utente)
		Vector relazioniVect = rel_listtextfield.getListElements();
		for (int i = 0; i < relazioniVect.size(); i++) {
			Relazione r = (Relazione) relazioniVect.elementAt(i);
			
			if (r.getId() != null) {
				
				try {
					String s = r.getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(r.getId().substring(prefix.length()));
						if (idValue.intValue() > max) {
							max = idValue.intValue();
						}
					}
				} catch (IndexOutOfBoundsException exc) {
				}
			}
		}

		

		uID += (max + 1);
		
		return uID;
	}

	public Relazione[] getRelazioniTotalefromCdvf() {
		Vector vec = rel_listtextfield.getListElements();
    	Relazione[] rel_ins = new Relazione[vec.size()];
		vec.toArray(rel_ins);
    	
		Vector relazioniVect = new Vector();
		
		
		for (int i = 0; i < rel_ins.length; i++) {
			relazioniVect.add(rel_ins[i]);
		}

		Relazione[] newRelazioni = new Relazione[relazioniVect.size()];
		relazioniVect.copyInto(newRelazioni);
		return newRelazioni;
	}

	public boolean verifyForm() {
		return true;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public int getEventoSelezionato() {
		// TODO Auto-generated method stub
		return -1;
	}

	public boolean getModificaEventi() {
		// TODO Auto-generated method stub
		return false;
	}

	public Evento[] getEventi() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEventi(Evento[] eventi) {
		// TODO Auto-generated method stub
		
	}

	public void setEventiOnVigenze(String[] eventiOnVigenze, VigenzaEntity[] vigenze) {
		// TODO Auto-generated method stub
		
	}

	
}
