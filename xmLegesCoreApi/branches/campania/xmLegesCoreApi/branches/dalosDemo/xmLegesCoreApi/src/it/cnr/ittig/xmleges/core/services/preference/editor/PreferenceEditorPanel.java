package it.cnr.ittig.xmleges.core.services.preference.editor;

import java.awt.Component;

/**
 * Interfaccia necessaria per registrare un pannello per le modifiche delle
 * preferenze specifiche di una sezione del file di configurazione<br>
 * Il pannello che implementa questa interfaccia pu&ograve;  specificare il
 * gruppo di appartenenza tramite il metodo
 * <code>getPreferenceEditorGroup</code>. Cos&igrave; facendo l'albero di
 * riepilogo mostrer&agrave; le varie sezioni modificabili raggruppate.<br>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface PreferenceEditorPanel {

	/**
	 * Restituisce il nome del gruppo di appartenenza. Pu&ograve; contenere il
	 * carattere di separazione "/" che permette di specificare gruppi con
	 * livelli di profondit&agrave; maggiore di 1 (uno).
	 * 
	 * @return nome del gruppo
	 */
	public String getPrefereceEditorGroup();

	/**
	 * Restituisce il nome della sezione.
	 * 
	 * @return nome della sezione
	 */
	public String getPrefereceEditorSection();

	/**
	 * Restituisce il componente che verr&agrave; usato per modificare la
	 * sezione delle preferenze indicata dal metodo
	 * <code>getPreferenceEditorSection</code>.
	 * 
	 * @return componente grafico per la modifica della sezione
	 */
	public Component getPreferenceEditorPanel();

	/**
	 * Metodo che indica di salvare le preferenze apportate al pannello ottenuto
	 * con il metodo <code>getPreferenceEditorPanel</code>.
	 */
	public void savePreference();

}
