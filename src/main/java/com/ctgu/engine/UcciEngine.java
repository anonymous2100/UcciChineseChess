package com.ctgu.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

import com.ctgu.view.Utils;

public class UcciEngine
{
	private static Logger log = Logger.getLogger(UcciEngine.class);

	public static long requestTime = 0L;
	public static boolean hasReturn = false;

	private static Process process;
	private static InputStream inputStream;
	private static OutputStream outputStream;

	static
	{
		// 初始化引擎
		String cmd = UcciEngine.class.getClassLoader().getResource("engines/ucci/ELEEYE.EXE").getFile();
		try
		{
			process = Runtime.getRuntime().exec(cmd);
			inputStream = process.getInputStream();
			outputStream = process.getOutputStream();
			System.out.println("加载引擎成功！");
		}
		catch (IOException e)
		{
			System.out.println("加载引擎失败：" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static InputStream getInputStream()
	{
		return inputStream;
	}

	public static OutputStream getOutputStream()
	{
		return outputStream;
	}

	/**
	 * 获取最佳着法
	 * <p>
	 * bestmove <最佳着法> [ponder <后台思考的猜测着法>] [draw | resign]<br/>
	 * 思考状态的反馈，此后引擎返回空闲状态。显示思考结果，即引擎认为在当前局面下的最佳着法，<br/>
	 * 以及猜测在这个着法后对手会有怎样的应对(即后台思考的猜测着法)。<br/>
	 * 通常，最佳着法是思考路线(主要变例)中的第一个着法，而后台思考的猜测着法则是第二个着法。<br/>
	 * 在对手尚未落子时，可以根据该着法来设定局面，并作后台思考。当对手走出的着法和后台思考的猜测着法吻合时，称为“后台思考命中”。<br/>
	 * draw选项表示引擎提和或者接受界面向引擎发送的提和请求，参阅go draw和ponderhit draw指令。<br/>
	 * resign选项表示引擎认输。UCCI界面在人机对弈方式下，根据不同情况，可以对引擎的bestmove反馈中的draw和resign选项作出相应的处理：<br/>
	 * (1) 如果用户提和，界面向引擎发出go draw或ponderhit draw指令，而引擎反馈带draw的bestmove，那么界面可终止对局并判议和；<br/>
	 * (2) 如果用户没有提和，而引擎反馈带draw的bestmove，那么界面可向用户提和，用户接受提和则可终止对局并判议和；<br/>
	 * (3) 如果引擎反馈带resign的bestmove，那么界面可终止对局并判引擎认输。<br/>
	 * 引擎应该根据当前局面的情况(由position指令给出)，以及界面是否发送了带draw的go或ponderhit指令，来考虑是否反馈带draw或resign的bestmove。<br/>
	 * <p>
	 * nobestmove：思考状态的反馈，此后引擎返回空闲状态。<br/>
	 * 显示思考结果，但引擎一步着法也没计算，表示当前局面是死局面，<br/>
	 * 或者接收到诸如 go depth 0 等只让引擎给出静态局面评价的指令。<br/>
	 */

	public static String getBestMove(String fen) throws IOException
	{
		System.out.println(">>>正在向引擎发送命令，等待回复最佳走法中...");
		OutputStream outputStream = UcciEngine.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
		bw.write(fen);
		bw.newLine();

		bw.write("go time 100 increase 0");
		bw.newLine();

		bw.flush();

		requestTime = System.currentTimeMillis();
		hasReturn = false;
		// 取得命令结果的输出流
		InputStream fis = UcciEngine.getInputStream();
		// 用一个读输出流类去读
		InputStreamReader isr = new InputStreamReader(fis);
		// 用缓冲器读行
		BufferedReader br = new BufferedReader(isr);
		String bestmove = null;
		while (true)
		{
			bestmove = br.readLine();
			if (bestmove == null)
			{
				continue;
			}
			System.out.println("<<<" + bestmove);
			Utils.writeToFile(bestmove);
			if (bestmove.startsWith("bestmove"))
			{
				Utils.writeToFile("\n");
				hasReturn = true;
				log.info("<<<引擎已返回最佳着法！");
				return bestmove.substring(9, 13);
			}
			else if (bestmove.startsWith("nobestmove") || (System.currentTimeMillis() - requestTime) > 1000 * 60 * 5)
			{
				hasReturn = true;
				log.info("引擎未返回最佳着法，可能棋局已结束！");
				return "null";
			}
		}
	}

	// public static void main(String[] args) throws IOException, Exception
	// {
	// while (true)
	// {
	// Scanner s = new Scanner(System.in);
	// String fen = s.next();
	// String bestMove = getBestMove(fen);
	// System.out.println("引擎返回结果：" + bestMove);
	// }
	// }
}
