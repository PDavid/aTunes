package net.sourceforge.atunes.kernel.modules.statistics;

public class StatisticsAlbum {
	
	private String artist;
	
	private String album;

	/**
	 * @param artist
	 * @param album
	 */
	protected StatisticsAlbum(String artist, String album) {
		super();
		this.artist = artist;
		this.album = album;
	}

	/**
	 * @return the artist
	 */
	protected String getArtist() {
		return artist;
	}

	/**
	 * @param artist the artist to set
	 */
	protected void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the album
	 */
	protected String getAlbum() {
		return album;
	}

	/**
	 * @param album the album to set
	 */
	protected void setAlbum(String album) {
		this.album = album;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatisticsAlbum other = (StatisticsAlbum) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		return true;
	}

}
