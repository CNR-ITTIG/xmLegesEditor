package it.cnr.ittig.xmleges.editor.blocks.form.meta.inquadramento;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
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
		Serviceable, Initializable, ActionListener {
	
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

	String[] proponenti;
	
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
		form.setSize(600, 650);
		form.showDialog();

		return form.isOk();

	}

	public void setTipoDTD(String tipoDTD) {
		// TODO Auto-generated method stub

	}

	public void setInfodoc() {
		// TODO Auto-generated method stub

	}

	public void setInfomancanti() {
		// TODO Auto-generated method stub

	}

	public void setOggetto() {
		// TODO Auto-generated method stub

	}

	public void setProponenti() {
		// TODO Auto-generated method stub

	}

	public void getInfodoc() {
		// TODO Auto-generated method stub

	}

	public void getInfomancanti() {
		// TODO Auto-generated method stub

	}

	public void getOggetto() {
		// TODO Auto-generated method stub

	}

	public void getProponenti() {
		// TODO Auto-generated method stub

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
		
		tagFormInfodocNatura = (JTextField) formDatiInfodoc.getComponentByName("editor.form.meta.inquadramento.infodoc.natura");
		tagFormInfodocNatura.setText("");		
		tagFormNatura = (JLabel) form.getComponentByName("editor.form.meta.inquadramento.infodoc.natura_label");
		
		tagFormInfoDocNormativa = (JComboBox) formDatiInfodoc.getComponentByName("editor.form.meta.inquadramento.infodoc.normativa");
		tagFormInfoDocNormativa.addItem("Si");
		tagFormInfoDocNormativa.addItem("No");
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
				 tagFormNormativa.setText((String)tagFormInfoDocNormativa.getSelectedItem());				
				 tagFormFunzione.setText((String)tagFormInfoDocFunzione.getSelectedItem());				
				 tagFormFonte.setText((String)tagFormInfoDocFonte.getSelectedItem());
					
			 }

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
			


}
