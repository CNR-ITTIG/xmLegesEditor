package it.cnr.ittig.xmleges.editor.blocks.form.filenew;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.editor.services.form.filenew.FileNewForm;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.ClasseItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.DtdItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.ProvvedimentiItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.TemplateItem;

import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.form.filenew.FileNewForm</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>Form</li>
 * <li>Provvedimenti</li>
 * <li>UtilUI</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * 
 * @version 1.0
 * @author Lorenzo Sarti, Tommaso Paba <t.paba@onetech.it>
 */
public class FileNewFormImpl implements FileNewForm, Loggable, Serviceable, Initializable, FormVerifier, ListSelectionListener {

	Logger logger;

	Form form;

	Provvedimenti provvedimenti;

	UtilUI utilui;

	JList classilist;

	JList provvedimentilist;

	JList strutturelist;

	JList dtdlist;

	JLabel classilabel;

	JLabel provvedimentilabel;

	JLabel strutturelabel;

	JLabel dtdlabel;

	ClasseItem[] classlist;

	boolean isOKclicked = false;

	String requiredDtd = null;

	String errorMessage = "";

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		provvedimenti = (Provvedimenti) serviceManager.lookup(Provvedimenti.class);
		utilui = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	public boolean isOKClicked() {
		return isOKclicked;
	}

	private boolean checkTemplate(ClasseItem classe) {
		ProvvedimentiItem[] provvedimenti = classe.getProvvedimentiList();
		for (int i = 0; i < provvedimenti.length; i++) {
			if (!provvedimenti[i].getHasTemplate())
				return false;
			if (requiredDtd != null) {
				DtdItem[] listaDtd = provvedimenti[i].getDtdList();
				boolean foundDtd = false;
				for (int j = 0; j < listaDtd.length; j++) {
					if (listaDtd[j].getFileName().equalsIgnoreCase(requiredDtd))
						foundDtd = true;
				}
				if (!foundDtd)
					return false;
			}
		}
		return true;
	}

	public void openForm(String requiredDtd) {
		this.requiredDtd = requiredDtd;
		openForm();
		this.requiredDtd = null;
	}

	public void openForm() {
		if (!form.hasMainComponent())
			try {
				initialize();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		logger.debug("Lettura classi di provvedimento");
		ClasseItem[] classlist = provvedimenti.getAllClassi();
		logger.debug("Fine lettura");
		DefaultListModel lmd = new DefaultListModel();

		for (int i = 0; i < classlist.length; i++) {
			if (checkTemplate(classlist[i]))
				lmd.addElement(classlist[i]);
		}

		classilist.setModel(lmd);
		classilist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		classilist.addListSelectionListener(this);
		classilist.setSelectedIndex(0);

		provvedimentilist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		provvedimentilist.addListSelectionListener(this);
		provvedimentilist.setSelectedIndex(0);

		strutturelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		popolaListaStrutture((ProvvedimentiItem) provvedimentilist.getSelectedValue());

		dtdlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		popolaListaDtd((ProvvedimentiItem) provvedimentilist.getSelectedValue());

		form.showDialog();

		isOKclicked = form.isOk();
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("FileNew.jfrm"));
		form.setSize(550, 420);
		form.setName("editor.form.filenew");
		
		form.setHelpKey("help.contents.form.filenew");

		classilist = (JList) form.getComponentByName("editor.form.filenew.list.classe");
		provvedimentilist = (JList) form.getComponentByName("editor.form.filenew.list.provvedimento");
		strutturelist = (JList) form.getComponentByName("editor.form.filenew.list.struttura");
		dtdlist = (JList) form.getComponentByName("editor.form.filenew.list.dtd");

		classilabel = (JLabel) form.getComponentByName("editor.form.filenew.label.classe");
		provvedimentilabel = (JLabel) form.getComponentByName("editor.form.filenew.label.provvedimento");
		strutturelabel = (JLabel) form.getComponentByName("editor.form.filenew.label.struttura");
		dtdlabel = (JLabel) form.getComponentByName("editor.form.filenew.label.dtd");

		form.addFormVerifier(this);
	}

	private void popolaListaProvvedimenti(ClasseItem classe) {

		DefaultListModel dlm = new DefaultListModel();
		try {
			for (int i = 0; i < classe.getProvvedimentiList().length; i++) {
				dlm.addElement(classe.getProvvedimentoAt(i));
			}
			provvedimentilist.setModel(dlm);
			provvedimentilist.setSelectedIndex(0);
		} catch (Exception e) {
		}
	}

	private void popolaListaStrutture(ProvvedimentiItem provvedimento) {

		DefaultListModel dlm = new DefaultListModel();
		try {
			for (int i = 0; i < provvedimento.getTemplateList().length; i++) {
				dlm.addElement(provvedimento.getTemplateAt(i));
			}
			strutturelist.setModel(dlm);
			strutturelist.setSelectedIndex(0);
		} catch (Exception e) {
		}
	}

	private void popolaListaDtd(ProvvedimentiItem provvedimento) {

		DefaultListModel dlm = new DefaultListModel();

		for (int i = 0; i < provvedimento.getDtdList().length; i++) {
			DtdItem dtd = provvedimento.getDtdAt(i);
			if (logger.isDebugEnabled()) {
				logger.debug("dtd name: " + provvedimento.getDtdAt(i).getDtdName());
				logger.debug("dtd file: " + provvedimento.getDtdAt(i).getFileName());
			}
			if (requiredDtd != null) {
				if (dtd.getFileName().equals(requiredDtd)) {
					dlm.addElement(dtd);
				}
			} else {
				// Se non ? richiesta una particolare DTD, aggiungi sempre
				dlm.addElement(dtd);
			}
		}
		dtdlist.setModel(dlm);
		if (dlm.size() > 0) {
			dtdlist.setSelectedIndex(0);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(classilist)) {
			popolaListaProvvedimenti((ClasseItem) classilist.getSelectedValue());
		} else if (e.getSource().equals(provvedimentilist)) {
			popolaListaStrutture((ProvvedimentiItem) provvedimentilist.getSelectedValue());
			popolaListaDtd((ProvvedimentiItem) provvedimentilist.getSelectedValue());
		}
	}

	public Properties getSelectedDTD() {

		Properties p = new Properties();
		DtdItem dtd = (DtdItem) dtdlist.getModel().getElementAt(dtdlist.getSelectedIndex());

		p.put("DOCTYPE", "<!DOCTYPE NIR SYSTEM \"" + dtd.getFileName() + "\">");

		return p;
	}

	public String getSelectedTemplate() {
		return ((TemplateItem) strutturelist.getSelectedValue()).getFileName();
	}

	public String getSelectedProvvedimento() {
		return ((ProvvedimentiItem) provvedimentilist.getSelectedValue()).getName();
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean verifyForm() {
		if (dtdlist.getSelectedIndex() == -1) {
			errorMessage = "editor.form.filenew.error.dtd";
			return false;
		}
		return true;
	}
}
