import java.awt.Canvas;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

public class VideoRecorder 
{
	private MediaPlayerFactory mFactory;
	private EmbeddedMediaPlayer mMediaPlayer;
	private CanvasVideoSurface videoSurface;
	
	private String mFileName = null;
	private String mCamName = null;
	private String mMRL = null;
	
	private boolean mIsMacOs = false;
	
	public VideoRecorder(Canvas can, String MRL, String camName, boolean isMacOS)
	{
		mIsMacOs = isMacOS;
		mCamName = camName;
		mMRL = MRL;
		
		List<String> vlcArgs = new ArrayList<String>();
		
		if(mIsMacOs)
		{
			vlcArgs.add("--vout=macosx");
		}
		else
		{
			vlcArgs.add("--no-video-title-show");
			vlcArgs.add("--clock-jitter=0");
		}
		
		mFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		
	    mMediaPlayer = mFactory.newEmbeddedMediaPlayer();
	    
	    videoSurface = mFactory.newVideoSurface(can);
	    mMediaPlayer.setVideoSurface(videoSurface);
	    
	    startPlay();
	}
	
	public void startPlay() 
	{
		boolean res = mMediaPlayer.playMedia(mMRL, buildOptionsPlay());
	    
		//does not seem to work, errors happen in native world...
	    if(!res)
	    {
	    	System.out.println("Error");
	    	RecMyShoes.getInstance().showErrorDialog("Video Recorder Problem", "Error while trying to play! is the camera plugged?");
	    }
	}
	
	public void startRec(File dir) 
	{		
		if(mMediaPlayer.isPlaying())
		{
			mMediaPlayer.stop();
		}
		
	    DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
	    mFileName =  dir.getAbsolutePath() + "\\Capture-" + df.format(new Date()) + ".mpg";
	    
	    boolean res = mMediaPlayer.playMedia(mMRL, buildOptionsRec());
	    
	    if(!res)
	    {
	    	RecMyShoes.getInstance().showErrorDialog("Video Recorder Problem", "Error while trying to play! is the camera plugged?");
	    }
	}
	
	private String[] buildOptionsPlay()
	{
		List<String> vlcArgs = new ArrayList<String>();
		
		if(mIsMacOs)
		{
			vlcArgs.add(":qtcapture-vdev=" + mCamName);
			vlcArgs.add(":sout=#transcode{vcodec=h264,acodec=mp4a}:std{mux=mp4,access=file,dst=" + mFileName + "}");	
		}
		else
		{
			vlcArgs.add(":dshow-vdev=" + mCamName);
			vlcArgs.add(":dshow-adev=none");
		}
		
		return vlcArgs.toArray(new String[vlcArgs.size()]);
	}
	
	private String[] buildOptionsRec()
	{
		List<String> vlcArgs = new ArrayList<String>();
		
		if(mIsMacOs)
		{
			vlcArgs.add(":qtcapture-vdev=" + mCamName);
			vlcArgs.add(":sout=#transcode{vcodec=h264,acodec=mp4a}:std{mux=mp4,access=file,dst=" + mFileName + "}");
		}
		else
		{
			vlcArgs.add(":dshow-vdev=" + mCamName);
			vlcArgs.add(":dshow-adev=none");
			vlcArgs.add(":sout=#transcode{vcodec=mp2v,vb=4096,scale=1,ab=128,channels=2,samplerate=44100}:duplicate{dst=file{dst=" + mFileName + "},dst=display}");	
		}
		
		return vlcArgs.toArray(new String[vlcArgs.size()]);
	}
	
	public void stop()
	{
		//stop the player
		mMediaPlayer.stop();
		
		//add the video to the current playlist
		File file = new File(mFileName);
		
		if(file.exists())
		{
			System.out.println("File exists");
			RecMyShoes.getInstance().addVideo(mFileName);
		}
		else
		{
			System.out.println("File is null");
		}
		
		//restart the playback
		startPlay();
	}
	
	public void finish()
	{
		if(mMediaPlayer.isPlaying())
		{
			mMediaPlayer.stop();
		}
		
		mMediaPlayer.release();
		mFactory.release();
		
		mMediaPlayer = null;
		mFactory = null;
	}
}
