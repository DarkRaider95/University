/*!

 =========================================================
 * Vue Light Bootstrap Dashboard - v2.0.0 (Bootstrap 4)
 =========================================================

 * Product Page: http://www.creative-tim.com/product/light-bootstrap-dashboard
 * Copyright 2019 Creative Tim (http://www.creative-tim.com)
 * Licensed under MIT (https://github.com/creativetimofficial/light-bootstrap-dashboard/blob/master/LICENSE.md)

 =========================================================

 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 */
import Vue from 'vue'
import VueRouter from 'vue-router'
import App from './App.vue'

// LightBootstrap plugin
import LightBootstrap from './light-bootstrap-main'

// router setup
import routes from './routes/routes'

import './registerServiceWorker'

import store from './requests/store'
import Axios from 'axios'
import AirbnbStyleDatepicker from "vue-airbnb-style-datepicker";
import 'vue-airbnb-style-datepicker/dist/vue-airbnb-style-datepicker.min.css'
import requests from "./requests/requests";

//opzione per il datepicker
const datepickerOptions = {sundayFirst: false,
  dateLabelFormat: 'dddd, MMMM D, YYYY',
  days: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
  daysShort: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  monthNames: [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December',
  ],
  colors: {
    selected: '#00a699',
    inRange: '#66e2da',
    selectedText: '#000',
    text: '#000',
    inRangeBorder: '#33dacd',
    disabled: '#ff0',
    hoveredInRange: '#67f6ee'
  }
}

// plugin setup
Vue.use(VueRouter)
Vue.use(LightBootstrap)
Vue.use(AirbnbStyleDatepicker, datepickerOptions)

//configure axios
Vue.prototype.$http = Axios;
const token = localStorage.getItem('token')

if (token) {
  console.log("imposto l'header")
  Vue.prototype.$http.defaults.headers.common['Authorization'] = 'Bearer '+ token
}

// configure router
const router = new VueRouter({
  routes, // short for routes: routes
  linkActiveClass: 'nav-item active',
  scrollBehavior: (to) => {
    if (to.hash) {
      return {selector: to.hash}
    } else {
      return { x: 0, y: 0 }
    }
  }
})

//controlla se l'indirizzo che viene caricato richiede l'autenticazione
router.beforeEach((to, from, next) => {
  if(to.matched.some(record => record.meta.requiresAuth)) {
    if (store.getters.isLoggedIn) {
      next()
      return
    }
    next('/login/')
  } else {
    next()
  }
})


/* eslint-disable no-new */
new Vue({
  el: '#app',
  render: h => h(App),
  router,
  store
})


/*console.log(window.location)

  if(window.location.pathname == "/oauth2/redirect"){
    let search = window.location.search

    let indexToken = search.indexOf("?token=")
    //let indexError = search.indexOf("?error=")

    if(indexToken > -1) {
      search = search.replace("?token=", "")
      console.log(search)

      localStorage.setItem('token', search)

      let tokenDecoded = requests.tokenDecode(search)
      store.commit('auth_success', tokenDecoded)

      window.location = 'http://localhost:8080/#/home'
    } else {
      search.replace("?error=", "")
      window.location = 'http://localhost:8080s/#/home'
    }
  }
  else */