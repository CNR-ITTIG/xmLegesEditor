package it.cnr.ittig.xmleges.editor.blocks.dom.disposizioni;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dom.extracttext.ExtractText;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
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
	
	public boolean setDOMDisposizioni(String pos, String norma, String partizione, String novellando, String novella, String preNota, String autoNota, String postNota, boolean implicita) {
		Document doc = documentManager.getDocumentAsDom();
		
		Node activeMeta = nirUtilDom.findActiveMeta(doc,null);

		//inserisco la disposizione
		Node disposizioniNode = UtilDom.findRecursiveChild(activeMeta,"disposizioni");
		if (disposizioniNode==null)
			disposizioniNode = nirUtilDom.checkAndCreateMeta(doc,activeMeta,"disposizioni");
			
		Node modifichepassiveNode = UtilDom.findRecursiveChild(disposizioniNode,"modifichepassive");
		if (modifichepassiveNode==null)
			modifichepassiveNode = UtilDom.checkAndCreate(disposizioniNode, "modifichepassive");		

		Node operazioneNode;
		if (!novellando.equals("") && !novella.equals("")) {	//sostituzione
			operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:sostituzione");
			if (implicita)
				UtilDom.setAttributeValue(operazioneNode, "implicita", "si");
			else
				UtilDom.setAttributeValue(operazioneNode, "implicita", "no");				
			modifichepassiveNode.appendChild(operazioneNode);
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
				modifichepassiveNode.appendChild(operazioneNode);
				setNorma(operazioneNode, pos, norma, partizione, preNota, autoNota, postNota);
				setNovellando(operazioneNode, novellando);
			}
			else if (!novella.equals("")) {	//integrazione
				operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:integrazione");
				if (implicita)
					UtilDom.setAttributeValue(operazioneNode, "implicita", "si");
				else
					UtilDom.setAttributeValue(operazioneNode, "implicita", "no");
				modifichepassiveNode.appendChild(operazioneNode);
				setNorma(operazioneNode, pos, norma, partizione, preNota, autoNota, postNota);
				setNovella(operazioneNode, novella);
			}
		
		//marco il documento come multivigente
		setTipoDocVigenza();
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
		Node notaittigNode = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.it/provvedimenti/2.2", "ittig:notavigenza");
		normaNode.appendChild(notaittigNode);
		
		UtilDom.setIdAttribute(notaittigNode, "itt" + documentManager.getDocumentAsDom().getElementsByTagName("ittig:notavigenza").getLength());
		
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
	
	public void makeNotaVigenza(Node node) {
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
			n =partizioni.nuovaPartizione(container, container.getNodeName());
		else
			n= utilRulesManager.getNodeTemplate(container.getNodeName());	
		try {
			if (prima)
				n = container.getParentNode().insertBefore(n, container);
			else
				if (container.getParentNode().getLastChild()==container)
					n = container.getParentNode().appendChild(n);
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
		try {
			EditTransaction tr = documentManager.beginEdit();
			if ((ret=setDOMVigenza(node, selectedText, start, end, vigenza))!=null) {
				rinumerazione.aggiorna(documentManager.getDocumentAsDom());
				documentManager.commitEdit(tr);
				
				
				//makeNotaVigenza(ret);
				
				
			} else
				documentManager.rollbackEdit(tr);
		} catch (DocumentManagerException ex) {
			logger.error(ex.toString() + " DocumentManagerException in SetVigenza");
			return null;
		}
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
			EditTransaction tr;
			Node selezione = undo.getParentNode();
			try {
				tr = documentManager.beginEdit();
				
				if (!cancellaTesto && undo.getFirstChild().getNodeValue()!=null)
					extractText.extractTextDOM(undo.getFirstChild(),0,undo.getFirstChild().getNodeValue().length());
				selezione.removeChild(undo);
				UtilDom.mergeTextNodes(selezione);		
				documentManager.commitEdit(tr);
				selectionManager.setSelectedText(this, selezione, 0, 0);				
			} catch (DocumentManagerException e) {
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
			EditTransaction tr;
			try {
				tr = documentManager.beginEdit();
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
				documentManager.commitEdit(tr);
				selectionManager.setSelectedText(this, undo, 0, 0);
			} catch (DocumentManagerException e) {
				logger.error("fallito undo su id: " + id);
			}	
		}
	}
	
	public void doChange(String norma, String pos, Node disposizione, String autonota, boolean implicita, Node novellando, String status) {		
		
		
		Document doc = documentManager.getDocumentAsDom();

		
		if (implicita && UtilDom.getAttributeValueAsString(disposizione, "implicita").equalsIgnoreCase("no"))
			UtilDom.setAttributeValue(disposizione, "implicita","si");
		else
			if (!implicita && UtilDom.getAttributeValueAsString(disposizione, "implicita").equalsIgnoreCase("si"))
				UtilDom.setAttributeValue(disposizione, "implicita","no");
		Node modifica = UtilDom.getElementsByTagName(doc, disposizione, "dsp:norma")[0];
		if (!UtilDom.getAttributeValueAsString(modifica, "xlink:href").equals(norma))
			UtilDom.setAttributeValue(modifica, "xlink:href", norma);
		modifica = UtilDom.getElementsByTagName(doc, modifica, "dsp:pos")[0]; 
		if (!UtilDom.getAttributeValueAsString(modifica, "xlink:href").equals(pos))
			UtilDom.setAttributeValue(modifica, "xlink:href", norma+"#"+pos);		
		modifica = UtilDom.getElementsByTagName(doc, modifica.getParentNode(), "ittig:notavigenza")[0];
		if (!UtilDom.getAttributeValueAsString(modifica, "auto").equals(autonota))
			UtilDom.setAttributeValue(modifica, "auto", autonota);
		if (UtilDom.getAttributeValueAsString(novellando, "status")!=null)
			if (!UtilDom.getAttributeValueAsString(novellando, "status").equals(status))
				UtilDom.setAttributeValue(novellando, "status", status);
	}
	
	public void doErase(String idNovellando, String idNovella, Node disposizione, Node novellando) {

		if (disposizione.getNodeName().equals("dsp:abrogazione"))
			if (!"".equals(idNovellando))
				doUndo(idNovellando, false);	
		if (disposizione.getNodeName().equals("dsp:integrazione"))	
			if (!"".equals(idNovella))
				doUndo(idNovella, true);
		if (disposizione.getNodeName().equals("dsp:sostituzione")) {	
			if (!"".equals(idNovellando))
				if (novellando.getNodeName().equals("h:span"))
					doUndo(idNovellando, false);
				else
					doUndo(idNovellando, null, null, null);
			if (!"".equals(idNovella))
				doUndo(idNovella, true);	
			Node ndr = UtilDom.findRecursiveChild(novellando,"ndr");
			if (ndr!=null)
				ndr.getParentNode().removeChild(ndr);
		}
		
		int numeroNotaEliminata = Integer.parseInt(UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(disposizione, "ittig:notavigenza"), "id").substring(3))-1;
		disposizione.getParentNode().removeChild(disposizione);
	
		NodeList noteMeta = documentManager.getDocumentAsDom().getElementsByTagName("ittig:notavigenza");
		for(int i=0; i<noteMeta.getLength();i++) {
			int numero = Integer.parseInt(UtilDom.getAttributeValueAsString(noteMeta.item(i), "id").substring(3))-1;
			if (numero>numeroNotaEliminata)
				UtilDom.setAttributeValue(noteMeta.item(i), "id", "itt"+numero);
		}
	}
	
	
	
	//	non uso nulla da qui in poi (controllare)
	
	public boolean canSetVigenza(Node node) {
		if (node != null && node.getParentNode() != null) {
			try {
				return (node.getNodeName()!=null && 
						(dtdRulesManager.queryIsValidAttribute(node.getNodeName(), "iniziovigore")
								|| UtilDom.isTextNode(node)) 
						);
			} catch (DtdRulesManagerException e) {
				return UtilDom.isTextNode(node);
			}
		}
		return false;
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
	
}//metodo

	public String getSelectedText() {
		return selectedText;
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
	
	public void updateVigenzaOnDoc(VigenzaEntity vig){
		
		Node node=vig.getOnNode();
		if(node==null)
			return;
			try{
				NamedNodeMap nnm = node.getAttributes();
				if(nnm!=null){
					EditTransaction tr = documentManager.beginEdit();
					
					//se esisteva gi� l'evento inizio sul dom si aggiorna al nuovo o si elimina se il nuovo � null
					if(nnm.getNamedItem("iniziovigore")!=null){
						if(vig.getEInizioVigore()!=null){
//							UtilDom.setAttributeValue(node, "iniziovigore", vig.getEInizioVigore().getId());
						}

						else
							nnm.removeNamedItem("iniziovigore");	

					}
	//				se esisteva gi� l'evento fine sul dom si aggiorna al nuovo o si elimina se il nuovo � null	
					if(nnm.getNamedItem("finevigore")!=null){
						if(vig.getEFineVigore()!=null){
//							UtilDom.setAttributeValue(node, "finevigore", vig.getEFineVigore().getId());
						}

						else
							nnm.removeNamedItem("finevigore");

					}
					if(nnm.getNamedItem("iniziovigore")==null && nnm.getNamedItem("finevigore")==null){
						if(nnm.getNamedItem("status")!=null)
							nnm.removeNamedItem("status");
						if(node.getNodeName().equals("h:span")){
//							appiattisce lo span
							Node padre=node.getParentNode();				
							extractText.extractTextDOM(node.getFirstChild(),0,UtilDom.getText(node.getFirstChild()).length());
							padre.removeChild(node);
							UtilDom.mergeTextNodes(padre);
						}
				
					}
					documentManager.commitEdit(tr);	
				}
				
			}catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return;
			}

		
	}
	
	public boolean setDOMDispAttive(String pos, String norma, String partizione, String novellando, String novella, String autoNota, boolean implicita) {
		Document doc = documentManager.getDocumentAsDom();
		
		Node activeMeta = nirUtilDom.findActiveMeta(doc,null);

		//inserisco la disposizione
		Node disposizioniNode = UtilDom.findRecursiveChild(activeMeta,"disposizioni");
		if (disposizioniNode==null)
			disposizioniNode = nirUtilDom.checkAndCreateMeta(doc,activeMeta,"disposizioni");
			
		Node modificheattiveNode = UtilDom.findRecursiveChild(disposizioniNode,"modificheattive");
		if (modificheattiveNode==null)
			modificheattiveNode = UtilDom.checkAndCreate(disposizioniNode, "modificheattive");		


		Node operazioneNode;
		if (!novellando.equals("") && !novella.equals("")) {	//sostituzione
			operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:sostituzione");
			if (implicita)
				UtilDom.setAttributeValue(operazioneNode, "implicita", "si");
			else
				UtilDom.setAttributeValue(operazioneNode, "implicita", "no");				
			modificheattiveNode.appendChild(operazioneNode);
			setNorma(operazioneNode, pos, norma, partizione, "", autoNota, "");
			setNovella(operazioneNode, novella);
			setNovellando(operazioneNode, novellando);			
		}
		else if (!novellando.equals("")) {	//abrogazione
				operazioneNode = utilRulesManager.getNodeTemplate(doc, "dsp:abrogazione");
				if (implicita)
					UtilDom.setAttributeValue(operazioneNode, "implicita", "si");
				else
					UtilDom.setAttributeValue(operazioneNode, "implicita", "no");
				modificheattiveNode.appendChild(operazioneNode);
				setNorma(operazioneNode, pos, norma, partizione, "", autoNota, "");
				setNovellando(operazioneNode, novellando);
			}
			else if (!novella.equals("")) {	//integrazione
				operazioneNode = utilRulesManager.getNodeTemplate(doc,"dsp:integrazione");
				if (implicita)
					UtilDom.setAttributeValue(operazioneNode, "implicita", "si");
				else
					UtilDom.setAttributeValue(operazioneNode, "implicita", "no");
				modificheattiveNode.appendChild(operazioneNode);
				setNorma(operazioneNode, pos, norma, partizione, "", autoNota, "");
				setNovella(operazioneNode, novella);
			}		
		return true;
	}

}
