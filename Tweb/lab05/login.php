<?php
#pagina che controlla se fare il login
require_once("common.php");

#se ci sono username e password controllo se sono corretti se no faccio una redirect a user.php
if (isset($_REQUEST["name"]) && isset($_REQUEST["password"])) {
	$name = $_REQUEST["name"];
	$password = $_REQUEST["password"];
	
	/*verifico se username e password sono corretti
	se si faccio una redirect alla index se no faccio
	una redirect a user.php*/
	if (is_password_correct($name, $password)) {
		if (isset($_SESSION)) {
			session_destroy();
			session_regenerate_id(TRUE);
			session_start();
		}	
		$_SESSION["name"] = $name;
		redirect("index.php", "Login successful! Welcome back ".$_SESSION['name'].".");
	} else {
		redirect("user.php", "User or Password incorrect or empty!");
	}
} else {
	redirect("user.php", "User or Password missing!");
}
?>