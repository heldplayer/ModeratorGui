var session = "";

function init() {
	var pass = Sha256
			.hash(prompt("Please enter the password to use the web client", ""), false);

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
		document.getElementById("dialog").innerText = "Failed to validate password!";
		return;
	}

	displayList();
}

function displayList() {
	document.getElementById("dialog").innerText = "Loading list...";
	document.getElementById("waiting").setAttribute("class", "visible");

	var xmlhttp = null;

	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();
	} else {
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp
			.open("GET", "/GENERATED/LIST/" + session + "/" + getRequestFlags(), false);
	xmlhttp.send();

	if (xmlhttp.status != 200) {
		document.getElementById("side").innerText = "Error: " + xmlhttp.status + " - " + xmlhttp.statusText;
		document.getElementById("waiting").setAttribute("class", "");
		return;
	}

	try {
		var data = eval(xmlhttp.responseText);

		outputData(data);

		document.getElementById("side").innerText = "Displaying " + data.length + " items";

		document.getElementById("waiting").setAttribute("class", "");

	} catch (ex) {
		document.getElementById("waiting").setAttribute("class", "");
		document.getElementById("side").innerText = "An error occourd while parsing :/";
	}

	document.getElementById("waiting").setAttribute("class", "");
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
	xmlhttp
			.open("GET", "/GENERATED/REPORTER/" + session + "/" + getRequestFlags() + "/" + document
					.getElementById("username").value, false);
	xmlhttp.send();

	if (xmlhttp.status != 200) {
		document.getElementById("side").innerText = "Error: " + xmlhttp.status + " - " + xmlhttp.statusText;
		document.getElementById("waiting").setAttribute("class", "");
		return;
	}

	try {
		var data = eval(xmlhttp.responseText);

		outputData(data);

		document.getElementById("side").innerText = "Displaying " + data.length + " items";

		document.getElementById("waiting").setAttribute("class", "");

	} catch (ex) {
		document.getElementById("waiting").setAttribute("class", "");
		document.getElementById("side").innerText = "An error occourd while parsing :/";
	}
}

function findReported() {
	document.getElementById("dialog").innerText = "Loading data, please wait...";
	document.getElementById("waiting").setAttribute("class", "visible");

	var xmlhttp = null;

	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();
	} else {
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	xmlhttp
			.open("GET", "/GENERATED/REPORTED/" + session + "/" + getRequestFlags() + "/" + document
					.getElementById("username").value, false);
	xmlhttp.send();

	if (xmlhttp.status != 200) {
		document.getElementById("side").innerText = "Error: " + xmlhttp.status + " - " + xmlhttp.statusText;
		document.getElementById("waiting").setAttribute("class", "");
		return;
	}

	try {
		var data = eval(xmlhttp.responseText);

		outputData(data);

		document.getElementById("side").innerText = "Displaying " + data.length + " items";

		document.getElementById("waiting").setAttribute("class", "");

	} catch (ex) {
		document.getElementById("waiting").setAttribute("class", "");
		document.getElementById("side").innerText = "An error occourd while parsing :/";
	}
}

function outputData(data) {
	document.getElementById("content").innerHTML = "";

	for ( var i = 0; i < data.length; i++) {
		var row = data[i];

		var element = document.createElement("tr");

		element
				.setAttribute("class", "report " + row["type"] + (i % 2 == 0 ? "" : " second"));

		element.innerHTML = "<td class=\"type\">" + row["type"] + "</td>";
		element.innerHTML += "<td class=\"reporter\">" + row["reporter"] + "</td>";
		element.innerHTML += "<td class=\"reported\">" + row["reported"] + "</td>";
		element.innerHTML += "<td class=\"reason\">" + row["reason"] + "</td>";
		element.innerHTML += "<td class=\"time\">" + row["time"] + "</td>";

		if (row["type"] == "promote") {
			element.innerHTML += "<td class=\"prev\">" + row["prev"] + "</td>";
			element.innerHTML += "<td class=\"new\">" + row["new"] + "</td>";
		} else if (row["type"] == "demote") {
			element.innerHTML += "<td class=\"prev\">" + row["prev"] + "</td>";
			element.innerHTML += "<td class=\"new\">" + row["new"] + "</td>";
		} else {
			element.innerHTML += "<td class=\"from\">N/A</td>";
			element.innerHTML += "<td class=\"to\">N/A</td>";
		}

		document.getElementById("content").appendChild(element);
	}
}

function getRequestFlags() {
	var flags = "";

	flags += (document.getElementById("issues").checked ? "i" : "");
	flags += (document.getElementById("promotions").checked ? "p" : "");
	flags += (document.getElementById("demotions").checked ? "d" : "");
	flags += (document.getElementById("bans").checked ? "b" : "");
	flags += (document.getElementById("unbans").checked ? "u" : "");

	return flags;
}