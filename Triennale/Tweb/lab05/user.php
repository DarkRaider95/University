<?php
#pagina per effetture il login
require_once("common.php");
require_once("top.php");
#se sono loggato faccio una redirect alla index se no mostro il form per il login
if (isset($_SESSION["name"])){
	redirect("index.php");
} else { ?>
	<h1>Log in</h1>
	<form id="login" action="login.php" method="post">
		<fieldset>
			<legend>Log in:</legend>
			<strong>Name:</strong><input type="text" name="name" /><br>
			<strong>Password:</strong><input type="password" name="password" /><br>
			<input id="login-but" type="submit" value="Log in" />
		</fieldset>
	</form>
<?php
}
require_once("bottom-login.html");
?>