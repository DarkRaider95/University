/*  API NOTIFICHE
POST /news/add
user
title
message

SCARICARE LE NOTIFICHE
GET /news/<user>

QUANDO LA NOTIFICA E' STATA LETTA
POST /news/read
[newsID, newID, newsID, ...]

 */

const NOTIFICATION_SERVICE = "http://217.61.56.247:19090"
//const NOWRENTME_SERVICES = "http://localhost:8081"
//const NOWRENTME_SERVICES = "http://192.168.2.91:8081"
const NOWRENTME_SERVICES = "https://safe-escarpment-32688.herokuapp.com"

//const OAUTH2_REDIRECT_URI = 'http://localhost:8080/oauth2/redirect'
const OAUTH2_REDIRECT_URI = 'http://nowrent.me:8000/oauth2/redirect'
const SOCIAL_LOGIN = NOWRENTME_SERVICES+'/oauth2/authorize/github?redirect_uri='+OAUTH2_REDIRECT_URI


const requests = {
  socialLoginURL:{
    SOCIAL_LOGIN
  },
  loginReq: function (user) {
    /*let bodyFormData = new FormData();
    bodyFormData.set('username', user.userName);
    bodyFormData.set('password', user.password);*/

    return {
      url: NOWRENTME_SERVICES+'/auth/login',
      data: user,
      method: 'POST',
      headers: {'Content-Type': 'application/json' }
    }
  },
  registerReq: function (user) {
    /*let bodyFormData = new FormData();
    bodyFormData.set('username', user.userName);
    bodyFormData.set('password', user.password);
    bodyFormData.set('email', user.email);*/

    return {
      url: NOWRENTME_SERVICES +'/auth/signup',
      data: user,//bodyFormData,
      method: 'POST',
      headers: {'Content-Type': 'application/json' }
    }
  },
  editUser: function (user) {
    /*let bodyFormData = new FormData();
    bodyFormData.set('username', user.userName);
    bodyFormData.set('password', user.password);*/

    return {
      url: NOWRENTME_SERVICES + '/user/profile',
      data: user,
      method: 'PUT',
      headers: {'Content-Type': 'application/json' }
    }
  },
  searchQuery: function (query){
    console.log(NOWRENTME_SERVICES + '/products/search/'+query)
    return{
      url: NOWRENTME_SERVICES + '/products/search/'+query,
      method: 'GET'
    }
  },
  searchQueryCategories: function (query, categories){
    console.log(NOWRENTME_SERVICES + '/products/search/'+query)
    return{
      url: NOWRENTME_SERVICES + '/products/search/'+query,
      method: 'POST',
      data:categories,
      headers: {'Content-Type': 'application/json' }
    }
  },
  addProduct: function (product){
    /*let bodyFormData = new FormData();
    bodyFormData.set('name', product.name);
    bodyFormData.set('image', product.image);
    bodyFormData.set('descr', product.descr);
    bodyFormData.set('price', product.price);
    bodyFormData.set('category', product.category);*/

    return {
      url: NOWRENTME_SERVICES + '/products',
      data: product,
      method: 'POST',
      headers: {'Content-Type': 'application/json' }
    }
  },
  editProduct: function (product){
    return {
      url: NOWRENTME_SERVICES + '/products/'+product.id,
      data: product,
      method: 'PUT',
      headers: {'Content-Type': 'application/json' }
    }
  },
  addCategory: function (category) {
    return {
      url: NOWRENTME_SERVICES + '/category',
      data: category,
      method: 'POST',
      headers: {'Content-Type': 'application/json' }
    }
  },
  showProduct: function (id){
    return {
      url: NOWRENTME_SERVICES + '/products/'+id,
      method: 'GET'
    }
  },
  showProductRentals: function (id){
    return {
      url: NOWRENTME_SERVICES + '/rent/product/'+id,
      method: 'GET'
    }
  },
  removeProduct: function (id){
    return {
      url: NOWRENTME_SERVICES + '/products/'+id,
      method: 'DELETE'
    }
  },
  rentProduct: function (id, rentData){
    return {
      url: NOWRENTME_SERVICES + '/rent/product/'+id,
      data:rentData,
      method: 'POST',
      headers: {'Content-Type': 'application/json' }
    }
  },
  showRental:function (id){
      return {
      url: NOWRENTME_SERVICES + '/rent/'+id,
      method: 'GET'
    }
  },
  removeRental: function (id) {
    return {
      url: NOWRENTME_SERVICES + '/rent/'+id,
      method: 'DELETE'
    }
  },
  getProductRentDates: function (id) {
    return {
      url: NOWRENTME_SERVICES + '/rent/product/'+id,
      method: 'GET'
    }
  },
  myProducts: {
    url: NOWRENTME_SERVICES + '/products/my/',
    method: 'GET'
  },
  myRentalsSent: {
    url: NOWRENTME_SERVICES + '/rent/sent/',
    method: 'GET'
  },
  myRentalsRcv: {
    url: NOWRENTME_SERVICES + '/rent/received/',
    method: 'GET'
  },
  tokenDecode: function parseJwt (token) {
    let base64Url = token.split('.')[1];
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    let jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  },
  getUserInfo: {
    url: NOWRENTME_SERVICES + '/user/profile',
    method: 'GET',
    /*headers:{
      'Authorization':localStorage.getItem('token')
    }*/
  },
  getCategories: {
    url: NOWRENTME_SERVICES + '/category',
    method: 'GET'
  },
  readNotifications: function(news){
    return {
      url: NOTIFICATION_SERVICE+'/news/read',
      data:news,
      method: 'POST',
      headers: {'Content-Type': 'application/json' }
    }
  },
  loadNotifications: function(userName) {
    return{
      url: NOTIFICATION_SERVICE+'/news/'+userName,
      method: 'GET'
    }
  }
}

export default requests



