<?php
#pagina per la ricerca dei film con kevin bacon
require_once("common.php");
ensure_logged_in();
require_once("top.php");
?>

<h1>Results for <?=$_GET['firstname']." ".$_GET['lastname']?></h1>
<p>Films with <?=$_GET['firstname']." ".$_GET['lastname']?> and Kevin Bacon</p>
<?=print_films($_GET['firstname'],$_GET['lastname'],true)?>

<?php require_once("bottom.html"); ?>