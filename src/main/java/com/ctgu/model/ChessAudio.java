package com.ctgu.model;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import lombok.extern.slf4j.Slf4j;

/**
 * 象棋游戏播放类 <br>
 * 使用的是 sun.audio.* 中的类, 在一些JVM平台可能不受支持
 **/
@Slf4j
public enum ChessAudio
{
	/**
	 * from 点击时
	 */
	CLICK_FROM("300"),
	/**
	 * 长拦, 或者导致 BOSS面对面, 或者被将军时, 走错棋
	 */
	MAN_MOV_ERROR("301"),
	/**
	 * MAN 角色触发正常走棋
	 */
	MAN_MOVE("302"),
	/**
	 * COM 走棋
	 */
	COM_MOVE("303"),
	/**
	 * MAN 方吃掉 对方
	 */
	MAN_EAT("304"),
	/**
	 * COM 方吃掉 对方
	 */
	COM_EAT("305"),
	/**
	 * MAN 方 抑制/将军 对方
	 */
	MAN_CHECK("306"),
	/**
	 * COM 方 抑制/将军 对方
	 */
	COM_CHECK("307"), WIN_BGM("308"), LOSE_BGM("309"),
	/**
	 * 被 COM 角色将死
	 */
	BE_CHECKMATED_BY_COM("310"), OPEN_BOARD("311");

	private String tag;

	private static AudioInputStream audioStream;
	private static AudioFormat audioFormat;
	private static SourceDataLine sourceDataLine;

	ChessAudio(String tag)
	{
		this.tag = tag;
	}

	public String getTag()
	{
		return tag;
	}

	/**
	 * 根据name查找
	 */
	public static String findByName(String name)
	{
		String retTag = null;
		for (ChessAudio chessAudio : ChessAudio.values())
		{
			if (name.equals(chessAudio.getTag()))
				retTag = chessAudio.getTag();
		}
		return retTag;
	}

	public static void play(ChessAudio audioTag)
	{
		// 播放音频
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				realPlay(audioTag);
			}
		}).start();
	}

	/**
	 * 播放音频
	 */
	public static void realPlay(ChessAudio audioTag)
	{
		final String name = "wave/" + findByName(audioTag.getTag()) + ".wav";
		// log.info("name: " + name);
		String fileName = ChessAudio.class.getClassLoader()
				.getResource(name)
				.getFile();
		// log.info("fileName: " + fileName);
		File file = new File(fileName);
		try
		{
			int count;
			byte buf[] = new byte[2048];
			// 获取音频输入流
			audioStream = AudioSystem.getAudioInputStream(file);
			// 获取音频格式
			audioFormat = audioStream.getFormat();
			log.info("播放音频文件: " + file.getAbsolutePath());
			// log.info("音频Encoding: " + audioFormat.getEncoding());
			// 如果不是wav格式，转换mp3文件编码。MPEG1L3（mp3格式）转为PCM_SIGNED（wav格式）
			if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
			{
				audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16,
						audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
				audioStream = AudioSystem.getAudioInputStream(audioFormat, audioStream);
			} // 转换mp3文件编码结束
				 // 封装音频信息
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat,
					AudioSystem.NOT_SPECIFIED);
			// 获取虚拟扬声器（SourceDataLine）实例
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			while ((count = audioStream.read(buf, 0, buf.length)) != -1)
			{
				sourceDataLine.write(buf, 0, count);
			}
			// 播放结束，释放资源
			sourceDataLine.drain();
			sourceDataLine.close();
			audioStream.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{
		play(ChessAudio.MAN_EAT);
	}
}
