/*
 * assumes loaded jQuery 1.8.1 via
 * var jQuery181 = jQuery.noConflict(true);
 */
jQuery181(function($) {

	var ENTER = function() {
		return 13;
	};

	console.log('using jQuery ' + $().jquery);

	$(' #searchButton, #extendSearchButton').css({
		cursor : 'pointer'
	});

	/*
	 * generalSearchInput searchButton - 'enter' submits form
	 */
	$('#generalSearchInput, #searchButton').bind({
		keypress : function(event) {
			if (event.which == ENTER()) {
				console.log('submit form (enter pressed)');
				$('#searchForm').submit();
				event.preventDefault();
			}
		}
	});

	// searchButton - click submits form
	$('#searchButton').bind({
		click : function() {
			$('#searchForm').submit();
		}
	});

	/*
	 * extendSearchButton 'click' and 'keypress' ENTER toggle display of
	 * advanced search fields
	 */
	$('#extendSearchButton').bind({
		// click : function() {
		// $('#advancedSearchFields').toggle(500);
		// },
		keypress : function(event) {
			if (event.which == ENTER()) {
				$('#advancedSearchFields').toggle(500);
				event.preventDefault();
			}
		}
	});

	$('#extendSearchButton').toggle(function() {
		$(this).attr("value", Liferay.Language.get('extended-search-hide'));
	}, function() {
		$(this).attr("value", Liferay.Language.get('extended-search'));
	});

});

function clearForm(oForm) {

	var elements = oForm.elements;

//	oForm.reset();

	for (i = 0; i < elements.length; i++) {

		field_type = elements[i].type.toLowerCase();

		switch (field_type) {

		case "text":
		case "password":
		case "textarea":
		case "hidden":

			elements[i].value = "";
			break;

		case "radio":
		case "checkbox":
			if (elements[i].checked) {
				elements[i].checked = false;
			}
			break;

		case "select-one":
		case "select-multi":
			elements[i].selectedIndex = 0;
			break;

		default:
			break;
		}
	}
};