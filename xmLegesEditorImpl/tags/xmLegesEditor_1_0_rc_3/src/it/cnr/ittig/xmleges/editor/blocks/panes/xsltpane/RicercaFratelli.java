/*
 * Created on May 12, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane;

/**
 * @author mirco
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RicercaFratelli {

	/**
	 * 
	 */
	public RicercaFratelli() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * private Vector currentBros;
	 * 
	 * 
	 * 
	 * public void updateSelection(int start, int end, boolean internal) {
	 * logger.debug(getClass().getName().toUpperCase() + " Caret pos: " +
	 * xsltPaneImpl.getXsltTextPane().getCaretPosition()); // endSel =
	 * xsltPaneImpl.getXsltTextPane().getCaretPosition(); if (end > start) {
	 * logger.info(getClass().getName().toUpperCase() + " FWD"); if (activeLeaf !=
	 * getElement(end) & (getElement(end).getId() != null)) {
	 * 
	 * logger.info(getClass().getName().toUpperCase() + " SELEZIONE MULTIPLA SUI NODI");
	 * 
	 * Node[] textNode = getTextNodes(start, end);
	 * 
	 * Node[] bros = getBrothers(textNode);
	 * 
	 * Node[] sel = getSelectedTextNodes(bros);
	 * 
	 * XsltDocument.XsltLeafElement[] leaves = getSelectedLeaf(sel);
	 * 
	 * xsltPaneImpl.getXsltTextPane().setSelectionStart(leaves[0].getStartOffset());
	 * xsltPaneImpl.getXsltTextPane().setSelectionEnd( (leaves[leaves.length -
	 * 1]).getEndOffset());
	 * 
	 * xsltPaneImpl.fireSelectedNodesChanged(bros); } } else if (end < start) {
	 * logger.info(getClass().getName().toUpperCase() + " BACK"); if (activeLeaf !=
	 * getElement(end)) { logger.info(getClass().getName().toUpperCase() + " SELEZIONE
	 * MULTIPLA SUI NODI"); Node[] textNode = getTextNodes(end, start);
	 * 
	 * Node[] bros = getBrothers(textNode);
	 * 
	 * Node[] sel = getSelectedTextNodes(bros);
	 * 
	 * XsltDocument.XsltLeafElement[] leaves = getSelectedLeaf(sel);
	 * 
	 * xsltPaneImpl.getXsltTextPane().setSelectionStart(leaves[0].getStartOffset());
	 * xsltPaneImpl.getXsltTextPane().setSelectionEnd( (leaves[leaves.length -
	 * 1]).getEndOffset()); //
	 * xsltPaneImpl.getXsltTextPane().setCaretPosition(leaves[0].getStartOffset());
	 * xsltPaneImpl.fireSelectedNodesChanged(bros); } } }
	 * 
	 * 
	 * 
	 * private String getBrothersLevel(Node[] node) { String[] path = new
	 * String[node.length]; String[] commonPath = new String[node.length]; for (int i = 0;
	 * i < node.length; i++) path[i] = UtilDom.getPathName(node[i]); String common =
	 * path[0]; for (int i = 0; i < (path.length); i++) { // if (path[i]) // TODO: cercare
	 * la sottostringa comune! while (!path[i].startsWith(common)) common =
	 * common.substring(0, common.lastIndexOf("."));
	 * logger.info(getClass().getName().toUpperCase() + " - common: " + common); }
	 * logger.info(getClass().getName().toUpperCase() + " - Def. common: " + common);
	 * return common.substring(common.lastIndexOf(".") + 1, common.length()); }
	 *  // Trova gli elementi testo da selezionare a partire dai fratelli comuni public
	 * Node[] getSelectedTextNodes(Node[] node) { currentSelection = new Vector(); for
	 * (int i = 0; i < node.length; i++) { getSelectedTextNodes(node[i]); } Node[] n = new
	 * Node[currentSelection.size()]; currentSelection.copyInto(n); return n; } // Per la
	 * ricorsione di met3 protected void getSelectedTextNodes(Node n) { if
	 * (n.getNodeType() == Node.TEXT_NODE | xsltMapper.getXsltByDom(n).getNodeName() ==
	 * "XsltMapperNode") { currentSelection.addElement(n); } else { NodeList nl =
	 * n.getChildNodes(); for (int i = 0; i < nl.getLength(); i++)
	 * getSelectedTextNodes(nl.item(i)); } }
	 * 
	 * 
	 * Node[] fratelli; // Trova i fratelli comuni public Node[] getBrothers(Node[] node) {
	 * selLevel = getBrothersLevel(node); logger.info(selLevel); fratelli =
	 * findBrothers(node); selLevel = ""; return fratelli; }
	 * 
	 * Vector currentSelection;
	 * 
	 * private int caretPos;
	 * 
	 * 
	 * private Node[] findBrothers(Node[] node) { currentBros = new Vector();
	 * findBrothers(getCommonAncestor(node)); Node[] res = new Node[currentBros.size()];
	 * currentBros.copyInto(res); return res; }
	 * 
	 * private void findBrothers(Node parent) { NodeList nl = parent.getChildNodes(); for
	 * (int i = 0; i < nl.getLength(); i++) { if (nl.item(i).getNodeName() == "#text" |
	 * nl.item(i).getNodeType() == Node.TEXT_NODE) { String path =
	 * UtilDom.getPathName(nl.item(i)); if (path.indexOf(selLevel) != -1) { Node tmp =
	 * nl.item(i); while (!(tmp.getNodeName().matches(selLevel))) tmp =
	 * tmp.getParentNode(); if (!currentBros.contains(tmp)) currentBros.addElement(tmp); } }
	 * else { findBrothers(nl.item(i)); } } }
	 * 
	 * private Node getCommonAncestor(Node[] node) { Node res = null; Node[] tmp =
	 * (Node[]) node.clone(); if (tmp.length > 1) { for (int i = 0; i < tmp.length - 1;
	 * i++) tmp[i + 1] = UtilDom.getCommonAncestor(tmp[i], tmp[i + 1]); res =
	 * tmp[tmp.length - 1]; } else if (tmp.length == 1) res = tmp[0]; return res; }
	 */
}
