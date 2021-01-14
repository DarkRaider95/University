<template>
  <card class="product-card">
    <h1 class="product-title">{{product.name}}</h1>
    <div class="row" style="padding: 10px">
      <div class="col-md-3">
        <div class="img-container">
          <img class="product-img" v-bind:src="product.image">
        </div>
      </div>
      <div class="col-md-8 product-descr">
        <!--<h3 class="product-descr-text">{{product.name}}</h3>-->
        <h2 class="product-descr-text">{{product.price}} €/day</h2>
        <p v-if="product.category.name" class="product-descr-text">Category: {{product.category.name}}</p>
      </div>
    </div>
    <div slot="footer" style="float: right">
      <slot name="buttons">
      </slot>
    </div>
  </card>
</template>
<script>
  import Card from './Card.vue'
  export default {
    components: {
      Card
    },
    props:{
      product: {
        image: String,
        name: String,
        price: Float32Array,
        descr: String,
        category:{
          id:BigInt,
          name:String,
          descr:String,
          defaultImage:String
        },
        owner:{
          userName:String
        }
      }
    }
  };

</script>
<style>
  .product-img{
    max-width:100%;
    max-height:100%;
  }

  .img-container{
    height: 150px;
    width: 150px;
    border-radius: 0.25rem;
  }

  .product-descr{
    margin-left: 14px;
    color: white !important;
    height: fit-content;
  }

  .product-card{
    margin-bottom: 10px;
    background-color: #006688!important;
    max-height: 260px;
    max-width: 900px;
    border-radius: 10px;
  }

  .product-descr-text{
    margin: 0;
  }
</style>
