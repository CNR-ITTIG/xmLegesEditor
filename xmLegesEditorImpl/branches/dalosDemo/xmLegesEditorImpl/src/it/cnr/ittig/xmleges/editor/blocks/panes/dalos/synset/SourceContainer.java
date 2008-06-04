package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;


import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.blocks.dalos.kb.KbConf;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Source;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

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
	
	public void draw(Synset synset) {

		if(synset == null) {
			setText("<html>No match!</html>");
			return;
		}
		
		String html = "<html><body bgcolor=\"#EDE275\">" +
			"</h3><h2><i>Sources</i></h2><table>"; 
		
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
				def = highlightDef(def, synset);
			}
			
			// SETTARE LA URL
			source.setLink("http://www.dalosproject.eu");
			
			html += "<tr bgcolor="+bgColor+"><td><img src=\"./signature.png\"></td><td><font face=\"Arial\">" + 
						def + "</font></td><td>&nbsp;</td><td>" +
						"<a href=\"" + source.getLink() + 
						"\">" + source.getPartitionId() + "</a></td></tr>";			
		}
		
		html += "</table></body></html>";

		setText(html);
		getCaret().setDot(0);

	}
	
	
	
	// una sola variante per ogni def ? 
	
	private String highlightDef(String def,Synset syn){
		String highlighted;
		int index;
		List variants = Arrays.asList(syn.getVariants().toArray());
				
		// ordino le varianti dalla piu' lunga alla piu' corta
		Collections.sort(variants,new Comparator() {
		    public int compare(Object a, Object b ) {
		    	  return(((String)b).length()-((String)a).length());
		    }
		});
		
		for(Iterator it = variants.iterator(); it.hasNext();){
			String currentVariant = (String) it.next();
			index = searchString(currentVariant, def);
			//index = def.toLowerCase().indexOf(currentVariant.toLowerCase());
			if(index!=-1){
				highlighted = def.substring(0,index)+"<font style=\"background-color: #FF8040\">"+def.substring(index,index+currentVariant.length())+"</font>"+def.substring(index+currentVariant.length());
				def = highlighted;
			}
		}
		return def;
	}
	
	
	
	// un po' di lacchezzi per ignorare gli spazi
	private int searchString(String match, String text){
		int idx = text.toLowerCase().indexOf(match.toLowerCase());
		if(idx!=-1)
			return idx;
		
		String[] tok = match.split("\\s+");
		int start = text.toLowerCase().indexOf(tok[0].toLowerCase());
		if(start!=-1){
			idx = squeeze(text.substring(start)).toLowerCase().indexOf(squeeze(match).toLowerCase());
			if(idx!=-1)
				idx = start +idx;
		}
		return idx;
	}
	
	
	/**
	 * @param str stringa da comprimere
	 * @return la stringa compressa
	 */
	private String squeeze(String str) {
		char[] buffer = str.toCharArray();
		StringBuffer out = new StringBuffer(buffer.length);

		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] !=' ') {
				out.append(buffer[i]);
			}
		}
		return out.toString();
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
