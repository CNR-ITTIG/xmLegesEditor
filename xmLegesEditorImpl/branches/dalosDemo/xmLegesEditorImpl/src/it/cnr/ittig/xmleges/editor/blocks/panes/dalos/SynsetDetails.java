package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;


import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.blocks.dalos.kb.KbConf;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

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
			File file = UtilFile.getFileFromTemp(KbConf.dalosRepository);
			URL url = new URL("file:////" + file.getAbsolutePath() + "/");
			doc.setBase(url);
			//System.out.println("URL: " + url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setI18n(I18n i18n){
		this.i18n = i18n;
	}
	
	public void setSynset(Synset synset){
		this.synset = synset;
	}
	
	public void draw() {
		
		if(synset == null) {
			setText("<html></html>");
			return;
		}
		
		String img = "<img src=\"./kontact_journal.png\">";
		
		String def = synset.getDef();
		if(def == null) {
			def = "";
		}
		
		String html = "<html><body>" +  img +
			"</h3><h2><i>Definition</i></h2><h3>" + 
			def +  "</h3><h2><i>Variants</i></h2><h3>"; 
		
		for(Iterator i = synset.variants.iterator(); i.hasNext();) {
			String variant = (String) i.next();
			html += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - " + 
						variant + "<br>";				
		}
		
		html += "</h3></body></html>";

		setText(html);
		
		//System.out.println("DEBUG HTML: " + html + "\n");
	}
	
	public void clearContent() {
		
		setText("<html><body></body></html>");
	}

}
