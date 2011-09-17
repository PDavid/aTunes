/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.model.IAboutDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class AboutDialog extends AbstractCustomDialog implements IAboutDialog {

    /*
     * Static attributes with immutable data to be shown in properties table
     */
    static final String VERSION = Constants.VERSION.toString();
    static final String JAVA_VERSION = System.getProperty("java.version");
    static final String OS_NAME = StringUtils.getString(System.getProperty("os.name"), " (", System.getProperty("os.arch"), ')');

    /**
     * The Class AboutDialogTableModel.
     */
    private static class AboutDialogTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1786557125033788184L;
        
        private String usedHeapUsage;
		
		private String garbageCollectionCount;

		private String maxHeapUsage;

		private String initHeapUsage;

		private String committedHeapUsage;

		private String usedNonHeapUsage;

		private String maxNonHeapUsage;

		private String initNonHeapUsage;

		private String committedNonHeapUsage;

		private String uptime;

		private String totalLoadedClassCount;

		private String loadedClassCount;

		private String unloadedClassCount;

		private String threadCount;

        /**
         * Instantiates a new about dialog table model.
         */
        AboutDialogTableModel() {
        	refreshData();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Property" : "Value";
        }

        @Override
        public int getRowCount() {
            return 17;
        }

        @Override
        public String getValueAt(int rowIndex, int columnIndex) {
        	if (columnIndex == 0) {
        		switch (rowIndex) {
				case 0: return "Version";
				case 1: return "Java Runtime Enviroment";
				case 2: return "OS";
				case 3: return "Used Heap Space";
				case 4: return "Max Heap Space";
				case 5: return "Initial Heap Space";
				case 6: return "Committed Heap Space";
				case 7: return "Used Non Heap Space";
				case 8: return "Max Non Heap Space";
				case 9: return "Initial Non Heap Space";
				case 10: return "Committed Non Heap Space";
				case 11: return "Uptime";
				case 12: return "Total Loaded Classes Count";
				case 13: return "Loaded Classes Count";
				case 14: return "Unloaded Classes Count";
				case 15: return "Thread Count";
				case 16: return "Garbage Collection Count";
				default:
					break;
				}
        	} else {
        		switch (rowIndex) {
				case 0: return VERSION;
				case 1: return JAVA_VERSION;
				case 2: return OS_NAME;
				case 3: return usedHeapUsage;
				case 4: return maxHeapUsage;
				case 5: return initHeapUsage;
				case 6: return committedHeapUsage;
				case 7: return usedNonHeapUsage;
				case 8: return maxNonHeapUsage;
				case 9: return initNonHeapUsage;
				case 10: return committedNonHeapUsage;
				case 11: return uptime;
				case 12: return totalLoadedClassCount;
				case 13: return loadedClassCount;
				case 14: return unloadedClassCount;
				case 15: return threadCount;
				case 16: return garbageCollectionCount;
				default:
					break;
				}        		
        	}
        	return null;
        }
        
        public void refreshData() {
            MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            usedHeapUsage = StringUtils.fromByteToMegaOrGiga(heapUsage.getUsed());
            maxHeapUsage = StringUtils.fromByteToMegaOrGiga(heapUsage.getMax());
            initHeapUsage = StringUtils.fromByteToMegaOrGiga(heapUsage.getInit());
            committedHeapUsage = StringUtils.fromByteToMegaOrGiga(heapUsage.getCommitted());
            
            MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
            usedNonHeapUsage = StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getUsed());
            maxNonHeapUsage = StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getMax());
            initNonHeapUsage = StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getInit());
            committedNonHeapUsage = StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getCommitted());
            
            uptime = StringUtils.fromSecondsToHoursAndDays(ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
            
            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
            totalLoadedClassCount = String.valueOf(classLoadingMXBean.getTotalLoadedClassCount());
            loadedClassCount = String.valueOf(classLoadingMXBean.getLoadedClassCount());
            unloadedClassCount = String.valueOf(classLoadingMXBean.getUnloadedClassCount());
            
            threadCount = String.valueOf(ManagementFactory.getThreadMXBean().getThreadCount());
            
            long collectionCount = 0;
            for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                collectionCount += Math.max(0, garbageCollectorMXBean.getCollectionCount());
            }
            garbageCollectionCount = String.valueOf(collectionCount);
        }
    }

    private static final long serialVersionUID = 8666235475424750562L;

    /** The table model. */
    private AboutDialogTableModel tableModel = new AboutDialogTableModel();

    /** The license text. */
    private String licenseText = getLicenseText();

    private Timer timer = new Timer(2000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
        	tableModel.refreshData();
            tableModel.fireTableDataChanged();
        }

    });

    /**
     * Instantiates a new about dialog.
     * 
     * @param frame
     */
    public AboutDialog(IFrame frame) {
        super(frame, 600, 550, true, CloseAction.DISPOSE);
        add(getContent());
        setResizable(false);
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());

        UrlLabel title = new UrlLabel(Constants.APP_NAME + ' ' + Constants.VERSION.toString(), Constants.APP_WEB);
        title.setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getAboutBigFont());
        title.setFocusPainted(false);
        JLabel description = new JLabel(Constants.APP_DESCRIPTION);

        JLabel icon = new JLabel(Images.getImage(Images.APP_LOGO_90));

        JTextArea license = new CustomTextArea(licenseText);
        license.setEditable(false);
        license.setLineWrap(true);
        license.setWrapStyleWord(true);
        license.setOpaque(false);
        license.setBorder(BorderFactory.createEmptyBorder());

        UrlLabel contributors = new UrlLabel(I18nUtils.getString("CONTRIBUTORS"), Constants.CONTRIBUTORS_WEB);

        JTable propertiesTable = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTable();
        propertiesTable.setModel(tableModel);
        propertiesTable.setDefaultRenderer(Object.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(
                GuiUtils.getComponentOrientationTableCellRendererCode()));
        JScrollPane propertiesScrollPane = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableScrollPane(propertiesTable);

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
        return StringUtils.getString("Copyright (C) 2006-2010  The aTunes Team\n\n", "This program is free software; you can redistribute it and/or ",
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
