package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.xmleges.editor.services.dalos.SynsetHolder;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.util.Observable;

public class SynsetHolderImpl extends Observable
implements SynsetHolder {

	//This object can be a Synset or a TreeOntoClass
	private Object selectedSynset = null;
	
	public void setSynset(Object synObject) {
		
		selectedSynset = synObject;
		setChanged();
		notifyObservers(selectedSynset);
	}
	
	public Object getSynset() {
		
		return selectedSynset;
	}
}
