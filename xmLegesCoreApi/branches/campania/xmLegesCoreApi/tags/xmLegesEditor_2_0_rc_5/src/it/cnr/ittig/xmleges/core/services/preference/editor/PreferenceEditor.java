package it.cnr.ittig.xmleges.core.services.preference.editor;

import it.cnr.ittig.services.manager.Service;

/**
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @version 1.0
 */
public interface PreferenceEditor extends Service {

	/**
	 * Aggiunge un pannello di modifica per una specifica sezione delle
	 * preferenze.
	 * 
	 * @param editor pannello di modifica
	 */
	public void addEditor(PreferenceEditorPanel editor);

	/**
	 * Visualizza il pannello di modifica delle preferenze.
	 */
	public void showPreferenceEditor();

}
