package it.cnr.ittig.xmleges.core.services.i18n;

import it.cnr.ittig.services.manager.Service;

import java.awt.Image;
import java.util.Locale;

import javax.swing.Icon;

/**
 * Servizio per la gestione dell'internazionalizzazione delle stringhe, delle
 * icone, delle immagini e degli oggetti. Ogni elemento &egrave; identificato da
 * un chiave e pu&ograve; essere recuperato tramite i metodi:
 * <code>getTextFor</code>,<code>getIconFor</code>,
 * <code>getImageFor</code>,<code>getObjectFor</code>.<br>
 * Inoltre &egrave; possibile aggiungere una coppia (chiave,valore) ai
 * dizionari. <br>
 * Esempio:
 * 
 * <pre>
 *      ...
 *      JLabel label = new JLabel();
 *      label.setText(i18n.getTextFor(&quot;hello.text&quot;));
 *      label.setIcon(i18n.getIconFor(&quot;hello.icon&quot;));
 *      ... 
 * </pre>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @see it.cnr.ittig.xmleges.core.blocks.i18n.I18nImpl
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface I18n extends Service {

	/**
	 * Imposta la lingua corrente.
	 * 
	 * @param locale lingua
	 */
	public void setLocale(Locale locale);

	/**
	 * Imposta la lingua corrente.
	 * 
	 * @param language codice della lingua
	 */
	public void setLocale(String language);

	/**
	 * Imposta la lingua corrente specificando la lingua e la nazione. <br>
	 * Esempio:
	 * 
	 * <pre>
	 * setLocale(&quot;it&quot;, &quot;IT&quot;);
	 * </pre>
	 * 
	 * @param language codice della lingua
	 * @param country codice della nazione
	 */
	public void setLocale(String language, String country);

	/**
	 * Imposta la lingua corrente specificando la lingua, la nazione e la
	 * variante. <br>
	 * Esempio:
	 * 
	 * <pre>
	 * setLocale(&quot;it&quot;, &quot;IT&quot;, &quot;NIR&quot;)
	 * </pre>
	 * 
	 * @param language codice della lingua
	 * @param country codice della nazione
	 * @param variant variante
	 */
	public void setLocale(String language, String country, String variant);

	/**
	 * Restituisce la lingua corrente.
	 * 
	 * @return lingua corrente
	 */
	public Locale getLocale();

	/**
	 * Indica se &egrave; presente la chiave <code>key</code> nel dizionario
	 * corrente.
	 * 
	 * @param key chiave
	 * @return true se la chiave esiste
	 */
	public boolean hasKey(String key);

	/**
	 * Indica se &egrave; presente la chiave <code>key</code> e il suo
	 * contenuto non &egrave; vuoto.
	 * 
	 * @param key chiave
	 * @return true se esiste e non &egrave; vuota
	 */
	public boolean hasNotEmptyKey(String key);

	/**
	 * Restituisce la stringa identificata dalla chiave <code>key</code>.
	 * 
	 * @param key chiave del testo
	 * @return stringa secondo la lingua
	 */
	public String getTextFor(String key);

	/**
	 * Restituisce l'immagine identificata dalla chiave <code>key</code>.
	 * 
	 * @param key chiave dell'immagine
	 * @return immagine secondo la lingua
	 */
	public Image getImageFor(String key);

	/**
	 * Restituisce l'icona identificata dalla chiave <code>key</code>.
	 * 
	 * @param key chiave dell'icona
	 * @return icona secondo la lingua
	 */
	public Icon getIconFor(String key);

	/**
	 * Restituisce l'oggetto istanziato identificatato dalla chiave
	 * <code>key</code>.
	 * 
	 * @param key chiave dell'oggetto
	 * @return oggetto secondo la lingua
	 * @throws ClassNotFoundException se la classe richiesta non esiste nel
	 *         class loader corrente
	 * @throws InstantiationException se ci sono problemi per creare l'istanza
	 * @throws IllegalAccessException se non ci sono i permessi adeguati per
	 *         accedere ai metodi o alle propriet&agrave; della classe
	 * @see java.lang.Class#forName(java.lang.String)
	 */
	public Object getObjectFor(String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	/**
	 * Aggiunge la chiave <code>key</code> e il valore <code>value</code> al
	 * dizionario di traduzione per la lingua corrente.
	 * 
	 * @param key chiave
	 * @param value valore
	 */
	public void addKey(String key, String value);

	/**
	 * Aggiunge la chiave <code>key</code> e il valore <code>value</code> al
	 * dizionario di traduzione relativo alla lingua <code>locale</code>.
	 * 
	 * @param key chiave
	 * @param value valore
	 * @param locale locale
	 */
	public void addKey(String key, String value, Locale locale);

}
