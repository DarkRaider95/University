<?php

require_once("/php/common.php");

/*se si è loggati distruzione della sessione e dei cookie e redirect
  altrimenti solo redirect*/
if(isset($_SESSION["name"])){
	session_destroy();
	session_regenerate_id(TRUE);
	session_start();
	setcookie('level', null, -1, '/');
	setcookie('user', null, -1, '/');
	redirect("./user.php", "Logged out succefully");
} else {
	redirect("./user.php");
}
?>