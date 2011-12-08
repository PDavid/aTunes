/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.gui.views.controls.JavaVirtualMachineStatisticsTableModel;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.model.IAboutDialog;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateMidnight;

public final class AboutDialog extends AbstractCustomDialog implements IAboutDialog {

    private static final long serialVersionUID = 8666235475424750562L;

    /** The table model. */
    private JavaVirtualMachineStatisticsTableModel tableModel = new JavaVirtualMachineStatisticsTableModel();

    /** The license text. */
    private String licenseText = getLicenseText();

    private Timer timer = new Timer(2000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
        	tableModel.fireTableDataChanged();
        }

    });

    private IDesktop desktop;

    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * @param desktop
     */
    public void setDesktop(IDesktop desktop) {
		this.desktop = desktop;
	}
    
    /**
     * Instantiates a new about dialog.
     * 
     * @param frame
     * @param lookAndFeelManager
     */
    public AboutDialog(IFrame frame, ILookAndFeelManager lookAndFeelManager) {
        super(frame, 600, 550, true, CloseAction.DISPOSE, lookAndFeelManager.getCurrentLookAndFeel());
        this.lookAndFeelManager = lookAndFeelManager;
    }
    
    /**
     * Initializes dialog after finishing setting dependencies
     */
    public void initialize() {
        add(getContent(lookAndFeelManager.getCurrentLookAndFeel()));
        setResizable(false);    	
    }

    /**
     * Gets the content.
     * 
     * @param lookAndFeel
     * @return
     */
    private JPanel getContent(ILookAndFeel lookAndFeel) {
        UrlLabel title = new UrlLabel(desktop, StringUtils.getString(Constants.APP_NAME, " ", Constants.VERSION.toString()), Constants.APP_WEB);
        title.setFont(lookAndFeel.getAboutBigFont());
        title.setFocusPainted(false);
        JLabel description = new JLabel(Constants.APP_DESCRIPTION);

        JLabel icon = new JLabel(Images.getImage(Images.APP_LOGO_90));

        JTextArea license = new CustomTextArea(licenseText);
        license.setEditable(false);
        license.setLineWrap(true);
        license.setWrapStyleWord(true);
        license.setOpaque(false);
        license.setBorder(BorderFactory.createEmptyBorder());

        UrlLabel contributors = new UrlLabel(desktop, I18nUtils.getString("CONTRIBUTORS"), Constants.CONTRIBUTORS_WEB);

        JTable propertiesTable = lookAndFeel.getTable();
        propertiesTable.setModel(tableModel);
        propertiesTable.setDefaultRenderer(Object.class, lookAndFeel.getTableCellRenderer(GuiUtils.getComponentOrientationTableCellRendererCode(lookAndFeel)));
        JScrollPane propertiesScrollPane = lookAndFeel.getTableScrollPane(propertiesTable);

        JButton close = new JButton(I18nUtils.getString("CLOSE"));
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(I18nUtils.getString("LICENSE"), license);
        tabbedPane.addTab(I18nUtils.getString("PROPERTIES"), propertiesScrollPane);

        return createPanel(title, description, icon, contributors, close, tabbedPane);
    }

	/**
	 * @param title
	 * @param description
	 * @param icon
	 * @param contributors
	 * @param close
	 * @param tabbedPane
	 * @return
	 */
	private JPanel createPanel(UrlLabel title, JLabel description, JLabel icon,
			UrlLabel contributors, JButton close, JTabbedPane tabbedPane) {
		JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.1;
        c.weightx = 1;
        c.insets = new Insets(0, 40, 0, 0);
        c.anchor = GuiUtils.getComponentOrientation().isLeftToRight() ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        panel.add(title, c);
        c.gridy = 1;
        panel.add(description, c);
        c.gridy = 2;
        panel.add(contributors, c);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 0.1;
        c.anchor = GuiUtils.getComponentOrientation().isLeftToRight() ? GridBagConstraints.EAST : GridBagConstraints.WEST;
        panel.add(icon, c);

        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 20, 10, 20);
        panel.add(tabbedPane, c);

        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0;
        c.insets = new Insets(0, 20, 10, 20);
        panel.add(close, c);
		return panel;
	}

    /**
     * Gets the license text.
     * 
     * @return the license text
     */
    private String getLicenseText() {
        return StringUtils.getString("Copyright (C) 2006-", DateMidnight.now().year().get(), "  The aTunes Team\n\n", "This program is free software; you can redistribute it and/or ",
                "modify it under the terms of the GNU General Public License ", "as published by the Free Software Foundation; either version 2 ",
                "of the License, or (at your option) any later version.\n\n", "This program is distributed in the hope that it will be useful, ",
                "but WITHOUT ANY WARRANTY; without even the implied warranty of ", "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the ",
                "GNU General Public License for more details.\n\n", "You should have received a copy of the GNU General Public License ",
                "along with this program; if not, write to the\n\nFree Software ", "Foundation, Inc.\n51 Franklin Street, Fifth Floor\nBoston, MA\n02110-1301, USA");
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            timer.start();
        } else {
            timer.stop();
        }
        super.setVisible(visible);
    }
    
    @Override
    public void showDialog() {
    	setVisible(true);
    }
}
