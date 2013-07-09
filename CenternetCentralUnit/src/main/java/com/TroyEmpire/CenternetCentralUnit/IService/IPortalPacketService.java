package com.TroyEmpire.CenternetCentralUnit.IService;

import com.TroyEmpire.Centernet.Entity.TCPProbeResponsePacket;
import com.TroyEmpire.Centernet.Entity.UdpProbeRequestPacket;

public interface IPortalPacketService {
	/**
	 * Load the TCP probe response package based on the proberequest
	 * 
	 * @param probeRequest
	 */
	TCPProbeResponsePacket configurePortalResponsePacket(
			UdpProbeRequestPacket probeRequest);

	/**
	 * retrieve the latest portal packet from centernet server update the portal
	 * packet
	 * 
	 * @param url
	 *            the url from which the portal packet will be downloaded
	 * @param credential
	 *            the ccu's credential for downloading packet from server
	 */
	void updateLocalPortalPacket(String url, String credential);

	/**
	 * the ccu only save at most one portalpacket
	 * 
	 * @return version of portal packet saved in the ccu null if no portalpacket
	 *         saved yet
	 */
	String getTheLocalPortalPacketVersion();

}
