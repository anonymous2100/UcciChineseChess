//package com.ctgu.engine;
//
//import java.io.OutputStream;
//
//public class StopCommand implements Runnable
//{
//	// 引擎最多可以考虑的时间，超过就要马上返回下法
//	private static final long LIMITTIME = Integer.valueOf("20000");
//
//	@Override
//	public void run()
//	{
//		long currentTimeMillis = System.currentTimeMillis();
//		if (UcciEngine.requestTime != 0 && !UcciEngine.hasReturn
//				&& currentTimeMillis - UcciEngine.requestTime > LIMITTIME)
//		{
//			try
//			{
//				OutputStream outputStream = UcciEngine.getOutputStream();
//				outputStream.write("stop\r\n".getBytes());
//				outputStream.flush();
//				System.out.println("stop");
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
//
//}
