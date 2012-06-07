import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;


public class GUIFrame extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton record = null;
	private JButton upload = null;
	
	private JButton mBtnVideo = null;
	
	private boolean mIsRecording = false;
	
	private JPanel mContentPanel = null;
	private JPanel mVideoPanel = null;
	private Canvas mRecorderCanvas = null;
	private Canvas mPlayerCanvas = null;
	
	public GUIFrame()
	{
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    setLocation(50, 50);
	    setSize(800, 400);
	    setLocationRelativeTo(null);
	    setUndecorated(true);

	    mPlayerCanvas = new Canvas();
		mPlayerCanvas.setBackground(Color.black);
		
	    mRecorderCanvas = new Canvas();
		mRecorderCanvas.setBackground(Color.black);
		
		mContentPanel = new JPanel();
		mContentPanel.setBackground(Color.white);
		
		mVideoPanel = new JPanel();
		mVideoPanel.setBackground(Color.white);
		mVideoPanel.setLayout(new GridLayout(1, 2, 10, 10));
		
		mContentPanel.setLayout(new GridLayout(2,2));
		
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;

		mVideoPanel.add(mPlayerCanvas);
		mVideoPanel.add(mRecorderCanvas);
		
		mContentPanel.add(mVideoPanel);
		setContentPane(mContentPanel);
		
		Font f = new Font("Arial", Font.PLAIN, 60);

		mBtnVideo = new JButton("RECORD");
		mBtnVideo.setLayout(new BorderLayout());
		mBtnVideo.setMargin(new Insets(150, 150, 150, 150));
		mBtnVideo.setFocusPainted( false );
		
		JLabel l = new JLabel();
		//l.setFont(f);
		l.setBorder(new LineBorder(Color.BLACK, 8));
		
		mBtnVideo.add(l, BorderLayout.CENTER);
		mBtnVideo.setFont(f);
		mBtnVideo.setBackground(new Color(255,255,255));
		mBtnVideo.addActionListener(this);
	    
	    mContentPanel.add(mBtnVideo);
	}
	
	public Canvas getPlayerCanvas()
	{
		return mPlayerCanvas;
	}
	
	public Canvas getRecorderCanvas()
	{
		return mRecorderCanvas;
	}
	
	private void  updateButton()
	{
		if(mIsRecording)
		{
			mBtnVideo.setText("STOP AND UPLOAD");
		}
		else
		{
			mBtnVideo.setText("RECORD");
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) 
	{
		// TODO Auto-generated method stub
		
		if( evt.getSource( ) == mBtnVideo ) 
		{
			if(mIsRecording)
			{
				RecMyShoes.getInstance().stopCapture();
				mIsRecording = false;
			}
			else
			{
				RecMyShoes.getInstance().startCapture();
				mIsRecording = true;
			}
			
			updateButton();
		}
	}
}
