<?php include("top.html"); ?>
<!--Tommaso Toscano Corso B Es 4 esercizio php form, pagina html iscrizione-->

<form action="signup-submit.php" method="post">
	<fieldset>
		<legend>New User Signup:</legend>
		<strong>Name:</strong><input type="text" name="Name" maxlength="16" size="16"><br/>
		<strong>Gender:</strong>
			<label><input type="radio" name="Gender" value="M">Male</label>
			<label><input type="radio" name="Gender" value="F"  checked="checked">Female</label>
		<br/>
		<strong>Age:</strong><input type="text" name="Age" size="6" maxlength="2"><br/>
		<strong>Personality Type:</strong><input type="text" name="Personality" size="6" maxlength="4">
		(<a href="http://www.humanmetrics.com/cgi-win/JTypes2.asp">Don't know your type?</a>)<br/>
		<strong>Favorite OS:</strong>
			<select name="OS">
 			<option selected="selected" value="Linux">Linux</option>
 			<option value="Windows">Windows</option>
 			<option value="Mac OS X">Mac OS X</option>
			</select>
		<br/>
		<strong>Seeking Age:</strong>
			<input type="text" name="LowerAge" size="6" maxlength="2" placeholder="Min">to
			<input type="text" name="UpperAge" size="6" maxlength="2" placeholder="Max"><br/>
		<strong>Seeking Gender:</strong>
			<label><input type="checkbox" name="SM">Male</label>
			<label><input type="checkbox" name="SF">Female</label><br/>
		<input type="submit" value="Sign Up">
	</fieldset>
</form>
<?php include("bottom.html"); ?>
