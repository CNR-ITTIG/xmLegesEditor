package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.meta.MetaAction;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DecorrenzaForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DecorrenzaFormImpl implements DecorrenzaForm, Loggable, Serviceable, Initializable, ActionListener {
	
	Logger logger;
	
	DocumentManager documentManager;
	SelectionManager selectionManager;
	NirUtilDom nirUtilDom;
	MetaAction metaAction;
	NewRinviiForm newrinvii;
	
	Form form;
	JLabel vigenzaEti;
	JLabel vigenzaData;
	JLabel tempoEti;
	JLabel condizioneEti;
	JLabel dataliberaEti;
	JList tempoData;
	JTextField condizioneData;
	DefaultListModel listModel = new DefaultListModel();
	DateForm dataliberaData;
	JRadioButton vigenza;
	JRadioButton tempo;
	JRadioButton condizione;
	JRadioButton datalibera;
	JButton ciclodivita;
	JButton riferimento;
	JRadioButton selezionato;
	
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		form = (Form) serviceManager.lookup(Form.class);
		dataliberaData = (DateForm) serviceManager.lookup(DateForm.class);	
		metaAction = (MetaAction) serviceManager.lookup(MetaAction.class);
		newrinvii = (NewRinviiForm) serviceManager.lookup(NewRinviiForm.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}
	
	public void initialize() throws Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Decorrenza.jfrm"));
		form.setName("editor.form.disposizioni.decorrenza");
		form.setHelpKey("help.contents.form.disposizioniattive.decorrenza");
		form.replaceComponent("editor.disposizioni.datalibera.data", dataliberaData.getAsComponent());
		vigenzaEti = (JLabel) form.getComponentByName("editor.disposizioni.vigenza.eti");
		vigenzaData = (JLabel) form.getComponentByName("editor.disposizioni.vigenza.data");
		tempoEti = (JLabel) form.getComponentByName("editor.disposizioni.tempo.eti");
		condizioneEti = (JLabel) form.getComponentByName("editor.disposizioni.condizione.eti");
		dataliberaEti = (JLabel) form.getComponentByName("editor.disposizioni.datalibera.eti");
		tempoData = (JList) form.getComponentByName("editor.disposizioni.tempo.data");
		tempoData.setModel(listModel);
		condizioneData = (JTextField) form.getComponentByName("editor.disposizioni.condizione.data");
		vigenza = (JRadioButton) form.getComponentByName("editor.disposizioni.vigenza");
		tempo = (JRadioButton) form.getComponentByName("editor.disposizioni.tempo");
		condizione = (JRadioButton) form.getComponentByName("editor.disposizioni.condizione");
		datalibera = (JRadioButton) form.getComponentByName("editor.disposizioni.datalibera");

		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(vigenza);
		grupporadio.add(tempo);
		grupporadio.add(condizione);
		grupporadio.add(datalibera);		
		ciclodivita = (JButton) form.getComponentByName("editor.disposizioni.ciclodivita");
		ciclodivita.addActionListener(this);
		riferimento = (JButton) form.getComponentByName("editor.disposizioni.riferimento");
		riferimento.addActionListener(this);
	}

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == ciclodivita) {
			metaAction.doCiclodiVita();
			setTempiMod(selectionManager.getActiveNode());
		}
		if (e.getSource() == riferimento)
			if (newrinvii.openForm("", true, true, true, true))
					condizioneData.setText(((Urn) newrinvii.getUrn().get(0)).toString());
	}

	public String getDecorrenza() {
		if (vigenza.isSelected())
			return vigenzaData.getText();
		if (tempo.isSelected())
			return (String) tempoData.getSelectedValue();
		if (condizione.isSelected())
			return condizioneData.getText();
		if (datalibera.isSelected())
			return dataliberaData.getAsString();
		return "";
	}

	public boolean isDecorrenzaCondizionata() {
		return condizione.isSelected();
	}
	
	public boolean openForm() {
		
		if (vigenza.isSelected())
			selezionato = vigenza;
		if (tempo.isSelected())
			selezionato = tempo;
		if (condizione.isSelected())
			selezionato = condizione;
		if (datalibera.isSelected())
			selezionato = datalibera;
		form.setSize(260, 310);
		form.showDialog();
		if (!form.isOk())
			selezionato.setSelected(true);
		return form.isOk();
	}
	
	public void initForm(Node nodoCorrente) {
		vigenza.setSelected(true);
		vigenzaData.setText(getEntrataInVigore(nodoCorrente));
		setTempiMod(nodoCorrente);
		dataliberaData.set(null);
	}
	
	private String getEntrataInVigore(Node nodoCorrente) {
		try {
			Document doc = documentManager.getDocumentAsDom();
			Node activeMeta = nirUtilDom.findActiveMeta(doc,nodoCorrente);
			return UtilDate.normToString(UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(activeMeta, "entratainvigore"),"norm"));
		} catch (Exception e) {}
		return null;
	}

	private void setTempiMod(Node nodoCorrente) {
		try {
			listModel.clear();
			Document doc = documentManager.getDocumentAsDom();
			Node activeMeta = nirUtilDom.findActiveMeta(doc,nodoCorrente);
			Node eventi = UtilDom.findRecursiveChild(activeMeta,"eventi");
			Node relazioni = UtilDom.findRecursiveChild(activeMeta,"relazioni");
			if (relazioni!=null) {
				NodeList eventiList = eventi.getChildNodes();
				NodeList relazioniList = relazioni.getChildNodes();
				for (int i = 0; i < relazioniList.getLength(); i++) {
					Node relazioneNode = relazioniList.item(i);
					if ("attiva".equals(relazioneNode.getNodeName())) {
						String id = UtilDom.getAttributeValueAsString(relazioneNode, "id");
						for (int j = 0; j < eventiList.getLength(); j++)
							if (id.equals(UtilDom.getAttributeValueAsString(eventiList.item(j), "fonte")))
								listModel.addElement(UtilDate.normToString(UtilDom.getAttributeValueAsString(eventiList.item(j), "data")));
					}
				}
			}
		} catch (Exception e) {}
	}



}
