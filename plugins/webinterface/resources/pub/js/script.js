var lastTitle = null;
var lastArtist = null;

function getActionsPackageName() {
	return 'net.sourceforge.atunes.plugins.webinterface.actions';
}

function updateImage() {
	var randomnumber = Math.floor(Math.random()*10000)	
	$('#current_image').attr('src', "/images?action="+getActionsPackageName()+".CurrentImageAction&random="+randomnumber);
}

function callUpdateImage() {
	/* See if audio object has changed */
	var currentTitleDiv = document.getElementById('title');
	var currentTitle;
	if (currentTitleDiv != null) {
		currentTitle = currentTitleDiv.firstChild.data;
	}
	var currentArtistDiv = document.getElementById('artist');
	var currentArtist;
	if (currentArtistDiv != null) {
		currentArtist = currentArtistDiv.firstChild.data;
	}
	if (lastTitle == null || lastArtist == null || (lastTitle != currentTitle && lastArtist != currentArtist)) {
		lastTitle = currentTitle;
		lastArtist = currentArtist;
		updateImage();		
	}
}

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

function updateControls() {
	// Get player state
	$('#player_state').load('/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=PlayerControlsTemplate&option=state', updateState);
}

function millisecondsToString(milliseconds) {
    var seconds = milliseconds / 1000;
    var minutes = Math.floor(seconds / 60);
    seconds = Math.round(seconds % 60);
    return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
}

function updateTimeControls() {
	var elapsedTime = $('#elapsed_time_span').text();
	var totalTime = $('#total_time_span').text();
	var remainingTime = totalTime - elapsedTime;
	
	$('#elapsed_time').attr('innerHTML', '<span>'+millisecondsToString(elapsedTime)+'</span>');
	$('#remaining_time').attr('innerHTML', '<span>-'+millisecondsToString(remainingTime)+'</span>');
	
	var progress = Math.round((elapsedTime / totalTime) * 100);
	
	$('#progressbar').progressbar('option', 'value', progress);
}

function updateTime() {
	// Get player state
	$('#time').load('/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=TimeTemplate&option=time', updateTimeControls);
}

function update() {

	updateTime();
	updateControls();
	
	// Update properties and image
	$("#current_ao_properties").load('/velocity?action='+getActionsPackageName()+
			'.GetCurrentAudioObjectAction&template=AudioObjectPropertiesTemplate.html', callUpdateImage);
	
}

function initialize() {
	var style320 = document.getElementById('style320_480');
	var style480 = document.getElementById('style480_320');
	var width = screen.width;
	if (width < 480) {
		style320.disabled = false;
		style480.disabled = true;
	} else {
		style320.disabled = false;
		style480.disabled = true;
	}
	
	$("#progressbar").hide();
	$("#progressbar").progressbar({
		value: 0
	});
	$('#elapsed_time').hide();
	$('#remaining_time').hide();

	update();
	
	setInterval('update()',5000);	
}

function callPlayerControl(option) {
	$.get('/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=VoidTemplate&option='+option);
	/* Adding a callback function to call update seems to not work in mobile device */
	update();
}

function stop() {
	$('#elapsed_time').fadeOut('slow');
	$('#remaining_time').fadeOut('slow');
	$('#progressbar').fadeOut('slow');
	callPlayerControl('stop');
}

function previous() {
	callPlayerControl('previous');
}

function play() {
	callPlayerControl('play');
}

function next() {
	callPlayerControl('next');
}

function volumeDown() {
	$.get('/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=VoidTemplate&option=volumedown');
}

function volumeUp() {
	$.get('/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=VoidTemplate&option=volumeup');
}
