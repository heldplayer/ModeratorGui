var session = "";

function init() {
	var pass = prompt("Please enter the password to use the web client", "");

	var xmlhttp = null;

	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();
	} else {
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.open("GET", "/GENERATED/LOGIN/" + pass, false);
	xmlhttp.send();

	session = xmlhttp.responseText;

	if (session == "invalid") {
		alert("Invalid password, aborting...");
		document.getElementById("dialog").innerText = "Failed to validate password";
		return;
	}

	document.getElementById("waiting").setAttribute("class", "");

	document.getElementById("side").innerText = "Session ID: " + session;
}

function findReporter() {
	document.getElementById("dialog").innerText = "Loading data, please wait...";
	document.getElementById("waiting").setAttribute("class", "visible");

	var xmlhttp = null;

	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();
	} else {
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.open("GET", "/GENERATED/REPORTER/"
			+ document.getElementById("username").value, false);
	xmlhttp.send();

	try {
		var data = eval(xmlhttp.responseText);

		document.getElementById("content").innerHTML = "";

		for (var i = 0; i < data.length; i++) {
			var row = data[i];
			
			var element = document.createElement("div");

			element.setAttribute("class", "report " + row["type"] + (i % 2 == 0 ? "" : " second"));

			element.innerHTML = "<span class=\"reported\">" + row["reported"] + "</span>: <span class=\"reason\">" + row["reason"] + "</span>";
			element.innerHTML += "<br/><span class=\"reporter\">By " + row["reporter"] + "</span> <span class=\"time\">" + row["time"] + "</span>";

			document.getElementById("content").appendChild(element);
		}

		document.getElementById("side").innerText = "Displaying " + data.length
				+ " items";

		document.getElementById("waiting").setAttribute("class", "");

	} catch (ex) {
		document.getElementById("dialog").innerText = "An error occourd while parsing :/";
	}
}

function findReported() {

}