package com.ctgu.model;

/**
 * 势力类, 红方 & 黑方
 */
public enum ChessPart
{
	/**
	 * 红方势力
	 */
	RED,

	/**
	 * 黑方势力
	 */
	BLACK;

	/**
	 * 返回相反的势力
	 */
	public static ChessPart getOpposite(ChessPart part)
	{
		if (part == RED)
		{
			return BLACK;
		}
		else
		{
			return RED;
		}
	}
}
