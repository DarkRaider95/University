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
        <div class="now-rent-me-comp">
            <div style="float:right;"><button v-on:click="addProd()" class="btn btn-info btn-fill"><i class="nc-icon nc-simple-add"></i>Add Product</button></div>
            <br><br>
            <ul id="products-list" v-if="products">
                <li is="product-card"
                    v-for="product in products"
                    v-bind:key="product.id"
                    v-bind:product="product">
                    <div slot="buttons">
                        <button v-on:click="edit(product.id)" class="btn btn-info btn-fill" style="margin-right: 10px">Edit</button>
                        <button v-on:click="remove(product.id)" class="btn btn-danger btn-fill">Remove</button>
                        <!--span v-if="!checkRented(product.id)"></span>
                        <button v-if="rented" v-on:click="remove(product.id)" class="btn btn-danger btn-fill">Remove</button-->
                    </div>
                </li>
            </ul>
            <h2 v-if="products.length == 0" class="empty-message">
                Non hai ancora inserito prodotti
            </h2>
        </div>
    </div>
</template>

<script>
    import ProductCard from "../../components/Cards/ProductCard";
    import axios from "axios";
    import requests from "../../requests/requests";


    export default {
        components: {
            ProductCard
        },
        data:function(){
            return {
                products:[],
                message:''
                //rented:true
            }
        },
        created: function(){
            this.loadProducts()
        },
        methods:{
            addProd:function () {
                this.$router.push('/addproduct')
            },
            edit:function (id) {
                this.$router.push('/editproduct/'+id)
            },
            remove:function (id) {
                new Promise((resolve, reject) => {
                    axios(requests.removeProduct(id))
                        .then(resp => {
                            this.loadProducts()
                        })
                        .catch(err=>{
                            console.log(err)
                            if(err.response.data === 'Error! This product is rented!'){
                                this.showMessage(err.response.data)
                            }
                            /*this.$store.dispatch('logout')
                                .then(() => {
                                    this.$router.push('/login')
                                })*/
                        })
                })
            },
            loadProducts: function(){
                new Promise((resolve, reject) => {
                    axios(requests.myProducts)
                        .then(resp => {
                            //console.log(resp.data)

                            this.products = resp.data
                            /*for(let i=0; i<this.products.length; i++){
                                let isRented= this.checkRented(this.products[i].id)


                               // console.log(isRented)

                             //   this.rented[this.products[i].id]=isRented
                            }*/
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
            showMessage:function (message) {
                this.message = message
                this.$refs["messageModal"].style.display = "block";
            },
            closeModal:function () {
                this.$refs["messageModal"].style.display = "none";
            }
            /*checkRented:function (id) {
                new Promise((resolve, reject) => {
                    axios(requests.getProductRentDates(id))
                        .then(resp => {
                            //console.log(resp.data)
                            let invalidDates = resp.data

                            console.log("CHECK RENTED")
                            console.log(invalidDates.length)

                            if(invalidDates.length === 0){
                                //this.rented = true
                                return true
                            } else {
                                //this.rented = false
                                return false
                            }
                        })
                        .catch(err => {
                            console.log("SONO ANDATO IN ERRORE")
                            console.log(err)
                        })
                })
            },*/
        }
    };

</script>
<style>
    #products-list{
        padding: 15px;
    }
    /*.search-bar{
        border-top-left-radius: 10px;
        border-bottom-left-radius: 10px;
    }
    .search-button{
        border-top-right-radius: 10px;
        border-bottom-right-radius: 10px;
        background: white;
    }*/
</style>