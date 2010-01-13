package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispAttiveForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispNonTestualiForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.w3c.dom.Node;


/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive</code>.
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
 */
public class DispNonTestualiFormImpl implements DispNonTestualiForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable, FormClosedListener {

	JRadioButton radioContenuto;
	JRadioButton radioTempo;
	JRadioButton radioPortata;
	JRadioButton radioOrdinamento;
	JComboBox sceltecontenuto;
	JComboBox sceltetempo;
	JComboBox scelteportata;
	JComboBox scelteordinamento;
	JTextField dominioportata;
	JTextField dominiocontenuto;
	JTextField contenutodove;
	JButton sceltacontenutodove;
	
	JButton avanti;
	JButton indietro;
	
	Logger logger;
	EventManager eventManager;
	UtilMsg utilmsg;
	Form form;
	DispAttiveForm disposizioni;
	PartizioniForm partizioniForm;
	
	FormClosedListener listener;
	
	DocumentManager documentManager;	
	SelectionManager selectionManager;
	Disposizioni domDisposizioni;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		disposizioni = (DispAttiveForm) serviceManager.lookup(DispAttiveForm.class);
		partizioniForm = (PartizioniForm) serviceManager.lookup(PartizioniForm.class);
		domDisposizioni = (Disposizioni) serviceManager.lookup(Disposizioni.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("DispNonTestuali.jfrm"));
		form.setName("editor.form.disposizioni.attive.nontestuali");
		form.setCustomButtons(null);	
		
		radioContenuto = (JRadioButton) form.getComponentByName("editor.disposizioni.attive.ntcontenuto");
		radioTempo = (JRadioButton) form.getComponentByName("editor.disposizioni.attive.nttempo");
		radioPortata = (JRadioButton) form.getComponentByName("editor.disposizioni.attive.ntportata");
		radioOrdinamento = (JRadioButton) form.getComponentByName("editor.disposizioni.attive.ntordinamento");
		radioContenuto.addActionListener(this);
		radioTempo.addActionListener(this);
		radioPortata.addActionListener(this);
		radioOrdinamento.addActionListener(this);
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(radioContenuto);
		grupporadio.add(radioTempo);
		grupporadio.add(radioPortata);
		grupporadio.add(radioOrdinamento);
		dominioportata = (JTextField) form.getComponentByName("editor.disposizioni.attive.dominioportata");
		dominiocontenuto = (JTextField) form.getComponentByName("editor.disposizioni.attive.dominiocontenuto");
		contenutodove = (JTextField) form.getComponentByName("editor.disposizioni.attive.contenutodove");
		sceltacontenutodove = (JButton) form.getComponentByName("editor.disposizioni.attive.sceltacontenutodove");
		sceltecontenuto = (JComboBox) form.getComponentByName("editor.disposizioni.attive.sceltecontenuto");
		sceltecontenuto.addActionListener(this);
		sceltetempo = (JComboBox) form.getComponentByName("editor.disposizioni.attive.sceltetempo");
		scelteportata = (JComboBox) form.getComponentByName("editor.disposizioni.attive.scelteportata");
		scelteordinamento = (JComboBox) form.getComponentByName("editor.disposizioni.attive.scelteordinamento");
		
		avanti = (JButton) form.getComponentByName("editor.form.disposizioni.attive.btn.avanti");
		indietro = (JButton) form.getComponentByName("editor.form.disposizioni.attive.btn.indietro");
		avanti.addActionListener(this);
		indietro.addActionListener(this);
		form.setSize(420, 320);
	}
	

	public void actionPerformed(ActionEvent e) {
		
		if (!form.isDialogVisible())
			return;
		
		if (e.getSource() == sceltecontenuto) {
			abilitaControlli(sceltecontenuto);
		}
		
		if (e.getSource()==radioContenuto) {
			abilitaControlli(sceltecontenuto);
		}
		if (e.getSource() == radioTempo) {
			abilitaControlli(sceltetempo);
		}
		if (e.getSource() == radioPortata) {
			abilitaControlli(scelteportata);
		}
		if (e.getSource() == radioOrdinamento) {
			abilitaControlli(scelteordinamento);
		}
		if (e.getSource() == sceltacontenutodove) {
			if (partizioniForm.openForm()) {
				String partizioneSelezionata = partizioniForm.getPartizioneEstesa();
				contenutodove.setText(makeSub(partizioneSelezionata));
			}	
		}
		if (e.getSource() == avanti) {
			disposizioni.setOperazioneProssima(DispAttiveForm.FINE);
			form.close();
			return;
		}
		if (e.getSource() == indietro) {
			form.close();
			return;
		}
		setBottoneAvanti();
	}
	private void recuperaMeta(Node disposizione) {
		//da implementare
	}
	
	public void openForm(FormClosedListener listener, Node modificoMetaEsistenti) {

		this.listener = listener;
		disposizioni.setListenerFormClosed(true);
//		if (modificoMetaEsistenti==null)
			initControlli();	//inizializzo la form
//		else
//			recuperaMeta(modificoMetaEsistenti);
		setBottoneAvanti();
		form.showDialog(listener);
	}
		
	private void setBottoneAvanti() {

		avanti.setEnabled(false);
		if (radioContenuto.isSelected() && "ricollocazione".equals(sceltecontenuto.getSelectedItem()))
			avanti.setEnabled(!"".equals(contenutodove.getText().trim()));
		else
			avanti.setEnabled(true);
		
		
		
		if ("ricollocazione".equals(sceltecontenuto.getSelectedItem())) {
			sceltacontenutodove.addActionListener(this);
			dominiocontenuto.setEnabled(false);
		} else {
			sceltacontenutodove.removeActionListener(this);
			dominiocontenuto.setEnabled(true);
		}
		
	}
	
	public void setMeta(Node meta) { 
		
		String partizioneSelezionata = null;
		String dominioSelezionato = null;
		if (radioContenuto.isSelected())
			if ("ricollocazione".equals(sceltecontenuto.getSelectedItem()))
				partizioneSelezionata = contenutodove.getText().trim();
			else
				dominioSelezionato = dominiocontenuto.getText().trim();
		if (radioPortata.isSelected())
			dominioSelezionato = dominioportata.getText().trim();
		
		domDisposizioni.setDOMNonTestualiAttive(meta, partizioneSelezionata, dominioSelezionato);
	}
	
	private String makeSub(String analizza) {
		StringTokenizer st = new StringTokenizer(analizza, " ");
		String token;
		String ret ="";
		boolean dispari = ((st.countTokens() % 2)==0) ? true : false;
		analizza="";
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (dispari) {
				analizza += token.toLowerCase();
				dispari = false;
				if (token.length()>3)
					ret = ret + token.substring(0, 3).toLowerCase();
				else ret = ret + token.toLowerCase();	//non dovrebbe capitare mai
			}
			else {
				analizza += token+", ";
				dispari = true;
				ret = ret + token.toLowerCase() + "-";
			}
		}
		if (analizza.length()>1)
			analizza = analizza.substring(0,analizza.length());
		if (ret.length()>1)
			return ret.substring(0, ret.length()-1);
		else
			return ret;
	}

	public void manageEvent(EventObject event) {
		if (form.isDialogVisible()) 
			if (event instanceof DocumentClosedEvent) 
				form.close();
	}
	
	private void initControlli() {
		
		sceltecontenuto.addItem("ricollocazione");
		sceltecontenuto.addItem("intautentica");
		sceltecontenuto.addItem("variazione");
		sceltecontenuto.addItem("modtermini");
		sceltecontenuto.setSelectedIndex(0);
		
		sceltetempo.addItem("vigenza");
		sceltetempo.addItem("annullamento");
		sceltetempo.addItem("proroga");
		sceltetempo.addItem("reviviscenza");
		sceltetempo.addItem("posticipo");
		sceltetempo.addItem("sospensione");
		sceltetempo.addItem("retroattivita");
		sceltetempo.addItem("utrattivita");
		sceltetempo.addItem("inapplicazione");
		sceltetempo.setSelectedIndex(0);
		
		scelteportata.addItem("deroga");
		scelteportata.addItem("estensione");
		scelteportata.setSelectedIndex(0);
		
		scelteordinamento.addItem("recepisce");
		scelteordinamento.addItem("attua");
		scelteordinamento.addItem("ratifica");
		scelteordinamento.addItem("attuadelegifica");
		scelteordinamento.addItem("converte");
		scelteordinamento.addItem("reitera");
		scelteordinamento.setSelectedIndex(0);
		
		abilitaControlli(sceltetempo);
		radioTempo.setSelected(true);
		contenutodove.setEnabled(false);
		sceltacontenutodove.removeActionListener(this);
		
		dominioportata.setText("");
		dominiocontenuto.setText("");
		contenutodove.setText("");
	}
	
	private void abilitaControlli(JComboBox attivo) {
		sceltetempo.setEnabled(false);
		sceltecontenuto.setEnabled(false);
		scelteportata.setEnabled(false);
		scelteordinamento.setEnabled(false);
		attivo.setEnabled(true);
		if (attivo == sceltecontenuto) {
			if ("ricollocazione".equals(sceltecontenuto.getSelectedItem())) {
				sceltacontenutodove.addActionListener(this);
				dominiocontenuto.setEnabled(false);
			} else {
				sceltacontenutodove.removeActionListener(this);
				dominiocontenuto.setEnabled(true);
			}
		}
		else { 
			sceltacontenutodove.removeActionListener(this);
			dominiocontenuto.setEnabled(false);
		}
		if (attivo == scelteportata) 
			dominioportata.setEnabled(true);
		else 
			dominioportata.setEnabled(false);
	}

	public void formClosed() {
		form.showDialog(listener);
	}

	public String getDSP() {
		if (radioContenuto.isSelected())
			return "dsp:"+sceltecontenuto.getSelectedItem();
		if (radioTempo.isSelected())
			return "dsp:"+sceltetempo.getSelectedItem();
		if (radioPortata.isSelected())
			return "dsp:"+scelteportata.getSelectedItem();
		if (radioOrdinamento.isSelected())
			return "dsp:"+scelteordinamento.getSelectedItem();
		return null;
	}
}
