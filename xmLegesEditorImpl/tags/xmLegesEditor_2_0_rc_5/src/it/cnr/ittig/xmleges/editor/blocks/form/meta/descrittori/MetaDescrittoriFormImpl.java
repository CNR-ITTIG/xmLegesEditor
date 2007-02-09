package it.cnr.ittig.xmleges.editor.blocks.form.meta.descrittori;

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
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Pubblicazione;
import it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MetaDescrittoriForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class MetaDescrittoriFormImpl implements MetaDescrittoriForm, Loggable, Serviceable, Initializable, ActionListener {

	Logger logger;

	// Form riepilogo
	Form form;

	String tipoDTD;

	String tipoPubblicazione;

	//tag della pubblicazione del documento
	JTextField report_tipoPubblicazione;

	DateForm report_dataPubblicazione;

	JTextField report_numeroPubblicazione;

//	tag della redazione del documento
	JTextField report_redazioneNome;

	DateForm report_redazioneData;

	JTextField report_redazioneUrl;
	
	JTextField report_redazioneContributo;
	
	JButton pubblicazioniButton;

	JButton aliasButton;

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

	ListTextField pub_listtextfield;
//	tag delle altre pubblicazioni
	DateForm tagDataSottoFormDatiPubblicazione;

	JComboBox tagAnonimaSottoFormDatiPubblicazione;
	
	JTextField tagTipoSottoFormDatiPubblicazione;
	
	JTextField tagNumeroSottoFormDatiPubblicazione;
	
		
	// Form alias
	Form formAlias;
	
	ListTextField alias_listtextfield;
	
	UrnForm urnForm;

	

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
			
			String tipo = ((JTextField) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.tipo")).getText();

			return new Pubblicazione(nometag, tipo, numero, data.getAsYYYYMMDD());
		}

		public void setElement(Object object) {
			Pubblicazione p = (Pubblicazione) object;
			((JComboBox) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.nometag")).setSelectedItem(p.getTag());
			((JTextField) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.numero")).setText(p.getNum());
			data.set(UtilDate.normToDate(p.getNorm()));
		}

		public void clearFields() {
			((JComboBox) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.nometag")).setSelectedIndex(0);
			((JTextField) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.numero")).setText(null);
			((JTextField) form.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.tipo")).setText(null);
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

	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		formPubblicazioni = (Form) serviceManager.lookup(Form.class);
		formAlias = (Form) serviceManager.lookup(Form.class);
		sottoFormDatiPubblicazione = (Form) serviceManager.lookup(Form.class);
		
		urnForm = (UrnForm) serviceManager.lookup(UrnForm.class);

		report_dataPubblicazione = (DateForm) serviceManager.lookup(DateForm.class);
		
		report_redazioneData = (DateForm) serviceManager.lookup(DateForm.class);

		tagDataSottoFormDatiPubblicazione = (DateForm) serviceManager.lookup(DateForm.class);

		pub_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
		alias_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("MetaDescrittori.jfrm"));
		form.replaceComponent("editor.meta.descrittori.riepilogo.datapubblicazione", report_dataPubblicazione.getAsComponent());
		form.replaceComponent("editor.meta.descrittori.redazione.data", report_redazioneData.getAsComponent());
		report_numeroPubblicazione = (JTextField) form.getComponentByName("editor.meta.descrittori.riepilogo.numeropubblicazione");
		report_tipoPubblicazione = (JTextField) form.getComponentByName("editor.meta.descrittori.riepilogo.tipopubblicazione");
		report_redazioneNome = (JTextField) form.getComponentByName("editor.meta.descrittori.riepilogo.redazione.nome");
		report_redazioneUrl = (JTextField) form.getComponentByName("editor.meta.descrittori.riepilogo.redazione.url");
		report_redazioneContributo = (JTextField) form.getComponentByName("editor.meta.descrittori.riepilogo.redazione.contributo");
		form.setName("editor.form.meta.descrittori.riepilogo");
		
		form.setHelpKey("help.contents.form.metadescrittori");
		
		sottoFormDatiPubblicazione.setMainComponent(getClass().getResourceAsStream("DatiPubblicazione.jfrm"));
		

		formPubblicazioni.setMainComponent(getClass().getResourceAsStream("Pubblicazioni.jfrm"));

		formPubblicazioni.replaceComponent("editor.meta.descrittori.pubblicazioni.altre", pub_listtextfield.getAsComponent());
		formPubblicazioni.setSize(680, 400);
		formPubblicazioni.setName("editor.form.meta.descrittori.pubblicazioni");
		pub_listtextfield.setEditor(new PubListTextFieldEditor(sottoFormDatiPubblicazione, tagDataSottoFormDatiPubblicazione));

		sottoFormDatiPubblicazione.replaceComponent("editor.meta.descrittori.pubblicazioni.datipubblicazione.data", tagDataSottoFormDatiPubblicazione
				.getAsComponent());

		tagTipoSottoFormDatiPubblicazione = (JTextField) sottoFormDatiPubblicazione
		.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.tipo");
		
		tagAnonimaSottoFormDatiPubblicazione = (JComboBox) sottoFormDatiPubblicazione
				.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.nometag");
		tagAnonimaSottoFormDatiPubblicazione.addItem("ripubblicazione");
		tagAnonimaSottoFormDatiPubblicazione.addItem("errata");
		tagAnonimaSottoFormDatiPubblicazione.addItem("rettifica");
		tagNumeroSottoFormDatiPubblicazione = (JTextField) sottoFormDatiPubblicazione
			.getComponentByName("editor.meta.descrittori.pubblicazioni.datipubblicazione.numero");

		formAlias.setMainComponent(getClass().getResourceAsStream("Alias.jfrm"));
		formAlias.replaceComponent("editor.meta.descrittori.alias.listtextfield", alias_listtextfield.getAsComponent());
		formAlias.setSize(650, 400);
		formAlias.setName("editor.form.meta.descrittori.alias");
		alias_listtextfield.setEditor(new AliasListTextFieldEditor());
		
		// Report items
		aliasList = (JList) form.getComponentByName("editor.meta.descrittori.riepilogo.alias");
		altrePubblicazioniList = (JList) form.getComponentByName("editor.meta.descrittori.riepilogo.altrepubblicazioni");
	

		pubblicazioniButton = (JButton) form.getComponentByName("editor.meta.descrittori.riepilogo.pubblicazioni_btn");
		aliasButton = (JButton) form.getComponentByName("editor.meta.descrittori.riepilogo.alias_btn");
		
		pubblicazioniButton.addActionListener(this);
		aliasButton.addActionListener(this);
		
	}

	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	public boolean openForm() {
		form.setSize(600, 650);
		form.showDialog();
		if(form.isOk()){
			pubblicazione = new Pubblicazione("pubblicazione", report_tipoPubblicazione.getText(), report_numeroPubblicazione.getText(), report_dataPubblicazione.getAsYYYYMMDD());
		}
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

			formPubblicazioni.showDialog();
			if (formPubblicazioni.isOk()) {

				altrePubblicazioni = new Pubblicazione[pub_listtextfield.getListElements().size()];
				pub_listtextfield.getListElements().toArray(altrePubblicazioni);
				altrePubblicazioniList.setListData(altrePubblicazioni);

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
		}
		
	}

	public Pubblicazione getPubblicazione() {
		return pubblicazione;
	}

//	public Vigenza[] getVigenze() {
//		return vigenze;
//	}

//	public Relazione[] getRelazioniUlteriori() {
//		return relazioniUlteriori;
//	}

	public String[] getAlias() {
		return aliases;
	}

	public String getTipoPubblicazione() {
		return tipoPubblicazione;
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


	public void setTipoPubblicazione(String tipoPubblicazione) {
		this.tipoPubblicazione = tipoPubblicazione;
		report_tipoPubblicazione.setText(tipoPubblicazione);
	}

	public void setTipoDTD(String tipoDTD) {
		this.tipoDTD = tipoDTD;
	}


	public void setPubblicazione(Pubblicazione pubblicazione) {
		this.pubblicazione = pubblicazione;
		report_dataPubblicazione.set(UtilDate.normToDate(pubblicazione.getNorm()));
		report_numeroPubblicazione.setText(pubblicazione.getNum());
		report_tipoPubblicazione.setText(pubblicazione.getTipo());
	}
	public void setRedazione(String[] redazione) {
		report_redazioneData.set(UtilDate.normToDate(redazione[0]));
		report_redazioneNome.setText(redazione[1]);
		report_redazioneUrl.setText(redazione[2]);
		report_redazioneContributo.setText(redazione[3]);
		
	}

	public String[] getRedazione() {
		String[] redazione=new String[]{UtilDate.dateToNorm(report_redazioneData.getAsDate()),
				report_redazioneNome.getText(),
				report_redazioneUrl.getText(),
				report_redazioneContributo.getText()};
		return redazione;
	}

}
