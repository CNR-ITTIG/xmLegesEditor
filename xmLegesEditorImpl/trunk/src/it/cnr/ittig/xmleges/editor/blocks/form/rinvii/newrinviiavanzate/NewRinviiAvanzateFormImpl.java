/*
 * Created on Apr 28, 2005 TODO To change the template for this generated file
 * go to Window - Preferences - Java - Code Style - Code Templates
 */
package it.cnr.ittig.xmleges.editor.blocks.form.rinvii.newrinviiavanzate;

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
import it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinviiavanzate.NewRinviiAvanzateForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.text.MaskFormatter;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiAvanzateForm</code>.</h1>
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
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti </a>
 */
public class NewRinviiAvanzateFormImpl implements NewRinviiAvanzateForm, Loggable, KeyListener, Serviceable, Configurable, Initializable, ActionListener,
		FocusListener, FormVerifier {

	Vector urn = new Vector();

	Logger logger;

	Form form;

	UtilUI utilui;

	UtilMsg utilmsg;

	NirUtilUrn nirutilurn;

	// Componenti grafiche

	JLabel labelallegati, labeldenominazione, labeltitolo;

	JTextField denominazione;

	JTextField titolo;

	JComboBox tipocomunicato;

	JFormattedTextField datacomunicato;

	JFormattedTextField dataversione;

	JButton addAllegato;

	JButton delAllegato;

	JButton addComunicato;

	JButton delComunicato;

	JList allegati = new JList();

	JList comunicati = new JList();

	DefaultListModel lmAllegati;

	DefaultListModel lmComunicati;

	Vector vectorAllegati = new Vector();

	Vector vectorComunicati = new Vector();

	String versione = null;

	boolean isAllowAnnessi;

	public Vector getAllegati() {
		return vectorAllegati;
	}

	public Vector getComunicati() {
		return vectorComunicati;
	}

	public String getVersione() {
		return versione.substring(6)+"-"+versione.substring(3,5)+"-"+versione.substring(0,2);	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiAvanzateForm#getUrn()
	 */
	public Vector getUrn() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiAvanzateForm#openForm(java.util.Vector)
	 */
	public boolean openForm(Vector u, boolean allowAnnessi) {
		try {
			isAllowAnnessi = allowAnnessi;

			denominazione.setVisible(isAllowAnnessi);
			titolo.setVisible(isAllowAnnessi);
			addAllegato.setVisible(isAllowAnnessi);
			delAllegato.setVisible(isAllowAnnessi);
			allegati.setVisible(isAllowAnnessi);
			labelallegati.setVisible(isAllowAnnessi);
			labeldenominazione.setVisible(isAllowAnnessi);
			labeltitolo.setVisible(isAllowAnnessi);

			lmAllegati = new DefaultListModel();
			lmComunicati = new DefaultListModel();
			allegati.setModel(lmAllegati);
			comunicati.setModel(lmComunicati);
			azzeraControlli();
			vectorAllegati = new Vector();
			vectorComunicati = new Vector();
			for (int i = 0; i < u.size(); i++) {
				urn.add(u.get(i));
				// ALLEGATI
				if (((Urn) u.get(i)).getAllegati().size() > 0) {
					vectorAllegati = ((Urn) u.get(i)).getAllegati();
					for (int j = 0; j < vectorAllegati.size(); j++) {
						StringTokenizer stpv = new StringTokenizer(vectorAllegati.get(j).toString(), ";");
						StringTokenizer st;
						String allegato = "";
						while (stpv.hasMoreTokens()) {
							st = new StringTokenizer(stpv.nextToken(), ".");
							while (st.hasMoreTokens())
								allegato += st.nextToken() + " ";
						}
						if (!lmAllegati.contains(allegato))
							lmAllegati.addElement(allegato);
					}
					allegati.setModel(lmAllegati);
				}
				// COMUNICATI
				if (((Urn) u.get(i)).getComunicati().size() > 0) {
					vectorComunicati = ((Urn) u.get(i)).getComunicati();
					for (int j = 0; j < vectorComunicati.size(); j++)
						if (!lmComunicati.contains(vectorComunicati.get(j)))
							lmComunicati.addElement(vectorComunicati.get(j));
					comunicati.setModel(lmComunicati);
				}
				// VERSIONE
				if (((Urn) u.get(i)).getVersione() != null && ((Urn) u.get(i)).getVersione().length() > 0) {
					versione = ((Urn) u.get(i)).getVersione();
					// ///////////////////////// Popola Data
					// ////////////////////////////////////////
					String aaaa;
					String mm;
					String gg;
					String dataver = "";
					StringTokenizer stDate = new StringTokenizer(versione, "-");
					if (stDate.countTokens() > 1) {// La data ? indicata nel
													// formato gg-mm-aaaa
						aaaa = stDate.nextToken();
						mm = stDate.nextToken();
						gg = stDate.nextToken();
						dataver += gg + "/" + mm + "/" + aaaa + ", ";
					} else
						dataver += "__/__/" + stDate.nextToken() + ",";

					dataversione.setText(dataver.substring(0, dataver.length() - 1));
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}

		form.setDialogResizable(false);
		form.addFormVerifier(this);
		form.showDialog();
		if (form.isOk())
			return true;
		else
			return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.services.manager.Loggable#enableLogging(it.cnr.ittig.services.manager.Logger)
	 */
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		logger.debug("Avvio servizi");
		form = (Form) serviceManager.lookup(Form.class);
		utilui = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		nirutilurn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		logger.debug("Fine attivazione servizi");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.services.manager.Configurable#configure(it.cnr.ittig.services.manager.Configuration)
	 */
	public void configure(Configuration configuration) throws ConfigurationException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.services.manager.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		logger.debug("Inizializzazione Form NewRinvii");
		form.setMainComponent(this.getClass().getResourceAsStream("Avanzate.jfrm"));
		form.setSize(600, 450);
		form.setName("editor.form.rinvii.newrinvii.avanzate");

		form.setHelpKey("help.contents.form.newrinviiavanzate");
		
		labelallegati = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.label.allegati");
		labeldenominazione = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.label.denominazione");
		labeltitolo = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.label.titolo");
		JLabel labelcomunicati = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.label.comunicati");
		JLabel labeltipo = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.label.tipo");
		JLabel labeldata = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.label.data");
		JLabel labelversione = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.label.versione");
		JLabel labelemissione = (JLabel) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.label.emissione");

		denominazione = (JTextField) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.txtfield.denominazione");
		titolo = (JTextField) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.txtfield.titolo");
		tipocomunicato = (JComboBox) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.combo.tipo");
		datacomunicato = (JFormattedTextField) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.txtfield.data");
		dataversione = (JFormattedTextField) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.txtfield.versione");
		MaskFormatter mf = new MaskFormatter("##/##/####");
		mf.setPlaceholderCharacter('_');
		mf.install(datacomunicato);
		MaskFormatter mf2 = new MaskFormatter("##/##/####");
		mf2.setPlaceholderCharacter('_');
		mf2.install(dataversione);

		allegati = (JList) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.list.allegati");
		comunicati = (JList) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.list.comunicati");

		addAllegato = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.button.addallegato");
		delAllegato = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.button.delallegato");
		addComunicato = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.button.addcomunicato");
		delComunicato = (JButton) form.getComponentByName("editor.form.rinvii.newrinvii.avanzate.button.delcomunicato");

		datacomunicato.addFocusListener(this);
		dataversione.addFocusListener(this);
		dataversione.addKeyListener(this);
		addAllegato.addActionListener(this);
		delAllegato.addActionListener(this);
		addComunicato.addActionListener(this);
		delComunicato.addActionListener(this);

		logger.debug("Fine inizializzazione");
		logger.debug("Inizio internazionalizzazione");
		utilui.applyI18n(labelallegati);
		utilui.applyI18n(labeldenominazione);
		utilui.applyI18n(labeltitolo);
		utilui.applyI18n(labelcomunicati);
		utilui.applyI18n(labeltipo);
		utilui.applyI18n(labeldata);
		utilui.applyI18n(labelversione);
		utilui.applyI18n(labelemissione);
		utilui.applyI18n(addAllegato);
		utilui.applyI18n(delAllegato);
		utilui.applyI18n(addComunicato);
		utilui.applyI18n(delComunicato);
		logger.debug("Fine internazionalizzazione");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */

	private void azzeraControlli() {
		denominazione.setText("");
		titolo.setText("");
		tipocomunicato.removeAllItems();
		tipocomunicato.addItem("");
		tipocomunicato.addItem("errata.corrige");
		tipocomunicato.addItem("rettifica");
		tipocomunicato.addItem("entrata.vigore");
		tipocomunicato.addItem("decadenza");
		tipocomunicato.setSelectedIndex(0);
		datacomunicato.setText("");
		dataversione.setText("");
		if (lmAllegati.size() > 0) {
			lmAllegati.removeAllElements();
			allegati.setModel(lmAllegati);
		}
		if (lmComunicati.size() > 0) {
			lmComunicati.removeAllElements();
			comunicati.setModel(lmComunicati);
		}
		versione = null;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addAllegato)) {// Aggiungo un allegato
			// TODO mettere un controllo per non inserire un allegato gi?
			// esistente ?
			if (checkAllegati()) {
				String nuovoAllegato = denominazione.getText();
				if (nuovoAllegato.trim().length() > 0) {
					StringTokenizer st = new StringTokenizer(nuovoAllegato, " ");
					String urnAllegato = "";
					while (st.hasMoreTokens())
						urnAllegato += st.nextToken().toLowerCase() + ".";
					urnAllegato = urnAllegato.substring(0, urnAllegato.length() - 1);

					String titoloAllegato = titolo.getText();
					st = new StringTokenizer(titoloAllegato, " ");
					urnAllegato += ";";
					while (st.hasMoreTokens())
						urnAllegato += st.nextToken().toLowerCase() + ".";
					urnAllegato = urnAllegato.substring(0, urnAllegato.length() - 1);

					vectorAllegati.add(urnAllegato);
					lmAllegati.addElement(denominazione.getText() + " " + titolo.getText());
					allegati.setModel(lmAllegati);
					denominazione.setText("");
					titolo.setText("");
				} else
					utilmsg.msgError("editor.form.rinvii.newrinvii.avanzate.msg.emptyallegato");
			}
		}
		if (e.getSource().equals(delAllegato)) {// Elimino un allegato
			if (allegati.getSelectedValue() != null) {
				int index = allegati.getSelectedIndex();
				lmAllegati.removeElementAt(index);
				allegati.setModel(lmAllegati);
				vectorAllegati.remove(index);
			} else
				utilmsg.msgError("editor.form.rinvii.newrinvii.avanzate.msg.noselectedallegato");
		}
		if (e.getSource().equals(addComunicato)) {// Aggiungo un comunicato
			// TODO mettere un controllo per non inserire comunicato gi?
			// esistente ?
			if (checkComunicato()) {
				String nuovoComunicato = tipocomunicato.getSelectedItem().toString();
				StringTokenizer stdata = new StringTokenizer(datacomunicato.getText(), "/");
				String gg = stdata.nextToken();
				String mm = stdata.nextToken();
				String aaaa = stdata.nextToken();
				nuovoComunicato += ";" + aaaa + "-" + mm + "-" + gg;

				vectorComunicati.add(nuovoComunicato);
				lmComunicati.addElement(nuovoComunicato);
				comunicati.setModel(lmComunicati);
			}
			tipocomunicato.setSelectedIndex(0);
			datacomunicato.setText("");
		}
		if (e.getSource().equals(delComunicato)) {// Elimino un allegato
			if (comunicati.getSelectedValue() != null) {
				int index = comunicati.getSelectedIndex();
				lmComunicati.removeElementAt(index);
				comunicati.setModel(lmComunicati);
				vectorComunicati.remove(index);
			}

			else
				utilmsg.msgError("editor.form.rinvii.newrinvii.avanzate.msg.noselectedcomunicato");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */

	private boolean checkComunicato() {
		if ((tipocomunicato.getSelectedItem().toString().equals("")) && (!datacomunicato.getText().equals(""))) {
			utilmsg.msgError("editor.form.rinvii.newrinvii.avanzate.msg.comunicato");
			return false;
		} else
			return true;
	}

	private boolean checkAllegati() {
		if (lmAllegati.size() > 0) {
			utilmsg.msgError("editor.form.rinvii.newrinvii.avanzate.msg.allegati");
			return false;
		} else
			return true;
	}

	private boolean checkDate(String data) {
		if (data.equals("__/__/____"))
			return true;
		else {
			String date = data;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date d = sdf.parse(date);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.form.FormVerifier#getErrorMessage()
	 */
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return ("editor.form.rinvii.newrinvii.avanzate.msg.datinonvalidi");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.form.FormVerifier#verifyForm()
	 */
	public boolean verifyForm() {
		if (checkDate(dataversione.getText()))
			return true;
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		if (!checkDate(((JFormattedTextField) e.getSource()).getText()))
			utilmsg.msgError("editor.form.rinvii.newrinvii.avanzate.msg.dateformat");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (checkDate(dataversione.getText()))
			versione = dataversione.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {

	}
}
