<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-md-8">
          <edit-profile-form v-bind:user="user">
          </edit-profile-form>
        </div>
        <div class="col-md-4">
          <user-card v-bind:user="user">
          </user-card>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import UserCard from "./UserCard";
  import EditProfileForm from "./EditProfileForm";
  import axios from "axios";
  import requests from "../../requests/requests";

  export default {
    components: {
      EditProfileForm,
      UserCard
    },
    data: function() {
        return {
          user: {
            name: '',
            userName: '',
            email: '',
            descript: '',
            imageUrl:'',
            cards:[],
            confirmPassword:'',
            provider:'',
            providerId:''
          }
        }
    },
    created: function () {
        new Promise((resolve, reject) => {
          axios(requests.getUserInfo)
            .then(resp => {
              //console.log(resp.data)
              this.user = resp.data
            })
            .catch(err=>{
              console.log(err)
              /*this.$store.dispatch('logout')
                  .then(() => {
                      this.$router.push('/login')
                  })*/
            })
        })
    }
  }

</script>
<style>

</style>
