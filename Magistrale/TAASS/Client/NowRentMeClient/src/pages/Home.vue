<template>
    <div class="content home-background">
        <h1 class="home-title">NowRent.me</h1>
        <div class="motivations">
            <h2 class="home-headers">Rent from & to anyone for anything</h2>
            <p class="home-text">
                A crazy simple way to rent stuff and properties online.
            </p>
        </div>
        <div style="text-align: center">
            <button @click="$router.push('/search')" class="btn btn-info btn-fill home-button">Search</button>
        </div>
        <!--<div class="motivations">
            <h2 class="home-headers">Turn your stuff and properties into business</h2>
            <p class="home-text">
                Pay your monthly bills with the earnings you'll get from renting out your stuff and properties.
            </p>

        </div>-->
        <!--<div class="motivations">
            <h2 class="home-headers">Why buy when you can rent?</h2>
            <p class="home-text">
                Save up for a big occasion by renting items you don't use often instead of buying.
            </p>
        </div>
        <div class="motivations">
            <h2 class="home-headers">Search Your Product Now!!!</h2>

        </div>-->
    </div>
</template>

<script>
    import ProductCard from "../components/Cards/ProductCard";
    import Card from "../components/Cards/Card";
    import axios from "axios";
    import requests from "../requests/requests";


    export default {
        components: {
            ProductCard,
            Card
        },
        data:function(){
            return {

            }
        },
        methods: {}
    };
</script>
<style>
    .home-background{
        /*background-image: url("")!important;*/

    }
    .home-title{
        /*background-color: #006688;*/
        border-top-right-radius: 10px;
        border-top-left-radius: 10px;
        padding:30px;
        font-size: 50pt;
    }

    .home-title, .home-headers, .home-text{
        text-align: center;
        color: white;
    }

    .home-headers{
        font-family: Corben,Georgia,Times,serif;
    }

    .home-text{
        font-size: 15pt;
    }

    .motivations{
        margin-top: 100px;
        padding: 50px;
        width: 80%;
        margin:auto;
    }

    .home-button{
        text-align:center;
    }
    /*
    .motivations:nth-child(odd){
        background-color: #006688;
    }

    .motivations:nth-child(even){
        background-color: #1DC7EA;
    }*/
</style>