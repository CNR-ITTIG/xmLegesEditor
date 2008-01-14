package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions;

import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.AntiAliasedTextPane;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.TextAction;
import javax.swing.text.html.HTMLDocument;

/**
 * Classe base per definire le azioni da aggiungere a <code>XsltEditorKit</code>
 * per modificare quelle di default per HTMLEditorKit.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @see javax.swing.text.html.HTMLEditorKit
 * @see javax.swing.text.TextAction
 * @version 1.0
 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
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
	 * @param defaultAction azione di default
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
	 * Azione di default per le sottoclassi di <code>XsltAction</code>.
	 * Questo metodo esegue l'azione di default impostata tramite il metodo
	 * <code>setDefaultAction</code> e imposta automaticamente il leaf
	 * corrente del documento con quello alla posizione del cursore. <br>
	 * <b>Se la sottoclasse desidera modificare il comportamento di default deve
	 * sovrascrive questo metodo </b>.
	 * 
	 * @param event evento catturato
	 */
	public void actionPerformed(ActionEvent event) {
		fireDefaultAction(event);
	}

	/**
	 * Restituisce il nome dell'azione.
	 * 
	 * @return nome dell'azione
	 */
	public String getName() {
		return getValue(Action.NAME).toString();
	}
	
	protected String getText(ActionEvent evt, Element span)
	{
		if(!AntiAliasedTextPane.isMappedElement(span)) return null;
		AntiAliasedTextPane pane = (AntiAliasedTextPane)getTextComponent(evt);
		pane.getPane().getLogger().debug("XsltAction.getText()");
		String retVal = null;
		int start = span.getStartOffset();
		int end = span.getEndOffset();
		try	{
			retVal = pane.getText(start, end - start).trim();
		} catch (BadLocationException ble) 
		{
			ble.printStackTrace();
		}
		return retVal;
	}

	public void insertDefaultText(AntiAliasedTextPane pane, Element span, HTMLDocument doc)
	{
		if(!AntiAliasedTextPane.isMappedElement(span)) return;
		pane.getPane().getLogger().debug("XsltAction.insertDefaultText(" + pane.getElementId(span) +")");
		int start = span.getStartOffset();
    	try
		{
    			// skip starting space
    		doc.insertString(start+1, pane.getDefaultText(span), span.getAttributes());
		} 
    	catch (BadLocationException ble) 
		{
			ble.printStackTrace();
		}
	}
}
