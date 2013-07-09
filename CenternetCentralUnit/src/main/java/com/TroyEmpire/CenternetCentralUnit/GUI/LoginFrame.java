package com.TroyEmpire.CenternetCentralUnit.GUI;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

class LoginFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	static JTextField accountNameField;
	static JPasswordField passwordField;
	static JButton buttonConfirm, buttonCancel;
	int boo[] = { 0, 0, 0, 0, 0, 0, 0 };

	LoginFrame(String string) {
		super(string);

		Font font = new Font("Serief", Font.BOLD, 17);
		accountNameField = new JTextField();
		accountNameField.setPreferredSize(new Dimension(200, 30));
		accountNameField.setFont(font);
		passwordField = new JPasswordField();
		passwordField.setPreferredSize(new Dimension(200, 30));
		passwordField.setFont(font);
		buttonConfirm = new JButton("确定");
		buttonConfirm.setPreferredSize(new Dimension(100, 30));
		buttonConfirm.setFont(font);
		buttonCancel = new JButton("取消");
		buttonCancel.setPreferredSize(new Dimension(100, 30));
		buttonCancel.setFont(font);
		buttonConfirm.addActionListener(this);
		buttonCancel.addActionListener(this);

		JPanel accountNamePanel = new JPanel();
		JPanel passwordPanel = new JPanel();
		JPanel buttonDecisionPanel = new JPanel();
		JLabel titleLabel = new JLabel("欢迎使用CCU系统");
		titleLabel.setFont(new Font("Serief", Font.BOLD, 20));

		JLabel accountNameLabel = new JLabel("用户名称：");
		accountNameLabel.setFont(new Font("Serief", Font.BOLD, 15));
		accountNamePanel.add(accountNameLabel);
		accountNamePanel.add(accountNameField);
		accountNamePanel.setOpaque(false);

		JLabel passwordLabel = new JLabel("用户密码：");
		passwordLabel.setFont(new Font("Serief", Font.BOLD, 15));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		passwordPanel.setOpaque(false);
		buttonDecisionPanel.add(buttonConfirm);
		buttonDecisionPanel.add(buttonCancel);
		buttonDecisionPanel.setOpaque(false);

		Box box = Box.createVerticalBox();
		box.add(titleLabel);
		box.add(Box.createVerticalStrut(35));
		box.add(accountNamePanel);
		box.add(Box.createVerticalStrut(8));
		box.add(passwordPanel);
		box.add(Box.createVerticalStrut(8));
		box.add(buttonDecisionPanel);

		ImageIcon icon = new ImageIcon(getClass().getResource("1.jpg"));
		JLabel iconLabel = new JLabel(icon);
		getLayeredPane().add(iconLabel, new Integer(Integer.MIN_VALUE));

		Container con = getContentPane();
		con.setLayout(new FlowLayout());
		con.add(box);
		((JPanel) con).setOpaque(false);

		validate();
		setVisible(true);
		setSize(450, 260);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		iconLabel.setBounds(0, 0, this.getWidth(), this.getHeight());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonConfirm) {
			IndexFrame indexFrame = new IndexFrame();
			indexFrame.createAndShowGUI();
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(buttonConfirm, "帐号或密码有误！", "消息对话框",
					JOptionPane.INFORMATION_MESSAGE);
			accountNameField.setText(null);
			passwordField.setText(null);
		}
		if (e.getSource() == buttonCancel) {
			System.exit(0);
		}
	}
}