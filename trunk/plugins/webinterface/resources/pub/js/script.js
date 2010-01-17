function getActionsPackageName() {
	return 'net.sourceforge.atunes.plugins.webinterface.actions';
}

function updateImage() {
	var imageDiv = document.getElementById('current_player_image');
	var randomnumber = Math.floor(Math.random()*10000)
	var image = '<img class="current_image" src="/images?action='+getActionsPackageName()+'.CurrentImageAction&random='+randomnumber+'">';
	imageDiv.innerHTML = image;
}

function updateProperties() {
	$("#current_ao_properties").load('/velocity?action='+getActionsPackageName()+'.GetCurrentAudioObjectAction&template=AudioObjectPropertiesTemplate.html');
}

function update() {
	updateImage();
	updateProperties();
}

function initialize() {
	update();
	
	setInterval('update()',10000);
}

function callPlayerControl(option) {
	$.get('/velocity?action='+getActionsPackageName()+'.PlayerControlsAction&template=VoidTemplate&option='+option);
	
	update();
}

function stop() {
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
