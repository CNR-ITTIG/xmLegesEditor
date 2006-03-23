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
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaEventoForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.JComboBox;

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

	Form form;

	Form sottoformVigenzaSingola;

	Form sottoformMultivigenza;

	Form datiVigenza;

	JComboBox tipoVigenzaComboBox;

	JComboBox tipoRelazione;

	UrnForm urnFormMultivigenza;

	UrnForm urnFormVigenzaSingola;

	ListTextField vig_listtextfield;

	DateForm dataInizio;

	DateForm dataFine;

	DateForm dataInizioDatiVigenza;

	DateForm dataFineDatiVigenza;

	Evento[] vigenze;

	Relazione[] relazioniUlteriori;

	String tipoDocumento;

	String tipoDTD;

	String errorMessage = "";

	/**
	 * Editor per il ListTextField con la lista delle vigenze
	 */
	private class VigListTextFieldEditor implements ListTextFieldEditor, ListTextFieldElementListener {
		Evento v;

		Relazione r;

		Form form;

		UrnForm urnForm;

		DateForm inizio;

		DateForm fine;

		String errorMessage = "";

		public VigListTextFieldEditor(Form form, UrnForm urnForm, DateForm inizio, DateForm fine) {
			this.form = form;
			this.urnForm = urnForm;
			this.inizio = inizio;
			this.fine = fine;
		}

		public Component getAsComponent() {
			return form.getAsComponent();
		}

		public void elementChanged(ListTextFieldElementEvent e) {

//			int eventID = e.getID();
//
//			if (eventID == ListTextFieldElementEvent.ELEMENT_ADD) {
//
//				v = new Evento(calcolaIDVigenza(), inizio.getAsYYYYMMDD());
//				v.setFine(fine.getAsYYYYMMDD());
//
//				// Se la data di fine ? riempita, inseriamo anche la relazione
//				if (v.getFine() != null) {
//					String nomeTag = (String) ((JComboBox) form.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).getSelectedItem();
//					if (nomeTag != null && urnForm.getUrn() != null) {
//						r = new Relazione(nomeTag, calcolaIDRelazione(nomeTag), urnForm.getUrn().toString());
//						v.setFonte(r);
//					} else {
//						// L'utente non ha riempito i campi per bene!
//						r = null;
//					}
//				} else {
//					r = null;
//					v.setFonte(null);
//				}
//			} else if (eventID == ListTextFieldElementEvent.ELEMENT_MODIFY) {
//
//				v.setInizio(inizio.getAsYYYYMMDD());
//				v.setFine(fine.getAsYYYYMMDD());
//
//				// Se la data di fine ? riempita, inseriamo anche la relazione
//				if (v.getFine() != null) {
//					if (r == null) {
//						// Non esiste una relazione gi? associata alla vigenza,
//						// ne creiamo una nuova
//						String nomeTag = (String) ((JComboBox) form.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).getSelectedItem();
//						if (nomeTag != null && urnForm.getUrn() != null) {
//							r = new Relazione(nomeTag, calcolaIDRelazione(nomeTag), urnForm.getUrn().toString());
//						} else {
//							// L'utente non ha riempito i campi per bene!
//							r = null;
//						}
//					} else {
//						// Esiste gi? una relazione associata alla vigenza,
//						// perci? modifichiamo quella,
//						// in modo da mantenere lo stesso ID.
//						r.setTag(((JComboBox) form.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).getSelectedItem().toString());
//						r.setLink(urnForm.getUrn().toString());
//					}
//					v.setFonte(r);
//				} else {
//					// L'utente non ha inserito una data di fine, quindi non c'?
//					// una relazione.
//					r = null;
//					v.setFonte(null);
//				}
//			} else if (eventID == ListTextFieldElementEvent.ELEMENT_REMOVE) {
//				v = null;
//				inizio.set(null);
//				fine.set(null);
//				r = null;
//				((JComboBox) form.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).setSelectedItem(null);
//				urnForm.setUrn(new Urn());
//			}
		}

		public Object getElement() {
			if (!checkData())
				return null;
			else
				return v;
		}

		public void setElement(Object object) {
			v = (Evento) object;
			r = v.getFonte();
//			inizio.set(UtilDate.normToDate(v.getInizio()));
//			fine.set(UtilDate.normToDate(v.getFine()));
			if (r != null) {
				((JComboBox) form.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).setSelectedItem(r.getTag());
				try {
					urnForm.setUrn(new Urn(r.getLink()));
				} catch (ParseException e) {
				}
			} else {
				((JComboBox) form.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).setSelectedItem(null);
				urnForm.setUrn(new Urn());
			}
		}

		public void clearFields() {
			inizio.set(null);
			fine.set(null);
			((JComboBox) form.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).setSelectedItem(null);
			urnForm.setUrn(new Urn());
		}

		public boolean checkData() {
			if (inizio.getAsDate() == null) {
				errorMessage = "editor.form.meta.descrittori.vigenza.msg.err.datainiziovuota";
				return false;
			}
			if (fine.getAsDate() != null && !fine.getAsDate().after(inizio.getAsDate())) {
				errorMessage = "editor.form.meta.descrittori.vigenza.msg.err.datamaggiore";
				return false;
			}
			if (fine.getAsDate() != null) {
				String nomeTag = (String) ((JComboBox) form.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).getSelectedItem();
				if (nomeTag == null || urnForm.getUrn() == null || !urnForm.getUrn().isValid()) {
					errorMessage = "editor.form.meta.descrittori.vigenza.msg.err.relazione";
					return false;
				}
			}
			errorMessage = "";
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
		form.setName("editor.form.meta.descrittori.vigenze");

		sottoformMultivigenza = (Form) serviceManager.lookup(Form.class);
		sottoformVigenzaSingola = (Form) serviceManager.lookup(Form.class);
		datiVigenza = (Form) serviceManager.lookup(Form.class);

		vig_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);

		dataInizio = (DateForm) serviceManager.lookup(DateForm.class);
		dataFine = (DateForm) serviceManager.lookup(DateForm.class);

		dataInizioDatiVigenza = (DateForm) serviceManager.lookup(DateForm.class);
		dataFineDatiVigenza = (DateForm) serviceManager.lookup(DateForm.class);

		urnFormMultivigenza = (UrnForm) serviceManager.lookup(UrnForm.class);
		urnFormVigenzaSingola = (UrnForm) serviceManager.lookup(UrnForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {

		// Form che viene utilizzato quando il documento non ? multivigente
		sottoformVigenzaSingola.setMainComponent(getClass().getResourceAsStream("VigenzaSingola.jfrm"));
		sottoformVigenzaSingola.getAsComponent().setName("editor.meta.descrittori.vigenze.periodi");
		sottoformVigenzaSingola.replaceComponent("editor.meta.descrittori.vigenza.datainizio", dataInizio.getAsComponent());
		sottoformVigenzaSingola.replaceComponent("editor.meta.descrittori.vigenza.datafine", dataFine.getAsComponent());
		sottoformVigenzaSingola.replaceComponent("editor.meta.descrittori.vigenza.urn", urnFormVigenzaSingola.getAsComponent());
		tipoRelazione = (JComboBox) sottoformVigenzaSingola.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione");
		tipoRelazione.addItem("passiva");
		tipoRelazione.addItem("giurisprudenza");

		// Form che viene usato come editor nel ListTextField quando il doc ?
		// multivigente
		datiVigenza.setMainComponent(getClass().getResourceAsStream("VigenzaSingola.jfrm"));
		datiVigenza.replaceComponent("editor.meta.descrittori.vigenza.datainizio", dataInizioDatiVigenza.getAsComponent());
		datiVigenza.replaceComponent("editor.meta.descrittori.vigenza.datafine", dataFineDatiVigenza.getAsComponent());
		datiVigenza.replaceComponent("editor.meta.descrittori.vigenza.urn", urnFormMultivigenza.getAsComponent());
		JComboBox tipoVigenza2 = (JComboBox) datiVigenza.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione");
		tipoVigenza2.addItem("passiva");
		tipoVigenza2.addItem("giurisprudenza");
		tipoVigenza2.setSelectedItem(null);

		sottoformMultivigenza.setMainComponent(getClass().getResourceAsStream("Multivigenza.jfrm"));

		VigListTextFieldEditor vtfe = new VigListTextFieldEditor(datiVigenza, urnFormMultivigenza, dataInizioDatiVigenza, dataFineDatiVigenza);
		vig_listtextfield.setEditor(vtfe);
		vig_listtextfield.addListTextFieldElementListener(vtfe);

		sottoformMultivigenza.replaceComponent("editor.meta.descrittori.vigenza.periodi.multivigenza", vig_listtextfield.getAsComponent());
		sottoformMultivigenza.getAsComponent().setName("editor.meta.descrittori.vigenze.periodi");

		form.setMainComponent(getClass().getResourceAsStream("Vigenze.jfrm"));
		form.replaceComponent("editor.meta.descrittori.vigenze.periodi", sottoformMultivigenza.getAsComponent());
		form.addFormVerifier(this);

		tipoVigenzaComboBox = (JComboBox) form.getComponentByName("editor.meta.descrittori.vigenze.tipo");
		tipoVigenzaComboBox.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(tipoVigenzaComboBox)) {
			int sel = tipoVigenzaComboBox.getSelectedIndex();
			if (sel == 4) {
				try {
					form.replaceComponent("editor.meta.descrittori.vigenze.periodi", sottoformMultivigenza.getAsComponent());
				} catch (FormException exc) {
					// TODO fare qualcosa?
				}
			} else {
				try {
					form.replaceComponent("editor.meta.descrittori.vigenze.periodi", sottoformVigenzaSingola.getAsComponent());
					switch (sel) {
					case 0:
						abilitaFineVigenza(false);
						break;
					case 1:
						abilitaFineVigenza(true);
						break;
					case 2:
						abilitaFineVigenza(false);
						break;
					case 3:
						abilitaFineVigenza(true);
						break;
					}
				} catch (FormException exc) {
					// TODO fare qualcosa?
				}
			}
		}
	}

	private void abilitaFineVigenza(boolean b) {
		dataFine.getAsComponent().setEnabled(b);
		sottoformVigenzaSingola.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione").setEnabled(b);
		urnFormVigenzaSingola.getAsComponent().setEnabled(b);
		((JComboBox) sottoformVigenzaSingola.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).setSelectedIndex(0);
		if (b == false) {
			dataFine.set(null);
			((JComboBox) sottoformVigenzaSingola.getComponentByName("editor.meta.descrittori.vigenza.tiporelazione")).setSelectedItem(null);
			urnFormVigenzaSingola.setUrn(new Urn());
		}
	}

	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	public boolean openForm() {

		tipoVigenzaComboBox.removeAllItems();
		tipoVigenzaComboBox.addItem("Testo originale e ancora vigente in questa forma");
		tipoVigenzaComboBox.addItem("Testo originale e non pi? vigente in questa forma");
		tipoVigenzaComboBox.addItem("Testo aggiornato e vigente a tutt'oggi");
		tipoVigenzaComboBox.addItem("Testo aggiornato ma non pi? vigente in questa forma");
		if (!tipoDTD.equals("nirbase.dtd")) {
			tipoVigenzaComboBox.addItem("Multivigente");
		}
		if (tipoDocumento.equals("multivigente")) {
			tipoVigenzaComboBox.setSelectedIndex(4);
			Vector v = new Vector();
			if (vigenze != null) {
				for (int i = 0; i < vigenze.length; i++) {
					v.add(vigenze[i]);
				}
			}
			vig_listtextfield.setListElements(v);
		} else {
//			dataInizio.set(UtilDate.normToDate(vigenze[0].getInizio()));
//			if (vigenze[0].hasFineFonte()) {
//				dataFine.set(UtilDate.normToDate(vigenze[0].getFine()));
//				Relazione r = vigenze[0].getFonte();
//				tipoRelazione.setSelectedItem(r.getTag());
//				try {
//					urnFormVigenzaSingola.setUrn(new Urn(r.getLink()));
//				} catch (ParseException e) {
//				}
//			}
//			if (tipoDocumento.equals("originale")) {
//				if (vigenze[0].hasFineFonte())
//					tipoVigenzaComboBox.setSelectedIndex(1);
//				else
//					tipoVigenzaComboBox.setSelectedIndex(0);
//			} else if (tipoDocumento.equals("vigente")) {
//				if (vigenze[0].hasFineFonte())
//					tipoVigenzaComboBox.setSelectedIndex(3);
//				else
//					tipoVigenzaComboBox.setSelectedIndex(2);
//			}
		}
		form.setSize(740, 500);
		form.showDialog();
		return form.isOk();
	}

	// ////////////////////////////////////////////// FormVerifier Interface
	public boolean verifyForm() {
		if (!getTipoDocumento().equals("multivigente")) {
			if (dataInizio.getAsDate() == null) {
				errorMessage = "editor.form.meta.descrittori.vigenza.msg.err.datainiziovuota";
				return false;
			}
			int sel = tipoVigenzaComboBox.getSelectedIndex();
			if (sel == 1 || sel == 3) { // Controlla data fine e urn
				if (dataFine.getAsDate() == null) {
					errorMessage = "editor.form.meta.descrittori.vigenza.msg.err.datafinevuota";
					return false;
				}
				if (dataFine.getAsDate() != null && !dataFine.getAsDate().after(dataInizio.getAsDate())) {
					errorMessage = "editor.form.meta.descrittori.vigenza.msg.err.datamaggiore";
					return false;
				}
				if (tipoRelazione.getSelectedItem() == null || !urnFormVigenzaSingola.getUrn().isValid()) {
					errorMessage = "editor.form.meta.descrittori.vigenza.msg.err.relazione";
					return false;
				}
			}
		}
		return true;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Evento[] getVigenze() {
		if (getTipoDocumento().equals("multivigente")) {
			Vector v = vig_listtextfield.getListElements();
			vigenze = new Evento[v.size()];
			v.toArray(vigenze);
			return vigenze;
		} else {
			Evento[] newVigenze = new Evento[1];
			Relazione r = null;
			if (dataFine.getAsDate() != null) {
				String nomeTag = tipoRelazione.getSelectedItem().toString();
				r = new Relazione(nomeTag, calcolaIDRelazione(nomeTag), urnFormVigenzaSingola.getUrn().toString());
			}
//			newVigenze[0] = new Evento(calcolaIDVigenza(), dataInizio.getAsYYYYMMDD(), dataFine.getAsYYYYMMDD(), r);
			return newVigenze;
		}
	}

	public void setVigenze(Evento[] vigenze) {
		this.vigenze = vigenze;
	}

	public void setRelazioniUlteriori(Relazione[] relazioniUlteriori) {
		this.relazioniUlteriori = relazioniUlteriori;
	}

	public String getTipoDocumento() {
		int sel = tipoVigenzaComboBox.getSelectedIndex();
		switch (sel) {
		case 0:
			return "originale";
		case 1:
			return "originale";
		case 2:
			return "vigente";
		case 3:
			return "vigente";
		case 4:
			return "multivigente";
		default:
			return null;
		}
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
	private String calcolaIDVigenza() {
		String prefix = "v";
		String uID = prefix;
		int max = 0;

		// TODO questo codice in pratica duplica quello per le relazioni...

		if (!getTipoDocumento().equals("multivigente")) {
			return "v1";
		}
		// Prendi il massimo degli id delle vigenze. Usiamo il vettore preso
		// dalla ListTextField
		// in modo tale da considerare anche le nuove vigenze inserite.
		Vector vigenzeVect = vig_listtextfield.getListElements();
		for (int i = 0; i < vigenzeVect.size(); i++) {
			Evento v = (Evento) vigenzeVect.elementAt(i);
			if (v.getId() != null) {
				try {
					String s = v.getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(v.getId().substring(prefix.length()));
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
		}

		String uID = prefix;
		int max = 0;

		// TODO questo codice duplica quello di MetaDescrittoriFormImpl!!!
		// TODO sarebbe anche da implementare in maniera un po' pi? elegante...
		// TODO e spostare nelle utils

		// Prendi il massimo degli id nelle relazioni ulteriori...
		for (int i = 0; i < relazioniUlteriori.length; i++) {
			if (relazioniUlteriori[i].getId() != null) {
				try {
					String s = relazioniUlteriori[i].getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(relazioniUlteriori[i].getId().substring(prefix.length()));
						if (idValue.intValue() > max) {
							max = idValue.intValue();
						}
					}
				} catch (IndexOutOfBoundsException exc) {
				}
			}
		}

		// ...e poi nelle relazioni delle vigenze, se il documento ?
		// multivigente.
		// Usiamo il vettore preso dalla ListTextField in modo tale da
		// considerare
		// anche le nuove vigenze inserite.
		if (getTipoDocumento().equals("multivigente")) {
			Vector vigenzeVect = vig_listtextfield.getListElements();
			for (int i = 0; i < vigenzeVect.size(); i++) {
				Evento v = (Evento) vigenzeVect.elementAt(i);
				if (v.getFonte() != null) {
					try {
						String s = v.getFonte().getId().substring(0, prefix.length());
						if (s.equals(prefix)) {
							Integer idValue = Integer.decode(v.getFonte().getId().substring(prefix.length()));
							if (idValue.intValue() > max) {
								max = idValue.intValue();
							}
						}
					} catch (IndexOutOfBoundsException exc) {
					}
				}
			}
		}
		uID += (max + 1);
		return uID;
	}
}
