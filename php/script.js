function init() {
    if (window.location.protocol != "http:") {
        document.getElementById("dialog").innerText = "Page is being accessed incorrectly!";
        alert("Page is being accessed incorrectly! Redirecting...");
        window.location.href = "http://127.0.0.1:8273/";
        return;
    }
    var username = prompt("Please enter username to log in to", "");
    var pass = Sha256.hash(prompt("Please enter the password to use the web client", ""), false);

    var xmlhttp = null;
    var request = "name=" + username + "&pass=" + pass;

    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.open("POST", "login.php", false);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send(request);

    var result = xmlhttp.responseText.split("/")

    var text = "<option value=\"null\">Select rank</option>";

    for (var i = 1; i < result.length; i++) {
        text += "<option value=\"" + result[i] + "\">" + result[i] + "</option>";
    }

    document.getElementById("previousRank").innerHTML = text;
    document.getElementById("newRank").innerHTML = text;

    if (result[0] == "invalid") {
        document.getElementById("dialog").innerText = "Failed to validate password!";
        return;
    }

    displayList();
}

var criteria = new Array();
criteria[0] = false;
criteria[1] = false;
criteria[2] = false;
criteria[3] = false;
criteria[4] = false;
criteria[5] = false;

function sendReport() {
    if (criteria[0] && criteria[1] && criteria[2] && criteria[4] && criteria[5]) {
        document.getElementById("dialog").innerText = "Creating report...";
        document.getElementById("waiting").setAttribute("class", "visible");
        document.getElementById("createreport").setAttribute("class", "");

        var xmlhttp = null;

        if (window.XMLHttpRequest) {
            xmlhttp = new XMLHttpRequest();
        } else {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }

        var flags = getReportFlags();
        var reported = document.getElementById("reported").value;
        var reason = document.getElementById("reason").value;
        var newRank = document.getElementById("newRank");
        newRank = newRank.options[newRank.selectedIndex].value;
        var previousRank = document.getElementById("previousRank");
        previousRank = previousRank.options[previousRank.selectedIndex].value;

        var request = "type=" + flags + "&reported=" + reported + "&reason=" + reason + "&newRank=" + newRank + "&previousRank=" + previousRank;

        xmlhttp.open("POST", "report.php", false);
        xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlhttp.send(request);

        response = xmlhttp.responseText;
        
        alert(response);

        if (response == "true") {
            displayList();
        } else if (response.status != 200) {
            document.getElementById("side").innerText = "Error: " + xmlhttp.status + " - " + xmlhttp.statusText;
            document.getElementById("waiting").setAttribute("class", "");
            return;
        }
    } else {
        updateCriteria();
    }
}

function writeReport() {
    // criteria[0]
    document.getElementById("issuesradio").checked = false;
    document.getElementById("promotionsradio").checked = false;
    document.getElementById("demotionsradio").checked = false;
    document.getElementById("bansradio").checked = false; // criteria[3]
    document.getElementById("unbansradio").checked = false; // criteria[3]
    // criteria[1]
    document.getElementById("reported").value = "";
    // criteria[2]
    document.getElementById("reason").value = "";
    // if criteria[3] then criteria[4]
    document.getElementById("previousRank").disabled = true;
    document.getElementById("previousRank").selectedIndex = 0;
    // if criteria[3] then criteria[5]
    document.getElementById("newRank").disabled = true;
    document.getElementById("newRank").selectedIndex = 0;
    // if criteria[0] and criteria[1] and criteria[2] and if criteria[3] then criteria[4] and criteria[5]
    document.getElementById("makeReport").disabled = true;

    document.getElementById("createreport").setAttribute("class", "visible");
    
    updateCriteria();
}

function abortReport() {
    document.getElementById("createreport").setAttribute("class", "");
}

function updateCriteria() {
    if (document.getElementById("issuesradio").checked) {
        criteria[0] = true;
        criteria[3] = false;
    } else if (document.getElementById("promotionsradio").checked) {
        criteria[0] = true;
        criteria[3] = true;
    } else if (document.getElementById("demotionsradio").checked) {
        criteria[0] = true;
        criteria[3] = true;
    } else if (document.getElementById("bansradio").checked) {
        criteria[0] = true;
        criteria[3] = false;
    } else if (document.getElementById("unbansradio").checked) {
        criteria[0] = true;
        criteria[3] = false;
    } else {
        criteria[0] = false;
        criteria[3] = false;
    }

    if (document.getElementById("reported").value != "") {
        criteria[1] = true;
    } else {
        criteria[1] = false;
    }

    if (document.getElementById("reason").value != "") {
        criteria[2] = true;
    } else {
        criteria[2] = false;
    }

    if (criteria[3]) {
        document.getElementById("previousRank").disabled = false;
        document.getElementById("newRank").disabled = false;

        if (document.getElementById("previousRank").selectedIndex > 0) {
            criteria[4] = true;
        } else {
            criteria[4] = false;
        }

        if (document.getElementById("newRank").selectedIndex > 0) {
            criteria[5] = true;
        } else {
            criteria[5] = false;
        }
    } else {
        criteria[4] = true;
        criteria[5] = true;
        document.getElementById("previousRank").disabled = true;
        document.getElementById("newRank").disabled = true;
    }
    
    if (criteria[0] && criteria[1] && criteria[2] && criteria[4] && criteria[5]) {
        document.getElementById("makeReport").disabled = false;
    } else {
        document.getElementById("makeReport").disabled = true;
    }
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
    xmlhttp.open("GET", "list.php?flags=" + getRequestFlags(), false);
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
    var username = document.getElementById("username").value;

    if (username == "") {
        return;
    }

    document.getElementById("dialog").innerText = "Loading data, please wait...";
    document.getElementById("waiting").setAttribute("class", "visible");

    var xmlhttp = null;

    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xmlhttp.open("GET", "list.php?flags=" + getRequestFlags() + "&reporter=" + username, false);
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

    var username = document.getElementById("username").value;

    if (username == "") {
        return;
    }

    document.getElementById("dialog").innerText = "Loading data, please wait...";
    document.getElementById("waiting").setAttribute("class", "visible");

    var xmlhttp = null;

    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xmlhttp.open("GET", "list.php?flags=" + getRequestFlags() + "&reported=" + username, false);
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

        element.setAttribute("class", "report " + row["type"] + (i % 2 == 0 ? "" : " second"));

        element.innerHTML = "<td class=\"type\">" + row["type"] + "</td>";
        element.innerHTML += "<td class=\"reporter\">" + row["reporter"] + "</td>";
        element.innerHTML += "<td class=\"reported\">" + row["reported"] + "</td>";
        element.innerHTML += "<td class=\"reason\">" + decodeURIComponent(row["reason"]) + "</td>";
        element.innerHTML += "<td class=\"time\">" + row["time"] + "</td>";

        if (row["type"] == "issue" && row["closed"] == true) {
            element.setAttribute("class", "report " + row["type"] + (i % 2 == 0 ? "" : " second") + " closed");
        }

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

function getReportFlags() {
    var flags = "";

    flags += (document.getElementById("issuesradio").checked ? "i" : "");
    flags += (document.getElementById("promotionsradio").checked ? "p" : "");
    flags += (document.getElementById("demotionsradio").checked ? "d" : "");
    flags += (document.getElementById("bansradio").checked ? "b" : "");
    flags += (document.getElementById("unbansradio").checked ? "u" : "");

    return flags;
}