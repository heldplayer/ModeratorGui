<?php 
require_once('include.php');

if (isset($_POST['name']) && isset($_POST['pass'])) {
    $name = $_POST['name'];
    $pass = $_POST['pass'];

    if (isset($accounts[$name]) && $accounts[$name] == $pass) {
        echo "true";

        $_SESSION['modgui_name'] = $name;
        $_SESSION['modgui_pass'] = $accounts[$name];

        foreach ($ranks as $rank) {
            echo "/$rank";
        }

        die();
        return;
    }
}

echo "invalid";
?>