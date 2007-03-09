/*
 * Created on Dec 15, 2004 TODO To change the template for this generated file
 * go to Window - Preferences - Java - Code Style - Code Templates
 */
package it.cnr.ittig.xmleges.editor.services.provvedimenti;

/**
 * @author Lorenzo Sarti TODO To change the template for this generated type
 *         comment go to Window - Preferences - Java - Code Style - Code
 *         Templates
 */
public class ProvvedimentiItem {
	/**
	 * Nome del provvedimento
	 */
	private String name;

	/**
	 * Indica se il tipo di provvedimento richiede la specificazione di un
	 * emanante
	 */
	private boolean emananti;

	/**
	 * tipo provvedimento da utilizzare nella composizione della urn
	 */
	private String urnatto;

	/**
	 * forma estesa di citazione
	 */
	private String urncitazione;

	/**
	 * espressione regolare che indica quali sono le autorita' emananti. Serve
	 * per interrogare il registro autorita'
	 */
	private String urnautorita;

	/**
	 * urn completa del provvedimento. Utilizzata se il provvedimento e' gia'
	 * esistente e se ne conoscono data di emanazione, autorita' emanante,
	 * ecc... . Per es. la Costituzione.
	 */
	private String urnvalore;

	/**
	 * Elenco dei template associati al provvedimento. Possono essere uno o
	 * piu', a seconda delle combinazioni ammissibili "Articolato" o
	 * "SemiArticolato" e Numerato o Non numerato. Al max. sono quattro.
	 */
	private TemplateItem[] templatelist;

	/**
	 * Elenco delle dtd associate al provvedimento.
	 */
	private DtdItem[] dtdlist;

	boolean hastemplate;

	public ProvvedimentiItem() {
		name = "";
		emananti = false;
		urnatto = "";
		urncitazione = "";
		urnautorita = "";
		urnvalore = "";
		hastemplate = false;
	}

	public boolean getHasTemplate() {
		return hastemplate;
	}

	public void hasTemplate(boolean b) {
		this.hastemplate = b;
	}

	/**
	 * Restituisce il nome del provvedimento
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Restituisce la necessita' o meno di indicare un emanante
	 * 
	 * @return
	 */

	public boolean getEmananti() {
		return emananti;
	}

	/**
	 * Restituisce la porzione di urn relativa all'atto
	 * 
	 * @return
	 */
	public String getUrnAtto() {
		return urnatto;
	}

	/**
	 * Restituisce la forma testuale di citazione
	 * 
	 * @return
	 */
	public String getUrnCitazione() {
		return urncitazione;
	}

	/**
	 * Restituisce l'espressione regolare necessaria per interrogare il registro
	 * autorita'
	 * 
	 * @return
	 */

	public String getUrnAutorita() {
		return urnautorita;
	}

	/**
	 * Restituisce la urn completa del provvedimento, se presente
	 * 
	 * @return
	 */
	public String getUrnValore() {
		return urnvalore;
	}

	/**
	 * Restituisce la lista dei template associati al provvedimento
	 * 
	 * @return
	 */
	public TemplateItem[] getTemplateList() {
		return templatelist;
	}

	/**
	 * Restituisce un elemento della lista dei template
	 * 
	 * @param index: elemento richiesto
	 * @return
	 */
	public TemplateItem getTemplateAt(int index) {
		try {
			return (templatelist[index]);
		} catch (Exception e) {
			// System.out.println(e.toString());
			return (null);
		}
	}

	/**
	 * Restituisce la lista delle dtd associate al provvedimento
	 * 
	 * @return
	 */
	public DtdItem[] getDtdList() {
		return dtdlist;
	}

	/**
	 * Restituisce un elemento della lista delle dtd
	 * 
	 * @param index: elemento richiesto
	 * @return
	 */
	public DtdItem getDtdAt(int index) {
		try {
			return (dtdlist[index]);
		} catch (Exception e) {
			return (null);
		}
	}

	/**
	 * Imposta la proprieta' name
	 * 
	 * @param name: nome provvedimento
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Imposta la proprieta' emananti
	 * 
	 * @param emananti: il provvedimento ammette piu' di un'emanante?
	 */
	public void setEmananti(boolean emananti) {
		this.emananti = emananti;
	}

	/**
	 * Imposta la proprieta' che si riferisce alla stringa da utilizzare nella
	 * composizione della urn
	 * 
	 * @param urnatto: urn relativa all'atto
	 */
	public void setUrnAtto(String urnatto) {
		this.urnatto = urnatto;
	}

	/**
	 * Imposta la forma testuale della citazione
	 * 
	 * @param urncitazione: forma testuale della citazione
	 */
	public void setUrnCitazione(String urncitazione) {
		this.urncitazione = urncitazione;
	}

	/**
	 * Imposta l'espressione regolare da utilizzare per interrogare il registro
	 * autorita'
	 * 
	 * @param urnautorita: espressione regolare
	 */
	public void setUrnAutorita(String urnautorita) {
		this.urnautorita = urnautorita;
	}

	/**
	 * Imposta la urn completa del provvedimeto, se disponibile
	 * 
	 * @param urnvalore: urn completa del provvedimento
	 */
	public void setUrnValore(String urnvalore) {
		this.urnvalore = urnvalore;
	}

	/**
	 * Imposta la lista dei template
	 * 
	 * @param templatelist: lista dei template
	 */
	public void setTemplateList(TemplateItem[] templatelist) {
		this.templatelist = templatelist;
	}

	/**
	 * Imposta un elemento della lista dei template
	 * 
	 * @param item: nuovo elemento
	 * @param index: posizione di inserimento
	 */
	public void setTemplateListAt(TemplateItem item, int index) {
		try {
			templatelist[index] = item;
		} catch (Exception e) {
			// System.out.println(e.toString());

		}
	}

	/**
	 * Imposta la lista delle dtd
	 * 
	 * @param dtdlist: lista delle dtd
	 */
	public void setDtdList(DtdItem[] dtdlist) {
		this.dtdlist = dtdlist;
	}

	/**
	 * Imposta un elemento della lista delle dtd
	 * 
	 * @param item: nuovo elemento
	 * @param index: posizione di inserimento
	 */
	public void setDtdListAt(DtdItem item, int index) {
		try {
			dtdlist[index] = item;
		} catch (Exception e) {
			// System.out.println(e.toString());
		}
	}

	public String toString() {
		return (this.getName());
	}
}
