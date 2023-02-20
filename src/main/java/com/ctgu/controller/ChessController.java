package com.ctgu.controller;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctgu.model.ChessPoint;

public class ChessController
{
	// 最初局面，FEN格式串http://www.xqbase.com/protocol/cchess_fen.htm
	public static final String INIT_BOARD_FEN = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1";

	// 黑方坐标
	public String[] blackMarkNumbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	// 红方坐标
	public String[] redMarkNumbers = { "九", "八", "七", "六", "五", "四", "三", "二", "一" };
	// iccs格式的横坐标
	public static String[] iccsHorizontalNumbers = { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
	// iccs的纵坐标
	public static String[] iccsVerticalNumbers = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	
	public String[] directionNumbers = { "进", "退", "平" };

	// 初始棋子pieceArray[10][9]
	public String[][] pieceArray = //
			{ //
					{ "br.gif", "bn.gif", "bb.gif", "ba.gif", "bk.gif", "ba.gif", "bb.gif", "bn.gif", "br.gif" }, // 1
					{ "", "", "", "", "", "", "", "", "" },// 2
					{ "", "bc.gif", "", "", "", "", "", "bc.gif", "" },// 3
					{ "bp.gif", "", "bp.gif", "", "bp.gif", "", "bp.gif", "", "bp.gif" },// 4
					{ "", "", "", "", "", "", "", "", "" },// 5A
					{ "", "", "", "", "", "", "", "", "" },// 6
					{ "rp.gif", "", "rp.gif", "", "rp.gif", "", "rp.gif", "", "rp.gif" },// 7
					{ "", "rc.gif", "", "", "", "", "", "rc.gif", "" },// 8
					{ "", "", "", "", "", "", "", "", "" },// 9
					{ "rr.gif", "rn.gif", "rb.gif", "ra.gif", "rk.gif", "ra.gif", "rb.gif", "rn.gif", "rr.gif" }// 10
			};

	public BufferedImage[][] pieceImageArray = new BufferedImage[10][9];

	public final String[] BOARD_NAME = { "background.jpg", "canvas.png", "drops.png", "green.png", "qianhong.png",
			"sheet.png", "white.png", "wood.png" };

	public final String[] PIECES_NAME = { "wood", "delicate", "polish" };

	public boolean redMove = true;

	// 棋盘坐标与iccs坐标的转换
	public static Map<String, String> chessMap = new LinkedHashMap<>(90);

	// 用于绘制点击的位置
	public Point lastPoint = new Point(-1, -1);

	public Point currentPoint = new Point(-1, -1);

	// 棋子可移动路线图
	public List<ChessPoint> moveIndicitorList = new ArrayList<>();

	static
	{
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				// chessMap.put("[" + y + "][" + x + "]", iccsHorizontalNumbers[x] + y);
				chessMap.put(y + "_" + x, iccsHorizontalNumbers[x] + y);
			}
		}
		// System.out.println("chessMap=" + chessMap);
	}

	// 清空棋盘
	// public void clearBoard()
	// {
	// for (int y = 0; y < 10; y++)
	// {
	// for (int x = 0; x < 9; x++)
	// {
	// pieceImageArray[y][x] = "";
	// }
	// }
	// }

	public boolean hasEat(String[][] chessArray)
	{
		int k = 0;
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				if (chessArray[y][x].length() > 0)
				{
					k++;
				}
			}
		}
		if (k < 32)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean checkIfRedChess(String pieceArrayValue)
	{
		if (pieceArrayValue.startsWith("r"))
		{
			return true;
		}
		return false;
	}

	public static boolean checkIfBlackChess(String pieceArrayValue)
	{
		if (pieceArrayValue.startsWith("b"))
		{
			return true;
		}
		return false;
	}

	/**
	 * 判断是否已经有了胜利者
	 */
	public boolean hasWinner(String[][] chessArray)
	{
		if (redMove)
		{
			// 若当前是红棋走棋
			int flag = 0;
			for (int y = 0; y < 10; y++)
			{
				for (int x = 0; x < 9; x++)
				{
					String chessValue = chessArray[y][x];
					if (chessValue.startsWith("bk"))
					{
						flag = 1;
					}
				}
			}
			if (flag == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			// 轮到黑棋走棋
			int flag = 0;
			for (int y = 0; y < 10; y++)
			{
				for (int x = 0; x < 9; x++)
				{
					String chessValue = chessArray[y][x];
					if (chessValue.startsWith("rk"))
					{
						flag = 1;
					}
				}
			}
			if (flag == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	/**
	 * @Title: checkCanMove
	 * @Description: 判断能否落子
	 * @param lastPoint
	 * @param currentPoint
	 * @param
	 * @return boolean
	 */
	public boolean checkWhetherCanMove(String chessValue, Point startPoint, Point endPoint)
	{
		int lastX = (int) startPoint.getX();
		int lastY = (int) startPoint.getY();
		int currentX = (int) endPoint.getX();
		int currentY = (int) endPoint.getY();
		// 棋子
		if (lastX < 0 || lastY < 0 || currentX < 0 || currentY < 0)
		{
			return false;
		}
		// 红方走棋
		if (redMove)
		{
			// 当前位置有红方棋子，不允许走棋
			if (pieceArray[currentY][currentX].startsWith("r"))
			{
				// 更新棋子标记
				// System.out.println("当前位置有红方棋子，不允许走棋！");
				return false;
			}
			// 起始位置不是红方棋子，不允许走棋
			if (pieceArray[lastY][lastX].startsWith("r") == false)
			{
				// System.out.println("现在是红方走棋！");
				return false;
			}
			// 符合规则
			if (checkRule(chessValue, startPoint, endPoint))
			{
				return true;
			}
			else
			{
				// System.out.println("走法不符合规则！");
				return false;
			}
		}
		else
		{
			// 当前位置有黑方棋子，不允许走棋
			if (pieceArray[currentY][currentX].startsWith("b"))
			{
				// 更新棋子标记
				// System.out.println("当前位置有黑方棋子，不允许走棋！");
				return false;
			}
			// 起始位置不是黑方棋子，不允许走棋
			if (pieceArray[lastY][lastX].startsWith("b") == false)
			{
				// System.out.println("现在是黑方走棋！");
				return false;
			}
			// 符合规则
			if (checkRule(chessValue, startPoint, endPoint))
			{
				// Step step = new Step(new ChessPoint(lastPoint), new ChessPoint(currentPoint));
				// stepList.add(step);
				// System.out.println("当前轮到红方下棋了！");
				return true;
			}
			else
			{
				// System.out.println("走法不符合规则！");
				return false;
			}
		}
	}

	/**
	 * 将FEN串转化为一维棋局数组<br>
	 * 通过FEN串初始化棋局，也就是将参数fen表示的棋局，<br>
	 * 转化为一维棋局数组squares表示的棋局。
	 */
	public void fromFen(String fen)
	{
		// clearBoard();
	}

	/**
	 * 一维数组转化为字符串
	 */
	public String toFen(String[][] chessArray)
	{
		StringBuffer fen = new StringBuffer();
		for (int y = 0; y < 10; y++)
		{
			// 连续空格的个数
			int k = 0;
			for (int x = 0; x < 9; x++)
			{
				String chessValue = chessArray[y][x];
				if (chessValue.length() > 0)
				{// 有棋子，如果连续空格数大于1，则需要先拼接空格数，再拼接棋子数
					if (k > 0)
					{
						fen.append(k);
					}
					// 黑方
					if (chessValue.startsWith("b"))
					{
						fen.append(chessValue.substring(1, 2));
					}
					else if (chessValue.startsWith("r"))
					{
						fen.append(chessValue.substring(1, 2).toUpperCase());
					}
					k = 0;
				}
				else
				{// 没有棋子，则累加连续空格数
					k++;
				}
			}
			if (k > 0)
			{
				fen.append(k);
			}
			fen.append('/');
		}
		fen.deleteCharAt(fen.length() - 1);
		fen.append(" ");
		fen.append(redMove == true ? 'w' : 'b');
		return fen.toString();
	}

	/**
	 * 判断能否走棋
	 */
	public boolean checkRule(String chessValue, Point lastPoint, Point currentPoint)
	{
		int lastX = (int) lastPoint.getX();
		int lastY = (int) lastPoint.getY();
		int currentX = (int) currentPoint.getX();
		int currentY = (int) currentPoint.getY();
		// 终点位置的棋子
		// String chessValue = pieceArray[lastY][lastX];
		String currentPiece = pieceArray[currentY][currentX];
		// 1、落子位置不能有本方棋子
		if ((chessValue.startsWith("rr") && currentPiece.startsWith("r"))
				|| (chessValue.startsWith("br") && currentPiece.startsWith("b")))
		{
			// System.out.println("当前走法不符合象棋基本走棋规则：落子位置不能有本方棋子！");
			return false;
		}
		// 车的走棋规则：1、起点和落子点中间没有棋子；2、棋子走的是直线
		if (chessValue.startsWith("rr") || chessValue.startsWith("br"))
		{
			// System.out.println("你点击了【车】");
			// 红方走棋判断
			// 1、不能走斜线
			if (Math.abs(lastX - currentX) != 0 && Math.abs(lastY - currentY) != 0)
			{
				// System.out.println("当前走法不符合【车】的走棋规则：必须走直线，且中间无棋子阻拦！");
				return false;
			}
			// 2、横着走时，中间不能有棋子
			if (lastX < currentX)
			{
				// 从左往右走
				for (int x = lastX + 1; x < currentX; x++)
				{
					if (pieceArray[currentY][x].length() > 0)
					{
						// System.out.println("当前走法不符合【车】的走棋规则：必须走直线，且中间无棋子阻拦！");
						return false;
					}
				}
			}
			else
			{// 从右往左走
				for (int x = currentX + 1; x < lastX; x++)
				{
					if (pieceArray[currentY][x].length() > 0)
					{
						// System.out.println("当前走法不符合【车】的走棋规则：必须走直线，且中间无棋子阻拦！");
						return false;
					}
				}
			}
			// 3、竖着走时，中间不能有棋子
			if (lastY < currentY)
			{
				// 从上往下走
				for (int y = lastY + 1; y < currentY; y++)
				{
					if (pieceArray[y][lastX].length() > 0)
					{
						// System.out.println("当前走法不符合【车】的走棋规则：必须走直线，且中间无棋子阻拦！");
						return false;
					}
				}
			}
			else
			{
				// 从上往下走
				for (int y = currentY + 1; y < lastY; y++)
				{
					if (pieceArray[y][lastX].length() > 0)
					{
						// System.out.println("当前走法不符合【车】的走棋规则：必须走直线，且中间无棋子阻拦！");
						return false;
					}
				}
			}
			// 4、 吃子的时候，只能吃掉对方棋子
			// 红方
			if (chessValue.startsWith("rr"))
			{
				if (pieceArray[currentY][currentX].startsWith("r"))
				{
					return false;
				}
			}
			// 黑方
			if (chessValue.startsWith("br"))
			{
				if (pieceArray[currentY][currentX].startsWith("b"))
				{
					return false;
				}
			}
			return true;
		}
		// 马
		else if (chessValue.startsWith("rn") || chessValue.startsWith("bn"))
		{
			// System.out.println("你点击了【马】");
			// 马的走棋规则：1、横向或者纵向走2个格子，且纵向或横向只走一个格子；2、前进方向的位置不能有棋子（别马腿）
			// 横向走
			if (Math.abs(lastX - currentX) == 2 && Math.abs(lastY - currentY) == 1)
			{
				// 左上和左下 右上和右下，分别判断有没有被别马腿
				if (pieceArray[lastY][(lastX + currentX) / 2].length() == 0)
				{
					return true;
				}
				else
				{
					// System.out.println("当前走法不符合【马】的走棋规则：必须同时横向纵向走两格，且没有被别马腿！");
					return false;
				}
			}
			// 纵向走
			if (Math.abs(lastX - currentX) == 1 && Math.abs(lastY - currentY) == 2)
			{
				// 左上和右上 左下和右下，分别判断有没有被别马腿
				if (pieceArray[(lastY + currentY) / 2][lastX].length() == 0)
				{
					return true;
				}
				else
				{
					// System.out.println("当前走法不符合【马】的走棋规则：必须同时横向纵向走两格，且没有被别马腿！");
					return false;
				}
			}
			// System.out.println("当前走法不符合【马】的走棋规则：必须同时横向纵向走两格，且没有被别马腿！");
			return false;
		}
		// 炮
		else if (chessValue.startsWith("rc") || chessValue.startsWith("bc"))
		{
			// System.out.println("你点击了【炮】");
			// 炮的走棋规则：1、中间没有棋子；2、棋子走的是直线；3、吃子时中间必须间隔一个棋子
			if (Math.abs(lastX - currentX) != 0 && Math.abs(lastY - currentY) != 0)
			{
				// System.out.println("当前走法不符合【炮】的走棋规则：炮必须走直线！");
				return false;
			}
			if (pieceArray[(int) currentPoint.getY()][(int) currentPoint.getX()].length() == 0)
			{
				// 往上
				for (int y = (int) currentPoint.getY(); y <= (int) lastPoint.getY() - 1; y++)
				{
					if (pieceArray[y][(int) lastPoint.getX()].length() > 0)
					{
						// System.out.println("当前走法不符合【炮】的走棋规则：移动路径上有棋子！");
						return false;
					}
				}
				// 往下
				for (int y = (int) lastPoint.getY() + 1; y <= (int) currentPoint.getY(); y++)
				{
					if (pieceArray[y][(int) lastPoint.getX()].length() > 0)
					{
						// System.out.println("当前走法不符合【炮】的走棋规则：移动路径上有棋子！");
						return false;
					}
				}
				// 往左
				for (int x = (int) currentPoint.getX(); x <= (int) lastPoint.getX() - 1; x++)
				{
					if (pieceArray[(int) lastPoint.getY()][x].length() > 0)
					{
						// System.out.println("当前走法不符合【炮】的走棋规则：移动路径上有棋子！");
						return false;
					}
				}
				// 往右
				for (int x = (int) lastPoint.getX() + 1; x <= (int) currentPoint.getX(); x++)
				{
					if (pieceArray[(int) lastPoint.getY()][x].length() > 0)
					{
						// System.out.println("当前走法不符合【炮】的走棋规则：移动路径上有棋子！");
						return false;
					}
				}
				return true;
			}
			else
			{// 炮吃子：中间要有一个子
				// 往上
				int upCount = 0;
				for (int y = (int) currentPoint.getY() + 1; y <= (int) lastPoint.getY() - 1; y++)
				{
					if (pieceArray[y][(int) lastPoint.getX()].length() > 0)
					{
						upCount++;
					}
				}
				if (upCount == 1)
				{
					return true;
				}
				// 往下
				int downCount = 0;
				for (int y = (int) lastPoint.getY() + 1; y <= (int) currentPoint.getY() - 1; y++)
				{
					if (pieceArray[y][(int) lastPoint.getX()].length() > 0)
					{
						downCount++;
					}
				}
				if (downCount == 1)
				{
					return true;
				}
				// 往左
				int leftCount = 0;
				for (int x = (int) currentPoint.getX() + 1; x <= (int) lastPoint.getX() - 1; x++)
				{
					if (pieceArray[(int) lastPoint.getY()][x].length() > 0)
					{
						leftCount++;
					}
				}
				if (leftCount == 1)
				{
					return true;
				}
				// 往右
				int rightCount = 0;
				for (int x = (int) lastPoint.getX() + 1; x <= (int) currentPoint.getX() - 1; x++)
				{
					if (pieceArray[(int) lastPoint.getY()][x].length() > 0)
					{
						rightCount++;
					}
				}
				if (rightCount == 1)
				{
					return true;
				}
			}
			// System.out.println("当前走法不符合【炮】的走棋规则：中间要有一个棋子！");
			return false;
		}
		// 相（象）
		else if (chessValue.startsWith("rb") || chessValue.startsWith("bb"))
		{
			// System.out.println("你点击了【相】");
			// 象的走棋规则：1、往x方向走两格，且往y方向走两格；2、象不能过河；3、象眼不能被堵
			if ((Math.abs(lastX - currentX) == 2 && Math.abs(lastY - currentY) == 2)  //
					&& ((chessValue.startsWith("rb") && (int) currentPoint.getY() >= 5)
							|| (chessValue.startsWith("bb") && (int) currentPoint.getY() <= 4))// 红相或黑象不能过河
					&& (pieceArray[(currentY + lastY) / 2][(currentX + lastX) / 2].length() == 0)// 象眼不能被堵
			)
			{
				return true;
			}
			else
			{
				// System.out.println("当前走法不符合【相（象）】的走棋规则：走田字，不能过河，象眼畅通！");
				return false;
			}
		}
		// 仕（士）
		else if (chessValue.startsWith("ra") || chessValue.startsWith("ba"))
		{
			// System.out.println("你点击了【仕】");
			// 仕（士）的走棋规则：x或y方向同时走一格，且不出九宫
			if ((Math.abs(lastX - currentX) == 1 && Math.abs(lastY - currentY) == 1)  // x和y方向同时走一格
					&& ((chessValue.startsWith("ra") && (int) currentPoint.getX() >= 3 && (int) currentPoint.getX() <= 5
							&& (int) currentPoint.getY() >= 7 && (int) currentPoint.getY() <= 9)
							|| (chessValue.startsWith("ba") && (int) currentPoint.getX() >= 3
									&& (int) currentPoint.getX() <= 5 && (int) currentPoint.getY() >= 0
									&& (int) currentPoint.getY() <= 2))//
			)
			{
				return true;
			}
			else
			{
				// System.out.println("当前走法不符合【仕（士）】的走棋规则：仕（士）必须斜着走，且只能在九宫内！");
				return false;
			}
		}
		// 帅（将）
		else if (chessValue.startsWith("rk") || chessValue.startsWith("bk"))
		{
			// System.out.println("你点击了【帅】");
			if (((Math.abs(lastX - currentX) == 1 && Math.abs(lastY - currentY) == 0)
					|| (Math.abs(lastX - currentX) == 0 && Math.abs(lastY - currentY) == 1)) // 一次走一格
					&& ((chessValue.startsWith("rk") && (int) currentPoint.getX() >= 3 && (int) currentPoint.getX() <= 5
							&& (int) currentPoint.getY() >= 7 && (int) currentPoint.getY() <= 9)
							|| (chessValue.startsWith("bk") && (int) currentPoint.getX() >= 3
									&& (int) currentPoint.getX() <= 5 && (int) currentPoint.getY() >= 0
									&& (int) currentPoint.getY() <= 2)))
			{
				return true;
			}
			else
			{
				// System.out.println("当前走法不符合【帅（将）】的走棋规则：只能在九宫内行走！");
				return false;
			}
		}
		// 兵（卒）
		else if (chessValue.startsWith("rp") || chessValue.startsWith("bp"))
		{
			// System.out.println("你点击了【 兵】");
			// 红方
			if (chessValue.startsWith("rp"))
			{
				// 只能往前走，如果后退则返回false
				if (currentY > lastY)
				{
					// System.out.println("当前走法不符合【兵（卒）】的走棋规则：只能往前走一格！");
					return false;
				}
				// 过河之前，只能每次向前一格
				if ((lastY >= 5 && lastY <= 9) // 没有过河
						&& (Math.abs(lastX - currentX) == 0 && Math.abs(lastY - currentY) == 1))// 往前走了一格
				{
					return true;
				}
				else if ((lastY >= 0 && lastY <= 4) // 过了河
						&& ((Math.abs(lastX - currentX) == 0 && Math.abs(lastY - currentY) == 1)
								|| (Math.abs(lastX - currentX) == 1 && Math.abs(lastY - currentY) == 0)))	// 往前走或者往左右走一格
				{
					return true;
				}
				// System.out.println("当前走法不符合【兵（卒）】的走棋规则：过河前只能往前走一格，过河后每次只能向前或左或右走一格！");
				return false;
			}
			else if (chessValue.startsWith("bp"))
			{// 黑方
				// 只能往前走，如果后退则返回false
				if (currentY < lastY)
				{
					return false;
				}
				// 过河之前，只能每次向前一格
				if ((lastY >= 0 && lastY <= 4) // 没有过河
						&& (Math.abs(lastX - currentX) == 0 && Math.abs(lastY - currentY) == 1))// 往前走了一格
				{
					return true;
				}
				else if ((lastY >= 5 && lastY <= 9) // 过了河
						&& ((Math.abs(lastX - currentX) == 0 && Math.abs(lastY - currentY) == 1)
								|| (Math.abs(lastX - currentX) == 1 && Math.abs(lastY - currentY) == 0)))	// 往前走或者往左右走一格
				{
					return true;
				}
				// System.out.println("当前走法不符合【兵（卒）】的走棋规则：过河前只能往前走一格，过河后每次只能向前或左或右走一格！");
				return false;
			}
		}
		// System.out.println("当前走法正确！");
		return true;
	}

	public   String getChineseChess(String chessValue)
	{
		// 车
		if (chessValue.startsWith("rr") || chessValue.startsWith("br"))
		{
			return "车";
		}
		// 马
		else if (chessValue.startsWith("rn") || chessValue.startsWith("bn"))
		{
			return "马";
		}
		// 炮
		else if (chessValue.startsWith("rc") || chessValue.startsWith("bc"))
		{
			return "炮";
		}
		// 相（象）
		else if (chessValue.startsWith("rb") || chessValue.startsWith("bb"))
		{
			if (chessValue.startsWith("rb"))
			{
				return "相";
			}
			else
			{
				return "象";
			}
		}
		// 仕（士）
		else if (chessValue.startsWith("ra") || chessValue.startsWith("ba"))
		{
			if (chessValue.startsWith("ra"))
			{
				return "仕";
			}
			else
			{
				return "士";
			}
		}
		// 帅（将）
		else if (chessValue.startsWith("rk") || chessValue.startsWith("bk"))
		{
			if (chessValue.startsWith("rk"))
			{
				return "帅";
			}
			else
			{
				return "将";
			}
		}
		// 兵（卒）
		else if (chessValue.startsWith("rp") || chessValue.startsWith("bp"))
		{
			if (chessValue.startsWith("rp"))
			{
				return "兵";
			}
			else
			{
				return "卒";
			}
		}
		return "[]";
	}

	public String getChineseNumber(int number)
	{
		String sd = "";
		switch (number)
		{
			case 1:
				sd = "一";
				break;
			case 2:
				sd = "二";
				break;
			case 3:
				sd = "三";
				break;
			case 4:
				sd = "四";
				break;
			case 5:
				sd = "五";
				break;
			case 6:
				sd = "六";
				break;
			case 7:
				sd = "七";
				break;
			case 8:
				sd = "八";
				break;
			case 9:
				sd = "九";
				break;
			default:
				break;
		}
		return sd;
	}

	public int findIndexOf(String query, String[] iccsArray)
	{
		for (int i = 0; i < iccsArray.length; i++)
		{
			if (query.equals(iccsArray[i]))
			{
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args)
	{
		ChessController pos = new ChessController();
		// System.out.println(pos.redMove);
		// System.out.println(toFen(pieceArray));

		String bestMove = "c3c4";
		String startPoint = bestMove.substring(0, 2);
		int lastY = pos.findIndexOf(startPoint.substring(0, 1), pos.iccsHorizontalNumbers);
		int lastX = Integer.valueOf(startPoint.substring(1, startPoint.length()));
		System.out.println("lastY=" + lastY + ",lastX=" + lastX);

		String endPoint = bestMove.substring(2, 4);
		int currentY = pos.findIndexOf(endPoint.substring(0, 1), pos.iccsHorizontalNumbers);
		int currentX = Integer.valueOf(endPoint.substring(1, endPoint.length()));
		System.out.println("currentY=" + currentY + ",currentX=" + currentX);
	}

}
