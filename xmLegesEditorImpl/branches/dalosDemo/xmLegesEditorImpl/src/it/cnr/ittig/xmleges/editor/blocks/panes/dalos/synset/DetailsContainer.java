package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;


import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.blocks.dalos.kb.KbConf;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.PivotOntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Source;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.TreeOntoClass;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;

public class DetailsContainer extends JEditorPane {
	
	I18n i18n;
	
	public DetailsContainer() {
		
		super();
		
		setEditable(false);
		setContentType("text/html");
		
		HTMLDocument doc = (HTMLDocument) this.getDocument();

		try {
			File file = UtilFile.getFileFromTemp(KbConf.dalosRepository);
			URL url = new URL("file:////" + file.getAbsolutePath() + "/");
			doc.setBase(url);
			//System.out.println("BASE URL IN DETAILS CONTAINER: " + url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setI18n(I18n i18n){
		this.i18n = i18n;
	}
	
	public void draw(Synset synset) {
		
		//System.out.println("Drawing synset: " + synset);
		if(synset == null) {
			setText("<html>No match!</html>");
			return;
		}
		
		String img = "<img src=\"./kontact_journal.png\">";
		
		//String def = synset.getDef();
		
		String html = "<html><body>";
		
		html += "<table align=\"center\"><tr><td>" + img + "</td><td><h1>" + synset +  "</h1></td></tr></table>";
		
		//html += "<p><h1>" + synset + "</h1></p>";
		
		Collection defs = synset.getDefinitions();
		
		if(defs.size() > 0) {
//			html += "<table><tr><td>" + img +
//			"</td><td><h2><i>Definition</i></h2><h3>" + 
//			def +  "</h3></td></tr></table>";
			
			//html += "<table><tr><td>&nbsp;</td><td><h2><i>Definition</i></h2></td></tr>";
			html += "<h2><i>Definition</i></h2><table>";
			for(Iterator i = defs.iterator(); i.hasNext(); ) {
				Source source = (Source) i.next();
				html += "<tr><td><img src=\"./signature.png\"></td><td><font face=\"Arial\">" + 
						source.getContent() + "</font></td><td>&nbsp;</td><td>" +
						"<a href=\"" + source.getLink() + 
						"\">" + source.getPartitionId() + "</a></td></tr>";
			}
			html += "</table>";
		}
				
		html += "<h2><i>Variants</i></h2><table>"; 
		
		for(Iterator i = synset.getVariants().iterator(); i.hasNext();) {
			String variant = (String) i.next();
			html += "<tr><td><img src=\"./lexical.png\"></td><td>" +
					variant + "</td></tr>";
		}
		
		PivotOntoClass poc = synset.getPivotClass();
		if(poc != null) {			
			Collection links = poc.getLinks();
			if(links.size() > 0) {
				html += "</table><h2><i>Semantic Paths</i></h2><table>";
				for(Iterator i = links.iterator(); i.hasNext();) {
					TreeOntoClass toc = (TreeOntoClass) i.next();
					if(toc != null) {
						Collection paths = toc.getSemanticPaths();
						if(paths != null) {
							for(Iterator p = paths.iterator(); p.hasNext();) {
								String path = (String) p.next();
								html += "<tr><td><img src=\"./treeopen.png\"></td><td>" +
									path + "</td></tr>";
							}
						}
//						html += "<tr><td><img src=\"./treeopen.png\"></td><td>" +
//						toc.getName() + "</td></tr>";
						
					}
				}				
			}
		}
		
		html += "</table></body></html>";

		setText(html);
		getCaret().setDot(0);
	}

	public void clearContent() {
		
		setText("<html><body></body></html>");
	}

}
