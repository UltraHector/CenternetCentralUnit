package com.TroyEmpire.CenternetCentralUnit.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.TroyEmpire.CenternetCentralUnit.Constant.Constant;
import com.TroyEmpire.Centernet.Constant.ProbeResponseBodyType;
import com.TroyEmpire.Centernet.Entity.TCPProbeResponsePacket;
import com.TroyEmpire.Centernet.Entity.UdpProbeRequestPacket;
import com.TroyEmpire.CenternetCentralUnit.IService.IPortalPacketService;
import com.TroyEmpire.CenternetCentralUnit.Util.IOUtil;
import com.TroyEmpire.CenternetCentralUnit.Util.NetworkUtil;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

public class PortalPacketService implements IPortalPacketService {

	public TCPProbeResponsePacket configurePortalResponsePacket(
			UdpProbeRequestPacket probeRequest) {
		try {
			// if the user's phone has the newest portalpacket, the body would't
			// mounted
			boolean whetherLoadTheBody = false;
			boolean firstContact = true;
			String localPortalPacketVersion = null;
			/*
			 * portalPacketVersions is seperated by & Example:
			 * 9899899=version&342894013=version version commonly use
			 * Date->toString()
			 */
			if (probeRequest.getPortalPacketsVersion() == null
					|| probeRequest.getPortalPacketsVersion().equals("")) {
				whetherLoadTheBody = true;
				firstContact = true;
			} else {
				String[] portalPacketVersions = probeRequest
						.getPortalPacketsVersion().split("&");
				// if contains some value, do the following
				for (String portalPacketVersion : portalPacketVersions) {
					int equalMarkIndex = portalPacketVersion.indexOf("=");
					if (NetworkUtil.getMACAddress().hashCode() == Integer
							.valueOf(portalPacketVersion.substring(0,
									equalMarkIndex))) {
						// if contains the ccu's id, do the following
						firstContact = false;
						localPortalPacketVersion = getTheLocalPortalPacketVersion();
						if (localPortalPacketVersion != null
								&& !localPortalPacketVersion
										.equals(portalPacketVersion
												.substring(equalMarkIndex + 1))) {
							// the users' portal packet version is different
							// from ccu's
							whetherLoadTheBody = true;
							break;
						}
					}
				}
			}

			return loadTheResponsePacket(firstContact, whetherLoadTheBody,
					localPortalPacketVersion);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * load the TCPProbeResponsePacket based on the condition extracted from
	 * proberequest
	 */
	private TCPProbeResponsePacket loadTheResponsePacket(boolean firstContact,
			boolean whetherLoadTheBody, String localPortalPacketVersion) {
		String portalPacketTitle = getPortalPacketTitle();
		if (portalPacketTitle == null)
			portalPacketTitle = "欢迎使用CCU系统";

		// configure the packet
		TCPProbeResponsePacket tcpProbeResponsePacket = new TCPProbeResponsePacket();

		try {
			tcpProbeResponsePacket.getHead().setCcuId(NetworkUtil.getMACAddress()
					.hashCode());
			tcpProbeResponsePacket.getHead().setFirstCotact(firstContact);
			tcpProbeResponsePacket.getHead().setTitile(portalPacketTitle);

			// if load a new body, set the flag to updated to notice user's
			// phone to update
			tcpProbeResponsePacket.getHead().setUpdated(false);

			// only support zip file
			tcpProbeResponsePacket.getHead().setBodyType(ProbeResponseBodyType.ZIP);

			if (whetherLoadTheBody) {
				// need to load the body
				//TODO ?
				tcpProbeResponsePacket.getHead()
						.setContentLastModified(localPortalPacketVersion);

				File testFile = getPortalPacketBody();
				if (testFile != null) {
					tcpProbeResponsePacket.getBody().setBodyLoad(IOUtils
							.toByteArray(new FileInputStream(testFile)));
					tcpProbeResponsePacket.getHead().setUpdated(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tcpProbeResponsePacket;
	}

	public void updateLocalPortalPacket(String url, String credential) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		String fileName = "";
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs
					.add(new BasicNameValuePair("credential", credential));
			nameValuePairs.add(new BasicNameValuePair("portalPacketVersion",
					getTheLocalPortalPacketVersion()));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				if (header.getName().equals("Content-Disposition")) {
					fileName = header.getValue().substring(20);
				}
			}
			fileName = createLocaFileName(fileName);
			if (fileName == null) {
				// no file received
				return;
			}
			// TODO maybe not good, every time delete the legacy packet
			IOUtil.deleteFolder(new File(getLocalPortalPacketDirec()));
			File newPacketFile = new File(getLocalPortalPacketDirec() + "/"
					+ fileName);
			IOUtils.copy(response.getEntity().getContent(),
					new FileOutputStream(newPacketFile));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String createLocaFileName(String fileName) {
		try {
			if (fileName.equals(""))
				return null;
			// the fileName is in a format like: Body_123342.zip
			int underlineIndex = fileName.indexOf("_");
			int dotIndex = fileName.indexOf(".");
			String packetVersion = fileName.substring(underlineIndex + 1,
					dotIndex);
			// local packet filename is in a format: Body_macHashcode_123342.zip
			return "Body_" + NetworkUtil.getMACAddress().hashCode() + "_"
					+ packetVersion + fileName.substring(dotIndex);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getPortalPacketTitle() {
		Ini ccuConfigFile = new Ini();
		try {
			ccuConfigFile.load(new File(Constant.CENTERNET_CENTRAL_UNIT_INI));
			if (ccuConfigFile
					.containsKey(Constant.SECTION_CENTERNET_CENTRAL_UNIT)) {
				Section ccuConfig = ccuConfigFile
						.get(Constant.SECTION_CENTERNET_CENTRAL_UNIT);
				if (ccuConfig.containsKey(Constant.KEY_CCU_PORTAL_PACKET_TITLE)) {
					return ccuConfig.get(Constant.KEY_CCU_PORTAL_PACKET_TITLE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public File getPortalPacketBody() {
		Ini ccuConfigFile = new Ini();
		try {
			ccuConfigFile.load(new File(Constant.CENTERNET_CENTRAL_UNIT_INI));
			if (ccuConfigFile
					.containsKey(Constant.SECTION_CENTERNET_CENTRAL_UNIT)) {
				Section ccuConfig = ccuConfigFile
						.get(Constant.SECTION_CENTERNET_CENTRAL_UNIT);
				if (ccuConfig.containsKey(Constant.KEY_CCU_PORTAL_PACKET_PATH)) {
					File localPortalPacketDirec = new File(
							ccuConfig.get(Constant.KEY_CCU_PORTAL_PACKET_PATH));
					if (!localPortalPacketDirec.exists()) {
						// if not portal packet saved, return null
						return null;
					} else {
						File[] files = localPortalPacketDirec.listFiles();
						/*************************************************/
						// CCU only save at most one portal packet
						// so only need to return one file
						/*************************************************/
						if (files.length > 0)
							return files[0];

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	private String getLocalPortalPacketDirec() {
		Ini ccuConfigFile = new Ini();
		try {
			ccuConfigFile.load(new File(Constant.CENTERNET_CENTRAL_UNIT_INI));
			if (ccuConfigFile
					.containsKey(Constant.SECTION_CENTERNET_CENTRAL_UNIT)) {
				Section ccuConfig = ccuConfigFile
						.get(Constant.SECTION_CENTERNET_CENTRAL_UNIT);
				if (ccuConfig.containsKey(Constant.KEY_CCU_PORTAL_PACKET_PATH)) {
					File localPortalPacketDirec = new File(
							ccuConfig.get(Constant.KEY_CCU_PORTAL_PACKET_PATH));
					if (!localPortalPacketDirec.exists()) {
						localPortalPacketDirec.mkdirs();
					}
					return ccuConfig.get(Constant.KEY_CCU_PORTAL_PACKET_PATH);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public String getTheLocalPortalPacketVersion() {
		File localPortalPacketDirec = new File(getLocalPortalPacketDirec());
		if (!localPortalPacketDirec.exists()) {
			// if not portal packet saved, return null
			return null;
		} else {
			File[] files = localPortalPacketDirec.listFiles();
			if (files.length > 0)
				/*
				 * the file name is sepreated by "_" example:
				 * "Body_hashcode_datestring"
				 */
				return files[0].getName().split("_")[2];
		}
		return null;
	}
}
