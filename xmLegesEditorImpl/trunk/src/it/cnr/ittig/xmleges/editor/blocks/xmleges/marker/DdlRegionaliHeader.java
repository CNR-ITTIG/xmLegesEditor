package it.cnr.ittig.xmleges.editor.blocks.xmleges.marker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DdlRegionaliHeader {

	Document dom = null;
	Element art = null;
	Element intestazione = null;
	
	public DdlRegionaliHeader(Document dom, Element art, Element intestazione) {
		
		this.dom = dom;
		this.art = art;
		this.intestazione = intestazione;
	}
	
	public boolean parse(String text) {

		/*
	Disegno di legge regionale n. 72.
	 "Legge finanziaria per l'anno 2011".
	 
	 opp.
	 
	 Proposta di legge regionale n. 6.
	 "Valorizzazione e conservazione dei massi erratici di alto pregio paesaggistico, naturalistico e storico".

		 */
		
		//String linePattern = ".*disegno\\sdi\\slegge\\sregionale\\s+n.\\s+([\\d/]+).*";
		String numPattern = "n.?\\s*([\\d]+[\\\\/]?r?)";
		Pattern pattern = Pattern.compile(numPattern);
		
		boolean disegno = true;
		boolean tipoFound = false;
		String numText = "";
		String titleText = "";
		
		String[] lines = text.split("\\n");
		
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			
			System.out.println(">> LINE: " + line.toLowerCase());
	
			if(line.equals("") || line.toLowerCase().startsWith("error")) {
				continue;
			}

			Matcher matcher = pattern.matcher(line.toLowerCase());
			
			if(numText.equals("") && matcher.find()) {
				System.out.println(">>>> MATCH " + matcher.group(0));
				numText = matcher.group(1).trim();
			}

			if(!tipoFound && line.toLowerCase().startsWith("diseg")) {
				disegno = true;
				tipoFound = true;
				continue;
			}
			
			if(!tipoFound && line.toLowerCase().startsWith("prop")) {
				disegno = false;
				tipoFound = true;
				continue;
			}
			
			titleText += line + " ";
		}
		
		//tipoDoc
		Element tipo = null;
		NodeList nlist = intestazione.getElementsByTagName("tipoDoc");
		if(nlist.getLength() == 0) {
			tipo = dom.createElement("tipoDoc");
			intestazione.appendChild(tipo);
		} else {
			tipo = (Element) nlist.item(0);			
		}
		
		if(disegno) {
			//disegno
			tipo.setTextContent("Disegno di legge regionale");
			art.setAttribute("nome", "disegnoLeggeRegionale"); 

		} else {
			//proposta
			tipo.setTextContent("Proposta di legge regionale");
			art.setAttribute("nome", "propostaLeggeRegionale"); 
		}
		
		//Emanante
		Element ema = null;
		nlist = intestazione.getElementsByTagName("emanante");
		if(nlist.getLength() == 0) {
			ema = dom.createElement("emanante");
			//intestazione.appendChild(ema);
			intestazione.insertBefore(ema, tipo);
		} else {
			ema = (Element) nlist.item(0);
		}
		ema.setTextContent("Consiglio Regionale del Piemonte");

		//Data
		Element data = null;
		nlist = intestazione.getElementsByTagName("dataDoc");
		if(nlist.getLength() == 0) {
			data = dom.createElement("dataDoc");
			intestazione.appendChild(data);
		} else {
			data = (Element) nlist.item(0);
		}
		//TODO DATA? e Normalizzazione con attributo (che solitamente è presente nell'xml originale)
		
		//numDoc
		Element num = null;
		nlist = intestazione.getElementsByTagName("numDoc");
		if(nlist.getLength() == 0) {
			Text txtNode = dom.createTextNode("n.");
			//intestazione.appendChild(txtNode);
			intestazione.insertBefore(txtNode, data);
			num = dom.createElement("numDoc");
			//intestazione.appendChild(num);
			intestazione.insertBefore(num, data);
			
		} else {
			num = (Element) nlist.item(0);
		}
		num.setTextContent(numText);

		//Titolo
		Element title = null;
		nlist = intestazione.getElementsByTagName("titoloDoc");
		if(nlist.getLength() == 0) {
			title = dom.createElement("titoloDoc");
			intestazione.appendChild(title);
		} else {
			title = (Element) nlist.item(0);
		}
		title.setTextContent(titleText);
		

		
		return true;
	}
	
}
