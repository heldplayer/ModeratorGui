<?php 
require_once('include.php');

header("Content-Type: text/plain");

if (!loggedin()) {
    header("HTTP/1.1 403 Forbidden");
    die();
    return;
}

$type = str_replace("'", "&apos;", $_POST['type']);
$reported = str_replace("'", "&apos;", $_POST['reported']);
$reporter = str_replace("'", "&apos;", $_SESSION['modgui_name']);
$reason = str_replace("'", "&apos;", $_POST['reason']);
$newRank = str_replace("'", "&apos;", $_POST['newRank']);
$previousRank = str_replace("'", "&apos;", $_POST['previousRank']);
$timestamp = time();

$query = "";
$resulttype = 0;

if ($type == "i") {
    $query = "INSERT INTO `mgui_issues` (`reporter`, `reported`, `issue`, `timestamp`)
    VALUES ('$reporter', '$reported', '$reason', '$timestamp')";
    $resulttype = 1;
} elseif ($type == "b") {
    $query = "INSERT INTO `mgui_bans` (`reporter`, `reported`, `reason`, `timestamp`)
    VALUES ('$reporter', '$reported', '$reason', '$timestamp')";
    $resulttype = 2;
} elseif ($type == "u") {
    $query = "INSERT INTO `mgui_unbans` (`reporter`, `reported`, `reason`, `timestamp`)
    VALUES ('$reporter', '$reported', '$reason', '$timestamp')";
    $resulttype = 3;
} elseif ($type == "p") {
    $query = "INSERT INTO `mgui_promotions` (`reporter`, `reported`, `reason`, `prev_rank`, `new_rank`, `timestamp`)
    VALUES ('$reporter', '$reported', '$reason', '$previousRank', '$newRank', '$timestamp')";
    $resulttype = 4;
} elseif ($type == "d") {
    $query = "INSERT INTO `mgui_demotions` (`reporter`, `reported`, `reason`, `prev_rank`, `new_rank`, `timestamp`)
    VALUES ('$reporter', '$reported', '$reason', '$previousRank', '$newRank', '$timestamp')";
    $resulttype = 5;
}

if ($query == "") {
    echo "false";
    die();
    return;
}

$result = mysql_query($query);
$id = mysql_insert_id();

$query = "INSERT INTO `mgui_lists` (`type`, `report_id`, `reporter`, `target`)
VALUES ('$resulttype', '$id', '$reporter', '$reported')";

$result = mysql_query($query);

if (!$result) {
    echo "false\n";
    echo mysql_error();
    die();
    return;
}

echo "true";

?>