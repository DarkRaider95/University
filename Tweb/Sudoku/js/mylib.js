"use strict";

//funzione che restituisce il valore di un cookie dato il nome
function getCookie(name){
	var biscuit = decodeURIComponent(document.cookie);
	//separo il cookie in un array che contiene chiave valore
	var crumbs =  biscuit.split("; ");
	var index;

	//ciclo gli elementi dell'array
	for (var i = 0; i < crumbs.length; i++) {
		//se la chiave è presente restituisco il valore
		index=crumbs[i].indexOf('=');
		if(crumbs[i].substring(0,index)==name){
			return crumbs[i].substring(index+1);
		}
	}

	//se non c'è restituisco undefined
	return undefined;
}

//funzione che imposta un cookie
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

//funzione che controlla se c'è un messagio da mostrare e lo mostra all'interno del container prima di element
function checkMessage(container,element){
	var message = getCookie("message");

	if (message){
		showMessage(message,container,element);
		document.cookie='message=; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/';
	}
}

//funzione che mostra un messaggio all'interno di una pagina
function showMessage(str,container,element){

  //controlla che il messaggio non contenga simboli
  if(str.match(/^[a-z0-9 !]+$/i)){

  	//se c'è già un messaggio aggiorno il testo se no lo creo
  	if($("message")){
  		$("message").innerHTML=str;
  	} else {
  		var message = document.createElement("p");
  		message.innerHTML=str;
  		message.className="message";
  		message.id="message";
  		container.insertBefore(message,element);
  	}
  }
}

//funzione per gli errori di ajax
function ajaxFailure(ajax){
	alert("Error making Ajax request Server status:\n"+
		ajax.status+" "+ajax.statusText+
		"\nServer response text:\n" + ajax.responseText);
}