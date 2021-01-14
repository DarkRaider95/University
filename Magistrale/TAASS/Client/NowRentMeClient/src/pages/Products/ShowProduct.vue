<template>
    <div class="content">
        <div ref="messageModal" class="modal">
            <!-- Modal content -->
            <div class="modal-content">
                <span v-on:click="closeModal" class="close">&times;</span>
                <h3>{{message}}</h3>
                <button class="btn btn-info btn-fill float-right" @click="closeModal">
                    Ok
                </button>
            </div>
        </div>
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
                    <h2 class="product-descr-text">{{product.price}} €/day</h2>
                    <p class="product-descr-text">Category: {{product.category.name}}</p>
                    <p class="product-descr-text">Owner: {{product.owner.userName}}</p>
                </div>
            </div>
            <div v-if="product.descr" class="row">
                <div class="col product-descr">
                    <h3>Descrizione:</h3><br>
                    <p>{{product.descr}}</p>
                </div>
            </div>
            <div v-if="checkWho" class="row">
                <div class="col rent-date">
                    <h3 style="color: white">Data Prenotazione:</h3><br>
                    <div class="datepicker-trigger">
                        <input
                                type="text"
                                id="datepicker-trigger"
                                placeholder="Select dates"
                                :value="formatDates(rentData.startDate, rentData.endDate)"
                        >

                        <AirbnbStyleDatepicker
                                :trigger-element-id="'datepicker-trigger'"
                                :mode="'range'"
                                :fullscreen-mobile="true"
                                :date-one="rentData.startDate"
                                :date-two="rentData.endDate"
                                :disabledDates="disabledDates"
                                @date-one-selected="val => { rentData.startDate = val }"
                                @date-two-selected="val => { rentData.endDate = val }"
                        />
                    </div>
                </div>
            </div>
            <div slot="footer" style="float: right">
                <slot name="buttons">
                    <button class="btn btn-danger btn-fill float-right" @click="goBack()">
                        Back
                    </button>
                    <button v-if="checkWho" class="btn btn-info btn-fill float-right" style="margin-right: 10px" @click="rent()" :disabled="allFull">
                        Rent
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
    import addDays from 'date-fns/add_days';

    export default {
        components: {
            Card
        },
        props:{
            id:String
        },
        data:function() {
            return {
                message:'',
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
                dateFormat: 'D MMM YYYY',
                disabledDateFormat:'YYYY-MM-DD',
                rentData:{
                    startDate:'',
                    endDate:''
                },
                disabledDates:[],
                invalidDates:[]
            }
        },
        created: function() {
            console.log("ulla ma l'id è proprio questo"+ this.id)
            //this.id=this.$route.params.id
            this.getDisabledDate()
            this.loadProduct()
        },
        methods:{
            formatDates(startDate, endDate) {
                console.log(this.rentData)
                let formattedDates = ''
                if (this.checkValidDate(startDate, endDate)){
                    if (startDate) {
                        formattedDates = format(startDate, this.dateFormat)
                    }
                    if (endDate) {
                        formattedDates += ' - ' + format(endDate, this.dateFormat)
                    }
                } else {
                    this.rentData.startDate = ''
                    this.rentData.endDate = ''
                }

                return formattedDates
            },
            goBack:function () {
                this.$router.go(-1)
            },
            loadProduct:function () {
                new Promise((resolve, reject) => {
                    axios(requests.showProduct(this.id))
                        .then(resp => {
                            //console.log(resp.data)
                            this.product=resp.data
                        })
                        .catch(err => {
                            console.log(err)
                        })
                })
            },
            rent:function () {
                new Promise((resolve, reject) => {
                    axios(requests.rentProduct(this.id, this.rentData))
                        .then(resp => {
                            console.log(resp.data)
                            this.$router.push("/myrentals")
                            //this.product=resp.data
                        })
                        .catch(err => {
                            console.log(err)
                        })
                })
            },
            getDisabledDate:function(){
                new Promise((resolve, reject) => {
                    axios(requests.getProductRentDates(this.id))
                        .then(resp => {
                            //console.log(resp.data)
                            this.invalidDates=resp.data

                            for(let index in this.invalidDates) {
                                let date = this.invalidDates[index]

                                let dates = this.getDatesBetween(date.startDate, date.endDate)
                                this.disabledDates=this.disabledDates.concat(dates)
                            }
                            console.log("DATE DISABILITATE")
                            console.log(this.disabledDates)
                        })
                        .catch(err => {
                            console.log(err)
                        })
                })
            },
            getDatesBetween: function (startDate, stopDate) {
                let dateArray = [];
                let currentDate = Date.parse(startDate);
                let endDate = Date.parse(stopDate);

                console.log("Guarda che belle date"+currentDate+" "+ endDate);

                while (currentDate <= endDate) {
                    dateArray.push(format(new Date (currentDate), this.disabledDateFormat))
                    currentDate = addDays(currentDate,1);
                }
                return dateArray;
            },
            checkValidDate:function (startDate, endDate) {
                console.log("CONTROLLO DATE")
                console.log(startDate)
                console.log(endDate)

                let today = new Date()

                if(startDate !== '' && endDate !== ''){
                    let start = Date.parse(startDate)
                    let end = Date.parse(endDate)
                    if (start < today || end < today) {
                        console.log("PRIMA OGGI")
                        this.showMessage("Non puoi prenotare in una data precedente a oggi")
                        return false
                    }
                }

                for (let index in this.invalidDates){
                    let date = this.invalidDates[index]

                    //data di inizio prima inizio prenotazione e data fine dopo fine prenotazione
                    if(startDate <= date.startDate && endDate >= date.endDate){
                        return false
                    }


                }

                return true
            },
            showMessage:function (message) {
                this.message = message
                this.$refs["messageModal"].style.display = "block";
            },
            closeModal:function () {
                this.$refs["messageModal"].style.display = "none";
            }
        },
        computed:{
            allFull () {
                return this.rentData.startDate === '' || this.rentData.endDate === ''
            },
            checkWho(){
                return this.product.owner.userName!==this.$store.getters.user
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

    .rent-date{
        margin-left: 14px;
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