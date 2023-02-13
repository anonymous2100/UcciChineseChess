package com.ctgu.model;

public class Step
{
	private ChessPoint from;
	private ChessPoint to;

	public Step()
	{
		super();
	}

	public Step(ChessPoint from, ChessPoint to)
	{
		super();
		this.from = from;
		this.to = to;
	}

	public ChessPoint getFrom()
	{
		return from;
	}

	public void setFrom(ChessPoint from)
	{
		this.from = from;
	}

	public ChessPoint getTo()
	{
		return to;
	}

	public void setTo(ChessPoint to)
	{
		this.to = to;
	}

	@Override
	public String toString()
	{
		return "Step [from=" + from + ", to=" + to + "]";
	}

}
