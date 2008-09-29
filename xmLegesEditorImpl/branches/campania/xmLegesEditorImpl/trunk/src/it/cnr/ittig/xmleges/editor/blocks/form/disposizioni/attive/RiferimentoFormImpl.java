package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.RiferimentoForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class RiferimentoFormImpl implements RiferimentoForm, Loggable, ActionListener, Serviceable, Initializable {
	
	Logger logger;
	EventManager eventManager;
	DocumentManager documentManager;
	NirUtilDom nirUtilDom;

	Form form;
	NewRinviiForm rinviiForm;
	
	String[] bordi=null;
	
	JLabel riferimentoEti;
	JLabel riferimentoliberoEti;
	JList riferimentoData;
	DefaultListModel listModel = new DefaultListModel();
	JTextField riferimentoliberoData;
	JRadioButton riferimento;
	JRadioButton riferimentolibero;
	JButton riferimentoliberoScelta;

	JRadioButton selezionato;
	
	String unicaPartizione = "";
	
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);		
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		form = (Form) serviceManager.lookup(Form.class);
		rinviiForm = (NewRinviiForm) serviceManager.lookup(NewRinviiForm.class);
	}
	
	public void initialize() throws Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Riferimento.jfrm"));
		form.setName("editor.form.disposizioni.riferimento");
		form.setHelpKey("help.contents.form.disposizioniattive.riferimento");
		riferimentoEti = (JLabel) form.getComponentByName("editor.disposizioni.rif.eti");
		riferimentoliberoEti = (JLabel) form.getComponentByName("editor.disposizioni.riferimentolibero.eti");
		riferimentoData = (JList) form.getComponentByName("editor.disposizioni.rif.data");
		riferimentoData.setModel(listModel);
		riferimentoliberoData = (JTextField) form.getComponentByName("editor.disposizioni.riferimentolibero.data");
		riferimentoliberoScelta = (JButton) form.getComponentByName("editor.disposizioni.riferimentolibero.scelta");
		riferimentoliberoScelta.addActionListener(this);
		riferimento = (JRadioButton) form.getComponentByName("editor.disposizioni.rif");
		riferimentolibero = (JRadioButton) form.getComponentByName("editor.disposizioni.riferimentolibero");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(riferimento);
		grupporadio.add(riferimentolibero);
	}


	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == riferimentoliberoScelta) {
			if (rinviiForm.openForm(riferimentoliberoData.getText(), false, false, false, true)) {
				Vector v = rinviiForm.getUrn();
				if (v.size() > 0) 
					riferimentoliberoData.setText(((Urn) v.get(0)).toString());
			}
		}
	}
	public String getRiferimento() {
		if (riferimento.isSelected())
			return (String) riferimentoData.getSelectedValue();
		if (riferimentolibero.isSelected())
			return riferimentoliberoData.getText();
		return "";
	}

	public boolean openForm() {
		
		if (riferimento.isSelected())
			selezionato = riferimento;
		if (riferimentolibero.isSelected())
			selezionato = riferimentolibero;
		form.setSize(400, 250);
		form.showDialog();
		if (!form.isOk())
			selezionato.setSelected(true);
		return form.isOk();
	}
	
	public void initForm(Node nodoCorrente) {
		unicaPartizione="";
		setRiferimentiMod(nodoCorrente);
		if (listModel.getSize()>0) {
			riferimentoData.setSelectedIndex(0);
			riferimento.setSelected(true);
		}	
		else
			riferimentolibero.setSelected(true);
	}
	

	public String getPartizionePrimoAtto() {
		return unicaPartizione;
	}
	
	public String[] getBordi() {
		return bordi;
	}
	
	private void setRiferimentiMod(Node nodoCorrente) {
		Document doc = documentManager.getDocumentAsDom();
		Node mod = UtilDom.findParentByName(nodoCorrente, "mod");
		Node[] riferimenti = UtilDom.getElementsByTagName(doc, mod, "rif");
		listModel.clear();
		bordi=new String[0];
		int trovati=0;
		for (int i=0; i<riferimenti.length; i++)
			try {
				//se è un RIF in una VIRGOLETTA la scarto
				if (UtilDom.findParentByName(riferimenti[i], "virgolette")!=null)
					continue;
				trovati++;
				String valore = UtilDom.getAttributeValueAsString(riferimenti[i],"xlink:href");
				if (valore.indexOf("#")!=-1) {
					if (trovati==1) {
						unicaPartizione = valore.substring(valore.indexOf("#")+1,valore.length());
						bordi = getBordiDaNota(riferimenti[i]);
					}	
					//else	NO, voglio settare la partizione del primo atto 
					//	unicaPartizione = "";
					valore = valore.substring(0, valore.indexOf("#"));
				}	
				listModel.addElement(valore);
			} catch (Exception e) {}
	}
	
	public String[] getBordiDaNota(Node rif) {
		String parolaChiave="bordo:";
		String[] vuota = new String[0];
		Node bordoInCommento = rif.getNextSibling();
		if (bordoInCommento==null)
			return vuota;
		if (bordoInCommento.getNodeType()!=Node.COMMENT_NODE)
			return vuota;
		String testoNota = bordoInCommento.getNodeValue();
		if (!testoNota.startsWith(parolaChiave))
			return vuota;
		testoNota=testoNota.substring(parolaChiave.length());
		//SINTASSI ATTESA: <!--bordo:partizione,numero,ordinale#part2,num2,ord2-->
		String[] bordiTrovati;
		try {
			StringTokenizer partizioni = new StringTokenizer(testoNota, "#");
			int token = partizioni.countTokens();
			bordiTrovati = new String[3*token];
			for (int i=0; i<token; i++) {
				StringTokenizer elementi = new StringTokenizer(partizioni.nextToken(), ",");
				bordiTrovati[i*3] = elementi.nextToken();
				bordiTrovati[i*3+1] = elementi.nextToken();
				try {
					bordiTrovati[i*3+2] = elementi.nextToken();
				}
				catch (Exception token3) {
					bordiTrovati[i*3+2] = "";
				}
			}
		} catch (Exception e) {
			return vuota;
		}
		return bordiTrovati;
	}
}
