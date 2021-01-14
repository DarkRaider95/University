"use strict";

(function (){
window.onload=function() {
	//aggiungo i listener e controllo se ci sono messaggi da mostrare 
	addListener();
	checkMessage($("form"),$$("strong")[0]);
};

//aggiungo i listener ai bottoni
function addListener(){

	$("logbut").onclick=login;
	
	$("regtab").onclick=changeTab;
	
	window.onkeypress=send;
}

//funzione che controlla i parametri del form di login se sono validi tenta il login tramite ajax
function login(){
	var username = $("user").value;
	var password = $("pass").value;
	var container=$("form");
	var element=$$("strong")[0];

	if(username == "" && password == ""){
		showMessage("Username and Password missing!",container,element);
	} else if(username == ""){
		showMessage("Username missing!",container,element);
	} else if(password == ""){
		showMessage("Password missing!",container,element);
	} else {
		checkLogin(username,password);
	}
}

//funzione che controlla i parametri del form di registrazione se sono validi tenta il login tramite ajax
function register(){
	var username = $("user").value;
	var password = $("pass").value;
	var container=$("form");
	var retpass = $("retpass").value;
	var element=$$("strong")[0];

	if(username == "" && password == "" && retpass == ""){
		showMessage("Fill the fields please!",container,element);
	} else if(username == ""){
		showMessage("Username missing!",container,element);
	} else if(password == ""){
		showMessage("Password missing!",container,element);
	} else if(password == ""){
		showMessage("Retype Password please!",container,element);
	} else if(username.length < 3){
		showMessage("Username must be at least 3 character long!",container,element);
	} else if(password.length < 8){
		showMessage("Password must be at least 8 character long!",container,element);
	} else {
		//se le password corrispondono tenta la registrazione se no mostra messaggio di errore
		if(password == retpass){
			checkReg(username,password);
		} else {
			showMessage("The passwords are different!",container,element);
		}
	}
}

//funzione che cambia scheda
function changeTab(){
	var add;
	
	//se ci sono messaggi li rimuovo
	if($("message")){
		$("message").remove();
	}

	//modifico la classe dei bottoni
	if(this.className!="clicked"){
		if(this.id=="logtab"){
			$("regtab").className="unclicked button";
			$("regtab").onclick=changeTab;
			add=false;
		} else {
			$("logtab").className="unclicked button";
			$("logtab").onclick=changeTab;
			add=true;
		}
		this.className="clicked button";
		this.onclick=undefined;
		changeForm(add);
	}
}

//funzione che modifica il form
function changeForm(add){

	//aggiungo un campo oppure lo rimuovo
	if(add){
		var retype=document.createElement("input");
		var br=document.createElement("br");
		var strong=document.createElement("strong");
		var button=$("logbut");
		var form=$("form");

		strong.innerHTML="Retype password:";
		retype.type="password";
		retype.id="retpass";

		form.insertBefore(strong,button);
		form.insertBefore(br,button);
		form.insertBefore(retype,button);
		br=document.createElement("br");
		form.insertBefore(br,button);

		button.id="regbut";
		button.value="Register";
		button.onclick=register;
	} else {
		var button=$("regbut");

		//rimuovo gli a capi, il campo di testo e il testo
		for (var i = 0; i < 4; i++) {
			button.previousSibling.remove();
		}

		button.id="logbut";
		button.value="Login";
		button.onclick=login;
	}

	//svuoto i campi di testo
	$("user").value="";
	$("pass").value="";
}

//funzione che fa una richiesta ajax per tentare il login
function checkLogin(user,pass){
	new Ajax.Request("./php/login.php",
		{
			method: "post",
			parameters: {username:user, password:pass},
			onSuccess: ajaxLog,
			onFailure: ajaxFailure
		}
	);
}

//funzione che fa una richiesta ajax per tentare la registrazione
function checkReg(user,pass){
	new Ajax.Request("./php/register.php",
		{
			method: "post",
			parameters: {username:user, password:pass},
			onSuccess: ajaxReg,
			onFailure: ajaxFailure
		}
	);
}

//funzione che fa login oppure mostra un messaggio di errore
function ajaxLog(ajax){
	var data = JSON.parse(ajax.responseText);

	console.log(data.logged);

	if(data.logged){
		var message="Welcome Back "+getCookie("user")+"!!!";
		setCookie("message",message,1);
		window.location.href = data.url;
	} else {
		showMessage(data.errors,$("form"),$$("strong")[0]);
	}
}

//funzione che fa login dopo che la registrazione ha avuto successo, oppure mostra un messaggio di errore
function ajaxReg(ajax){
	var data = JSON.parse(ajax.responseText);

	console.log(data.logged);

	if(data.logged){
		var message="Register Successful Welcome "+getCookie("user")+"!!!";
		setCookie("message",message,1);
		window.location.href = data.url;
	} else {
		showMessage(data.errors,$("form"),$$("strong")[0]);
	}
}

//funzione che gestisce la pressione del tasto invio
function send(event){
	var num= event.charCode || event.which || event.keyCode;
	console.log("num: "+num+" event.which: "+event.which+" event.keycode: "+event.keyCode);
	if(num==13){
		if($("logbut")){
			login();
		} else {
			register();
		}
	} 
}
})();