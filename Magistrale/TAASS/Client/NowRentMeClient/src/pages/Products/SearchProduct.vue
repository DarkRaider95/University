<template>
    <div class="content">
        <div class="container-fluid now-rent-me-comp">
            <div style="padding: 10px">
                <h2 style="color: white"><!-- v-if="products.length == 0 && query == ''">-->
                    Cerca un prodotto
                </h2>
                <form @submit.prevent="search">
                    <div class="input-group mb-3" style="width: 60%">

                        <input class="form-control search-bar"  aria-describedby="basic-addon2" v-on:input="isQueryEmpty" v-model="query" placeholder="Search..."/>
                        <div class="input-group-append">
                            <button  class="btn-info btn-fill search-button" type="submit">Search</button>
                        </div>

                    </div>
                    <div id="category-list" v-if="categories">
                        <h3 style="color: white">Categories:</h3>
                            <ul>
                                <li v-for="category in categories" v-bind:key="category.id">
                                    <label style="margin-right: 10px">{{category.name}}</label>
                                    <input
                                            type="checkbox"
                                            v-model="categoriesChecked"
                                            v-bind:value="category"
                                    >
                                </li>
                            </ul>
                        <!--<h5>Categories:</h5>
                        <div class="row">
                            <div class="col" v-for="category in categories" v-bind:key="category.id">
                                <label>{{category.name}}</label>
                                <input
                                        type="checkbox"
                                        v-model="categoriesChecked"
                                        v-bind:value="category"
                                >
                            </div>
                        </div>-->
                    </div>
                </form>
            </div>

            <br/><br/>
            <ul id="products-list" v-if="products">
                <li is="product-card"
                v-for="product in products"
                v-bind:key="product.id"
                v-bind:product="product">
                    <div slot="buttons">
                        <button v-on:click="show(product.id)" class="btn btn-info btn-fill">Show</button>
                    </div>
                </li>
            </ul>
            <h2 v-if="empty" class="empty-message">
                Nessun prodotto corrisponde ai criteri di ricerca
            </h2>
        </div>
    </div>
</template>

<script>
    import ProductCard from "../../components/Cards/ProductCard";
    import Card from "../../components/Cards/Card";
    import axios from "axios";
    import requests from "../../requests/requests";


    export default {
        components: {
            ProductCard,
            Card
        },
        data:function(){
            return {
                products:[],
                categories:[],
                categoriesChecked:[],
                query:'',
                empty:false
            }
        },
        created: function() {
            this.loadCategories()
        },
        methods:{
            search: function () {
                if(this.query!=='') {
                    this.products=[]
                    new Promise((resolve, reject) => {
                        axios(requests.searchQueryCategories(this.query, this.categoriesChecked))
                            .then(resp => {
                                console.log(resp.data)
                                if (resp.data.length > 0) {
                                    this.products = resp.data
                                    this.empty = false
                                } else {
                                    this.empty = true
                                }
                            })
                            .catch(err => {
                                console.log(err)
                            })
                    })
                }
            },
            loadCategories:function () {
                new Promise((resolve, reject) => {
                    axios(requests.getCategories)
                        .then(resp => {

                            this.categories = resp.data
                            /*for (let index in resp.data){
                                let category = resp.data[index]
                                this.categories[category.name] = category
                            }*/
                            console.log("CHE BELLE CATEGORIE")
                            console.log(this.categories)
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
            isQueryEmpty:function(){
                console.log("sono dentro isquery empty"+this.query)
                if(this.query==''){
                    this.empty=false
                    this.products=[]
                }
            },
            show:function (id) {
                this.$router.push("/showproduct/"+id)
            }
        },
        computed:{

        }
    };
/*
PRODOTTI DI ESEMPIO
{
    image: "/img/sidebar-5.jpg",
    name: "prodotto del pargolo",
    price: 50.50,
    descr: "come pargola bene",
    category:{
        id:1,
        name:"animale",
        descr:"animale"
    }
},{
    image: "/img/sidebar-5.jpg",
    name: "prodotto del pargolo 2",
    price: 70.50,
    descr: "come pargola bene",
    category:{
        id:1,
        name:"animale",
        descr:"animale"
    }
}
 */
</script>
<style>

    #category-list > ul{
        color:white;
        list-style-type: none;
        margin-left: 0;
        padding: 0;
        overflow: hidden;
    }

    #category-list > ul > li {
        float: left;
        margin-right: 20px;

    }

    .search-bar{
        border-top-left-radius: 10px;
        border-bottom-left-radius: 10px;
        width: 30%;
    }

    .search-button{
        border-top-right-radius: 10px;
        border-bottom-right-radius: 10px;
        background: white;
        border: none;
        color:#006688 !important;
    }

    .search-button:hover{
        color: white !important;
        border: white;
    }
</style>