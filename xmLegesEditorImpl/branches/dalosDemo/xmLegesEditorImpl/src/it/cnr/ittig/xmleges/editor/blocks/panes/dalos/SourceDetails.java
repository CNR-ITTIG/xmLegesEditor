package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;


import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.blocks.dalos.kb.KbConf;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Source;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;

public class SourceDetails extends JEditorPane {
	
	Synset synset = null;
	I18n i18n;
	
	public SourceDetails() {
		
		super();
		
		setEditable(false);
		setContentType("text/html");
		
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
	
//	public void draw() {
//
//		if(synset == null) {
//			setText("<html></html>");
//			return;
//		}
//		
//		String html = "<html><body>" +
//			"</h3><h2><i>Sources</i></h2><table>"; 
//		
//		for(Iterator i = synset.getSources().iterator(); i.hasNext();) {
//			Source source = (Source) i.next();
//			String def = source.getContent();
//			if(def == null || def.trim().length() < 1) {
//				def = "Source text not available";
//			}
//			
//			html += "<tr><td><img src=\"./signature.png\"></td><td><i>" + 
//						def + "</i></td><td>&nbsp;</td><td>" +
//						"<A HREF=\"" + source.getLink() + 
//						"\">" + source.getId() + "</A></td></tr>";			
//		}
//		
//		html += "</table></body></html>";
//
//		setText(html);
//		
//		//System.out.println("DEBUG HTML: " + html + "\n");
//	}
	
	public void draw() {

		String html = "<html><body>" +
			"</h3><h2><i>Sources</i></h2><table>"; 
		
	String def = "Both for consumers and suppliers of financial services, the distance marketing of financial services will constitute one of the main tangible results of the completion of the internal market.";
	String source = "32002L0065-20050612-it-pre-rec2";

	html += "<tr><td><img src=\"./signature.png\"></td><td><i>" + 
	def + "</i></td><td>&nbsp;</td><td>" +
	"<A HREF=\"" + source + 
	"\">" + source + "</A></td></tr>";			

		def = "the total price to be paid by the consumer to the supplier for the financial service, including all related fees, charges and expenses, and all taxes paid via the supplier or, when an exact price cannot be indicated, the basis for the calculation of the price enabling the consumer to verify it;";
		source = "32002L0065-20050612-it-art3-par2-pntb";
		
		html += "<tr><td><img src=\"./signature.png\"></td><td><i>" + 
						def + "</i></td><td>&nbsp;</td><td>" +
						"<A HREF=\"" + source + 
						"\">" + source + "</A></td></tr>";			

		def = "At any time during the contractual relationship the consumer is entitled, at his request, to receive the contractual terms and conditions on paper. In addition, the consumer is entitled to change the means of distance communication used, unless this is incompatible with the contract concluded or the nature of the financial service provided.";
		source = "32002L0065-20050612-it-art5-par3";
			
		html += "<tr><td><img src=\"./signature.png\"></td><td><i>" + 
		def + "</i></td><td>&nbsp;</td><td>" +
		"<A HREF=\"" + source + 
		"\">" + source + "</A></td></tr>";			

		html += "</table></body></html>";

		setText(html);
		
		//System.out.println("DEBUG HTML: " + html + "\n");
	}

	public void clearContent() {
		
		setText("<html><body></body></html>");
	}

}
