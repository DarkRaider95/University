<template>
  <card>
    <h4 slot="header" class="card-title">Edit Profile</h4>
    <div ref="passForm" class="modal">
      <!-- Modal content -->
      <div class="modal-content">
        <span v-on:click="closeInsPas" class="close">&times;</span>
        <h2>Insert password to confirm edit profile</h2>
        <h4 v-if="err" class="text-danger">Password Errata!</h4>
        <base-input type="password"
                    label="Password"
                    placeholder="Password"
                    v-model="user.confirmPassword">
        </base-input>
        <div>
          <button class="btn btn-danger btn-fill float-right" @click="closeInsPas">
            Cancel
          </button>
          <button class="btn btn-info btn-fill float-right" style="margin-right: 10px;" @click="editProfile" :disabled="user.confirmPassword === undefined || user.confirmPassword === ''">
            Confirm
          </button>
        </div>
      </div>
    </div>
      <div class="row">
        <div class="col-md-4">
          <base-input type="email"
                    label="Email"
                    placeholder="Email"
                    v-model="user.email">
          </base-input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-4">
          <base-input type="text"
                      label="Profile Image URL"
                      placeholder="http://"
                      v-model="user.imageUrl">
          </base-input>
        </div>
      </div>

      <div class="row">
        <div class="col-md-6">
          <base-input type="text"
                    label="Name"
                    placeholder="Name"
                    v-model="user.name">
          </base-input>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <div class="form-group">
            <label>About Me</label>
            <textarea rows="5" class="form-control border-input"
                      placeholder="Here can be your description"
                      v-model="user.descript">
              </textarea>
          </div>
        </div>
      </div>
      <div class="text-center">
        <button v-if="user.provider==='local'" class="btn btn-info btn-fill float-right" @click="openInsPas">
          Edit Profile
        </button>
        <button v-if="user.provider==='github'" class="btn btn-info btn-fill float-right" @click="editProfile">
          Edit Profile
        </button>
      </div>
      <div class="clearfix"></div>
  </card>
</template>
<script>
  import Card from 'src/components/Cards/Card.vue'
  import axios from "axios";
  import requests from "../../requests/requests";

  export default {
    components: {
      Card
    },
    props: {
        user: {
          userName: String,
          email: String,
          name:String,
          descript: String,
          confirmPassword:String,
          imageUrl:String,
          provider:String,
          providerId:String
        }
    },
    data(){
      return{
        err:false
      }
    },
    methods: {
      editProfile: function () {
        this.user.password = ''
        new Promise((resolve, reject) => {
          axios(requests.editUser(this.user))
                  .then(resp => {
                    console.log("EDIT PROFILE"+resp.data)
                    this.closeInsPas()
                    //this.user = resp.data
                  })
                  .catch(err=>{

                    console.log(err.response.data)
                    if(err.response.data === 'Error! Wrong password.'){
                      this.err=true
                    }
                    /*this.$store.dispatch('logout')
                            .then(() => {
                              this.$router.push('/login')
                            })*/
                  })
        })
      },
      openInsPas:function() {
        this.$refs["passForm"].style.display = "block";
      },
      closeInsPas:function () {
        this.$refs["passForm"].style.display = "none";
        this.err = false
        this.user.confirmPassword = ''
      }
    }
  }

</script>
<style>
  .modal {
    display: none; /* Hidden by default */
    position: fixed; /* Stay in place */
    z-index: 1; /* Sit on top */
    left: 0;
    top: 0;
    width: 100%; /* Full width */
    height: 100%; /* Full height */
    overflow: auto; /* Enable scroll if needed */
    background-color: rgb(0,0,0); /* Fallback color */
    background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
  }

  /* Modal Content/Box */
  .modal-content {
    background-color: #fefefe;
    margin: 10% auto; /* 15% from the top and centered */
    padding: 20px;
    border: 1px solid #888;
    width: 40%; /* Could be more or less, depending on screen size */
  }

  /* The Close Button */
  .close {
    color: #aaa;
    float: right;
    font-size: 28px;
    font-weight: bold;
  }

  .close:hover,
  .close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
  }
</style>
