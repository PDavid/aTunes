/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The image dialog used in the audio object panel
 * 
 * @author fleax
 */
public final class ImageDialog extends CustomFrame {

    private static final long serialVersionUID = -5163960681035103913L;

    JLabel imageLabel;
    private JScrollPane scrollPane;

    /**
     * Instantiates a new image dialog.
     * 
     * @param parent
     *            the parent
     * @param audioObject
     *            the audio object
     */
    public ImageDialog(JFrame parent, AudioObject audioObject) {
        super(null, Constants.THUMBS_WINDOW_WIDTH, Constants.THUMBS_WINDOW_HEIGHT, parent);
        final ImageIcon[] images;
        ImageIcon[] thumbs;

        if (audioObject instanceof Radio) {
            images = new ImageIcon[] { ImageLoader.getImage(ImageLoader.RADIO_BIG) };
            thumbs = new ImageIcon[] { ImageLoader.getImage(ImageLoader.RADIO) };
        } else if (audioObject instanceof PodcastFeedEntry) {
            images = new ImageIcon[] { ImageLoader.getImage(ImageLoader.RSS_BIG) };
            thumbs = new ImageIcon[] { ImageLoader.getImage(ImageLoader.RSS) };
        } else {
            images = AudioFilePictureUtils.getPicturesForFile((AudioFile) audioObject, -1, -1);
            thumbs = AudioFilePictureUtils.getPicturesForFile((AudioFile) audioObject, Constants.IMAGE_SIZE.getSize(), Constants.IMAGE_SIZE.getSize());
        }
        if (images.length > 1) {
            setTitle(StringUtils.getString(Constants.APP_NAME, " - ", I18nUtils.getString("PICTURES_OF_FILE"), " ", audioObject.getTitle()));
        } else {
            setTitle(StringUtils.getString(Constants.APP_NAME, " - ", I18nUtils.getString("PICTURE_OF_FILE"), " ", audioObject.getTitle()));
        }

        setResizable(images.length > 1);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addContent(images, thumbs);

        GuiUtils.applyComponentOrientation(this);
        enableDisposeActionWithEscapeKey();
        setVisible(true);

    }

    /**
     * Adds the content.
     * 
     * @param images
     *            the images
     * @param thumbs
     *            the thumbs
     */
    private void addContent(final ImageIcon[] images, final ImageIcon[] thumbs) {
        JPanel panel = new JPanel(new BorderLayout());
        imageLabel = new JLabel(scaleImageIfNecessaryToFitWindow(images[0], thumbs[0].getIconHeight()));
        scrollPane = new JScrollPane(imageLabel);
        panel.add(scrollPane, BorderLayout.CENTER);

        if (images.length > 1) {
            FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 10, 25);
            JPanel thumbsPanel = new JPanel(layout);
            for (int i = 0; i < thumbs.length; i++) {
                final ImageIcon imageToShow = images[i];
                if (imageToShow != null) {
                    JButton thumb = new JButton(thumbs[i]);
                    thumb.setPreferredSize(new Dimension(thumbs[i].getIconWidth(), thumbs[i].getIconHeight()));
                    thumb.setSize(thumb.getSize());
                    thumb.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // If image is bigger than scroll pane then scale down to fit
                            imageLabel.setIcon(scaleImageIfNecessaryToFitWindow(imageToShow, thumbs[0].getIconHeight()));
                        }
                    });
                    thumbsPanel.add(thumb);
                }
            }
            JScrollPane scrollPane2 = new JScrollPane(thumbsPanel);
            scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            panel.add(scrollPane2, BorderLayout.SOUTH);
        }
        setContentPane(panel);

    }

    /**
     * Scale image if necessary to fit window.
     * 
     * @param image
     *            the image
     * @param thumbsSize
     *            the thumbs size
     * 
     * @return the image icon
     */
    ImageIcon scaleImageIfNecessaryToFitWindow(ImageIcon image, int thumbsSize) {
        int maxWidth = this.getWidth() - 50;
        int maxHeight = this.getHeight() - thumbsSize - 150;
        if (image.getIconWidth() > maxWidth || image.getIconHeight() > maxHeight) {
            return ImageUtils.scaleImageBicubic(image.getImage(), maxWidth, maxHeight);
        }
        return image;

    }
}
