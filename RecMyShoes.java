import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.logger.Logger;

import com.sun.jna.NativeLibrary;


public class RecMyShoes 
{
	//instance
	private static RecMyShoes mInstance = null;
	
	//JFrame holding the UI
	private GUIFrame mFrame = null;
	
	//Video player / recorder
	private VideoRecorder mVideoRec = null;
	private VideoPlayer mVideoPlayer = null;
	
	//settings
	private File mOutputDir = null;
	private String mOS = null;
	private boolean mIsMacOs = false;
	private String mCamMRL = null;
	private static String mCamName = "IceCam2";
	private String mVLCLibPath = null;
	private String mVideoDirectoryPath = null;
	private String mDirName = "ShoeVideos";
	
	public static void main(final String[] args) 
	{
		if(args.length > 1) 
		{
			StringBuilder sb = new StringBuilder();
			
		    for(String s : args) 
		    {
		        sb.append(s);
		        sb.append(" "); //some length of whitespace
		    }

		    mCamName = sb.toString().trim();
	    }
		
		//to avoid black screen while full screen
		System.setProperty("sun.java2d.noddraw", "true");
		
		RecMyShoes.getInstance();
	}
	
	public static RecMyShoes getInstance()
	{
		try
		{
			if(mInstance == null)
			{
				mInstance = new RecMyShoes();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return mInstance;
	}
	
	public RecMyShoes() throws IOException
	{
		init();
	}
	
	protected void finalize() throws Throwable
	{
		turnOff();
		super.finalize();
	} 

	private void init()
	{
		//init settings according to OS
		osCheck();
		Logger.info("OS is: " + mOS);
		
		//init GUI
		mFrame = new GUIFrame();
	    mFrame.setVisible(true);

	    //load VLC libs
	    Logger.info("mVLCLibPath is " + mVLCLibPath);
	    
	    if(mIsMacOs)
	    {
	    	System.setProperty("jna.library.path", mVLCLibPath);
	    }
	    else
	    {
	    	NativeLibrary.addSearchPath("libvlc", mVLCLibPath);
	    }
	    
	    //init ouput directory
	    mOutputDir =  new File(mVideoDirectoryPath, mDirName);
	    mOutputDir.mkdir();
	    
	    //init video player
	    mVideoPlayer = new VideoPlayer(mFrame.getPlayerCanvas(), mOutputDir, mIsMacOs);
	    
	    //init video recorder
	    //mVideoRec = new VideoRecorder(mFrame.getRecorderCanvas(), mCamMRL, "Periferica video USB", mIsMacOs); 
	    mVideoRec = new VideoRecorder(mFrame.getRecorderCanvas(), mCamMRL, mCamName, mIsMacOs); 
	    
		//Full screen?
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		
		if (gs.isFullScreenSupported()) 
		{
			gs.setFullScreenWindow(mFrame);
		}
	}
	
	private void osCheck()
	{
		//get the OS
		mOS = System.getProperty("os.name").toLowerCase();
		
		Logger.info("OS arch: " + System.getProperty("os.arch").toLowerCase());
		Logger.info("VM arch is: " + System.getProperty("java.vm.name").toLowerCase());
		
		System.out.println("OS lang: " + System.getProperty("user.language"));
		
		//setup settings according to the OS
	    if(mOS.indexOf("win") >= 0)
		{
	    	mCamMRL = "dshow://";
	    	
	    	if(System.getProperty("user.language").equals("it"))
	    	{
	    		mVLCLibPath = "C:\\Programmi\\VideoLAN\\VLC";
	    	}
	    	else
	    	{
	    		mVLCLibPath = "C:\\Program Files\\VideoLAN\\VLC";
	    	}
	    	
	    	mVideoDirectoryPath = "C:";
		}
	    else if(mOS.indexOf("mac") >= 0)
	    {
	    	mIsMacOs = true;
	    	mCamMRL = "qtcapture://" ;
	    	mVLCLibPath = "/Applications/VLC.app/Contents/MacOS/lib/";
	    	mVideoDirectoryPath = System.getProperty("user.home");
	    }
	    else if(mOS.indexOf("nix") >= 0 || mOS.indexOf("nux") >= 0)
	    {
	    	mCamMRL = "v4l2:///dev/video0";
	    	mVLCLibPath = "/home/linux/vlc/install/lib/";
	    	mVideoDirectoryPath = System.getProperty("user.home");
	    }
	}
	
	public void startCapture()
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			@Override
			public void run() 
			{
		    	mVideoRec.startRec(mOutputDir);
			}
    	});
	}
	
	public void stopCapture()
	{
		mVideoRec.stop();
	}
	
	public void addVideo(String videoPath)
	{
		mVideoPlayer.addVideo(videoPath);
	}
	
	public void showErrorDialog(String title, String message)
	{
    	JOptionPane.showMessageDialog(mFrame,
    			message,
    		    title,
    		    JOptionPane.ERROR_MESSAGE);
	}
	
	private void turnOff()
	{
		if(mVideoPlayer != null)
		{
			mVideoPlayer.stop();
			mVideoPlayer = null;
		}
		
		if(mVideoRec != null)
		{
			mVideoRec.finish();
			mVideoRec = null;
		}
	}
}
