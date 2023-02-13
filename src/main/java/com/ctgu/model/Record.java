package com.ctgu.model;

public class Record
{
	/**
	 * 棋子名称
	 */
	private String chessName;

	/**
	 * 是否红方走棋
	 */
	private boolean redMove;

	/**
	 * 上一次走棋步骤
	 */
	private Step chessStep;

	/**
	 * 上一次走棋是否吃子
	 */
	private boolean eatFlag;

	public boolean isEatFlag()
	{
		return eatFlag;
	}

	public void setEatFlag(boolean eatFlag)
	{
		this.eatFlag = eatFlag;
	}

	public String getChessName()
	{
		return chessName;
	}

	public void setChessName(String chessName)
	{
		this.chessName = chessName;
	}

	public boolean isRedMove()
	{
		return redMove;
	}

	public void setRedMove(boolean redMove)
	{
		this.redMove = redMove;
	}

	public Step getChessStep()
	{
		return chessStep;
	}

	public void setChessStep(Step chessStep)
	{
		this.chessStep = chessStep;
	}

	@Override
	public String toString()
	{
		return "ChessMoveRecord [chessName=" + chessName + ", chessStep=" + chessStep + "]";
	}
}
