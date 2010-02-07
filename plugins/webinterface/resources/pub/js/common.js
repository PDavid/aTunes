var lastTitle = null;
var lastArtist = null;

/*
 * Name of package to call actions
 */
function getActionsPackageName() {
	return 'net.sourceforge.atunes.plugins.webinterface.actions';
}

/*
 * Url to call a player action
 */
function getPlayerActionUrl(option) {
	return '/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=VoidTemplate&option='+option;
}

/*
 * Returns a string representing minutes and seconds of a given amount of milliseconds
 */
function millisecondsToString(milliseconds) {
    var seconds = milliseconds / 1000;
    var minutes = Math.floor(seconds / 60);
    seconds = Math.round(seconds % 60);
    return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
}

/*
 * Returns a string representing minutes and seconds of a given amount of seconds
 */
function secondsToString(seconds) {
    var minutes = Math.floor(seconds / 60);
    seconds = Math.round(seconds % 60);
    return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
}

/*
 * Volume down
 */
function volumeDown() {
	$.get(getPlayerActionUrl('volumedown'));
}

/*
 * Volume up
 */
function volumeUp() {
	$.get(getPlayerActionUrl('volumeup'));
}

/*
 * Stop player
 */
function stop() {
	$('#elapsed_time').fadeOut('slow');
	$('#remaining_time').fadeOut('slow');
	$('#progressbar').fadeOut('slow');
	callPlayerControl('stop');
}

/*
 * Move to previous
 */
function previous() {
	callPlayerControl('previous');
}

/*
 * Play or pause
 */
function play() {
	callPlayerControl('play');
}

/*
 * Move to next
 */
function next() {
	callPlayerControl('next');
}

/*
 * Called to perform a player action
 */
function callPlayerControl(option) {
	$.get(getPlayerActionUrl(option), update);
}

/*
 * Updates current image
 */
function updateImage() {
	var randomnumber = Math.floor(Math.random()*10000)	
	$('#current_image').attr('src', "/images?action="+getActionsPackageName()+".CurrentImageAction&random="+randomnumber);
}

/*
 * Checks if title and / or artist have changed and updates image
 */
function checkAndUpdateImage() {
	/* See if audio object has changed */
	var currentTitleDiv = document.getElementById('title');
	var currentTitle;
	if (currentTitleDiv != null) {
		currentTitle = currentTitleDiv.firstChild.data;
	}
	if (currentTitle.indexOf('Play list is empty') != -1) {
		$('#current_image').hide();
		$('.abutton').hide();
		$('.volume').hide();
	} else {
		$('#current_image').show();
		$('.abutton').show();
		$('.volume').show();
	}
	var currentArtistDiv = document.getElementById('artist');
	var currentArtist;
	if (currentArtistDiv != null) {
		currentArtist = currentArtistDiv.firstChild.data;
	}	
	if (lastTitle == null || lastArtist == null || (lastTitle != currentTitle && lastArtist != currentArtist)) {
		lastTitle = currentTitle;
		lastArtist = currentArtist;
		if (currentTitle.indexOf('Play list is empty') == -1) {
			updateImage();
		}
	}
}

/*
 * Updates state for player controls
 */
function updateState() {
	var state = $('#player_state').text();
	
	// Use state to set play or pause button
	if (state == "PLAYING" || state == "RESUMING") {
		$('#play').attr('src','images/pause.png');
	} else {
		$('#play').attr('src','images/play.png');
	}
	
	// If stopped hide time and progressbar
	if (state == "STOPPED") {
		$('#elapsed_time').fadeOut('slow');
		$('#remaining_time').fadeOut('slow');
		$('#progressbar').fadeOut('slow');
	} else {
		$('#elapsed_time').fadeIn('slow');
		$('#remaining_time').fadeIn('slow');
		$('#progressbar').fadeIn('slow');
	}
}

/*
 * Retrieves current player state
 */
function updateControls() {
	// Get player state
	$('#player_state').load('/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=PlayerControlsTemplate&option=state', updateState);
}

/*
 * Updates time controls
 */
function updateTimeControls() {
	var elapsedTime = $('#elapsed_time_span').text();
	var totalTime = $('#total_time_span').text();
	var remainingTime = totalTime - elapsedTime;
	
	$('#elapsed_time').attr('innerHTML', '<span>'+millisecondsToString(elapsedTime)+'</span>');
	$('#remaining_time').attr('innerHTML', '<span>-'+millisecondsToString(remainingTime)+'</span>');
	
	var progress = Math.round((elapsedTime / totalTime) * 100);
	
	$('#progressbar').progressbar('option', 'value', progress);
}

/*
 * Retrieves time
 */
function updateTime() {
	// Get player state
	$('#time').load('/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=TimeTemplate&option=time', updateTimeControls);
}

/*
 * Updates all controls and information about player
 */
function update() {
	updateTime();
	updateControls();
	
	// Update properties and image
	$("#current_ao_properties").load('/velocity?action='+getActionsPackageName()+
			'.GetCurrentAudioObjectAction&template=AudioObjectPropertiesTemplate.html', checkAndUpdateImage);
}

/*
 * Common tasks needed to initialize
 */
function commonInitialize() {
	$("#progressbar").hide();
	$("#progressbar").progressbar({
		value: 0
	});
	$('#elapsed_time').hide();
	$('#remaining_time').hide();

	update();
	setInterval('update()',5000);	
}
