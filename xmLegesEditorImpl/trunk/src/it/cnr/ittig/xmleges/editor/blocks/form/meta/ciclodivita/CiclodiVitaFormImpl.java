package it.cnr.ittig.xmleges.editor.blocks.form.meta.ciclodivita;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaEventoForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MetaDescrittoriForm</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, <a
 *         href="mailto:t.paba@onetech.it">Tommaso Paba</a>
 */
public class CiclodiVitaFormImpl implements CiclodiVitaForm, Loggable, Serviceable, Initializable, ActionListener, FormVerifier {

	Logger logger;

	//   Ciclodivita.jfrm
	Form form;
	
	
	CiclodiVitaEventoForm formEventi;

	String tipoDTD;

	String tipoDocumento;

	JButton eventoButton;
	
	JList eventiList;
	
	Evento[] eventi;
	
//	ListTextField rel_listtextfield;

		
//	JComboBox tagEffettoTipoSFormDatiRelazione;
	
//	JLabel labelEffettoTipo;

//	UrnForm urnFormRelazioni;
	
	String errorMessage = "";





	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);		
		
		formEventi = (CiclodiVitaEventoForm) serviceManager.lookup(CiclodiVitaEventoForm.class);

	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		
		
		form.setMainComponent(getClass().getResourceAsStream("Ciclodivita.jfrm"));
		
		form.setHelpKey("help.contents.form.ciclodivita");
		
		
		eventiList = (JList) form.getComponentByName("editor.form.meta.ciclodivita.eventi");
	
		form.setName("editor.form.meta.ciclodivita.riepilogo");
		
	
		eventoButton = (JButton) form.getComponentByName("editor.form.meta.ciclodivita.riepilogo.eventi_btn");
		
		eventoButton.addActionListener(this);
		
	}

	// ////////////////////////////////////////////// CiclodiVitaForm Interface
	public boolean openForm() {
		form.setSize(650, 400);
		form.showDialog();
		return form.isOk();
	}

	public void actionPerformed(ActionEvent e) {				
		 if (e.getSource().equals(eventoButton)) { 			// EVENTI
			formEventi.setRel_totali(null/*getRelazioniUlteriori()*/);
			formEventi.setEventi(eventi);
			setEventi(eventi);
			
			formEventi.setTipoDTD(tipoDTD);
			
			if (formEventi.openForm()) {
				eventi = formEventi.getEventi();
				eventiList.setListData(eventi);
			}
		}
		
	}

	public int getEventoSelezionato() {
		if (eventiList.isSelectionEmpty())
			return -1;
		else
			return eventiList.getSelectedIndex();
	}
	
	
	public Evento[] getEventi() {
		return eventi;
	}

	

	public void setTipoDTD(String tipoDTD) {
		this.tipoDTD = tipoDTD;
	}

	public void setEventi(Evento[] eventi) {
		this.eventi = eventi;		
		eventiList.setListData(eventi);
	}

	

//	/**
//	 * Restituisce un ID univoco per una nuova relazione.
//	 */
//	private String calcolaIDRelazione(String nomeTag) {
//		
//		String prefix = "r";
//
//		if (nomeTag.equals("attiva")) {
//			prefix = "ra";
//		} else if (nomeTag.equals("passiva")) {
//			prefix = "rp";
//		} else if (nomeTag.equals("originale")) {
//			prefix = "ro";
//		} else if (nomeTag.equals("giurisprudenza")) {
//			prefix = "rg";
//		} else if (nomeTag.equals("haallegato")) {
//			prefix = "haa";
//		}else if (nomeTag.equals("allegatodi")) {
//			prefix = "all";
//		}
//
//		String uID = prefix;
//		int max = 0;
//
//		
//		// e poi nelle relazioni degli eventi
//		for (int i = 0; i < eventi.length; i++) {
//			if (eventi[i].getFonte() != null) {
//				
//				try {
//					String s = eventi[i].getFonte().getId().substring(0, prefix.length());
//					if (s.equals(prefix)) {
//						Integer idValue = Integer.decode(eventi[i].getFonte().getId().substring(prefix.length()));
//						if (idValue.intValue() > max) {
//							max = idValue.intValue();
//						}
//					}
//				} catch (IndexOutOfBoundsException exc) {
//				}
//			}
//		}
//
//		uID += (max + 1);
//		
//		return uID;
//	}

	public Relazione[] getRelazioniTotalefromCdvf() {
		
//		 Ricomponi le relazioni eliminando quelle duplicate (caso di +eventi linkati ad 1 relazione)
		Vector relazioniVect = new Vector();
		boolean duplicated;
		
		for (int i = 0; i < eventi.length; i++) {
			duplicated = false;
			
			for(int j=0; j<relazioniVect.size();j++){
		        if(((Relazione)relazioniVect.get(j)).getId().equalsIgnoreCase(eventi[i].getFonte().getId()))
		        	duplicated = true;
			}
			if(!duplicated)
				relazioniVect.add(eventi[i].getFonte());		
		}
		

		Relazione[] newRelazioni = new Relazione[relazioniVect.size()];
		relazioniVect.copyInto(newRelazioni);
		return newRelazioni;
	}


	public boolean verifyForm() {
		return true;
	}

	public String getErrorMessage() {
		return errorMessage;
	}



	
}
