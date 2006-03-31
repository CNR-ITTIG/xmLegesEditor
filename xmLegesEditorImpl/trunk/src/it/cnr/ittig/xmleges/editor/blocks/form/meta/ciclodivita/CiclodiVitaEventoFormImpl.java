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
import it.cnr.ittig.xmleges.core.services.form.FormException;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;
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
public class CiclodiVitaEventoFormImpl implements CiclodiVitaEventoForm, Initializable, Serviceable, ActionListener, FormVerifier {

	//Eventi.jfrm
	Form form;

	//Evento.jfrm
	Form sottoFormEvento;

	//DatiEvento.jfrm
	Form sottoFormDatiEvento;

	DateForm dataDatiEvento;
	
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
	 * Editor per il ListTextField con la lista delle vigenze
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

		public void elementChanged(ListTextFieldElementEvent evt) {

			int eventID = evt.getID();
			
			if (eventID == ListTextFieldElementEvent.ELEMENT_ADD) {

				String nomeTag=tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().toString();
				Relazione r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazione.getUrn().toString());
				e = new Evento(calcolaIDevento(), dataDatiEvento.getAsYYYYMMDD(),r);
				
	
				// Se la data ? riempita, inseriamo anche la relazione
				if (e.getData() != null) {
//					String nomeTag = (String) ((JComboBox) form.getComponentByName("editor.meta.ciclodivita.evento.tiporelazione")).getSelectedItem();
					if (nomeTag != null && urnFormRelazione.getUrn() != null) {
						r = new Relazione(nomeTag, calcolaIDRelazione(nomeTag), urnFormRelazione.getUrn().toString());
						e.setFonte(r);
						e.setEffetto(tagEffettoSottoFormDatiEvento.getSelectedItem().toString());
						e.setTipoEvento(tagTipoEvento.getText());
					} else {
						// L'utente non ha riempito i campi per bene!
						r = null;
					}
				} else {
					r = null;
					e.setFonte(null);
					
				}
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_MODIFY) {
									

				
				String nomeTag=tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().toString();

				
				// Se la data ? riempita, inseriamo anche la relazione
				if (e.getData() != null) {
					if (e.getFonte() == null) {
						// Non esiste una relazione gi? associata alla relazione,
						// ne creiamo una nuova
				
						if (nomeTag != null && urnFormRelazione.getUrn() != null) {
							e.setFonte(new Relazione(nomeTag, calcolaIDRelazione(nomeTag), urnFormRelazione.getUrn().toString()));

						} else {
							// L'utente non ha riempito i campi per bene!
							e.setFonte(null);
						}
					} else {
						// Esiste gi? una relazione associata alla vigenza,
						// perci? modifichiamo quella,
						// in modo da mantenere lo stesso ID.
						e.getFonte().setTagTipoRelazione(tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().toString());
						e.getFonte().setLink(urnFormRelazione.getUrn().toString());
					}
					
				} else {
					// L'utente non ha inserito una data di fine, quindi non c'?
					// una relazione.
					e.setFonte(null);
					
				}
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_REMOVE) {
				e = null;
				dataDatiEvento.set(null);
				tagTipoEvento.setText("");
//				f = null;
				tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);
				urnFormRelazione.setUrn(new Urn());
				tagEffettoSottoFormDatiEvento.setSelectedItem(null);
			}
		}

		public Object getElement() {
			if (!checkData())
				return null;
			else
				return e;
		}

		public void setElement(Object object) {
			e = (Evento) object;
			
			dataDatiEvento.set(UtilDate.normToDate(e.getData()));
			tagTipoEvento.setText(e.getTipoEvento());
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(e.getFonte().getTagTipoRelazione());
			try {
				urnFormRelazione.setUrn(new Urn(e.getFonte().getLink()));
			} catch (ParseException e) {
			}
			tagEffettoSottoFormDatiEvento.setSelectedItem(e.getEffetto());
			
		
		}

		public void clearFields() {

			dataDatiEvento.set(null);
			tagTipoEvento.setText("");
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);			
			urnFormRelazione.setUrn(new Urn());				
			tagEffettoSottoFormDatiEvento.setSelectedItem(null);
						
		}

		public boolean checkData() {
			//da fare
			return true;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public Dimension getPreferredSize() {
			return new Dimension(800, 150);
		}
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {

		form = (Form) serviceManager.lookup(Form.class);
		form.setName("editor.form.meta.ciclodivita.eventi");

		sottoFormEvento = (Form) serviceManager.lookup(Form.class);
		sottoFormDatiEvento = (Form) serviceManager.lookup(Form.class);

		eventi_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);

		dataDatiEvento = (DateForm) serviceManager.lookup(DateForm.class);
			
		urnFormRelazione = (UrnForm) serviceManager.lookup(UrnForm.class);
		
		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		sottoFormDatiEvento.setMainComponent(getClass().getResourceAsStream("DatiEvento.jfrm"));
		sottoFormDatiEvento.replaceComponent("editor.meta.ciclodivita.evento.data", dataDatiEvento.getAsComponent());
		sottoFormDatiEvento.replaceComponent("editor.form.meta.urn", urnFormRelazione.getAsComponent());
		
		
		tagTipoEvento = (JTextField) sottoFormDatiEvento.getComponentByName("editor.form.meta.ciclodivita.evento.tipoeventi");
		
		tagTipoRelazioneSottoFormDatiEvento = (JComboBox) sottoFormDatiEvento.getComponentByName("editor.meta.ciclodivita.evento.tiporelazione");
		tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
		tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
		tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
		tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
		tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
		tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");
		
		tagEffettoSottoFormDatiEvento = (JComboBox) sottoFormDatiEvento.getComponentByName("editor.form.meta.ciclodivita.evento.tipoeffetto");
		tagEffettoSottoFormDatiEvento.addItem("normativo");
		tagEffettoSottoFormDatiEvento.addItem("implementativo");
		
		
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

	public void actionPerformed(ActionEvent e) {
				
		try {
			form.replaceComponent("editor.form.meta.ciclodivita.eventi", sottoFormEvento.getAsComponent());
		} catch (FormException exc) {
			// TODO fare qualcosa?
		}
	}

	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	public boolean openForm() {

		Vector v = new Vector();
		if (eventi != null) {
			for (int i = 0; i < eventi.length; i++) {
				v.add(eventi[i]);
			}
			dataDatiEvento.set(UtilDate.normToDate(eventi[0].getData()));
						
			Relazione fonte = eventi[0].getFonte();
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(fonte.getTagTipoRelazione());
			try {
				urnFormRelazione.setUrn(new Urn(fonte.getLink()));
			} catch (ParseException e) {
			}
			
			eventi_listtextfield.setListElements(v);
				
		}
		
		form.setSize(740, 500);
		form.showDialog();
		return form.isOk();
	}

	// ////////////////////////////////////////////// FormVerifier Interface
	public boolean verifyForm() {
		
//			if (dataDatiEvento.getAsDate() == null) {
//				errorMessage = "editor.form.meta.descrittori.vigenza.msg.err.datainiziovuota";
//				return false;
//			}
		//da fare
		
		return true;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Evento[] getEventi() {
		
		Vector v = eventi_listtextfield.getListElements();
		eventi = new Evento[v.size()];
		v.toArray(eventi);
		return eventi;
	
	}

	public void setEventi(Evento[] eventi) {
		this.eventi = eventi;
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
	 * Restituisce un ID univoco per una nuova vigenza
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
			prefix = "rh";
		}else if (nomeTag.equals("allegatodi")) {
			prefix = "rd";
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

	public Evento getSeletedEvento() {
		// TODO Auto-generated method stub
		return (Evento)eventi_listtextfield.getSelectedItem();
	}

}

	
