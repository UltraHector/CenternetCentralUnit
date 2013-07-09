package com.TroyEmpire.CenternetCentralUnit.Service;

import java.io.File;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.TroyEmpire.CenternetCentralUnit.Constant.Constant;
import com.TroyEmpire.CenternetCentralUnit.IService.ICenternetServerService;

public class CenternetServerService implements ICenternetServerService {

	public String getCenternetServerDownloadPacketURL() {
		try {
			String url = "";
			Ini configIni = new Ini();
			configIni.load(new File(Constant.CENTERNET_CENTRAL_UNIT_INI));
			if (configIni.containsKey(Constant.SECTION_CENTERNET_CENTRAL_UNIT)) {
				Section section = configIni
						.get(Constant.SECTION_CENTERNET_CENTRAL_UNIT);
				if (section.containsKey(Constant.CENTERNET_SERVER_IP)) {
					// load ip
					url += "http://"
							+ section.get(Constant.CENTERNET_SERVER_IP);
					// load port and request path
					if (section.containsKey(Constant.CENTERNET_SERVER_PORT)) {
						url += ":"
								+ section.get(Constant.CENTERNET_SERVER_PORT)
								+ "/getLatestResponseBody";
						return url;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
