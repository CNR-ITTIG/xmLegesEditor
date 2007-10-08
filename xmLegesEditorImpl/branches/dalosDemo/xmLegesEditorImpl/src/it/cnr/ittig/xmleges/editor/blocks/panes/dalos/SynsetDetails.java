package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;


import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class SynsetDetails extends JEditorPane {
	
	Synset synset = null;
	I18n i18n;
	
	public SynsetDetails() {
		
		super();
		
		setEditable(false);
		setContentType("text/html");
		
		HTMLEditorKit kit = (HTMLEditorKit) this.getEditorKit();
		HTMLDocument doc = (HTMLDocument) this.getDocument();

		try {
			URL url = new URL("file:////" );//+ EditorConf.DATA_DIR + "/");
			doc.setBase(url);
			//System.out.println("URL: " + url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	public void draw(Concetto c) {
//
//		String definizione = c.getDefinizione();
//		String datains = UtilEditor.getHumanDateFormat(c.getDataIns());
//		String datamod = c.getDataMod();		
//		if(datamod != null && !datamod.equals("")) {
//			datamod = "<br> Data ultima modifica: " + datamod;
//		} else {
//			//Non mostrare data ultima modifica se non � mai stato modificato
//			datamod = "";
//		}
//		
//		//Usa una sola immagine
//		//String img = "<img src=\"" + EditorConf.JWS_URL + "img/kontact_journal.png\">";
//		String img = "<img src=\"./" + EditorConf.SYNSET_GENERIC_IMG + "\">";
//		if(c.ontoclassi.size() > 0) {
//			img = "<img src=\"./" + EditorConf.SYNSET_ONTO_IMG + "\">" +
//			"<pre>" + c.ontoclassi.get(0).getName() + "</pre>";
//		}
//		
//		String html = "<html><body>" + img +
////			"<h3>Data inserimento: " + datains + datamod +
//			"</h3><h2><i>Definizione</i></h2><h3>" + definizione + 
//			"</h3><h2><i>Varianti</i></h2><h3>"; 
//		
//		for(Iterator<Lemma> i = c.lemmi.iterator(); i.hasNext();) {
//			Lemma item = i.next();
//			String lstr = item.toString();
//			html += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + item.getOrdine() + 
//					". " + lstr + "<br>";				
//		}
//		
//		html += "</h3></body></html>";
//
//		//UtilEditor.debug("HTML:\n" + html + "\n");
//		
//		setText(html);		
//	}
	
	
	public void setI18n(I18n i18n){
		this.i18n = i18n;
	}
	
	public void setSynset(Synset synset){
		this.synset = synset;
	}
	
	public void draw() {
		
			String img = "<img src=\"./" + i18n.getIconFor("editor.panes.dalos.synsetlist")+ "\">";
			String html = "<html><body>" +  img +
				"</h3><h2><i>Definizione</i></h2><h3>" + synset.toString() + 
				"</h3><h2><i>Varianti</i></h2><h3>"+synset.getLexicalForm(); 
			
			html += "</h3></body></html>";
	
			setText(html);		
	}
	
	public void clearContent() {
		
		setText("<html><body></body></html>");
	}

}
