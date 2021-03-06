package it.cnr.ittig.xmleges.core.blocks.form.sourcePanel;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.blocks.panes.source.SourceTextPane;
import it.cnr.ittig.xmleges.core.services.action.edit.findreplace.SourceFindReplaceAction;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.sourcePanel.SourcePanelForm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import org.bounce.text.xml.XMLEditorKit;
import org.bounce.text.xml.XMLFoldingMargin;
import org.bounce.text.xml.XMLStyleConstants;

public class SourcePanelFormImpl implements SourcePanelForm, Loggable,
		Serviceable, Initializable {

	Logger logger;

	// SourcePanelForm.jfrm
	Form form;

	JPanel panel = new JPanel(new BorderLayout());

	// pannello per la gestione
	SourceTextPane textPane = new SourceTextPane(true);

	public SourceTextPane getTextPane() {
		return textPane;
	}

	JPopupMenu popupMenu;

	MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3)
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	};
	
	SourceFindReplaceAction sourceFindReplaceAction;


	public boolean openForm() {
		form.setSize(900, 700);
		form.showDialog();
		return form.isOk();

	}

	public void enableLogging(Logger logger) {
		this.logger = logger;

	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		sourceFindReplaceAction = (SourceFindReplaceAction) serviceManager.lookup(SourceFindReplaceAction.class);
	}

	public void initialize() throws Exception {

		form.setMainComponent(getClass().getResourceAsStream(
				"SourcePanelForm.jfrm"));
		form.setName("editor.form.sourcepane.riepilogo");
		form.setHelpKey("help.contents.form.sourcepane");
		form.setSize(850, 300);
		form.setDialogResizable(true);

		textPane.setEditable(true);

		popupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
		menuItem.setText("Cut");
		menuItem.setMnemonic(KeyEvent.VK_T);
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
		menuItem.setText("Copy");
		menuItem.setMnemonic(KeyEvent.VK_C);
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
		menuItem.setText("Paste");
		menuItem.setMnemonic(KeyEvent.VK_P);
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sourceFindReplaceAction.doFind();
			}
		});
		menuItem.setText("Find and replace..");
		menuItem.setMnemonic(KeyEvent.VK_F);
		popupMenu.add(menuItem);
		textPane.addMouseListener(mouseAdapter);
		InputMap inputMap = textPane.getInputMap();
		KeyStroke keyF = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK);
		inputMap.put(keyF, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sourceFindReplaceAction.doFind();
			}
		});
		
		KeyStroke keyF3 = KeyStroke.getKeyStroke("F3");
		inputMap.put(keyF3, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sourceFindReplaceAction.doFindNext();
			}
		});


		XMLEditorKit kit = new XMLEditorKit();
		textPane.setEditorKit(kit);
		// Enable auto indentation.
		kit.setAutoIndentation(true);
		// Enable tag completion.
		kit.setTagCompletion(true);
		// Enable error highlighting.
		textPane.getDocument().putProperty(
				XMLEditorKit.ERROR_HIGHLIGHTING_ATTRIBUTE, new Boolean(true));
		// Set a style
		kit.setStyle(XMLStyleConstants.ATTRIBUTE_NAME, new Color(255, 0, 0),
				Font.BOLD);

		JPanel subpanel = new JPanel(new BorderLayout());
		subpanel.add(new XMLFoldingMargin(textPane), BorderLayout.WEST);
		subpanel.add(textPane, BorderLayout.CENTER);
		
		JScrollPane scroll = new JScrollPane(subpanel);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		
		panel.add(scroll, BorderLayout.CENTER);
		

		form.replaceComponent("editor.form.sourcepane", panel);
		
		

//		FindDialog dialog = new FindDialog(form.getDialog(), true);
//		dialog.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent event) {
//				System.exit(0);
//			}
//
//			public void windowClosed(WindowEvent event) {
//				System.exit(0);
//			}
//		});
//		dialog.pack();
//		dialog.setVisible(true);

	}

	public void setSourceText(String text) {
		text = text.replaceAll("\r", "");
		textPane.setText(text);
		// si posiziona in testa
		textPane.getCaret().setDot(0);
	}

	public String getSourceText() {
		return textPane.getText();
	}

}
