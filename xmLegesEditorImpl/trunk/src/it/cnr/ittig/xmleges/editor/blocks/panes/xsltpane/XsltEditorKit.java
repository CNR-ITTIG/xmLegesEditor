package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltBackwardAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltBeginAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltBeginLineAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltBeginParagraphAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltBeginWordAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltCopyAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltCutAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltDeleteNextCharAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltDeletePrevCharAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltDownAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltEndAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltEndLineAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltEndParagraphAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltEndWordAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltForwardAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltInsertBreakAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltKeyTypedAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltNextWordAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltPageDownAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltPageUpAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltPasteAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltPreviousWordAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltSelectionBackwardAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltSelectionForwardAction;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions.XsltUpAction;

import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.text.Document;
import javax.swing.text.TextAction;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Classe per impostare le azioni di modifica.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltEditorKit extends HTMLEditorKit {
	XsltPaneImpl xsltPaneImpl;

	Logger logger;

	XsltDocument document;

	XsltBackwardAction xsltBackwardAction = new XsltBackwardAction();

	XsltBeginAction xsltBeginAction = new XsltBeginAction();

	XsltBeginLineAction xsltBeginLineAction = new XsltBeginLineAction();

	XsltBeginParagraphAction xsltBeginParagraphAction = new XsltBeginParagraphAction();

	XsltBeginWordAction xsltBeginWordAction = new XsltBeginWordAction();

	XsltCopyAction xsltCopyAction = new XsltCopyAction();

	XsltCutAction xsltCutAction = new XsltCutAction();

	XsltDeleteNextCharAction xsltDeleteNextCharAction = new XsltDeleteNextCharAction();

	XsltDeletePrevCharAction xsltDeletePrevCharAction = new XsltDeletePrevCharAction();

	XsltDownAction xsltDownAction = new XsltDownAction();

	XsltEndAction xsltEndAction = new XsltEndAction();

	XsltEndLineAction xsltEndLineAction = new XsltEndLineAction();

	XsltEndParagraphAction xsltEndParagraphAction = new XsltEndParagraphAction();

	XsltEndWordAction xsltEndWordAction = new XsltEndWordAction();

	XsltForwardAction xsltForwardAction = new XsltForwardAction();

	XsltInsertBreakAction xsltInsertBreakAction = new XsltInsertBreakAction();

	XsltKeyTypedAction xsltKeyTypedAction = new XsltKeyTypedAction();

	XsltNextWordAction xsltNextWordAction = new XsltNextWordAction();

	XsltPageDownAction xsltPageDownAction = new XsltPageDownAction();

	XsltPageUpAction xsltPageUpAction = new XsltPageUpAction();

	XsltPasteAction xsltPasteAction = new XsltPasteAction();

	XsltPreviousWordAction xsltPreviousWordAction = new XsltPreviousWordAction();

	XsltUpAction xsltUpAction = new XsltUpAction();

	XsltSelectionForwardAction xsltSelectionForwardAction = new XsltSelectionForwardAction();

	XsltSelectionBackwardAction xsltSelectionBackwardAction = new XsltSelectionBackwardAction();

	private Action[] xsltActions;

	/** Crea una nuova istanza di XsltEditorKit */
	public XsltEditorKit(XsltPaneImpl xsltPaneImpl) {
		this.xsltPaneImpl = xsltPaneImpl;
		this.logger = xsltPaneImpl.getLogger();
		xsltDeleteNextCharAction.setForwardAction(xsltForwardAction);
		xsltDeletePrevCharAction.setBackwardAction(xsltBackwardAction);
		xsltActions = new Action[] { xsltBackwardAction, xsltBeginAction, xsltBeginLineAction, xsltBeginParagraphAction, xsltBeginWordAction, xsltCopyAction,
				xsltCutAction, xsltDeleteNextCharAction, xsltDeletePrevCharAction, xsltDownAction, xsltEndAction, xsltEndLineAction, xsltEndParagraphAction,
				xsltEndWordAction, xsltForwardAction, xsltInsertBreakAction, xsltKeyTypedAction, xsltNextWordAction, xsltPageDownAction, xsltPageUpAction,
				xsltPasteAction, xsltPreviousWordAction, xsltUpAction, xsltSelectionForwardAction, xsltSelectionBackwardAction };
	}

	public Document createDefaultDocument() {
		document = new XsltDocument(xsltPaneImpl);
		return document;
	}

	public Action[] getActions() {
		Hashtable hash = new Hashtable();
		Action[] curr = super.getActions();
		for (int i = 0; i < curr.length; i++) {
			hash.put(curr[i].getValue(Action.NAME), curr[i]);
			// System.out.println("getAction():" + curr[i].getValue(Action.NAME)
			// + " -> " + curr[i]);
		}
		for (int i = 0; i < xsltActions.length; i++)
			try {
				XsltAction a = (XsltAction) xsltActions[i];
				Action oa = (Action) hash.get(a.getName());
				// System.out.println("a=" + a.getName() + "oa=" +
				// oa.getValue(Action.NAME));
				a.setDefaultAction(oa);
			} catch (ClassCastException ex) {
			}
		return TextAction.augmentList(super.getActions(), xsltActions);
	}

	public HTMLEditorKit.Parser getParser() {
		return super.getParser();
	}

}
