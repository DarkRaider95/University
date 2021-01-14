<template>
    <div class="content">
        <div class="container-fluid">
            <div class="now-rent-me-comp">
                <ul class="myrentals nav nav-pills mb-3 nav-pills nav-justified" id="pills-tab" role="tablist">
                    <li v-bind:class="sentClass" >
                        <button @click="activeSent"class="btn tab-button">Sent</button>
                    </li>
                    <li v-bind:class="rcvClass">
                        <button @click="activeReceived" class="btn tab-button">Received</button>
                    </li>
                </ul>
                <div class="tab-content" id="pills-tabContent">
                    <div v-if="sent">
                        <ul class="rentals-list" v-if="rent_sent">
                            <li is="rental-card"
                                v-for="rental in rent_sent"
                                v-bind:key="rental.id"
                                v-bind:rental="rental">
                                <div slot="buttons">
                                    <button v-on:click="show(rental.id)" class="btn btn-info btn-fill">Show</button>
                                </div>
                            </li>
                        </ul>
                        <h2 v-if="rent_sent.length == 0" class="empty-message">
                            Non hai ancora noleggiato prodotti
                        </h2>
                    </div>
                    <div v-if="!sent">
                        <ul class="rentals-list" v-if="rent_received">
                            <li is="rental-card"
                                v-for="rental in rent_received"
                                v-bind:key="rental.id"
                                v-bind:rental="rental">
                                <div slot="buttons">
                                    <button v-on:click="show(rental.id)" class="btn btn-info btn-fill">Show</button>
                                </div>
                            </li>
                        </ul>
                        <h2 v-if="rent_received.length == 0" class="empty-message">
                            Non hai ancora ricevuto richieste
                        </h2>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import RentalCard from "../../components/Cards/RentalCard";
    import axios from "axios";
    import requests from "../../requests/requests";


    export default {
        components: {
            RentalCard
        },
        data:function(){
            return {
                rent_sent:[],
                rent_received:[],
                sent:true,
                sentClass:"nav-item button-selected",
                rcvClass:"nav-item"
            }
        },
        created: function(){
            this.loadRentSent()
            this.loadRentRcv()
        },
        methods:{
            loadRentSent: function(){
                new Promise((resolve, reject) => {
                    axios(requests.myRentalsSent)
                        .then(resp => {
                            //console.log(resp.data)
                            this.rent_sent = resp.data
                        })
                        .catch(err=>{
                            console.log(err)
                            /*this.$store.dispatch('logout')
                                .then(() => {
                                    this.$router.push('/login')
                                })*/
                        })
                })
            },
            loadRentRcv: function(){
                new Promise((resolve, reject) => {
                    axios(requests.myRentalsRcv)
                        .then(resp => {
                            //console.log(resp.data)
                            this.rent_received = resp.data
                        })
                        .catch(err=>{
                            console.log(err)
                            /*this.$store.dispatch('logout')
                                .then(() => {
                                    this.$router.push('/login')
                                })*/
                        })
                })
            },
            activeSent: function () {
                this.sentClass = "nav-item button-selected"
                this.rcvClass = "nav-item"
                this.sent = true
            },
            activeReceived: function(){
                this.sentClass = "nav-item"
                this.rcvClass = "nav-item button-selected"
                this.sent = false
            },
            show:function (id) {
                this.$router.push("/showrental/"+id)
            }
        }
    };

</script>
<style>
    .rentals-list{
        padding: 15px;
    }


    .myrentals{
        /*background-color: #1D62F0;*/
        background-color:#e5f4e3;
    }

    .button-selected{
        background-color: #0099CC !important;/*#e5f4e3;*/
        color:#e5f4e3 !important;/*#007bff !important;*/
    }

    .tab-button:hover{
        background-color: #0099CC;/*#e5f4e3;*/
    }

    .tab-button{
        width: -moz-available;
        border: none;
        font-size: 20px;
        font-weight: bolder;
        color: black;
    }
</style>