<?php
#pagina di logout
require_once("common.php");
session_destroy();
session_regenerate_id(TRUE);
session_start();
redirect("user.php", "Logged out succefully ".$_SESSION['name']);
?>