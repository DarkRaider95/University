<?php
require_once("common.php");

#controllo se la richiesta è fatta in POST
if (!isset($_SERVER["REQUEST_METHOD"]) || $_SERVER["REQUEST_METHOD"]!="POST") {
	header("HTTP/1.1 400 Invalid Request");
	die("ERROR 400: Invalid request - This service accepts only POST requests.");
}

#intestazione json
header("Content-type: application/json");

#controllo che se si è loggati
if(isset($_SESSION["name"])){
	#controllo che il parametro sia presente
	if (isset($_REQUEST["levels"])) {
		
		$level=$_REQUEST["levels"];
		#verifico che il parametro sia valido
		if(checkParam($level)){
			#conto le righe della tabella con uno specifico livello
			$count = count_row($level);			
			echo"{\n\"status\":true,\n\"count\":\"".$count."\"";		
		} else {
			echo "{\n\"status\":false,\n\"error\":\"Invalid Parameters\"";	
		}
	} else {
		echo "{\n\"status\":false,\n\"error\":\"Missing Parameters\"";
	}
} else {
	echo "{\n\"status\":false,\n\"error\":\"Not Logged\"";
}
echo "\n}\n";

#funzione che controlla che il parametro sia valido
function checkParam($level){
	if(preg_match("/(easy|medium|hard)/", $level)){
		return TRUE;
	} else {
		return FALSE;
	}
}
?>