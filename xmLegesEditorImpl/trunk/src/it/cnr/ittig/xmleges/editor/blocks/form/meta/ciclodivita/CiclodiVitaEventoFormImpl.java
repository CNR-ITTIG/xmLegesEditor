/*
 * Created on 22-apr-2005 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package it.cnr.ittig.xmleges.editor.blocks.form.meta.ciclodivita;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementEvent;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementListener;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittori;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaEventoForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MetaDescrittoriVigenzaForm</code>.
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
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba </a>
 */
public class CiclodiVitaEventoFormImpl implements CiclodiVitaEventoForm, Initializable, Serviceable, FormVerifier {

	//	Eventi.jfrm
    Form form;

    //Evento.jfrm
    Form sottoFormEvento;

    //DatiEvento.jfrm
    Form sottoFormDatiEvento;

    DateForm dataFormDatiEvento;

    JTextField tagTipoEvento;

    JComboBox tagTipoRelazioneSottoFormDatiEvento;

    UrnForm urnFormRelazione;

    JComboBox tagEffettoTipoSFormDatiEvento;
    
    JLabel labelEffettoTipo;

    Evento[] eventi;

    String tipoDocumento;

    String tipoDTD;

    String errorMessage = "";

    ListTextField eventi_listtextfield;
    
    Relazione[] rel_totali;
    
    String[] eventiOnVigenze;
    
    VigenzaEntity[] vigenze;
    
    VigenzaEntity[] vigToUpdate;
    
    UtilMsg utilMsg;
    
    MetaCiclodivita ciclodivita; //dom
    
    MetaDescrittori descrittori;//dom
    
        
    DocumentManager documentManager;
    
    NirUtilDom nirUtilDom;
    
       
    
    /**
     * Editor per il ListTextField con la lista degli eventi
     */
    private class EventiListTextFieldEditor implements ListTextFieldEditor, ListTextFieldElementListener {
        Evento e;

        Form formList;
        ListTextFieldEditor listEditor;

        public EventiListTextFieldEditor(Form form) {
            this.formList = form;
            
        }

        public Component getAsComponent() {
            return formList.getAsComponent();
        }
        
        public Object getElement() {

                return e;
                

        }
        


       
		public void setElement(Object object) {
			
			e = (Evento) object;
			
			dataFormDatiEvento.set(UtilDate.normToDate(e.getData()));
			tagTipoEvento.setText(e.getTipoEvento());
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(e.getFonte().getTagTipoRelazione());
			
			if(e.getFonte().getTagTipoRelazione().equals("originale")){
				tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
				tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
			}else{
				Relazione[] rel_ins = getRelazioniTotalefromCdvEf();
				boolean found=false;
				for(int i =0;i<rel_ins.length;i++){
					if(rel_ins[i].getTagTipoRelazione().equals("originale")){	
						tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
						tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
						tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
						tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
						tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
						tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");		
						
						found=true;
						break;
					}			
				}
				if(!found){
					tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
					tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
					tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
					tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
					tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
					tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
					tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");		
						
					
					System.out.println("ERRORE!!!!");
				}

		    }
			
			
			
			try {
				urnFormRelazione.setUrn(new Urn(e.getFonte().getLink()));
			} catch (ParseException e) {
			}
			if(e.getFonte().getTagTipoRelazione().equals("giurisprudenza")){
				labelEffettoTipo.setText("Effetto:");
				tagEffettoTipoSFormDatiEvento.setEnabled(true);
				tagEffettoTipoSFormDatiEvento.setSelectedItem(e.getFonte().getEffetto_tipoall());
			}else if(e.getFonte().getTagTipoRelazione().equals("haallegato")||e.getFonte().getTagTipoRelazione().equals("allegatodi")){
				labelEffettoTipo.setText("Tipo allegato:");
				tagEffettoTipoSFormDatiEvento.setEnabled(true);
				tagEffettoTipoSFormDatiEvento.setSelectedItem(e.getFonte().getEffetto_tipoall());
			}else{
				tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
				tagEffettoTipoSFormDatiEvento.setEnabled(false);
				labelEffettoTipo.setText(" ");
			}
						
		
		}

		public void clearFields() {
			
			dataFormDatiEvento.set(null);
			tagTipoEvento.setText("");
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);			
			urnFormRelazione.setUrn(new Urn());
			labelEffettoTipo.setText(" ");
			tagEffettoTipoSFormDatiEvento.setSelectedItem(null);

			Relazione[] rel_ins = getRelazioniTotalefromCdvEf();
			boolean found=false;
			for(int i =0;i<rel_ins.length;i++){
				if(rel_ins[i].getTagTipoRelazione().equals("originale")){					
					tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
					tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
					tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
					tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
					tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
					tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");	
					tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);
					found=true;
					break;
				}			
			}
			if(!found){
				Document doc = documentManager.getDocumentAsDom();
				tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
				tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
				
				NodeList pubList = doc.getElementsByTagName("entratainvigore");
				String data="";
				if (pubList.getLength() > 0) {
					Node n = pubList.item(0);					
					data = n.getAttributes().getNamedItem("norm") != null ? n.getAttributes().getNamedItem("norm").getNodeValue() : null;
				}				
				dataFormDatiEvento.set(UtilDate.normToDate(data));
				tagTipoEvento.setText("entrata in vigore");
				tagTipoRelazioneSottoFormDatiEvento.setSelectedItem("originale");			
				urnFormRelazione.setUrn(getUrnFromDocument(doc));
				labelEffettoTipo.setText(" ");
				tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
								
			}
			

						
		}

		public boolean checkData() {
			
			
			boolean isvalid=true;
			isvalid = (dataFormDatiEvento!=null)&&(dataFormDatiEvento.getAsYYYYMMDD()!=null)&&(!dataFormDatiEvento.getAsYYYYMMDD().trim().equals(""));
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.datavuota";				
				return false;
			}
			
//			isvalid=(tagTipoEvento!=null)&&(!tagTipoEvento.getText().trim().equals(""));
//			if(!isvalid){
//				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.tipoeventovuoto";				
//				return false;
//			}
			isvalid=(urnFormRelazione.getUrn().isValid());
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.urnvuota";				
				return false;
			}	
			isvalid=(tagTipoRelazioneSottoFormDatiEvento.getSelectedItem()!=null);
			if(!isvalid){
				errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.tiporelvuoto";				
				return false;
			}else{
				if(tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().equals("giurisprudenza")){
					if(tagEffettoTipoSFormDatiEvento.getSelectedItem()==null){
						errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.effettovuoto";
						return false;
					}else return true;
					
				}else if(tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().equals("haallegato")
						||
						tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().equals("allegatodi")){
					if(tagEffettoTipoSFormDatiEvento.getSelectedItem()==null){
						errorMessage = "editor.form.meta.ciclodivita.eventi.msg.err.tipoallegato";
						return false;
					}else return true;
					
				}
			}
			return isvalid;

		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public Dimension getPreferredSize() {
			return new Dimension(800, 150);
		}
		
		
		public void elementChanged(ListTextFieldElementEvent evt) {
			 
			int eventID = evt.getID();
			
			if (eventID == ListTextFieldElementEvent.ELEMENT_ADD) {
				
				
				String nomeTag=(String)tagTipoRelazioneSottoFormDatiEvento.getSelectedItem();
				Relazione r=null;						
				if( (dataFormDatiEvento.getAsYYYYMMDD() != null)&&(!dataFormDatiEvento.getAsYYYYMMDD().trim().equals(""))
						&&(nomeTag!=null)&&(urnFormRelazione.getUrn() != null))
				{
					if((nomeTag.toString().equals("giurisprudenza"))&&(tagEffettoTipoSFormDatiEvento.getSelectedItem()!=null)){
						r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazione.getUrn().toString(),tagEffettoTipoSFormDatiEvento.getSelectedItem().toString());
					}else if(
							((nomeTag.toString().equals("haallegato"))&&(tagEffettoTipoSFormDatiEvento.getSelectedItem()!=null))
					||
					((nomeTag.toString().equals("allegatodi"))&&(tagEffettoTipoSFormDatiEvento.getSelectedItem()!=null)))
					{
						r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazione.getUrn().toString(),tagEffettoTipoSFormDatiEvento.getSelectedItem().toString());
					}
					else if(!nomeTag.equals("")){ 
						r=new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazione.getUrn().toString());
					}else 
						r=null;
					e =new Evento(calcolaIDevento(), dataFormDatiEvento.getAsYYYYMMDD(),r,tagTipoEvento.getText());
									
					if(e.getFonte().getTagTipoRelazione().equals("originale")){
						tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
						tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
						tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
						tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
						tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
						tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");	
					}
				}
				
					
				
				
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_MODIFY) {
					
				String nomeTag=tagTipoRelazioneSottoFormDatiEvento.getSelectedItem().toString();
				Evento sel_item=((Evento)eventi_listtextfield.getSelectedItem());
				//se il tipo è lo stesso di prima mantengo id
				if (sel_item.getFonte().getTagTipoRelazione().equals(nomeTag)){
					e.setFonte(new Relazione(nomeTag,sel_item.getFonte().getId(),urnFormRelazione.getUrn().toString()));
				}
				else
					e.setFonte(new Relazione(nomeTag,calcolaIDRelazione(nomeTag),urnFormRelazione.getUrn().toString()));

				if(nomeTag.equals("giurisprudenza")){							
					e.getFonte().setEffetto_tipoall((String)tagEffettoTipoSFormDatiEvento.getSelectedItem());
				} else if(nomeTag.equals("haallegato")||nomeTag.equals("allegatodi")){							
					e.getFonte().setEffetto_tipoall((String)tagEffettoTipoSFormDatiEvento.getSelectedItem());
				}
				
				e.setTipoEvento(tagTipoEvento.getText());
				e.setData(dataFormDatiEvento.getAsYYYYMMDD());
								
				if(e.getFonte().getTagTipoRelazione().equals("originale")){
					tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
					tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
					tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
					tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
					tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
					tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");	
				}
				
			} else if (eventID == ListTextFieldElementEvent.ELEMENT_REMOVE) {
				
				if(e.getFonte().getTagTipoRelazione().equals("originale")){
					utilMsg.msgError("Evento originale obbligatorio!!");
					return;
				}
				
				Vector eligibleVigs=new Vector();
				if(eventiOnVigenze!=null){
					for(int i=0; i<eventiOnVigenze.length;i++){
						if(eventiOnVigenze[i].equals(e.getId())){
							for(int j=0;j<vigenze.length;j++){
								
									if(vigenze[j].getEInizioVigore()!=null){
										if(vigenze[j].getEInizioVigore().getId().equals(e.getId())){											
												
												eligibleVigs.add(vigenze[j]);
												((VigenzaEntity)eligibleVigs.elementAt(eligibleVigs.size()-1)).setEInizioVigore(null);
											}
									}
								
								
									if(vigenze[j].getEFineVigore()!=null){
										if(vigenze[j].getEFineVigore().getId().equals(e.getId())){											
												eligibleVigs.add(vigenze[j]);
												((VigenzaEntity)eligibleVigs.elementAt(eligibleVigs.size()-1)).setEFineVigore(null);
										}
									}
							}						
						}
						
					}
				}
				
				String msg="";
				if(eligibleVigs.size()>0){
					vigToUpdate=new VigenzaEntity[eligibleVigs.size()];
					eligibleVigs.copyInto(vigToUpdate);
					msg="Evento "+e.getId()+" presente sulle seguenti vigenze: \n";
					for(int i=0; i<vigToUpdate.length;i++){
						msg+="nodo: "+vigToUpdate[i].getOnNode().toString()+"\n";
												
					}
					msg+="Eliminare?";
					if (!utilMsg.msgYesNo(msg)){
						vigToUpdate=null;
						return;
					}
					if(e.getFonte().getTagTipoRelazione().equals("originale")){				
							 if(tagTipoRelazioneSottoFormDatiEvento.getItemCount()<6)
									tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
					}	
						
				}else
					vigToUpdate=null;
				

				e.setFonte(null);
				e = null;
				dataFormDatiEvento.set(null);
				tagTipoEvento.setText("");
				tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);
				urnFormRelazione.setUrn(new Urn());
				tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
				
				
				 
			}	
		}
		
		
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {

		form = (Form) serviceManager.lookup(Form.class);
        form.setName("editor.form.meta.ciclodivita.eventi");

        sottoFormEvento = (Form) serviceManager.lookup(Form.class);
        sottoFormDatiEvento = (Form) serviceManager.lookup(Form.class);

        eventi_listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);

        dataFormDatiEvento = (DateForm) serviceManager.lookup(DateForm.class);

        urnFormRelazione = (UrnForm) serviceManager.lookup(UrnForm.class);
        
        utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
        
        descrittori = (MetaDescrittori) serviceManager.lookup(MetaDescrittori.class);
		ciclodivita = (MetaCiclodivita) serviceManager.lookup(MetaCiclodivita.class);
		
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		

		
		

		
		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		sottoFormDatiEvento.setMainComponent(getClass().getResourceAsStream("DatiEvento.jfrm"));
		sottoFormDatiEvento.replaceComponent("editor.meta.ciclodivita.evento.data", dataFormDatiEvento.getAsComponent());
		sottoFormDatiEvento.replaceComponent("editor.form.meta.urn", urnFormRelazione.getAsComponent());
		
		urnFormRelazione.setAnnessi(true);
		urnFormRelazione.setPartizioni(true);
		urnFormRelazione.setAttiGiaCitati(true);
		
		
		tagTipoEvento = (JTextField) sottoFormDatiEvento.getComponentByName("editor.form.meta.ciclodivita.evento.tipoeventi");
		
		labelEffettoTipo = (JLabel) sottoFormDatiEvento.getComponentByName("editor.form.meta.ciclodivita.evento.label.tipoeffetto");
		labelEffettoTipo.setText(" ");
		
		tagTipoRelazioneSottoFormDatiEvento = (JComboBox) sottoFormDatiEvento.getComponentByName("editor.meta.ciclodivita.evento.tiporelazione");
		tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
		tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
		tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
		tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
		tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
		tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");
		
		tagEffettoTipoSFormDatiEvento = (JComboBox) sottoFormDatiEvento.getComponentByName("editor.form.meta.ciclodivita.evento.tipoeffetto");
		
		tagTipoRelazioneSottoFormDatiEvento.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange()==ItemEvent.SELECTED){
					Object selectedTag=tagTipoRelazioneSottoFormDatiEvento.getSelectedItem();
					if (selectedTag.equals("giurisprudenza")){
						labelEffettoTipo.setText("Effetto:");
						
						tagEffettoTipoSFormDatiEvento.removeAllItems();				
						tagEffettoTipoSFormDatiEvento.addItem("normativo");
						tagEffettoTipoSFormDatiEvento.addItem("interpretativo");
						tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
						tagEffettoTipoSFormDatiEvento.setEnabled(true);
						
						
					}else if (selectedTag.equals("haallegato")||selectedTag.equals("allegatodi")){
						labelEffettoTipo.setText("Tipo allegato:");
						tagEffettoTipoSFormDatiEvento.removeAllItems();
						tagEffettoTipoSFormDatiEvento.addItem("attoallegato");
						tagEffettoTipoSFormDatiEvento.addItem("allegatointegrante");
						tagEffettoTipoSFormDatiEvento.addItem("informativo");
						tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
						tagEffettoTipoSFormDatiEvento.setEnabled(true);
						
					}
					
					else {
						tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
						tagEffettoTipoSFormDatiEvento.setEnabled(false);
						labelEffettoTipo.setText(" ");
					}
									
				}
				
			}
			
		});		
		
		
		tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
		tagEffettoTipoSFormDatiEvento.setEnabled(false);
		
		EventiListTextFieldEditor etfe = new EventiListTextFieldEditor(sottoFormDatiEvento);
		eventi_listtextfield.setEditor(etfe);
		eventi_listtextfield.addListTextFieldElementListener(etfe);
		
		sottoFormEvento.setMainComponent(getClass().getResourceAsStream("Evento.jfrm"));		
		sottoFormEvento.replaceComponent("editor.form.meta.ciclodivita.eventi", eventi_listtextfield.getAsComponent());
		sottoFormEvento.getAsComponent().setName("editor.form.meta.ciclodivita.eventi");		
		sottoFormEvento.setSize(650, 400);
		
		form.setMainComponent(getClass().getResourceAsStream("Eventi.jfrm"));		
		form.replaceComponent("editor.form.meta.ciclodivita.eventi", sottoFormEvento.getAsComponent());		
		form.addFormVerifier(this);
		

	}


	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	
	public boolean openForm() {

		form.setSize(650, 500);
		form.showDialog();
		return form.isOk();
	
	}

	// ////////////////////////////////////////////// FormVerifier Interface
	public boolean verifyForm() {
		
		return true;
				
	
	}

	public String getErrorMessage() {
		return errorMessage;
		
	}

	public Evento[] getEventi() {

		Vector e = eventi_listtextfield.getListElements();
		eventi = new Evento[e.size()];
		e.toArray(eventi);
		return eventi;
	
	}

	
	public void setEventi(Evento[] eventi) {
		
		Vector v = new Vector();
		if (eventi != null && eventi.length>0) {
			for (int i = 0; i < eventi.length; i++) {
				v.add(eventi[i]);
			}
			
			
			dataFormDatiEvento.set(UtilDate.normToDate(eventi[0].getData()));
						
			Relazione fonte = eventi[0].getFonte();
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(fonte.getTagTipoRelazione());
			if(fonte.getTagTipoRelazione().equals("giurisprudenza")){
				tagEffettoTipoSFormDatiEvento.setEnabled(true);
				tagEffettoTipoSFormDatiEvento.setSelectedItem(fonte.getEffetto_tipoall());
			}else if(fonte.getTagTipoRelazione().equals("haallegato")||fonte.getTagTipoRelazione().equals("allegatodi")){
				tagEffettoTipoSFormDatiEvento.setEnabled(true);
				tagEffettoTipoSFormDatiEvento.setSelectedItem(fonte.getEffetto_tipoall());
			}else{
				tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
				tagEffettoTipoSFormDatiEvento.setEnabled(false);
			}
			
		
			
			try {
				urnFormRelazione.setUrn(new Urn(fonte.getLink()));
			} catch (ParseException e) {
			}	
			
			
//				boolean found=false;
//				for(int i =0;i<eventi.length;i++){
//					if(eventi[i].getFonte().getTagTipoRelazione().equals("originale")){
//						tagTipoRelazioneSottoFormDatiEvento.removeItem("originale");
//						found=true;
//						break;
//					}			
//				}
//				if(!found){
//					if(tagTipoRelazioneSottoFormDatiEvento.getItemCount()<6)
//						tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
//				}
							
	
				
		}else{
			
			dataFormDatiEvento.set(null);
			tagTipoEvento.setText("");
			tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);
			tagEffettoTipoSFormDatiEvento.setSelectedItem(null);
			urnFormRelazione.setUrn(new Urn());
			eventi_listtextfield.setListElements(v);
			
			
		}
		Relazione[] rel_ins=getRelazioniTotalefromCdvEf();
		boolean found=false;
		for(int i =0;i<rel_ins.length;i++){
			if(rel_ins[i].getTagTipoRelazione().equals("originale")){
				tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
				tagTipoRelazioneSottoFormDatiEvento.addItem("attiva");
				tagTipoRelazioneSottoFormDatiEvento.addItem("passiva");
				tagTipoRelazioneSottoFormDatiEvento.addItem("giurisprudenza");
				tagTipoRelazioneSottoFormDatiEvento.addItem("haallegato");
				tagTipoRelazioneSottoFormDatiEvento.addItem("allegatodi");	
//				tagTipoRelazioneSottoFormDatiEvento.setSelectedItem(null);
				found=true;
				break;
			}			
		}
		if(!found){
			if(tagTipoRelazioneSottoFormDatiEvento.getItemCount()<6){
				tagTipoRelazioneSottoFormDatiEvento.removeAllItems();
				tagTipoRelazioneSottoFormDatiEvento.addItem("originale");
			}
				
		}
		eventi_listtextfield.setListElements(v);
	}
		


	public String getTipoDocumento() {
		
		return null;
		
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public void setTipoDTD(String tipoDTD) {
		this.tipoDTD = tipoDTD;
	}
	/**
	 * Restituisce un ID univoco per un nuovo evento
	 */
	private String calcolaIDevento() {
		String prefix = "t";
		String uID = prefix;
		int max = 0;

		// TODO questo codice in pratica duplica quello per le relazioni...

		// Prendi il massimo degli id delle vigenze. Usiamo il vettore preso
		// dalla ListTextField
		// in modo tale da considerare anche le nuove vigenze inserite.
		Vector eventiVect = eventi_listtextfield.getListElements();
		for (int i = 0; i < eventiVect.size(); i++) {
			Evento e = (Evento) eventiVect.elementAt(i);
			if (e.getId() != null) {
				try {
					String s = e.getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(e.getId().substring(prefix.length()));
						if (idValue.intValue() > max) {
							max = idValue.intValue();
						}
					}
				} catch (IndexOutOfBoundsException exc) {
				}
			}
		}

		uID += (max + 1);
		return uID;
	}
	

	
	/**
	 * Restituisce un ID univoco per una nuova relazione.
	 */
	private String calcolaIDRelazione(String nomeTag) {

		String prefix = "r";

		if (nomeTag.equals("attiva")) {
			prefix = "ra";
		} else if (nomeTag.equals("passiva")) {
			prefix = "rp";
		} else if (nomeTag.equals("originale")) {
			prefix = "ro";
		} else if (nomeTag.equals("giurisprudenza")) {
			prefix = "rg";
		} else if (nomeTag.equals("haallegato")) {
			prefix = "haa";
		}else if (nomeTag.equals("allegatodi")) {
			prefix = "all";
		}

		

		String uID = prefix;
		int max = 0;

		// TODO questo codice duplica quello di MetaDescrittoriFormImpl!!!
		// TODO sarebbe anche da implementare in maniera un po' pi? elegante...
		// TODO e spostare nelle utils

		
		// Usiamo il vettore preso dalla ListTextField in modo tale da
		// considerare
		// anche le nuove vigenze inserite.
		
		Vector eventiVect = eventi_listtextfield.getListElements();
		for (int i = 0; i < eventiVect.size(); i++) {
			Evento e = (Evento) eventiVect.elementAt(i);
			
			if (e.getFonte() != null) {
				try {
					String s = e.getFonte().getId().substring(0, prefix.length());
					if (s.equals(prefix)) {
						Integer idValue = Integer.decode(e.getFonte().getId().substring(prefix.length()));
						if (idValue.intValue() > max) {
							max = idValue.intValue();
						}
					}
				} catch (IndexOutOfBoundsException exc) {
				}
			}
		}
	
		uID += (max + 1);
		
		return uID;
	}
	

	public Evento getSelectedEvento() {

		return (Evento)eventi_listtextfield.getSelectedItem();
	}

	public void setRel_totali(Relazione[] rel_totali) {
		this.rel_totali = rel_totali;
	}

	public Relazione[] getRelazioniTotalefromCdvEf() {
		Vector vec = eventi_listtextfield.getListElements();
		Evento[] ev_ins = new Evento[vec.size()];
		vec.toArray(ev_ins);
    	
		Vector relVect = new Vector();
		boolean duplicated;
		if(rel_totali!=null){
			for (int i = 0; i < rel_totali.length; i++) {
				duplicated = false;
				
				for(int j=0; j<relVect.size();j++){
			        if(((Relazione)relVect.get(j)).getId().equalsIgnoreCase(rel_totali[i].getId()))
			        	duplicated = true;
				}
				if(!duplicated)
					relVect.add(rel_totali[i]);		
			}
		}
		for (int i = 0; i < ev_ins.length; i++) {
			relVect.add(ev_ins[i].getFonte());
		}

		Relazione[] newRelazioni = new Relazione[relVect.size()];
		relVect.copyInto(newRelazioni);
		return newRelazioni;
	}

	public void setEventiOnVigenze(String[] eventiOnVigenze, VigenzaEntity[] vigenze) {
		this.eventiOnVigenze = eventiOnVigenze;
		this.vigenze=vigenze;
	}

	
	public VigenzaEntity[] getVigToUpdate() {
		return vigToUpdate;
	}
	
	/**
	 * Restituisce le urn presenti nel documento
	 * 
	 * @param doc
	 * @return urn del documento
	 */
	private Urn getUrnFromDocument(Document doc) {

		Urn urnDoc=null;
		
		NodeList urn = doc.getElementsByTagName("urn");
		if(urn!=null && urn.getLength()>0)
			try {
				urnDoc = new Urn(UtilDom.getAttributeValueAsString(urn.item(0),"value"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return urnDoc;
	}
	
	





}

	
