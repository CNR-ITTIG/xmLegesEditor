/*
 * Created on 22-apr-2005 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package it.cnr.ittig.xmleges.editor.blocks.form.meta.ciclodivita;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementEvent;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementListener;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaEventoForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MetaDescrittoriVigenzaForm</code>.
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
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba </a>
 */
public class CiclodiVitaEventoFormImpl implements CiclodiVitaEventoForm, Initializable, Serviceable, FormVerifier {

//	Eventi.jfrm
    Form form;

    //Evento.jfrm
    Form sottoFormEvento;

    //DatiEvento.jfrm
    Form sottoFormDatiEvento;

    DateForm dataFormDatiEvento;

    JTextField tagTipoEvento;

    JComboBox tagTipoRelazioneSottoFormDatiEvento;

    UrnForm urnFormRelazione;

    JComboBox tagEffettoSottoFormDatiEvento;

    Evento[] eventi;

    String tipoDocumento;

    String tipoDTD;

    String errorMessage = "";

    ListTextField eventi_listtextfield;
	
	
    /**
     * Editor per il ListTextField con la lista degli eventi
     */
    private class EventiListTextFieldEditor implements ListTextFieldEditor, ListTextFieldElementListener {
        Evento e;

        Form formList;
        ListTextFieldEditor listEditor;

        public EventiListTextFieldEditor(Form form) {
            this.formList = form;
        }

        public Component getAsComponent() {
            return formList.getAsComponent();
        }


        	

        public Object getElement() {
            if (!checkData())
                return null;
            else
                return e;
        }


       
		public void setElement(Object object) {
			
			e = (Evento) object;
			
			dataFormDatiEvento.set(UtilDate.normToDate(e.getData()));
			tagTipoEvento.setText(e.getTipoEvento());
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(e.getFonte().getTagTipoRelazione());
			try {
				urnFormRelazione.setUrn(new Urn(e.getFonte().getLink()));
			} catch (ParseException e) {
			}
			if(e.getFonte().getTagTipoRelazione().equals("giurisprudenza")){
				tagEffettoSottoFormDatiEvento.setEnabled(true);
				tagEffettoSottoFormDatiEvento.setSelectedItem(e.getFonte().getEffetto());
			}else{
				tagEffettoSottoFormDatiEvento.setSelectedItem(null);
				tagEffettoSottoFormDatiEvento.setEnabled(false);
			}
			
		
		}

		public void clearFields() {
			
			dataFormDatiEvento.set(null);
			tagTipoEvento.setText("");
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);			
			urnFormRelazione.setUrn(new Urn());				
			tagEffettoSottoFormDatiEvento.setSelectedItem(null);
//			eventi_listtextfield.setListElements(new Vector());
						
		}

		public boolean checkData() {
			
			boolean isvalid=true;
			isvalid = (dataFormDatiEvento!=null)&&(dataFormDatiEvento.getAsYYYYMMDD()!=null)&&(!dataFormDatiEvento.getAsYYYYMMDD().trim().equals(""));
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.datavuota";				
				return false;
			}
			isvalid=(tagTipoEvento!=null)&&(!tagTipoEvento.getText().trim().equals(""));
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.tipoeventovuoto";				
				return false;
			}
			isvalid=(urnFormRelazione.getUrn().isValid());
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.urnvuota";				
				return false;
			}	
			isvalid=(tagTipoRelazioneSottoFormDatiEvento.getSelectedItem()!=null);
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.tiporelvuoto";				
				return false;
			}else{
				if(tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().equals("giurisprudenza")){
					if(tagEffettoSottoFormDatiEvento.getSelectedItem()==null){
						errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.effettovuoto";
						return false;
					}else return true;
					
				}
			}
			return isvalid;

		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public Dimension getPreferredSize() {
			return new Dimension(800, 150);
		}
		
		
		public void elementChanged(ListTextFieldElementEvent evt) {
			 
			int eventID = evt.getID();
			
			if (eventID == ListTextFieldElementEvent.ELEMENT_ADD) {
				String nomeTag=(String)tagTipoRelazioneSottoFormDatiEvento.getSelectedItem();
				Relazione r=null;						
				if( (dataFormDatiEvento.getAsYYYYMMDD() != null)&&(!dataFormDatiEvento.getAsYYYYMMDD().trim().equals(""))
						&&(nomeTag!=null)&&(urnFormRelazione.getUrn() != null))
				{
					if((nomeTag.toString().equals("giurisprudenza"))&&(tagEffettoSottoFormDatiEvento.getSelectedItem()!=null)){
						r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazione.getUrn().toString(),tagEffettoSottoFormDatiEvento.getSelectedItem().toString());
					}
					else if(!nomeTag.equals("")){ 
						r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazione.getUrn().toString());
					}else 
						r=null;
					e =new Evento(calcolaIDevento(), dataFormDatiEvento.getAsYYYYMMDD(),r,tagTipoEvento.getText());
				}
				
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_MODIFY) {
					
				String nomeTag=tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().toString();
				e.setFonte(new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazione.getUrn().toString()));

				if(nomeTag.equals("giurisprudenza")){							
					e.getFonte().setEffetto((String)tagEffettoSottoFormDatiEvento.getSelectedItem());
				}
				
				e.setTipoEvento(tagTipoEvento.getText());
				e.setData(dataFormDatiEvento.getAsYYYYMMDD());

			} else if (eventID == ListTextFieldElementEvent.ELEMENT_REMOVE) {
				e.setFonte(null);
				e = null;
				dataFormDatiEvento.set(null);
				tagTipoEvento.setText("");
				tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);
				urnFormRelazione.setUrn(new Urn());
				tagEffettoSottoFormDatiEvento.setSelectedItem(null);
			}
			
		}
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {

		form = (Form) serviceManager.lookup(Form.class);
        form.setName("editor.form.meta.ciclodivita.eventi");

        sottoFormEvento = (Form) serviceManager.lookup(Form.class);
        sottoFormDatiEvento = (Form) serviceManager.lookup(Form.class);

        eventi_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);

        dataFormDatiEvento = (DateForm) serviceManager.lookup(DateForm.class);

        urnFormRelazione = (UrnForm) serviceManager.lookup(UrnForm.class);

		
		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		sottoFormDatiEvento.setMainComponent(getClass().getResourceAsStream("DatiEvento.jfrm"));
		sottoFormDatiEvento.replaceComponent("editor.meta.ciclodivita.evento.data", dataFormDatiEvento.getAsComponent());
		sottoFormDatiEvento.replaceComponent("editor.form.meta.urn", urnFormRelazione.getAsComponent());
		
		
		tagTipoEvento = (JTextField) sottoFormDatiEvento.getComponentByName("editor.form.meta.ciclodivita.evento.tipoeventi");
		
		tagTipoRelazioneSottoFormDatiEvento = (JComboBox) sottoFormDatiEvento.getComponentByName("editor.meta.ciclodivita.evento.tiporelazione");
		tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
		tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
		tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
		tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
		tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
		tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");
		
		tagTipoRelazioneSottoFormDatiEvento.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange()==ItemEvent.SELECTED){
					if (tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().equals("giurisprudenza")){
						tagEffettoSottoFormDatiEvento.setEnabled(true);
						
					}else
						tagEffettoSottoFormDatiEvento.setEnabled(false);
				}
				
			}
			
		});		
		
		tagEffettoSottoFormDatiEvento = (JComboBox) sottoFormDatiEvento.getComponentByName("editor.form.meta.ciclodivita.evento.tipoeffetto");
		tagEffettoSottoFormDatiEvento.addItem("normativo");
		tagEffettoSottoFormDatiEvento.addItem("interpretativo");
		tagEffettoSottoFormDatiEvento.setEnabled(false);
		
		EventiListTextFieldEditor etfe = new EventiListTextFieldEditor(sottoFormDatiEvento);
		eventi_listtextfield.setEditor(etfe);
		eventi_listtextfield.addListTextFieldElementListener(etfe);
		
		sottoFormEvento.setMainComponent(getClass().getResourceAsStream("Evento.jfrm"));		
		sottoFormEvento.replaceComponent("editor.form.meta.ciclodivita.eventi", eventi_listtextfield.getAsComponent());
		sottoFormEvento.getAsComponent().setName("editor.form.meta.ciclodivita.eventi");		
		sottoFormEvento.setSize(650, 400);
		
		form.setMainComponent(getClass().getResourceAsStream("Eventi.jfrm"));		
		form.replaceComponent("editor.form.meta.ciclodivita.eventi", sottoFormEvento.getAsComponent());		
		form.addFormVerifier(this);
		

	}


	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	
	public boolean openForm() {

		form.setSize(740, 500);
		form.showDialog();
		return form.isOk();
	
	}

	// ////////////////////////////////////////////// FormVerifier Interface
	public boolean verifyForm() {
		
		return true;
				
	
	}

	public String getErrorMessage() {
		return errorMessage;
		
	}

	public Evento[] getEventi() {

		Vector e = eventi_listtextfield.getListElements();
		eventi = new Evento[e.size()];
		e.toArray(eventi);
		return eventi;
	
	}

	
	public void setEventi(Evento[] eventi) {
		
		
//		this.eventi = eventi;
		Vector v = new Vector();
		if (eventi != null && eventi.length>0) {
			for (int i = 0; i < eventi.length; i++) {
				v.add(eventi[i]);
			}
			
			
			dataFormDatiEvento.set(UtilDate.normToDate(eventi[0].getData()));
						
			Relazione fonte = eventi[0].getFonte();
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(fonte.getTagTipoRelazione());
			if(fonte.getTagTipoRelazione().equals("giurisprudenza")){
				tagEffettoSottoFormDatiEvento.setEnabled(true);
				tagEffettoSottoFormDatiEvento.setSelectedItem(fonte.getEffetto());
			}else{
				tagEffettoSottoFormDatiEvento.setSelectedItem(null);
				tagEffettoSottoFormDatiEvento.setEnabled(false);
			}
			
			
			try {
				urnFormRelazione.setUrn(new Urn(fonte.getLink()));
			} catch (ParseException e) {
			}
		
			
				
		}else{
			
			dataFormDatiEvento.set(null);
			tagTipoEvento.setText("");
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);
			tagEffettoSottoFormDatiEvento.setSelectedItem(null);
			urnFormRelazione.setUrn(new Urn());
			eventi_listtextfield.setListElements(v);
			
			
		}
		eventi_listtextfield.setListElements(v);
	}


	public String getTipoDocumento() {
		
		return null;
		
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public void setTipoDTD(String tipoDTD) {
		this.tipoDTD = tipoDTD;
	}
	/**
	 * Restituisce un ID univoco per un nuovo evento
	 */
	private String calcolaIDevento() {
		String prefix = "e";
		String uID = prefix;
		int max = 0;

		// TODO questo codice in pratica duplica quello per le relazioni...

		// Prendi il massimo degli id delle vigenze. Usiamo il vettore preso
		// dalla ListTextField
		// in modo tale da considerare anche le nuove vigenze inserite.
		Vector eventiVect = eventi_listtextfield.getListElements();
		for (int i = 0; i < eventiVect.size(); i++) {
			Evento e = (Evento) eventiVect.elementAt(i);
			if (e.getId() != null) {
				try {
					String s = e.getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(e.getId().substring(prefix.length()));
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

		// TODO questo codice duplica quello di MetaDescrittoriFormImpl!!!
		// TODO sarebbe anche da implementare in maniera un po' pi? elegante...
		// TODO e spostare nelle utils

		
		// Usiamo il vettore preso dalla ListTextField in modo tale da
		// considerare
		// anche le nuove vigenze inserite.
		
		Vector eventiVect = eventi_listtextfield.getListElements();
		for (int i = 0; i < eventiVect.size(); i++) {
//		for (int i = eventiVect.size()-1; i >= 0; i--) {
			Evento e = (Evento) eventiVect.elementAt(i);
			if (e.getFonte() != null) {
				try {
					String s = e.getFonte().getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(e.getFonte().getId().substring(prefix.length()));
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
	

	public Evento getSelectedEvento() {

		return (Evento)eventi_listtextfield.getSelectedItem();
	}

}

	
