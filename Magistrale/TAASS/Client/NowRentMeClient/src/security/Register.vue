<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row justify-content-center">
        <card class="card bg-primary text-white border-primary">
          <div class="card-header bg-primary text-center">
            <h1>Register</h1>
          </div>
          <div class="row social-div-buttons">
            <a class="btn btn-simple social-button" v-bind:href="this.$store.state.socialLoginURL">
              <img class="social-image" src="https://www.corsinvest.it/wp-content/uploads/2019/10/github-logo-300x300.png">
              Log in with Github
            </a>
          </div>
          <div class="or-separator">
            <span class="or-text">OR</span>
          </div>
          <div class="card-body text-white text-center">
            <h3 class="text-danger font-weight-bolder" v-if="register_error">User already in use!</h3>
            <h3 class="text-danger font-weight-bolder" v-if="password_mismatch">Password mismatch!</h3>
            <form @submit.prevent="register">
              <label class="text-white">UserName</label><br>
              <input id="userName" type="text" v-model="user.userName" placeholder="Username" required autofocus><br/><br/>
              <label class="text-white" >E-Mail Address</label><br>
              <input id="mail" type="email" v-model="user.email" placeholder="Email" required><br/><br/>
              <label class="text-white">Password</label><br/>
              <input v-on:blur="validate" id="password" type="password" v-model="user.password" placeholder="Password" required><br/><br/>
              <label class="text-white">Confirm Password</label><br/>
              <input v-on:blur="validate" id="password-confirm" type="password" v-model="password_confirmation" placeholder="Confirm Password" required><br/><br/>
              <hr class="border-white"/>
              <div>
                <button class="border-white text-white btn btn-default btn-block" v-on:click="validate" type="submit">Register</button>
              </div>
            </form>
          </div>
          <div slot="footer">
            <p class="text-white">
              You have an account? <router-link class="text-white font-weight-bold" to="/login"><u>Log on</u></router-link> yourself!
            </p>
          </div>
        </card>
      </div>
    </div>
  </div>
</template>

<script>

  import requests from "../requests/requests";

  export default {
    data(){
      return {
        user: {
          userName: "",
          email: "",
          password: "",
        },
        password_confirmation: "",
        password_mismatch: false,
        register_error: false,
      }
    },
    computed:{
      socialLoginURL() {
        return requests.socialLoginURL
      }
    },
    methods: {
      register: function () {
        let data = {
          userName: this.userName,
          email: this.email,
          password: this.password
        }
        this.$store.dispatch('register', this.user)
          .then(() => this.$router.push('/'))
          .catch(err => {
            this.register_error=true
            console.log(err)
          })
      },
      //controlla se le password sono uguali
      validate: function (event) {
        if(this.password_confirmation !== "" && this.user.password !== "") {
          this.password_mismatch = (this.user.password !== this.password_confirmation)
        } else {
          this.password_mismatch = false
        }
      }
    }
  }
</script>
