package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;


import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.blocks.dalos.kb.KbConf;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Source;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

public class SourceContainer extends JEditorPane implements HyperlinkListener{
	
	I18n i18n;
	UtilDalos utilDalos;
	
	String[] browsers={"firefox", "mozilla-firefox", "cmd /C start"};
	
	
	public SourceContainer() {
		
		super();
		
		setEditable(false);
		setContentType("text/html");
		//setBackground(Color.YELLOW);
		addHyperlinkListener(this);

		
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
	
	
	public void setUtilDalos(UtilDalos utilDalos){
		this.utilDalos = utilDalos;
	}
	
	public void draw(Synset synset) {

		if(synset == null) {
			setText("<html>No match!</html>");
			return;
		}
		
		String html = "<html><body bgcolor=\"#EDE275\">" +
			//"<h2><i>Sources</i></h2><table>"; 
			//"<h2>&nbsp;</h2>" +
			"<table width=\"98%\"><tr><td>&nbsp;</td><td><h2><i>Source Text</i></h2></td>" +
			"<td><h2><i>Links</i></h2></td></tr>";
		
		String evenBgColor = "#FFF380";
		String oddBgColor =  "#EDE275";
		String bgColor = evenBgColor;
		int count = 0;

		for(Iterator i = synset.getSources().iterator(); i.hasNext();) {
			
			Source source = (Source) i.next();
			String def = source.getContent();
			
			bgColor = count++ % 2==0?evenBgColor:oddBgColor;
			
	
			if(def == null || def.trim().length() < 1) {
				def = "Source text not available";
			}else{
				def = utilDalos.highlightDef(def, synset);
			}
			
			String dcode = source.getDocumentId();
			
			String pcode = source.getPartitionId();
			//Throw away partition code
			pcode = pcode.substring(dcode.length());
			//Throw away lang code
			dcode = dcode.substring(0, dcode.length() - 3);
			
			//System.out.println("d:" + source.getDocumentId() + " p:" + source.getPartitionId() + " dcode:" + dcode + " pcode:" + pcode);

			String linkBase = "http://turing.ittig.cnr.it/jwn/corpus/";

			html += "<tr bgcolor="+bgColor+"><td><img src=\"./signature.png\"></td><td><font face=\"Arial\">" + 
						def + "</font></td><td align=\"center\">";
						
			String languages[] = {"EN", "ES", "IT", "NL" };			
			for(int k = 0; k < languages.length; k++) {
				String lang = languages[k];
				if(k != 0) {
					html += "<br>";
				}
				String langdcode = dcode + "-" + lang.toLowerCase();
				String langpcode = langdcode + pcode;
				html += "<a href=\"" + linkBase + lang + "/" + langdcode + "/" + langpcode + ".html" + "\">" + lang + "</a>";							
			}												
			html += "</td></tr>";
		}
		
		html += "</table></body></html>";

		setText(html);
		getCaret().setDot(0);

	}
	
	
	
	
	public void clearContent() {
		
		setText("<html><body></body></html>");
	}


	public void hyperlinkUpdate(HyperlinkEvent e) {
		HyperlinkEvent.EventType type = e.getEventType();
		if (type == HyperlinkEvent.EventType.ACTIVATED) {
			for (int i = 0; i < browsers.length; i++)
				try {
					String cmd = browsers[i] + " " +e.getURL();
					Runtime.getRuntime().exec(cmd);
					break;
				} catch (Exception ex) {
				}
		}
	}

}
