package it.cnr.ittig.xmleges.editor.blocks.form.rinvii.newrinvii;

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
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.editor.services.autorita.Autorita;
import it.cnr.ittig.xmleges.editor.services.autorita.Istituzione;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.attogiacitato.AttogiacitatoForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinviiavanzate.NewRinviiAvanzateForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.ClasseItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.ProvvedimentiItem;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.text.MaskFormatter;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiForm</code>.</h1>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti</a>, Tommaso Paba
 */
public class NewRinviiFormImpl implements NewRinviiForm, Loggable, Serviceable, Initializable, ActionListener, KeyListener, FormVerifier {

	Logger logger;

	Form form;

	UtilUI utilui;

	UtilMsg utilmsg;

	Autorita registroautorita;

	Vector urn = new Vector();

	Vector mRifDescription = new Vector();

	// Oggetti grafici
	JComboBox comboprovvedimenti;

	JComboBox comboautorita;

	JComboBox combosottolivello1;

	JComboBox combosottolivello2;

	JLabel labelautorita;

	JLabel labelsottolivello1;

	JLabel labelsottolivello2;

	JLabel labelpartizioni;

	JList partizioni;

	JList autorita;

	JTextField numerolegge;

	JFormattedTextField datadispositivo;

	JButton inseriscipartizione;

	JButton eliminapartizione;

	JButton inserisciautorita;

	JButton eliminaautorita;

	JButton apriattigiacitati;

	JButton avanzate;

	JList areaurn;

	PartizioniForm partizioniForm;

	AttogiacitatoForm attogiacitatoForm;

	NewRinviiAvanzateForm avanzateForm;

	Provvedimenti provvedimenti;

	NirUtilUrn nirutilurn;

	Vector Allegati = new Vector();

	Vector Comunicati = new Vector();

	String versione = null;

	String errorStr;

	DefaultListModel lmd;

	DefaultListModel lmurn;

	DefaultListModel lmautorita;

	boolean isMultiCitazione = false;

	boolean isAllowMRif;

	boolean isAllowPartizioni;

	boolean isAllowAnnessi;

	boolean isAllowAttigiacitati;

	private MaskFormatter mf;

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
		partizioniForm = (PartizioniForm) serviceManager.lookup(PartizioniForm.class);
		attogiacitatoForm = (AttogiacitatoForm) serviceManager.lookup(AttogiacitatoForm.class);
		provvedimenti = (Provvedimenti) serviceManager.lookup(Provvedimenti.class);
		registroautorita = (Autorita) serviceManager.lookup(Autorita.class);
		nirutilurn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		avanzateForm = (NewRinviiAvanzateForm) serviceManager.lookup(NewRinviiAvanzateForm.class);
		logger.debug("Fine attivazione servizi");
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		logger.debug("Inizializzazione Form NewRinvii");
		form.setMainComponent(this.getClass().getResourceAsStream("Newrinvii.jfrm"));
		form.setSize(600, 600);
		// TODO cambiare il nome per editor.form.rinvii.newrinvii
		form.setName("editor.form.rinvii.newrinvii");

		areaurn = (JList) form.getComponentByName("editor.form.rinvii.newrinvii.list.urn");
		areaurn.setEnabled(false);

		comboprovvedimenti = (JComboBox) form.getComponentByName("editor.form.rinvii.newrinvii.combo.provvedimenti");
		comboautorita = (JComboBox) form.getComponentByName("editor.form.rinvii.newrinvii.combo.autorita");
		combosottolivello1 = (JComboBox) form.getComponentByName("editor.form.rinvii.newrinvii.combo.sottolivello1");
		combosottolivello2 = (JComboBox) form.getComponentByName("editor.form.rinvii.newrinvii.combo.sottolivello2");
		comboprovvedimenti.addActionListener(this);

		comboautorita.addActionListener(this);
		combosottolivello1.addActionListener(this);
		combosottolivello2.addActionListener(this);

		numerolegge = (JTextField) form.getComponentByName("editor.form.rinvii.newrinvii.txtfield.numero");
		datadispositivo = (JFormattedTextField) form.getComponentByName("editor.form.rinvii.newrinvii.txtfield.data");

		mf = new MaskFormatter("##/##/####");
		mf.setPlaceholderCharacter('_');
		mf.install(datadispositivo);

		datadispositivo.addKeyListener(this);
		numerolegge.addKeyListener(this);

		partizioni = (JList) form.getComponentByName("editor.form.rinvii.newrinvii.list.partizioni");
		autorita = (JList) form.getComponentByName("editor.form.rinvii.newrinvii.list.elencoautorita");

		inseriscipartizione = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.button.inserisci");
		eliminapartizione = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.button.elimina");

		inserisciautorita = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.button.addautorita");
		eliminaautorita = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.button.removeautorita");
		apriattigiacitati = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.button.attigiacitati");
		avanzate = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.button.avanzate");

		inseriscipartizione.addActionListener(this);
		eliminapartizione.addActionListener(this);
		inserisciautorita.addActionListener(this);
		eliminaautorita.addActionListener(this);
		apriattigiacitati.addActionListener(this);
		avanzate.addActionListener(this);

		JLabel labelurn = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.label.urn");
		JLabel labelnumero = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.label.numero");
		JLabel labeldata = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.label.data");
		JLabel labelprovvedimento = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.label.provvedimento");
		labelpartizioni = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.label.partizioni");
		labelautorita = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.label.autorita");
		labelsottolivello1 = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.label.sottolivello1");
		labelsottolivello2 = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.label.sottolivello2");

		logger.debug("Fine inizializzazione");
		logger.debug("Inizio internazionalizzazione");
		utilui.applyI18n(labelurn);
		utilui.applyI18n(labelnumero);
		utilui.applyI18n(labelpartizioni);
		utilui.applyI18n(labeldata);
		utilui.applyI18n(labelprovvedimento);
		utilui.applyI18n(labelautorita);
		utilui.applyI18n(labelsottolivello1);
		utilui.applyI18n(labelsottolivello2);
		logger.debug("Fine internazionalizzazione");
	}

	public Vector getUrn() {
		for (int i = 0; i < urn.size(); i++) {
			((Urn) urn.get(i)).setFormaTestuale(nirutilurn.getFormaTestuale((Urn) urn.get(i)));
		}
		return urn;
	}

	private void addElementAreaUrn(String urn) {
		if (lmurn.indexOf(urn) == -1) {
			lmurn.addElement(urn);
			areaurn.setModel(lmurn);
		}
	}

	private void eraseAreaUrn() {
		if (lmurn != null && lmurn.size() > 0)
			lmurn.removeAllElements();
	}

	private void azzeraControlli() {
		eraseAreaUrn();
		addElementAreaUrn("urn:nir:");
		numerolegge.setText("");
		datadispositivo.setText("");
		if (lmautorita.getSize() > 0)
			lmautorita.removeAllElements();
		if (lmd.getSize() > 0)
			lmd.removeAllElements();
		urn = new Vector();
	}

	private int getIndexOf(String provvedimento) {
		int index = -1;
		for (int i = 0; i < comboprovvedimenti.getModel().getSize(); i++)
			if (comboprovvedimenti.getModel().getElementAt(i).toString().equals(provvedimento))
				return i;
		return index;
	}

	public boolean checkMultiRifValidity() {
		Vector multiRif = new Vector();
		for (int i = 0; i < urn.size(); i++) {
			multiRif.add(((Urn) urn.get(i)).getPartizione());
		}
		for (int i = 0; i < multiRif.size() - 1; i++) {
			if (!(multiRif.get(i).equals(multiRif.get(i + 1)))) {
				return (false);
			}
		}
		return (true);
	}

	private void getDescrizioneMrif() {

		String[] sottopart1;
		String[] sottopart2;
		Vector Partizioni = new Vector();
		mRifDescription = new Vector();
		for (int i = 0; i < urn.size(); i++)
			Partizioni.add(((Urn) urn.get(i)).getPartizione());
		for (int i = 0; i < Partizioni.size(); i++) {
			if (Partizioni.get(i) != null) {
				mRifDescription.add(nirutilurn.getFormaTestualeById(Partizioni.get(i).toString()));
				for (int j = i + 1; j < Partizioni.size(); j++) {
					if (Partizioni.get(i).toString().length() == Partizioni.get(j).toString().length()) {// Forse
																											// le
						// due
						// citazioni fanno
						// riferimento alla
						// stessa
						// sottopartizione
						sottopart1 = Partizioni.get(i).toString().split("-");
						sottopart2 = Partizioni.get(j).toString().split("-");
						for (int k = 0; k < sottopart1.length; k++)
							if (sottopart1[k].compareTo(sottopart2[k]) == 0 && k < sottopart1.length - 1) { // stessa
								// radice
								// "es
								// prt-2"
								if (sottopart1[k + 1].substring(0, 3).compareTo(sottopart2[k + 1].substring(0, 3)) == 0) {
									mRifDescription.add(",");
									mRifDescription.add(sottopart2[k + 1].substring(3, sottopart2[k + 1].length()));
									Partizioni.set(j, null);
									break;
								}
							}
					}
				}
			}
		}
	}

	public boolean openForm(String urnString, boolean allowMRif, boolean allowPartizioni, boolean allowAnnessi) {
		return openForm(urnString, allowMRif, allowPartizioni, allowAnnessi, true);
	}

	public boolean openForm(String urnString, boolean allowMRif, boolean allowPartizioni, boolean allowAnnessi, boolean allowAttigiacitati) {

		String[] vect = new String[1];
		vect[0] = urnString;
		return openForm(vect, allowMRif, allowPartizioni, allowAnnessi, allowAttigiacitati);

	}

	public boolean openForm(String[] urnString, boolean allowMRif, boolean allowPartizioni, boolean allowAnnessi, boolean allowAttigiacitati) {
		try {
			isAllowMRif = allowMRif;
			isAllowPartizioni = allowPartizioni;
			isAllowAnnessi = allowAnnessi;
			isAllowAttigiacitati = allowAttigiacitati;

			apriattigiacitati.setVisible(isAllowAttigiacitati);

			partizioni.setVisible(isAllowPartizioni);
			inseriscipartizione.setVisible(isAllowPartizioni);
			eliminapartizione.setVisible(isAllowPartizioni);
			labelpartizioni.setVisible(isAllowPartizioni);

			lmd = new DefaultListModel();
			partizioni.setModel(lmd);
			partizioni.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lmautorita = new DefaultListModel();
			autorita.setModel(lmautorita);
			autorita.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lmurn = new DefaultListModel();
			areaurn.setModel(lmurn);

			// azzeraControlli();
			Allegati = new Vector();
			Comunicati = new Vector();
			versione = null;
			Urn u = new Urn();
			urn.add(u);

			comboprovvedimenti.removeAllItems();
			ClasseItem[] listaclassi = provvedimenti.getAllClassi();
			for (int i = 0; i < listaclassi.length; i++) {
				for (int j = 0; j < listaclassi[i].getProvvedimentiList().length; j++)
					comboprovvedimenti.addItem(listaclassi[i].getProvvedimentoAt(j));
			}
			comboprovvedimenti.getModel().setSelectedItem(listaclassi[0].getProvvedimentoAt(0));

		} catch (Exception e) {
			logger.error(e.toString());
		}

		azzeraControlli();

		if (urnString.length > 0 && !urnString[0].equals("")) {
			for (int i = 0; i < urnString.length; i++)
				popolaControlli(urnString[i]);
			manageUrnList();
			getDescrizioneMrif();
		}

		form.setDialogResizable(false);
		form.addFormVerifier(this);
		form.showDialog();

		return form.isOk();
	}

	private void popolaControlli(String urnString) {

		Urn u = new Urn();
		try {
			u.parseUrn(urnString);

			// /////////////////////////// Popola avanzate
			// ALLEGATI
			if (u.getAllegati().size() > 0)
				Allegati = u.getAllegati();
			// COMUNICATI
			if (u.getComunicati().size() > 0) {
				Comunicati = u.getComunicati();
			}
			// VERSIONE
			if (u.getVersione() != null && u.getVersione().length() > 0)
				versione = u.getVersione();

			// ///////////////////////// Popola Numero
			// ////////////////////////////////////////////////
			String numero = "";
			for (int i = 0; i < u.getNumeri().size(); i++)
				numero += u.getNumeri().get(i).toString() + ", ";
			if (u.getNumeri().size() > 0)
				numero = numero.substring(0, numero.length() - 2);
			numerolegge.setText(numero);

			// ///////////////////////// Popola Data
			// /////////////////////////////////////////////////
			String aaaa;
			String mm;
			String gg;
			String date;
			String datadisp = "";
//			if(u.getDate().size()==0)
//				u.setUniqueData("____-__-__");
			for (int i = 0; i < u.getDate().size(); i++) {
				date = u.getDate().get(i).toString();
				StringTokenizer stDate = new StringTokenizer(date, "-");
				if (stDate.countTokens() > 1) {// La data ?? indicata nel
					// formato gg-mm-aaaa
					aaaa = stDate.nextToken();
					mm = stDate.nextToken();
					gg = stDate.nextToken();
					datadisp += gg + "/" + mm + "/" + aaaa + ", ";
				} else {
					mf.setAllowsInvalid(true);
					datadisp += "__/__/" + stDate.nextToken() + ",";
				}
			}
			datadisp = datadisp.substring(0, datadisp.length() - 1);
			datadispositivo.setText(datadisp);
			mf.setAllowsInvalid(false);

			// Gestione associazione provvedimenti - autorita'
			// In alcuni casi non e' univocamente determinabile il tipo di
			// provvedimento data la sua urn
			// decreto, per esempio, puo' corrispondere a piu' tipi di
			// provvedimento

			if (u.getProvvedimento().equals("decreto")) {
				if (u.getAutorita().size() > 0) {
					String autorita = u.getAutorita().get(0).toString();
					if (autorita.equals("presidente.repubblica"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Decreto Presidente Repubblica"));
					else if (autorita.equals("presidente.consiglio.ministri"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Decreto Presidente Consiglio Ministri"));
					else if ((autorita.length() > 9) && (autorita.substring(0, 9).equals("ministero")) || (autorita.length() > 8)
							&& (autorita.substring(0, 8).equals("ministro")))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Decreto Ministeriale"));
					else
						comboprovvedimenti.setSelectedIndex(getIndexOf("Decreto di altre autorita'"));
				}
			} else if (u.getProvvedimento().equals("ordinanza")) {
				if (u.getAutorita().size() > 0) {
					String autorita = u.getAutorita().get(0).toString();
					if (autorita.equals("presidente.consiglio.ministri"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Ordinanza Presidente Consiglio Ministri"));
					else if ((autorita.length() > 9) && (autorita.substring(0, 9).equals("ministero")))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Ordinanza Ministeriale"));
					else
						comboprovvedimenti.setSelectedIndex(getIndexOf("Ordinanza di altre autorita'"));
				}
			} else if (u.getProvvedimento().equals("regolamento")) {
				if (u.getAutorita().size() > 0) {
					String autorita = u.getAutorita().get(0).toString();
					if (autorita.equals("comunita.europee"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Regolamento CE"));
					else
						comboprovvedimenti.setSelectedIndex(getIndexOf("Regolamento di altre autorita'"));
				}
			} else if (u.getProvvedimento().equals("direttiva")) {
				if (u.getAutorita().size() > 0) {
					String autorita = u.getAutorita().get(0).toString();
					if (autorita.equals("comunita.europee"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Direttiva europea"));
					else if (autorita.equals("presidente.consiglio.ministri"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Direttiva Presidente Consiglio Ministri"));
					else if ((autorita.length() > 9) && (autorita.substring(0, 9).equals("ministero")))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Direttiva Ministeriale"));
					else
						comboprovvedimenti.setSelectedIndex(getIndexOf("Direttiva di altre autorita'"));
				}
			} else if (u.getProvvedimento().equals("provvedimento")) {
				if (u.getAutorita().size() > 0) {
					String autorita = u.getAutorita().get(0).toString();
					if ((autorita.length() > 7) && (autorita.substring(0, 8).equals("autorita")))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Provvedimento di Authority"));
					else
						comboprovvedimenti.setSelectedIndex(getIndexOf("Provvedimento di altre autorita'"));
				}
			} else if (u.getProvvedimento().equals("circolare")) {
				if (u.getAutorita().size() > 0) {
					String autorita = u.getAutorita().get(0).toString();
					if (autorita.equals("presidente.consiglio.ministri"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Circolare Presidente Consiglio Ministri"));
					else if ((autorita.length() > 9) && (autorita.substring(0, 9).equals("ministero")))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Circolare Ministeriale"));
					else
						comboprovvedimenti.setSelectedIndex(getIndexOf("Circolare di altre autorita'"));
				}
			} else if (u.getProvvedimento().equals("decisione")) {
				if (u.getAutorita().size() > 0) {
					String autorita = u.getAutorita().get(0).toString();
					if (autorita.equals("comunita.europee"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Decisione comunitaria"));
					else if ((autorita.equals("unione.europea")))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Decisione UE"));
					else
						comboprovvedimenti.setSelectedIndex(getIndexOf("Decisione di altre autorita'"));
				}
			} else if (u.getProvvedimento().equals("legge")) {
				if (u.getAutorita().size() > 0) {
					String autorita = u.getAutorita().get(0).toString();
					if (autorita.startsWith("regione"))
						comboprovvedimenti.setSelectedIndex(getIndexOf("Legge regionale"));
				}
			} else
				comboprovvedimenti.setSelectedIndex(getIndexOf(provvedimenti.getProvvedimentoByUrn(u.getProvvedimento()).toString()));

			// /////////////////////////// Popola Lista Autorit?
			// /////////////////////////////////////////
			for (int i = 0; i < u.getAutorita().size(); i++) {
				if (u.getAutorita().get(i).equals("comunita.europee")) {
					Istituzione ist = new Istituzione();
					ist.setUrn("comunita.europee");
					ist.setNome("Comunita europee");
					lmautorita.addElement(ist);
				} else if (u.getAutorita().get(i).equals("unione.europea")) {
					Istituzione ist = new Istituzione();
					ist.setUrn("unione.europea");
					ist.setNome("Unione europea");
					lmautorita.addElement(ist);
				} else {
					if (registroautorita.getNomeIstituzioneFromUrnIstituzione(u.getAutorita().get(i).toString()) != null)
						if (!lmautorita.contains(registroautorita.getNomeIstituzioneFromUrnIstituzione(u.getAutorita().get(i).toString())))
							lmautorita.addElement(registroautorita.getNomeIstituzioneFromUrnIstituzione(u.getAutorita().get(i).toString()));
				}
			}

			// ////////////////////////// Popola Lista Partizioni
			if (!u.getPartizione().equals("")) {
				StringTokenizer Partizioni = new StringTokenizer(u.getPartizione(), "-");
				String Partizione = "";
				String Parte;
				while (Partizioni.hasMoreTokens()) {
					Parte = Partizioni.nextToken();
					Partizione += u.getPartizioneFromUrn(Parte.substring(0, 3));
					Partizione += " " + Parte.substring(3, Parte.length()) + " ";
				}
				lmd.addElement(Partizione.trim());
			}

		} catch (Exception e) {
		}

		costruisciUrn();

		// ////////////////////////////////////// TRASH
		// /////////////////////////////
		// eraseAreaUrn();
		// addElementAreaUrn(urnString);
		// if (urn.size() > 0) {
		// urn.removeAllElements();
		// }

		// if(!urn.contains(u)) // bug su modifica rif singolo
		// urn.add(u);

		// if(!isInsertedUrn(u)) // bug su modifica rif singolo
		// urn.add(u);

		// ///////////////////////////////////////////////////////////////////////////////////

	}

	private boolean isInsertedUrn(Urn u) {
		for (int i = 0; i < urn.size(); i++) {
			if (((Urn) urn.get(i)).toString().equalsIgnoreCase(u.toString()))
				return true;
		}
		return false;
	}

	private void costruisciUrn() {
		if (urn.size() > 0)
			urn.removeAllElements();
		for (int i = 0; i < lmd.size(); i++) {
			Urn u = new Urn();
			try {
				for (int j = 0; j < lmautorita.getSize(); j++) {
					StringTokenizer st = new StringTokenizer(lmautorita.get(j).toString(), "/");
					String token = st.nextToken();
					if (token.equals("Comunita europee"))
						u.addAutorita("comunita.europee");
					else if (token.equals("Unione europea"))
						u.addAutorita("unione.europea");
					else
						u.addAutorita(registroautorita.getUrnIstituzioneFromNomeIstituzione(token));
				}
				u.setProvvedimento(((ProvvedimentiItem) comboprovvedimenti.getSelectedItem()).getUrnAtto());
				StringTokenizer stdata = new StringTokenizer(datadispositivo.getText(), "/");
				String gg = stdata.nextToken();
				String mm = stdata.nextToken();
				String aaaa = stdata.nextToken();
				if ((mm.equals("__")) && (gg.equals("__")))
					u.addData(aaaa);
				else
					u.addData(aaaa + "-" + mm + "-" + gg);
				if (numerolegge.getText().equals(""))
					u.addNumero("nir-1");
				else
					u.addNumero(numerolegge.getText());
				StringTokenizer st = new StringTokenizer(lmd.get(i).toString(), " ");
				String partizione = "";
				String partUrn = "";
				String token;
				while (st.hasMoreTokens()) {
					token = st.nextToken();
					partUrn = (nirutilurn.getUrnPartizione(token));
					if (partUrn.equals(""))
						partizione += token + "-";
					else
						partizione += partUrn;
				}
				partizione = partizione.substring(0, partizione.length() - 1);
				u.setPartizione(partizione);
				for (int j = 0; j < Allegati.size(); j++)
					u.addAllegato(Allegati.get(j).toString());
				for (int j = 0; j < Comunicati.size(); j++)
					u.addComunicato(Comunicati.get(j).toString());
				if (versione != null)
					u.setVersione(versione);

				if (!isInsertedUrn(u))
					urn.add(u);
			} catch (Exception e) {

			}
		}
		if (lmd.size() == 0) {
			Urn u = new Urn();
			String urnautorita = "";
			for (int i = 0; i < lmautorita.getSize(); i++) {
				StringTokenizer st = new StringTokenizer(lmautorita.get(i).toString(), "/");
				int numerotoken = st.countTokens();
				for (int j = 0; j < numerotoken; j++) {
					String token = st.nextToken();
					if (token.equals("Comunita europee"))
						urnautorita = "comunita.europee.";
					else if (token.equals("Unione europea"))
						urnautorita = "unione.europea.";
					else {

						if (j == 0)
							urnautorita = registroautorita.getUrnIstituzioneFromNomeIstituzione(token) + ".";

						else {
							StringTokenizer st2 = new StringTokenizer(token, " ");
							while (st2.hasMoreTokens())
								urnautorita += st2.nextToken() + ".";

						}
					}
				}
				if (urnautorita.substring(urnautorita.length() - 1, urnautorita.length()).equals("."))
					u.addAutorita(urnautorita.substring(0, urnautorita.length() - 1));
				else
					u.addAutorita(urnautorita);
				/*
				 * String token=st.nextToken(); if (token.equals("Comunita
				 * europee")) u.addAutorita("comunita.europee"); else if
				 * (token.equals("Unione europea"))
				 * u.addAutorita("unione.europea"); else
				 * u.addAutorita(registroautorita.getUrnIstituzioneFromNomeIstituzione(token));
				 */
			}
			u.setProvvedimento(((ProvvedimentiItem) comboprovvedimenti.getSelectedItem()).getUrnAtto());
			StringTokenizer stdata = new StringTokenizer(datadispositivo.getText(), "/");
			String gg = stdata.nextToken();
			String mm = stdata.nextToken();
			String aaaa = stdata.nextToken();
			if ((mm.equals("__")) && (gg.equals("__")))
				u.addData(aaaa);
			else
				u.addData(aaaa + "-" + mm + "-" + gg);
			if (numerolegge.getText().equals(""))
				u.addNumero("nir-1");
			else
				u.addNumero(numerolegge.getText());
			for (int j = 0; j < Allegati.size(); j++)
				u.addAllegato(Allegati.get(j).toString());
			for (int j = 0; j < Comunicati.size(); j++)
				u.addComunicato(Comunicati.get(j).toString());
			if (versione != null)
				u.setVersione(versione);

			if (!isInsertedUrn(u))
				urn.add(u);
		}
	}

	private void manageUrnList() {
		if (lmurn.size() > 0)
			lmurn.removeAllElements();
		for (int i = 0; i < urn.size(); i++)
			addElementAreaUrn(urn.get(i).toString());
	}

	public Vector getDescrizioneMRif() {
		return mRifDescription;
	}

	public void actionPerformed(ActionEvent e) {
		Dimension originalDimension = combosottolivello1.getSize();
		if (e.getSource().equals(inseriscipartizione)) {
			try {
				partizioniForm.openForm();
				inseriscipartizione.setEnabled(isAllowMRif);

				if (partizioniForm.getPartizioneEstesa().length() > 0) {
					lmd.addElement(partizioniForm.getPartizioneEstesa());

					partizioni.setModel(lmd);
					costruisciUrn();
					manageUrnList();
					if (lmd.size() > 1) {
						isMultiCitazione = true;
						getDescrizioneMrif();
					}
				}
			} catch (Exception exc) {
				logger.error("Errore durante l'inserimento di una partizione");
			}
		}
		if (e.getSource().equals(eliminapartizione)) {
			try {
				if (partizioni.getSelectedValue() != null) {
					inseriscipartizione.setEnabled(true);
					lmd.removeElement(partizioni.getSelectedValue());
					if (lmd.size() < 2)
						isMultiCitazione = false;
					costruisciUrn();
					manageUrnList();
				} else
					utilmsg.msgError("editor.form.rinvii.newrinvii.msg.eliminapartizioni");
			} catch (Exception exc) {
				logger.error("Errore durante l'eliminazione di una partizione");
			}
		}
		if (e.getSource().equals(apriattigiacitati)) {
			if (attogiacitatoForm.openForm()) {
				azzeraControlli();
				popolaControlli(attogiacitatoForm.getUrn());
				costruisciUrn();
				manageUrnList();
			}
		}

		if (e.getSource().equals(comboprovvedimenti)) {
			try {
				StringTokenizer stdata = new StringTokenizer(datadispositivo.getText(), "/");
				stdata.nextToken();
				stdata.nextToken();
				String aaaa = stdata.nextToken();
				if (datadispositivo.getText().indexOf("_") < 0 || aaaa.indexOf("_") < 0) { // ?
																							// specificata
																							// una
																							// data
																							// o
					// almeno l'anno
					String datadispositivostring = "";
					if (datadispositivo.getText().indexOf("_") < 0)
						datadispositivostring = datadispositivo.getText();
					else
						datadispositivostring = "31/12/" + aaaa;
					SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.getDateFormat());
					Date d = sdf.parse(datadispositivostring);
					ProvvedimentiItem selectedProvvedimento = provvedimenti.getProvvedimentoByName(comboprovvedimenti.getSelectedItem().toString());

					// Nel caso di urn specificata nel file Provvedimenti (es
					// codici, costituzione etc) forza data e numero
					String urnAtto = selectedProvvedimento.getUrnValore();
					if (!urnAtto.equals(""))
						popolaControlli(urnAtto);

					String urnAutorita = selectedProvvedimento.getUrnAutorita();

					if (!urnAutorita.substring(0, 1).equals("+")) {
						Istituzione[] emananti = registroautorita.getIstituzioniValideFromProvvedimenti(d, selectedProvvedimento.getUrnAutorita());
						if (!lmautorita.isEmpty())
							lmautorita.removeAllElements();
						manageAutorita(emananti);
						inserisciautorita.setEnabled(true);
					} else {
						Istituzione[] emananti = new Istituzione[1];
						emananti[0] = new Istituzione();
						emananti[0].setUrn(urnAutorita.substring(1, urnAutorita.length()));
						if (emananti[0].getUrn().equals("comunita.europee"))
							emananti[0].setNome("Comunita europee");
						else
							emananti[0].setNome("Unione europea");
						if (!lmautorita.isEmpty())
							lmautorita.removeAllElements();
						manageAutorita(emananti);
					}
				} else {
					ProvvedimentiItem selectedProvvedimento = provvedimenti.getProvvedimentoByName(comboprovvedimenti.getSelectedItem().toString());
					String urnAtto = selectedProvvedimento.getUrnValore();
					if (!urnAtto.equals("")) {
						popolaControlli(urnAtto);
					}
				}
				costruisciUrn();
				manageUrnList();
			} catch (Exception exc) {
				// logger.error(exc.toString());
			}
		}

	

		if (e.getSource().equals(comboautorita))
			manageSottoAutorita(combosottolivello1, (Istituzione) comboautorita.getSelectedItem());

		if (e.getSource().equals(combosottolivello1))
			manageSottoAutorita(combosottolivello2, (Istituzione) combosottolivello1.getSelectedItem());
		if (e.getSource().equals(inserisciautorita)) {
			if (comboautorita.getSelectedItem() != null) {
				addAutoritaToList();
				costruisciUrn();
				manageUrnList();
			} else
				utilmsg.msgError("editor.form.rinvii.newrinvii.msg.noautorita");
		}
		if (e.getSource().equals(eliminaautorita)) {
			if (autorita.getSelectedValue() != null) {
				lmautorita.removeElement(autorita.getSelectedValue());
				costruisciUrn();
				manageUrnList();
			} else
				utilmsg.msgError("editor.form.rinvii.newrinvii.msg.eliminaautorita");
		}
		if (e.getSource().equals(avanzate))
			if (avanzateForm.openForm(urn, isAllowAnnessi)) {
				Allegati = avanzateForm.getAllegati();
				Comunicati = avanzateForm.getComunicati();
				versione = avanzateForm.getVersione();
				costruisciUrn();
				manageUrnList();
			}
		combosottolivello1.setSize(originalDimension);
		combosottolivello2.setSize(originalDimension);
		combosottolivello1.repaint();
		combosottolivello2.repaint();
	}

	private void manageSottoAutorita(JComboBox combo, Istituzione emanante) {
		try {

			if (combo.getItemCount() > 0)
				combo.removeAllItems();
			Vector sottoemananti = emanante.getSottoIstituzioni();

			combo.addItem(new Istituzione());
			for (int j = 0; j < sottoemananti.size(); j++) {

				combo.addItem(((Istituzione) sottoemananti.get(j)));
			}

			if (sottoemananti.size() > 0)
				combo.getModel().setSelectedItem(new Istituzione());
			// combo.getModel().setSelectedItem(((Istituzione)sottoemananti.get(0)));
			// combo.addItem("");
		} catch (Exception e) {

		}
	}

	// private void setVisibleEmananti(boolean visible){
	// labelautorita.setEnabled(visible);
	// labelsottolivello1.setEnabled(visible);
	// labelsottolivello2.setEnabled(visible);
	// comboautorita.setEnabled(visible);
	// combosottolivello1.setEnabled(visible);
	// combosottolivello2.setEnabled(visible);
	// inserisciautorita.setEnabled(visible);
	// eliminaautorita.setEnabled(visible);
	// autorita.setEnabled(visible);
	// if (!visible)
	// lmautorita.addElement(comboautorita.getSelectedItem());
	// else{
	// lmautorita.removeAllElements();
	// }
	//			
	// }

	private void addAutoritaToList() {
		try {
			if (comboautorita.getSelectedItem() != null) {

				String selectedAutorita = comboautorita.getSelectedItem().toString();

				if ((combosottolivello1.getItemCount() > 0) && (combosottolivello1.getSelectedItem() != null)
						&& (!((Istituzione) (combosottolivello1.getSelectedItem())).getNome().equals(""))) {
					selectedAutorita += "/" + combosottolivello1.getSelectedItem().toString();
					if ((combosottolivello2.getItemCount() > 0) && (combosottolivello2.getSelectedItem() != null)
							&& (!((Istituzione) (combosottolivello2.getSelectedItem())).getNome().equals("")))
						selectedAutorita += "/" + combosottolivello2.getSelectedItem().toString();
				}
				if (lmautorita.contains(selectedAutorita))
					utilmsg.msgError("editor.form.rinvii.newrinvii.msg.duplicatedautorita");
				else
					lmautorita.addElement(selectedAutorita.substring(0, selectedAutorita.length()));
				// areaurn.setText(costruisciUrn());
			}
		} catch (Exception e) {
		}
	}

	private void manageAutorita(Istituzione[] emananti) {
		try {
			if (emananti.length > 0) {
				if (comboautorita.getItemCount() > 0)
					comboautorita.removeAllItems();
				for (int i = 0; i < emananti.length; i++)
					comboautorita.addItem(emananti[i]);
				comboautorita.getModel().setSelectedItem(emananti[0]);

				if (emananti.length == 1)
					addAutoritaToList();
			}
		} catch (Exception e) {
		}
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	public void keyReleased(KeyEvent e) {
		Dimension originalDimension = combosottolivello1.getSize();
		if (e.getSource().equals(numerolegge)) {
			costruisciUrn();
			manageUrnList();
		}
		if (e.getSource().equals(datadispositivo)) {
			try {
				if (datadispositivo.getText().indexOf("_") < 0) {
					SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.getDateFormat());
					Date d = sdf.parse(datadispositivo.getText());
					Date datainizio = sdf.parse("01/01/1860");
					if ((d.compareTo(datainizio) < 0) || (d.compareTo(UtilDate.getCurrentDate()) > 0))
						utilmsg.msgError("editor.form.rinvii.newrinvii.msg.datanonvalida");

					logger.debug(d.toString());
					ProvvedimentiItem selectedProvvedimento = provvedimenti.getProvvedimentoByName(comboprovvedimenti.getSelectedItem().toString());
					String urnAutorita = selectedProvvedimento.getUrnAutorita();
					if (!urnAutorita.substring(0, 1).equals("+")) {
						Istituzione[] emananti = registroautorita.getIstituzioniValideFromProvvedimenti(d, selectedProvvedimento.getUrnAutorita());
						if (!lmautorita.isEmpty())
							lmautorita.removeAllElements();
						manageAutorita(emananti);
						inserisciautorita.setEnabled(true);
					} else {

						Istituzione[] emananti = new Istituzione[1];
						emananti[0] = new Istituzione();
						emananti[0].setUrn(urnAutorita.substring(1, urnAutorita.length()));
						if (emananti[0].getUrn().equals("comunita.europee"))
							emananti[0].setNome("Comunita europee");
						else
							emananti[0].setNome("Unione europea");
						if (!lmautorita.isEmpty())
							lmautorita.removeAllElements();
						manageAutorita(emananti);

					}

					costruisciUrn();
					manageUrnList();
				} else {// E' stato inserito solo l'anno?
					try {
						String[] componenti = datadispositivo.getText().split("/");
						if ((componenti[0].equals("__")) && (componenti[0].equals("__"))) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
							Date d = sdf.parse(componenti[2]);
							ProvvedimentiItem selectedProvvedimento = provvedimenti.getProvvedimentoByName(comboprovvedimenti.getSelectedItem().toString());
							String urnAutorita = selectedProvvedimento.getUrnAutorita();
							if (!urnAutorita.substring(0, 1).equals("+")) {
								Istituzione[] emananti = registroautorita.getIstituzioniValideFromProvvedimenti(d, selectedProvvedimento.getUrnAutorita());
								if (!lmautorita.isEmpty())
									lmautorita.removeAllElements();
								manageAutorita(emananti);
								inserisciautorita.setEnabled(true);
							} else {

								Istituzione[] emananti = new Istituzione[1];
								emananti[0] = new Istituzione();
								emananti[0].setUrn(urnAutorita.substring(1, urnAutorita.length()));
								if (emananti[0].getUrn().equals("comunita.europee"))
									emananti[0].setNome("Comunita europee");
								else
									emananti[0].setNome("Unione europea");
								if (!lmautorita.isEmpty())
									lmautorita.removeAllElements();
								manageAutorita(emananti);

							}

							costruisciUrn();
							manageUrnList();
						}
					} catch (Exception exc) {

					}
				}

			} catch (Exception exc) {
				logger.error(exc.toString() + "Errore lettura registro autorita");
			}
		}
		combosottolivello1.setSize(originalDimension);
		combosottolivello1.repaint();
		combosottolivello2.setSize(originalDimension);
		combosottolivello2.repaint();
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public String getErrorMessage() {
		// return(errorStr);
		return ("editor.form.rinvii.newrinvii.msg.datinonvalidi");
	}

	private boolean checkDate() {
		String date = datadispositivo.getText();
		if (date.indexOf('_') >= 0) {// Verifica se ?? stato inserito solo
			// l'anno nel formato "__/__/yyyy"
			try {
				String[] componenti = date.split("/");
				if ((componenti[0].equals("__")) && (componenti[0].equals("__"))) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
					Date d = sdf.parse(componenti[2]);
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.getDateFormat());
			Date d = sdf.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean checkNumber() {
		String numbers = numerolegge.getText();
		if (numbers.trim().length() == 0) {
			if (utilmsg.msgWarning("editor.form.rinvii.newrinvii.msg.nonumber")) {
				return true;
			} else
				return false;
		}
		if (numbers.length() > 3) {
			StringTokenizer st = new StringTokenizer(numbers, ",");
			if (st.countTokens() > 1) {
				while (st.hasMoreTokens()) {
					try {
						Integer.parseInt(st.nextToken());
					} catch (Exception e) {
						return false;
					}
				}
			} else {
				for (int i = 0; i < urn.size(); i++)
					((Urn) urn.get(i)).setUniqueNumero(numbers);
				return true;
			}
		}
		return true;
	}

	private boolean checkProvvedimento() {
		String provvedimento = comboprovvedimenti.getSelectedItem().toString();
		ProvvedimentiItem p = provvedimenti.getProvvedimentoByName(provvedimento);
		if (p != null)
			return true;
		else
			return false;
	}

	private boolean checkAutorita() {

		return (lmautorita.size() > 0);
	}

	public boolean verifyForm() {
		// errorStr = "Verificare i dati inseriti.\nControllare che sia stata
		// scelta almeno una autorita'";
		// if (!checkDate())
		// errorStr += "\nControllare il formato della data";
		// if(!checkNumber())
		// errorStr += "\nControllare il formato della data";
		// if(!checkAutorita())
		// errorStr += "\\nControllare che sia stata scelta almeno una
		// autorita'";
		return checkDate() && checkNumber() && checkProvvedimento() && checkAutorita();
	}
}
