var session = "";

function init () {
    var pass = prompt("Please enter the password to use the web client", "");

    var xmlhttp = null;

    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.open("GET","/GENERATED/LOGIN/" + pass, false);
    xmlhttp.send();

    session = xmlhttp.responseText;

    if (session == "invalid") {
        alert("Invalid password, aborting...");
        document.getElementById("message").innerText = "Failed to validate password";
        return;
    }

    document.getElementById("waiting").setAttribute("class", "");
}