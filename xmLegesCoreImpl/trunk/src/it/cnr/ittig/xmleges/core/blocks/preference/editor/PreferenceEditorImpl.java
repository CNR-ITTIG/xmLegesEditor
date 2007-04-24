package it.cnr.ittig.xmleges.core.blocks.preference.editor;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.preference.editor.PreferenceEditor;
import it.cnr.ittig.xmleges.core.services.preference.editor.PreferenceEditorPanel;

import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.preference.editor.PreferenceEditor</code>.
 * </h1>
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
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class PreferenceEditorImpl implements PreferenceEditor, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Form form;

	JTree tree;

	DefaultMutableTreeNode root;

	DefaultTreeModel treeModel;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("/forms/PreferenceEditor.jfrm"));
		form.setCustomButtons(new String[] { "preference.manager.editor.close" });
		tree = (JTree) form.getComponentByName("preference.manager.editor.tree");
		root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);
		tree.setModel(treeModel);
	}

	// ////////////////////////////////////////////// PreferenceEditor Interface
	public void addEditor(PreferenceEditorPanel editor) {
		String group = editor.getPrefereceEditorGroup();
		StringTokenizer st = new StringTokenizer(group, "/");
		while (st.hasMoreTokens()) {
			String g = st.nextToken();
			root.add(new DefaultMutableTreeNode(g));
		}
	}

	public void showPreferenceEditor() {
		form.showDialog();
	}

}
