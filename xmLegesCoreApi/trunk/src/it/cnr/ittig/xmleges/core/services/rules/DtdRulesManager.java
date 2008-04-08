package it.cnr.ittig.xmleges.core.services.rules;

import it.cnr.ittig.services.manager.Service;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import org.w3c.dom.Node;

/**
 * Servizio per che gestisce le regole scritte nella DTD di un documento XML.
 * DtdRulesManager legge una DTD e trasforma il content di ogni elemento in un
 * automa a stati finiti che rappresenta le regole a cui deve sottostare il
 * contenuto dell'elemento. Questo automa pu&ograve essere usate per interrogare
 * la classe su come &egrave possibile modificare un elemento.
 * 
 * @author Alessio Ceroni
 */
public interface DtdRulesManager extends Service {

	public void clear();

	/**
	 * @param filename
	 */
	public void loadRules(String filename);

	/**
	 * @param filename
	 * @param dtdPath
	 */
	public void loadRules(String filename, String dtdPath);

	/**
	 * @param file
	 */
	public void loadRules(File file);

	/**
	 * Restituisce una stringa in formato XML che definisce il contenuto di
	 * default di un elemento
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public String getDefaultContent(String elem_name) throws DtdRulesManagerException;

	/**
	 * Restituisce una stringa in formato XML che definisce il contenuto di
	 * default di un elemento
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param alternative uno dei possibili contenuti alternativi dell'elemento
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public String getDefaultContent(String elem_name, String alternative) throws DtdRulesManagerException;

	/**
	 * Restituisce i possibili contenuti alternativi di un elemento
	 * 
	 * @param elem_name
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Vector getAlternativeContents(String elem_name) throws DtdRulesManagerException;

	/**
	 * Controlla se una collezione di nomi di elementi puo rappresentare un
	 * insieme di figli di un nodo
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean isValid(String elem_name, Collection elem_children) throws DtdRulesManagerException;

	/**
	 * Enumera le alternative possibili dati il nodo padre ed i nomi dei nodi
	 * figli.
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @param choice_point la posizione nella sequenza di figli in cui valutare
	 *        le alternative
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Collection getAlternatives(String elem_name, Collection elem_children, int choice_point) throws DtdRulesManagerException;

	/**
	 * Restituisce un vettore di stringhe contenente il nome dei figli di un
	 * nodo I nodi che non sono testo o elementi vengono ignorati
	 * 
	 * @param node il nodo padre
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Vector getChildren(Node node) throws DtdRulesManagerException;

	/**
	 * Restituisce il nome di un nodo
	 * 
	 * @param dom_node
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public String getNodeName(Node dom_node) throws DtdRulesManagerException;

	/**
	 * Restituisce l'indice di un nodo all'interno del padre
	 * 
	 * @param parent il nodo padre
	 * @param child il nodo figlio
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public int getChildIndex(Node parent, Node child) throws DtdRulesManagerException;

	/**
	 * Controlla se il contenuto di un nodo &egrave valido
	 * 
	 * @param dom_node
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryIsValid(Node dom_node) throws DtdRulesManagerException;

	/**
	 * Controlla se un nodo pu&ograve contenere del testo
	 * 
	 * @param dom_node
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryTextContent(Node dom_node) throws DtdRulesManagerException;

	/**
	 * Controlla se un nodo pu&ograve contenere del testo
	 * 
	 * @param elem_name il nome del nodo in esame
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryTextContent(String elem_name) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile appendere un nodo ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_node il nodo da appendere
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanAppend(Node parent, Node new_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile appendere una collezione di nodi ad un
	 * elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_nodes i nodi da appendere
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanAppend(Node parent, Collection new_nodes) throws DtdRulesManagerException;

	/**
	 * Restituisce i possibili nodi da appendere ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Collection queryAppendable(Node parent) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile pre-pendere un nodo ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_node il nodo da pre-pendere
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanPrepend(Node parent, Node new_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile pre-pendere una collezione di nodi ad un
	 * elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_nodes i nodi da pre-pendere
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanPrepend(Node parent, Collection new_nodes) throws DtdRulesManagerException;

	/**
	 * Restituisce i possibili nodi da pre-pendere ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Collection queryPrependable(Node parent) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile inserire un nodo dopo un certo figlio di
	 * un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio dopo cui inserire il nuovo nodo
	 * @param new_node il nodo da inserire
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanInsertAfter(Node parent, Node child_node, Node new_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile inserire una collezione di nodi dopo un
	 * certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio dopo cui inserire il nuovo nodo
	 * @param new_nodes i nodi da inserire
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanInsertAfter(Node parent, Node child_node, Collection new_nodes) throws DtdRulesManagerException;

	/**
	 * Restituisce i nodi che &egrave possibile inserire dopo un certo figlio di
	 * un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio dopo cui inserire il nuovo nodo
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Collection queryInsertableAfter(Node parent, Node child_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile inserire un nodo prima di un certo figlio
	 * di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio prima di cui inserire il nuovo nodo
	 * @param new_node il nodo da inserire
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanInsertBefore(Node parent, Node child_node, Node new_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile inserire una collezione di nodi prima di
	 * un certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio prima di cui inserire il nuovo nodo
	 * @param new_nodes i nodi da inserire
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanInsertBefore(Node parent, Node child_node, Collection new_nodes) throws DtdRulesManagerException;

	/**
	 * Restituisce i nodi che &egrave possibile inserire prima di un certo
	 * figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio prima di cui inserire il nuovo nodo
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Collection queryInsertableBefore(Node parent, Node child_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile racchiudere un insieme di figli di un
	 * elemento in un altro elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il primo figlio da racchiudere
	 * @param no_children il numero di figli da racchiudere
	 * @param new_node il nodo contenitore
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanEncloseIn(Node parent, Node child_node, int no_children, Node new_node) throws DtdRulesManagerException;

	/**
	 * Restituisce gli elementi in cui &egrave possibile racchiudere un insieme
	 * di figli di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il primo figlio da racchiudere
	 * @param no_children il numero di figli da racchiudere
	 * @return gli elementi contenitore
	 * @throws DtdRulesManagerException
	 */
	public Collection queryContainers(Node parent, Node child_node, int no_children) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile racchiudere una parte di testo di un
	 * elemento in un altro elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il nodo testo da racchiudere
	 * @param new_node il nodo contenitore
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanEncloseTextIn(Node parent, Node child_node, Node new_node) throws DtdRulesManagerException;

	/**
	 * Restituisce gli elementi in cui &egrave possibile racchiudere una parte
	 * di testo di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il nodo di testo da racchiudere
	 * @return gli elementi contenitore
	 * @throws DtdRulesManagerException
	 */
	public Collection queryTextContainers(Node parent, Node child_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile sostituire un insieme di figli di un
	 * elemento con un altro insieme di elementi
	 * 
	 * @param parent il nodo padre
	 * @param child_node il primo figlio da racchiudere
	 * @param no_children il numero di figli da racchiudere
	 * @param new_nodes gli elementi con cui sostituire i figli specificati
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanReplaceWith(Node parent, Node child_node, int no_children, Collection new_nodes) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile sostituire una parte di testo con un
	 * insieme di elementi
	 * 
	 * @param parent il nodo padre
	 * @param child_node il nodo testo da racchiudere
	 * @param new_nodes gli elementi con cui sostituire il testo specificato
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanReplaceTextWith(Node parent, Node child_node, Collection new_nodes) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile eliminare un certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio da eliminare
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanDelete(Node parent, Node child_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile eliminare un certo insieme di figli di un
	 * elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il primo figlio da eliminare
	 * @param no_children il numero di figli da eliminare
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanDelete(Node parent, Node child_node, int no_children) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile inserire un nodo all'interno di un certo
	 * figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio in cui inserire il nuovo nodo, deve essere un
	 *        nodo testo
	 * @param new_node il nodo da inserire
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanInsertInside(Node parent, Node child_node, Node new_node) throws DtdRulesManagerException;

	/**
	 * Controlla se &egrave possibile inserire una collezione di nodi
	 * all'interno di un certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio in cui inserire il nuovo nodo, deve essere un
	 *        nodo testo
	 * @param new_nodes i nodi da inserire
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryCanInsertInside(Node parent, Node child_node, Collection new_nodes) throws DtdRulesManagerException;

	/**
	 * Restituisce i nodi che &egrave possibile inserire all'interno di un certo
	 * figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio in cui inserire il nuovo nodo, deve essere un
	 *        nodo testo
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Collection queryInsertableInside(Node parent, Node child_node) throws DtdRulesManagerException;

	/**
	 * Restituisce la lista dei nomi degli attributi di un elemento
	 * 
	 * @param elem_name il nome dell'elemento di cui si vuole la lista degli
	 *        attributi
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public Collection queryGetAttributes(String elem_name) throws DtdRulesManagerException;

	/**
	 * Restituisce il valore di default (se esiste) di un attributo di un
	 * elemento
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede il valore di
	 *        default dell'attributo
	 * @param att_name il nome dell'attributo
	 * @return la stringa vuota se non esiste il valore di default
	 * @throws DtdRulesManagerException
	 */
	public String queryGetAttributeDefaultValue(String elem_name, String att_name) throws DtdRulesManagerException;

	/**
	 * Restituisce i valori possibili per un attributo
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede il valore di
	 *        default dell'attributo
	 * @param att_name il nome dell'attributo
	 * @return <code>null</code> se l'attributo puo' avere qualsiasi valore
	 * @throws DtdRulesManagerException
	 */
	public Collection queryGetAttributePossibleValues(String elem_name, String att_name) throws DtdRulesManagerException;

	/**
	 * Restituisce <code>true</code> se esiste l'attributo specificato per
	 * questo elemento
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede l'esistena
	 *        dell'attributo
	 * @param att_name il nome dell'attributo
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryIsValidAttribute(String elem_name, String att_name) throws DtdRulesManagerException;

	/**
	 * Restituisce <code>true</code> se l'attributo specificato &egrave di
	 * tipo <code>#REQUIRED</code>
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede l'esistena
	 *        dell'attributo
	 * @param att_name il nome dell'attributo
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryIsRequiredAttribute(String elem_name, String att_name) throws DtdRulesManagerException;

	/**
	 * Restituisce <code>true</code> se l'attributo specificato &egrave di
	 * tipo <code>#FIXED</code>
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede l'esistena
	 *        dell'attributo
	 * @param att_name il nome dell'attributo
	 * @throws DtdRulesManagerException
	 * @return
	 */
	public boolean queryIsFixedAttribute(String elem_name, String att_name) throws DtdRulesManagerException;

	/**
	 * Restituisce <code>true</code> se l'attributo specificato per questo
	 * elemento pu&ograve assumere il valore richiesto
	 * 
	 * @return
	 * @param elem_name il nome dell'elemento di cui si chiede la validit&agrave
	 *        del valore dell'attributo
	 * @param att_name il nome dell'attributo
	 * @param value il valore dell'attributo
	 * @throws DtdRulesManagerException
	 */
	public boolean queryIsValidAttributeValue(String elem_name, String att_name, String value) throws DtdRulesManagerException;

	/**
	 * Riempie il nodo con gli attributi necessari
	 * 
	 * @param elem il nodo di cui si vogliono settare gli attributi
	 * @throws DtdRulesManagerException
	 */
	public void fillRequiredAttributes(Node elem) throws DtdRulesManagerException;
}
