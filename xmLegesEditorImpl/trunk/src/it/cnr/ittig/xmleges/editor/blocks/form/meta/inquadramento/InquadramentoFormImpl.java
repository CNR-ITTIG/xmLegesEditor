package it.cnr.ittig.xmleges.editor.blocks.form.meta.inquadramento;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.InfoMancanti;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.Infodoc;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.Oggetto;
import it.cnr.ittig.xmleges.editor.services.form.meta.inquadramento.InquadramentoForm;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

public class InquadramentoFormImpl implements InquadramentoForm, Loggable,
		Serviceable, Initializable, ActionListener, FormVerifier {
	
	Logger logger;

	//   Inquadramento.jfrm
	Form form;

	Form formDatiInfodoc;
	
	Form formDatiInfomancanti;
	
	Form formDatiOggetto;
	
	Form formDatiProponenti;

	String tipoDTD;

	String tipoDocumento;
	
	JButton infodocButton;

	JButton infomancButton;
	
	JButton oggettoButton;
	
	JButton proponentiButton;
	
	JList proponentiList;

	String[] proponenti;//da fare sposta con gli altri su apiimpl
	
	ListTextField prop_listtextfield;

	JTextField tagFormInfodocNatura;
	
	JLabel tagFormNatura;
	
	JLabel tagFormNormativa;
	
	JLabel tagFormFunzione;
	
	JLabel tagFormFonte;
	
	JComboBox tagFormInfoDocNormativa;
	
	JComboBox tagFormInfoDocFunzione;
	
	JComboBox tagFormInfoDocFonte;
	
	JTextField tagFormInfomancantiTitolodoc;
	
	JTextField tagFormInfomancantiTipodoc;
	
	DateForm tagFormInfomancantiDatadoc;
	
	JTextField tagFormInfomancantiNumdoc;
	
	JTextField tagFormInfomancantiEmanante;
	
	JLabel tagFormTitolo;
	
	JLabel tagFormTipo;
	
	JLabel tagFormData;
	
	JLabel tagFormNumdoc;
	
	JLabel tagFormEmanante;
	
	JTextField tagFormOggettoFinalita;
	
	JTextField tagFormOggettoDestinatario;
	
	JTextField tagFormOggettoTerritorio;
	
	JTextField tagFormOggettoAttivita;
	
	JLabel tagFormFinalita;
	
	JLabel tagFormDestinatario;
	
	JLabel tagFormTerritorio;
	
	JLabel tagFormAttivita;
	
	String errorMessage = "";
	
	
	/**
	 * Editor per il ListTextField della lista dei proponenti
	 */
	
	private class ProponentiListTextFieldEditor implements ListTextFieldEditor {
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
			return "error";
		}

		public Dimension getPreferredSize() {
			return new Dimension(600, 150);
		}
	}
	
		
	public boolean openForm() {
		form.setSize(400, 600);
		form.showDialog();

		return form.isOk();

	}

	public void setTipoDTD(String tipoDTD) {
		this.tipoDTD = tipoDTD;
	}

	public void setInfodoc(Infodoc infodoc) {
		if(infodoc!=null){
			if(infodoc.getNatura()!=null)
				this.tagFormNatura.setText(infodoc.getNatura());
			else
				this.tagFormNatura.setText(" ");
			this.tagFormInfodocNatura.setText(infodoc.getNatura());
			if(infodoc.getNormativa()!=null)
				this.tagFormNormativa.setText(infodoc.getNormativa());
			else
				this.tagFormNormativa.setText(" ");
			this.tagFormInfoDocNormativa.setSelectedItem(infodoc.getNormativa());			
			if(infodoc.getFunzione()!=null)
				this.tagFormFunzione.setText(infodoc.getFunzione());
			else
				this.tagFormFunzione.setText(" ");
			this.tagFormInfoDocFunzione.setSelectedItem(infodoc.getFunzione());
			if(infodoc.getFonte()!=null)
				this.tagFormFonte.setText(infodoc.getFonte());
			else
				this.tagFormFonte.setText(" ");
			this.tagFormInfoDocFonte.setSelectedItem(infodoc.getFonte());
		}else{
			this.tagFormNatura.setText(" ");
			this.tagFormNormativa.setText(" ");
			this.tagFormFunzione.setText(" ");
			this.tagFormFonte.setText(" ");
			this.tagFormInfodocNatura.setText(null);
			this.tagFormInfoDocNormativa.setSelectedItem(null);
			this.tagFormInfoDocFunzione.setSelectedItem(null);
			this.tagFormInfoDocFonte.setSelectedItem(null);
			
			
		}
		
	}

	public void setInfomancanti(InfoMancanti infomancanti) {
		if(infomancanti!=null){
			if(infomancanti.getMDatadoc()!=null){
				this.tagFormData.setText(infomancanti.getMDatadoc());
//				this.tagFormInfomancantiDatadoc.set(UtilDate.normToDate(infomancanti.getMDatadoc()));
			}
			else{
				this.tagFormData.setText(" ");
//				this.tagFormInfomancantiDatadoc.set(UtilDate.normToDate(""));
			}
			
			if(infomancanti.getMEmanante()!=null)
				this.tagFormEmanante.setText(infomancanti.getMEmanante());
			else
				this.tagFormEmanante.setText(" ");
			this.tagFormInfomancantiEmanante.setText(infomancanti.getMEmanante());
			if(infomancanti.getMNumdoc()!=null)
				this.tagFormNumdoc.setText(infomancanti.getMNumdoc());
			else
				this.tagFormNumdoc.setText(" ");
			this.tagFormInfomancantiNumdoc.setText(infomancanti.getMNumdoc());
			if(infomancanti.getMTitolodoc()!=null)
				this.tagFormTitolo.setText(infomancanti.getMTitolodoc());
			else
				this.tagFormTitolo.setText(" ");
			this.tagFormInfomancantiTitolodoc.setText(infomancanti.getMTitolodoc());
			if(infomancanti.getMTipodoc()!=null)
				this.tagFormTipo.setText(infomancanti.getMTipodoc());
			else
				this.tagFormTipo.setText(" ");
			this.tagFormInfomancantiTipodoc.setText(infomancanti.getMTipodoc());
			
		}else{
			this.tagFormData.setText(" ");
			this.tagFormEmanante.setText(" ");
			this.tagFormNumdoc.setText(" ");
			this.tagFormTitolo.setText(" ");
			this.tagFormTipo.setText(" ");
			
			this.tagFormInfomancantiDatadoc.set(null);
			this.tagFormInfomancantiEmanante.setText(null);
			this.tagFormInfomancantiNumdoc.setText(null);
			this.tagFormInfomancantiTitolodoc.setText(null);
			this.tagFormInfomancantiTipodoc.setText(null);
			
			
		}
			
		
	}

	public void setOggetto(Oggetto oggetto) {
		if(oggetto!=null){
			if(oggetto.getAttivita()!=null)
				this.tagFormAttivita.setText(oggetto.getAttivita());
			else
				this.tagFormAttivita.setText("");
			this.tagFormOggettoAttivita.setText(oggetto.getAttivita());
			if(oggetto.getDestinatario()!=null)
				this.tagFormDestinatario.setText(oggetto.getDestinatario());
			else
				this.tagFormDestinatario.setText("");
			this.tagFormOggettoDestinatario.setText(oggetto.getDestinatario());
			if(oggetto.getFinalita()!=null)
				this.tagFormFinalita.setText(oggetto.getFinalita());
			else
				this.tagFormFinalita.setText("");
			this.tagFormOggettoFinalita.setText(oggetto.getFinalita());
			if(oggetto.getTerritorio()!=null)
				this.tagFormTerritorio.setText(oggetto.getTerritorio());
			else
				this.tagFormTerritorio.setText("");
			this.tagFormOggettoTerritorio.setText(oggetto.getTerritorio());
		}else{
			this.tagFormAttivita.setText("");
			this.tagFormDestinatario.setText("");
			this.tagFormFinalita.setText("");
			this.tagFormTerritorio.setText("");
			this.tagFormOggettoAttivita.setText(null);
			this.tagFormOggettoDestinatario.setText(null);
			this.tagFormOggettoFinalita.setText(null);
			this.tagFormOggettoTerritorio.setText(null);
			
		}

	}

	public void setProponenti(String[] proponenti) {
		if (proponenti != null) {
			this.proponenti = proponenti;
			proponentiList.setListData(proponenti);
		}

	}

	public Infodoc getInfodoc() {
		return (new Infodoc(tagFormNatura.getText(),tagFormNormativa.getText(),tagFormFunzione.getText(),tagFormFonte.getText()));

	}

	public InfoMancanti getInfomancanti() {
		return (new InfoMancanti(tagFormTitolo.getText(),tagFormTipo.getText(),tagFormData.getText(),tagFormNumdoc.getText(),tagFormEmanante.getText()));

	}

	public Oggetto getOggetto() {
		return (new Oggetto(tagFormFinalita.getText(),tagFormDestinatario.getText(),tagFormTerritorio.getText(),tagFormAttivita.getText()));

	}

	public String[] getProponenti() {
		return proponenti;

	}

	public void enableLogging(Logger logger) {
		this.logger = logger;

	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);		
		formDatiInfodoc = (Form) serviceManager.lookup(Form.class);
		formDatiInfomancanti = (Form) serviceManager.lookup(Form.class);
		formDatiOggetto = (Form) serviceManager.lookup(Form.class);
		formDatiProponenti = (Form) serviceManager.lookup(Form.class);
		
		tagFormInfomancantiDatadoc = (DateForm) serviceManager.lookup(DateForm.class);
				
		prop_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
	}

	public void initialize() throws Exception {
		
		form.setMainComponent(getClass().getResourceAsStream("Inquadramento.jfrm"));
		form.setName("editor.form.meta.inquadramento.riepilogo");
		
		form.setSize(350, 300);
		
		formDatiInfodoc.setMainComponent(getClass().getResourceAsStream("DatiInfodoc.jfrm"));
		formDatiInfodoc.setName("editor.form.meta.inquadramento.infodoc");
		formDatiInfodoc.addFormVerifier(this);
		
		tagFormInfodocNatura = (JTextField) formDatiInfodoc.getComponentByName("editor.form.meta.inquadramento.infodoc.natura");
		tagFormInfodocNatura.setText("");		
		tagFormNatura = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infodoc.natura_label");
		
		tagFormInfoDocNormativa = (JComboBox) formDatiInfodoc.getComponentByName("editor.form.meta.inquadramento.infodoc.normativa");
		tagFormInfoDocNormativa.addItem("si");
		tagFormInfoDocNormativa.addItem("no");
		tagFormInfoDocNormativa.setSelectedItem(null);
		tagFormNormativa = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infodoc.normativa_label");
		
		tagFormInfoDocFunzione = (JComboBox) formDatiInfodoc.getComponentByName("editor.form.meta.inquadramento.infodoc.funzione");
		tagFormInfoDocFunzione.addItem("informativa");
		tagFormInfoDocFunzione.addItem("regolativa");
		tagFormInfoDocFunzione.setSelectedItem(null);
		tagFormFunzione = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infodoc.funzione_label");
		
		tagFormInfoDocFonte = (JComboBox) formDatiInfodoc.getComponentByName("editor.form.meta.inquadramento.infodoc.fonte");
		tagFormInfoDocFonte.addItem("costituzionale");
		tagFormInfoDocFonte.addItem("primario");
		tagFormInfoDocFonte.addItem("secondario");
		tagFormInfoDocFonte.addItem("terziario");
		tagFormInfoDocFonte.addItem("prassi");
		tagFormInfoDocFonte.setSelectedItem(null);
		tagFormFonte = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infodoc.fonte_label");
			
		infodocButton = (JButton) form.getComponentByName("editor.form.meta.inquadramento.riepilogo.infodoc_btn");
		infodocButton.addActionListener(this);
		
		formDatiInfomancanti.setMainComponent(getClass().getResourceAsStream("DatiInfoMancanti.jfrm"));
		formDatiInfomancanti.setName("editor.form.meta.inquadramento.infomancanti");
		
		tagFormInfomancantiTitolodoc=(JTextField) formDatiInfomancanti.getComponentByName("editor.form.meta.inquadramento.infomancanti.titolodoc");
		tagFormTitolo = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infomancanti.titolo_label");
		
		tagFormInfomancantiTipodoc=(JTextField) formDatiInfomancanti.getComponentByName("editor.form.meta.inquadramento.infomancanti.tipodoc");
		tagFormTipo = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infomancanti.tipo_label");
		
		formDatiInfomancanti.replaceComponent("editor.form.meta.inquadramento.infomancanti.datadoc", tagFormInfomancantiDatadoc.getAsComponent());
		tagFormData = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infomancanti.data_label");
		
		tagFormInfomancantiNumdoc=(JTextField) formDatiInfomancanti.getComponentByName("editor.form.meta.inquadramento.infomancanti.numdoc");
		tagFormNumdoc = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infomancanti.numero_label");
		
		tagFormInfomancantiEmanante=(JTextField) formDatiInfomancanti.getComponentByName("editor.form.meta.inquadramento.infomancanti.emanante");
		tagFormEmanante = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infomancanti.emanante_label");
		 
		infomancButton = (JButton) form.getComponentByName("editor.form.meta.inquadramento.riepilogo.infomancanti_btn");
		infomancButton.addActionListener(this);
		
		formDatiOggetto.setMainComponent(getClass().getResourceAsStream("DatiOggetto.jfrm"));
		formDatiOggetto.setName("editor.form.meta.inquadramento.oggetto");
		
		tagFormOggettoFinalita = (JTextField) formDatiOggetto.getComponentByName("editor.form.meta.inquadramento.oggetto.finalita");
		tagFormFinalita = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.oggetto.finalita_label");
		
		tagFormOggettoDestinatario = (JTextField) formDatiOggetto.getComponentByName("editor.form.meta.inquadramento.oggetto.destinatario");
		tagFormDestinatario = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.oggetto.destinatario_label");
		
		tagFormOggettoTerritorio = (JTextField) formDatiOggetto.getComponentByName("editor.form.meta.inquadramento.oggetto.territorio");
		tagFormTerritorio = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.oggetto.territorio_label");
		
		tagFormOggettoAttivita = (JTextField) formDatiOggetto.getComponentByName("editor.form.meta.inquadramento.oggetto.attivita");
		tagFormAttivita = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.oggetto.attivita_label");

		oggettoButton = (JButton) form.getComponentByName("editor.form.meta.inquadramento.riepilogo.oggetto_btn");
		oggettoButton.addActionListener(this);
				
		formDatiProponenti.setMainComponent(getClass().getResourceAsStream("Proponente.jfrm"));
		formDatiProponenti.replaceComponent("editor.form.meta.inquadramento.proponenti.listtextfield", prop_listtextfield.getAsComponent());
		formDatiProponenti.setSize(650, 400);
		formDatiProponenti.setName("editor.form.meta.inquadramento.proponenti");
		prop_listtextfield.setEditor(new ProponentiListTextFieldEditor());
		proponentiList = (JList) form.getComponentByName("editor.form.meta.inquadramento.proponenti");
		
		proponentiButton = (JButton) form.getComponentByName("editor.form.meta.inquadramento.riepilogo.proponenti_btn");
		proponentiButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		 if (e.getSource().equals(infodocButton)) { // INFODOC
			 formDatiInfodoc.showDialog();	
			 
			 if (formDatiInfodoc.isOk()) {
							 
				 tagFormNatura.setText(tagFormInfodocNatura.getText());
				 String prova=(String)tagFormInfoDocNormativa.getSelectedItem();
				 tagFormNormativa.setText(prova);				
				 tagFormFunzione.setText((String)tagFormInfoDocFunzione.getSelectedItem());				
				 tagFormFonte.setText((String)tagFormInfoDocFonte.getSelectedItem());
				
			 }
			 tagFormNormativa.revalidate();

		 }else if (e.getSource().equals(infomancButton)) { // INFOMANC
			 formDatiInfomancanti.showDialog();	
			 if (formDatiInfomancanti.isOk()) {
				 
				 tagFormTitolo.setText(tagFormInfomancantiTitolodoc.getText());					
				 tagFormTipo.setText(tagFormInfomancantiTipodoc.getText());
				 tagFormData.setText((String)tagFormInfomancantiDatadoc.getAsString());				
				 tagFormNumdoc.setText((String)tagFormInfomancantiNumdoc.getText());				
				 tagFormEmanante.setText((String)tagFormInfomancantiEmanante.getText());
				 
					
			 }

		 }else if (e.getSource().equals(oggettoButton)) { // OGGETTO
			 formDatiOggetto.showDialog();	
			 if (formDatiOggetto.isOk()) {
				 
				 tagFormFinalita.setText(tagFormOggettoFinalita.getText());					
				 tagFormDestinatario.setText(tagFormOggettoDestinatario.getText());
				 tagFormTerritorio.setText((String)tagFormOggettoTerritorio.getText());				
				 tagFormAttivita.setText((String)tagFormOggettoAttivita.getText());				
				 				 
					
			 }

		 }else if (e.getSource().equals(proponentiButton)) { // PROPONENTI
			 Vector v = new Vector();
				if (proponenti != null) {
					for (int i = 0; i < proponenti.length; i++) {
						v.add(proponenti[i]);
					}
				}
				prop_listtextfield.setListElements(v);

				formDatiProponenti.showDialog();

				if (formDatiProponenti.isOk()) {
					proponenti = new String[prop_listtextfield.getListElements().size()];
					prop_listtextfield.getListElements().toArray(proponenti);
					proponentiList.setListData(proponenti);
				}

		 }
	}

	public boolean verifyForm() {
		boolean isvalid=true;
		isvalid=tagFormInfodocNatura.getText()!=null && !tagFormInfodocNatura.getText().trim().equals("");
		if(!isvalid){
			errorMessage="editor.form.meta.inquadramento.infodoc.msg.err.naturavuota";
			return false;
		}
		isvalid=tagFormInfoDocNormativa.getSelectedItem()!=null;
		if(!isvalid){
			errorMessage="editor.form.meta.inquadramento.infodoc.msg.err.normativavuota";
			return false;
		}
		isvalid=tagFormInfoDocFunzione.getSelectedItem()!=null;
		if(!isvalid){
			errorMessage="editor.form.meta.inquadramento.infodoc.msg.err.funzionevuota";
			return false;
		}	
		isvalid=tagFormInfoDocFonte.getSelectedItem()!=null;
		if(!isvalid){
			errorMessage="editor.form.meta.inquadramento.infodoc.msg.err.fontevuota";
			return false;
		}
		
		return isvalid;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	
			


}
