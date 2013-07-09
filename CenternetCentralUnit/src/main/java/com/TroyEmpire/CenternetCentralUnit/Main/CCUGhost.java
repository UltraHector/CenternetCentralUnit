package com.TroyEmpire.CenternetCentralUnit.Main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import com.TroyEmpire.Centernet.Constant.Constant;
import com.TroyEmpire.Centernet.Constant.OSType;
import com.TroyEmpire.Centernet.Entity.UdpProbeRequestPacket;
import com.TroyEmpire.CenternetCentralUnit.IService.IPortalPacketService;
import com.TroyEmpire.CenternetCentralUnit.Service.PortalPacketService;
import com.TroyEmpire.CenternetCentralUnit.Util.IOUtil;

public class CCUGhost {

	private DatagramSocket udpSocket;

	private IPortalPacketService configureDPService = new PortalPacketService();

	public void endCCUGhost() {
		if (!udpSocket.isClosed())
			udpSocket.close();
	}

	public void startCCUGhost() {
		try {
			// Keep a socket open to listen to all the UDP trafic that is
			// destined for this port
			udpSocket = new DatagramSocket(Constant.PROBE_SERVER_PORT);
			udpSocket.setBroadcast(true);
			byte[] recvBuf = new byte[Constant.PROBE_REQUEST_SERVER_MAX_BUFFER];
			DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
			System.out.println(getClass().getName()
					+ ">>>Ready to receive broadcast packets!");
			while (true) {
				udpSocket.receive(packet);
				// Packet received
				System.out.println(getClass().getName()
						+ ">>>Discovery packet received from: "
						+ packet.getAddress().getHostAddress());
				// convert the packet data to an object
				UdpProbeRequestPacket probeRequest = (UdpProbeRequestPacket) IOUtil
						.convertBytesToObject(packet.getData());
				// new thread to handle a request, not block the server
				final UdpProbeRequestPacket probeRequestCopy = new UdpProbeRequestPacket(
						probeRequest);
				final InetAddress ip = packet.getAddress();
				Thread child = new Thread() {
					@Override
					public void run() {
						handleARequest(probeRequestCopy, ip);
					}
				};
				child.start();
			}
		} catch (IOException e) {
			System.out.println(">>>Stop receiving broadcast packets!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * handle the request and configure the portal packet to send back
	 */
	private void handleARequest(UdpProbeRequestPacket probeRequest,
			InetAddress ip) {
		System.out.println(getClass().getName()
				+ ">>>Packet received; data: Operating system is: "
				+ probeRequest.getOSType());
		// See if the packet holds the right command (message)
		if (probeRequest.getOSType() == OSType.ANDROID) {
			try {
				System.out.println(ip.getHostAddress() + ":"
						+ probeRequest.getTcpPort());
				Socket tcpSocket = new Socket(ip.getHostAddress(),
						probeRequest.getTcpPort());
				tcpSocket.getOutputStream().write(
						IOUtil.convertObjectToBytes(configureDPService
								.configurePortalResponsePacket(probeRequest)));
			} catch (Exception e) {
				System.out.println(getClass().getName()
						+ "Error: Sent packet to: " + ip);
				e.printStackTrace();
				return;
			}
			System.out.println(getClass().getName() + ">>>Sent packet to: "
					+ ip);
		}
	}
}