/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class AboutDialog extends CustomModalDialog {

    /*
     * Static attributes with inmutable data to be shown in properties table
     */
    static final String[] VERSION = new String[] { "Version", Constants.VERSION.toString() };
    static final String[] JAVA_VERSION = new String[] { "Java Runtime Enviroment", System.getProperty("java.version") };
    static final String[] OS_NAME = new String[] { "OS", StringUtils.getString(System.getProperty("os.name"), " (", System.getProperty("os.arch"), ')') };

	/**
     * The Class AboutDialogTableModel.
     */
    private static class AboutDialogTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1786557125033788184L;

        /** The values to show. */
        private List<String[]> valuesToShow;

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

        /**
         * Gets the data.
         * 
         * @return the data
         */
        private List<String[]> getData() {
            MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            List<GarbageCollectorMXBean> garbageCollectionMXBeans = ManagementFactory.getGarbageCollectorMXBeans();

            List<String[]> data = new ArrayList<String[]>();
            data.add(VERSION);
            data.add(JAVA_VERSION);
            data.add(OS_NAME);
            data.add(new String[] { "Used Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getUsed()) });
            data.add(new String[] { "Max Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getMax()) });
            data.add(new String[] { "Initial Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getInit()) });
            data.add(new String[] { "Committed Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getCommitted()) });
            data.add(new String[] { "Used Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getUsed()) });
            data.add(new String[] { "Max Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getMax()) });
            data.add(new String[] { "Initial Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getInit()) });
            data.add(new String[] { "Committed Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getCommitted()) });
            data.add(new String[] { "Uptime", StringUtils.fromSecondsToHoursAndDays(runtimeMXBean.getUptime() / 1000) });
            data.add(new String[] { "Total Loaded Classes Count", String.valueOf(classLoadingMXBean.getTotalLoadedClassCount()) });
            data.add(new String[] { "Loaded Classes Count", String.valueOf(classLoadingMXBean.getLoadedClassCount()) });
            data.add(new String[] { "Unloaded Classes Count", String.valueOf(classLoadingMXBean.getUnloadedClassCount()) });
            data.add(new String[] { "Thread Count", String.valueOf(threadMXBean.getThreadCount()) });
            long collectionCount = 0;
            for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectionMXBeans) {
                collectionCount += Math.max(0, garbageCollectorMXBean.getCollectionCount());
            }
            data.add(new String[] { "Garbage Collection Count", String.valueOf(collectionCount) });

            return data;
        }

        @Override
        public int getRowCount() {
            return valuesToShow.size();
        }

        @Override
        public String getValueAt(int rowIndex, int columnIndex) {
            return valuesToShow.get(rowIndex)[columnIndex];
        }

        /**
         * Refresh data.
         */
        public void refreshData() {
            valuesToShow = getData();
        }
    }

    private static final long serialVersionUID = 8666235475424750562L;

    /** The table model. */
    private AboutDialogTableModel tableModel = new AboutDialogTableModel();

    /** The license text. */
    private String licenseText = getLicenseText();

    private Timer timer = new Timer(1000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            tableModel.refreshData();
            tableModel.fireTableDataChanged();
        }

    });

    /**
     * Instantiates a new about dialog.
     * 
     * @param owner
     *            the owner
     */
    public AboutDialog(JFrame owner) {
        super(owner, 600, 550, true);
        setContent(getContent());
        setResizable(false);
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());

        UrlLabel title = new UrlLabel(Constants.APP_NAME + ' ' + Constants.VERSION.toString(), Constants.APP_WEB);
        title.setFont(Fonts.getAboutBigFont());
        title.setFocusPainted(false);
        JLabel description = new JLabel(Constants.APP_DESCRIPTION);

        JLabel icon = new JLabel(Images.getImage(Images.APP_ICON_BIG));

        JTextArea license = new CustomTextArea(licenseText);
        license.setEditable(false);
        license.setLineWrap(true);
        license.setWrapStyleWord(true);
        license.setOpaque(false);
        license.setBorder(BorderFactory.createEmptyBorder());

        UrlLabel contributors = new UrlLabel(I18nUtils.getString("CONTRIBUTORS"), Constants.CONTRIBUTORS_WEB);
        contributors.setFont(Fonts.getAppVersionLittleFont());

        JTable propertiesTable = new JTable(tableModel);
        propertiesTable.setShowGrid(false);
        propertiesTable.setDefaultRenderer(Object.class, 
        		LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(GuiUtils.getComponentOrientationTableCellRendererCode()));
        JScrollPane propertiesScrollPane = new JScrollPane(propertiesTable);

        JButton close = new CustomButton(null, I18nUtils.getString("CLOSE"));
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
        return StringUtils.getString("Copyright (C) 2006-2009  The aTunes Team\n\n", "This program is free software; you can redistribute it and/or ",
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
}
