package it.cnr.ittig.xmleges.editor.blocks.form.meta.ciclodivita;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementEvent;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementListener;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaEventoForm;
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
public class CiclodiVitaFormImpl implements CiclodiVitaForm, Loggable, Serviceable, Initializable, ActionListener {

	Logger logger;

	//   Ciclodivita.jfrm
	Form form;
	
	//	 Relazioni.jfrm
	Form formRelazioni;

	//   DatiRelazione.jfrm
	Form sottoFormDatiRelazione;
	
	
	CiclodiVitaEventoForm formEventi;

	String tipoDTD;

	String tipoDocumento;

	JButton eventoButton;

	JButton relazioniButton;
	
	JList eventiList;

	JList relazioniList;
	
	Evento[] eventi;
	
	Relazione[] relazioniUlteriori;
	
	ListTextField rel_listtextfield;

	JComboBox tagSottoFormDatiRelazione;
	
	JComboBox tagEffettoSottoFormDatiRelazione;

	UrnForm urnFormRelazioni;



	/**
	 * Editor per il ListTextField della lista delle relazioni
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
					r = new Relazione(nomeTag, calcolaIDRelazione(nomeTag), urn.toString());
				}
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_MODIFY) {

				if (!checkData()) {
					r = null;
				} else {
					r.setTagTipoRelazione(nomeTag);
					r.setLink(urn.toString());
				}
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_REMOVE) {
				r = null;
				tagSottoFormDatiRelazione.setSelectedItem(null);
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
		}

		public void clearFields() {
			tagSottoFormDatiRelazione.setSelectedItem(null);
			urnFormRelazioni.setUrn(new Urn());
		}

		public boolean checkData() {
			String nomeTag = (String) (tagSottoFormDatiRelazione).getSelectedItem();
			Urn urn = urnFormRelazioni.getUrn();
			if (urn == null || !urn.isValid() || tagSottoFormDatiRelazione == null || "".equals(nomeTag.trim()) || "".equals(urn.toString().trim())) {
				return false;
			}
			return true;
		}

		public String getErrorMessage() {
			return "editor.form.meta.descrittori.msg.err.datirelazione";
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
		formEventi = (CiclodiVitaEventoForm) serviceManager.lookup(CiclodiVitaEventoForm.class);

		rel_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		
		
		form.setMainComponent(getClass().getResourceAsStream("Ciclodivita.jfrm"));
				
		relazioniList = (JList) form.getComponentByName("editor.form.meta.ciclodivita.relazioni");
		eventiList = (JList) form.getComponentByName("editor.form.meta.ciclodivita.eventi");
	
		form.setName("editor.form.meta.ciclodivita.riepilogo");
		
		sottoFormDatiRelazione.setMainComponent(getClass().getResourceAsStream("DatiRelazione.jfrm"));
		sottoFormDatiRelazione.replaceComponent("editor.form.meta.urn", urnFormRelazioni.getAsComponent());
		
		formRelazioni.setMainComponent(getClass().getResourceAsStream("Relazioni.jfrm"));
		formRelazioni.replaceComponent("editor.form.meta.ciclodivita.relazioni.listtextfield", rel_listtextfield.getAsComponent());
		formRelazioni.setName("editor.form.meta.ciclodivita.relazioni");
		formRelazioni.setSize(650, 400);

		RelListTextFieldEditor tfe = new RelListTextFieldEditor(sottoFormDatiRelazione);
		rel_listtextfield.setEditor(tfe);
		rel_listtextfield.addListTextFieldElementListener(tfe);

		tagEffettoSottoFormDatiRelazione = (JComboBox) sottoFormDatiRelazione.getComponentByName("editor.form.meta.ciclodivita.relazione.tipoeffetto");
		tagEffettoSottoFormDatiRelazione.addItem("normativo");
		tagEffettoSottoFormDatiRelazione.addItem("implementativo");
		tagEffettoSottoFormDatiRelazione.setEnabled(false);
		
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
					if (tagSottoFormDatiRelazione.getSelectedItem().equals("giurisprudenza")){
						tagEffettoSottoFormDatiRelazione.setEnabled(true);
						
					}else
						tagEffettoSottoFormDatiRelazione.setEnabled(false);
				}
				
			}
			
		});		
		
		
		
		
		

		eventoButton = (JButton) form.getComponentByName("editor.form.meta.ciclodivita.riepilogo.eventi_btn");
		relazioniButton = (JButton) form.getComponentByName("editor.form.meta.ciclodivita.riepilogo.relazioni_btn");
		eventoButton.addActionListener(this);
		relazioniButton.addActionListener(this);
	}

	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	public boolean openForm() {
		form.setSize(600, 650);
		form.showDialog();

		return form.isOk();
	}

	public void actionPerformed(ActionEvent e) {				
		 if (e.getSource().equals(eventoButton)) { // EVENTI
			formEventi.setEventi(eventi);
			setEventi(eventi);
			setRelazioniUlteriori(relazioniUlteriori);
			formEventi.setTipoDTD(tipoDTD);
			
			if (formEventi.openForm()) {
				
				eventi = formEventi.getEventi();
				eventiList.setListData(eventi);
				
			}
		} else if (e.getSource().equals(relazioniButton)) { // RELAZIONI
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

	
	public Evento[] getEventi() {
		return eventi;
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

	public void setEventi(Evento[] eventi) {
		this.eventi = eventi;		
		eventiList.setListData(eventi);
	}

	

	/**
	 * Restituisce un ID univoco per una nuova relazione.
	 */
	private String calcolaIDRelazione(String nomeTag) {

//		if(true)
//			return "r1";
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

		// e poi nelle relazioni delle vigenze
		for (int i = 0; i < eventi.length; i++) {
			if (eventi[i].getFonte() != null) {
				try {
					String s = eventi[i].getFonte().getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(eventi[i].getFonte().getId().substring(prefix.length()));
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

	
}
