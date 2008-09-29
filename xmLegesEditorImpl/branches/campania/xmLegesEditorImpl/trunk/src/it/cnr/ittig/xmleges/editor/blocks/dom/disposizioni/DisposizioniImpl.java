package it.cnr.ittig.xmleges.editor.blocks.dom.disposizioni;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dom.extracttext.ExtractText;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive.DispAttiveFormImpl;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.partizioni.Partizioni;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni</code>.</h1>
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
 */
public class DisposizioniImpl implements Disposizioni, Loggable, Serviceable {
	Logger logger;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;
	
	UtilRulesManager utilRulesManager;
	
	ExtractText extractText;
	
	Rinumerazione rinumerazione;
		
	Node selectedNode;
	
	String selectedText;
	
	NirUtilDom nirUtilDom;

	Partizioni partizioni;
	
	SelectionManager selectionManager;

	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		extractText = (ExtractText) serviceManager.lookup(ExtractText.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		partizioni = (Partizioni) serviceManager.lookup(Partizioni.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}
	
	private boolean setUrn(Evento eventoOriginale, Evento eventoVigore) {
		
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,null);
		Node descrittoriNode = UtilDom.findRecursiveChild(activeMeta,"descrittori");
		Node[] urnNode = UtilDom.getElementsByTagName(doc, descrittoriNode, "urn");
		String urnOriginale = "";
		String versione = "@";
		if (urnNode.length>0) {
			//controllo la urn originale
			urnOriginale = UtilDom.getAttributeValueAsString(urnNode[0], "valore");
			//Se non � ancora settato inizio vigore lo imposto all'evento originale
			if (UtilDom.getAttributeValueAsString(urnNode[0], "iniziovigore")==null)
				UtilDom.setAttributeValue(urnNode[0], "iniziovigore", eventoOriginale.getId());
		}
		try {
			versione += eventoVigore.getFonte().toString().split(":")[4].replaceAll("-", "");
			if (versione.indexOf(';')!=-1)
					versione = versione.split(";")[0];
		}
		catch (Exception e) {
			logger.error("Errore inserimento urn versione");	
		}
		String urnVersione = urnOriginale+versione;
		boolean inserisci = true;
		for (int i=1; i<urnNode.length; i++)
			if (urnVersione.equals(UtilDom.getAttributeValueAsString(urnNode[i], "valore")))
				inserisci = false;
		
		if (inserisci) {
			//imposto all'ultima urn il finevigore
			UtilDom.setAttributeValue(urnNode[urnNode.length -1], "finevigore", eventoVigore.getId());
			//inserisco una nuova urn con iniziovigore
			Node nuovaUrn = utilRulesManager.getNodeTemplate(doc,"urn");
			UtilDom.setAttributeValue(nuovaUrn, "valore", urnVersione);
			UtilDom.setAttributeValue(nuovaUrn, "iniziovigore", eventoVigore.getId());
			descrittoriNode.appendChild(nuovaUrn);
		}
				
		return true;
	}
	
	private Node getMetaAfter(Node modifichepassive, String idNovella, String idNovellando) {

		String cercoId = (!"".equals(idNovella) ? idNovella : idNovellando);
		Document doc = documentManager.getDocumentAsDom();
		Node[] nodi = UtilDom.getElementsByAttributeValue(doc,doc.getDocumentElement(),"iniziovigore",null);
		for (int i=0; i<nodi.length; i++)
			if (cercoId.equals(UtilDom.getAttributeValueAsString(nodi[i], "id"))) {
				//cerco (i-1) se i!=0 altrimenti cerco i=1 (se esiste)
				String cercaMeta;
				if (i>0)
					cercaMeta = UtilDom.getAttributeValueAsString(nodi[i-1], "id");
				else 
					if (nodi.length>1)
						cercaMeta = UtilDom.getAttributeValueAsString(nodi[1], "id");
					else
						return null;
				NodeList disposizioni = modifichepassive.getChildNodes();
				Node dispCorrente;
				Node test;
				for (int j=0; j<disposizioni.getLength(); j++) {
					dispCorrente = disposizioni.item(j);
					test = UtilDom.findDirectChild(dispCorrente, "dsp:novella");
					if (test!=null)
						if (cercaMeta.equals(UtilDom.getAttributeValueAsString(test.getFirstChild(), "xlink:href")))
								return dispCorrente.getNextSibling();
					test = UtilDom.findDirectChild(dispCorrente, "dsp:novellando"); 
					if (test!=null)
						if (cercaMeta.equals(UtilDom.getAttributeValueAsString(test.getFirstChild(), "xlink:href")))
								return dispCorrente.getNextSibling();						
				}
			}
		return null;
	}
	
	public boolean setDOMDisposizioni(String pos, String norma, String partizione, String novellando, String novella, String preNota, String autoNota, String postNota, boolean implicita, Evento eventoOriginale, Evento eventoVigore) {

		Document doc = documentManager.getDocumentAsDom();

		Node activeMeta = nirUtilDom.findActiveMeta(doc,null);

		//inserisco la disposizione
		Node disposizioniNode = UtilDom.findRecursiveChild(activeMeta,"disposizioni");
		if (disposizioniNode==null)
			disposizioniNode = nirUtilDom.checkAndCreateMeta(doc,activeMeta,"disposizioni");
			
		Node modifichepassiveNode = UtilDom.findRecursiveChild(disposizioniNode,"modifichepassive");
		if (modifichepassiveNode==null)
			modifichepassiveNode = UtilDom.checkAndCreate(disposizioniNode, "modifichepassive");		
		
		Node metaAfter = getMetaAfter(modifichepassiveNode,novella, novellando);
		Node operazioneNode;
		if (!novellando.equals("") && !novella.equals("")) {	//sostituzione
			operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:sostituzione");
			if (implicita)
				UtilDom.setAttributeValue(operazioneNode, "implicita", "si");
			else
				UtilDom.setAttributeValue(operazioneNode, "implicita", "no");				
			
			//modifichepassiveNode.appendChild(operazioneNode);
			modifichepassiveNode.insertBefore(operazioneNode,metaAfter);
			
			setNorma(operazioneNode, pos, norma, partizione, preNota, autoNota, postNota);
			setNovella(operazioneNode, novella);
			setNovellando(operazioneNode, novellando);			
		}
		else if (!novellando.equals("")) {	//abrogazione
				operazioneNode = utilRulesManager.getNodeTemplate(doc, "dsp:abrogazione");
				if (implicita)
					UtilDom.setAttributeValue(operazioneNode, "implicita", "si");
				else
					UtilDom.setAttributeValue(operazioneNode, "implicita", "no");
				
				//modifichepassiveNode.appendChild(operazioneNode);
				modifichepassiveNode.insertBefore(operazioneNode,metaAfter);
		
				setNorma(operazioneNode, pos, norma, partizione, preNota, autoNota, postNota);
				setNovellando(operazioneNode, novellando);
			}
			else if (!novella.equals("")) {	//integrazione
				operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:integrazione");
				if (implicita)
					UtilDom.setAttributeValue(operazioneNode, "implicita", "si");
				else
					UtilDom.setAttributeValue(operazioneNode, "implicita", "no");
				
				//modifichepassiveNode.appendChild(operazioneNode);
				modifichepassiveNode.insertBefore(operazioneNode,metaAfter);
		
				setNorma(operazioneNode, pos, norma, partizione, preNota, autoNota, postNota);
				setNovella(operazioneNode, novella);
			}
		//aggiorno le urn
		setUrn(eventoOriginale, eventoVigore);
		//marco il documento come multivigente
		setTipoDocVigenza();
		rinumerazione.aggiorna(doc);
		
		return true;
	}

	private void setNorma(Node n, String pos, String norma, String partizione, String preNota, String autoNota, String postNota) {
		/*	
		 *  Voglio ottenere:
		 *  
		 *  <dsp:pos xlink:href="pos" xlink:type="simple"/>
		 * 	<dsp:norma xlink:href="norma" xlink:type="simple"/>	
		 * 		<dsp:pos xlink:href="partizione" xlink:type="simple"/>
		 * 
		 * 				Inserisco anche la nota qui !!!!
		 * 
		 *  </dsp:norma>
		 *  
		 */
			
		Node posNode = UtilDom.findDirectChild(n, "dsp:pos");
		if (posNode == null) {// Non � stato inserito dal template minimale
			posNode = utilRulesManager.getNodeTemplate("dsp:pos");
			n.appendChild(posNode);
		}	
		UtilDom.setAttributeValue(posNode, "xlink:href", pos);
		
		Node normaNode = UtilDom.findDirectChild(n, "dsp:norma");
		if (normaNode == null) {// Non � stato inserito dal template minimale
			normaNode = utilRulesManager.getNodeTemplate("dsp:norma");
			n.appendChild(normaNode);
		}	
		UtilDom.setAttributeValue(normaNode, "xlink:href", norma);
		Node normaposNode = utilRulesManager.getNodeTemplate("dsp:pos");
		UtilDom.setAttributeValue(normaposNode, "xlink:href", partizione);
		normaNode.appendChild(normaposNode);
		
		//Inserisco anche la nota qui !!!!
		//cambio tutto!!
//		Node metaittigNode = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.it/provvedimenti/2.2", "ittig:meta");
//		normaNode.appendChild(metaittigNode);
//		Node notaittigNode = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.it/provvedimenti/2.2", "ittig:nota");
//		metaittigNode.appendChild(notaittigNode);
		Node notaittigNode = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:notavigenza");
		normaNode.appendChild(notaittigNode);
		
		
		//UtilDom.setIdAttribute(notaittigNode, "itt" + documentManager.getDocumentAsDom().getElementsByTagName("ittig:notavigenza").getLength());
		UtilDom.setIdAttribute(notaittigNode, "");
		
		UtilDom.setAttributeValue(notaittigNode, "auto", autoNota);
		
	}
		
	private void setNovellando(Node n, String novellando) {
		/*	
		 *  Voglio ottenere:
		 *  
		 * 	<dsp:novellando xlink:href="norma" xlink:type="simple"/>	
		 * 		<dsp:pos xlink:href="novellando" xlink:type="simple"/>
		 *  </dsp:norma>
		 *  
		 */
			
		Node novellandoNode = UtilDom.findDirectChild(n, "dsp:novellando");
		if (novellandoNode == null) {// Non � stato inserito dal template minimale
			novellandoNode = utilRulesManager.getNodeTemplate("dsp:novellando");
			n.appendChild(novellandoNode);
		}	
		Node posNode = utilRulesManager.getNodeTemplate("dsp:pos");
		UtilDom.setAttributeValue(posNode, "xlink:href", novellando);
		novellandoNode.appendChild(posNode);

	}
	
	private void setNovella(Node n, String novella) {
		/*	
		 *  Voglio ottenere:
		 *  
		 * 	<dsp:novella xlink:href="norma" xlink:type="simple"/>	
		 * 		<dsp:pos xlink:href="novella" xlink:type="simple"/>
		 *  </dsp:norma>
		 *  
		 */
			
		Node novellaNode = UtilDom.findDirectChild(n, "dsp:novella");
		if (novellaNode == null) {// Non � stato inserito dal template minimale
			novellaNode = utilRulesManager.getNodeTemplate("dsp:novella");
			n.appendChild(novellaNode);
		}	
		Node posNode = utilRulesManager.getNodeTemplate("dsp:pos");
		UtilDom.setAttributeValue(posNode, "xlink:href", novella);
		novellaNode.appendChild(posNode);
		
	}
	
	public void makeNotaVigenza(Node node) {			//non usata... buttare
			
		int numeroNota = 1+documentManager.getDocumentAsDom().getElementsByTagName("ittig:notavigenza").getLength();
		Element ndr = documentManager.getDocumentAsDom().createElement("ndr");
		UtilDom.setAttributeValue(ndr, "num", "itt"+numeroNota);
		UtilDom.setAttributeValue(ndr, "valore", ""+numeroNota);
		UtilDom.setTextNode(ndr, "{" + numeroNota + "}");
		if (node.getNodeName().equals("h:span"))
			node.appendChild(ndr);
		else
			UtilDom.findRecursiveChild(node,"num").appendChild(ndr);
	}
	
	
	public Node makePartition(Node container, boolean prima, VigenzaEntity vigenza) {
			
		Node n;		
		if (nirUtilDom.isContainer(container)) 
			n = partizioni.getPartizioneTemplate(container.getNodeName());			
		else
			n= utilRulesManager.getNodeTemplate(container.getNodeName());	
		try {
			if (prima)
				n = container.getParentNode().insertBefore(n, container);
			else
				n = container.getParentNode().insertBefore(n, container.getNextSibling());
			n = setVigenza(n, "", -1, -1, vigenza);
		}
		catch (DOMException e) {
			logger.error("Errore inserimento nuova partizione ( " + container.getLocalName() + " )");
		}
		
		
		//questo inseriva una ndr come notaDIvigenza
		//makeNotaVigenza(n);

		return n;
	}
	
	public Node makeSpan(Node node, int posizione, VigenzaEntity vigenza, String testo) {

		Node span=null;
		if (posizione < 0) 
			if (node.getNextSibling()==null) {
				span = node.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(documentManager.getDocumentAsDom().getDocumentElement(), "h"), "h:span");
				node.getParentNode().appendChild(span);
			}
			else
				span = utilRulesManager.encloseTextInTag(node.getNextSibling(), 0, 0,"h:span","h");
		else
			span = utilRulesManager.encloseTextInTag(node, posizione, posizione,"h:span","h");
		if (!testo.trim().equals(""))
			UtilDom.setTextNode(span,testo);
		span = setVigenza(span, "", -1, -1, vigenza);
		
		
		//questo inseriva una ndr come notaDIvigenza
		//makeNotaVigenza(span);

		return span;

	}

	public Node setVigenza(Node node, String selectedText, int start, int end, VigenzaEntity vigenza) {
		Node ret = null;
		
		if ((ret=setDOMVigenza(node, selectedText, start, end, vigenza))!=null)
				rinumerazione.aggiorna(documentManager.getDocumentAsDom());
		
		return ret;
	}
	
	public Node setDOMVigenza(Node node, String selectedText, int start, int end, VigenzaEntity vigenza) {
		
		
		selectedNode=node;
		
		if(node==null) 
			return null;	
		
		if(node.getNodeValue()==null){
			if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
				//caso di selzione solo su nodo generici (articolato, formulainiziale, formulafinale ecc..)
				selectedText=node.getNodeName();
			else
				//caso di selezione su nodo paragrafo o sottoscrivente o visto (perche'?)
				selectedText=UtilDom.getTextNode(node);
		}
//		else				
//			selectedText=node.getNodeValue();

		if(UtilDom.isTextNode(node)){
			// solo nodi finali di testo (con matitina verde)
			Element span;
			if(node.getNodeValue().equals(selectedText) || (start==-1 && end==-1)){
				//il testo selezionato coincide con tutto il nodo
				span = (Element) node.getParentNode();
				selectedText=node.getNodeValue();
			}
			else{        // racchiude il testo in uno span e lo riestrae ????
            // il testo selezionato � una sottoparte del nodo di testo (va creato lo span)				
				
				//qui crea uno span dal testo selezionato 
				span = (Element) utilRulesManager.encloseTextInTag(node, start, end,"h:span","h");
				if(vigenza.getEInizioVigore()==null && vigenza.getEFineVigore()==null && vigenza.getStatus()==null){
					Node padre=span.getParentNode();
					//	appiattisce lo span
					extractText.extractTextDOM(node,0,selectedText.length());
					padre.removeChild(span);
					UtilDom.mergeTextNodes(padre);
					return padre;
				}
				
			}
			
//			 Assegnazione attributi di vigenza allo span creato
			if(vigenza.getEInizioVigore()!=null){
				UtilDom.setAttributeValue(span,"iniziovigore",vigenza.getEInizioVigore().getId());
				if(vigenza.getStatus()!=null && !vigenza.getStatus().equals("--"))
					span.setAttribute("status", vigenza.getStatus());
			}
			else{//inizio obbligatorio, se non presente si elimina la vigenza e si esce	
				
				Node padre=span.getParentNode();
				//	appiattisce lo span
				extractText.extractTextDOM(node,0,selectedText.length());
				padre.removeChild(span);
				UtilDom.mergeTextNodes(padre);
				return padre;
			}
			
			if(vigenza.getEFineVigore()!=null)
				UtilDom.setAttributeValue(span,"finevigore",vigenza.getEFineVigore().getId());				
			else{
				try{
					if(!dtdRulesManager.queryIsRequiredAttribute(span.getNodeName(),"finevigore"))
					    span.removeAttribute("finevigore");
					else 
						UtilDom.setAttributeValue(span,"finevigore","");
				}
				catch(DtdRulesManagerException ex){}
			}	

		    return span;
		
		}else{   
			//non � un nodo di testo
		    NamedNodeMap nnm = node.getAttributes();
		    // Assegnazione attributi di vigenza al nodo
			if(vigenza.getEInizioVigore()!=null)
				UtilDom.setAttributeValue(node, "iniziovigore", vigenza.getEInizioVigore().getId());
			else{						
				if(nnm.getNamedItem("iniziovigore")!=null)
					nnm.removeNamedItem("iniziovigore");
				if(nnm.getNamedItem("finevigore")!=null)
					nnm.removeNamedItem("finevigore");
				if(nnm.getNamedItem("status")!=null)
					nnm.removeNamedItem("status");
				return node;
			}
			if(vigenza.getEFineVigore()!=null)
				UtilDom.setAttributeValue(node, "finevigore", vigenza.getEFineVigore().getId());
			else{						
				if(nnm.getNamedItem("finevigore")!=null)
					nnm.removeNamedItem("finevigore");
			}	
			if(vigenza.getStatus()!=null)
					UtilDom.setAttributeValue(node, "status", vigenza.getStatus());
			else{
				if(nnm.getNamedItem("status")!=null)
					nnm.removeNamedItem("status");
			}

			return node;		
		}
		
	}

	
	public void doUndo(String id, boolean cancellaTesto) {
			
		Document doc = documentManager.getDocumentAsDom();
		Element undo =null;
		if (id!=null) 
			undo = doc.getElementById(id);
		if (undo==null) {
			logger.error("fallito undo perch� non trovo id: " + id);
		}
		else {
			Node selezione = undo.getParentNode();
			try {				
				if (!cancellaTesto && undo.getFirstChild().getNodeValue()!=null)
					extractText.extractTextDOM(undo.getFirstChild(),0,undo.getFirstChild().getNodeValue().length());
				selezione.removeChild(undo);
				UtilDom.mergeTextNodes(selezione);		
				selectionManager.setSelectedText(this, selezione, 0, 0);				
			} catch (Exception e) {
				logger.error("fallito undo su id: " + id);
			}	
		}

	}

	public void doUndo(String id, String iniziovigore, String finevigore, String status) {
			
		Document doc = documentManager.getDocumentAsDom();
		Element undo =null;
		if (id!=null) 
			undo = doc.getElementById(id);
		if (undo==null) {
			logger.error("fallito undo perch� non trovo id: " + id);
		}
		else {
			try {
				//ripristino solo gli attributi pre-esistenti
				if(iniziovigore!=null && !iniziovigore.equals(""))	
					undo.setAttribute("iniziovigore", iniziovigore);
				else
					undo.removeAttribute("iniziovigore");
				if (finevigore!=null && !finevigore.equals(""))
					undo.setAttribute("finevigore", finevigore);
				else
					undo.removeAttribute("finevigore");
				if (status!=null && !status.equals(""))
					undo.setAttribute("status", status);
				else
					undo.removeAttribute("status");
				selectionManager.setSelectedText(this, undo, 0, 0);
			} catch (Exception e) {
				logger.error("fallito undo su id: " + id);
			}	
		}
	}
	
	public void doChange(String norma, String pos, Node disposizione, String autonota, boolean implicita, Node novellando, String status, String idEvento, String idNovella) {		
		
		EditTransaction t = null;
		try {
			t = documentManager.beginEdit();
			
		Document doc = documentManager.getDocumentAsDom();


		if (implicita && UtilDom.getAttributeValueAsString(disposizione, "implicita").equalsIgnoreCase("no"))
			UtilDom.setAttributeValue(disposizione, "implicita","si");
		else
			if (!implicita && UtilDom.getAttributeValueAsString(disposizione, "implicita").equalsIgnoreCase("si"))
				UtilDom.setAttributeValue(disposizione, "implicita","no");
		Node modifica = UtilDom.getElementsByTagName(doc, disposizione, "dsp:norma")[0];
		if (!UtilDom.getAttributeValueAsString(modifica, "xlink:href").equals(norma)) {
			UtilDom.setAttributeValue(modifica, "xlink:href", norma);
			//cambio di ID  (per ora no)


			//aggiorno attributi inizio/finevigore
			if (disposizione.getNodeName().equals("dsp:abrogazione")) {
				UtilDom.setAttributeValue(novellando, "finevigore", idEvento);
			}
			else if (disposizione.getNodeName().equals("dsp:integrazione")) {
					Node novella = doc.getElementById(idNovella);
					if (novella!=null)
						UtilDom.setAttributeValue(novella, "iniziovigore", idEvento);
				 }
			     else if (disposizione.getNodeName().equals("dsp:sostituzione")) {
			    	 	UtilDom.setAttributeValue(novellando, "finevigore", idEvento);
						Node novella = doc.getElementById(idNovella);
						if (novella!=null)
							UtilDom.setAttributeValue(novella, "iniziovigore", idEvento);
			     	  }
		}
		modifica = UtilDom.getElementsByTagName(doc, modifica, "dsp:pos")[0]; 
		if (!UtilDom.getAttributeValueAsString(modifica, "xlink:href").equals(pos))
			UtilDom.setAttributeValue(modifica, "xlink:href", norma+"#"+pos);		
		modifica = UtilDom.getElementsByTagName(doc, modifica.getParentNode(), "ittig:notavigenza")[0];
		if (!UtilDom.getAttributeValueAsString(modifica, "auto").equals(autonota))
			UtilDom.setAttributeValue(modifica, "auto", autonota);
		if (UtilDom.getAttributeValueAsString(novellando, "status")!=null)
			if (!UtilDom.getAttributeValueAsString(novellando, "status").equals(status))
				UtilDom.setAttributeValue(novellando, "status", status);		
		
		documentManager.commitEdit(t);
		} catch (Exception ex) {
			documentManager.rollbackEdit(t);
		}
	}
	
	public Node doErase(String idNovellando, String idNovella, Node disposizione, Node novellando) {
			
		EditTransaction t = null;
		try {
			t = documentManager.beginEdit();
			
		if (disposizione.getNodeName().equals("dsp:abrogazione"))
			if (!"".equals(idNovellando)) {
				String iniziovigore = UtilDom.getAttributeValueAsString(novellando, "iniziovigore");
				if (novellando.getNodeName().equals("h:span"))
					if (iniziovigore==null || "t1".equals(iniziovigore) )
						doUndo(idNovellando, false);		
					else {
						((Element) novellando).removeAttribute("finevigore");
						((Element) novellando).removeAttribute("status");
					}				
				else 
					doUndo(idNovellando, ("t1".equals(iniziovigore) ? null : iniziovigore), null, null);
			}	
		
		if (disposizione.getNodeName().equals("dsp:integrazione"))	
			if (!"".equals(idNovella)) 
				doUndo(idNovella, true);
	
		
		if (disposizione.getNodeName().equals("dsp:sostituzione")) {	
			if (!"".equals(idNovellando)) {
				String iniziovigore = UtilDom.getAttributeValueAsString(novellando, "iniziovigore");
				if (novellando.getNodeName().equals("h:span"))
					if (iniziovigore==null || "t1".equals(iniziovigore) )
						doUndo(idNovellando, false);
					else {
						((Element) novellando).removeAttribute("finevigore");
						((Element) novellando).removeAttribute("status");
					}	
				else 
					doUndo(idNovellando, ("t1".equals(iniziovigore) ? null : iniziovigore), null, null);
				
			}
			if (!"".equals(idNovella))
				doUndo(idNovella, true);	
		}
		
		int numeroNotaEliminata = Integer.parseInt(UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(disposizione, "ittig:notavigenza"), "id").substring(3))-1;
		disposizione.getParentNode().removeChild(disposizione);
	
		NodeList noteMeta = documentManager.getDocumentAsDom().getElementsByTagName("ittig:notavigenza");
		for(int i=0; i<noteMeta.getLength();i++) {
			int numero = Integer.parseInt(UtilDom.getAttributeValueAsString(noteMeta.item(i), "id").substring(3))-1;
			if (numero>numeroNotaEliminata)
				UtilDom.setAttributeValue(noteMeta.item(i), "id", "itt"+numero);
		}
		
		documentManager.commitEdit(t);
		} catch (Exception ex) {
			documentManager.rollbackEdit(t);
		}
		
		return novellando;
	}
	
	public VigenzaEntity getVigenza(Node node, int start, int end) {

	Document doc = documentManager.getDocumentAsDom();
	
	if(node==null)
		return null;
	
	if(UtilDom.isTextNode(node) && (node.getParentNode().getAttributes()== null || node.getParentNode().getAttributes().getNamedItem("iniziovigore")==null) )
		return null;
	
	if(!UtilDom.isTextNode(node) && (node.getAttributes()==null || node.getAttributes().getNamedItem("iniziovigore")==null))
		return null;
	
	Evento e_iniziovig=null;
	Evento e_finevig=null;
	Node iniziovig=null;
	Node finevig=null;
	Node status=null;
	selectedText="";
	
	if(!UtilDom.isTextNode(node)){
		//non c'� selezione di testo sono su nodo generico
		if(node.getNodeValue()==null){
			if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
				//	caso di selzione solo su nodo generici (articolato, formulainiziale, formulafinale ecc..)
				selectedText=node.getNodeName();
			else
				// caso di selezione su nodo paragrafo o sottoscrivente o visto (perche'?)
				selectedText=UtilDom.getTextNode(node);
		}
		else				
			selectedText=node.getNodeValue();
		
	}
	//else	siamo su nodo di testo (matita verde) 
	else {

		Node parentNode=node.getParentNode();
		
	   // Recupero contenuto Nodo
		selectedText=UtilDom.getTextNode(parentNode);

		//se il testo selezionato non coincide con quello dello span di cui � figlio
		//si crea una nuova vigenza		
		if(start!=end && selectedText.substring(start,end).length()<selectedText.length()){
				selectedText=selectedText.substring(start,end);
				return null;
		}
		
		
		
		node=parentNode;

	}
				
	
		iniziovig=node.getAttributes().getNamedItem("iniziovigore");
		finevig=node.getAttributes().getNamedItem("finevigore");
		status=node.getAttributes().getNamedItem("status");
	

		Element evento_ie_Tag = (iniziovig==null?null:doc.getElementById(iniziovig.getNodeValue()));
		Element evento_fe_Tag = (finevig==null?null:doc.getElementById(finevig.getNodeValue()));
		
		if(evento_ie_Tag==null){
			//evento inizio non presente quindi vigenza vuota
			return null;
		}
		
		//	qui rappresenta la fonte dell'evento di inizio
		Element evento_fonte_Tag = (evento_ie_Tag==null?null:doc.getElementById(evento_ie_Tag.getAttribute("fonte")));
		
		String tag, id, link, effetto, tipo;
		Relazione rel = null;

		if (evento_fonte_Tag!=null && evento_fonte_Tag.getNodeType() == Node.ELEMENT_NODE) {
			tag = evento_fonte_Tag.getNodeName();
			id = evento_fonte_Tag.getAttributes().getNamedItem("id") != null ? evento_fonte_Tag.getAttributes().getNamedItem("id").getNodeValue() : null;
			link = evento_fonte_Tag.getAttributes().getNamedItem("xlink:href") != null ? evento_fonte_Tag.getAttributes().getNamedItem("xlink:href")
					.getNodeValue() : null;
		    if(tag.equals("giurisprudenza")){
		    	effetto=evento_fonte_Tag.getAttributes().getNamedItem("effetto") != null ? evento_fonte_Tag.getAttributes().getNamedItem("effetto").getNodeValue() : null;
			    rel=new Relazione(tag, id, link,effetto);
		    }else if(tag.equals("haallegato") || tag.equals("allegatodi")){
		    	tipo=evento_fonte_Tag.getAttributes().getNamedItem("tipo") != null ? evento_fonte_Tag.getAttributes().getNamedItem("tipo").getNodeValue() : null;
			    rel=new Relazione(tag, id, link,tipo);
		    }else 						    	
		    	rel=new Relazione(tag, id, link);
		}
		

		e_iniziovig=iniziovig==null?null:new Evento(iniziovig!=null?iniziovig.getNodeValue():"",
				evento_ie_Tag!=null?evento_ie_Tag.getAttribute("data"):null,//				
						rel,
						evento_ie_Tag!=null?evento_ie_Tag.getAttribute("tipo"):null);
				
	
		//		qui rappresenta la fonte dell'evento di fine
		evento_fonte_Tag = evento_fe_Tag==null?null:doc.getElementById(evento_fe_Tag.getAttribute("fonte"));
		rel=null;
			
		if (evento_fonte_Tag!=null && evento_fonte_Tag.getNodeType() == Node.ELEMENT_NODE) {
			tag = evento_fonte_Tag.getNodeName();
			id = evento_fonte_Tag.getAttributes().getNamedItem("id") != null ? evento_fonte_Tag.getAttributes().getNamedItem("id").getNodeValue() : null;
			link = evento_fonte_Tag.getAttributes().getNamedItem("xlink:href") != null ? evento_fonte_Tag.getAttributes().getNamedItem("xlink:href")
					.getNodeValue() : null;
		    if(tag.equals("giurisprudenza")){
		    	effetto=evento_fonte_Tag.getAttributes().getNamedItem("effetto") != null ? evento_fonte_Tag.getAttributes().getNamedItem("effetto").getNodeValue() : null;
			    rel=new Relazione(tag, id, link,effetto);
		    }else if(tag.equals("haallegato")||tag.equals("allegatodi")){
		    	tipo=evento_fonte_Tag.getAttributes().getNamedItem("tipo") != null ? evento_fonte_Tag.getAttributes().getNamedItem("tipo").getNodeValue() : null;
			    rel=new Relazione(tag, id, link,tipo);
		    }else
		    	rel=new Relazione(tag, id, link);
		}
		e_finevig=(finevig==null)?null:new Evento(finevig!=null?finevig.getNodeValue():"",
				evento_fe_Tag!=null?evento_fe_Tag.getAttribute("data"):null,
				rel,
				evento_fe_Tag!=null?evento_fe_Tag.getAttribute("tipo"):null);
	
	
	if(e_iniziovig==null && e_finevig==null) return null;
	
	return new VigenzaEntity(node, e_iniziovig,e_finevig, status!=null?status.getNodeValue():null,selectedText);
	
	}

	public boolean isVigente() {
		Document doc = documentManager.getDocumentAsDom();
		
		NodeList lista = doc.getElementsByTagName("*");
		
		for(int i=0; i<lista.getLength();i++){
			if(lista.item(i).getAttributes().getNamedItem("iniziovigore")!=null){
				return true;
			}
//			if(lista.item(i).getAttributes().getNamedItem("finevigore")!=null){
//				return true;
//			}
		}	
		return false;	
	}

	// T: aggiunto il controllo di non riaggiornare il tipo se e' gia' settato: risparmia molto tempo sull'update dei pannelli
	public void setTipoDocVigenza() {
		Document doc = documentManager.getDocumentAsDom();
		if (isVigente()){
			if(!UtilDom.getAttributeValueAsString(doc.getDocumentElement(),"tipo").equalsIgnoreCase("multivigente"))
				UtilDom.setAttributeValue(doc.getDocumentElement(),"tipo","multivigente");
		}else{
			if(!UtilDom.getAttributeValueAsString(doc.getDocumentElement(),"tipo").equalsIgnoreCase("originale"))
				UtilDom.setAttributeValue(doc.getDocumentElement(),"tipo","originale");	
		}
	}
	
	
	/*
	 *   PARTE DISPOSIZIONI ATTIVE. 
	 */
	
	public Node setDOMDispAttive(Node metaDaModificare, String idMod, int operazioneIniziale, String completa, boolean condizione, String decorrenza, String idevento, String norma, String partizione, String[] delimitatori) {
				
		Document doc = documentManager.getDocumentAsDom();		
		Node activeMeta = nirUtilDom.findActiveMeta(doc,null);
		Node disposizioniNode = UtilDom.findRecursiveChild(activeMeta,"disposizioni");
		if (disposizioniNode==null)
			disposizioniNode = nirUtilDom.checkAndCreateMeta(doc,activeMeta,"disposizioni");
			
		Node modificheattiveNode = UtilDom.findRecursiveChild(disposizioniNode,"modificheattive");
		if (modificheattiveNode==null)
			modificheattiveNode = UtilDom.checkAndCreate(disposizioniNode, "modificheattive");		

		//se � una modifica, butto via il vecchio pacchetto.
		
		
		//////////////buttare anche eventi collegati !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		if (metaDaModificare!=null)
			modificheattiveNode.removeChild(metaDaModificare);
		
		Node operazioneNode=null;
		if (operazioneIniziale==DispAttiveFormImpl.SOSTITUZIONE)
			operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:sostituzione");
		else if (operazioneIniziale==DispAttiveFormImpl.INTEGRAZIONE)
			operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:integrazione");
		else if (operazioneIniziale==DispAttiveFormImpl.ABROGAZIONE)
			operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:abrogazione");
		UtilDom.setAttributeValue(operazioneNode, "completa", completa);
		UtilDom.setAttributeValue(operazioneNode, "implicita", "si");	//imposto sempre a SI
		modificheattiveNode.appendChild(operazioneNode);
		//Pos -- indica il mod.
		Node nodo = UtilDom.findDirectChild(operazioneNode, "dsp:pos");
		if (nodo != null) // � stato inserito dal template minimale
			operazioneNode.removeChild(nodo);
		nodo = utilRulesManager.getNodeTemplate("dsp:pos");
		UtilDom.setAttributeValue(nodo, "xlink:href", idMod);
		operazioneNode.appendChild(nodo);			
		UtilDom.setAttributeValue(nodo, "xlink:href", idMod);
		operazioneNode.appendChild(nodo);
		//Se � condizionata -- metto la condizione, altrimenti il termine
		if (condizione) {
			nodo = utilRulesManager.getNodeTemplate("dsp:condizione");
			Node nodoCondizione = utilRulesManager.getNodeTemplate("dsp:testo");
			UtilDom.setAttributeValue(nodoCondizione, "valore", decorrenza);
			nodo.appendChild(nodoCondizione);
		}
		else {
			if (idevento==null) {  //devo creare il nuovo evento (e relazione)
				//relazione
				Node relazioniNode = UtilDom.findRecursiveChild(activeMeta,"relazioni");
				NodeList relazioniList = relazioniNode.getChildNodes();
				int max=0;
				for (int i = 0; i < relazioniList.getLength(); i++) {
					Node relazioneNode = relazioniList.item(i);
					if ("attiva".equals(relazioneNode.getNodeName())) {
						String id = UtilDom.getAttributeValueAsString(relazioneNode, "id");
						Integer idValue = Integer.decode(id.substring(2));
						if (idValue.intValue() > max)
							max = idValue.intValue();
					}
				}
				max++;
				Node nuovo = utilRulesManager.getNodeTemplate("attiva");
				UtilDom.setAttributeValue(nuovo, "id", "ra"+max);
				UtilDom.setAttributeValue(nuovo, "xlink:href", norma);
				//UtilDom.setAttributeValue(nuovo, "xlink:type", "simple");
				relazioniNode.appendChild(nuovo);
				//evento
				Node eventiNode = UtilDom.findRecursiveChild(activeMeta,"eventi");
				idevento = "t" + (1 + eventiNode.getChildNodes().getLength());
				nuovo = utilRulesManager.getNodeTemplate("evento");
				UtilDom.setAttributeValue(nuovo, "data", decorrenza.substring(6, 10)+decorrenza.substring(3, 5)+decorrenza.substring(0, 2));   //???????
				UtilDom.setAttributeValue(nuovo, "fonte", "ra"+max);
				UtilDom.setAttributeValue(nuovo, "tipo", "modifica");
				UtilDom.setIdAttribute(nuovo, idevento);
				eventiNode.appendChild(nuovo);
			}
			nodo = utilRulesManager.getNodeTemplate("dsp:termine");
			UtilDom.setAttributeValue(nodo, "da", "#"+idevento);
		}
		operazioneNode.appendChild(nodo);
		//Norma -- Pos con eventuali bordi
		Node normaNode = UtilDom.findDirectChild(operazioneNode, "dsp:norma");
		if (normaNode != null) // � stato inserito dal template minimale
			operazioneNode.removeChild(normaNode);
		normaNode = utilRulesManager.getNodeTemplate("dsp:norma");
		UtilDom.setAttributeValue(normaNode, "xlink:href", norma);
		operazioneNode.appendChild(normaNode);
		//inserisco il pos della Norma
		nodo = utilRulesManager.getNodeTemplate("dsp:pos");
		UtilDom.setAttributeValue(nodo, "xlink:href", norma+"#"+partizione);
		normaNode.appendChild(nodo);
		//inserisco il bordo della Norma
		Node delimitatoreNode = null;
		for (int i=0; i<delimitatori.length/3; i++) {
			if (i==0) {	//inserisco dsp:subarg
				Node subargNode = utilRulesManager.getNodeTemplate("dsp:subarg");
				normaNode.appendChild(subargNode);
				delimitatoreNode = subargNode;
			}
			nodo = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:bordo");
			UtilDom.setAttributeValue(nodo, "tipo", delimitatori[i*3]);
			UtilDom.setAttributeValue(nodo, "num", delimitatori[i*3+1]);
			if ("ordinale".equalsIgnoreCase(delimitatori[i*3+2]))
				UtilDom.setAttributeValue(nodo, "ordinale", "si");
			else
				UtilDom.setAttributeValue(nodo, "ordinale", "no");
			delimitatoreNode.appendChild(nodo);
			delimitatoreNode = nodo;
		}
		modificheattiveNode.appendChild(operazioneNode);
		return operazioneNode;
	}

	private void setPosizione(Node posizioneNode, String posizione, String virgoletta) {
		Node posNode = null;
		
		if (!posizione.equals("inizio") | !posizione.equals("fine")) {	//aggiungo il POS in Posizione
			posNode = utilRulesManager.getNodeTemplate("dsp:pos");
			UtilDom.setAttributeValue(posNode, "xlink:href", virgoletta);
			posizioneNode.appendChild(posNode);
		}		
		Node nodo = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:dove");
		UtilDom.setAttributeValue(nodo, "valore", posizione);
		if (posNode==null)
			posizioneNode.appendChild(nodo);
		else
			posNode.appendChild(nodo);
	}
	
	public void setDOMNovellaDispAttive(Node meta, String virgolettaContenuto, String tipo, String posizione, String virgolettaA, String virgolettaB) {
		//Posizione
		if (posizione!=null) {
			Node posizioneNode = utilRulesManager.getNodeTemplate("dsp:posizione");
			if (posizione.equals("fra")) {
				setPosizione(posizioneNode, "fra-prima", "#"+virgolettaA);
				setPosizione(posizioneNode, "fra-dopo", "#"+virgolettaB);
			}
			else
				setPosizione(posizioneNode, posizione, "#"+virgolettaA);	
			meta.appendChild(posizioneNode);	
		}
		//Novella
		Node novellaNode = utilRulesManager.getNodeTemplate("dsp:novella");
		Node nodo = utilRulesManager.getNodeTemplate("dsp:pos");
		UtilDom.setAttributeValue(nodo, "xlink:href", "#"+virgolettaContenuto);
		novellaNode.appendChild(nodo); 
		Node subargNode = utilRulesManager.getNodeTemplate("dsp:subarg");
		nodo = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:tipo");
		UtilDom.setAttributeValue(nodo, "valore", tipo);
		subargNode.appendChild(nodo);
		novellaNode.appendChild(subargNode);
		meta.appendChild(novellaNode);
	}	
	
	public void setDOMNovellandoDispAttive(Node meta, boolean parole, String tipoPartizione, String tipo, String ruoloA, String virgolettaA, String ruoloB, String virgolettaB) {
		//Novellando
		Node novellandoNode = utilRulesManager.getNodeTemplate("dsp:novellando");
		Node nodo;
		if (parole) {
			nodo = utilRulesManager.getNodeTemplate("dsp:pos");
			if (tipo.equals("contenuto")) {
				UtilDom.setAttributeValue(nodo, "xlink:href", "#"+virgolettaA);
				novellandoNode.appendChild(nodo);
				if (ruoloB!=null) {
					nodo = utilRulesManager.getNodeTemplate("dsp:pos");
					UtilDom.setAttributeValue(nodo, "xlink:href", "#"+virgolettaB);
					Node ruoloNode = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:ruolo");
					UtilDom.setAttributeValue(ruoloNode, "valore", ruoloB);
					nodo.appendChild(ruoloNode);
					novellandoNode.appendChild(nodo);
				}
			}
			else {		//delimitatori  (stesso di sopra ma ho anche un ruolo(A)... sopra � il default 'contenuto'
				UtilDom.setAttributeValue(nodo, "xlink:href", "#"+virgolettaA);
				Node ruoloNode = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:ruolo");
				UtilDom.setAttributeValue(ruoloNode, "valore", ruoloA);
				nodo.appendChild(ruoloNode);
				novellandoNode.appendChild(nodo);
				if (ruoloB!=null) {
					nodo = utilRulesManager.getNodeTemplate("dsp:pos");
					UtilDom.setAttributeValue(nodo, "xlink:href", "#"+virgolettaB);
					ruoloNode = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:ruolo");
					UtilDom.setAttributeValue(ruoloNode, "valore", ruoloB);
					nodo.appendChild(ruoloNode);
					novellandoNode.appendChild(nodo);
				}
			}
		}
		Node subargNode = utilRulesManager.getNodeTemplate("dsp:subarg");
		nodo = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:tipo");
		UtilDom.setAttributeValue(nodo, "valore", tipoPartizione);
		subargNode.appendChild(nodo);
		novellandoNode.appendChild(subargNode);
		meta.appendChild(novellandoNode);
	}
}
