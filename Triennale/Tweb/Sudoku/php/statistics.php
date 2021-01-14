<?php
require_once("common.php");

#controllo se la richiesta è fatta in POST
if (!isset($_SERVER["REQUEST_METHOD"]) || $_SERVER["REQUEST_METHOD"]!="POST"){
	header("HTTP/1.1 400 Invalid Request");
	die("ERROR 400: Invalid request - This service accepts only POST requests.");
}

#intestazione json
header("Content-type: application/json");

#controllo che se si è loggati
if(isset($_SESSION["name"])){
	#controllo che ci siano tutti i parametri
	if (isset($_REQUEST["username"]) && isset($_REQUEST["times"]) && isset($_REQUEST["levels"])) {
		$name = $_REQUEST["username"];
		$times = $_REQUEST["times"];
		$levels = $_REQUEST["levels"];
		
		#controllo che i parametri non contengano simboli
		if (check_param($name, $times, $levels)) {
			#inserisco le statistiche di partita nel database
			add_statistics($name,$times,$levels); 
			echo"{\n\"status\":\"OK\"";
		} else {
			echo "{\n\"status\":\"KO\",\n\"error\":\"Invalid Parameters\"";
		}
	} else {
		echo "{\n\"status\":\"KO\",\n\"error\":\"Missing Parameters\"";
	}
} else {
	echo "{\n\"status\":\"KO\",\n\"error\":\"Not Logged\"";
}
echo "\n}\n";

#funzione che verifica che i parametri non contengano simboli
function check_param($name, $times, $level){
		if(preg_match("/^[a-z0-9]+$/i", $name) && preg_match("/[0-9]+/", $times) && preg_match("/(easy|medium|hard)/", $level)){
			return true;
		} else {
			return false;
		}
	}
?>