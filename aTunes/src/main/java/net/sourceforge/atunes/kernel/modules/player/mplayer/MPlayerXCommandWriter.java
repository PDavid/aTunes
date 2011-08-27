package net.sourceforge.atunes.kernel.modules.player.mplayer;

/**
 * MPlayerX for Mac OS X needs a custom command writer as pause / resume needs an additional parameter
 * Information about this parameter has been learn experimentally
 * @author alex
 *
 */
class MPlayerXCommandWriter extends MPlayerCommandWriter {

	
	MPlayerXCommandWriter(Process process) {
		super(process);
	}

	@Override
	void sendPauseCommand() {
        sendCommand("pause 0");
	}
	
	void sendResumeCommand() {
        sendCommand("pause 0");
	};
}
