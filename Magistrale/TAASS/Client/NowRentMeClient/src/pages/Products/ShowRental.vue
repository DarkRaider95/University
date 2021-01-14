<template>
    <div class="content">
        <card class="now-rent-me-comp">
            <div class="row">
                <div class="col">
                    <h1 class="product-title">{{product.name}}</h1>
                </div>
            </div>
            <div class="row" style="padding: 10px">
                <div class="col-md-3">
                    <div class="img-container">
                        <img class="product-img" v-bind:src="product.image">
                    </div>
                </div>
                <div class="col-md-8 product-info">
                    <h2 class="rental-descr-text">{{product.price}} €/day</h2>
                    <p class="rental-descr-text">Category: {{product.category.name}}</p>
                    <p class="rental-descr-text">Owner: {{product.owner.userName}}</p>
                </div>
            </div>
            <div v-if="rental.descr" class="row">
                <div class="col product-descr">
                    <h3>Descrizione:</h3><br>
                    <p>{{product.descr}}</p>
                </div>
            </div>
            <div v-if="rental.sender.userName!==$store.getters.user" class="row">
                <div class="col product-descr">
                    <h3>Sender:</h3>
                    <h4 class="rental-descr-text">{{rental.sender.userName}}</h4>
                </div>
            </div>
            <div class="row">
                <div class="col product-descr">
                    <h3>Rented:</h3>
                    <h4 class="rental-descr-text">From:{{formatDate(rental.startDate)}}  To:{{formatDate(rental.endDate)}}</h4>
                </div>
            </div>
            <div slot="footer" style="float: right">
                <slot name="buttons">
                    <button class="btn btn-info btn-fill float-right" @click="goBack()">
                        Back
                    </button>
                    <button v-if="rental.sender.userName===$store.getters.user" class="btn btn-danger btn-fill float-right" style="margin-right: 10px" @click="remove">
                        Remove
                    </button>
                </slot>
            </div>
        </card>
    </div>
</template>
<script>
    import Card from "../../components/Cards/Card";
    import axios from "axios";
    import requests from "../../requests/requests";
    import format from 'date-fns/format';

    export default {
        components: {
            Card
        },
        props:{
            id:String
        },
        data:function() {
            return {
                rental: {
                    id: 0,
                    product: {
                        id: 0,
                        name: '',
                        image: '',
                    },
                    startDate: '',
                    endDate: '',
                    sender: {
                        userName: ''
                    }
                },
                product: {
                    image: '',
                    name: '',
                    price: 0.0,
                    descr: '',
                    category: {
                        id: 0,
                        name: '',
                        descr: '',
                        defaultImage:''
                    },
                    owner:{
                        userName:''
                    }
                },
                dateFormat: 'D MMM YYYY'
            }
        },
        created: function() {
            console.log("ulla ma l'id è proprio questo"+ this.id)
            this.loadRentalAndProduct()
        },
        methods:{
            loadRentalAndProduct:function(){
                new Promise((resolve, reject) => {
                    axios(requests.showRental(this.id))
                        .then(resp => {
                            //console.log(resp.data)
                            this.rental=resp.data
                            this.loadProduct()
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
            loadProduct:function () {
                new Promise((resolve, reject) => {
                    axios(requests.showProduct(this.rental.product.id))
                        .then(resp => {
                            //console.log(resp.data)
                            this.product=resp.data
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
            formatDate:function (date) {
                return format(date, this.dateFormat)
            },
            goBack:function () {
                this.$router.go(-1)
            },
            remove:function () {
                new Promise((resolve, reject) => {
                    axios(requests.removeRental(this.id))
                        .then(resp => {
                            this.$router.go(-1)
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
        },
        watch:{
            $route(to,from) {
                this.loadRentalAndProduct()
            }
        }
    };

</script>
<style>
    .product-img{
        max-width:100%;
        max-height:100%;
    }

    .product-title{
        margin: 0px 8px 0px 5px;
        color: white !important;
    }

    .product-info{
        margin-left: 14px;
        color: white !important;
    }

    /*.product-card{
        margin-bottom: 10px;
        background-color: #0099CC !important;
    }*/

    .product-description{
        margin: 0;
        min-height: 300px;
    }
</style>