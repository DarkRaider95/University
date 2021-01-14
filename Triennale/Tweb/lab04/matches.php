<?php include("top.html"); ?>
<!--Tommaso Toscano Corso B Es 4 esercizio php form, pagina ricerca match-->

<form action="matches-submit.php" method="get">
	<fieldset>
		<legend>Returning User:</legend>
		<strong>Name:</strong><input type="text" name="Name" maxlength="16" size="16"><br/>
		<input type="submit" value="View My Matches">
	</fieldset>
</form>
<?php include("bottom.html"); ?>