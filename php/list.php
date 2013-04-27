<?php 
require_once('include.php');

header("Content-Type: text/plain");

if (!loggedin()) {
    header("HTTP/1.1 403 Forbidden");
    die();
    return;
}

$flags = isset($_GET['flags']) ? $_GET['flags'] : "ipdbu";

$query = "SELECT `type`, `report_id`
        FROM `mgui_lists`";

$where = false;

if (strpos($flags, "i") !== false) {
    if ($where) {
        $query .= " OR `type` = '1'";
    } else {
        $query = "$query
        WHERE (`type` = '1'";

        $where = true;
    }
}

if (strpos($flags, "b") !== false) {
    if ($where) {
        $query .= " OR `type` = '2'";
    } else {
        $query = "$query
        WHERE (`type` = '2'";

        $where = true;
    }
}

if (strpos($flags, "u") !== false) {
    if ($where) {
        $query .= " OR `type` = '3'";
    } else {
        $query = "$query
        WHERE (`type` = '3'";

        $where = true;
    }
}

if (strpos($flags, "p") !== false) {
    if ($where) {
        $query .= " OR `type` = '4'";
    } else {
        $query = "$query
        WHERE (`type` = '4'";

        $where = true;
    }
}

if (strpos($flags, "d") !== false) {
    if ($where) {
        $query .= " OR `type` = '5'";
    } else {
        $query = "$query
        WHERE (`type` = '5'";

        $where = true;
    }
}

if ($where) {
    $query .= ")";
}

if (isset($_GET['reporter'])) {
    $reporter = $_GET['reporter'];
    $reporter = str_replace("'", "&apos;", $reporter);
    if ($where) {
        $query .= " AND `reporter` LIKE '$reporter%'";
    } else {
        $query = "$query
        WHERE `reporter` LIKE '$reporter%'";

        $where = true;
    }
}
if (isset($_GET['reported'])) {
    $reported = $_GET['reported'];
    $reported = str_replace("'", "&apos;", $reported);
    if ($where) {
        $query .= " AND `target` LIKE '$reported%'";
    } else {
        $query = "$query
        WHERE `target` LIKE '$reported%'";

        $where = true;
    }
}

$query = "$query
ORDER BY `id` DESC
LIMIT 500";

$result = mysql_query($query, $con);

echo mysql_error();

$first = true;

echo "[ \n";

while ($row = mysql_fetch_array($result)) {
    $id = $row['report_id'];
    $type = "";

    switch ($row['type']) {
        case 1:
            $query = "SELECT `reporter`, `reported`, `issue`, `is_closed`, `timestamp`
            FROM `mgui_issues`
            WHERE `id` = '$id'";
            $type = "issue";
            break;
        case 2:
            $query = "SELECT `reporter`, `reported`, `reason`, `timestamp`
            FROM `mgui_bans`
            WHERE `id` = '$id'";
            $type = "ban";
            break;
        case 3:
            $query = "SELECT `reporter`, `reported`, `reason`, `timestamp`
            FROM `mgui_unbans`
            WHERE `id` = '$id'";
            $type = "unban";
            break;
        case 4:
            $query = "SELECT `reporter`, `reported`, `reason`, `prev_rank`, `new_rank`, `timestamp`
            FROM `mgui_promotions`
            WHERE `id` = '$id'";
            $type = "promote";
            break;
        case 5:
            $query = "SELECT `reporter`, `reported`, `reason`, `prev_rank`, `new_rank`, `timestamp`
            FROM `mgui_demotions`
            WHERE `id` = '$id'";
            $type = "demote";
            break;
    }

    $report_result = mysql_query($query, $con);

    if (mysql_num_rows($report_result) == 1) {
        $report = mysql_fetch_array($report_result);

        if ($first) {
            $first = false;
        } else {
            echo ", \n";
        }

        echo "{ \n";
        echo "type: \"$type\", \n";
        if (isset($report['is_closed'])) {
            echo "closed: " . ($report['is_closed'] ? "true" : "false") . ", \n";
        }
        echo "reporter: \"" . $report['reporter'] . "\", \n";
        echo "reported: \"" . $report['reported'] . "\", \n";
        echo "time: \"" . date($dateformat, $report['timestamp']) . "\", \n";
        if (isset($report['prev_rank'])) {
            echo "prev: \"" . $report['prev_rank'] . "\", \n";
        }
        if (isset($report['new_rank'])) {
            echo "new: \"" . $report['new_rank'] . "\", \n";
        }
        if (isset($report['issue'])) {
            echo "reason: \"" . $report['issue'] . "\" \n";
        } else {
            echo "reason: \"" . $report['reason'] . "\" \n";
        }
        echo " }";
    }
}

echo " ]";
?>