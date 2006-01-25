package it.cnr.ittig.xmleges.editor.blocks.form.rinvii.partizioni;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm</code>.</h1>
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
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @see
 * @version 1.0
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti</a>
 */
public class PartizioniFormImpl implements PartizioniForm, Loggable, Serviceable, Configurable, Initializable, ActionListener, FormVerifier, FocusListener {
	Logger logger;

	Form form;

	UtilUI utilui;

	UtilMsg utilmsg;

	JComboBox comboPartizioni_0;

	JComboBox comboPartizioni_1;

	JComboBox comboPartizioni_2;

	JComboBox comboPartizioni_3;

	JComboBox comboPartizioni_4;

	JComboBox comboLat_0;

	JComboBox comboLat_1;

	JComboBox comboLat_2;

	JComboBox comboLat_3;

	JComboBox comboLat_4;

	JTextField textPartizioni_0;

	JTextField textPartizioni_1;

	JTextField textPartizioni_2;

	JTextField textPartizioni_3;

	JTextField textPartizioni_4;

	JLabel tipo;

	JLabel numero;

	JLabel lat;

	String partizioneEstesa = "";

	boolean isMultiCit = false;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		logger.debug("Avvio servizi");
		form = (Form) serviceManager.lookup(Form.class);
		utilui = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		logger.debug("Fine attivazione servizi");
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		logger.debug("Inizializzazione Form Partizioni");
		form.setMainComponent(this.getClass().getResourceAsStream("Partizioni.jfrm"));
		form.setSize(350, 250);
		form.setName("editor.form.rinvii.partizioni");
		comboPartizioni_0 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.tipo0");
		comboPartizioni_1 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.tipo1");
		comboPartizioni_2 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.tipo2");
		comboPartizioni_3 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.tipo3");
		comboPartizioni_4 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.tipo4");
		comboLat_0 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.latin0");
		comboLat_1 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.latin1");
		comboLat_2 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.latin2");
		comboLat_3 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.latin3");
		comboLat_4 = (JComboBox) form.getComponentByName("editor.form.rinvii.partizioni.combo.latin4");
		textPartizioni_0 = (JTextField) form.getComponentByName("editor.form.rinvii.partizioni.txtfield.numero0");
		textPartizioni_1 = (JTextField) form.getComponentByName("editor.form.rinvii.partizioni.txtfield.numero1");
		textPartizioni_2 = (JTextField) form.getComponentByName("editor.form.rinvii.partizioni.txtfield.numero2");
		textPartizioni_3 = (JTextField) form.getComponentByName("editor.form.rinvii.partizioni.txtfield.numero3");
		textPartizioni_4 = (JTextField) form.getComponentByName("editor.form.rinvii.partizioni.txtfield.numero4");

		textPartizioni_0.addFocusListener(this);
		textPartizioni_1.addFocusListener(this);
		textPartizioni_2.addFocusListener(this);
		textPartizioni_3.addFocusListener(this);
		textPartizioni_4.addFocusListener(this);

		tipo = (JLabel) form.getComponentByName("editor.form.rinvii.partizioni.label.tipo");
		numero = (JLabel) form.getComponentByName("editor.form.rinvii.partizioni.label.numero");
		lat = (JLabel) form.getComponentByName("editor.form.rinvii.partizioni.label.latin");

		comboPartizioni_0.addActionListener(this);
		comboPartizioni_1.addActionListener(this);
		comboPartizioni_2.addActionListener(this);
		comboPartizioni_3.addActionListener(this);
		comboPartizioni_4.addActionListener(this);

		logger.debug("Fine inizializzazione");
		logger.debug("Inizio internazionalizzazione");
		utilui.applyI18n(tipo);
		utilui.applyI18n(numero);
		utilui.applyI18n(lat);

		logger.debug("Fine internazionalizzazione");
	}

	private void popolaControlli(JComboBox combo) {
		combo.addItem("");
		combo.addItem("Libro");
		combo.addItem("Parte");
		combo.addItem("Titolo");
		combo.addItem("Capo");
		combo.addItem("Sezione");
		combo.addItem("Articolo");
		combo.addItem("Comma");
		combo.addItem("Lettera");
		combo.addItem("Numero");
	}

	public String getPartizioneEstesa() {
		return (partizioneEstesa);
	}

	public boolean openForm() {
		popolaLatinSource();
		partizioneEstesa = "";

		try {
			if (comboPartizioni_0.getItemCount() > 0)
				comboPartizioni_0.removeAllItems();
		} catch (Exception e) {
		}
		try {
			if (comboPartizioni_1.getItemCount() > 0)
				comboPartizioni_1.removeAllItems();
		} catch (Exception e) {
		}
		try {
			if (comboPartizioni_2.getItemCount() > 0)
				comboPartizioni_2.removeAllItems();
		} catch (Exception e) {
		}
		try {
			if (comboPartizioni_3.getItemCount() > 0)
				comboPartizioni_3.removeAllItems();
		} catch (Exception e) {
		}
		try {
			if (comboPartizioni_4.getItemCount() > 0)
				comboPartizioni_4.removeAllItems();
		} catch (Exception e) {
		}

		popolaControlli(comboPartizioni_0);
		popolaControlli(comboPartizioni_1);
		popolaControlli(comboPartizioni_2);
		popolaControlli(comboPartizioni_3);
		popolaControlli(comboPartizioni_4);
		textPartizioni_0.setText("");
		textPartizioni_1.setText("");
		textPartizioni_2.setText("");
		textPartizioni_3.setText("");
		textPartizioni_4.setText("");

		form.setDialogResizable(false);
		form.addFormVerifier(this);
		form.showDialog();
		if (form.isOk()) {
			partizioneEstesa = fromComponentToString();
			return true;
		} else
			return false;

	}

	private void popolaLatinSource() {
		comboLat_0.removeAllItems();
		comboLat_0.addItem("");
		comboLat_0.addItem("bis");
		comboLat_0.addItem("ter");
		comboLat_0.addItem("quater");
		comboLat_0.addItem("quinquies");
		comboLat_0.addItem("sexies");
		comboLat_0.addItem("septies");
		comboLat_0.addItem("octies");
		comboLat_0.addItem("novies");
		comboLat_0.addItem("decies");

		comboLat_1.removeAllItems();
		comboLat_1.addItem("");
		comboLat_1.addItem("bis");
		comboLat_1.addItem("ter");
		comboLat_1.addItem("quater");
		comboLat_1.addItem("quinquies");
		comboLat_1.addItem("sexies");
		comboLat_1.addItem("septies");
		comboLat_1.addItem("octies");
		comboLat_1.addItem("novies");
		comboLat_1.addItem("decies");

		comboLat_2.removeAllItems();
		comboLat_2.addItem("");
		comboLat_2.addItem("bis");
		comboLat_2.addItem("ter");
		comboLat_2.addItem("quater");
		comboLat_2.addItem("quinquies");
		comboLat_2.addItem("sexies");
		comboLat_2.addItem("septies");
		comboLat_2.addItem("octies");
		comboLat_2.addItem("novies");
		comboLat_2.addItem("decies");

		comboLat_3.removeAllItems();
		comboLat_3.addItem("");
		comboLat_3.addItem("bis");
		comboLat_3.addItem("ter");
		comboLat_3.addItem("quater");
		comboLat_3.addItem("quinquies");
		comboLat_3.addItem("sexies");
		comboLat_3.addItem("septies");
		comboLat_3.addItem("octies");
		comboLat_3.addItem("novies");
		comboLat_3.addItem("decies");

		comboLat_4.removeAllItems();
		comboLat_4.addItem("");
		comboLat_4.addItem("bis");
		comboLat_4.addItem("ter");
		comboLat_4.addItem("quater");
		comboLat_4.addItem("quinquies");
		comboLat_4.addItem("sexies");
		comboLat_4.addItem("septies");
		comboLat_4.addItem("octies");
		comboLat_4.addItem("novies");
		comboLat_4.addItem("decies");

	}

	private boolean isMultiCitazione(String numPartizione) {

		Character[] caratteri = new Character[numPartizione.length()];

		if (numPartizione.equals("")) {
			isMultiCit = false;
			return (false);
		}
		for (int i = 0; i < numPartizione.length(); i++) {
			caratteri[i] = new Character(numPartizione.charAt(i));
			if (((caratteri[i].toString().equals(",")) || (caratteri[i].toString().equals(";")))) {
				isMultiCit = true;
				return (true);
			} else {
				isMultiCit = false;
				return (false);
			}
		}
		isMultiCit = false;
		return (false);
	}

	private String fromStringToNumero(String testo) {
		// FIXME aggiungere l'informazione sul tipo di partizione per sbrogliare
		// l'ambiguit? fra roman e letter
		String valore = "";
		if (testo.equals(""))
			return ("");
		if (isArabo(testo, false))
			valore = testo;
		else {
			if (isLettera(testo, false))
				valore = "" + UtilLang.fromLetterToNumber(testo);
			else if (isRomano(testo, false))
				valore = "" + UtilLang.fromRomanToArabic(testo);
		}
		return valore;
	}

	private String fromComponentToString() {

		String partizione = "";
		String text0;
		String text1;
		String text2;
		String text3;
		String text4;

		// Verifica del formato dei dati inseriti

		// Verifica della presenza di citazioni multiple

		if (!(textPartizioni_0.getText().equals("")))
			isMultiCitazione(textPartizioni_0.getText());
		if (!(textPartizioni_1.getText().equals("")))
			isMultiCitazione(textPartizioni_1.getText());
		if (!(textPartizioni_2.getText().equals("")))
			isMultiCitazione(textPartizioni_2.getText());
		if (!(textPartizioni_3.getText().equals("")))
			isMultiCitazione(textPartizioni_3.getText());
		if (!(textPartizioni_4.getText().equals("")))
			isMultiCitazione(textPartizioni_4.getText());

		// Verifica del formato dei dati inseriti
		if (!isMultiCit) {

		}

		text0 = fromStringToNumero(textPartizioni_0.getText());
		text1 = fromStringToNumero(textPartizioni_1.getText());
		text2 = fromStringToNumero(textPartizioni_2.getText());
		text3 = fromStringToNumero(textPartizioni_3.getText());
		text4 = fromStringToNumero(textPartizioni_4.getText());

		partizione = getSelected(comboPartizioni_0) + " " + text0 + getSelected(comboLat_0) + " " + getSelected(comboPartizioni_1) + " " + text1
				+ getSelected(comboLat_1) + " " + getSelected(comboPartizioni_2) + " " + text2 + getSelected(comboLat_2) + " " + getSelected(comboPartizioni_3)
				+ " " + text3 + getSelected(comboLat_3) + " " + getSelected(comboPartizioni_4) + " " + text4 + getSelected(comboLat_4) + " ";

		return (partizione.trim());
	}

	private String getSelected(JComboBox combo) {
		return combo.getSelectedItem() == null ? "" : combo.getSelectedItem().toString();
	}

	private void PopolaSorgente(String evtsource, String TipoPartizione) {

		if (evtsource.equals("partizioni_0")) {
			if (comboPartizioni_1.getItemCount() > 0)
				comboPartizioni_1.removeAllItems();
			if (TipoPartizione.equals("")) {
				comboPartizioni_1.addItem("");
				comboPartizioni_1.addItem("Libro");
				comboPartizioni_1.addItem("Parte");
				comboPartizioni_1.addItem("Titolo");
				comboPartizioni_1.addItem("Capo");
				comboPartizioni_1.addItem("Sezione");
				comboPartizioni_1.addItem("Articolo");
				comboPartizioni_1.addItem("Comma");
				comboPartizioni_1.addItem("Lettera");
				comboPartizioni_1.addItem("Numero");
			} else if (TipoPartizione.equals("Libro")) {
				comboPartizioni_1.addItem("");
				comboPartizioni_1.addItem("Parte");
				comboPartizioni_1.addItem("Titolo");
				comboPartizioni_1.addItem("Capo");
				comboPartizioni_1.addItem("Sezione");
			} else if (TipoPartizione.equals("Parte")) {
				comboPartizioni_1.addItem("");
				comboPartizioni_1.addItem("Titolo");
				comboPartizioni_1.addItem("Capo");
				comboPartizioni_1.addItem("Sezione");
			} else if (TipoPartizione.equals("Titolo")) {
				comboPartizioni_1.addItem("");
				comboPartizioni_1.addItem("Capo");
				comboPartizioni_1.addItem("Sezione");
			} else if (TipoPartizione.equals("Capo")) {
				comboPartizioni_1.addItem("");
				comboPartizioni_1.addItem("Sezione");
			} else if (TipoPartizione.equals("Sezione")) {
				// comboPartizioni_1.addItem("");
			} else if (TipoPartizione.equals("Articolo")) {
				comboPartizioni_1.addItem("");
				comboPartizioni_1.addItem("Comma");
				comboPartizioni_1.addItem("Lettera");
				comboPartizioni_1.addItem("Numero");
			} else if (TipoPartizione.equals("Comma")) {
				comboPartizioni_1.addItem("");
				comboPartizioni_1.addItem("Lettera");
				comboPartizioni_1.addItem("Numero");

			} else if (TipoPartizione.equals("Lettera")) {
				comboPartizioni_1.addItem("");
				comboPartizioni_1.addItem("Numero");
			} else if (TipoPartizione.equals("Numero")) {
				// comboPartizioni_1.addItem("");
			}
		}
		if (evtsource.equals("partizioni_1")) {
			if (comboPartizioni_2.getItemCount() > 0)
				comboPartizioni_2.removeAllItems();

			if (TipoPartizione.equals("")) {
				comboPartizioni_2.addItem("");
				comboPartizioni_2.addItem("Libro");
				comboPartizioni_2.addItem("Parte");
				comboPartizioni_2.addItem("Titolo");
				comboPartizioni_2.addItem("Capo");
				comboPartizioni_2.addItem("Sezione");
				comboPartizioni_2.addItem("Articolo");
				comboPartizioni_2.addItem("Comma");
				comboPartizioni_2.addItem("Lettera");
				comboPartizioni_2.addItem("Numero");
			} else if (TipoPartizione.equals("Libro")) {
				comboPartizioni_2.addItem("");
				comboPartizioni_2.addItem("Parte");
				comboPartizioni_2.addItem("Titolo");
				comboPartizioni_2.addItem("Capo");
				comboPartizioni_2.addItem("Sezione");
			} else if (TipoPartizione.equals("Parte")) {
				comboPartizioni_2.addItem("");
				comboPartizioni_2.addItem("Titolo");
				comboPartizioni_2.addItem("Capo");
				comboPartizioni_2.addItem("Sezione");
			} else if (TipoPartizione.equals("Titolo")) {
				comboPartizioni_2.addItem("");
				comboPartizioni_2.addItem("Capo");
				comboPartizioni_2.addItem("Sezione");
			} else if (TipoPartizione.equals("Capo")) {
				comboPartizioni_2.addItem("");
				comboPartizioni_2.addItem("Sezione");
			} else if (TipoPartizione.equals("Sezione")) {
				// comboPartizioni_2.addItem("");
			} else if (TipoPartizione.equals("Articolo")) {
				comboPartizioni_2.addItem("");
				comboPartizioni_2.addItem("Comma");
				comboPartizioni_2.addItem("Lettera");
				comboPartizioni_2.addItem("Numero");
			} else if (TipoPartizione.equals("Comma")) {
				comboPartizioni_2.addItem("");
				comboPartizioni_2.addItem("Lettera");
				comboPartizioni_2.addItem("Numero");
			} else if (TipoPartizione.equals("Lettera")) {
				comboPartizioni_2.addItem("");
				comboPartizioni_2.addItem("Numero");
			} else if (TipoPartizione.equals("Numero")) {
				// comboPartizioni_2.addItem("");
			}
		}
		if (evtsource.equals("partizioni_2")) {
			if (comboPartizioni_3.getItemCount() > 0)
				comboPartizioni_3.removeAllItems();

			if (TipoPartizione.equals("")) {
				comboPartizioni_3.addItem("");
				comboPartizioni_3.addItem("Libro");
				comboPartizioni_3.addItem("Parte");
				comboPartizioni_3.addItem("Titolo");
				comboPartizioni_3.addItem("Capo");
				comboPartizioni_3.addItem("Sezione");
				comboPartizioni_3.addItem("Articolo");
				comboPartizioni_3.addItem("Comma");
				comboPartizioni_3.addItem("Lettera");
				comboPartizioni_3.addItem("Numero");
			} else if (TipoPartizione.equals("Libro")) {
				comboPartizioni_3.addItem("");
				comboPartizioni_3.addItem("Parte");
				comboPartizioni_3.addItem("Titolo");
				comboPartizioni_3.addItem("Capo");
				comboPartizioni_3.addItem("Sezione");
			} else if (TipoPartizione.equals("Parte")) {
				comboPartizioni_3.addItem("");
				comboPartizioni_3.addItem("Titolo");
				comboPartizioni_3.addItem("Capo");
				comboPartizioni_3.addItem("Sezione");
			} else if (TipoPartizione.equals("Titolo")) {
				comboPartizioni_3.addItem("");
				comboPartizioni_3.addItem("Capo");
				comboPartizioni_3.addItem("Sezione");
			} else if (TipoPartizione.equals("Capo")) {
				comboPartizioni_3.addItem("");
				comboPartizioni_3.addItem("Sezione");
			} else if (TipoPartizione.equals("Sezione")) {
				// comboPartizioni_3.addItem("");
			} else if (TipoPartizione.equals("Articolo")) {
				comboPartizioni_3.addItem("");
				comboPartizioni_3.addItem("Comma");
				comboPartizioni_3.addItem("Lettera");
				comboPartizioni_3.addItem("Numero");
			} else if (TipoPartizione.equals("Comma")) {
				comboPartizioni_3.addItem("");
				comboPartizioni_3.addItem("Lettera");
				comboPartizioni_3.addItem("Numero");
			} else if (TipoPartizione.equals("Lettera")) {
				comboPartizioni_3.addItem("");
				comboPartizioni_3.addItem("Numero");
			} else if (TipoPartizione.equals("Numero")) {
				// comboPartizioni_3.addItem("");
			}
		}
		if (evtsource.equals("partizioni_3")) {
			if (comboPartizioni_4.getItemCount() > 0)
				comboPartizioni_4.removeAllItems();
			if (TipoPartizione.equals("")) {
				comboPartizioni_4.addItem("");
				comboPartizioni_4.addItem("Libro");
				comboPartizioni_4.addItem("Parte");
				comboPartizioni_4.addItem("Titolo");
				comboPartizioni_4.addItem("Capo");
				comboPartizioni_4.addItem("Sezione");
				comboPartizioni_4.addItem("Articolo");
				comboPartizioni_4.addItem("Comma");
				comboPartizioni_4.addItem("Lettera");
				comboPartizioni_4.addItem("Numero");
			} else if (TipoPartizione.equals("Libro")) {
				comboPartizioni_4.addItem("");
				comboPartizioni_4.addItem("Parte");
				comboPartizioni_4.addItem("Titolo");
				comboPartizioni_4.addItem("Capo");
				comboPartizioni_4.addItem("Sezione");
			} else if (TipoPartizione.equals("Parte")) {
				comboPartizioni_4.addItem("");
				comboPartizioni_4.addItem("Titolo");
				comboPartizioni_4.addItem("Capo");
				comboPartizioni_4.addItem("Sezione");
			} else if (TipoPartizione.equals("Titolo")) {
				comboPartizioni_4.addItem("");
				comboPartizioni_4.addItem("Capo");
				comboPartizioni_4.addItem("Sezione");
			} else if (TipoPartizione.equals("Capo")) {
				comboPartizioni_4.addItem("");
				comboPartizioni_4.addItem("Sezione");
			} else if (TipoPartizione.equals("Sezione")) {
				// comboPartizioni_4.addItem("");
			} else if (TipoPartizione.equals("Articolo")) {
				comboPartizioni_4.addItem("");
				comboPartizioni_4.addItem("Comma");
				comboPartizioni_4.addItem("Lettera");
				comboPartizioni_4.addItem("Numero");
			} else if (TipoPartizione.equals("Comma")) {
				comboPartizioni_4.addItem("");
				comboPartizioni_4.addItem("Lettera");
				comboPartizioni_4.addItem("Numero");
			} else if (TipoPartizione.equals("Lettera")) {
				comboPartizioni_4.addItem("");
				comboPartizioni_4.addItem("Numero");
			} else if (TipoPartizione.equals("Numero")) {
				// comboPartizioni_4.addItem("");
			}
		}
	}

	private void ManageEnabled() {

		comboPartizioni_0.setEnabled(true);
		textPartizioni_0.setEnabled(true);
		comboLat_0.setEnabled(true);

		boolean enable1 = comboPartizioni_1.getItemCount() > 0 && comboPartizioni_0.getItemCount() > 0 && !comboPartizioni_0.getSelectedItem().equals("");
		comboPartizioni_1.setEnabled(enable1);
		textPartizioni_1.setEnabled(enable1);
		comboLat_1.setEnabled(enable1);

		boolean enable2 = comboPartizioni_2.getItemCount() > 0 && comboPartizioni_1.getItemCount() > 0 && !comboPartizioni_1.getSelectedItem().equals("");
		comboPartizioni_2.setEnabled(enable2);
		textPartizioni_2.setEnabled(enable2);
		comboLat_2.setEnabled(enable2);

		boolean enable3 = comboPartizioni_3.getItemCount() > 0 && comboPartizioni_2.getItemCount() > 0 && !comboPartizioni_2.getSelectedItem().equals("");
		comboPartizioni_3.setEnabled(enable3);
		textPartizioni_3.setEnabled(enable3);
		comboLat_3.setEnabled(enable3);

		boolean enable4 = comboPartizioni_4.getItemCount() > 0 && comboPartizioni_3.getItemCount() > 0 && !comboPartizioni_3.getSelectedItem().equals("");
		comboPartizioni_4.setEnabled(enable4);
		textPartizioni_4.setEnabled(enable4);
		comboLat_4.setEnabled(enable4);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(comboPartizioni_0)) {
			if (comboPartizioni_0.getItemCount() > 0)
				PopolaSorgente("partizioni_0", comboPartizioni_0.getSelectedItem().toString());
			ManageEnabled();
		}
		if (e.getSource().equals(comboPartizioni_1)) {
			if (comboPartizioni_1.getItemCount() > 0)
				PopolaSorgente("partizioni_1", comboPartizioni_1.getSelectedItem().toString());
			ManageEnabled();
		}
		if (e.getSource().equals(comboPartizioni_2)) {
			if (comboPartizioni_2.getItemCount() > 0)
				PopolaSorgente("partizioni_2", comboPartizioni_2.getSelectedItem().toString());
			ManageEnabled();
		}
		if (e.getSource().equals(comboPartizioni_3)) {
			if (comboPartizioni_3.getItemCount() > 0)
				PopolaSorgente("partizioni_3", comboPartizioni_3.getSelectedItem().toString());
			ManageEnabled();
		}
	}

	private boolean checkData() {
		boolean isValid = true;
		if (checkNumber(getSelected(comboPartizioni_0), textPartizioni_0.getText()) && checkNumber(getSelected(comboPartizioni_1), textPartizioni_1.getText())
				&& checkNumber(getSelected(comboPartizioni_2), textPartizioni_2.getText())
				&& checkNumber(getSelected(comboPartizioni_3), textPartizioni_3.getText())
				&& checkNumber(getSelected(comboPartizioni_4), textPartizioni_4.getText())) {
			if ((comboPartizioni_0.getSelectedIndex() != 0 && textPartizioni_0.getText().equals(""))
					|| (comboPartizioni_1.getSelectedIndex() > 0 && textPartizioni_1.getText().equals(""))
					|| (comboPartizioni_2.getSelectedIndex() > 0 && textPartizioni_2.getText().equals(""))
					|| (comboPartizioni_3.getSelectedIndex() > 0 && textPartizioni_3.getText().equals(""))
					|| (comboPartizioni_4.getSelectedIndex() > 0 && textPartizioni_4.getText().equals("")))
				isValid = false;
		}
		return isValid;
	}

	public String getErrorMessage() {
		return "editor.form.rinvii.partizioni.msg.error.datimancanti.text";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.form.FormVerifier#verifyForm()
	 */
	public boolean verifyForm() {
		return checkData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
	}

	private boolean isRomanOrArabo(String numero, boolean verbose) {
		boolean isRomanOrArabo = true;
		if (!isRomano(numero, false) && !isArabo(numero, false)) {
			isRomanOrArabo = false;
			if (verbose)
				utilmsg.msgError("editor.form.rinvii.partizioni.msg.error.nonumber.text");
		}
		return isRomanOrArabo;
	}

	private boolean isRomano(String numero, boolean verbose) {
		// Vengono verificati solo i simboli usati.
		// Se viene inserito un numero con i simboli giusti
		// ma mal formato, non viene visualizzato nessun messaggio
		boolean isRomano = true;
		for (int i = 0; i < numero.length(); i++)
			if (!((numero.toUpperCase().charAt(i) == 'I') || (numero.toUpperCase().charAt(i) == 'V') || (numero.toUpperCase().charAt(i) == 'X')
					|| (numero.toUpperCase().charAt(i) == 'L') || (numero.toUpperCase().charAt(i) == 'C') || (numero.toUpperCase().charAt(i) == 'D') || (numero
					.toUpperCase().charAt(i) == 'M'))) {
				isRomano = false;
				if (verbose)
					utilmsg.msgError("editor.form.rinvii.partizioni.msg.error.romano.text");
				break;
			}
		return isRomano;
	}

	private boolean isArabo(String numero, boolean verbose) {
		boolean isArabo = true;
		try {
			Integer.parseInt(numero);
		} catch (Exception e) {
			isArabo = false;
			if (verbose)
				utilmsg.msgError("editor.form.rinvii.partizioni.msg.error.arabo.text");
		}
		return isArabo;
	}

	private boolean isLettera(String numero, boolean verbose) {
		boolean isLettera = true;
		if (numero.length() == 1) {
			try {
				Integer.parseInt(numero);
				isLettera = false;
				if (verbose)
					utilmsg.msgError("editor.form.rinvii.partizioni.msg.error.lettera.text");
			} catch (Exception e) {

			}
		} else {
			isLettera = false;
			if (verbose)
				utilmsg.msgError("editor.form.rinvii.partizioni.msg.error.lettera.text");
		}
		return isLettera;

	}

	private boolean checkNumber(String partizione, String numero) {
		if (!partizione.equals("")) {
			if (partizione.equals("Libro") || partizione.equals("Parte") || partizione.equals("Titolo") || partizione.equals("Capo")
					|| partizione.equals("Sezione"))
				return (isRomanOrArabo(numero, true));
			else {
				if (partizione.equals("Articolo") || partizione.equals("Comma") || partizione.equals("Numero"))
					return (isArabo(numero, true));
				else
					return (isLettera(numero, true));
			}
		} else
			return true;
	}

	// private boolean checkNumber(String partizione,String numero){
	// if (partizione != null && !partizione.equals("")){
	// if
	// (partizione.equals("Libro")||partizione.equals("Parte")||partizione.equals("Titolo")||partizione.equals("Capo")||partizione.equals("Sezione"))
	// return(isRomanOrArabo(numero,true));
	// else {
	// if
	// (partizione.equals("Articolo")||partizione.equals("Comma")||partizione.equals("Numero"))
	// return(isArabo(numero,true));
	// else
	// return(isLettera(numero,true));
	// }
	// }
	// else
	// return true;
	//		
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {

		String textSource = e.getComponent().getName();
		String textnumSource = textSource.substring(textSource.length() - 1, textSource.length());
		switch (Integer.parseInt(textnumSource)) {
		case 0: {
			checkNumber(getSelected(comboPartizioni_0), textPartizioni_0.getText());
			break;
		}
		case 1: {
			checkNumber(getSelected(comboPartizioni_1), textPartizioni_1.getText());
			break;
		}
		case 2: {
			checkNumber(getSelected(comboPartizioni_2), textPartizioni_2.getText());
			break;
		}
		case 3: {
			checkNumber(getSelected(comboPartizioni_3), textPartizioni_3.getText());
			break;
		}
		case 4: {
			checkNumber(getSelected(comboPartizioni_4), textPartizioni_4.getText());
			break;
		}
		default:
			break;
		}

	}
}
