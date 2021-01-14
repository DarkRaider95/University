<template>
  <nav class="navbar navbar-expand-lg top-nav-bar">
    <div class="container-fluid">
      <button type="button"
              class="navbar-toggler navbar-toggler-right"
              :class="{toggled: $sidebar.showSidebar}"
              aria-controls="navigation-index"
              aria-expanded="false"
              aria-label="Toggle navigation"
              @click="toggleSidebar">
        <span class="navbar-toggler-bar burger-lines"></span>
        <span class="navbar-toggler-bar burger-lines"></span>
        <span class="navbar-toggler-bar burger-lines"></span>
      </button>
      <div class="collapse navbar-collapse justify-content-end">
        <ul class="navbar-nav ml-auto" v-if="isLoggedIn">
          <base-dropdown ref="notiflist" v-on:tommy="readNotifications()" tag="li">
            <template slot="title">
              <i class="nc-icon nc-planet text-white"></i>
              <b class="caret"></b>
              <span class="notification"  v-if="this.$store.state.unread > 0">{{this.$store.state.unread}}</span>
            </template>
            <h5 style="padding-top: 15px; padding-left:5px " v-if="notifyRead.length == 0">Non ci sono notifiche</h5>
            <a v-for="notify in notifyRead"
               v-bind:key="notify.id"
               class="dropdown-item"
              @click="$router.push('/showrental/'+notify.rentID)"><b>Nuova Richiesta</b><br>{{notify.title}}</a>
          </base-dropdown>
          <li class="nav-item" @click="$router.push('/profile')" style="text-align: center">
            <i class="nc-icon nc-circle-09 text-white"></i>
            <a class="nav-link text-white">
              {{this.$store.getters.user}}
            </a>
          </li>
          <li class="nav-item navbar-ct-red">
            <span>
              <a @click="logout" class="nav-link text-white">Logout</a>
            </span>
          </li>
        </ul>
        <ul class="navbar-nav ml-auto" v-if="!isLoggedIn">
          <li class="nav-item" style="text-align: center">
            <button class="btn btn-info btn-fill" @click="$router.push('/login')">Log In</button>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>
<script>

  import axios from "axios";
  import requests from "../requests/requests";

  export default {
    computed: {
      routeName () {
        const {name} = this.$route
        return this.capitalizeFirstLetter(name)
      },
      isLoggedIn : function(){
        if(this.$store.getters.isLoggedIn){
          this.loadNotification(1, this.showTotalNotify)
          this.first=true
        }

        return this.$store.getters.isLoggedIn
      }
    },
    props:{
      pagetitle: String
    },
    data () {
      return {
        activeNotifications: false,
        notifications:[],
        notifyRead:[],
        //isOpen:false,
        first:false
      }
    },
    created: function() {
      if(this.isLoggedIn) {
        this.loadNotification(1, this.showTotalNotify)
      }
    },
    updated: function() {
      if(this.isLoggedIn) {

        if(this.$store.state.polling == null) {
          this.$store.state.polling = setInterval(() => {

            if(this.first) {
              this.loadNotification(2, this.showPopUp)
            } else {
              //this.loadNotification(1, this.showTotalNotify)
              //this.first=true
            }
          }, 5000)
        }
      }
    },
    beforeDestroy: function() {
      clearInterval(this.$store.state.polling)
      this.$store.state.unread = 0
      this.$store.state.old_unread = 0
      this.$store.state.polling = null
    },
    methods: {
      capitalizeFirstLetter (string) {
        return string.charAt(0).toUpperCase() + string.slice(1)
      },
      toggleNotificationDropDown () {
        this.activeNotifications = !this.activeNotifications
      },
      closeDropDown () {
        this.activeNotifications = false
      },
      toggleSidebar () {
        this.$sidebar.displaySidebar(!this.$sidebar.showSidebar)
      },
      hideSidebar () {
        this.$sidebar.displaySidebar(false)
      },
      loadNotification:function (tipo, callback){
        new Promise((resolve, reject) => {
          axios(requests.loadNotifications(this.$store.getters.user))
            .then(resp => {
              //console.log(resp.data)

              //ordino notifiche in ordine decrescente
              this.notifyRead = this.orderDecrById(resp.data)

              if (this.notifyRead.length == 0){
                this.notifications = []

                /*if(this.$refs["notiflist"].isOpen){
                  this.$refs["notiflist"].isOpen=false
                }*/
              }

              //metto le notifiche da leggere in una lista separata
              for(let i = 0; i < this.notifyRead.length; i++){
                let notify = this.notifyRead[i]

                if(notify.read === false && !this.contains(this.notifications, notify)){
                  this.notifications.push(notify)
                }
              }

              //console.log("ci sono "+ this.notifications.length+ " notifiche")

              this.$store.state.unread = this.notifications.length


              if (tipo == 1 && this.$store.state.unread > this.$store.state.old_unread) {
                callback(this.$store.state.unread)
              }
              if (tipo == 2 && this.$store.state.unread > this.$store.state.old_unread) {
                let title = this.notifications[0].title

                callback(title + "<br>" + this.notifications[0].message)
              }

              this.$store.state.old_unread = this.$store.state.unread
            })
            .catch(err => {
              console.log(err)
              /*this.$store.dispatch('logout')
                      .then(() => {
                        this.$router.push('/login')
                      })*/
            })
        })
      },
      readNotifications: function() {
        this.loadNotification(1, this.showTotalNotify)

        /*if(this.notifyRead.length === 0){
          //console.log("panda")
          console.log(this.$refs["notiflist"])
          //this.$refs["notiflist"].isOpen=false

        }*/

        if(this.$store.state.unread > 0) {
          let ids = this.extractIdNotifications(this.notifications)
          console.log("LETTO TUTTO")
          new Promise((resolve, reject) => {
            axios(requests.readNotifications(ids))
                    .then(resp => {
                      //console.log(resp.data)

                      this.notifications = []
                      this.$store.state.unread = 0
                      this.$store.state.old_unread = 0
                    })
                    .catch(err => {
                      console.log(err)
                      /*this.$store.dispatch('logout')
                              .then(() => {
                                this.$router.push('/login')
                              })*/
                    })
          })
        }
      },
      showTotalNotify:function(n){
        let text = ''

        if(n === 1){
          text = 'Hai '+n+' nuova richiesta'
        } else if (n > 1){
          text = 'Hai '+n+' nuove richieste'
        }

        this.showPopUp(text)
      },
      showPopUp: function(text) {
        console.log(text)

        this.$notifications.notify(
          {
            message: text,
            icon: 'nc-icon nc-app',
            horizontalAlign: 'right',
            verticalAlign: 'bottom',
            type: 'info',
            timeout:10000
          })
      },
      logout: function () {
        this.$store.dispatch('logout')
          .then(() => {
            this.notifications=[]
            this.notifyRead = []
            this.first = false
            this.$router.push('/login')
          })
      },
      orderDecrById: function (array) {
        return array.sort(function(a, b) {
          // Compare the 2 dates
          if (a.id > b.id) return -1;
          if (a.id < b.id) return 1;
          return 0;
        })
      },
      extractIdNotifications: function (notifications) {
        let ids = []
        for(let i=0; i<notifications.length; i++){
          ids.push(notifications[i].id)
        }
        return ids
      },
      contains: function (notifications, notify) {
        for(let i=0; i<notifications.length; i++){
          if(notifications[i].id === notify.id){
            return true
          }
        }
        return false
      }
    }
  }

</script>
<style>
.top-nav-bar{
  background-color: #252525;
  position:static;
  width: 100%;
}
</style>
