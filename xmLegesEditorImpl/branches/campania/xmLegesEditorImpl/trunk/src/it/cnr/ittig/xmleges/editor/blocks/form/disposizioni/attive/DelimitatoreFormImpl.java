package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DelimitatoreForm;


public class DelimitatoreFormImpl implements DelimitatoreForm, Loggable, Serviceable, Initializable, ActionListener, FormVerifier {
	
	Logger logger;

	Form form;
	
	JCheckBox[] ordinale = new JCheckBox[5];
	JComboBox[] tipo = new JComboBox[5];
	JComboBox[] bis = new JComboBox[5];
	JTextField[] numero = new JTextField[5];
	JLabel delimitatore;
	JLabel num;
	JLabel bisEti;
	
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
	}
	
	public void initialize() throws Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Delimitatore.jfrm"));
		form.setName("editor.form.disposizioni.delimitatore");
		form.setHelpKey("help.contents.form.disposizioniattive.delimitatore");
		
		ordinale[0] = (JCheckBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.ordinale0");
		ordinale[1] = (JCheckBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.ordinale1");
		ordinale[2] = (JCheckBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.ordinale2");
		ordinale[3] = (JCheckBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.ordinale3");
		ordinale[4] = (JCheckBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.ordinale4");
		tipo[0] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.tipo0");
		tipo[1] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.tipo1");
		tipo[2] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.tipo2");
		tipo[3] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.tipo3");
		tipo[4] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.tipo4");
		numero[0] = (JTextField) form.getComponentByName("editor.disposizioni.attive.num.numero0");
		numero[1] = (JTextField) form.getComponentByName("editor.disposizioni.attive.num.numero1");
		numero[2] = (JTextField) form.getComponentByName("editor.disposizioni.attive.num.numero2");
		numero[3] = (JTextField) form.getComponentByName("editor.disposizioni.attive.num.numero3");
		numero[4] = (JTextField) form.getComponentByName("editor.disposizioni.attive.num.numero4");
		delimitatore = (JLabel) form.getComponentByName("editor.disposizioni.attive.delimitatore.eti");
		num = (JLabel) form.getComponentByName("editor.disposizioni.attive.num.eti");
		bisEti = (JLabel) form.getComponentByName("editor.disposizioni.attive.bis.eti");
		bis[0] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.bis0");
		bis[1] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.bis1");
		bis[2] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.bis2");
		bis[3] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.bis3");
		bis[4] = (JComboBox) form.getComponentByName("editor.disposizioni.attive.delimitatore.bis4");
		
		for (int i=0; i<5; i++) {
			popolaControlli(tipo[i]);
			tipo[i].addActionListener(this);
			popolaLatinSource(bis[i]);
		}
	}

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public String[] getDelimitatore() {

		for (int i=4; i>=0; i--) {
			if (" ".equals(tipo[i].getSelectedItem()))
					continue;
			String[] selezionati = new String[(i+1)*3];
			for (int j=0; j<=i; j++) {
				selezionati[j*3]=(String) tipo[j].getSelectedItem();
				if (" ".equals(bis[j].getSelectedItem()))
					selezionati[j*3+1]= numero[j].getText();
				else
					selezionati[j*3+1]= numero[j].getText().trim() + bis[j].getSelectedItem().toString().replaceAll(" ", "");
				selezionati[j*3+2]= ordinale[j].isSelected() ? "ordinale": "";
			}
			return selezionati;
		}
		return new String[0];
	}

	public boolean openForm(String[] selezionati) {
		
		initForm(selezionati);
		form.setSize(380, 250);
		form.addFormVerifier(this);
		form.showDialog();
		return form.isOk();
	}
		
	private void initForm(String[] selezionati) {

		for (int i=0; i<selezionati.length/3; i++) {
			tipo[i].setSelectedItem(selezionati[i*3]);
			String stringaNum = selezionati[1+i*3];
			String bisTerIndividuato="";
			for (int j=0; j<bisTer.length; j++)
				if (stringaNum.indexOf(bisTer[j])!=-1)	//individuato bis, ter, ... nel NUM
					if (bisTerIndividuato.length() < bisTer[j].length())
						bisTerIndividuato = bisTer[j];
			if ("".equals(bisTerIndividuato))
				numero[i].setText(stringaNum);
			else
				numero[i].setText(stringaNum.substring(0, stringaNum.indexOf(bisTerIndividuato)));
			bis[i].setSelectedItem(bisTerIndividuato);
			ordinale[i].setSelected(!"".equalsIgnoreCase(selezionati[2+i*3]));
			tipo[i].setEnabled(true);
			numero[i].setEnabled(true);
			ordinale[i].setEnabled(true);
		} 
		for (int i=selezionati.length/3; i<5; i++) {
			tipo[i].setSelectedItem(" ");
			numero[i].setText("");
			tipo[i].setEnabled(false);
			numero[i].setEnabled(false);
			ordinale[i].setSelected(false);
			ordinale[i].setEnabled(false);
			bis[i].setSelectedItem(" ");
			bis[i].setEnabled(false);
		}
		if (selezionati.length/3<5) {
			tipo[selezionati.length/3].setEnabled(true); 
			numero[selezionati.length/3].setEnabled(true);
			ordinale[selezionati.length/3].setEnabled(true);
			bis[selezionati.length/3].setEnabled(true);
		}	
	}

	private void popolaControlli(JComboBox combo) {
		combo.addItem(" ");
		combo.addItem("articolo");
		combo.addItem("rubrica");
		combo.addItem("alinea");
		combo.addItem("coda");
		combo.addItem("comma");
		combo.addItem("lettera");
		combo.addItem("numero");
		combo.addItem("punto");
		combo.addItem("periodo");
		combo.addItem("capoverso");
	}

	private String[] bisTer = {
			"bis",
			"ter",
			"quater",
			"quinquies",
			"sexies",
			"septies",
			"octies",
			"novies",
			"decies",
			"undecies",
			"duodecies",
			"terdecies",
			"quaterdecies",
			"quinquiesdecies",
			"sexiesdecies",
			"septiesdecies",
			"duodevicies",
			"undevicies",
			"vicies",
			"viciessemel",
			"viciesbis",
			"viciester",
			"viciesquater",
			"viciesquinques",
			"viciessexies",
			"viciessepteis",
			"duodetricies",
			"undetricies",
			"tricies",
			"triciessemel",
			"triciesbis",
			"triciester",
			"triciesquater",
			"triciesquinques",
			"triciessexies",
			"triciessepteis",
			"duodequadragies",
			"undequadragies",
			"quadragies",
			"quadragiessemel",
			"quadragiesbis",
			"quadragiester",
			"quadragiesquater",
			"quadragiesquinques",
			"quadragiessexies",
			"quadragiessepteis",
			"duodequinquagies",
			"undequinquagies",
			"quinquagies"
	};
	
	private void popolaLatinSource(JComboBox combo) {
		combo.removeAllItems();
		combo.addItem(" ");
		for (int i=0; i<bisTer.length; i++)
			combo.addItem(bisTer[i]);
	}	

	public void actionPerformed(ActionEvent e) {
		
		for (int i=0; i<4; i++)
			if (e.getSource() == tipo[i]) {
				if (" ".equals(tipo[i].getSelectedItem())) {
					numero[i].setText("");
					for (int j=i+1; j<5; j++) {
						tipo[j].setSelectedItem(" ");
						tipo[j].setEnabled(false);
						numero[j].setText("");
						numero[j].setEnabled(false);
						ordinale[j].setSelected(false);
						ordinale[j].setEnabled(false);
						bis[j].setSelectedItem(" ");
						bis[j].setEnabled(false);
					}
				}
				if ("alinea".equals(tipo[i].getSelectedItem()) | "rubrica".equals(tipo[i].getSelectedItem()) | "coda".equals(tipo[i].getSelectedItem())) {
					numero[i].setText("");
					numero[i].setEnabled(false);
					ordinale[i].setSelected(false);
					ordinale[i].setEnabled(false);
					bis[i].setSelectedItem(" ");
					bis[i].setEnabled(false);
				}
				else {
					numero[i].setEnabled(true);
					ordinale[i].setEnabled(true);
					bis[i].setEnabled(true);
				}
				numero[i+1].setEnabled(true);
				tipo[i+1].setEnabled(true);
				ordinale[i+1].setEnabled(true);
				bis[i].setEnabled(true);
				break;
			}
	}

	public String getErrorMessage() {
		return "editor.form.disposizioni.msg.error.datimancanti.text";
	}

	public boolean verifyForm() {
		for (int i=4; i>=0; i--) {
			if (" ".equals(tipo[i].getSelectedItem()) && "".equals(numero[i].getText().trim()))
				continue;	//Partizione e Occorrenza NON settati
			for (int j=0; j<=i; j++)
				if ((" ".equals(tipo[j].getSelectedItem()) && tipo[j].isEnabled()) || ("".equals(numero[j].getText().trim()) && numero[j].isEnabled()))
					return false;	//Partizione e/o Occorrenza mancanti
			break;
		}			
		return true;
	}
	
}
