<?php
// MySQL username
$mysqlusername = "username";
// MySQL password
$mysqlpassword = "password";
// MySQL server address
$mysqlserver = "server.address";

// MySQL database that should be connected to
$mysqldatabase = "database";

// The format to put time in, see http://php.net/manual/en/function.date.php
$dateformat = "m-d-Y";

// List of ranks that are available
$ranks = Array(
        "default",
        "member",
        "admin"
);

// Accounts and their passwords
$accounts = Array(
        "admin" => "lolpassword",
        "moderator" => "icanhazpassword",
        "overlord" => "gimmecookies"
);

// Do NOT edit anything below this point unless you know what you're doing!
session_start();

$con = @mysql_connect($mysqlserver, $mysqlusername, $mysqlpassword);

if (mysql_error() != "") {
    header("HTTP/1.1 503 Service Unavailable");
    die();
    return;
}

mysql_select_db($mysqldatabase);

foreach ($accounts as $key => $value) {
    $accounts[$key] = hash("sha256", $value);
}

function loggedin() {
    global $accounts;
    if (isset($_SESSION['modgui_name']) && isset($_SESSION['modgui_pass'])) {
        $name = $_SESSION['modgui_name'];
        $pass = $_SESSION['modgui_pass'];

        if (isset($accounts[$name]) && $accounts[$name] == $pass) {
            return true;
        }
    }

    return false;
}

?>