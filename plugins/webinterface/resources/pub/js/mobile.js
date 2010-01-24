/*
 * Initialize tasks
 */
function initialize() {
	monitorScreenResolution();
	setInterval('monitorScreenResolution()', 1000);
	commonInitialize();
}

/*
 * Changes style according to screen resolution
 */
function monitorScreenResolution() {
	var width = screen.width;
	$('#style320_480').attr('disabled', width >= 480);
	$('#style480_320').attr('disabled', width < 480);
}


