package com.showpage.swtfoundation;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Implement a splash screen.  To use this, implement a subclass that
 * will draw all the bits as needed.  Then you can set waitForBackgroundTasks
 * to true if your main application wants to let us know when to move on.
 * The just call setSplashTime() and display the window.
 */
public class SplashScreen extends MainFrame
{
	private boolean	waitForBackgroundTasks = false;
	private String	imageFileName;
	private String	labelText;
	private Image	image;
	private Label	label;
	
	/**
	 * Set up graphics.
	 */
	public void initialize(Shell _parent, int windowWidth, int windowHeight)
	{
		shellStyle = SWT.NO_TRIM;
		super.initialize(_parent, windowWidth, windowHeight);
		shell.setLayout(new FormLayout());
		centerWindow = true;
		
		label = new Label(shell, 0);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.top = new FormAttachment(0, 0);
		fd.bottom = new FormAttachment(100, 0);
		label.setLayoutData(fd);
		
		if (image != null)
		{
			label.setImage(image);
    		shell.pack();
		}
		else if (labelText != null) {
			label.setText(labelText);
		}
	}
	
	/**
	 * Remember the image to splash.
	 */
	public void setImageFileName(String fName)
	{
		// TODO: We need to search our path for the file.
		imageFileName = fName;
		
		image = new Image(SwtEnvironment.singleton().display(), imageFileName);
		if (label != null)
		{
			label.setImage(image);
		}
		shell.pack();
	}
	
	/**
	 * Display a label in the window.
	 */
	public void setLabelText(String _labelText) {
		labelText = _labelText;
		if (label != null) {
			label.setText(labelText);
		}
	}
	
	/**
	 * Should we synchronize with SWTApplication background startup?
	 */
	public void setWaitForBackgroundTasks(boolean val)
	{
		waitForBackgroundTasks = val;
	}
	
	public boolean waitForBackgroundTasks()
	{
		return waitForBackgroundTasks;
	}
	
	/**
	 * How long do we want to be displayed?
	 */
	public void setSplashTime(int millis)
	{
		Runnable r = new Runnable() {
			public SplashScreen ss = SplashScreen.this;
			boolean oldWait = ss.waitForBackgroundTasks();
			public void run() {
				if (oldWait)
				{
					ss.setWaitForBackgroundTasks(true);
				}
				else
				{
					ss.exitFromRunLoop = true;
				}
			}
		};
		setWaitForBackgroundTasks(false);
		
		SwtEnvironment.singleton().display().timerExec(millis, r);
	}
	
	/**
	 * Override from MainFrame.
	 */
	public void dispatchLoopHook()
	{
		if (waitForBackgroundTasks && SWTApplication.singleton().backgroundSetupComplete)
		{
			exitFromRunLoop = true;
			shell.close();
		}
	}
}
