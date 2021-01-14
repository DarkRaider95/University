<template>
  <div class="wrapper">
    <side-bar>
      <mobile-menu slot="content"></mobile-menu>
      <sidebar-link to="/home">
        <i class="nc-icon nc-bank"></i>
        <p>Home</p>
      </sidebar-link>
      <sidebar-link to="/search">
        <i class="nc-icon nc-zoom-split"></i>
        <p>Search</p>
      </sidebar-link>
      <sidebar-link to="/profile">
        <i class="nc-icon nc-circle-09"></i>
        <p>User Profile</p>
      </sidebar-link>
      <sidebar-link to="/myproducts">
        <i class="nc-icon nc-app"></i>
        <p>My Products</p>
      </sidebar-link>
      <sidebar-link to="/myrentals">
        <i class="nc-icon nc-tag-content" ></i>
        <p>My Rentals</p>
      </sidebar-link>
      <!--<sidebar-link to="/table-list">
        <i class="nc-icon nc-notes"></i>
        <p>Table list</p>
      </sidebar-link>
      <sidebar-link to="/typography">
        <i class="nc-icon nc-paper-2"></i>
        <p>Typography</p>
      </sidebar-link>
      <sidebar-link to="/icons">
        <i class="nc-icon nc-atom"></i>
        <p>Icons</p>
      </sidebar-link>
      <sidebar-link to="/maps">
        <i class="nc-icon nc-pin-3"></i>
        <p>Maps</p>
      </sidebar-link>
      <sidebar-link to="/notifications">
        <i class="nc-icon nc-bell-55"></i>
        <p>Notifications</p>
      </sidebar-link>

      <template slot="bottom-links">
        <sidebar-link class="active"
                      to="/upgrade">
          <i class="nc-icon nc-alien-33"></i>
          <p>Upgrade to PRO</p>
        </sidebar-link>
      </template>-->
    </side-bar>
    <div class="main-panel">
      <top-navbar></top-navbar>

      <dashboard-content @click="toggleSidebar" class="dashboard-content">

      </dashboard-content>

      <content-footer></content-footer>
    </div>
  </div>
</template>
<style lang="scss">

</style>
<script>
  import TopNavbar from './TopNavbar.vue'
  import ContentFooter from './ContentFooter.vue'
  import DashboardContent from './Content.vue'
  import MobileMenu from './MobileMenu.vue'
  export default {
    components: {
      TopNavbar,
      ContentFooter,
      DashboardContent,
      MobileMenu
    },
    data(){
      return{

      }
    },
    updated() {
      //console.log(this.pagetitle)
    },
    methods: {
      toggleSidebar () {
        if (this.$sidebar.showSidebar) {
          this.$sidebar.displaySidebar(false)
        }
      }
    }
  }

</script>

<style>
  .now-rent-me-comp{
    border-radius: 10px;
    background-color: #0099CC;
    width: 80%;
    margin:auto;
  }

  .empty-message{
    color: #006688;
    padding: 20px;
  }

  .dashboard-content{
    background-image: url("https://www.labnetwork.it/wp-content/uploads/2016/03/AI-world-forum-background-cropped-1920x1000.jpg");
  }
</style>
