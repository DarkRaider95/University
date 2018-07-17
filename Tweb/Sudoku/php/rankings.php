<?php
require_once("common.php");

#controllo se la richiesta è fatta in POST
if (!isset($_SERVER["REQUEST_METHOD"])){// || $_SERVER["REQUEST_METHOD"]!="POST") {
	header("HTTP/1.1 400 Invalid Request");
	die("ERROR 400: Invalid request - This service accepts only POST requests.");
}

#intestazione json
header("Content-type: application/json");

#controllo che se si è loggati
if(isset($_SESSION["name"])){
	#controllo che ci siano entrambi i parametri
	if (isset($_REQUEST["levels"]) && isset($_REQUEST["start"])) {
		
		$level=$_REQUEST["levels"];
		$start=$_REQUEST["start"];

		#controllo che i parametri siano validi
		if(checkParam($level,$start)){
			#estraggo i dati delle partite dal database
			$rows = ranking($level,$start);
			if ($rows == null) {
				echo"{\n\"values\":false,\n\"error\":\"Empty Table\"";
			} else {
				echo "{\n\"values\":true,\n\"entries\":";
				echo json_encode($rows);
				echo ",\n\"start\":\"".$start."\"";
			}
		} else {
			echo "{\n\"values\":false,\n\"error\":\"Invalid Parameters\"";	
		}
	} else {
		echo "{\n\"values\":false,\n\"error\":\"Missing Parameters\"";
	}
} else {
	echo "{\n\"values\":false,\n\"error\":\"Not Logged\"";
}
echo "\n}\n";

#funzione che verifica che i parametri siano validi
function checkParam($level,$start){
	if(preg_match("/(easy|medium|hard)/", $level) && preg_match("/[0-9]+/", $start)){
		return TRUE;
	} else {
		return FALSE;
	}
}
?>