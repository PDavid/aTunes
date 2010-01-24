/*
 * Tasks for initialization
 */
function initialize() {
	// If screen resolution is smaller than 600 pixels (width or height) redirect to mobile version
	if (screen.width < 600 || screen.height < 600) {
		top.location = '/mobile.html';
	}
	
	commonInitialize();
}