package com.ctgu;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import com.ctgu.engine.UcciEngine;
import com.ctgu.model.ChessAudio;
import com.ctgu.view.ChessPanel;
import com.ctgu.view.Utils;

public class Application
{
	/** UIManager中UI字体相关的key */
	private static String[] DEFAULT_FONT = new String[] { "Table.font", "TableHeader.font", "CheckBox.font",
			"Tree.font", "Viewport.font", "ProgressBar.font", "RadioButtonMenuItem.font", "ToolBar.font",
			"ColorChooser.font", "ToggleButton.font", "Panel.font", "TextArea.font", "Menu.font", "TableHeader.font",
			"OptionPane.font", "MenuBar.font", "Button.font", "Label.font", "PasswordField.font", "ScrollPane.font",
			"MenuItem.font", "ToolTip.font", "List.font", "EditorPane.font", "Table.font", "TabbedPane.font",
			"RadioButton.font", "CheckBoxMenuItem.font", "TextPane.font", "PopupMenu.font", "TitledBorder.font",
			"ComboBox.font" };

	protected static void addWindowClickOperation(JFrame frame)
	{
		frame.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
				Utils.createLogFile();
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				Utils.deleteLogFile();
			}

			@Override
			public void windowClosed(WindowEvent e)
			{

			}

			@Override
			public void windowIconified(WindowEvent e)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{

			}

			@Override
			public void windowActivated(WindowEvent e)
			{

			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{

			}
		});
	}

	/**
	 * 启动应用
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					// 设置本属性将改变窗口边框样式定义
					UIManager.put("RootPane.setupButtonVisible", false);
					String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
					UIManager.setLookAndFeel(lookAndFeel);
					// 调整默认字体
					for (int i = 0; i < DEFAULT_FONT.length; i++)
					{
						UIManager.put(DEFAULT_FONT[i], new Font("微软雅黑", Font.PLAIN, 14));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(1300, 950);
				frame.setResizable(true);
				// 窗口在屏幕中间显示
				frame.setLocationRelativeTo(null);
				frame.setTitle("中国象棋");
				frame.setVisible(true);
				frame.validate();

				ChessPanel chessPanel = new ChessPanel();
				chessPanel.setUseIccs(true);
				frame.getContentPane().add(chessPanel, BorderLayout.CENTER);

				// 创建菜单栏
				JMenuBar menuBar = new JMenuBar();
				// 创建菜单
				JMenu gameMenu = new JMenu("游戏");
				// 创建菜单项
				JMenuItem newItem = new JMenuItem("新棋局");
				JMenuItem openItem = new JMenuItem("打开棋局");
				JMenuItem saveItem = new JMenuItem("保存棋局");
				JMenuItem exitItem = new JMenuItem("退出");
				// 把菜单项添加到菜单中
				gameMenu.add(newItem);
				gameMenu.add(openItem);
				gameMenu.add(saveItem);
				gameMenu.add(exitItem);
				// 把菜单添加到菜单栏中
				menuBar.add(gameMenu);

				JMenu showMenu = new JMenu("显示");
				JMenuItem boradItem = new JMenuItem("棋盘方案");
				JMenuItem pieceItem = new JMenuItem("棋子方案");
				JMenuItem coordinateItem = new JMenuItem("坐标方案");
				showMenu.add(boradItem);
				showMenu.add(pieceItem);
				showMenu.add(coordinateItem);
				menuBar.add(showMenu);

				JMenu controlMenu = new JMenu("控制");
				JMenuItem engineThinkItem = new JMenuItem("电脑思考黑方");
				JMenuItem playerThinkItem = new JMenuItem("玩家思考黑方");
				JMenuItem retractItem = new JMenuItem("悔棋");
				JMenuItem instantRunItem = new JMenuItem("立即出招");
				controlMenu.add(engineThinkItem);
				controlMenu.add(playerThinkItem);
				controlMenu.add(retractItem);
				controlMenu.add(instantRunItem);
				menuBar.add(controlMenu);

				JMenu chessMenu = new JMenu("棋局");
				JMenuItem copyItem = new JMenuItem("复制局面");
				JMenuItem pasteItem = new JMenuItem("粘贴局面");
				chessMenu.add(copyItem);
				chessMenu.add(pasteItem);
				menuBar.add(chessMenu);

				JMenu helpMenu = new JMenu("帮助");
				JMenuItem helpItem = new JMenuItem("帮助");
				JMenuItem aboutItem = new JMenuItem("关于");
				helpMenu.add(helpItem);
				helpMenu.add(aboutItem);
				menuBar.add(helpMenu);
				// 设置布局管理器，把菜单栏添加到框架北部
				frame.getContentPane().add(menuBar, BorderLayout.NORTH);

				addWindowClickOperation(frame);
			}
		});

		try
		{
			// 初始化引擎
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(UcciEngine.getOutputStream()));
			bw.write("ucci\r\n");
			bw.newLine();
			bw.write("setoption Hash 256\r\n");
			bw.newLine();
			bw.flush();
			// System.out.println("初始化引擎...");
			ChessAudio.OPEN_BOARD.play();
		}
		catch (IOException e)
		{
			System.out.println("初始化引擎出错：" + e.getMessage());
			e.printStackTrace();
		}
	}
}
