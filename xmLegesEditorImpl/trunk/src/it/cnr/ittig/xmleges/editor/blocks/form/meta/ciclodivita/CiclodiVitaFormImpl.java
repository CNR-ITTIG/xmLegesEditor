package it.cnr.ittig.xmleges.editor.blocks.form.meta.ciclodivita;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementEvent;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementListener;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Pubblicazione;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaEventoForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;

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

	// Form riepilogo
	Form form;

	String tipoDTD;

	String tipoDocumento;

	JTextField report_tipoDocumento;

	DateForm report_dataPubblicazione;

	JTextField report_numeroPubblicazione;

	JButton pubblicazioniButton;

	JButton aliasButton;

	JButton vigenzaButton;

	JButton relazioniButton;

	String[] aliases;

	JList aliasList;

	Pubblicazione pubblicazione;

	Pubblicazione[] altrePubblicazioni;

	JList altrePubblicazioniList;

	Relazione[] relazioniUlteriori;

	JList relazioniList;

	// Form pubblicazioni
	Form formPubblicazioni;

	Form sottoFormDatiPubblicazione;

	JTextField pub_numeroPubblicazione;

	DateForm pub_dataPubblicazione;

	ListTextField pub_listtextfield;

	DateForm dataSottoFormDatiPubblicazione;

	JComboBox tagSottoFormDatiPubblicazione;

	// Form alias
	Form formAlias;

	ListTextField alias_listtextfield;

	// Form relazioni
	Form formRelazioni;

	Form sottoFormDatiRelazione;

	ListTextField rel_listtextfield;

	JComboBox tagSottoFormDatiRelazione;

	UrnForm urnForm;

	// Form vigenze
	CiclodiVitaEventoForm formVigenze;

	Evento[] vigenze;

	JList vigenzeList;

	/**
	 * Editor per il ListTextField della lista degli Alias
	 */
	private class AliasListTextFieldEditor implements ListTextFieldEditor {
		javax.swing.JTextField textField = new javax.swing.JTextField();

		public Component getAsComponent() {
			return textField;
		}

		public Object getElement() {
			return textField.getText();
		}

		public void setElement(Object object) {
			textField.setText(object.toString());
		}

		public void clearFields() {
			textField.setText(null);
		}

		public boolean checkData() {
			return (textField.getText() != null && !"".equals(textField.getText().trim()));
		}

		public String getErrorMessage() {
			return "editor.form.meta.descrittori.msg.err.datialias";
		}

		public Dimension getPreferredSize() {
			return new Dimension(600, 150);
		}
	}

	/**
	 * Editor per il ListTextField della lista delle pubblicazioni
	 */
	private class PubListTextFieldEditor implements ListTextFieldEditor {
		Form form;

		DateForm data;

		public PubListTextFieldEditor(Form form, DateForm data) {
			this.form = form;
			this.data = data;
		}

		public Component getAsComponent() {
			return form.getAsComponent();
		}

		public Object getElement() {

			String nometag = (String) ((JComboBox) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.nometag"))
					.getSelectedItem();
			String numero = ((JTextField) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.numero")).getText();

			return new Pubblicazione(nometag, "GU", numero, data.getAsYYYYMMDD());
		}

		public void setElement(Object object) {
			Pubblicazione p = (Pubblicazione) object;
			((JComboBox) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.nometag")).setSelectedItem(p.getTag());
			((JTextField) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.numero")).setText(p.getNum());
			data.set(UtilDate.normToDate(p.getNorm()));
		}

		public void clearFields() {
			((JComboBox) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.nometag")).setSelectedItem(null);
			((JTextField) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.numero")).setText(null);
			data.set(null);
		}

		public boolean checkData() {
			String nometag = (String) ((JComboBox) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.nometag"))
					.getSelectedItem();
			String numero = ((JTextField) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.numero")).getText();

			if (data.getAsDate() == null || nometag == null || numero == null || "".equals(nometag.trim()) || "".equals(numero.trim())) {
				return false;
			}
			return true;
		}

		public String getErrorMessage() {
			return "editor.form.meta.descrittori.msg.err.datipubblicazione";
		}

		public Dimension getPreferredSize() {
			return new Dimension(600, 150);
		}
	}

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

			String nomeTag = (String) ((JComboBox) form.getComponentByName("editor.meta.descrittori.relazioni.tiporelazione")).getSelectedItem();
			Urn urn = urnForm.getUrn();

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
					r.setTag(nomeTag);
					r.setLink(urn.toString());
				}
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_REMOVE) {
				r = null;
				((JComboBox) form.getComponentByName("editor.meta.descrittori.relazioni.tiporelazione")).setSelectedItem(null);
				urnForm.setUrn(new Urn());
			}
		}

		public Object getElement() {
			return r;
		}

		public void setElement(Object object) {
			r = (Relazione) object;
			((JComboBox) form.getComponentByName("editor.meta.descrittori.relazioni.tiporelazione")).setSelectedItem(r.getTag());
			try {
				urnForm.setUrn(new Urn(r.getLink()));
			} catch (ParseException e) {
			}
		}

		public void clearFields() {
			((JComboBox) form.getComponentByName("editor.meta.descrittori.relazioni.tiporelazione")).setSelectedItem(null);
			urnForm.setUrn(new Urn());
		}

		public boolean checkData() {
			String nomeTag = (String) ((JComboBox) form.getComponentByName("editor.meta.descrittori.relazioni.tiporelazione")).getSelectedItem();
			Urn urn = urnForm.getUrn();
			if (urn == null || !urn.isValid() || nomeTag == null || "".equals(nomeTag.trim()) || "".equals(urn.toString().trim())) {
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
		formPubblicazioni = (Form) serviceManager.lookup(Form.class);
		formAlias = (Form) serviceManager.lookup(Form.class);
		formRelazioni = (Form) serviceManager.lookup(Form.class);
		sottoFormDatiPubblicazione = (Form) serviceManager.lookup(Form.class);
		sottoFormDatiRelazione = (Form) serviceManager.lookup(Form.class);

		urnForm = (UrnForm) serviceManager.lookup(UrnForm.class);

		formVigenze = (CiclodiVitaEventoForm) serviceManager.lookup(CiclodiVitaEventoForm.class);

		report_dataPubblicazione = (DateForm) serviceManager.lookup(DateForm.class);
		pub_dataPubblicazione = (DateForm) serviceManager.lookup(DateForm.class);
		dataSottoFormDatiPubblicazione = (DateForm) serviceManager.lookup(DateForm.class);

		pub_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
		alias_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
		rel_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("MetaDescrittori.jfrm"));
		form.replaceComponent("editor.meta.descrittori.riepilogo.datapubblicazione", report_dataPubblicazione.getAsComponent());
		report_dataPubblicazione.getAsComponent().setEnabled(false);
		report_numeroPubblicazione = (JTextField) form.getComponentByName("editor.meta.descrittori.riepilogo.numeropubblicazione");
		report_tipoDocumento = (JTextField) form.getComponentByName("editor.meta.descrittori.riepilogo.tipodocumento");
		form.setName("editor.form.meta.descrittori.riepilogo");

		sottoFormDatiPubblicazione.setMainComponent(getClass().getResourceAsStream("DatiPubblicazione.jfrm"));
		sottoFormDatiPubblicazione.replaceComponent("editor.meta.descrittori.pubblicazioni.datipubblicazione.data", dataSottoFormDatiPubblicazione
				.getAsComponent());

		sottoFormDatiRelazione.setMainComponent(getClass().getResourceAsStream("DatiRelazione.jfrm"));
		sottoFormDatiRelazione.replaceComponent("editor.meta.descrittori.relazioni.urn", urnForm.getAsComponent());

		formPubblicazioni.setMainComponent(getClass().getResourceAsStream("Pubblicazioni.jfrm"));
		formPubblicazioni.replaceComponent("editor.meta.descrittori.pubblicazioni.datapubblicazione", pub_dataPubblicazione.getAsComponent());
		formPubblicazioni.replaceComponent("editor.meta.descrittori.pubblicazioni.altre", pub_listtextfield.getAsComponent());
		formPubblicazioni.setSize(680, 400);
		formPubblicazioni.setName("editor.form.meta.descrittori.pubblicazioni");
		pub_listtextfield.setEditor(new PubListTextFieldEditor(sottoFormDatiPubblicazione, dataSottoFormDatiPubblicazione));
		pub_numeroPubblicazione = (JTextField) formPubblicazioni.getComponentByName("editor.meta.descrittori.pubblicazioni.numeropubblicazione");
		tagSottoFormDatiPubblicazione = (JComboBox) sottoFormDatiPubblicazione
				.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.nometag");
		tagSottoFormDatiPubblicazione.addItem("ripubblicazione");
		tagSottoFormDatiPubblicazione.addItem("errata");
		tagSottoFormDatiPubblicazione.addItem("rettifica");

		formAlias.setMainComponent(getClass().getResourceAsStream("Alias.jfrm"));
		formAlias.replaceComponent("editor.meta.descrittori.alias.listtextfield", alias_listtextfield.getAsComponent());
		formAlias.setSize(650, 400);
		formAlias.setName("editor.form.meta.descrittori.alias");
		alias_listtextfield.setEditor(new AliasListTextFieldEditor());

		formRelazioni.setMainComponent(getClass().getResourceAsStream("Relazioni.jfrm"));
		formRelazioni.replaceComponent("editor.meta.descrittori.relazioni.listtextfield", rel_listtextfield.getAsComponent());
		formRelazioni.setName("editor.form.meta.descrittori.relazioni");
		formRelazioni.setSize(650, 400);

		RelListTextFieldEditor tfe = new RelListTextFieldEditor(sottoFormDatiRelazione);
		rel_listtextfield.setEditor(tfe);
		rel_listtextfield.addListTextFieldElementListener(tfe);

		tagSottoFormDatiRelazione = (JComboBox) sottoFormDatiRelazione.getComponentByName("editor.meta.descrittori.relazioni.tiporelazione");
		tagSottoFormDatiRelazione.addItem("originale");
		tagSottoFormDatiRelazione.addItem("attiva");
		tagSottoFormDatiRelazione.addItem("giurisprudenza");

		// Report items
		aliasList = (JList) form.getComponentByName("editor.meta.descrittori.riepilogo.alias");
		altrePubblicazioniList = (JList) form.getComponentByName("editor.meta.descrittori.riepilogo.altrepubblicazioni");
		relazioniList = (JList) form.getComponentByName("editor.meta.descrittori.riepilogo.altrerelazioni");
		vigenzeList = (JList) form.getComponentByName("editor.meta.descrittori.riepilogo.vigenze");

		pubblicazioniButton = (JButton) form.getComponentByName("editor.meta.descrittori.riepilogo.pubblicazioni_btn");
		aliasButton = (JButton) form.getComponentByName("editor.meta.descrittori.riepilogo.alias_btn");
		vigenzaButton = (JButton) form.getComponentByName("editor.meta.descrittori.riepilogo.vigenza_btn");
		relazioniButton = (JButton) form.getComponentByName("editor.meta.descrittori.riepilogo.relazioni_btn");

		pubblicazioniButton.addActionListener(this);
		aliasButton.addActionListener(this);
		vigenzaButton.addActionListener(this);
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
		if (e.getSource().equals(pubblicazioniButton)) { // PUBBLICAZIONI
			Vector v = new Vector();
			if (altrePubblicazioni != null) {
				for (int i = 0; i < altrePubblicazioni.length; i++) {
					v.add(altrePubblicazioni[i]);
				}
			}
			pub_listtextfield.setListElements(v);
			pub_numeroPubblicazione.setText(pubblicazione.getNum());
			pub_dataPubblicazione.set(UtilDate.normToDate(pubblicazione.getNorm()));
			formPubblicazioni.showDialog();
			if (formPubblicazioni.isOk()) {
				report_dataPubblicazione.set(UtilDate.normToDate(pub_dataPubblicazione.getAsYYYYMMDD()));
				report_numeroPubblicazione.setText(pub_numeroPubblicazione.getText());
				altrePubblicazioni = new Pubblicazione[pub_listtextfield.getListElements().size()];
				pub_listtextfield.getListElements().toArray(altrePubblicazioni);
				altrePubblicazioniList.setListData(altrePubblicazioni);
				pubblicazione = new Pubblicazione("pubblicazione", "GU", pub_numeroPubblicazione.getText(), pub_dataPubblicazione.getAsYYYYMMDD());
			}
		} else if (e.getSource().equals(aliasButton)) { // ALIAS
			Vector v = new Vector();
			if (aliases != null) {
				for (int i = 0; i < aliases.length; i++) {
					v.add(aliases[i]);
				}
			}
			alias_listtextfield.setListElements(v);

			formAlias.showDialog();

			if (formAlias.isOk()) {
				aliases = new String[alias_listtextfield.getListElements().size()];
				alias_listtextfield.getListElements().toArray(aliases);
				aliasList.setListData(aliases);
			}
		} else if (e.getSource().equals(vigenzaButton)) { // VIGENZE
			formVigenze.setVigenze(vigenze);
			formVigenze.setRelazioniUlteriori(relazioniUlteriori);
			formVigenze.setTipoDocumento(tipoDocumento);
			formVigenze.setTipoDTD(tipoDTD);
			if (formVigenze.openForm()) {
				vigenze = formVigenze.getVigenze();
				vigenzeList.setListData(vigenze);
				setTipoDocumento(formVigenze.getTipoDocumento());
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

	public Pubblicazione getPubblicazione() {
		return pubblicazione;
	}

	public Evento[] getEventi() {
		return vigenze;
	}

	public Relazione[] getRelazioniUlteriori() {
		return relazioniUlteriori;
	}

	public String[] getAlias() {
		return aliases;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public Pubblicazione[] getAltrePubblicazioni() {
		return altrePubblicazioni;
	}

	public void setAltrePubblicazioni(Pubblicazione[] altrePubblicazioni) {
		if (altrePubblicazioni != null) {
			this.altrePubblicazioni = altrePubblicazioni;
			altrePubblicazioniList.setListData(altrePubblicazioni);
		}
	}

	public void setAlias(String[] aliases) {
		if (aliases != null) {
			this.aliases = aliases;
			aliasList.setListData(aliases);
		}
	}

	public void setRelazioniUlteriori(Relazione[] relazioniUlteriori) {
		if (relazioniUlteriori != null) {
			this.relazioniUlteriori = relazioniUlteriori;
			relazioniList.setListData(relazioniUlteriori);
		}
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
		report_tipoDocumento.setText(tipoDocumento);
	}

	public void setTipoDTD(String tipoDTD) {
		this.tipoDTD = tipoDTD;
	}

	public void setEventi(Evento[] eventi) {
		this.vigenze = eventi;
		vigenzeList.setListData(eventi);
	}

	public void setPubblicazione(Pubblicazione pubblicazione) {
		this.pubblicazione = pubblicazione;
		report_dataPubblicazione.set(UtilDate.normToDate(pubblicazione.getNorm()));
		report_numeroPubblicazione.setText(pubblicazione.getNum());
	}

	/**
	 * Restituisce un ID univoco per una nuova relazione.
	 */
	private String calcolaIDRelazione(String nomeTag) {

		// TODO sarebbe da implementare in maniera un po' pi? elegante...
		// TODO e magari spostare nelle utils

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
		for (int i = 0; i < vigenze.length; i++) {
			if (vigenze[i].getFonte() != null) {
				try {
					String s = vigenze[i].getFonte().getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(vigenze[i].getFonte().getId().substring(prefix.length()));
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
