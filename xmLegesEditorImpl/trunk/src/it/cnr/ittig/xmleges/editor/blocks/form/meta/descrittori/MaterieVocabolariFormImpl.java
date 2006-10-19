package it.cnr.ittig.xmleges.editor.blocks.form.meta.descrittori;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vocabolario;
import it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MaterieVocabolariForm;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;

public class MaterieVocabolariFormImpl implements MaterieVocabolariForm , Loggable,
Serviceable, Initializable, ActionListener, FormVerifier {
	
//  MaterieVocabolari.jfrm
	Form form;
		
	Logger logger;
	String errorMessage = "";
	UtilMsg utilmsg;
	
	JButton materieButton;
	
//	Form ListTextFile materie
//	Materie.jfrm
	Form formMaterie;	
	JList listaMaterieSelectedVocab;
	ListTextField materie_listtextfield;
	
//	Form ListTextFile del browser Teseo
//	Teseo.jfrm
	Form formMaterieTeseo;
	JList listaMaterieTeseo;
	ListTextField materie_teseo_listtextfield;
	
			
	//	Form ListTextFiled vocabolari
//	Vocabolari.jfrm
	Form formVocabolari;
	JButton vocabolariButton;
	JComboBox comboVocabolari;	
	ListTextField vocabolari_listtextfield;		
	Vocabolario[] vocabolari;
	
	
	public boolean openForm() {
		form.setSize(450, 600);
		form.showDialog();
		return form.isOk();

	}

	private String[] getMaterieVocab(String vocabolario) {
		for(int i=0;i<vocabolari.length;i++){
			if(vocabolari[i].getNome().equals(vocabolario)){
				return vocabolari[i].getMaterie();
			}
		}
		return null;
	}

	private void setMaterieVocab(String[] materie, String vocabolario) {

		
		for(int i=0;i<vocabolari.length;i++){
			if(vocabolari[i].getNome().equalsIgnoreCase(vocabolario)){
				vocabolari[i].setMaterie( (materie!=null && materie.length>0) ? materie : null);
				return;
			}
		}			
				
		
	}

	public Vocabolario[] getVocabolari(){
		
		return vocabolari;
	}
	public void setVocabolari(Vocabolario[] vocabolari) {
		if (vocabolari != null) {
			this.vocabolari = vocabolari;
			comboVocabolari.removeAllItems();
			for(int i=0;i<vocabolari.length;i++){
				comboVocabolari.addItem(vocabolari[i].getNome());
				setMaterieVocab(vocabolari[i].getMaterie(),vocabolari[i].getNome());
			}
		}
		
	}

	public void enableLogging(Logger logger) {
		this.logger = logger;
		
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		form = (Form) serviceManager.lookup(Form.class);
		formVocabolari = (Form) serviceManager.lookup(Form.class);
		formMaterie = (Form) serviceManager.lookup(Form.class);	
		formMaterieTeseo = (Form) serviceManager.lookup(Form.class);
		vocabolari_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
		materie_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
		materie_teseo_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
	}

	public void initialize() throws Exception {
		form.setMainComponent(getClass().getResourceAsStream("MaterieVocabolari.jfrm"));
		form.setName("editor.form.meta.descrittori.materie");
		
		form.setSize(350, 150);
		vocabolariButton = (JButton) form.getComponentByName("editor.meta.vocabolario.modifica_btn");
		vocabolariButton.addActionListener(this);
		materieButton = (JButton) form.getComponentByName("editor.meta.vocabolario.materie.modifica_btn");
		materieButton.addActionListener(this);
		
		comboVocabolari = (JComboBox) form.getComponentByName("editor.meta.descrittori.vocabolari.dativocabolari.nometagvocabolario");
		
		comboVocabolari.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent e) {

				if(e.getStateChange()==ItemEvent.SELECTED){
					
										
					String[] materieToShow=getMaterieVocab((String)comboVocabolari.getSelectedItem());
					if(materieToShow!=null)
						listaMaterieSelectedVocab.setListData(materieToShow);
					else
						listaMaterieSelectedVocab.setListData(new Vector());
				}
				
			}
			
		});

		listaMaterieSelectedVocab = (JList) form.getComponentByName("editor.meta.descrittori.vocabolari.riepilogo.materie");
		
		formVocabolari.setMainComponent(getClass().getResourceAsStream("Vocabolari.jfrm"));
		formVocabolari.replaceComponent("editor.meta.descrittori.materie.vocabolari.listtextfield", vocabolari_listtextfield.getAsComponent());
		formVocabolari.setSize(650, 400);
		formVocabolari.setName("editor.meta.descrittori.materie.vocabolari");
		vocabolari_listtextfield.setEditor(new VocabolariListTextFieldEditor());
		
		formMaterie.setMainComponent(getClass().getResourceAsStream("Materie.jfrm"));
		formMaterie.replaceComponent("editor.meta.descrittori.materie.materie.listtextfield", materie_listtextfield.getAsComponent());
		formMaterie.setSize(650, 400);
		formMaterie.setName("editor.meta.descrittori.materie.materie");
		
		materie_listtextfield.setEditor(new MaterieListTextFieldEditor());
		
		formMaterieTeseo.setMainComponent(getClass().getResourceAsStream("Teseo.jfrm"));
		formMaterieTeseo.replaceComponent("editor.meta.descrittori.materie.materie.listtextfield", materie_teseo_listtextfield.getAsComponent());
		formMaterieTeseo.setSize(650, 400);
		formMaterieTeseo.setName("editor.meta.descrittori.materie.materieteseo");
		
		materie_teseo_listtextfield.setEditor(new MaterieListTextFieldEditor());
	
		
		
	}

	public void actionPerformed(ActionEvent e) {
		 if (e.getSource().equals(vocabolariButton)) { // VOCABOLARI
			 Vector v = new Vector();
				if (vocabolari != null) {
					for (int i = 0; i < vocabolari.length; i++) {
						v.add(vocabolari[i].getNome());
					}
				}
				vocabolari_listtextfield.setListElements(v);

				formVocabolari.showDialog();

				if (formVocabolari.isOk()) {
					String[] nomiVocabolari=new String[vocabolari_listtextfield.getListElements().size()];
					vocabolari_listtextfield.getListElements().toArray(nomiVocabolari);
					if(vocabolari==null){						
						vocabolari=new Vocabolario[nomiVocabolari.length];
						for(int i=0;i<nomiVocabolari.length;i++){
							vocabolari[i]=new Vocabolario();
							vocabolari[i].setNome(nomiVocabolari[i]);
						}
					}
					else{
					
						for(int i=0;i<vocabolari.length;i++){
							boolean found=false;
							for(int j=0;j<nomiVocabolari.length;j++){
								if(vocabolari[i].getNome().equalsIgnoreCase(nomiVocabolari[j])){
									found=true;
									break;
								}
							}
							if (found)
								break;
							else{
								removeVocabolario(i);
							}
						}
						for(int i=0;i<nomiVocabolari.length;i++){
							boolean missing=true;
							for(int j=0;j<vocabolari.length;j++){
								if(nomiVocabolari[i].equalsIgnoreCase(vocabolari[j].getNome())){
									missing=false;
									break;
								}
							}
							if (missing)
								addVocabolario(nomiVocabolari[i]);
							
						}
					}
					
					comboVocabolari.removeAllItems();
					for(int i=0;i<vocabolari.length;i++){
						comboVocabolari.addItem(vocabolari[i].getNome());
					}
					comboVocabolari.setSelectedIndex(comboVocabolari.getItemCount()-1);
				}

		 }else if (e.getSource().equals(materieButton)) { // MATERIE
			 if(comboVocabolari.getSelectedItem()==null){
				 utilmsg.msgInfo("Scegliere prima il vocabolario");				 
				 return;
			 }
			 
			 Vector v = new Vector();
			 String[] materieVocab=getMaterieSelectedVocabolario();
				if (materieVocab != null) {
					for (int i = 0; i < materieVocab.length; i++) {
						v.add(materieVocab[i]);
					}
				}
				if("teseo".equalsIgnoreCase((String) comboVocabolari.getSelectedItem())){
					//////////////
					String urlData = "http://www.normeinrete.it/stdoc/xmlrae/data_creazione_rae.txt";
					
					URL source = null;
					try {
						source = new URL(urlData);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}			
					
						try {
							source.openConnection();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}		
				
					/////////////////
					materie_teseo_listtextfield.setListElements(v);
					formMaterieTeseo.showDialog();
	
					if (formMaterieTeseo.isOk()) {
						
						materieVocab = new String[materie_teseo_listtextfield.getListElements().size()];
						materie_teseo_listtextfield.getListElements().toArray(materieVocab);
						
						listaMaterieSelectedVocab.setListData(materieVocab);
						setMaterieVocab(materieVocab,(String) comboVocabolari.getSelectedItem());
						
					}
				}else{
					materie_listtextfield.setListElements(v);
					formMaterie.showDialog();
	
					if (formMaterie.isOk()) {
						
						materieVocab = new String[materie_listtextfield.getListElements().size()];
						materie_listtextfield.getListElements().toArray(materieVocab);
						
						listaMaterieSelectedVocab.setListData(materieVocab);
						setMaterieVocab(materieVocab,(String) comboVocabolari.getSelectedItem());
						
					}
				}
	 

		 } 
		
	}

	private void addVocabolario(String nome) {
		Vocabolario[] newVocabolario=new Vocabolario[vocabolari.length+1];
		int i=0;
		for(i=0;i<newVocabolario.length-1;i++)
				newVocabolario[i]=vocabolari[i];
		newVocabolario[i]=new Vocabolario();	
		newVocabolario[i].setNome(nome);
		
		vocabolari=newVocabolario;
		
	}

	private void removeVocabolario(int index) {
		Vocabolario[] newVocabolario=new Vocabolario[vocabolari.length-1];
		for(int i=0;i<newVocabolario.length;i++){
			if(i<index)
				newVocabolario[i]=vocabolari[i];
			else if(i>index)
				newVocabolario[i]=vocabolari[i+1];
		}
		vocabolari=newVocabolario;
	}

	private String[] getMaterieSelectedVocabolario() {
		
		for(int i=0;i<vocabolari.length;i++){
			if(vocabolari[i].getNome().equalsIgnoreCase((String) comboVocabolari.getSelectedItem())){
				return vocabolari[i].getMaterie();
			}
		}
		return null;
		
	}
	
	public boolean verifyForm() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Editor per il ListTextField della lista dei vocabolari
	 */
	private class VocabolariListTextFieldEditor implements ListTextFieldEditor {
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
			return "editor.form.meta.descrittori.msg.err.datialias";
		}

		public Dimension getPreferredSize() {
			return new Dimension(600, 150);
		}
	}
	/**
	 * Editor per il ListTextField della lista delle materie
	 */
	private class MaterieListTextFieldEditor implements ListTextFieldEditor {
		javax.swing.JTextField textField = new javax.swing.JTextField();
		

		public MaterieListTextFieldEditor() {
			
		}
		
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
			return "editor.form.meta.descrittori.msg.err.datialias";
		}

		public Dimension getPreferredSize() {
			return new Dimension(600, 150);
		}
	}
	


	

}
