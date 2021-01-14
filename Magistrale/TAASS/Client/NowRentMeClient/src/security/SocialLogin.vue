<template>

</template>
<script>
import requests from "../requests/requests";
import axios from "axios";
import Vue from "vue";

export default {
    props:{
        token:String,
        error:String
    },
    created() {
        if(this.token !== undefined){
            localStorage.setItem('token', this.token)

            this.$store.dispatch('setToken', this.token)
                .then(() => this.$router.push('/home'))
                .catch(err => {

                    console.log(err)
                })
            this.$store.setToken(this.token)

            this.$router.push('/home')
        } else if (this.error){
            console.log(this.error)
            this.$router.push('/login/'+"Social Login Failure")
        }
    }
}
</script>