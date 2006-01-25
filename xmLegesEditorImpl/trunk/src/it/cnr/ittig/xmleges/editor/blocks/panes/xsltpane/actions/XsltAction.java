package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltDocument;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltTextPane;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.TextAction;

/**
 * Classe base per definire le azioni da aggiungere a <code>XsltEditorKit</code> per
 * modificare quelle di default per HTMLEditorKit.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @see javax.swing.text.html.HTMLEditorKit
 * @see javax.swing.text.TextAction
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltAction extends TextAction {

	Action defaultAction = null;

	/**
	 * Costruttore di XsltAbstractAction
	 * 
	 * @param name nome dell'azione
	 */
	public XsltAction(String name) {
		super(name);
	}

	/**
	 * Imposta l'azione di default dell'HTMLEditorKit.
	 * 
	 * @param action azione di default
	 */
	public void setDefaultAction(Action defaultAction) {
		this.defaultAction = defaultAction;
	}

	/**
	 * Esegue l'azione di default.
	 * 
	 * @param event evento catturare
	 */
	public void fireDefaultAction(ActionEvent event) {
		if (defaultAction != null)
			this.defaultAction.actionPerformed(event);
	}

	/**
	 * Azione di default per le sottoclassi di <code>XsltAction</code>. Questo metodo
	 * esegue l'azione di default impostata tramite il metodo
	 * <code>setDefaultAction</code> e imposta automaticamente il leaf corrente del
	 * documento con quello alla posizione del cursore. <br>
	 * <b>Se la sottoclasse desidera modificare il comportamento di default deve
	 * sovrascrive questo metodo </b>.
	 * 
	 * @param event evento catturato
	 */
	public void actionPerformed(ActionEvent event) {
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug(getName().toUpperCase() + " BEGIN");
		fireDefaultAction(event);
		setActiveLeaf(event);
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug(getName().toUpperCase() + " END");
	}

	/**
	 * Restituisce il nome dell'azione.
	 * 
	 * @return nome dell'azione
	 */
	public String getName() {
		return getValue(Action.NAME).toString();
	}

	/**
	 * Restituisce il documento di tipo <code>XsltDocument</code> associato all'evento
	 * <code>event</code>.
	 * 
	 * @param event evento catturato
	 * @return documento
	 */
	public XsltDocument getXsltDocument(ActionEvent event) {
		return getXsltTextPane(event).getXsltDocument();
	}

	/**
	 * Restituisce il pannello di testo <code>XsltTextPane</code> associato all'evento
	 * <code>event</code>.
	 * 
	 * @param event evento catturato
	 * @return pannello di testo
	 */
	public XsltTextPane getXsltTextPane(ActionEvent event) {
		return (XsltTextPane) event.getSource();
	}

	/**
	 * Imposta l'elemento di testo attivo di <code>XsltDocument</code> associato
	 * all'evento <code>event</code> come quello individuato alla posizione corrente del
	 * caret.
	 * 
	 * @param event evento catturato
	 */
	public void setActiveLeaf(ActionEvent event) {
		setActiveLeaf(event, getLeafAtCurrentCaretPosition(event));
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug("new active leaf:" + getActiveLeaf(event).toString());
	}

	/**
	 * Imposta l'elemento di testo attivo di <code>XsltDocument</code> associato
	 * all'evento <code>event</code>.
	 * 
	 * @param event evento catturato
	 * @param leaf elemento di testo
	 */
	public void setActiveLeaf(ActionEvent event, XsltDocument.XsltLeafElement leaf) {
		if (getXsltDocument(event).setActiveLeaf(leaf, true))
			getXsltTextPane(event).updateSelections(true);
	}

	/**
	 * Restituisce l'elemento di testo di <code>XsltDocument</code> associato all'evento
	 * <code>event</code>.
	 * 
	 * @param event evento catturato
	 * @return elemento di testo di XsltDocument
	 */
	public XsltDocument.XsltLeafElement getActiveLeaf(ActionEvent event) {
		return getXsltDocument(event).getActiveLeaf();
	}

	/**
	 * Restituisce l'elemento di testo di <code>XsltDocument</code> alla posizione
	 * corrente del cursore.
	 * 
	 * @param event evento catturato
	 * @param pos posizione
	 * @return elemento di testo alla posizione corrente
	 */
	public XsltDocument.XsltLeafElement getLeafAtCurrentCaretPosition(ActionEvent event) {
		return getLeafAtCaretPosition(event, getCurrentCaretPosition(event));
	}

	/**
	 * Restituisce l'elemento di testo di <code>XsltDocument</code> alla posizione
	 * specificata da <code>pos</code>.
	 * 
	 * @param event evento catturato
	 * @param pos posizione
	 * @return elemento di testo alla posizione specificata
	 */
	public XsltDocument.XsltLeafElement getLeafAtCaretPosition(ActionEvent event, int pos) {
		return getXsltDocument(event).getLeafByPos(pos);
	}

	public XsltDocument.XsltLeafElement getPrevLeaf(ActionEvent e, int from) {
		XsltDocument.XsltLeafElement prev = null;
		for (; from >= 0; from--) {
			prev = getLeafAtCaretPosition(e, from);
			if (prev != null && prev.getId() != null)
				break;
			else
				prev = null;
		}
		return prev;
	}

	public XsltDocument.XsltLeafElement getNextLeaf(ActionEvent e, int from) {
		XsltDocument.XsltLeafElement next = null;
		int len = getXsltTextPane(e).getText().length();
		for (; from < len; from++) {
			next = getLeafAtCaretPosition(e, from);
			if (next != null && next.getId() != null)
				break;
			else
				next = null;
		}
		return next;
	}

	/**
	 * Muove il cursore al successivo leaf con id non nullo rispetto alla posizione del
	 * cursore corrente.
	 * 
	 * @param event evento catturato
	 * @return <code>true</code> se il leaf &egrave; stato trovato
	 */
	protected boolean goToNextValidLeaf(ActionEvent e) {
		return goToNextValidLeaf(e, getCurrentCaretPosition(e));
	}

	/**
	 * Muove il cursore al successivo leaf con id non nullo rispetto alla posizione
	 * <code>from</code>.
	 * 
	 * @param event evento catturato
	 * @param posizione di inizio per la ricerca
	 * @return <code>true</code> se il leaf &egrave; stato trovato
	 */
	protected boolean goToNextValidLeaf(ActionEvent e, int from) {
		if (getLeafAtCurrentCaretPosition(e).getId() == null) {
			XsltDocument.XsltLeafElement leaf = getNextLeaf(e, from);
			if (leaf == null) {
				leaf = getPrevLeaf(e, from);
				if (leaf != null) {
					setActiveLeaf(e, leaf);
					setCaretPosition(e, leaf.getEndOffset());
					getXsltTextPane(e).updateSelections(true);
					return true;
				}
			} else {
				setActiveLeaf(e, leaf);
				setCaretPosition(e, leaf.getStartOffset());
				getXsltTextPane(e).updateSelections(true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Muove il cursore al precedente leaf con id non nullo rispetto alla posizione del
	 * cursore corrente.
	 * 
	 * @param event evento catturato
	 * @return <code>true</code> se il leaf &egrave; stato trovato
	 */
	protected boolean goToPreviousValidLeaf(ActionEvent e) {
		return goToPreviousValidLeaf(e, getCurrentCaretPosition(e));
	}

	/**
	 * Muove il cursore al successivo leaf con id non nullo rispetto alla posizione
	 * <code>from</code>.
	 * 
	 * @param event evento catturato
	 * @param posizione di inizio per la ricerca
	 * @return <code>true</code> se il leaf &egrave; stato trovato
	 */
	protected boolean goToPreviousValidLeaf(ActionEvent e, int from) {
		if (getLeafAtCurrentCaretPosition(e).getId() == null) {
			XsltDocument.XsltLeafElement leaf = getPrevLeaf(e, from);
			if (leaf == null) {
				leaf = getNextLeaf(e, from);
				if (leaf != null) {
					setActiveLeaf(e, leaf);
					setCaretPosition(e, leaf.getStartOffset());
					getXsltTextPane(e).updateSelections(true);
					return true;
				}
			} else {
				setActiveLeaf(e, leaf);
				setCaretPosition(e, leaf.getEndOffset());
				getXsltTextPane(e).updateSelections(true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Restituisce la posizione corrente del cursore.
	 * 
	 * @param event evento catturato
	 * @return posizione corrente del cursore
	 */
	public int getCurrentCaretPosition(ActionEvent event) {
		return getXsltTextPane(event).getCaretPosition();
	}

	/**
	 * Imposta il cursore alla posizione <code>pos</code>.
	 * 
	 * @param event evento catturato
	 * @param pos nuova posizione del cursore
	 */
	public void setCaretPosition(ActionEvent event, int pos) {
		getXsltTextPane(event).setCaretPosition(pos, true);
	}

	/**
	 * Restituisce il logger.
	 * 
	 * @param event evento catturato
	 * @return logger
	 */
	public Logger getLogger(ActionEvent event) {
		return getXsltDocument(event).getLogger();
	}

	public String getText(ActionEvent event, XsltDocument.XsltLeafElement leaf) {
		try {
			int s = leaf.getStartOffset();
			int e = leaf.getEndOffset();
			return getXsltTextPane(event).getText(s, e - s);
		} catch (BadLocationException ex) {
			getLogger(event).error(ex.toString(), ex);
			return null;
		}
	}
}
