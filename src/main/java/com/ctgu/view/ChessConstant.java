package com.ctgu.view;

public class ChessConstant
{
	/**
	 * 棋盘每个格子的大小
	 */
	public static final int GRID_WIDTH = 56;
	/**
	 * 棋盘距离边框的距离
	 */
	public static final int CHESSBOARD_MARGIN = 2 * GRID_WIDTH;

	public static int RADIUS = 15;

	// /**
	// * 线宽，这里默认设置所有线宽都一样,也可根据需求分别设置
	// */
	// public static final float STROKE_WIDTH = 5.0f;
	/**
	 * 箭头的高度，单位像素
	 */
	public static final Integer ARROW_HEIGHT = 20;

	/**
	 * 箭头底边的一半，单位像素
	 */
	public static final Integer ARROW_LENGTH = 10;

	/**
	 * 棋盘X轴范围
	 */
	public static final int RANGE_X = 9;
	/**
	 * 棋盘Y轴范围
	 */
	public static final int RANGE_Y = 10;
	/**
	 * 棋子宽度和高度
	 */
	public static final int PIECE_WIDTH = 56;
	/**
	 * 棋子宽度和高度
	 */
	public static final int PIECE_HEIGHT = PIECE_WIDTH;
	/**
	 * 棋盘 x 坐标初始位置
	 */
	public static final int X_INIT = 28 - PIECE_WIDTH / 2;
	/**
	 * 棋盘 y 坐标初始位置
	 */
	public static final int Y_INIT = 28 - PIECE_WIDTH / 2;

}
