package it.cnr.ittig.xmleges.editor.blocks.form.meta.descrittori;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.blocks.form.browser.BrowserEvent;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vocabolario;
import it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MaterieVocabolariForm;

//disabilito il teseo
//import it.cnr.ittig.xmleges.editor.services.form.browser.BrowserForm;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MaterieVocabolariFormImpl implements MaterieVocabolariForm , Loggable,
Serviceable, Initializable, ActionListener {
	
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
	
	ListTextField materie_teseo_listtextfield;
	
	String nomeVocabolario = null;
			
	//	Form ListTextFiled vocabolari
//	Vocabolari.jfrm
	Form formVocabolari;
	JButton vocabolariButton;
	JComboBox comboVocabolari;	
	ListTextField vocabolari_listtextfield;		
	Vocabolario[] vocabolari;
	Vocabolario[] vocaboliSelezionati;
	
	// Mie variabili
	Form sottoFormTeseo;
	ListTextField teseo_listtextfield;
	
	//disabilito il teseo
	//BrowserForm browserForm;
	EventManager eventManager;
	
	
	public boolean openForm() {
		
		form.setSize(650, 400);
		form.showDialog();
		updateVocaboli();
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

	private void addMateriaVocab(String materia, String vocabolario) {

		for(int i=0;i<vocabolari.length;i++){
			if(vocabolari[i].getNome().equalsIgnoreCase(vocabolario)){
				vocabolari[i].addMateria(materia);
				return;
			}
		}				
	}
	
	private void updateVocaboli(){
		
		if (vocaboliSelezionati==null)
			vocaboliSelezionati = new Vocabolario[0];
			
		for (int i=0; i<vocaboliSelezionati.length; i++) 
			if (nomeVocabolario.equals(vocaboliSelezionati[i].getNome())) {
				vocaboliSelezionati[i].setMaterie(new String[0]);
				Object[] materieSelezionate =listaMaterieSelectedVocab.getSelectedValues();
				for (int j=0; j<materieSelezionate.length; j++)
					vocaboliSelezionati[i].addMateria((String) materieSelezionate[j]);
				return;
			}
		//il vocabolario non era ancora fra i selezionati
		Vocabolario[] nuovoVocabolario = new Vocabolario[1+vocaboliSelezionati.length];
		for (int i=0; i<vocaboliSelezionati.length; i++)
			nuovoVocabolario[i]=vocaboliSelezionati[i];
		nuovoVocabolario[vocaboliSelezionati.length] = new Vocabolario();
		nuovoVocabolario[vocaboliSelezionati.length].setNome(nomeVocabolario); 
		Object[] materieSelezionate =listaMaterieSelectedVocab.getSelectedValues();
		for (int i=0; i<materieSelezionate.length; i++)
			nuovoVocabolario[vocaboliSelezionati.length].addMateria((String) materieSelezionate[i]);
		
		vocaboliSelezionati = nuovoVocabolario;
	}
	
	public Vocabolario[] getVocabolari(){
		return vocaboliSelezionati;
	}
	
	public void setVocabolari(Vocabolario[] vocabolari) {
		
		nomeVocabolario = null;
		vocaboliSelezionati = vocabolari;
		
		//aggiungo le materie presenti nel documento (nei rispettivi vocabolari)
		//che potrebbere non essere presenti nei vocabolari su file.
		if (vocabolari!=null)
			for (int i=0; i<vocabolari.length; i++) {
				boolean trovato = false;
				for (int j=0; j<this.vocabolari.length; j++)
					if (this.vocabolari[j].getNome().equals(vocabolari[i].getNome())) {
						trovato = true;
						//aggiungo tutte le materie eventualmente non presenti nel vocabolario
						String[] materie = vocabolari[i].getMaterie();
						if (materie!=null)
							for (int k=0; k<materie.length; k++)
								addMateriaVocab(materie[k], vocabolari[i].getNome());
					}	
				if (!trovato) {
					//aggiungo il vocabolario e tutte le sue materie
					Vocabolario[] temp = new Vocabolario[this.vocabolari.length+1];
					for (int j=0; j<this.vocabolari.length; j++)
						temp[j] = this.vocabolari[j];
					temp[this.vocabolari.length]=vocabolari[i];
					this.vocabolari = temp;
				}
				
			}
			
		comboVocabolari.removeAllItems();
		listaMaterieSelectedVocab.setListData(new Vector());
		
		if (this.vocabolari != null) {			
			for(int i=0;i<this.vocabolari.length;i++){
				comboVocabolari.addItem(this.vocabolari[i].getNome());
				setMaterieVocab(this.vocabolari[i].getMaterie(),this.vocabolari[i].getNome());
			}
			//Seleziono il vocabolario e le materie presenti nel documento
			if (vocabolari != null) 
				setMaterieSelez(vocabolari[0]);

		}	
	}

	private void setMaterieSelez(Vocabolario vocabolari) {
		
		comboVocabolari.setSelectedItem(vocabolari.getNome());
		String[] materie = this.vocabolari[comboVocabolari.getSelectedIndex()].getMaterie();
		String[] materieDaSelezionare = vocabolari.getMaterie();
		if (materieDaSelezionare!=null) {
		int[] selezionati = new int[materieDaSelezionare.length];
			for (int k=0; k<materieDaSelezionare.length; k++)
				for (int i=0; i<materie.length; i++)
					if (materie[i].equals(materieDaSelezionare[k])) {
						selezionati[k]=i;
						break;
				}
			listaMaterieSelectedVocab.setSelectedIndices(selezionati);
		}
		
		nomeVocabolario = (String) comboVocabolari.getSelectedItem();
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
		sottoFormTeseo = (Form) serviceManager.lookup(Form.class);
		//disabilito il teseo
//		try {
//			if (isWin())
//				browserForm = (BrowserForm) serviceManager.lookup(BrowserForm.class);
//		}
//		catch (Exception e){
//			//Probabilmente non caricato
//		}
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	public void initialize() throws Exception {
		form.setMainComponent(getClass().getResourceAsStream("MaterieVocabolari.jfrm"));
		form.setName("editor.form.meta.descrittori.materie");
		
		form.setHelpKey("help.contents.form.materievocabolari");
		
		form.setSize(350, 150);
		vocabolariButton = (JButton) form.getComponentByName("editor.meta.vocabolario.modifica_btn");
		vocabolariButton.addActionListener(this);
		materieButton = (JButton) form.getComponentByName("editor.meta.vocabolario.materie.modifica_btn");
		materieButton.addActionListener(this);
		
		comboVocabolari = (JComboBox) form.getComponentByName("editor.meta.descrittori.vocabolari.dativocabolari.nometagvocabolario");
		
		comboVocabolari.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent e) {

				if(e.getStateChange()==ItemEvent.DESELECTED){
					if (nomeVocabolario!=null)
						updateVocaboli();
				}
				
				if(e.getStateChange()==ItemEvent.SELECTED){
						
					String[] materieToShow=getMaterieVocab((String)comboVocabolari.getSelectedItem());
					if(materieToShow!=null)
						listaMaterieSelectedVocab.setListData(materieToShow);
					else
						listaMaterieSelectedVocab.setListData(new Vector());
					
					if (vocaboliSelezionati!=null)
						for (int j=0; j<vocaboliSelezionati.length; j++)
							if (vocaboliSelezionati[j].getNome().equals(comboVocabolari.getSelectedItem()))
								setMaterieSelez(vocaboliSelezionati[j]);
					nomeVocabolario = (String) comboVocabolari.getSelectedItem();
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
		
		sottoFormTeseo.setMainComponent(getClass().getResourceAsStream("TeseoBrowser.jfrm"));
		materie_teseo_listtextfield.setEditor(new MaterieTeseoListTextFieldEditor(sottoFormTeseo));
		
		try {
			loadVocabulary();
		} catch (Exception e) {}
		
		
	}

	private void loadVocabulary() {
		
		String prefix ="vocabolari";
		String[] vocabulary = new String[]{"TESEO.xml", "regioneUmbria.xml", "CNIPA.xml", "regioneToscana.xml"};

		for (int i = 0; i < vocabulary.length; i++)		
			//Non sovrascrivo i vocabolari già presenti
			if (!new File(UtilFile.getTempDirName() + File.separatorChar + prefix+File.separator+vocabulary[i]).exists())	
				UtilFile.copyFileInTempDir(getClass().getResourceAsStream(prefix + "/" + vocabulary[i]), prefix, vocabulary[i]);
				
		
		//TODO: recupero anche altri vocabolari presenti nella temp (vocabolari)
		//TODO: permettere il salvataggio dei vocabolari modifcati e/o nuovi
		
		vocabolari = new Vocabolario[vocabulary.length];
		for (int i = 0; i < vocabulary.length; i++) { 
			Document voc = UtilXml.readXML(UtilFile.getTempDirName() + File.separatorChar + prefix+File.separator+vocabulary[i]);
			Node radice = (Node) voc.getDocumentElement();
			vocabolari[i] = new Vocabolario();
			Vector listaMaterie = new Vector();
			vocabolari[i].setNome(UtilDom.getAttributeValueAsString(radice, "nome"));
			componiMateria(listaMaterie, radice, "");
			String[] temp = new String[listaMaterie.size()];
			listaMaterie.copyInto(temp);
			vocabolari[i].setMaterie(temp);
			logger.debug("Letto vocabolario: " + vocabolari[i].getNome());
		}	
	}
	
	private void componiMateria(Vector listaMaterie, Node nodo, String materia) {
		
		if (nodo.getNodeType()==Node.TEXT_NODE)
			return;
		NodeList figli = nodo.getChildNodes();
        if (figli.getLength() == 0)
        	listaMaterie.add(materia.substring(1));
		else {
			if (!"".equals(materia)) //voglio rendere disponibile anche le voci contenitore, come materie
				listaMaterie.add(materia.substring(1)); 
			for (int i=0; i<figli.getLength(); i++)
				componiMateria(listaMaterie, figli.item(i), materia+";"+UtilDom.getAttributeValueAsString(figli.item(i), "nome"));
		}	
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
				 utilmsg.msgInfo("materie.vocabolari");				 
				 return;
			 }
			 
			 Vector v = new Vector();
			 String[] materieVocab=getMaterieSelectedVocabolario();
				if (materieVocab != null) {
					for (int i = 0; i < materieVocab.length; i++) {
						v.add(materieVocab[i]);
					}
				}
				if(isWin() && isteseoOK()){
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

	private boolean isWin() {
		
		//disabilito il teseo
		//return JdicManager.getPlatform().equals("windows");
		
		return false;
	}
	
	private boolean isteseoOK() {
				
		if (!"teseo".equalsIgnoreCase((String) comboVocabolari.getSelectedItem()))
			return false;

		//test connessione
		JEditorPane editor = new JEditorPane();
	    try {
	       editor.setPage("http://www.senato.it/");
	    } catch (IOException e) {
	    	logger.debug("Connessione assente");
			return false;
	    }
		return true;
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
			return "editor.form.meta.descrittori.msg.err.datimaterie";
		}

		public Dimension getPreferredSize() {
			return new Dimension(600, 150);
		}
	}
	/**
	 * Editor per il ListTextField della lista delle materie
	 */
	private class MaterieListTextFieldEditor implements ListTextFieldEditor{

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
	 * Editor per il ListTextField della lista delle materie di teseo
	 */
	private class MaterieTeseoListTextFieldEditor implements ListTextFieldEditor, EventManagerListener {
		
		Form form;
		Vector terminiSelezionati = new Vector();
		boolean primaVolta = true;
		DefaultListModel listModel = new DefaultListModel();
		JList listaSelezionati;
				
		public MaterieTeseoListTextFieldEditor(Form form) {		
			this.form = form;
			listaSelezionati = (JList) form.getComponentByName("editor.meta.teseo.scelte");
		}
		
		public Component getAsComponent() {
			return form.getAsComponent();
		}

		public Object getElement() {
			
			String[] temp = new String[terminiSelezionati.size()];
			for (int i=0; i<terminiSelezionati.size(); i++)
				temp[i] = (String) terminiSelezionati.get(i);
			return temp;
		}

		public void setElement(Object object) {			
			
			if (primaVolta)  
				init();			
			setPaginaIniziale();
		}

		private void init() {
			//disabilito il teseo
//			try {
//				form.replaceComponent("editor.meta.teseo.browser.interno", browserForm.getAsComponent());				
//			} catch (FormException e) {
//				e.printStackTrace();
//			}
//			eventManager.addListener(this, BrowserEvent.class);
//			browserForm.setUrlListener("http://www.senato.it/App/Search/sddl.asp#Cla");			
		}

		private void setPaginaIniziale() {

				//disabilito il teseo
//				logger.debug("Apro pagina iniziale Teseo");
//				try {
//					browserForm.setUrl(new URL("http://www.senato.it/App/Search/sddl.asp?CmdSelCla=Sistema+TESEO"));
//				} catch (MalformedURLException e) {
//					e.printStackTrace();
//				}

			terminiSelezionati.clear();
			listModel.clear();

		}

		public void clearFields() {
			
			if (primaVolta)  
				init();
			setPaginaIniziale();
			
		}
		
		private void estraiSelezionati(String content) {

//disabilito il teseo
//			// Estraggo i termini selezionati dall'HTML			
//			try {
//				// Mi avvicino alla zona della selezione in piï¿½ passi (migliorabile)
//				content = content.substring(content.indexOf("almeno un termine"), content.length());
//				content = content.substring(content.indexOf("Sistema TESEO"),content.length());
//				content = content.substring(content.indexOf("checkSubmit(event)"), content.length());
//				// Taglio fino a qui
//				content = content.substring(0, content.indexOf("Cerca nella classificazione"));
//				while (content.indexOf("valore") != -1) {
//					content = content.substring(content.indexOf("valore"), content.length());
//					content = content.substring(content.indexOf(">"), content.length());
//					terminiSelezionati.addElement(content.substring(1, content.indexOf("<")));
//					content = content.substring(content.indexOf("<"), content.length());
//				}
//				listaSelezionati.setListData(terminiSelezionati);
//				
//			} catch (StringIndexOutOfBoundsException e) {
//				logger.error("Errore nel parser del Teseo");
//			}							
//			try {
//				browserForm.setUrl(new URL("http://www.senato.it/App/Search/sddl.asp?CmdSelCla=Sistema+TESEO"));
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
		}

		public boolean checkData() {
			
			if (terminiSelezionati.isEmpty())
				return false;				
			return true;
		}

		public String getErrorMessage() {			
			return "editor.meta.teseo.info";
		}
		public Dimension getPreferredSize() {
			if (primaVolta) {
				primaVolta = false;
				return new Dimension(800, 600);
			}	
			return null;
		}

		public void manageEvent(EventObject event) {
			
			BrowserEvent e = (BrowserEvent) event;
			if (e.isUrlDownload()) {

				logger.debug("Selezione da browser confermata");
				estraiSelezionati((String) e.getSource());
			}	
		}
	}
}
