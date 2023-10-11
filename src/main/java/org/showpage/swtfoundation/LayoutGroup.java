package org.showpage.swtfoundation;

import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * LayoutGroup us used to handle the FormLayout settings for groups
 * of Label / Control pairs.  We use FormLayout rather extensively
 * in our app due to its power, but it's awkward to use for some
 * things.
 * 
 * Our goal is to lay out the labels so that they are all right justified
 * to the same location without wasting any space.  Thus, we will originally
 * set them all up to be left-justified to the same place, then when the
 * page is rendered, we can adjust the settings.
 */
public class LayoutGroup extends BaseLayout implements PaintListener
{
	public FormAttachment		leftA;
	public FormAttachment		rightA;
	public FormAttachment		topA;
	
	private boolean			haveAdjusted = false;
	
	/** The standard distance between a label and it's control. */
	public int				xIncrement = 5;
	
	/** The standard distance between a control and the label / control below it. */
	public int				yIncrement = 20;
	
	private ArrayList<Control>	labels = new ArrayList<Control>();
	private ArrayList<Control>	controls = new ArrayList<Control>();
	
	/**
	 * Constructor.
	 * 
	 * @param _leftAttachment	Location for the group
	 * @param _rightAttachment	Location for the group
	 * @param _topAttachment	Location for the group
	 */
	public LayoutGroup(FormAttachment _leftAttachment, FormAttachment _rightAttachment, FormAttachment _topAttachment)
	{
		leftA = _leftAttachment;
		rightA = _rightAttachment;
		topA = _topAttachment;
	}
	
	/**
	 * Add a Label / Control pair.
	 * 
	 * @param label	The label (on the left)
	 * @param control The control (widget on the right)
	 * @param controlBottomAttachment IF you want to attach this widget to the bottom, else null.
	 * 
	 * @return The FormData.
	 */
	public FormData addPair(Control label, Control control, FormAttachment controlBottomAttachment)
	{
		FormAttachment thisTop = topA;
		
		if (controls.size() > 0)
		{
			Control previousControl = controls.get(controls.size() - 1);
			thisTop = new FormAttachment(previousControl, yIncrement);
		}
		labels.add(label);
		controls.add(control);
		
		FormData fd = new FormData();
		fd.left = leftA;
		fd.top = thisTop;
		label.setLayoutData(fd);
		
		fd = new FormData();
		fd.left = new FormAttachment(label, xIncrement);
		fd.right = rightA;
		fd.top = new FormAttachment(label, 0, SWT.TOP);
		fd.bottom = controlBottomAttachment;
		control.setLayoutData(fd);
		
		label.addPaintListener(this);
		haveAdjusted = false;
		
		return fd;
	}
	
	/**
	 * Add a triplet, a label plus two widgets.
	 * 
	 * @param label The label on the left
	 * @param control1	We do a normal pair with the label and this control
	 * @param control2	This control is set to the right of control1.
	 * @param controlBottomAttachment	For bottom attachments.
	 */
	public void addTriplet(Control label, Control control1, Control control2, FormAttachment controlBottomAttachment)
	{
		FormData fd1 = addPair(label, control1, controlBottomAttachment);
		FormData fd;
		
		fd = new FormData();
		fd.right = rightA;
		fd.top = fd1.top;
		fd.bottom = controlBottomAttachment;
		control2.setLayoutData(fd);
		
		fd1.right = new FormAttachment(control2, -xIncrement);
	}
	
	/**
	 * Adjust all the attachments.
	 */
	public void adjust()
	{
		if (labels.size() <= 1)
		{
			return;
		}
		
		// See which label is widest.
		Control widestLabel = null;
		int widestX = 0;
		for (Control label : labels)
		{
			Point size = label.getSize();
			if (size.x > widestX)
			{
				widestLabel = label;
				widestX = size.x;
			}
		}
		if (widestLabel == null)
		{
			return;
		}
		
		// Adjust all the labels
		FormAttachment newRight = new FormAttachment(widestLabel, 0, SWT.RIGHT);
		for (Control label : labels)
		{
			FormData fd = (FormData)label.getLayoutData();
			if (label == widestLabel)
			{
				fd.left = leftA;
				fd.right = null;
			}
			else
			{
				fd.left = null;
				fd.right = newRight;
			}
			label.setLayoutData(fd);
		}
		
		widestLabel.getParent().layout();
		haveAdjusted = true;
	}
	
	/**
	 * Our first label has been painted.  We have an opportunity to adjust.
	 * 
	 * @param e Ignored. Required as part of the callback
	 */
	public void paintControl(PaintEvent e)
	{
		if (!haveAdjusted)
		{
			adjust();
		}
	}
	
	/**
	 * Forget this widget.
	 * 
	 * @param label This is the label that corresponds to what we're removing.
	 */
	public void removePair(Control label) {
		for (int index = 0; index < labels.size(); ++index) {
			if (labels.get(index) == label) {
				removeAt(index);
				break;
			}
		}
	}
	
	/**
	 * Remove from this index.
	 * 
	 * @param index Which one.
	 */
	private void removeAt(int index) {
		Control label = labels.get(index);
		Control control = controls.get(index);
		FormData thisFD = (FormData)label.getLayoutData();
		FormData thisControlFD = (FormData)control.getLayoutData();
		
		if (index + 1 < labels.size()) {
			Control belowLabel = labels.get(index + 1);
			FormData belowFD = (FormData)belowLabel.getLayoutData();
			belowFD.top = thisFD.top;
			belowLabel.setLayoutData(belowFD);
		}
		if (index > 0) {
			Control aboveLabel = labels.get(index - 1);
			Control aboveControl = controls.get(index - 1);
			FormData aboveFD = (FormData)aboveLabel.getLayoutData();
			FormData aboveControlFD = (FormData)aboveControl.getLayoutData();
			aboveFD.bottom = thisFD.bottom;
			aboveLabel.setLayoutData(aboveFD);
			
			aboveControlFD.bottom = thisControlFD.bottom;
			aboveControl.setLayoutData(aboveControlFD);
		}
		
		labels.remove(index);
		controls.remove(index);
		haveAdjusted = false;
	}
}
