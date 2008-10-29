/**
 * RulesManager &egrave la classe che gestisce le regole scritte nella
 * DTD del documento. RulesManager legge una DTD e trasforma il
 * content di ogni elemento in un automa a stati finiti che
 * rappresenta le regole a cui deve sottostare il contenuto
 * dell'elemento. Questo automa pu&ograve essere usate per interrogare
 * la classe su come &egrave possibile modificare un elemento.
 * @author Alessio Ceroni
 * @version %I%, %G%
 */

package it.cnr.ittig.xmleges.core.blocks.rules;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.blocks.dtd.DtdRulesManagerImpl;
import it.cnr.ittig.xmleges.core.blocks.schema.SchemaRulesManagerImpl;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Node;


public class RulesManagerImpl implements RulesManager, Initializable, Loggable {

	Logger logger;
	RulesManager rm;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	
	
	public void initialize() throws Exception {
		//selectRulesManager();		
	}
	
	
	
	public void createRulesManager(String extension){
		if(extension == null || ((extension.indexOf("dtd")==-1) && (extension.indexOf("xsd")==-1)))
			extension="xsd";   // defaule
			
		if(extension.indexOf("xsd")!=-1)
			rm=(RulesManager) new SchemaRulesManagerImpl(logger);
		else
			rm=(RulesManager) new DtdRulesManagerImpl(logger);
		
	}
	
	public boolean assessAttribute(Node node, String attributeName){
		return rm.assessAttribute(node, attributeName);
	}
	
	public boolean assess(Node node) {
		return rm.assess(node);
	}



	public void clear() {
		rm.clear();	
	}

	
	
	public Map getQNamePrefixToNamespaceMap() {
		return rm.getQNamePrefixToNamespaceMap();
	}


	public void fillRequiredAttributes(Node elem) throws RulesManagerException {
		rm.fillRequiredAttributes(elem);
	}



	public Vector getAlternativeContents(String elem_name) throws RulesManagerException {
		return rm.getAlternativeContents(elem_name);
	}



	public Collection getAlternatives(String elem_name, Collection elem_children, int choice_point) throws RulesManagerException {
		return rm.getAlternatives(elem_name, elem_children, choice_point);
	}



	public int getChildIndex(Node parent, Node child) throws RulesManagerException {
		return rm.getChildIndex(parent, child);
	}



	public Vector getChildren(Node node) throws RulesManagerException {
		return rm.getChildren(node);
	}



	public String getDefaultContent(String elem_name, String alternative) throws RulesManagerException {
		return rm.getDefaultContent(elem_name);
	}



	public String getDefaultContent(String elem_name) throws RulesManagerException {
		return rm.getDefaultContent(elem_name);
	}



	public String getNodeName(Node dom_node) throws RulesManagerException {
		return rm.getNodeName(dom_node);
	}



	public boolean isValid(String elem_name, Collection elem_children) throws RulesManagerException {
		return rm.isValid(elem_name, elem_children);
	}



	public void loadRules(File file) {
		rm.loadRules(file);
	}



	public void loadRules(String filename, String dtdPath) {
		rm.loadRules(filename, dtdPath);
	}



	public void loadRules(String filename) {
		rm.loadRules(filename);
	}



	public Collection queryAppendable(Node parent) throws RulesManagerException {
		return rm.queryAppendable(parent);
	}



	public boolean queryCanAppend(Node parent, Collection new_nodes) throws RulesManagerException {
		return rm.queryCanAppend(parent, new_nodes);
	}



	public boolean queryCanAppend(Node parent, Node new_node) throws RulesManagerException {
		return rm.queryCanAppend(parent, new_node);
	}



	public boolean queryCanDelete(Node parent, Node child_node, int no_children) throws RulesManagerException {
		return rm.queryCanDelete(parent, child_node, no_children);
	}



	public boolean queryCanDelete(Node parent, Node child_node) throws RulesManagerException {
		return rm.queryCanDelete(parent, child_node);
	}



	public boolean queryCanEncloseIn(Node parent, Node child_node, int no_children, Node new_node) throws RulesManagerException {
		return rm.queryCanEncloseIn(parent, child_node, no_children, new_node);
	}



	public boolean queryCanEncloseTextIn(Node parent, Node child_node, Node new_node) throws RulesManagerException {
		return rm.queryCanEncloseTextIn(parent, child_node, new_node);							
	}



	public boolean queryCanInsertAfter(Node parent, Node child_node, Collection new_nodes) throws RulesManagerException {
		return rm.queryCanInsertAfter(parent, child_node, new_nodes);
	}



	public boolean queryCanInsertAfter(Node parent, Node child_node, Node new_node) throws RulesManagerException {
		return rm.queryCanInsertAfter(parent, child_node, new_node);
	}



	public boolean queryCanInsertBefore(Node parent, Node child_node, Collection new_nodes) throws RulesManagerException {
		return rm.queryCanInsertBefore(parent, child_node, new_nodes);
	}



	public boolean queryCanInsertBefore(Node parent, Node child_node, Node new_node) throws RulesManagerException {
		return rm.queryCanInsertBefore(parent, child_node, new_node);
	}



	public boolean queryCanInsertInside(Node parent, Node child_node, Collection new_nodes) throws RulesManagerException {
		return rm.queryCanInsertInside(parent, child_node, new_nodes);
	}



	public boolean queryCanInsertInside(Node parent, Node child_node, Node new_node) throws RulesManagerException {
		return rm.queryCanInsertInside(parent, child_node, new_node);
	}



	public boolean queryCanPrepend(Node parent, Collection new_nodes) throws RulesManagerException {
		return rm.queryCanPrepend(parent, new_nodes);
	}



	public boolean queryCanPrepend(Node parent, Node new_node) throws RulesManagerException {
		return rm.queryCanPrepend(parent, new_node);
	}



	public boolean queryCanReplaceTextWith(Node parent, Node child_node, Collection new_nodes) throws RulesManagerException {
		return rm.queryCanReplaceTextWith(parent, child_node, new_nodes);
	}



	public boolean queryCanReplaceWith(Node parent, Node child_node, int no_children, Collection new_nodes) throws RulesManagerException {
		return rm.queryCanReplaceWith(parent, child_node, no_children, new_nodes);
	}



	public Collection queryContainers(Node parent, Node child_node, int no_children) throws RulesManagerException {
		return rm.queryContainers(parent, child_node, no_children);
	}



	public String queryGetAttributeDefaultValue(String elem_name, String att_name) throws RulesManagerException {
		return rm.queryGetAttributeDefaultValue(elem_name, att_name);
	}



	public Collection queryGetAttributePossibleValues(String elem_name, String att_name) throws RulesManagerException {
		return rm.queryGetAttributePossibleValues(elem_name, att_name);
	}



	public Collection queryGetAttributes(String elem_name) throws RulesManagerException {
		return rm.queryGetAttributes(elem_name);
	}



	public Collection queryInsertableAfter(Node parent, Node child_node) throws RulesManagerException {
		return rm.queryInsertableAfter(parent, child_node);
	}



	public Collection queryInsertableBefore(Node parent, Node child_node) throws RulesManagerException {
		return rm.queryInsertableBefore(parent, child_node);
	}



	public Collection queryInsertableInside(Node parent, Node child_node) throws RulesManagerException {
		return rm.queryInsertableInside(parent, child_node);
	}



	public boolean queryIsFixedAttribute(String elem_name, String att_name) throws RulesManagerException {
		return rm.queryIsFixedAttribute(elem_name, att_name);
	}



	public boolean queryIsRequiredAttribute(String elem_name, String att_name) throws RulesManagerException {
		return rm.queryIsRequiredAttribute(elem_name, att_name);
	}



	public boolean queryIsValid(Node dom_node) throws RulesManagerException {
		return rm.queryIsValid(dom_node);
	}



	public boolean queryIsValidAttribute(String elem_name, String att_name) throws RulesManagerException {
		return rm.queryIsValidAttribute(elem_name, att_name);
	}



	public boolean queryIsValidAttributeValue(String elem_name, String att_name, String value) throws RulesManagerException {
		return rm.queryIsValidAttributeValue(elem_name, att_name, value);
	}



	public Collection queryPrependable(Node parent) throws RulesManagerException {
		return rm.queryPrependable(parent);
	}



	public Collection queryTextContainers(Node parent, Node child_node) throws RulesManagerException {
		return rm.queryTextContainers(parent, child_node);
	}



	public boolean queryTextContent(Node dom_node) throws RulesManagerException {
		return rm.queryTextContent(dom_node);
	}



	public boolean queryTextContent(String elem_name) throws RulesManagerException {
		return rm.queryTextContent(elem_name);	
	}



	public String getDefaultContent(String elem_name, Vector nodes) throws RulesManagerException {
		return rm.getDefaultContent(elem_name, nodes);
	}

	
}
