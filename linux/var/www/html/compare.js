var fs = require('fs');
fs.readFile('/var/www/html/gps.xml', 'utf-8', function (err, data) {
	if (err) throw err;
	getData(data);
});
var r;
console.log(r);

function download(){
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			getData(this);
		}
	};
	xhttp.open("GET", "gps.xml", true);
	xhttp.send();
}

function getData(xml) {			
	var xmldom = require('xmldom').DOMParser;
	//var parser = new DOMParser();
	var xmlDoc = new xmldom().parseFromString(xml + "</locations>", "application/xml");
	if (xmlDoc == null) console.log("null");
	
	var markers = xmlDoc.getElementsByTagName("location");
	
	var l = markers.length;
	var lat1 = markers[l - 2].getElementsByTagName("latitude")[0].childNodes[0].nodeValue;
	var long1 = markers[l - 2].getElementsByTagName("longitude")[0].childNodes[0].nodeValue;

	var lat2 = markers[l - 1].getElementsByTagName("latitude")[0].childNodes[0].nodeValue;
	var long2 = markers[l - 1].getElementsByTagName("longitude")[0].childNodes[0].nodeValue;
	
	console.log(compare(lat1, long1, lat2, long2));
}

function compare(_lat1, _long1, _lat2, _long2){
	var lat1 = radians(_lat1);
	var lat2 = radians(_lat2);
	
	var haversine = Math.pow(Math.sin(radians(_lat2 - _lat1) / 2), 2) + 
		Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(radians(_long2 - _long1) / 2), 2);
	
	const RADIUS = 6371000; //radius of earth in meters
	
	var distance = RADIUS * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

	var result;
	
	if (distance > 20){
		result = 1;
	}
	else{
		result = 0;
	}
	
	return result;
}

function radians(i) {
  return i * Math.PI / 180;
}
