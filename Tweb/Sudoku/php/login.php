<?php

require_once("common.php");

#controllo se la richiesta è fatta in POST
if (!isset($_SERVER["REQUEST_METHOD"]) || $_SERVER["REQUEST_METHOD"]!="POST"){
	header("HTTP/1.1 400 Invalid Request");
	die("ERROR 400: Invalid request - This service accepts only POST requests.");
}

#intestazione json
header("Content-type: application/json");

#controllo che ci siano entrambi i parametri
if (isset($_REQUEST["username"]) && isset($_REQUEST["password"])) {
	$name = $_REQUEST["username"];
	$password = $_REQUEST["password"];
	
	#verifico se username e password sono validi
	if(check_param($name,$password)){
		#controllo se sono corretti
		if (is_password_correct($name, $password)) {
			if (isset($_SESSION)) {
				session_destroy();
				session_regenerate_id(TRUE);
				session_start();
			}	
			$_SESSION["name"] = $name;
			#setto un cookie per mostare il nome utente nel messaggio di benvenuto
			setcookie("user", $name, time() + (86400 * 30), "/");
			echo"{\n\"logged\":true,\n\"url\":\"index.php\"";
		} else {
			echo "{\n\"logged\":false,\n\"errors\":\"User or Password incorrect!\"";
		}
	} else{
		echo "{\n\"logged\":false,\n\"errors\":\"User or Password invalid can't contains symbols!\"";
	}
} else {
	echo "{\n\"logged\":false,\n\"errors\":\"User or Password missing!\"";
}

echo "\n}\n";

#funzione che verifica che i parametri non contengano simboli
function check_param($name,$password){
	if(preg_match("/^[a-z0-9]+$/i", $name) && preg_match("/^[a-z0-9]+$/i", $password)){
		return true;
	} else {
		return false;
	}
}
?>