import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import requests from "./requests";


Vue.use(Vuex)
let tok = localStorage.getItem('token')
let tokdec = false

if(tok){
  tokdec=requests.tokenDecode(tok)
}

export default new Vuex.Store({
  state: {
    status: '',
    token: tok || '',
    tokenDecoded : tokdec || {},
    user : tokdec.username || '',
    polling:null,
    unread:0,
    old_unread:0,
    socialLoginURL: requests.socialLoginURL.SOCIAL_LOGIN
  },
  mutations: {
    auth_request(){
      this.state.status = 'loading'
    },
    auth_success(state, tokenDecoded){
      this.state.status = 'success'

      this.state.token = tokenDecoded.token
      this.state.user = tokenDecoded.username
      this.state.tokeDecoded = tokenDecoded
    },
    auth_error(){
      this.state.status = 'error'
    },
    logout(){
      this.state.status = ''
      this.state.token = ''
      //this.state.tokeDecoded = null
    },
    setTok(state, token) {
      let tokenDecoded = requests.tokenDecode(token)
      //const usr = tokenDecoded.username
      localStorage.setItem('token', token)

      axios.defaults.headers.common['Authorization'] = 'Bearer '+token

      tokenDecoded.token = token

      this.commit('auth_success', tokenDecoded)
    }
  },
  actions: {
    setToken:function ({commit}, token) {
      this.commit('setTok',token)
    },
    login({commit}, user){
      return new Promise((resolve, reject) => {
        commit('auth_request')
        axios(requests.loginReq(user))
          .then(resp => {
              const token = resp.data.accessToken

              this.commit('setTok', token)
              //console.log("GUARDA LO USER")
              //console.log(this.state.user)
              //console.log("GUARDA IL TOKEN DECODED")
              //console.log(this.state.tokeDecoded)
              resolve(resp)
          })
          .catch(err => {
            commit('auth_error')
            localStorage.removeItem('token')
            reject(err)
          })
      })
    },
    register({commit}, user){
      return new Promise((resolve, reject) => {
        commit('auth_request')
        axios(requests.registerReq(user))
          .then(resp => {
            const token = resp.data.accessToken

            this.commit('setTok', token)

            resolve(resp)
          })
          .catch(err => {
            commit('auth_error', err)
            localStorage.removeItem('token')
            reject(err)
          })
      })
    },
    logout({commit}){
      console.log("HO FATTO LOGOUT")
      return new Promise((resolve, reject) => {
        clearInterval(this.state.polling)
        this.state.polling = null
        this.state.unread = 0
        this.state.old_unread = 0
        commit('logout')
        localStorage.removeItem('token')
        delete axios.defaults.headers.common['Authorization']
        resolve()
      })
    }
  },
  getters : {
    isLoggedIn: state => !!state.token,
    authStatus: state => state.status,
    user: state => state.user,
    tokenDecoded: state => state.tokenDecoded
  }
})
