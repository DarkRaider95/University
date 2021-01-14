<template>
    <div class="content">
    <card>
        <h4 slot="header" class="card-title">Add Product</h4>
        <div ref="catForm" class="modal">
            <!-- Modal content -->
            <div class="modal-content">
                <span v-on:click="closeAddCat()" class="close">&times;</span>
                <h2>Add Category</h2>
                <base-input type="text"
                            label="Name"
                            placeholder="Name"
                            v-model="category.name">
                </base-input>
                <base-input type="text"
                            label="Category Image"
                            placeholder="Image URL"
                            v-model="category.defaultImage">
                </base-input>
                <div class="form-group">
                    <label>Description</label>
                    <textarea rows="5" class="form-control border-input"
                              placeholder="Category description"
                              v-model="category.descr">
                    </textarea>
                </div>
                <div>
                    <button class="btn btn-danger btn-fill float-right" @click="closeAddCat()">
                        Cancel
                    </button>
                    <button class="btn btn-info btn-fill float-right" style="margin-right: 10px;" @click="addCategory()" :disabled="category.name === '' || category.image === ''">
                        Add Category
                    </button>
                </div>
            </div>
        </div>
            <div class="row">
                <div class="col-md-3">
                    <base-input type="text"
                                label="Name"
                                placeholder="Name"
                                v-model="product.name">
                    </base-input>
                </div>
                <div class="col-md-5">
                    <label>Category</label><br>
                    <select style="display: inline; width: 30%" v-model="product.category.id">
                        <option v-for="category in categories" v-bind:value="category.id">
                            {{ category.name}}
                        </option>
                    </select>
                    <button v-on:click="openAddCat()" class="btn btn-info btn-fill add-cat" style=""><i class="nc-icon nc-simple-add"></i>Add Category</button>
                    <!--<h3 class="text-info" style="display: inline; text-decoration: underline">There isn't your category?</h3>-->
                </div>
            </div>
            <div class="row">
                <div class="col-3">
                    <base-input type="number"
                                label="Price"
                                placeholder="100"
                                v-model="product.price">
                    </base-input>
                </div>
                <div class="col-1">
                    <h2 style="color: #9a9a9a">€/day</h2>
                </div>
            </div>
            <div class="row">
                <div class="col-4">
                    <base-input type="text"
                                label="Image Link"
                                placeholder="http://"
                                v-model="product.image">
                    </base-input>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="form-group">
                        <label>Description</label>
                        <textarea rows="5" class="form-control border-input"
                                  placeholder="Product description"
                                  v-model="product.descr">
                        </textarea>
                    </div>
                </div>
            </div>
            <div>
                <button class="btn btn-danger btn-fill float-right" @click="goMyProduct()">
                    Cancel
                </button>
                <button class="btn btn-info btn-fill float-right" style="margin-right: 10px" @click="addProduct()" :disabled="allFull">
                    Add Product
                </button>
            </div>
    </card>
    </div>
</template>
<script>
    import Card from 'src/components/Cards/Card.vue'
    import axios from "axios";
    import requests from "../../requests/requests";

    export default {
        components: {
            Card
        },
        data () {
            return {
                product: {
                    image: '',
                    name: '',
                    price: 0.0,
                    descr: '',
                    category:{
                        id:undefined,
                    }
                },
                categories:{},
                category: {
                    name:'',
                    defaultImage:'',
                    descr:''
                }
            }
        },
        created() {
            this.loadCategories()
        },
        methods: {
            addProduct: function() {
                new Promise((resolve, reject) => {
                    axios(requests.addProduct(this.product))
                        .then(resp => {
                            this.$router.push("/myproducts")
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
            addCategory: function () {
                new Promise((resolve, reject) => {
                    axios(requests.addCategory(this.category))
                        .then(resp => {
                            this.loadCategories()
                            this.closeAddCat()

                            //seleziono la categoria appena aggiunta
                            /*console.log(this.category.name)
                            console.log(this.categories.properties["pango"])

                            let newCategory = this.categories["pango"].id*/
                            this.product.category.id = resp.data.id

                            console.log(this.product.category.id)
                            //svuoto il modal
                            this.category.name=''
                            this.category.descr=''
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
            loadCategories:function () {
                new Promise((resolve, reject) => {
                    axios(requests.getCategories)
                        .then(resp => {
                            if(resp.data.length > 0) {
                                this.product.category.id = resp.data[0].id;

                                for (let index in resp.data) {
                                    let category = resp.data[index]
                                    this.categories[category.name] = category
                                }
                                console.log(this.categories)
                            }
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
            goMyProduct:function () {
                this.$router.push("/myproducts")
            },
            openAddCat:function() {
                this.$refs["catForm"].style.display = "block";
            },
            closeAddCat:function () {
                this.$refs["catForm"].style.display = "none";
            }
        },
        computed: {
            allFull () {
                return this.product.name === '' ||
                        this.product.category.id === undefined ||
                        this.product.price === 0.0
            }
        }
    }

</script>
<style>
.add-cat{
    background:#0099cc;
    margin-left: 30px;
}

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
    color: black;
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