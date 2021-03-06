/*
 * Copyright 2014 Actelion Pharmaceuticals Ltd., Gewerbestrasse 16, CH-4123 Allschwil, Switzerland
 *
 * This file is part of DataWarrior.
 * 
 * DataWarrior is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * DataWarrior is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with DataWarrior.
 * If not, see http://www.gnu.org/licenses/.
 *
 * @author Thomas Sander
 */

package com.actelion.research.datawarrior;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.actelion.research.calc.ProgressController;

public class DEProgressPanel extends JPanel implements ActionListener,ProgressController {
	private static final long serialVersionUID = 0x20140404;

	private static final int SHOW_ERROR = 1;
	private static final int START_PROGRESS = 2;
	private static final int UPDATE_PROGRESS = 3;
	private static final int STOP_PROGRESS = 4;

	static private ImageIcon sIcon;

	private JProgressBar mProgressBar = new JProgressBar();
	private JLabel mProgressLabel = new JLabel();
	private JButton mCancelButton;
	private volatile boolean mCancelAction;

	public DEProgressPanel(boolean showCancelButton) {
		Font font = new Font("Helvetica", Font.BOLD, 12);

		mProgressLabel.setFont(font);

		mProgressBar.setVisible(false);
		mProgressBar.setPreferredSize(new Dimension(80,8));
		mProgressBar.setMaximumSize(new Dimension(80,8));
		mProgressBar.setMinimumSize(new Dimension(80,8));
		mProgressBar.setSize(new Dimension(80,8));

		double[][] size = { {TableLayout.PREFERRED, 4, TableLayout.PREFERRED, 4, TableLayout.FILL},
							{TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL} };

		setLayout(new TableLayout(size));
		add(mProgressBar, "0,1");
		add(mProgressLabel, "4,0,4,2");

		if (showCancelButton) {
			if (sIcon == null)
				sIcon = new ImageIcon(this.getClass().getResource("/images/closeButton.png"));
			mCancelButton = createButton(sIcon, 14, 14, "close");
			mCancelButton.setVisible(false);
			mCancelButton.addActionListener(this);
			add(mCancelButton, "2,0,2,2");
			}
		}

	private JButton createButton(ImageIcon icon, int w, int h, String command) {
		JButton button = new JButton(icon);
		if ("quaqua".equals(System.getProperty("com.actelion.research.laf"))) {
			w += 4;
			h += 3;
			button.putClientProperty("Quaqua.Component.visualMargin", new Insets(1,1,1,1));
			button.putClientProperty("Quaqua.Button.style", "bevel");
			}
		button.setPreferredSize(new Dimension(w, h));
		if (command != null) {
			button.addActionListener(this);
			button.setActionCommand(command);
			}
		return button;
		}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("close")) {
			mCancelAction = true;
			return;
			}
		}

	/**
	 * Resets the progress cancelled flag, which is set by pressing the cancel button.
	 */
	public void initializeThreadMustDie() {
		mCancelAction = false;
		}

	@Override
	public void startProgress(String text, int min, int max) {
		doActionThreadSafe(START_PROGRESS, text, min, max);
		}

	@Override
	public void updateProgress(int value) {
		doActionThreadSafe(UPDATE_PROGRESS, null, value, 0);
		}

	@Override
	public void stopProgress() {
		doActionThreadSafe(STOP_PROGRESS, null, 0, 0);
		}

	@Override
	public void showErrorMessage(final String message) {
		doActionThreadSafe(SHOW_ERROR, message, 0, 0);
		}		

	public void showMessage(final String message) {
		mProgressLabel.setForeground(message.length() == 0 ? Color.BLACK : Color.RED);
		mProgressLabel.setText(message);
		}		

	@Override
	public boolean threadMustDie() {
		return mCancelAction;
		}

	private void doActionThreadSafe(final int action, final String text, final int v1, final int v2) {
		if (SwingUtilities.isEventDispatchThread()) {
			doAction(action, text, v1, v2);
			}
		else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						doAction(action, text, v1, v2);
						}
					});
				}
			catch (Exception e) {}
			}
		}

	private void doAction(final int action, final String text, final int v1, final int v2) {
		switch (action) {
		case SHOW_ERROR:
			Component c = this;
			while (!(c instanceof Frame))
				c = c.getParent();
			JOptionPane.showMessageDialog((Frame)c, text);
			break;
		case START_PROGRESS:
			mProgressBar.setVisible(true);
			mProgressBar.setMinimum(v1);
			mProgressBar.setMaximum(v2);
			mProgressBar.setValue(v1);
			if (mCancelButton != null)
				mCancelButton.setVisible(true);
			mProgressLabel.setText(text);
			break;
		case UPDATE_PROGRESS:
			int value = (v1 >= 0) ? v1 : mProgressBar.getValue()-v1;
			mProgressBar.setValue(value);
			break;
		case STOP_PROGRESS:
			mProgressLabel.setText("");
			mProgressBar.setValue(mProgressBar.getMinimum());
			mProgressBar.setVisible(false);
			if (mCancelButton != null)
				mCancelButton.setVisible(false);
			break;
			}
		}
	}
