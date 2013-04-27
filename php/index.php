<!DOCTYPE html>
<html>
<head>
<title>ModeratorGui Web Interface</title>
<link rel="stylesheet" href="./style.css" />
<script type="text/javascript" src="./script.js"></script>
<script type="text/javascript" src="./SHA256.js"></script>
</head>
<body onload="init();">
    <div id="waiting" class="visible">
        <!-- The semi-transparent screen blocker -->
        <div class="background"></div>
        <!-- The dialog box used to display messages -->
        <div class="dialog" id="dialog">Waiting for the page to load, hold on...</div>
    </div>
    <div id="createreport">
        <!-- The semi-transparent screen blocker -->
        <div class="background"></div>
        <!-- The dialog box used to display messages -->
        <div class="dialog" id="reportdialog">
            Create a new report<br />
            <label for="issuesradio"><input type="radio" id="issuesradio" name="reportradio" onchange="updateCriteria();" /> Issue</label>
            <label for="promotionsradio"><input type="radio" id="promotionsradio" name="reportradio" onchange="updateCriteria();" /> Promotion</label>
            <label for="demotionsradio"><input type="radio" id="demotionsradio" name="reportradio" onchange="updateCriteria();" /> Demotion</label>
            <label for="bansradio"><input type="radio" id="bansradio" name="reportradio" onchange="updateCriteria();" /> Ban</label>
            <label for="unbansradio"><input type="radio" id="unbansradio" name="reportradio" onchange="updateCriteria();" /> Unban</label>
            <br />
            Reported user: <input type="text" id="reported" onkeyup="updateCriteria();" /> <br />
            Reason: <input type="text" id="reason" style="width: 300px" onkeyup="updateCriteria();" /> <br />
            Previous rank: <select disabled="disabled" id="previousRank" onchange="updateCriteria();"></select> <br />
            New rank: <select disabled="disabled" id="newRank" onchange="updateCriteria();"></select> <br />
            <button disabled="disabled" id="makeReport" onclick="sendReport();">Report</button> <br />
            <button onclick="abortReport();">Abort</button>
        </div>
    </div>
    <div class="searchbar">
        <!-- Write report -->
        <button onclick="writeReport();">Write Report</button>
        <!-- Display all entries -->
        <button onclick="displayList();">Display all</button>
        <!-- Username to query -->
        Username: <input type="text" id="username" />
        <!-- If you want to query who reported something -->
        <button onclick="findReporter();">Find by reporter</button>
        <!-- If you want to query who was reported -->
        <button onclick="findReported();">Find by reported</button>
        <!-- List of flags for getting reports -->
        <label for="issues"><input type="checkbox" id="issues" checked="checked" /> Issues</label>
        <label for="promotions"><input type="checkbox" id="promotions" checked="checked" /> Promotions</label>
        <label for="demotions"><input type="checkbox" id="demotions" checked="checked" /> Demotions</label>
        <label for="bans"><input type="checkbox" id="bans" checked="checked" /> Bans</label>
        <label for="unbans"><input type="checkbox" id="unbans" checked="checked" /> Unbans</label>
        <!-- Side display -->
        <span id="side"></span>
    </div>
    <table id="outercontent">
        <thead>
            <tr>
                <th class="type">Type</th>
                <th>Reporter</th>
                <th>Reported</th>
                <th>Reason</th>
                <th>Timestamp</th>
                <th>Previous rank</th>
                <th>New rank</th>
            </tr>
        </thead>
        <tbody id="content">
        </tbody>
    </table>
</body>
</html>