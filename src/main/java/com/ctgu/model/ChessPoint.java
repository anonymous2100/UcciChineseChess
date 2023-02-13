package com.ctgu.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChessPoint implements Comparable
{
	/**
	 * 鼠标点击的x坐标
	 */
	public int x;
	/**
	 * 鼠标点击的y坐标
	 */
	public int y;

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public ChessPoint()
	{
		super();
	}

	public ChessPoint(int x, int y)
	{
		super();
		this.x = x;
		this.y = y;
	}

	public ChessPoint(Point point)
	{
		super();
		this.x = (int) point.getX();
		this.y = (int) point.getY();
	}

	@Override
	public String toString()
	{
		return "ChessPoint [x=" + x + ", y=" + y + "]";
	}

	@Override
	public boolean equals(Object o)
	{
		// 自反性
		if (this == o)
		{
			return true;
		}
		// 任何对象不等于null，比较是否为同一类型
		if (!(o instanceof ChessPoint))
		{
			return false;
		}
		// 强制类型转换
		ChessPoint point = (ChessPoint) o;
		// 比较属性值
		return getX() == point.getX() && Objects.equals(getX(), point.getX()) && Objects.equals(getY(), point.getY());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getX(), getY());
	}

	@Override
	public int compareTo(Object o)
	{
		ChessPoint s = (ChessPoint) o;
		if (this.x > s.x)
		{
			return 1;
		}
		else if (this.x < s.x)
		{
			return -1;
		}
		else
		{
			if (this.y >= s.y)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}

	public static void main(String[] args)
	{
		ChessPoint kingPoint1 = new ChessPoint(4, 9);
		ChessPoint kingPoint2 = new ChessPoint(1, 2);
		List<ChessPoint> moveIndicitorList = new ArrayList<>();
		moveIndicitorList.add(kingPoint1);
		moveIndicitorList.add(kingPoint2);
		Collections.sort(moveIndicitorList);
		System.out.println(moveIndicitorList);

		ChessPoint kingPoint3 = new ChessPoint(4, 9);
		System.out.println(moveIndicitorList.contains(kingPoint3));
	}

	// /**
	// *
	// */
	// public static final ChessPoint NULL_PLACE = new ChessPoint(-1, -1);
	// /**
	// * 对应棋盘坐标对象池
	// */
	// private static final ChessPoint[][] chessPointArray;
	//
	// static
	// {
	// // 初始化即开始建立棋盘上所有的位置坐标对象
	// chessPointArray = new ChessPoint[ChessDefined.RANGE_Y][ChessDefined.RANGE_X];
	// for (int y = 0; y < ChessDefined.RANGE_Y; y++)
	// {
	// for (int x = 0; x < ChessDefined.RANGE_X; x++)
	// {
	// chessPointArray[y][x] = new ChessPoint(y, x);
	// }
	// }
	// }
	//
	// private ChessPoint(int x, int y)
	// {
	// this.x = x;
	// this.y = y;
	// }
	//
	// /**
	// * @param x
	// * 棋盘x坐标(从0-9)
	// * @param y
	// * 棋盘y坐标(从0-10)
	// * @return 对应棋盘坐标对象
	// */
	// public static ChessPoint of(int x, int y)
	// {
	// return chessPointArray[x][y];
	// }

}
