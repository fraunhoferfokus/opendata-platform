function loadSubmit() {

    var ProgressImage = document.getElementById('progress_image');
    var bool = isXlsxFile();
    if(bool){
    	document.getElementById("progress").style.visibility = "visible";
    	return true;
    }
    else{
    	document.getElementById("progress").style.visibility = "hidden";
    	return false;
    }
}

function isXlsxFile() {
	 var form = document.forms["uploadForm"];
	 
	 var file = form.elements["xlsxFile"].value;
	 
	 if(file != "" && file.indexOf(".xlsx", file.length - (".xlsx").length) !== -1){
		return true;
	 }
	 else{
		 alert("Only xlsx files allowed");
		 return false;
	 }
};