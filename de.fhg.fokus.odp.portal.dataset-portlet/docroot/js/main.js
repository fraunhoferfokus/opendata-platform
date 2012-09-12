$('#average-rating-box').ready(function(){
	if (typeof packageRating != "undefined") {
		var integerNumber = parseInt(packageRating);
		for (var i=1;i<=integerNumber;i++) {
		if (i<=packageRating) {
			document.getElementById('average-rating-star'+i).src = siteroot+'/images/ratingsgreen.png';
		} else {
			document.getElementById('average-rating-star'+i).src = siteroot +'/images/ratingsgrey.png';
		}		
		}	
}});

function showCommentsInputBox() {
	if (document.getElementById("comments-input-box").style.display == "none") {
		document.getElementById("comments-input-box").style.display = "block";
	} else if (document.getElementById("comments-input-box").style.display == "block") {
		document.getElementById("comments-input-box").style.display = "none";
	} else {
		document.getElementById("comments-input-box").style.display = "block";
	}
};

function showRatingsInputBox() {
	if (document.getElementById("ratings-input-box").style.display == "none") {
		document.getElementById("ratings-input-box").style.display = "block";
	} else if (document.getElementById("ratings-input-box").style.display == "block") {
		document.getElementById("ratings-input-box").style.display = "none";
	} else {
		document.getElementById("ratings-input-box").style.display = "block";
	}
};

function lightStars(count) {
	for (var i=1;i<=5;i++) {
		if (i<=count) {
			document.getElementById('rating-star'+i).src = siteroot+'/images/ratingsgreen.png';
		}
		else {
			document.getElementById('rating-star'+i).src = siteroot +'/images/ratingsgrey.png';
		}
	}
};

function dimStars() {
	lightStars(0);
};