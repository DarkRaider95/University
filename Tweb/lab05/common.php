<?php
	if (!isset($_SESSION)) { session_start(); }

	#funzione che controlla se si è già loggati se no fa una redirect a user.php
	function ensure_logged_in() {
 		if (!isset($_SESSION["name"])) {
 			redirect("user.php", "You must log in before you can view that page.");
 		}
	}

	#funzione che fa una redirect all'url specificato e mostra un messaggio se necessario
	function redirect($url, $flash_message = null){
		if ($flash_message) {
 			$_SESSION["FLASH"] = $flash_message;
 		}

 		header("Location: $url");
 		die;
	}

	#funzione che controlla se la password è corretta interroga il database e poi confronta le due password
	function is_password_correct($name, $password) {
 		$db = new PDO("mysql:dbname=imdb;host=localhost", "root", "new-password");
 		$name = $db->quote($name);
 		$rows = $db->query("SELECT password FROM user WHERE name=$name");

 		if ($rows->rowCount()==1) {
 			$row=$rows->fetch();
 			return $password === $row["password"];
 		} else {
 			return FALSE; # user not found
 		}
	}

	#funzione che stampa la tabella con i film o un messaggio di errore
	function print_films($name,$lname,$kevin){
		$db = new PDO("mysql:dbname=imdb;host=localhost", "root", "new-password");
		$nameq = $db->quote($name);
		$lnameq = $db->quote($lname);
		$id=get_id($db,$name,$lnameq);
		#controllo se l'attore è stato trovato nel database
		if($id!=NULL){
			/*controllo se devo fare la query che cerca i film con kevin bacon 
			  oppure la query che fa la lista dei film di un dato attore*/
			if($kevin){
				$rows=get_film_kevin($db,$nameq,$lnameq,$id);
				#se ci sono dei film stampo la tabella altrimenti il messaggio "non ci sono film con kevin bacon"
				if($rows->rowCount() > 0){
					table($rows);
				} else {
					?><p><?=$_GET['firstname']." ".$_GET['lastname']?> wasn't in any films with Kevin Bacon.</p><?php
				}
			} else {
				$rows=get_film_all($db,$nameq,$lnameq,$id);
				table($rows);
			}			
		} else {
			?><p>Actor <?=$_GET['firstname']." ".$_GET['lastname']?> not found</p><?php
		}
	}

	#funzione che cerca sul server se ci sono film con kevin bacon
	function get_film_kevin($db,$name,$lname,$id){
		#seleziono tutti i film dell'attore richiesto e poi cerco per ogni film, se il film è tra quelli che ha fatto kevin bacon
		$query="SELECT name, year
		        FROM movies JOIN roles ON movies.id = movie_id
		        WHERE actor_id=$id AND movie_id IN 
		        (SELECT movie_id FROM movies JOIN roles ON movies.id = movie_id WHERE actor_id = 22591)
		        ORDER BY year DESC, name ASC";

		$rows = $db->query($query);
		return $rows;
	}

	#funzione che cerca nel database l'id dell'attore
	function get_id($db,$name,$lname){
		$name=$db->quote($name."%");
		
		/*seleziono gli attori che hanno: il nome che inizia per $name,
		  il cognome uguale a $lname e il loro numero di film
		  è il maggiore tra i selezionati con nome simile.
		  Ordino sempre per id, cosi nel caso escano più attori
		  che hanno il numero di film massimo, estraggo sempre quello
		  con id più basso*/
		$query="SELECT id FROM actors 
				WHERE LOWER(first_name) LIKE LOWER($name) 
				AND LOWER(last_name)=LOWER($lname) 
				AND film_count=(SELECT max(film_count) FROM actors 
				WHERE LOWER(first_name) LIKE LOWER($name) 
				AND LOWER(last_name)=LOWER($lname)) ORDER BY id";

		$id=$db->query($query);
		
		if($id->rowCount() > 0){
			$actor_id=$id->fetch()['id'];
		} else {
			$actor_id=NULL;
		}

		return $actor_id;
	}

	#funzione che effettua la query che trova tutti i film di un attore
	function get_film_all($db,$name,$lname,$id){
		$query="SELECT name, year
				FROM movies JOIN roles ON movies.id = movie_id JOIN actors ON actor_id = actors.id
				WHERE actors.id=$id
				ORDER BY year DESC, name ASC";

		$rows = $db->query($query);
		return $rows;
	}

	#funzione che stampa la tabella con i film
	function table($rows){
	?><table>
		<tr>
    		<th>#</th>
    		<th>Title</th> 
    		<th>Year</th>
  		</tr>
  	<?php
  		$count=1;
  		foreach ($rows as $row) {
  			//var_dump($row);
  		 ?>
  		<tr>
  			<td><?=$count?></td>
  			<td><?=$row['name']?></td>
  			<td><?=$row['year']?></td>
  		</tr>
  		<?php
  			$count++;
  		}
  	?>
	  </table>
	<?php
	}
?>