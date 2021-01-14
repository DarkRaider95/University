<template>
  <card class="card-user">
    <img slot="image" src="https://ununsplash.imgix.net/photo-1431578500526-4d9613015464?fit=crop&fm=jpg&h=300&q=75&w=400" alt="..."/>
    <div class="author">
      <a href="#">
        <img v-if="user.imageUrl" class="avatar border-gray" v-bind:src="user.imageUrl" alt="..."/>
        <img v-else class="avatar border-gray" src="https://www.cobdoglaps.sa.edu.au/wp-content/uploads/2017/11/placeholder-profile-sq.jpg" alt="..."/>
        <h4 class="title">{{user.name}}<br />
          <small>{{user.userName}}</small><br/>
          <small>{{user.email}}</small>
        </h4>
      </a>
    </div>
    <p class="description text-center"> {{user.descript}}</p>
    <div slot="footer" class="text-center d-flex justify-content-center">
      <button href="#" class="btn btn-simple"><i class="fa fa-facebook-square"></i></button>
      <button href="#" class="btn btn-simple"><i class="fa fa-twitter"></i></button>
      <button href="#" class="btn btn-simple"><i class="fa fa-google-plus-square"></i></button>
    </div>
  </card>
</template>

<script>
    import Card from "../../components/Cards/Card";

    export default {
      name:"NRMUserCard",
      components: {Card},
      props:{
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
      }
    };
</script>
