/*
 * Tasks for initialization
 */
function initialize() {
	// If screen resolution is smaller than 600 pixels (width or height) redirect to mobile version
	if (screen.width < 600 || screen.height < 600) {
		top.location = '/mobile.html';
	}
	
	commonInitialize();

	
	
	loadPlayLists();
}
/*
 * Setups play lists
 */
function setupPlaylists() {
	
	$('.playlist_table').chromatable();
	
	/* Transform seconds to string */
	$('.length_column').each(function(index) {
	  $(this).text(secondsToString($(this).text()));
    });
	
	/* Add CSS class to odd rows */
	$(".row:odd").addClass('odd_row');
	
	/* Add link to title column */
	$('.title_column').each(function(index){
		$(this).attr('innerHTML', '<a href="javascript:playElement(' + index + ')">' + $(this).text() + '</a>');	     
    });
}

/*
 * Loads play lists
 */
function loadPlayLists() {
	$('#playlist_div').load('/velocity?action='+getActionsPackageName()+'.GetPlayListsAction&template=GetPlayListsTemplate.html', function() {
		$('.playlist_link').each(function(index) {
			$(this).attr('href','/velocity?action='+getActionsPackageName()+'.GetPlayListAction&template=PlayListTemplate.html&playlist='+index);
		});
		
		$('#playlist_div').tabs();
	});	
}

/*
 * Starts playing element at index position
 */
function playElement(index) {
	alert('Not yet done');
}