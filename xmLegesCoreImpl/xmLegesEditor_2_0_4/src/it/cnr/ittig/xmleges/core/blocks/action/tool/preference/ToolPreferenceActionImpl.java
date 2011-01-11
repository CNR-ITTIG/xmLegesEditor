package it.cnr.ittig.xmleges.core.blocks.action.tool.preference;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.tool.preference.ToolPreferenceAction;
import it.cnr.ittig.xmleges.core.services.preference.editor.PreferenceEditor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Implementazione del servizio
 * it.cnr.ittig.xmleges.editor.services.action.tool.preference.ToolPreferenceAction. Questa
 * implementazione registra l'azione <code>tool.preference</code> nell'ActionManager.
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ToolPreferenceActionImpl extends AbstractAction implements ToolPreferenceAction, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	PreferenceEditor preferenceEditor;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		preferenceEditor = (PreferenceEditor) serviceManager.lookup(PreferenceEditor.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("tool.preference", this);
	}

	// ////////////////////////////////////////// ToolPreferenceAction Interface
	public void doEditPreference() {
		preferenceEditor.showPreferenceEditor();
	}

	// ////////////////////////////////////////////////// AbstractAction Extends
	public void actionPerformed(ActionEvent e) {
		doEditPreference();
	}

}
