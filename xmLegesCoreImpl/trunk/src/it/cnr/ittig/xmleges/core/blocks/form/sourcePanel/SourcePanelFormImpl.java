package it.cnr.ittig.xmleges.core.blocks.form.sourcePanel;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.blocks.panes.source.SourceTextPane;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.sourcePanel.SourcePanelForm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.bounce.text.xml.XMLEditorKit;
import org.bounce.text.xml.XMLFoldingMargin;
import org.bounce.text.xml.XMLStyleConstants;

public class SourcePanelFormImpl implements SourcePanelForm, Loggable,Serviceable, Initializable {
	
	Logger logger;

	//   SourcePanelForm.jfrm
	Form form;
	
	JPanel panel = new JPanel(new BorderLayout());
	
	// pannello per la gestione
	SourceTextPane textPane = new SourceTextPane(true);
		
	public boolean openForm() {
		form.setSize(450, 600);
		form.showDialog();
		return form.isOk();

	}

	public void enableLogging(Logger logger) {
		this.logger = logger;

	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);		
	
	}

	public void initialize() throws Exception {
		
		form.setMainComponent(getClass().getResourceAsStream("SourcePanelForm.jfrm"));
		form.setName("editor.form.sourcepane.riepilogo");
		form.setHelpKey("help.contents.form.sourcepane");
		form.setSize(850, 300);
		form.setDialogResizable(true);
						
		textPane.setEditable(true);		       
		XMLEditorKit kit = new XMLEditorKit();
		textPane.setEditorKit(kit);
		 // Enable auto indentation.
        kit.setAutoIndentation(true);
        // Enable tag completion.
        kit.setTagCompletion(true); 
        // Enable error highlighting.
        textPane.getDocument().putProperty(XMLEditorKit.ERROR_HIGHLIGHTING_ATTRIBUTE, new Boolean(true));
        // Set a style
        kit.setStyle(XMLStyleConstants.ATTRIBUTE_NAME, new Color(255, 0, 0), Font.BOLD);        
		
		JPanel subpanel = new JPanel(new BorderLayout());
		subpanel.add(new XMLFoldingMargin(textPane), BorderLayout.WEST);
		subpanel.add(textPane, BorderLayout.CENTER);
		
		JScrollPane scroll = new JScrollPane(subpanel);
				
		panel.add(scroll, BorderLayout.CENTER);

		form.replaceComponent("editor.form.sourcepane", panel);
			
	}



	public void setSourceText(String text) {
		text = text.replaceAll("\r", "");
		textPane.setText(text);	
		// si posiziona in testa
		textPane.getCaret().setDot(0);
	}

	
	
			


}
