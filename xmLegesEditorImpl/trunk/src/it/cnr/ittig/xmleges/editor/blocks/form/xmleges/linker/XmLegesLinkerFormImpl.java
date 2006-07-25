package it.cnr.ittig.xmleges.editor.blocks.form.xmleges.linker;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.form.xmleges.linker.XmLegesLinkerForm;
import it.cnr.ittig.xmleges.editor.services.xmleges.linker.XmLegesLinker;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.parser.riferimenti.RiferimentiForm</code>.
 * </h1>
 * <h1>Gestione della form per l'avvio del parser dei riferimenti</h1>
 * <h1>Configurazione:ParserRiferimentiFormImpl.xconfig - contiene l'elenco
 * delle regioni con le relative urn</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.ui.UtilUI:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.parser.riferimenti.ParserRiferimenti:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
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
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti </a>, <a
 *         href="mailto:mirco.taddei@gmail.com">Mirco Taddei </a>
 */
public class XmLegesLinkerFormImpl implements XmLegesLinkerForm, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Form form;

	XmLegesLinker parser;

	UtilUI utilUI;

	DocumentManager dm;

	UtilMsg utilMsg;

	JComboBox regione, ente;

	JTabbedPane tabbedPane;

	JTextArea sorgente;

	JTextArea risultati;
	
	AbstractButton analizza;

	JRadioButton radioSel;

	JRadioButton radioDoc;
	
	JCheckBox checkInt;
	
	JCheckBox checkInc;

	String[] elencoregioni = new String[22];
	
	String[] elencoregioniUrn = new String[22];
	
	String[] elencoenti = new String[5];
	
	String[] elencoentiUrn = new String[5];

	File fileRisultato;

	String risultato;

	boolean parseAll = false;

	boolean parsedAll = false;

	int tipo;

	final static int TESTO = 0;

	final static int NODO = 1;

	final static int NODI = 2;

	String text;

	Node node;

	Node[] nodes;

	String risText;

	String risNode;

	String[] risNodes;

	String risAll;

	String error;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		parser = (XmLegesLinker) serviceManager.lookup(XmLegesLinker.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		dm = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			Configuration[] elencoConf = configuration.getChildren();
			int j=0, k=0;
			for (int i = 0; i < elencoConf.length; i++){
				if(elencoConf[i].getName().toLowerCase().startsWith("region")){
				   elencoregioni[j] = elencoConf[i].getAttribute("name");
				   elencoregioniUrn[j++] = elencoConf[i].getAttribute("urn");
				}
				else{
				   elencoenti[k]= elencoConf[i].getAttribute("name");	
				   elencoentiUrn[k++]= elencoConf[i].getAttribute("urn");
				}
			}
		} catch (ConfigurationException e) {
			logger.error("Impossibile leggere il file di configurazione");
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("xmLegesLinker.jfrm"));
		form.setSize(700, 500);
		form.setName("editor.form.xmleges.link");
		regione = (JComboBox) form.getComponentByName("editor.form.xmleges.link.regione");
		ente = (JComboBox) form.getComponentByName("editor.form.xmleges.link.ente");
		// FIXME si potrebbe mettere anche editabile ma al parser non si passa questa stringa ma la urn dell'ente selezionato
		//ente.setEditable(true);
		tabbedPane = (JTabbedPane) form.getComponentByName("editor.form.xmleges.link.tab");
		sorgente = (JTextArea) form.getComponentByName("editor.form.xmleges.link.sorgente");
		sorgente.setWrapStyleWord(true);
		risultati = (JTextArea) form.getComponentByName("editor.form.xmleges.link.risultati");
		risultati.setWrapStyleWord(true);
		
		radioDoc = (JRadioButton) form.getComponentByName("editor.form.xmleges.link.radio.intero");
		radioDoc.setAction(new radioDocAction());
		
		radioSel = (JRadioButton) form.getComponentByName("editor.form.xmleges.link.radio.selezione");
		radioSel.setAction(new radioSelAction());
		
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(radioDoc);
		grupporadio.add(radioSel);
		
		
		checkInt = (JCheckBox) form.getComponentByName("editor.form.xmleges.link.check.interni");
		checkInc = (JCheckBox) form.getComponentByName("editor.form.xmleges.link.check.incompleti");
		
		analizza = (AbstractButton) form.getComponentByName("editor.form.xmleges.link.analizza");
		analizza.setAction(new AnalizzaDocAction());
		
		regione.removeAllItems();
		for (int i = 0; i < elencoregioni.length; i++)
			regione.addItem(elencoregioni[i]);
		
		ente.removeAllItems();
		for (int i = 0; i < elencoenti.length; i++)
			ente.addItem(elencoenti[i]);
	}

	// ///////////////////////////////////////// ParserRiferimentiForm Interface
	public boolean openForm(String text) {
		this.text = text;
		this.tipo = TESTO;
		setSorgente(text);
		return openForm();
	}

	public boolean openForm(Node node) {
		this.node = node;
		this.tipo = NODO;
		setSorgente(UtilDom.domToString(node, true));
		return openForm();
	}

	public boolean openForm(Node[] nodes) {
		this.nodes = nodes;
		this.tipo = NODI;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nodes.length; i++) {
			sb.append(UtilDom.domToString(nodes[i], true));
			sb.append('\n');
		}
		setSorgente(sb.toString());
		return openForm();
	}

	public void setParseAll(boolean all) {
		this.parseAll = all;
	}

	public boolean isParseAll() {
		return parsedAll;
	}

	public String getResultForText() {
		return this.risText;
	}

	public String getResultForNode() {
		return this.risNode;
	}

	public String[] getResultForNodes() {
		return this.risNodes;
	}

	public String getResultForAll() {
		return this.risAll;
	}

	protected boolean openForm() {
		if (!form.hasMainComponent())
			try {
				initialize();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		radioSel.setEnabled(!parseAll);
		radioDoc.setSelected(true);
		checkInt.setEnabled(true);
		checkInc.setEnabled(true);
		checkInt.setSelected(true);
		checkInc.setSelected(true);
		regione.setSelectedIndex(0);
		ente.setSelectedIndex(0);
		form.showDialog();
		return form.isOk();
	}

	protected void setSorgente(String text) {
		tabbedPane.setSelectedIndex(0);
		sorgente.setText(text);
		sorgente.setCaretPosition(0);
	}

	protected void setRisultati(String text) {
		tabbedPane.setSelectedIndex(1);
		risultati.setText(text);
		risultati.setCaretPosition(0);
	}

	protected class radioDocAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			checkInt.setEnabled(true);
			checkInc.setEnabled(true);	
		}
	}
	
	protected class radioSelAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			checkInt.setEnabled(false);
			checkInc.setEnabled(false);
		}
	}
	
	protected class AnalizzaDocAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			form.setDialogWaiting(true);
			if(radioSel.isSelected())
				AnalizzaSel();
			else if(radioDoc.isSelected())
				AnalizzaDoc();
			form.setDialogWaiting(false);
		}
	}
	
	private void AnalizzaSel(){
		form.setDialogWaiting(true);
		try {
			if (regione.getSelectedIndex() != 0)
				parser.setRegione(elencoregioniUrn[regione.getSelectedIndex()]);
			if (ente.getSelectedIndex() != 0)
				parser.setEnte(elencoentiUrn[ente.getSelectedIndex()]);
			parser.setEnabledRifIncompleti(false);
			parser.setEnabledRifInterni(false);
			switch (tipo) {
			case TESTO:
				risText = UtilFile.inputStreamToString(parser.parse(text));
				error = parser.getError();
				setRisultati(risText);
				break;
			case NODO:
				risNode = UtilFile.inputStreamToString(parser.parse(node));
				error = parser.getError();
				setRisultati(risNode);
				break;
			case NODI:
				risNodes = new String[nodes.length];
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < nodes.length; i++) {
					risNodes[i] = UtilFile.inputStreamToString(parser.parse(nodes[i]));
					error = parser.getError();
					if (error != null)
						break;
					sb.append(risNodes[i]);
					sb.append('\n');
				}
				setRisultati(sb.toString());
				break;
			}
			// TODO I18n
			utilMsg.msgInfo(form.getAsComponent(), "Analisi della selezione completata.");
			parsedAll = false;
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			// TODO I18n
			utilMsg.msgError(form.getAsComponent(), "Errore durante l'esecuzione dell'analizzatore dei riferimenti:\n" + ex.toString());
		}

		if (error != null)
			// TODO I18n
			utilMsg.msgError(form.getAsComponent(), "Errore durante l'esecuzione dell'analizzatore dei riferimenti:\n" + error);
	}
	
	
	private void AnalizzaDoc(){
		try {
			if (regione.getSelectedIndex() != 0)
				parser.setRegione(elencoregioniUrn[regione.getSelectedIndex()]);
			if (ente.getSelectedIndex() != 0)
				parser.setEnte(elencoentiUrn[ente.getSelectedIndex()]);
			parser.setEnabledRifIncompleti(checkInc.isSelected());
			parser.setEnabledRifInterni(checkInt.isSelected());
			risAll = UtilFile.inputStreamToString(parser.parse(dm.getRootElement()));
			error = parser.getError();
			setRisultati(risAll);
			parsedAll = true;
			// TODO I18n
			utilMsg.msgInfo(form.getAsComponent(), "Analisi dell'intero documento completata.");
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			// TODO I18n
			utilMsg.msgError(form.getAsComponent(), "Errore durante l'esecuzione dell'analizzatore dei riferimenti:\n" + ex.toString());
		}

		if (error != null)
			// TODO I18n
			utilMsg.msgError(form.getAsComponent(), "Errore durante l'esecuzione dell'analizzatore dei riferimenti:\n" + error);
	}

}
