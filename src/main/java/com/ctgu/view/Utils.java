package com.ctgu.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils
{
	private static final String TEMP_FILE_NAME = "chess-temp-log.txt";

	/**
	 * 文件读取
	 *
	 * @param sourceFilePath
	 *            源文件路径
	 * @throws IOException
	 *             文件写入异常
	 */
	public static byte[] readFile(String sourceFilePath) throws IOException
	{
		final File file = new File(sourceFilePath);
		if (!file.exists() || !file.isFile())
		{
			return new byte[0];
		}
		byte[] buf = new byte[(int) file.length()];
		try (FileInputStream in = new FileInputStream(sourceFilePath))
		{
			final int read = in.read(buf);
			assert read == file.length();
		}
		return buf;
	}

	/**
	 * 动态字节流文件读取
	 *
	 * @param classPath
	 *            源文件路径
	 * @throws IOException
	 *             文件写入异常
	 */
	public static byte[] readFromResource(String classPath) throws IOException
	{
		final ClassLoader classLoader = ChessPanel.class.getClassLoader();
		byte[] buf = new byte[8 * 1024];
		try (InputStream in = classLoader.getResourceAsStream(classPath))
		{
			if (in == null)
			{
				throw new FileNotFoundException("未发现资源文件 : " + classPath);
			}
			// 动态字节流
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			int len;
			while ((len = in.read(buf)) != -1)
			{
				arrayOutputStream.write(buf, 0, len);
			}
			return arrayOutputStream.toByteArray();
		}
	}

	public static String readFromFile()
	{
		StringBuffer sb = new StringBuffer("");
		try
		{
			File file = new File(System.getProperty("user.dir") + File.separator + TEMP_FILE_NAME);
			// System.out.println("read log file from " + file.getAbsolutePath());
			FileReader reader = new FileReader(file);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			while ((str = br.readLine()) != null)
			{
				sb.append(str + "\n");
			}
			br.close();
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void writeToFile(String bestmove)
	{
		try
		{
			File file = new File(System.getProperty("user.dir") + File.separator + TEMP_FILE_NAME);
			// System.out.println("write log file to " + file.getAbsolutePath());
			FileWriter writer = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(bestmove + "\n");
			bw.close();
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void createLogFile()
	{
		try
		{
			File file = new File(System.getProperty("user.dir") + File.separator + TEMP_FILE_NAME);
			// System.out.println("create log file " + file.getAbsolutePath());
			if (file.exists() == false)
			{
				file.createNewFile();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void deleteLogFile()
	{
		File file = new File(System.getProperty("user.dir") + File.separator + TEMP_FILE_NAME);
		if (file.exists())
		{
			file.delete();
		}
	}

}
