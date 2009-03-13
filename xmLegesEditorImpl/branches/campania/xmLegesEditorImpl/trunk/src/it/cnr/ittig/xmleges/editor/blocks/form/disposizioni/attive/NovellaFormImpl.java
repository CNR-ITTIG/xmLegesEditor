package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellaForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispAttiveForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.VirgolettaForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellaForm</code>.
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
public class NovellaFormImpl implements NovellaForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable, FormClosedListener {
	Logger logger;
	I18n i18n;
	
	Form form;
	DispAttiveForm disposizioni;
	VirgolettaForm virgolettaForm;
	
	DocumentManager documentManager;
	EventManager eventManager;
	DtdRulesManager dtdRulesManager;
	SelectionManager selectionManager;
	 
	JLabel etiContenuto;
	JButton sceltaContenuto;
	JLabel etiTipo;
	JTextField virContenuto;
	JComboBox tipo;
	JComboBox primadopo;
	JLabel etiInizio;
	JLabel etiFine;
	JLabel etiPrima;
	JButton sceltoPrima;
	JLabel etiDopo;
	JButton sceltoDopo;
	JLabel etiFra;
	JButton sceltoFra1;
	JButton sceltoFra2;
	JTextField virPrima;
	JTextField virDopo;
	JTextField virFra1;
	JTextField virFra2;
	JRadioButton solocontenuto;
	JRadioButton inizio;
	JRadioButton fine;
	JRadioButton prima;
	JRadioButton dopo;
	JRadioButton fra;
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
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		virgolettaForm = (VirgolettaForm) serviceManager.lookup(VirgolettaForm.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
	}

	public void initialize() throws java.lang.Exception {
		//eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("Novella.jfrm"));
		form.setCustomButtons(null);		
		
		form.setName("editor.form.disposizioni.attive.novella");
		form.setHelpKey("help.contents.form.disposizioniattive.novella");

		avanti = (JButton) form.getComponentByName("editor.form.disposizioni.attive.btn.avanti");
		indietro = (JButton) form.getComponentByName("editor.form.disposizioni.attive.btn.indietro");
		avanti.addActionListener(this);
		indietro.addActionListener(this);
		virContenuto = (JTextField) form.getComponentByName("editor.dispattive.novella.contenuto.vir");
		etiContenuto = (JLabel) form.getComponentByName("editor.dispattive.novella.contenuto.eti");
		sceltaContenuto = (JButton) form.getComponentByName("editor.dispattive.novella.contenuto.scelta");
		sceltaContenuto.addActionListener(this);
		etiTipo = (JLabel) form.getComponentByName("editor.dispattive.novella.tipo.eti");
				
		etiContenuto = (JLabel) form.getComponentByName("editor.dispattive.novella.contenuto.eti");
		tipo = (JComboBox) form.getComponentByName("editor.dispattive.novella.tipo");
		tipo.addActionListener(this);	
		primadopo = (JComboBox) form.getComponentByName("editor.dispattive.novella.primadopo");
		primadopo.addItem("Dopo");
		primadopo.addItem("Prima");
		etiInizio = (JLabel) form.getComponentByName("editor.dispattive.novella.inizio.eti");
		etiFine = (JLabel) form.getComponentByName("editor.dispattive.novella.fine.eti");
		etiPrima = (JLabel) form.getComponentByName("editor.dispattive.novella.prima.eti");
		virPrima = (JTextField) form.getComponentByName("editor.dispattive.novella.prima.vir");
		sceltoPrima = (JButton) form.getComponentByName("editor.dispattive.novella.prima.scelta");
		sceltoPrima.addActionListener(this);	
		etiDopo = (JLabel) form.getComponentByName("editor.dispattive.novella.dopo.eti");
		sceltoDopo = (JButton) form.getComponentByName("editor.dispattive.novella.dopo.scelta");
		sceltoDopo.addActionListener(this);	
		virDopo = (JTextField) form.getComponentByName("editor.dispattive.novella.dopo.vir");
		etiFra = (JLabel) form.getComponentByName("editor.dispattive.novella.fra.eti");
		sceltoFra1 = (JButton) form.getComponentByName("editor.dispattive.novella.fra.scelta1");
		sceltoFra1.addActionListener(this);	
		virFra1 = (JTextField) form.getComponentByName("editor.dispattive.novella.fra.vir1");
		sceltoFra2 = (JButton) form.getComponentByName("editor.dispattive.novella.fra.scelta2");
		sceltoFra2.addActionListener(this);	
		virFra2 = (JTextField) form.getComponentByName("editor.dispattive.novella.fra.vir2");
		solocontenuto = (JRadioButton) form.getComponentByName("editor.dispattive.novellando.solocontenuto");
		inizio = (JRadioButton) form.getComponentByName("editor.dispattive.novellando.inizio");
		fine = (JRadioButton) form.getComponentByName("editor.dispattive.novellando.fine");
		prima = (JRadioButton) form.getComponentByName("editor.dispattive.novellando.prima");
		dopo = (JRadioButton) form.getComponentByName("editor.dispattive.novellando.dopo");
		fra = (JRadioButton) form.getComponentByName("editor.dispattive.novellando.fra");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(solocontenuto);
		grupporadio.add(inizio);
		grupporadio.add(fine);
		grupporadio.add(prima);
		grupporadio.add(dopo);
		grupporadio.add(fra);
		solocontenuto.addActionListener(this);	
		inizio.addActionListener(this);	
		fine.addActionListener(this);	
		prima.addActionListener(this);
		dopo.addActionListener(this);	
		fra.addActionListener(this);	
		form.setSize(320, 350);
	}
	
	private void setTipo() {
		tipo.removeAllItems();
		String[] scelteTipo = new String[] {"allegato","libro","parte","titolo","capo","sezione","articolo","comma","lettera","numero","punto","rubrica","alinea","coda","periodo","capoverso","parole"};
		int numDelimitatori = disposizioni.getDelimitatori().length;
		int da = 0;
		String partizioneMenoGenerica;
		if (numDelimitatori==0) {
			partizioneMenoGenerica = disposizioni.getPartizione();
			if (!"".equals(partizioneMenoGenerica)) {
				if (partizioneMenoGenerica.lastIndexOf("-")!=-1)
					partizioneMenoGenerica = partizioneMenoGenerica.substring(partizioneMenoGenerica.indexOf("-")+1,partizioneMenoGenerica.length());
				if (partizioneMenoGenerica.length()>3)
					partizioneMenoGenerica = partizioneMenoGenerica.substring(0, 3).toLowerCase();
				else 
					partizioneMenoGenerica = partizioneMenoGenerica.toLowerCase();	
			}
		}
		else 
			partizioneMenoGenerica = disposizioni.getDelimitatori()[numDelimitatori-3].substring(0, 3);				                                        	
		for (int i=0; i<scelteTipo.length; i++)
			if (partizioneMenoGenerica.equals(scelteTipo[i].substring(0, 3))) {
				//da = i+1;
				da = i;
				break;
			}
		for (int i=da; i<scelteTipo.length; i++)
			tipo.addItem(scelteTipo[i]);
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
		
		if (e.getSource() == tipo)
			setSelezioni();

		if (e.getSource() == sceltaContenuto || e.getSource() == sceltoPrima || e.getSource() == sceltoDopo || e.getSource() == sceltoFra1 || e.getSource() == sceltoFra2) {
			disposizioni.setListenerFormClosed(false);
			form.close();		
			if (e.getSource() == sceltaContenuto) 
				virgolettaSelezionata = virContenuto;		
			if (e.getSource() == sceltoPrima) 
				virgolettaSelezionata = virPrima;
			if (e.getSource() == sceltoDopo) 
				virgolettaSelezionata = virDopo;
			if (e.getSource() == sceltoFra1) 
				virgolettaSelezionata = virFra1;
			if (e.getSource() == sceltoFra2) 
				virgolettaSelezionata = virFra2;
			virgolettaForm.openForm(this, disposizioni.getModCorrente());
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

	private void initForm() {
		solocontenuto.setSelected(true);
		//	se ho + di una virgoletta scelgo l'ultima per il contenuto
		activeNode = selectionManager.getActiveNode();
		Document doc = documentManager.getDocumentAsDom();
		Node mod = disposizioni.getModCorrente();
		Node[] virgolette = UtilDom.getElementsByTagName(doc, mod, "virgolette");
		if (virgolette.length>0)
			virContenuto.setText(UtilDom.getAttributeValueAsString(virgolette[virgolette.length-1],"id"));
		
		setTipo();
		setSelezioni();
		disposizioni.setListenerFormClosed(true);
		
	}
	
	private void setSelezioni() {
		boolean isParola = "parole".equals(tipo.getSelectedItem());
		primadopo.setEnabled(!isParola);
		sceltoPrima.setEnabled(isParola);
		sceltoDopo.setEnabled(isParola);
		sceltoFra1.setEnabled(isParola);
		sceltoFra2.setEnabled(isParola);
		virPrima.setEnabled(isParola);
		virDopo.setEnabled(isParola);
		virFra1.setEnabled(isParola);
		virFra2.setEnabled(isParola);
		solocontenuto.setEnabled(isParola);
		inizio.setEnabled(isParola);
		fine.setEnabled(isParola);
		prima.setEnabled(isParola);
		dopo.setEnabled(isParola);
		fra.setEnabled(isParola);

	}

	private void setBottoneAvanti() {

		avanti.setEnabled(false);
		if ("".equals(virContenuto.getText().trim()))
			return;
		boolean isParola = "parole".equals(tipo.getSelectedItem());
		if (!isParola || solocontenuto.isSelected() || inizio.isSelected() || fine.isSelected())
			avanti.setEnabled(true);
		else {
			if (prima.isSelected() && !"".equals(virPrima.getText().trim()))
				avanti.setEnabled(true);
			if (dopo.isSelected() && !"".equals(virDopo.getText().trim()))
				avanti.setEnabled(true);
			if (fra.isSelected() && !"".equals(virFra1.getText().trim())
					&& !"".equals(virFra2.getText().trim()))
				avanti.setEnabled(true);
		}			
	}
	
	public void openForm(FormClosedListener listener, Node modificoMetaEsistenti) {

		//avanti.setText(i18n.getTextFor("editor.form.disposizioni.attive.btn.avanti.text"));
		this.listener = listener;
		virPrima.setText("");
		virDopo.setText("");
		virFra1.setText("");
		virFra2.setText("");
		if (modificoMetaEsistenti==null)
			initForm();	//inizializzo la form
		else
			recuperaMeta(modificoMetaEsistenti);
		setBottoneAvanti();
		form.showDialog(listener);
	}

	private void recuperaMeta(Node disposizione) {
		//recupero virgoletta dai meta
		Node novella = UtilDom.findRecursiveChild(disposizione,"dsp:novella");
		if (novella==null)
			initForm();	//Non ho novella -> eseguo l'inizializzazione della form
		else {
			Node node = UtilDom.findRecursiveChild(novella,"dsp:pos");
			String href = UtilDom.getAttributeValueAsString(node,"xlink:href");
			if (href.length()>0)
				href=href.substring(1);
			activeNode = selectionManager.getActiveNode();
			Document doc = documentManager.getDocumentAsDom();
			Node mod = UtilDom.findParentByName(activeNode, "mod");
			//seleziono quello presente nel meta, se ammissibile
			virContenuto.setText("");
			if (mod!=null) {
				Node[] virgolette = UtilDom.getElementsByTagName(doc, mod, "virgolette");
				if (virgolette.length >0) {
					virContenuto.setText(UtilDom.getAttributeValueAsString(virgolette[virgolette.length-1],"id"));
					for (int i=0; i<virgolette.length; i++)
						if (href.equals(UtilDom.getAttributeValueAsString(virgolette[i], "id"))) {
							virContenuto.setText(href);
							break;
						}
				}
			}
			//seleziono tipo
			setTipo();
			//seleziono quello presente nel meta, se ammissibile
			node = UtilDom.findRecursiveChild(novella,"dsp:subarg");
			node = UtilDom.findRecursiveChild(node,"ittig:tipo");
			String valore = UtilDom.getAttributeValueAsString(node,"valore");
			tipo.setSelectedItem(valore);
			//seleziono la posizione
			setSelezioni();
			//seleziono quello presente nel meta, se ammissibile
			Node posizione = UtilDom.findRecursiveChild(disposizione,"dsp:posizione");
			if (posizione!=null) {
				Node tipo1 = UtilDom.findRecursiveChild(posizione,"dsp:pos");
				node = UtilDom.findRecursiveChild(tipo1,"ittig:dove");
				String valore1=null;
				if (node==null) //ho utilizzato la forma compatta (il valore lo trovo in dove di dsp:pos)
					valore1 = UtilDom.getAttributeValueAsString(tipo1,"dove");
				else
					valore1 = UtilDom.getAttributeValueAsString(node,"valore");
				
				solocontenuto.setSelected(true);
				if ("inizio".equals(valore1)) {
					inizio.setSelected(true);
				}
				else if ("fine".equals(valore1)) {
					fine.setSelected(true);
				}
				else if ("prima".equals(valore1)) {
					prima.setSelected(true);
					virPrima.setText(UtilDom.getAttributeValueAsString(tipo1,"xlink:href"));
					if (virPrima.getText().length()>0)
						virPrima.setText(virPrima.getText().substring(1));
				}
				else if ("dopo".equals(valore1)) {
					dopo.setSelected(true);
					virDopo.setText(UtilDom.getAttributeValueAsString(tipo1,"xlink:href"));
					if (virDopo.getText().length()>0)
						virDopo.setText(virDopo.getText().substring(1));
				}
				else {
					fra.setEnabled(true);
					virFra1.setText(UtilDom.getAttributeValueAsString(tipo1,"xlink:href"));
					if (virFra1.getText().length()>0)
						virFra1.setText(virFra1.getText().substring(1));
					Node tipo2 = tipo1.getNextSibling();
					if (tipo2!=null) {
						node = UtilDom.findRecursiveChild(novella,"ittig:tipo");
						virFra2.setText(UtilDom.getAttributeValueAsString(tipo2,"xlink:href"));
						if (virFra2.getText().length()>0)
							virFra2.setText(virFra2.getText().substring(1));
					}
				}
			}
			disposizioni.setListenerFormClosed(true);	
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
	
	public String setMeta(Node meta) {
		String posizione = null;
		String virgolettaA = null;
		String virgolettaB = null;
		if ("parole".equals(tipo.getSelectedItem())) {
			if (fine.isSelected())
				posizione = "fine";
			if (inizio.isSelected())
				posizione = "inizio";
			if (prima.isSelected()) {
				posizione = "prima";
				virgolettaA = virPrima.getText();
			}			
			if (dopo.isSelected()) {
				posizione = "dopo";
				virgolettaA = virDopo.getText();
			}			
			if (fra.isSelected()) {
				posizione = "fra";
				virgolettaA = virFra1.getText();
				virgolettaB = virFra2.getText();
			}
		}
		domDisposizioni.setDOMNovellaDispAttive(meta, virContenuto.getText(), (String) tipo.getSelectedItem(), posizione, virgolettaA, virgolettaB);
		return (String) tipo.getSelectedItem();
	}
}
