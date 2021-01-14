<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row justify-content-center" >
        <card class="card bg-primary text-white border-primary">
          <div class="card-header bg-primary text-center">
            <h1>Sign in</h1>
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
            <h3 class="text-danger font-weight-bolder" v-if="error">Invalid username or password!</h3>
            <h3 class="text-danger font-weight-bolder" v-if="logError">{{logError}}!</h3>
            <form class="login" @submit.prevent="login">
              <label class="text-white">Username</label><br/>
              <input required v-model="user.userName" type="username" placeholder="Username"/><br/><br/>
              <label class="text-white">Password</label><br/>
              <input required v-model="user.password" type="password" placeholder="Password"/><br/><br/>
              <hr class="border-white"/>
              <button class="border-white text-white btn btn-default btn-block" type="submit">Login</button>
            </form>
          </div>
          <div slot="footer">
            <p class="text-white">
              You don't have an account? <router-link class="text-white font-weight-bold" to="/register"><u>Register</u></router-link> yourself instead!
            </p>
          </div>
        </card>
      </div>
    </div>
  </div>
</template>

<script>
  import Card from "../components/Cards/Card";
  import requests from "../requests/requests";

  export default {
    components:{
      Card
    },
    props:{
      logError:String
    },
    data(){
      return {
        user: {
          userName: "",
          password: ""
        },
        error:false
      }
    },
    computed:{
      socialLoginURL() {
        return requests.socialLoginURL
      }
    },
    methods: {
      login: function () {
        let userName = this.userName
        let password = this.password
        this.$store.dispatch('login', this.user)
          .then(() => this.$router.push('/'))
          .catch(err => {
            this.error=true
            console.log(err)
          })
      }
    }
    //http://localhost:8081/api/oauth2/authorize/github?redirect_uri=http://localhost:8080/oauth2/redirect
  }
</script>

<style>
  .social-div-buttons{
    margin: auto;
    background-color: white;
    border-radius: 10px;
    text-align: center;
    color: black;
  }

  .social-button{
    width: -moz-available;
  }

  .social-image{
    height: 30px;
    width: 30px;
    margin-right: 20px;
  }

  .or-separator {
    border-bottom: 1px solid #eee;
    padding: 10px 0;
    position: relative;
    display: block;
    margin-top: 20px;
    margin-bottom: 15px;
    font-size: 1em;
    text-align: center;
    height: 0;
  }

  .or-text {
    /*position: absolute;
    left: 45%;
    top: 0;*/
    background: #007bff;
    padding: 10px;
    color: white;
  }
</style>
