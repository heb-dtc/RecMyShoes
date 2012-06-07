import java.awt.Canvas;
import java.io.File;

import uk.co.caprica.vlcj.logger.Logger;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;

public class VideoPlayer
{
	private MediaPlayerFactory mFactory;
	private MediaListPlayer mMediaListPlayer;
	private EmbeddedMediaPlayer mMediaPlayer;
	private MediaList mMediaList;
	
	private CanvasVideoSurface mVideoSurface;
	
	private File mVideoDir = null;
		
	private String[] mMediaOptions = {};
	
	public VideoPlayer(Canvas can, File videoDir, boolean isMacOS)
	{
		mVideoDir = videoDir;
		
		if(isMacOS)
		{
			mFactory = new MediaPlayerFactory("--vout=macosx");
		}
		else
		{
			mFactory = new MediaPlayerFactory();
		}
		
	    mMediaPlayer = mFactory.newEmbeddedMediaPlayer();
	    mMediaListPlayer = mFactory.newMediaListPlayer();
	    mMediaListPlayer.setMediaPlayer(mMediaPlayer);
	    
	    mVideoSurface = mFactory.newVideoSurface(can);
	    mMediaPlayer.setVideoSurface(mVideoSurface);
	    
	    mMediaListPlayer.setMode(MediaListPlayerMode.LOOP);
	    
	    mMediaList = mFactory.newMediaList();
	    listVideos();
	}
	
	private void listVideos()
	{
		String[] videoList = mVideoDir.list();
		
		if (videoList == null) 
		{
		    // Either dir does not exist or is not a directory
			Logger.info("Arf! dir does not exist or is not a directory");
		} 
		else 
		{
		    for (int i=0; i < videoList.length; i++) 
		    {
		        // Get filename of file or directory
		    	Logger.info("File " + i + ", " + mVideoDir.toString() + "\\" + videoList[i]);
		        
		        String mediaPath = mVideoDir.toString() + "/" + videoList[i];
		        
		        mMediaList.addMedia(mediaPath, mMediaOptions);
		        mMediaListPlayer.setMediaList(mMediaList);
		    }
		    
		    mMediaListPlayer.play();
		}
	}
	
	public void addVideo(String videoPath)
	{
		Logger.info("add video to playlist: " + videoPath);
		mMediaList.addMedia(videoPath, mMediaOptions);
		
		if(!mMediaListPlayer.isPlaying())
		{
			mMediaListPlayer.setMediaList(mMediaList);
			mMediaListPlayer.play();
		}
	}
	
	public void stop()
	{
		mMediaListPlayer.stop();

		mMediaListPlayer.release();
		mMediaPlayer.release();
		mFactory.release();
	}
}
