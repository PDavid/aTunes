package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

public class StopCurrentAudioObjectAction extends Action {

    private static final long serialVersionUID = -1177020643937370678L;

    StopCurrentAudioObjectAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PlayerHandler.getInstance().stopCurrentAudioObject(true);
    }

    @Override
    public String getCommandName() {
        return "stop";
    }
}
