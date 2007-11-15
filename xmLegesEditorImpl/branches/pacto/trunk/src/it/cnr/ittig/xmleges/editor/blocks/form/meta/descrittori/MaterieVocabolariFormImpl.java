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
import it.cnr.ittig.xmleges.editor.blocks.form.browser.BrowserEvent;
import it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MaterieVocabolariForm;
import it.ipiu.digest.parse.Archivio;
import it.ipiu.digest.parse.Materia;
import it.ipiu.digest.parse.ParseXmlToVocabolario;
import it.ipiu.digest.parse.Vocabolario;
import it.jaime.configuration.ConfigurationFacade;
import it.jaime.utilities.file.FileUtility;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JList;

public class MaterieVocabolariFormImpl implements MaterieVocabolariForm,
Loggable, Serviceable, Initializable, ActionListener {

	// MaterieVocabolari.jfrm
	Form form;

	Logger logger;

	String errorMessage = "";

	UtilMsg utilmsg;

	JButton materieButton;

	// Form ListTextFile materie
	// Materie.jfrm
	Form formMaterie;

	JList listaMaterieSelectedVocab;

	ListTextField materie_listtextfield;

	// Form ListTextFile del browser Teseo
	// Teseo.jfrm
	Form formMaterieTeseo;

	ListTextField materie_teseo_listtextfield;

	// Form ListTextFiled vocabolari
	// Vocabolari.jfrm
	Form formVocabolari;

	JButton vocabolariButton;

	JComboBox comboVocabolari;

	ListTextField vocabolari_listtextfield;

	// Mie variabili
	Form sottoFormTeseo;

	ListTextField teseo_listtextfield;

	// disabilito il teseo
	// BrowserForm browserForm;
	EventManager eventManager;

	private Archivio archivio;
	
	String vocabolariSelectedItem;
	
	public boolean openForm() {
		form.setSize(450, 300);
		form.showDialog();
		return form.isOk();

	}

	/**
	 * Recupera la lista di materia appartenenti al vocabolario selezionato
	 * 
	 * @param vocabolario
	 * @return an array of String
	 */
	private String[] getMaterieVocab(String vocabolario) {

		/*
		 * 
		 * for (int i = 0; i < vocabolari.length; i++) { if
		 * (vocabolari[i].getNome().equals(vocabolario)) { return
		 * vocabolari[i].getMaterie(); } } return null;
		 */
		return this.archivio.getMaterieSelectedVocabolario(vocabolario);
	}

	/**
	 * Setta una lista di materie al vocabolario passato come parametro
	 * 
	 * @param materie
	 * @param vocabolario
	 */
	private void setMaterieVocab(String[] materie, String vocabolario) {

		this.archivio.setVocabolario(materie, vocabolario);
	}

	
	public Vocabolario[] getVocabolari() {
		return this.archivio.getVocabolari();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MaterieVocabolariForm#setVocabolari(it.ipiu.digest.parse.Vocabolario[])
	 */
	public void setVocabolari(Vocabolario[] vocabolari) {
		this.archivio.setVocabolari(vocabolari);

		comboVocabolari.removeAllItems();
		listaMaterieSelectedVocab.setListData(new Vector());
		if (vocabolari != null) {
			for (int i = 0; i < vocabolari.length; i++) {
				comboVocabolari.addItem(vocabolari[i]);
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
		sottoFormTeseo = (Form) serviceManager.lookup(Form.class);
		
		// disabilito il teseo
		// try {
		// if (isWin())
		// browserForm = (BrowserForm) serviceManager.lookup(BrowserForm.class);
		// }
		// catch (Exception e){
		// //Probabilmente non caricato
		// }
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
		
		
		/* ********* Modifiche I+ ********* */
		listaMaterieSelectedVocab = (JList)form.getComponentByName("editor.meta.descrittori.vocabolari.riepilogo.materie");
				
		comboVocabolari.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {

					vocabolariSelectedItem = comboVocabolari.getSelectedItem().toString();
																
					String[] materieToShow = getMaterieVocab(vocabolariSelectedItem);
					
					if (materieToShow != null){
						listaMaterieSelectedVocab.setListData(materieToShow);
						}
					else
						listaMaterieSelectedVocab.setListData(new Vector());
				}
			}
		});
		
		listaMaterieSelectedVocab = (JList) form.getComponentByName("editor.meta.descrittori.vocabolari.riepilogo.materie");
				
		formVocabolari.setMainComponent(getClass().getResourceAsStream("Vocabolari.jfrm"));
		formVocabolari.replaceComponent("editor.meta.descrittori.materie.vocabolari.listtextfield",	vocabolari_listtextfield.getAsComponent());
		formVocabolari.setSize(650, 400);
		formVocabolari.setName("editor.meta.descrittori.materie.vocabolari");
		vocabolari_listtextfield.setEditor(new VocabolariListTextFieldEditor());

		formMaterie.setMainComponent(getClass().getResourceAsStream("Materie.jfrm"));
		formMaterie.replaceComponent("editor.meta.descrittori.materie.materie.listtextfield",
				materie_listtextfield.getAsComponent());
		formMaterie.setSize(650, 400);
		formMaterie.setName("editor.meta.descrittori.materie.materie");

		materie_listtextfield.setEditor(new MaterieListTextFieldEditor());

		formMaterieTeseo.setMainComponent(getClass().getResourceAsStream(
		"Teseo.jfrm"));
		formMaterieTeseo.replaceComponent(
				"editor.meta.descrittori.materie.materie.listtextfield",
				materie_teseo_listtextfield.getAsComponent());
		formMaterieTeseo.setSize(650, 400);
		formMaterieTeseo
		.setName("editor.meta.descrittori.materie.materieteseo");

		sottoFormTeseo.setMainComponent(getClass().getResourceAsStream("TeseoBrowser.jfrm"));
		materie_teseo_listtextfield.setEditor(new MaterieTeseoListTextFieldEditor(sottoFormTeseo));
	
		//IPIU-TODO configuration
		
		String filename = ConfigurationFacade.get("config.properties/archivio.path");
		String path = FileUtility.getInstance().getPath(filename);
	
		this.archivio = ParseXmlToVocabolario.parse(path);
		
		setVocabolari(this.archivio.getVocabolari());
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(vocabolariButton)) { 
			// VOCABOLARI
			modificaVocabolario();
		} else if (e.getSource().equals(materieButton)) { 
			// MATERIE
			if (comboVocabolari.getSelectedItem() == null) {
				utilmsg.msgInfo("materie.vocabolari");
				return;
			}
			modificaMaterie();
		}
	}

	/**
	 * Metodo richiamanto dal listener sul bottone "modifica materie", 
	 * della finestra "Materie".
	 */
	private void modificaMaterie() {
		// NOTA : converto le materie del vocabolario selezionate da un String[] a un Vector.
		Vector v = new Vector();
		String[] materieVocab = getMaterieSelectedVocabolario();
		if (materieVocab != null) {
			for (int i = 0; i < materieVocab.length; i++) {
				v.add(materieVocab[i]);
			}
		}
		//
		materie_listtextfield.setListElements(v);
		formMaterie.showDialog();

		if (formMaterie.isOk()) {
			materieVocab = new String[materie_listtextfield.getListElements().size()];
			materie_listtextfield.getListElements().toArray(materieVocab);

			listaMaterieSelectedVocab.setListData(materieVocab);

			setMaterieVocab(materieVocab, comboVocabolari.getSelectedItem().toString());
		}
}


	/**
	 *  FIXME : da inizializzare l'archivio con quello che viene messo in
	 *  vocabolari
	 *  
	 *  Metodo richiamanto dal listener sul bottone "modifica vocabolario"
	 *  della finestra "Materie" 
	 */
	private void modificaVocabolario() {

		vocabolari_listtextfield.setListElements(this.archivio.getVocabolariCollection());
		formVocabolari.showDialog();

		if (formVocabolari.isOk()) {
			Vector listElements = vocabolari_listtextfield.getListElements();
			archivio.setVocabolari(listElements);	
			
				comboVocabolari.removeAllItems();
				for (int i = 0; i < archivio.getVocabolari().length; i++) {
					comboVocabolari.addItem(archivio.getVocabolario(i));
				}
				comboVocabolari.setSelectedIndex(comboVocabolari.getItemCount() - 1);
				}
	}

	private boolean isWin() {

		// disabilito il teseo
		// return JdicManager.getPlatform().equals("windows");

		return false;
	}

	private boolean isteseoOK() {

		if (!"teseo".equalsIgnoreCase((String) comboVocabolari
				.getSelectedItem()))
			return false;

		// test connessione
		JEditorPane editor = new JEditorPane();
		try {
			editor.setPage("http://www.senato.it/");
		} catch (IOException e) {
			logger.debug("Connessione assente");
			return false;
		}
		return true;
	}

	/**
	 * add  specified {@link Vocabolario} at {@link Archivio}
	 * @param nome
	 */
	private void addVocabolario(String nome) {
		this.archivio.addVocabolario(new Vocabolario(nome));
	}

	/**
	 * remove {@link Vocabolario} specified from {@link Archivio}
	 * @param index
	 */
	private void removeVocabolario(int index) {
		this.archivio.removeVocabolario(index);
	}

	/**
	 * @return the array of {@link Materia} instance from {@link Vocabolario}
	 *         selected in the {@link MaterieVocabolariFormImpl#comboVocabolari}.
	 */
	private String[] getMaterieSelectedVocabolario() {

		return this.archivio.getMaterieSelectedVocabolario( comboVocabolari.getSelectedItem().toString());
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
	private class MaterieListTextFieldEditor implements ListTextFieldEditor {

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
			listaSelezionati = (JList) form
			.getComponentByName("editor.meta.teseo.scelte");
		}

		public Component getAsComponent() {
			return form.getAsComponent();
		}

		public Object getElement() {

			String[] temp = new String[terminiSelezionati.size()];
			for (int i = 0; i < terminiSelezionati.size(); i++)
				temp[i] = (String) terminiSelezionati.get(i);
			return temp;
		}

		public void setElement(Object object) {

			if (primaVolta)
				init();
			setPaginaIniziale();
		}

		private void init() {
			// disabilito il teseo
			// try {
			// form.replaceComponent("editor.meta.teseo.browser.interno",
			// browserForm.getAsComponent());
			// } catch (FormException e) {
			// e.printStackTrace();
			// }
			// eventManager.addListener(this, BrowserEvent.class);
			// browserForm.setUrlListener("http://www.senato.it/App/Search/sddl.asp#Cla");
		}

		private void setPaginaIniziale() {

			// disabilito il teseo
			// logger.debug("Apro pagina iniziale Teseo");
			// try {
			// browserForm.setUrl(new
			// URL("http://www.senato.it/App/Search/sddl.asp?CmdSelCla=Sistema+TESEO"));
			// } catch (MalformedURLException e) {
			// e.printStackTrace();
			// }

			terminiSelezionati.clear();
			listModel.clear();

		}

		public void clearFields() {

			if (primaVolta)
				init();
			setPaginaIniziale();

		}

		private void estraiSelezionati(String content) {

			// disabilito il teseo
			// // Estraggo i termini selezionati dall'HTML
			// try {
			// // Mi avvicino alla zona della selezione in pi� passi
			// (migliorabile)
			// content = content.substring(content.indexOf("almeno un termine"),
			// content.length());
			// content = content.substring(content.indexOf("Sistema
			// TESEO"),content.length());
			// content =
			// content.substring(content.indexOf("checkSubmit(event)"),
			// content.length());
			// // Taglio fino a qui
			// content = content.substring(0, content.indexOf("Cerca nella
			// classificazione"));
			// while (content.indexOf("valore") != -1) {
			// content = content.substring(content.indexOf("valore"),
			// content.length());
			// content = content.substring(content.indexOf(">"),
			// content.length());
			// terminiSelezionati.addElement(content.substring(1,
			// content.indexOf("<")));
			// content = content.substring(content.indexOf("<"),
			// content.length());
			// }
			// listaSelezionati.setListData(terminiSelezionati);
			//				
			// } catch (StringIndexOutOfBoundsException e) {
			// logger.error("Errore nel parser del Teseo");
			// }
			// try {
			// browserForm.setUrl(new
			// URL("http://www.senato.it/App/Search/sddl.asp?CmdSelCla=Sistema+TESEO"));
			// } catch (MalformedURLException e) {
			// e.printStackTrace();
			// }
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

	/* (non-Javadoc)
	 * @see it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MaterieVocabolariForm#geVocabolarioSelected()
	 */
	public Vocabolario geVocabolarioSelected() {
		Vocabolario vocabolarioSelected  = new Vocabolario(comboVocabolari.getSelectedItem().toString());
		if (listaMaterieSelectedVocab.getSelectedValue() != null) {
			Object[] materieSelected = listaMaterieSelectedVocab.getSelectedValues();
			for (int i = 0; i < materieSelected.length; i++) {
				vocabolarioSelected.addMateria(materieSelected[i].toString());
			}
		}
		return vocabolarioSelected;
	}
}