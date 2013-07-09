package com.TroyEmpire.CenternetCentralUnit.GUI;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.TroyEmpire.CenternetCentralUnit.IService.ICenternetServerService;
import com.TroyEmpire.CenternetCentralUnit.IService.IPortalPacketService;
import com.TroyEmpire.CenternetCentralUnit.Main.CCUGhost;
import com.TroyEmpire.CenternetCentralUnit.Service.CenternetServerService;
import com.TroyEmpire.CenternetCentralUnit.Service.PortalPacketService;

public class IndexFrame {
	private CCUGhost ccuGhost = new CCUGhost();
	private GhostWorker ghostWorker;
	private IPortalPacketService packetService = new PortalPacketService();
	private ICenternetServerService centernetServerService = new CenternetServerService();

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("CCU");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		GUIMain guiMain = new GUIMain();
		frame.setContentPane(createContentPane());
		frame.setJMenuBar(createMenuBar());
		// Display the window.
		frame.setSize(450, 260);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuSettings = new JMenu("系统设定");
		JMenu menuAbout = new JMenu("使用说明");
		menuBar.add(menuSettings);
		menuBar.add(menuAbout);
		return menuBar;
	}

	private Container createContentPane() {
		// Create the content-pane-to-be.
		JPanel contentPane = new JPanel(new FlowLayout());
		contentPane.setOpaque(true);

		// fresh the portal packet from centernet server
		JButton freshContentButton = new JButton();
		freshContentButton.setText("更新Portal");
		JButton startCCUButton = new JButton();
		startCCUButton.setText("启动CCU");
		JButton endCCUButton = new JButton();
		endCCUButton.setText("关闭CCU");
		startCCUButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ghostWorker = new GhostWorker();
				ghostWorker.execute();
			}
		});
		endCCUButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ccuGhost.endCCUGhost();
			}
		});
		freshContentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// dummy credential
				packetService.updateLocalPortalPacket(centernetServerService
						.getCenternetServerDownloadPacketURL(), "123456");
			}
		});
		JLabel ipLabel = new JLabel("服务器IP：");
		JTextField serverIp = new JTextField(25);
		// dummy data
		serverIp.setText("127.0.0.1");
		// Add the text area to the content pane.
		contentPane.add(ipLabel);
		contentPane.add(serverIp);
		contentPane.add(freshContentButton);
		contentPane.add(startCCUButton);
		contentPane.add(endCCUButton);
		return contentPane;
	}

	class GhostWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			ccuGhost.startCCUGhost();
			return null;
		}
	}

}
