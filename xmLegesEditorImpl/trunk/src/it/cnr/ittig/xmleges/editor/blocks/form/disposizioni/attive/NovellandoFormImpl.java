package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispAttiveForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellandoForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.VirgolettaForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellandoForm</code>.
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
public class NovellandoFormImpl implements NovellandoForm,
		EventManagerListener, Loggable, ActionListener, Serviceable,
		Initializable, FormClosedListener {
	Logger logger;

	I18n i18n;

	Form form;

	DispAttiveForm disposizioni;

	VirgolettaForm virgolettaForm;
	
	DocumentManager documentManager;
	EventManager eventManager;
	RulesManager rulesManager;
	SelectionManager selectionManager;

	JRadioButton parole;

	JRadioButton partiz_porz;

	JLabel etiPalole;
	JLabel etiPosizionamento;
	JLabel etiContenuto;
	JLabel etiDelimitatori;
	JLabel etiPartenza;
	JLabel etiArrivo;
	JRadioButton sceltocontenuto;
	JRadioButton sceltodelimitatori;
	JComboBox valorePosizionamento;
	JComboBox valorePartenza;
	JComboBox valoreArrivo;
	JTextField virPosizionamento;
	JTextField virContenuto;
	JTextField virPartenza;
	JTextField virArrivo;
	JButton sceltaPosizionamento;
	JButton sceltaContenuto;
	JButton sceltaPartenza;
	JButton sceltaArrivo;
	JButton avanti;
	JButton indietro;
	
	JTextField virgolettaSelezionata;
	Node activeNode;
	
	FormClosedListener listener;
	Disposizioni domDisposizioni;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		disposizioni = (DispAttiveForm) serviceManager.lookup(DispAttiveForm.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		virgolettaForm = (VirgolettaForm) serviceManager.lookup(VirgolettaForm.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
	}

	public void initialize() throws java.lang.Exception {
		//eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream(
				"Novellando.jfrm"));
		form.setCustomButtons(null);

		form.setName("editor.form.disposizioni.attive.novellando");
		form.setHelpKey("help.contents.form.disposizioniattive.novellando");

		avanti = (JButton) form.getComponentByName("editor.form.disposizioni.attive.btn.avanti");
		indietro = (JButton) form.getComponentByName("editor.form.disposizioni.attive.btn.indietro");
		avanti.addActionListener(this);
		indietro.addActionListener(this);
		parole = (JRadioButton) form.getComponentByName("editor.dispattive.novellando.parole");
		parole.addActionListener(this);
		partiz_porz = (JRadioButton) form
				.getComponentByName("editor.dispattive.novellando.partiz-porz");
		partiz_porz.addActionListener(this);
		ButtonGroup grupporadio1 = new ButtonGroup();
		grupporadio1.add(parole);
		grupporadio1.add(partiz_porz);
		etiPalole = (JLabel) form
				.getComponentByName("editor.dispattive.novellando.parole.eti");
		etiPosizionamento = (JLabel) form
				.getComponentByName("editor.dispattive.novellando.posizionamento.eti");
		etiContenuto = (JLabel) form
				.getComponentByName("editor.dispattive.novellando.contenuto.eti");
		etiDelimitatori = (JLabel) form
				.getComponentByName("editor.dispattive.novellando.delimitatori.eti");
		etiPartenza = (JLabel) form
				.getComponentByName("editor.dispattive.novellando.partenza.eti");
		etiArrivo = (JLabel) form
				.getComponentByName("editor.dispattive.novellando.arrivo.eti");
		sceltocontenuto = (JRadioButton) form
				.getComponentByName("editor.dispattive.novellando.contenuto");
		sceltodelimitatori = (JRadioButton) form
				.getComponentByName("editor.dispattive.novellando.delimitatori");
		sceltocontenuto.addActionListener(this);
		sceltodelimitatori.addActionListener(this);
		ButtonGroup grupporadio2 = new ButtonGroup();
		grupporadio2.add(sceltocontenuto);
		grupporadio2.add(sceltodelimitatori);
		valorePosizionamento = (JComboBox) form
				.getComponentByName("editor.dispattive.novellando.posizionamento");
		valorePartenza = (JComboBox) form
				.getComponentByName("editor.dispattive.novellando.partenza");
		valoreArrivo = (JComboBox) form
				.getComponentByName("editor.dispattive.novellando.arrivo");
		valorePosizionamento.addActionListener(this);
		valorePartenza.addActionListener(this);
		valoreArrivo.addActionListener(this);
		popolaControlli(valorePosizionamento);
		popolaControlli(valorePartenza);
		popolaControlli(valoreArrivo);
		virPosizionamento = (JTextField) form.getComponentByName("editor.dispattive.novellando.posizionamento.vir");
		virContenuto = (JTextField) form.getComponentByName("editor.dispattive.novellando.contenuto.vir");
		virPartenza = (JTextField) form.getComponentByName("editor.dispattive.novellando.partenza.vir");
		virArrivo = (JTextField) form.getComponentByName("editor.dispattive.novellando.arrivo.vir");
		sceltaPosizionamento = (JButton) form.getComponentByName("editor.dispattive.novellando.posizionamento.scelta");
		sceltaContenuto = (JButton) form.getComponentByName("editor.dispattive.novellando.contenuto.scelta");
		sceltaPartenza = (JButton) form.getComponentByName("editor.dispattive.novellando.partenza.scelta");
		sceltaArrivo = (JButton) form.getComponentByName("editor.dispattive.novellando.arrivo.scelta");
		sceltaPosizionamento.addActionListener(this);
		sceltaContenuto.addActionListener(this);
		sceltaPartenza.addActionListener(this);
		sceltaArrivo.addActionListener(this);

		form.setSize(380, 300);
	}
	
	private void popolaControlli(JComboBox combo) {
		combo.addItem(" ");
		combo.addItem("da");
		combo.addItem("a");
		combo.addItem("prima");
		combo.addItem("dopo");
	}
	
	public void manageEvent(EventObject event) {
		if (form.isDialogVisible()) {
			if (event instanceof DocumentClosedEvent) 
				form.close();
//			else {
//				SelectionChangedEvent e = (SelectionChangedEvent) event;
//				activeNode = ((SelectionChangedEvent) event).getActiveNode();
//			}
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (!form.isDialogVisible())
			return;
		if (e.getSource() == sceltocontenuto)
			setSceltaDelimitatori(false);
		if (e.getSource() == sceltodelimitatori)
			setSceltaDelimitatori(true);

		if (e.getSource() == parole || e.getSource() == partiz_porz)
			setSceltaParole();
		
		if (e.getSource() == sceltaPosizionamento || e.getSource() == sceltaContenuto || e.getSource() == sceltaPartenza || e.getSource() == sceltaArrivo) {
			disposizioni.setListenerFormClosed(false);
			form.close();		
			if (e.getSource() == sceltaContenuto) 
				virgolettaSelezionata = virContenuto;			
			if (e.getSource() == sceltaPosizionamento)
				virgolettaSelezionata = virPosizionamento;
			if (e.getSource() == sceltaPartenza)
				virgolettaSelezionata = virPartenza;
			if (e.getSource() == sceltaArrivo) 
				virgolettaSelezionata = virArrivo;
			virgolettaForm.openForm(this, disposizioni.getModCorrente());
		}
		
		if (e.getSource() == avanti) {
			
			if (disposizioni.getOperazioneIniziale()==DispAttiveForm.SOSTITUZIONE)
				disposizioni.setOperazioneProssima(DispAttiveForm.NOVELLA);
			else
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

	private void initForm() {
		
		//1)	se ho 2 virgolette (di TIPO parola) allora Scommetto su Delimitatori (con check PAROLE)
		//2)	se ho 1 virgoletta allora Scommetto su Contenuto (con check PAROLE)
		//3)	altrimenti (senza check PAROLE)   
		
		virgolettaSelezionata=null;
		disposizioni.setListenerFormClosed(true);
		valorePosizionamento.setSelectedItem(" ");
		valorePartenza.setSelectedItem(" ");
		valoreArrivo.setSelectedItem(" ");
		virPosizionamento.setText("");
		virContenuto.setText("");
		virPartenza.setText("");
		virArrivo.setText("");
		activeNode = selectionManager.getActiveNode();
		Document doc = documentManager.getDocumentAsDom();
		Node mod = disposizioni.getModCorrente();
		Node[] virgolette = UtilDom
				.getElementsByTagName(doc, mod, "virgolette");
		int trovate = virgolette.length;
		switch (trovate) {
		case 2:
			parole.setSelected(true);
			partiz_porz.setSelected(false);
			setSceltaDelimitatori(true);
			virPartenza.setText(UtilDom.getAttributeValueAsString(
					virgolette[0], "id"));
			virArrivo.setText(UtilDom.getAttributeValueAsString(virgolette[1],
					"id"));
			setSceltaParole();
			break;
		case 1:
			parole.setSelected(true);
			partiz_porz.setSelected(false);
			setSceltaDelimitatori(false);
			virContenuto.setText(UtilDom.getAttributeValueAsString(
					virgolette[0], "id"));
			virPartenza.setText("");
			virArrivo.setText("");
			setSceltaParole();
			break;
		default:
			parole.setSelected(false);
			partiz_porz.setSelected(true);
			setSceltaDelimitatori(false);
			setSceltaParole();
		}
	}

	private void setSceltaParole() {
		boolean valore = parole.isSelected();	
		sceltocontenuto.setEnabled(valore);
		sceltodelimitatori.setEnabled(valore);
		valorePosizionamento.setEnabled(valore);
		sceltaPosizionamento.setEnabled(valore);
		sceltaContenuto.setEnabled(valore);
		virContenuto.setEnabled(valore);
		virPosizionamento.setEnabled(valore);
		valorePartenza.setEnabled(valore);
		valoreArrivo.setEnabled(valore);
		virPartenza.setEnabled(valore);
		virArrivo.setEnabled(valore);
		sceltaPartenza.setEnabled(valore);
		sceltaArrivo.setEnabled(valore);
	}
	
	private void setSceltaDelimitatori(boolean valore) {
		sceltocontenuto.setSelected(!valore);	
		valorePosizionamento.setEnabled(!valore);
		sceltaPosizionamento.setEnabled(!valore);
		sceltaContenuto.setEnabled(!valore);
		virContenuto.setEnabled(!valore);
		virPosizionamento.setEnabled(!valore);
		
		sceltodelimitatori.setSelected(valore);
		valorePartenza.setEnabled(valore);
		valoreArrivo.setEnabled(valore);
		virPartenza.setEnabled(valore);
		virArrivo.setEnabled(valore);
		sceltaPartenza.setEnabled(valore);
		sceltaArrivo.setEnabled(valore);
	}
	
	private void setBottoneAvanti() {
		/*
		 * 1)	se non ho scelto parole posso proseguire.
		 * 2)	se ho scelto parole+Delimitatori devo avere almeno Partenza/Arrivo selezionato (valore+virgoletta).
		 * 3)	se ho scelto parole+Contenuto devo avere almento contenuto+virgoletta impostato;
		 * 		inoltre, posizionamento e posizionamentoVir ci devono essere entrambi o nessuno.
		 */

		avanti.setEnabled(false);
		if (!parole.isSelected()) 		//caso1
			avanti.setEnabled(true);
		else
			if (sceltodelimitatori.isSelected()) {		//caso2
				boolean partenza1 = " ".equals(valorePartenza.getSelectedItem());
				boolean partenza2 = "".equals(virPartenza.getText().trim());
				boolean partenzaValida = (!partenza1) && (!partenza2);
				boolean arrivo1 = " ".equals(valoreArrivo.getSelectedItem());
				boolean arrivo2 = "".equals(virArrivo.getText().trim());
				boolean arrivoValido = (!arrivo1) && (!arrivo2);
				boolean selezioniParziali = (partenza1 && !partenza2) || (!partenza1 && partenza2) ||
												(arrivo1 && !arrivo2) || (!arrivo1 && arrivo2);
				avanti.setEnabled((partenzaValida || arrivoValido) && !selezioniParziali);
			}
			else 
				if (sceltocontenuto.isSelected()) {		//caso3
					boolean contenuto = "".equals(virContenuto.getText().trim());
					boolean posizionamento1 = " ".equals(valorePosizionamento.getSelectedItem());
					boolean posizionamento2 = "".equals(virPosizionamento.getText().trim());
					boolean posizionamentoValido = posizionamento1 == posizionamento2;
					boolean selezioniParziali = (posizionamento1 && !posizionamento2) || (!posizionamento1 && posizionamento2);
					avanti.setEnabled(!contenuto && posizionamentoValido && !selezioniParziali);
				}
		
	}
	
	public void openForm(FormClosedListener listener, Node modificoMetaEsistenti) {
			
		//avanti.setText(i18n.getTextFor("editor.form.disposizioni.attive.btn.avanti.text"));
		this.listener = listener;
		if (modificoMetaEsistenti==null)
			initForm();	//inizializzo la form
		else
			recuperaMeta(modificoMetaEsistenti);
		setBottoneAvanti();
		form.showDialog(listener);
	}

	private void recuperaMeta(Node disposizione) {
		
		Node novellando = UtilDom.findRecursiveChild(disposizione,"dsp:novellando");
		if (novellando==null)
			initForm();	//Non ho novellando -> eseguo l'inizializzazione della form
		else {
			valorePosizionamento.setSelectedItem(" ");
			valorePartenza.setSelectedItem(" ");
			valoreArrivo.setSelectedItem(" ");
			virPosizionamento.setText("");
			virContenuto.setText("");
			virPartenza.setText("");
			virArrivo.setText("");
			virgolettaSelezionata=null;
			disposizioni.setListenerFormClosed(true);
			String valVirPosizionamento="";
			String valVirContenuto="";
			String valVirPartenza="";
			String valVirArrivo="";
			/*	
			 * 	  ***	recupero virgoletta dai meta	***
			 * 
			 *    pos1 = null     	No parole  (***A***)
			 *    		(else)		parole (test con pos2)
			 * 
			 *    pos2 = null		Se pos1 ha anche un figlio Ruolo		(1° delimitatore (***B***))
			 *            			(else)									(1° contenuto (***C***))
			 *            
			 * 			(else)		Se pos1 e pos2 hanno un figlio Ruolo	(1° e 2° delimitatore (***D***))
			 * 						(else)									(1° e 2° contenuto (***E***))
			 */
			Node pos1 = UtilDom.findRecursiveChild(novellando,"dsp:pos");
			if (pos1 == null) {
				//	(***A***)
				parole.setSelected(false);
				partiz_porz.setSelected(true);
				setSceltaDelimitatori(false);
				setSceltaParole();
			} else {
				parole.setSelected(true);
				partiz_porz.setSelected(false);
				Node pos2 = pos1.getNextSibling();
				while (pos2!=null && "dsp:subarg".equals(pos2.getNodeName()))
					pos2 = pos2.getNextSibling();	//salto eventuali SubArgomenti
				Node ruolo1 = UtilDom.findRecursiveChild(pos1,"ittig:ruolo");
				if (pos2==null) {
					if (ruolo1 == null) {
						// (***C***)
						valVirContenuto = UtilDom.getAttributeValueAsString(
								pos1, "xlink:href");
						if (valVirContenuto.length()>0)
							valVirContenuto=valVirContenuto.substring(1);
						setSceltaDelimitatori(false);
					} else {
						// (***B***)
						valorePartenza.setSelectedItem(UtilDom
								.getAttributeValueAsString(ruolo1, "valore"));
						valVirPartenza = UtilDom.getAttributeValueAsString(
								pos1, "xlink:href");
						if (valVirPartenza.length()>0)
							valVirPartenza=valVirPartenza.substring(1);
						setSceltaDelimitatori(true);
					}
				} else {
					Node ruolo2 = UtilDom.findRecursiveChild(pos2,"ittig:ruolo");
					if (ruolo2 == null) {
						// (***E***)
						valVirContenuto = UtilDom.getAttributeValueAsString(
								pos2, "xlink:href"); // scambio pos1 e 2
						if (valVirContenuto.length()>0)
							valVirContenuto=valVirContenuto.substring(1);
						valorePosizionamento.setSelectedItem(UtilDom
								.getAttributeValueAsString(ruolo1, "valore"));
						valVirPosizionamento = UtilDom
								.getAttributeValueAsString(pos1, "xlink:href");
						if (valVirPosizionamento.length()>0)
							valVirPosizionamento=valVirPosizionamento.substring(1);
						setSceltaDelimitatori(false);
					} else {
						// (***D***)
						valorePartenza.setSelectedItem(UtilDom
								.getAttributeValueAsString(ruolo1, "valore"));
						valVirPartenza = UtilDom.getAttributeValueAsString(
								pos1, "xlink:href");
						if (valVirPartenza.length()>0)
							valVirPartenza=valVirPartenza.substring(1);
						valoreArrivo.setSelectedItem(UtilDom
								.getAttributeValueAsString(ruolo2, "valore"));
						valVirArrivo = UtilDom.getAttributeValueAsString(pos2,
								"xlink:href");
						if (valVirArrivo.length()>0)
							valVirArrivo=valVirArrivo.substring(1);
						setSceltaDelimitatori(true);
					}				
				}
				setSceltaParole();
			}
			//testo se ammissibili le virgolette selezionate (i ruoli non necessitano di test)
			activeNode = selectionManager.getActiveNode();
			Document doc = documentManager.getDocumentAsDom();
			Node mod = UtilDom.findParentByName(activeNode, "mod");
			if (mod==null)
				return;
			Node[] virgolette = UtilDom.getElementsByTagName(doc, mod, "virgolette");
			if (virgolette!=null) {
				String href;
				for (int i=0; i<virgolette.length; i++) {
					href = UtilDom.getAttributeValueAsString(virgolette[i], "id");
					if (!valVirPosizionamento.equals("") && valVirPosizionamento.equals(href))
						virPosizionamento.setText(href);
					if (!valVirContenuto.equals("") && valVirContenuto.equals(href))
						virContenuto.setText(href);
					if (!valVirPartenza.equals("") && valVirPartenza.equals(href))
						virPartenza.setText(href);
					if (!valVirArrivo.equals("") && valVirArrivo.equals(href))
						virArrivo.setText(href);
				}	
			}			
		}
	}
	
	public void formClosed() {
		
		if (virgolettaSelezionata!=null) {	//ho chiuso la maschera per scegliere le virgolette
			String valore = virgolettaForm.getVirgoletta();
			if (valore!=null)	//ho scelto NO
				virgolettaSelezionata.setText(valore);
			disposizioni.setListenerFormClosed(true);			
			setBottoneAvanti();
		}			
		form.showDialog(listener);
	}
	
	public void setMeta(Node meta, String tipoPartizione) {
		String tipo = null; 
		String ruoloA = null; 
		String virgolettaA = null; 
		String ruoloB = null;
		String virgolettaB = null;
		if (sceltocontenuto.isSelected()) {
			tipo = "contenuto";  
			virgolettaA = virContenuto.getText(); 
			if (!" ".equals(valorePosizionamento.getSelectedItem())) { 
				ruoloB = (String) valorePosizionamento.getSelectedItem();
				virgolettaB = virPosizionamento.getText();
			}
		} else {
			tipo = "delimitatori"; 
			if (" ".equals(valorePartenza.getSelectedItem())) { //ha compilato solo il 2° delimitatore
				ruoloA = (String) valoreArrivo.getSelectedItem();
				virgolettaA = virArrivo.getText();
			}
			else {
				ruoloA = (String) valorePartenza.getSelectedItem();
				virgolettaA = virPartenza.getText();
				if (!" ".equals(valoreArrivo.getSelectedItem())) { //ha compilato entrambi i delimitatori
					ruoloB = (String) valoreArrivo.getSelectedItem();
					virgolettaB = virArrivo.getText();
				}	
			}
		}
		domDisposizioni.setDOMNovellandoDispAttive(meta, parole.isSelected(), tipoPartizione, tipo, ruoloA, virgolettaA, ruoloB, virgolettaB);
	}
}
