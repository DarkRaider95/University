<?php
	if (!isset($_SESSION)) { session_start(); }

	#funzione che controlla se si è già loggati se no fa una redirect a user.php
	function ensure_logged_in() {
 		if (!isset($_SESSION["name"])) {
 			redirect("./user.php", "You must log in before you can view that page!");
 		}
	}

	#funzione che fa una redirect all'url specificato e mostra un messaggio se necessario salvandolo in un cookie
	function redirect($url, $flash_message = null){
		if ($flash_message) {
 			setrawcookie("message", rawurlencode($flash_message), time() + (86400 * 30), "/");
 		}

 		header("Location: $url");
 		die;
	}

	#funzione che controlla se esiste l'utente e se esiste confronta le due password
	function is_password_correct($name, $password) {
 		$db = new PDO("mysql:dbname=sudokudb;host=localhost", "root", "new-password");
 		$name = $db->quote($name);
 		$rows = $db->query("SELECT password FROM users WHERE username=$name");

 		db_error($rows);
 		
 		$submitted_pw_hash = md5($password);

 		if ($rows->rowCount()==1) {
 			$row=$rows->fetch();
 			if ($submitted_pw_hash === $row["password"]) {
				return TRUE;
			}
 		} else {
 			return FALSE; # user not found
 		}
	}

	#funzione che aggiunge un utente al database
	function add_user($name, $password){
		$db = new PDO("mysql:dbname=sudokudb;host=localhost", "root", "new-password");
 		$name = $db->quote($name);

 		$password = md5($password);
 		$password = $db->quote($password);
 		$rows = $db->query("SELECT * FROM users WHERE username=$name");

 		db_error($rows);

 		if ($rows->rowCount()>0) {
 			return FALSE;
 		} else {
 			$rows = $db->query("INSERT INTO users (id, username, password) VALUES (NULL, $name, $password)");
 			
 			db_error($rows);
 			
 			return TRUE;	
 		}
	}

	#funzione che aggiunge le statistiche di una partita di un utente
	function add_statistics($name, $times, $level){
		$db = new PDO("mysql:dbname=sudokudb;host=localhost", "root", "new-password");
		
		$name = $db->quote($name);
 		$times = $db->quote($times);
 		$level = $db->quote($level);

 		$status=$db->query("INSERT INTO games (id, username, times, levels) VALUES (NULL, $name, $times, $level)");

 		db_error($status);
	}

	#funzione che conta quante righe della tabella delle partite, sono presenti nel database con un livello specifico
	function count_row($level){
		$db = new PDO("mysql:dbname=sudokudb;host=localhost", "root", "new-password");
 		$level = $db->quote($level);
 		
 		$rows=$db->query("SELECT count(*) FROM games WHERE levels=$level");

 		db_error($rows);

 		if ($rows->rowCount()>0) {
 			return $rows->fetch()[0];
 		} else {
 			return null;
 		}
	}

	/*funzione che restituisce le statistiche delle partite con un determinato livello.
	Il parametro start serve per specificare, da che punto partire del risultato
	della query. Verranno restituite un massimo di 30 righe dal punto start*/

	function ranking($level,$start){
		$db = new PDO("mysql:dbname=sudokudb;host=localhost", "root", "new-password");
 		$level = $db->quote($level);

 		$rows=$db->query("SELECT username,times FROM games WHERE levels=$level ORDER by times ASC LIMIT $start, 30");

 		db_error($rows);		

 		if ($rows->rowCount()>0) {
 			return $rows->fetchAll();
 		} else {
 			return null;
 		}
	}

	//funzione che verifica se la query ha avuto successo
	function db_error($status){
		if(!$status){
			header("HTTP/1.1 500 Internal Server Error");
			die("ERROR 500: Internal Server Error - Database Error.");
		}
	}
?>