package com.ctgu.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.ctgu.controller.ChessController;
import com.ctgu.engine.UcciEngine;
import com.ctgu.model.ChessAudio;
import com.ctgu.model.ChessPoint;
import com.ctgu.model.Record;
import com.ctgu.model.Step;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChessPanel extends JPanel
{
	/** serialVersionUID */
	private static final long serialVersionUID = 6672024539300490002L;
	public static String[] xIndex = { "0", "1", "2", "3", "4", "5", "6", "7", "8" };
	public static String[] yIndex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	private static List<ChessPoint> blackIndicitorList = new ArrayList<>();

	private boolean useIccs = true;
	private Image imgBoard;
	private int board = 0;
	private int pieces = 0;
	private static BufferedImage imgSelected;
	private static BufferedImage imgSelected2;
	private List<Record> stepList = new ArrayList<>();
	private ChessController controller = new ChessController();
	private String[][] initPieceArray = new String[10][9];
	/**
	 * 双方没有吃子的走棋步数，一旦上一步是吃子，则此变量将被置为0
	 */
	private int noEatMoves = 0;
	/**
	 * 当前的回合数，从1开始，
	 */
	// private int moveNums = 1;
	/**
	 * 下面是界面控件信息
	 */
	private JTabbedPane tabbedPane;
	private JScrollPane chessHistoryPane;
	private JTextArea stepTextArea;
	private JScrollPane engineInfoPane;
	private JTextArea engineTextArea;
	private JButton btnNewGame;
	private JButton btnSave;
	private JButton btnRetract;
	private JButton btnChangeBoard;
	private JButton btnChangePiece;
	private JButton btnChangeCoordinate;
	private JButton btnCopy;
	private JButton btnPaste;

	public ChessPanel()
	{
		this.setLayout(null);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(630, 55, 593, 770);
		this.add(tabbedPane);

		chessHistoryPane = new JScrollPane();
		tabbedPane.addTab("棋谱信息", null, chessHistoryPane, null);
		stepTextArea = new JTextArea();
		stepTextArea.setFont(new Font("黑体", 0, 22));
		stepTextArea.setEditable(false);
		chessHistoryPane.setViewportView(stepTextArea);

		engineInfoPane = new JScrollPane();
		tabbedPane.addTab("引擎思考信息", null, engineInfoPane, null);
		engineTextArea = new JTextArea();
		engineTextArea.setFont(new Font("黑体", 0, 22));
		engineTextArea.setEditable(false);
		engineInfoPane.setViewportView(engineTextArea);

		btnNewGame = new JButton("新棋局");
		btnNewGame.setBounds(90, 750, 95, 25);
		this.add(btnNewGame);

		btnSave = new JButton("存棋谱");
		btnSave.setBounds(220, 750, 95, 25);
		this.add(btnSave);

		btnRetract = new JButton("悔棋");
		btnRetract.setBounds(340, 750, 95, 25);
		this.add(btnRetract);

		btnCopy = new JButton("复制局面");
		btnCopy.setBounds(455, 750, 95, 25);
		this.add(btnCopy);

		btnChangeBoard = new JButton("更换棋盘");
		btnChangeBoard.setBounds(90, 800, 95, 25);
		this.add(btnChangeBoard);

		btnChangePiece = new JButton("更换棋子");
		btnChangePiece.setBounds(220, 800, 95, 25);
		this.add(btnChangePiece);

		btnChangeCoordinate = new JButton("更换坐标");
		btnChangeCoordinate.setBounds(340, 800, 95, 25);
		this.add(btnChangeCoordinate);

		btnPaste = new JButton("粘贴局面");
		btnPaste.setBounds(455, 800, 95, 25);
		this.add(btnPaste);

		// 初始化组件监听器
		initBoardListener();
		// 初始化棋盘面板监听器
		initComponentListener();
		// 初始化棋盘棋子资源
		for (int i = 0; i < controller.pieceArray.length; i++)
		{
			System.arraycopy(controller.pieceArray[i], 0, initPieceArray[i], 0, controller.pieceArray[i].length);
		}
		initView();
	}

	private void initComponentListener()
	{
		btnNewGame.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				log.info("重新开始游戏...");
				new MessageFrame("重新开始游戏...");
				for (int i = 0; i < initPieceArray.length; i++)
				{
					System.arraycopy(initPieceArray[i], 0, controller.pieceArray[i], 0, initPieceArray[i].length);
				}
				controller.currentPoint.setLocation(-1, -1);
				initView();
				repaint();
				stepList.clear();

				Utils.deleteLogFile();
				Utils.createLogFile();
				stepTextArea.setText("");
				engineTextArea.setText("");
				ChessAudio.play(ChessAudio.OPEN_BOARD);
			}
		});
	}

	private void initBoardListener()
	{
		this.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
			}

			public void mouseExited(MouseEvent e)
			{
			}

			public void mousePressed(MouseEvent e)
			{
				// 将鼠标点击位置转换为棋盘坐标
				int y = Math.round(1.0f * (e.getY() - ChessConstant.CHESSBOARD_MARGIN - ChessConstant.Y_INIT)
						/ ChessConstant.GRID_WIDTH);
				int x = Math.round(1.0f * (e.getX() - ChessConstant.CHESSBOARD_MARGIN - ChessConstant.X_INIT)
						/ ChessConstant.GRID_WIDTH);
				log.info("e.getY()=" + e.getY() + ",e.getX()=" + e.getX() + "，y=" + y + ",x=" + x);
				// 处理鼠标点击棋盘上交叉点的点击事件
				if ((y >= 0 && y <= 9) && (x >= 0 && x <= 8))
				{
					// log.info("pieceArray[" + y + "][" + x + "]=" + pieceArray[y][x]);
					// 更新标记，同时每次点击时都要更新当前位置
					if (controller.pieceArray[y][x].length() > 0)
					{
						updateMoveIndicitorList(y, x, controller.pieceArray[y][x]);
						loadPieces();
						repaint();
					}
					controller.currentPoint.setLocation(x, y);
					log.info("controller.lastPoint=(" + (int) controller.lastPoint.getX() + ","
							+ (int) controller.lastPoint.getY() + ")" + ", controller.currentPoint=("
							+ (int) controller.currentPoint.getX() + "," + (int) controller.currentPoint.getY() + ")");
					if (controller.hasWinner(controller.pieceArray))
					{
						log.warn("棋局已结束！");
						return;
					}
					if (controller.currentPoint.equals(controller.lastPoint))
					{
						log.warn("落子位置与起点位置相同！");
						// new MessageFrame("落子位置与起点位置相同！");
						return;
					}
					// 判断当前位置能否落子
					if (controller.checkWhetherCanMove(controller.pieceArray[y][x], controller.lastPoint,
							controller.currentPoint))
					{
						// 判断当前走棋是否会被将军
						boolean isCheckedFlag = isChecked();
						if (isCheckedFlag)
						{
							log.info("正在被将军，请应将！");
							new MessageFrame("正在被将军，请应将！");
							return;
						}

						// 当前位置有对方的棋子，则表示发生了吃子
						boolean eatFlag = checkIfEat(controller.pieceArray[(int) controller.currentPoint
								.getY()][(int) controller.currentPoint.getX()]);
						if (eatFlag)
						{
							if (controller.redMove)
							{
								ChessAudio.play(ChessAudio.MAN_EAT);
							}
							else
							{
								ChessAudio.play(ChessAudio.COM_EAT);
							}
						}
						else
						{
							ChessAudio.play(ChessAudio.MAN_MOVE);
						}
						// 上一次点击的棋子，移动到当前位置
						controller.pieceArray[(int) controller.currentPoint.getY()][(int) controller.currentPoint
								.getX()] = controller.pieceArray[(int) controller.lastPoint
										.getY()][(int) controller.lastPoint.getX()];
						// 再把上一个位置的棋子置为空
						controller.pieceArray[(int) controller.lastPoint.getY()][(int) controller.lastPoint
								.getX()] = "";
						controller.moveIndicitorList.clear();
						blackIndicitorList.clear();
						loadPieces();
						repaint();
						log.info("【红方落子】：lastPoint=(" + (int) controller.lastPoint.getY() + ","
								+ (int) controller.lastPoint.getX() + ")" + "--->" + "currentPoint=("
								+ (int) controller.currentPoint.getY() + "," + (int) controller.currentPoint.getX()
								+ ")，是否吃子=" + eatFlag + "，是否被将军=" + isCheckedFlag + "，轮到黑方下棋了！");

						// 记录棋谱
						Record record = new Record();
						String chessName = controller
								.getChineseChess(controller.pieceArray[(int) controller.currentPoint
										.getY()][(int) controller.currentPoint.getX()]);
						// log.info("chessName=" + chessName + ",redMove=" + controller.redMove);
						record.setChessName(chessName);
						record.setRedMove(controller.redMove);
						Step step = new Step(new ChessPoint(controller.lastPoint),
								new ChessPoint(controller.currentPoint));
						record.setChessStep(step);
						record.setEatFlag(eatFlag);
						stepList.add(record);

						// 交换红黑标记
						controller.redMove = !controller.redMove;
						// 开启一个新的线程，让引擎走棋
						new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									Thread.sleep(1000);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								thinking();
							}
						}).start();
						showSteps();
						// log.info("redMove=" + controller.redMove);
					}
					// else
					// {
					// new MessageFrame("不允许落子！");
					// ChessAudio.play(ChessAudio.MAN_MOV_ERROR);
					// }
					// 检查长拦和长捉
					// 判断是否会被将军
				}
			}

			public void mouseReleased(MouseEvent e)
			{
			}
		});
	}

	// TODO 判断当前红方是否正在被将军
	protected boolean isChecked()
	{
		ChessPoint redKingPoint = new ChessPoint();
		// 先找出黑方棋子的位置，若本方的帅在对方棋子的可移动路径上，则表示当前正在被将军
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				String chessValue = controller.pieceArray[y][x];
				// 找到红方帅的位置
				if (chessValue.startsWith("rk"))
				{
					redKingPoint.setX(x);
					redKingPoint.setY(y);
				}
			}
		}
		// 遍历黑方各个棋子的可移动路线，取合集，判断是否包含红方帅的位置
		List<String> blackList = new ArrayList<>();
		List<ChessPoint> allList = new ArrayList<>();
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				String chessValue = controller.pieceArray[y][x];
				if (chessValue.startsWith("b"))
				{
					blackList.add(chessValue);
					List<ChessPoint> singleIndicitorList = new ArrayList<>();
					for (int yy = 0; yy < 10; yy++)
					{
						for (int xx = 0; xx < 9; xx++)
						{
							// log.info("i={},blackList[i]={}，x={},y={}", i, blackList.get(i), x, y);
							if (controller.checkWhetherCanMove(chessValue, new Point(x, y), new Point(xx, yy)))
							{
								singleIndicitorList.add(new ChessPoint(xx, yy));
							}
						}
					}
					log.info("【isChecked】chessValue={}, singleIndicitorList={}", controller.getChineseChess(chessValue),
							singleIndicitorList);
					allList.addAll(singleIndicitorList);
				}
			}
		}
		Set<ChessPoint> userSet = new HashSet<>(allList);
		blackIndicitorList = new ArrayList<>(userSet);
		if (blackIndicitorList.size() > 0
				&& blackIndicitorList.contains(new ChessPoint(redKingPoint.getX(), redKingPoint.getY())))
		{
			return true;
		}
		return false;
	}

	protected void updateMoveIndicitorList(int yy, int xx, String pieceValue)
	{
		controller.moveIndicitorList.clear();
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				if (controller.checkWhetherCanMove(pieceValue, new Point(xx, yy), new Point(x, y)))
				{
					controller.moveIndicitorList.add(new ChessPoint(x, y));
				}
			}
		}
	}

	protected boolean checkIfEat(String pieceValue)
	{
		if (controller.redMove)
		{
			if (pieceValue.startsWith("b"))
			{
				return true;
			}
		}
		else
		{
			if (pieceValue.startsWith("r"))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 初始化棋盘和棋子
	 */
	private void initView()
	{
		loadBoard();
		loadPieces();
	}

	public void loadBoard()
	{
		imgBoard = getImage(getCodeBase(), "boards/" + controller.BOARD_NAME[board]);
	}

	public void loadPieces()
	{
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				// if (controller.pieceArray[y][x].length() == 0)
				// {
				// System.out.print("pieceArray[" + y + "][" + x + "]=" + controller.pieceArray[y][x] + " \t");
				// }
				// else
				// {
				// System.out.print("pieceArray[" + y + "][" + x + "]=" + controller.pieceArray[y][x] + "\t");
				// }
				// 将点击的位置与棋子图片对应起来
				if (controller.pieceArray[y][x].length() > 0)
				{
					String filePath = getCodeBase() + "pieces/" + controller.PIECES_NAME[pieces] + "/"
							+ controller.pieceArray[y][x];
					File file = new File(filePath);
					BufferedImage image;
					try
					{
						image = ImageIO.read(file);
						controller.pieceImageArray[y][x] = image;
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			// log.info();
		}
		imgSelected = getImage(getCodeBase(), "pieces/" + controller.PIECES_NAME[pieces] + "/oos.gif");
		imgSelected2 = getImage(getCodeBase(), "pieces/" + controller.PIECES_NAME[pieces] + "/oos2.jpg");
	}

	private String getCodeBase()
	{
		String filePath = this.getClass()
				.getClassLoader()
				.getResource("")
				.getFile();
		return filePath;
	}

	private BufferedImage getImage(String basePath, String string)
	{
		BufferedImage bufImage = null;
		try
		{
			File file = new File(basePath + string);
			bufImage = ImageIO.read(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return bufImage;
	}

	public boolean isUseIccs()
	{
		return useIccs;
	}

	public void setUseIccs(boolean useIccs)
	{
		this.useIccs = useIccs;
	}

	public Point getPoint()
	{
		return controller.currentPoint;
	}

	public void setPoint(Point currentPoint)
	{
		this.controller.currentPoint = currentPoint;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		// 调用super.paintComponent(g);去清除运动的痕迹
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		// 绘制棋盘背景图片
		g2.drawImage(imgBoard, ChessConstant.CHESSBOARD_MARGIN, ChessConstant.CHESSBOARD_MARGIN, this);
		// 画棋盘刻度线
		if (isUseIccs())
		{
			drawICCSNumbers(g2);
			// drawICoordinates(g2);
		}
		else
		{
			drawNumbers(g2);
		}
		// 画棋盘线
		drawGrid(g2);
		// 绘制方向箭头
		if (stepList.size() > 0)
		{
			Record lastRecord = stepList.get(stepList.size() - 1);
			Step lastStep = lastRecord.getChessStep();
			List<ChessPoint> list = new ArrayList<>();
			list.add(new ChessPoint(lastStep.getFrom()
					.getX() + 2,
					lastStep.getFrom()
							.getY() + 2));
			list.add(new ChessPoint(lastStep.getTo()
					.getX() + 2,
					lastStep.getTo()
							.getY() + 2));
			getDirectionLine(list, ChessConstant.GRID_WIDTH, ChessConstant.GRID_WIDTH, g2);
		}
		// 绘制棋子图片,10行9列
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				// 将点击的位置与棋子对应起来
				// log.info("pieceImageArray[x][y]=" + pieceImageArray[x][y]);
				if (controller.pieceArray[y][x].length() > 0 && !controller.pieceArray[y][x].equals("oos.gif"))
				{
					g2.drawImage(controller.pieceImageArray[y][x],
							ChessConstant.CHESSBOARD_MARGIN + x * ChessConstant.GRID_WIDTH
									- ChessConstant.GRID_WIDTH / 2,
							ChessConstant.CHESSBOARD_MARGIN + y * ChessConstant.GRID_WIDTH
									- ChessConstant.GRID_WIDTH / 2,
							this);
				}
			}
		}

		// 绘制当前点击位置提示
		if (controller.currentPoint.getX() >= 0 && controller.currentPoint.getY() >= 0 && controller.redMove)
		{
			g2.drawImage(imgSelected,
					ChessConstant.CHESSBOARD_MARGIN + (int) controller.currentPoint.getX() * ChessConstant.GRID_WIDTH
							- ChessConstant.GRID_WIDTH / 2,
					ChessConstant.CHESSBOARD_MARGIN + (int) controller.currentPoint.getY() * ChessConstant.GRID_WIDTH
							- ChessConstant.GRID_WIDTH / 2,
					this);
		}

		if (controller.currentPoint.getX() >= 0 && controller.currentPoint.getY() >= 0)
		{
			controller.lastPoint.setLocation((int) controller.currentPoint.getX(),
					(int) controller.currentPoint.getY());
		}

		// 绘制上一个位置和当前位置提示
		if (stepList.size() > 0)
		{
			Record lastRecord = stepList.get(stepList.size() - 1);
			Step lastStep = lastRecord.getChessStep();
			g2.drawImage(imgSelected2, ChessConstant.CHESSBOARD_MARGIN + lastStep.getFrom()
					.getX() * ChessConstant.GRID_WIDTH - ChessConstant.GRID_WIDTH / 2,
					ChessConstant.CHESSBOARD_MARGIN + lastStep.getFrom()
							.getY() * ChessConstant.GRID_WIDTH - ChessConstant.GRID_WIDTH / 2,
					this);
			g2.drawImage(imgSelected2, ChessConstant.CHESSBOARD_MARGIN + lastStep.getTo()
					.getX() * ChessConstant.GRID_WIDTH - ChessConstant.GRID_WIDTH / 2,
					ChessConstant.CHESSBOARD_MARGIN + lastStep.getTo()
							.getY() * ChessConstant.GRID_WIDTH - ChessConstant.GRID_WIDTH / 2,
					this);
		}

		// 画可移动线路图
		if (controller.moveIndicitorList.size() > 0)
		{
			g2.setColor(Color.BLUE);
			for (int i = 0; i < controller.moveIndicitorList.size(); i++)
			{
				ChessPoint chessPoint = controller.moveIndicitorList.get(i);

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	// 消除画图锯齿
				g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);		// 追求速度或质量
				// g2.fillArc(ChessConstant.GRID_WIDTH + column * ChessConstant.GRID_WIDTH - 5, ChessConstant.GRID_WIDTH
				// + row * ChessConstant.GRID_WIDTH - 5, 10, 10, 0, 360);
				g2.fillOval(
						ChessConstant.CHESSBOARD_MARGIN + chessPoint.getX() * ChessConstant.GRID_WIDTH
								- ChessConstant.RADIUS / 2,    //
						ChessConstant.CHESSBOARD_MARGIN + chessPoint.getY() * ChessConstant.GRID_WIDTH
								- ChessConstant.RADIUS / 2,  //
						ChessConstant.RADIUS,   // r
						ChessConstant.RADIUS);   // r
			}
		}
	}

	private static void getDirectionLine(List<ChessPoint> list, int width, int height, Graphics2D g2)
	{
		ChessPoint startPoint = list.get(0);
		ChessPoint endPoint = list.get(1);
		int sx = (int) (startPoint.getX() * width);
		int sy = (int) (startPoint.getY() * height);
		int ex = (int) (endPoint.getX() * width);
		int ey = (int) (endPoint.getY() * height);
		drawAL(sx, sy, ex, ey, g2);
	}

	/**
	 * 画箭头
	 */
	private static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2)
	{
		double H = ChessConstant.ARROW_HEIGHT; // 箭头高度
		double L = ChessConstant.ARROW_LENGTH; // 底边的一半
		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H); // 箭头角度
		double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
		double y_4 = ey - arrXY_2[1];

		Double X3 = Double.valueOf(x_3);
		x3 = X3.intValue();
		Double Y3 = Double.valueOf(y_3);
		y3 = Y3.intValue();
		Double X4 = Double.valueOf(x_4);
		x4 = X4.intValue();
		Double Y4 = Double.valueOf(y_4);
		y4 = Y4.intValue();
		g2.setColor(Color.RED);
		// 起始线
		g2.drawLine(sx, sy, ex, ey);
		// 箭头
		g2.drawLine(ex, ey, x3, y3);
		g2.drawLine(ex, ey, x4, y4);
	}

	// 计算
	private static double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen)
	{
		double mathstr[] = new double[2];
		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen)
		{
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}

	private void drawICCSNumbers(Graphics2D g2)
	{
		g2.setColor(Color.BLUE);    // 设置画笔颜色
		Font font = new Font("黑体", Font.PLAIN, 25);
		g2.setFont(font);  // 设置字体

		// 绘制上方横坐标
		for (int i = 0; i < 9; i++)
		{
			FontMetrics fm = g2.getFontMetrics(font);
			// 字符串绘制宽度
			int width = fm.stringWidth(ChessController.iccsHorizontalNumbers[i]);
			g2.drawString(ChessController.iccsHorizontalNumbers[i],
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH - width / 2,
					ChessConstant.CHESSBOARD_MARGIN / 2 + ChessConstant.GRID_WIDTH / 2);
		}
		// 绘制下方横坐标
		for (int i = 0; i < 9; i++)
		{
			FontMetrics fm = g2.getFontMetrics(font);
			// 字符串绘制宽度
			int width = fm.stringWidth(ChessController.iccsHorizontalNumbers[i]);
			g2.drawString(ChessController.iccsHorizontalNumbers[i],
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH - width / 2,
					ChessConstant.CHESSBOARD_MARGIN + ChessConstant.GRID_WIDTH * 9 + ChessConstant.GRID_WIDTH);
		}
		// 绘制竖坐标
		for (int i = 0; i <= 9; i++)
		{
			FontMetrics fm = g2.getFontMetrics(font);
			// 字符串绘制宽度
			int width = fm.stringWidth(ChessController.iccsVerticalNumbers[i]);
			g2.drawString(ChessController.iccsVerticalNumbers[9 - i], ChessConstant.CHESSBOARD_MARGIN / 2,
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH + width / 2);
		}
	}

	private void drawICoordinates(Graphics2D g2)
	{
		g2.setColor(Color.BLUE); // 设置画笔颜色
		Font font = new Font("黑体", Font.PLAIN, 25);
		g2.setFont(font); // 设置字体

		// 绘制上方横坐标
		for (int i = 0; i < 9; i++)
		{
			FontMetrics fm = g2.getFontMetrics(font);
			// 字符串绘制宽度
			int width = fm.stringWidth(xIndex[i]);
			g2.drawString(xIndex[i], ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH - width / 2,
					ChessConstant.CHESSBOARD_MARGIN / 2 + ChessConstant.GRID_WIDTH / 2);
		}
		// 绘制下方横坐标
		for (int i = 0; i < 9; i++)
		{
			FontMetrics fm = g2.getFontMetrics(font);
			// 字符串绘制宽度
			int width = fm.stringWidth(xIndex[i]);
			g2.drawString(xIndex[i], ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH - width / 2,
					ChessConstant.CHESSBOARD_MARGIN + ChessConstant.GRID_WIDTH * 9 + ChessConstant.GRID_WIDTH);
		}
		// 绘制竖坐标
		for (int i = 0; i <= 9; i++)
		{
			FontMetrics fm = g2.getFontMetrics(font);
			// 字符串绘制宽度
			int width = fm.stringWidth(yIndex[i]);
			g2.drawString(yIndex[i], ChessConstant.CHESSBOARD_MARGIN / 2,
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH + width / 2);
		}
	}

	/**
	 * 画棋盘刻度线
	 */
	private void drawNumbers(Graphics2D g2)
	{
		g2.setColor(Color.BLACK);    // 设置画笔颜色
		Font font = new Font("黑体", Font.PLAIN, 25);
		g2.setFont(font);  // 设置字体

		// 绘制黑方坐标
		for (int i = 0; i < 9; i++)
		{
			FontMetrics fm = g2.getFontMetrics(font);
			// 字符串绘制宽度
			int width = fm.stringWidth(controller.blackMarkNumbers[i]);
			// 画字符串，x坐标即字符串左边位置，y坐标是指baseline的y坐标，即字体所在矩形的左上角y坐标+ascent
			g2.drawString(controller.blackMarkNumbers[i],
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH - width / 2,
					ChessConstant.CHESSBOARD_MARGIN / 2 + ChessConstant.GRID_WIDTH / 2);
		}
		// 绘制红方坐标
		for (int i = 0; i < 9; i++)
		{
			FontMetrics fm = g2.getFontMetrics(font);
			// 字符串绘制宽度
			int width = fm.stringWidth(controller.redMarkNumbers[i]);
			g2.drawString(controller.redMarkNumbers[i],
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH - width / 2,
					ChessConstant.CHESSBOARD_MARGIN + ChessConstant.GRID_WIDTH * 9
							+ ChessConstant.CHESSBOARD_MARGIN / 2);
		}
	}

	/**
	 * 画棋盘线
	 */
	private void drawGrid(Graphics2D g2)
	{
		g2.setColor(Color.BLACK);    // 设置画笔颜色
		g2.setStroke(new BasicStroke(2.0f));
		Font f = new Font("微软雅黑", Font.BOLD, 30);
		g2.setFont(f);
		g2.setBackground(new Color(128, 118, 105));
		// 画外面的框
		g2.drawRect(ChessConstant.CHESSBOARD_MARGIN, ChessConstant.CHESSBOARD_MARGIN, 448, 504);
		// 画横线
		for (int i = 0; i <= 9; i++)
		{
			g2.drawLine(ChessConstant.CHESSBOARD_MARGIN, ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH,
					ChessConstant.CHESSBOARD_MARGIN + ChessConstant.GRID_WIDTH * 8,
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH);
		}
		// 画中间的文字
		Font f2 = new Font("楷体", Font.PLAIN, 32);
		g2.setFont(f2);
		g2.drawString("楚河", ChessConstant.CHESSBOARD_MARGIN + ChessConstant.GRID_WIDTH, ChessConstant.CHESSBOARD_MARGIN
				+ 4 * ChessConstant.GRID_WIDTH + (int) (ChessConstant.GRID_WIDTH * 2 / 3));
		g2.drawString("汉界", ChessConstant.CHESSBOARD_MARGIN + ChessConstant.GRID_WIDTH * 6,
				ChessConstant.CHESSBOARD_MARGIN + 4 * ChessConstant.GRID_WIDTH
						+ (int) (ChessConstant.GRID_WIDTH * 2 / 3));
		// 画竖线
		// 上半部分
		for (int i = 0; i < 9; i++)
		{
			g2.drawLine(ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH, ChessConstant.CHESSBOARD_MARGIN,
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH,
					ChessConstant.CHESSBOARD_MARGIN + 4 * ChessConstant.GRID_WIDTH);
		}
		// 下半部分
		for (int i = 0; i < 9; i++)
		{
			g2.drawLine(ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH,
					ChessConstant.CHESSBOARD_MARGIN + 5 * ChessConstant.GRID_WIDTH,
					ChessConstant.CHESSBOARD_MARGIN + i * ChessConstant.GRID_WIDTH,
					ChessConstant.CHESSBOARD_MARGIN + 9 * ChessConstant.GRID_WIDTH);
		}
		// 画九宫格斜线
		// 上半部分
		g2.drawLine(ChessConstant.CHESSBOARD_MARGIN + 3 * ChessConstant.GRID_WIDTH, ChessConstant.CHESSBOARD_MARGIN,
				ChessConstant.CHESSBOARD_MARGIN + 5 * ChessConstant.GRID_WIDTH,
				ChessConstant.CHESSBOARD_MARGIN + 2 * ChessConstant.GRID_WIDTH);
		g2.drawLine(ChessConstant.CHESSBOARD_MARGIN + 5 * ChessConstant.GRID_WIDTH, ChessConstant.CHESSBOARD_MARGIN,
				ChessConstant.CHESSBOARD_MARGIN + 3 * ChessConstant.GRID_WIDTH,
				ChessConstant.CHESSBOARD_MARGIN + 2 * ChessConstant.GRID_WIDTH);
		// 下半部分
		g2.drawLine(ChessConstant.CHESSBOARD_MARGIN + 3 * ChessConstant.GRID_WIDTH,
				ChessConstant.CHESSBOARD_MARGIN + 7 * ChessConstant.GRID_WIDTH,
				ChessConstant.CHESSBOARD_MARGIN + 5 * ChessConstant.GRID_WIDTH,
				ChessConstant.CHESSBOARD_MARGIN + 9 * ChessConstant.GRID_WIDTH);
		g2.drawLine(ChessConstant.CHESSBOARD_MARGIN + 5 * ChessConstant.GRID_WIDTH,
				ChessConstant.CHESSBOARD_MARGIN + 7 * ChessConstant.GRID_WIDTH,
				ChessConstant.CHESSBOARD_MARGIN + 3 * ChessConstant.GRID_WIDTH,
				ChessConstant.CHESSBOARD_MARGIN + 9 * ChessConstant.GRID_WIDTH);
	}

	/**
	 * 通过向引擎发送当前局面信息和历史走法，来获取引擎给出的最佳着法
	 */
	protected void thinking()
	{
		// 找出黑方的最佳走法
		String bestMove = findBestMove(controller.pieceArray);
		log.info("引擎返回的最佳下法为：" + bestMove + ", controller.redMove=" + controller.redMove);
		if (bestMove == null)
		{
			new MessageFrame("棋局已结束！");
			return;
		}
		// 黑方走棋
		if (controller.redMove == false)
		{
			String startPoint = bestMove.substring(0, 2);
			int lastY = (9 - Integer.valueOf(startPoint.substring(1, startPoint.length())));
			int lastX = controller.findIndexOf(startPoint.substring(0, 1), ChessController.iccsHorizontalNumbers);
			// log.info("lastY=" + lastY + ",lastX=" + lastX);

			String endPoint = bestMove.substring(2, 4);
			int currentY = (9 - Integer.valueOf(endPoint.substring(1, endPoint.length())));
			int currentX = controller.findIndexOf(endPoint.substring(0, 1), ChessController.iccsHorizontalNumbers);

			// 当前位置有对方的棋子，则表示发生了吃子
			boolean eatFlag = checkIfEat(controller.pieceArray[currentY][currentX]);
			if (eatFlag)
			{
				if (controller.redMove)
				{
					ChessAudio.play(ChessAudio.MAN_EAT);
				}
				else
				{
					if (controller.pieceArray[currentY][currentX].equals("rk.gif"))
					{
						new MessageFrame("你输了！");
						ChessAudio.play(ChessAudio.BE_CHECKMATED_BY_COM);
					}
					else
					{
						ChessAudio.play(ChessAudio.COM_EAT);
					}
				}
			}
			else
			{
				ChessAudio.play(ChessAudio.MAN_MOVE);
			}

			// 根据引擎返回的信息，更新棋盘
			// 上一次点击的棋子，移动到当前位置
			controller.pieceArray[currentY][currentX] = controller.pieceArray[lastY][lastX];
			// 再把上一个位置的棋子置为空
			controller.pieceArray[lastY][lastX] = "";

			controller.currentPoint.setLocation(currentX, currentY);
			// log.info("controller.lastPoint=(" + (int) controller.lastPoint.getX() + "," + (int)
			// controller.lastPoint.getY() +
			// ")"
			// + ", controller.currentPoint=(" + (int) controller.currentPoint.getX() + "," + (int)
			// controller.currentPoint.getY()
			// + ")");
			loadPieces();
			repaint();
			log.info("【黑方落子】：lastPoint=(" + lastY + "," + lastX + ")" + "--->" + "currentPoint=(" + currentY + ","
					+ currentX + ")，是否吃子=" + eatFlag + "，是否将军=" + isChecked() + "，轮到红方下棋了！");

			if (isChecked())
			{
				new MessageFrame("将军！");
				ChessAudio.play(ChessAudio.COM_CHECK);
			}

			// 记录棋谱
			Record record = new Record();
			String chessName = controller.getChineseChess(controller.pieceArray[currentY][currentX]);
			// log.info("chessName=" + chessName + ",redMove=" + controller.redMove);
			record.setChessName(chessName);
			record.setRedMove(controller.redMove);
			Step step = new Step(new ChessPoint(lastX, lastY), new ChessPoint(currentX, currentY));
			record.setChessStep(step);
			record.setEatFlag(eatFlag);
			stepList.add(record);

			// 交换红黑标记
			controller.redMove = !controller.redMove;

			showSteps();
		}
		else
		{
			log.info("现在是红方走棋！");
		}
	}

	/**
	 * 找出最佳着法
	 */
	protected String findBestMove(String[][] pieceArray)
	{
		// 找出棋盘上的黑方棋子，然后随机选取一个棋子，随机走出一步
		StringBuffer movesString = new StringBuffer("moves ");
		boolean hasEat = false;
		StringBuffer moveList = new StringBuffer("");
		int n = 0;
		for (int i = 0; i < stepList.size(); i++)
		{
			Record record = stepList.get(i);
			Step step = record.getChessStep();
			ChessPoint from = step.getFrom();
			ChessPoint to = step.getTo();
			hasEat = record.isEatFlag();
			if (hasEat)
			{
				moveList.setLength(0);
				n = 0;
			}
			else
			{
				String moveString = ChessController.chessMap.get((9 - from.getY()) + "_" + from.getX())
						+ ChessController.chessMap.get((9 - to.getY()) + "_" + to.getX());
				moveList.append(moveString)
						.append(" ");
				moveList.append("");
				n++;
			}
		}
		movesString.append(moveList);
		String moveFen;
		if (hasEat)
		{
			moveFen = "position fen " + controller.toFen(controller.pieceArray) + " - - " + noEatMoves + " 1";
		}
		else
		{
			moveFen = "position fen " + controller.toFen(controller.pieceArray) + " - - " + noEatMoves + " " + n + " "
					+ movesString;
		}
		log.info("\n>>>" + moveFen);
		String bestMove = null;
		try
		{
			bestMove = UcciEngine.getBestMove(moveFen);
		}
		catch (IOException e)
		{
			log.info("获取引擎走法出错：" + e.getMessage());
			e.printStackTrace();
		}
		// log.info("获取引擎走法成功，bestMove=" + bestMove);
		engineTextArea.setText(Utils.readFromFile() + "\n");
		engineTextArea.setCaretPosition(engineTextArea.getText()
				.length());
		return bestMove;
	}

	protected void showSteps()
	{
		log.info("┏------------------------------开始--------------------------------------┓");
		log.info("当前棋局步骤为：");
		// 输出坐标变化
		for (int i = 0; i < stepList.size(); i++)
		{
			Record record = stepList.get(i);
			Step step = record.getChessStep();
			ChessPoint from = step.getFrom();
			ChessPoint to = step.getTo();
			// 打印坐标变化
			System.out.print("    " + "(" + from.getY() + "," + from.getX() + ")" + "--->" + "(" + to.getY() + ","
					+ to.getX() + ")");
			// 打印iccs坐标变化
			System.out.print("\t iccs: " + ChessController.chessMap.get((9 - from.getY()) + "_" + from.getX()) + "-->"
					+ ChessController.chessMap.get((9 - to.getY()) + "_" + to.getX()) + "\n");
		}
		System.out.println();
		// 输出中文棋谱
		StringBuffer recordString = new StringBuffer("");
		for (int i = 0; i < stepList.size(); i++)
		{
			Record record = stepList.get(i);
			Step step = record.getChessStep();
			ChessPoint from = step.getFrom();
			ChessPoint to = step.getTo();
			// 用汉字记录象棋棋谱的基本规则：
			// 第一个字表示需要移动的棋子。
			// 第二个字表示移动的棋子所在的直线编码（红黑方均为由己方底线从右向左数），红方用汉字，黑方用阿拉伯数字表示。
			// 当同一直线上有两个相同的棋子，则采用前、后来区别，如“后车平四”、“前马进7”。
			// 第三个字表示棋子移动的方向，横走用”平“，向对方底线前进用”进“，向己方底线后退用”退“。
			// 第四个字分为两类：棋子在直线上进退时，表示棋子进退的步数；当棋子平走或斜走的时候，表示所到达直线的编号。
			String cnChessValue = record.getChessName();
			// 1、棋子名称
			StringBuffer sb = new StringBuffer();
			sb.append(cnChessValue);
			// 2、棋子起点竖线坐标
			if (record.isRedMove())
			{
				sb.append(controller.redMarkNumbers[from.getX()]);
			}
			else
			{
				sb.append(controller.blackMarkNumbers[from.getX()]);
			}
			// 3、棋子移动方向：进退平
			if (record.isRedMove())
			{
				if (from.getY() > to.getY())
				{
					// 进
					sb.append(controller.directionNumbers[0]);
				}
				else if (from.getY() < to.getY())
				{
					// 退
					sb.append(controller.directionNumbers[1]);
				}
				else
				{
					// 平
					sb.append(controller.directionNumbers[2]);
				}
			}
			else
			{
				if (from.getY() < to.getY())
				{
					// 进
					sb.append(controller.directionNumbers[0]);
				}
				else if (from.getY() > to.getY())
				{
					// 退
					sb.append(controller.directionNumbers[1]);
				}
				else
				{
					// 平
					sb.append(controller.directionNumbers[2]);
				}
			}
			// 4、棋子直线进退时，计算进退步数；棋子平走或者斜线时， 表示到达的棋子竖线编号
			// 直线进退
			if (Math.abs(to.getX() - from.getX()) == 0)
			{
				if (record.isRedMove())
				{
					sb.append(controller.getChineseNumber(Math.abs(to.getY() - from.getY())));
				}
				else
				{
					sb.append(Math.abs(to.getY() - from.getY()));
				}
			}
			// 棋子平走或者斜着走
			if (Math.abs(to.getY() - from.getY()) == 0
					|| ((Math.abs(from.getX() - to.getX()) == 2 && Math.abs(from.getY() - to.getY()) == 1)
							|| (Math.abs(from.getX() - to.getX()) == 1 && Math.abs(from.getY() - to.getY()) == 2)
							|| (Math.abs(from.getX() - to.getX()) == 2 && Math.abs(from.getY() - to.getY()) == 2)
							|| (Math.abs(from.getX() - to.getX()) == 1 && Math.abs(from.getY() - to.getY()) == 1)))
			{
				if (record.isRedMove())
				{
					sb.append(controller.redMarkNumbers[to.getX()]);
				}
				else
				{
					sb.append(controller.blackMarkNumbers[to.getX()]);
				}
			}
			if (i % 2 == 0)
			{
				recordString.append("(" + (int) (i / 2 + 1) + ")");
			}
			recordString.append(sb.toString() + "  ");
			System.out.print(sb.toString() + "\t");
			if (i % 2 == 1)
			{
				System.out.println();
				recordString.append("\n");
			}
		}
		log.info("\n┗-------------------------------结束-------------------------------------┛\n\n");
		stepTextArea.setText(recordString.toString());
		stepTextArea.setCaretPosition(stepTextArea.getText()
				.length());
	}
}
