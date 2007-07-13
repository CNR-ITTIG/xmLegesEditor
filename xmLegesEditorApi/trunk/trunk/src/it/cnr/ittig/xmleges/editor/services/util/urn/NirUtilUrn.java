package it.cnr.ittig.xmleges.editor.services.util.urn;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la fornitura di utilit&agrave specifiche delle URN.
 * 
 * @version 1.0
 */

public interface NirUtilUrn extends Service {

	/**
	 * @param urn
	 * @return
	 */
	public String getFormaTestuale(Urn urn);

	/**
	 * @param id
	 * @return
	 */
	public String getFormaTestualeById(String id);

	/**
	 * @param partitionId
	 * @param refId
	 * @return
	 */
	public String getFormaTestualeById(String partitionId, String refId);

	/**
	 * @param partizione
	 * @return
	 */
	public String getUrnPartizione(String partizione);
}
