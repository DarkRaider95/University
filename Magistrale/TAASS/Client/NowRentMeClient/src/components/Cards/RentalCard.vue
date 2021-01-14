<template>
    <card class="rental-card">
        <div class="row">
            <div class="col">
                <h1 class="product-title">{{rental.product.name}}</h1>
            </div>
        </div>
        <div class="row" style="padding: 10px">
            <div class="col-md-3">
                <div class="img-container">
                    <img class="product-img" v-bind:src="rental.product.image">
                </div>
            </div>
            <div class="col-md-8 float-right rental-descr">
                <h4 class="rental-descr-text">From:{{formatDate(rental.startDate)}}  To:{{formatDate(rental.endDate)}}</h4>
                <!--<h3 class="rental-descr-text">{{rental.product.name}}</h3>-->
                <h3 class="rental-descr-text" v-if="rental.sender.userName!==$store.getters.user">Sender: {{rental.sender.userName}}</h3>
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
    import format from 'date-fns/format';

    export default {
        components: {
            Card
        },
        data:function () {
            return{
                dateFormat: 'D MMM YYYY'
            }
        },
        props:{
            rental: {
                id: BigInt,
                product: {
                    id: BigInt,
                    name: String,
                    image: String,
                },
                startDate: String,
                endDate: String,
                sender: {
                    userName: String
                }
            }
        },
        methods:{
            formatDate:function (date) {
                /*console.log(this.$store.state.user)
                console.log(this.$store.state)
                console.log(this.rental.sender.userName)*/

                return format(date, this.dateFormat)
            }
        }
    };

</script>
<style>
    .product-img{
        max-width:100%;
        max-height:100%;
    }

    /*.img-container{
        height: 150px;
        width: 150px;
        border-radius: 0.25rem;
    }*/

    .rental-descr{
        margin-left: 14px;
        color: white !important;
        height: fit-content;
    }

    .rental-card{
        margin-bottom: 10px;
        background-color: #006688!important;
        max-height: 260px;
        max-width: 900px;
        border-radius: 10px;
    }

    .rental-descr-text{
        margin: 0;
    }
</style>
