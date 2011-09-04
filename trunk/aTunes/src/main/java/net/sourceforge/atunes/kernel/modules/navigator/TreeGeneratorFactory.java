package net.sourceforge.atunes.kernel.modules.navigator;

import net.sourceforge.atunes.model.ViewMode;

class TreeGeneratorFactory {

	/**
	 * Returns generator for given mode
	 * @param viewMode
	 * @return
	 */
	static TreeGenerator getTreeGenerator(ViewMode viewMode) {
		switch (viewMode) {
		case ARTIST: return new ArtistTreeGenerator();
		case ALBUM: return new AlbumTreeGenerator();
		case GENRE: return new GenreTreeGenerator();
		case FOLDER: return new FolderTreeGenerator();
		case YEAR: return new YearTreeGenerator();
		case ARTIST_ALBUM: return new ArtistTreeGenerator();
		default: return null;
		}
	}
		
	

}
