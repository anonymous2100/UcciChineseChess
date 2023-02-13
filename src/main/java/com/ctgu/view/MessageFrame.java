package com.ctgu.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class MessageFrame extends javax.swing.JFrame
{
	/** serialVersionUID */
	private static final long serialVersionUID = -1072559780508199525L;

	private JLabel text;
	Toolkit tk = Toolkit.getDefaultToolkit();
	Dimension screensize = tk.getScreenSize();
	int height = screensize.height;
	int width = screensize.width;
	private String str = null;

	public MessageFrame(String str)
	{
		this.str = str;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				initGUI();
			}
		}).start();
	}

	private void initGUI()
	{
		try
		{
			// 让Frame窗口失去边框和标题栏的修饰
			setUndecorated(true);
			// 将此窗口置于屏幕的中央
			setLocationRelativeTo(null);
			setVisible(true);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				text = new JLabel("<html>" + str + "</html>", JLabel.CENTER);
				text.setFont(new Font("微软雅黑", Font.PLAIN, 24));
				getContentPane().add(text, BorderLayout.CENTER);
				text.setBackground(new java.awt.Color(255, 251, 240));
			}
			pack();
			setBounds(width / 2 - 180, height / 2, 360, 120);
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
			dispose();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		new MessageFrame("落子位置与起点位置相同！");
	}
}