package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

public class PlayPreviousAudioObjectAction extends Action {

    private static final long serialVersionUID = -1177020643937370678L;

    PlayPreviousAudioObjectAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PlayerHandler.getInstance().playPreviousAudioObject();
    }

    @Override
    public String getCommandName() {
        return "previous";
    }
}
