import DashboardLayout from '../layout/DashboardLayout.vue'
// GeneralViews
import NotFound from '../pages/NotFoundPage.vue'

// Pages
import Overview from 'src/pages/Template/Overview.vue'
import TableList from 'src/pages/Template/TableList.vue'
import Typography from 'src/pages/Template/Typography.vue'
import Icons from 'src/pages/Template/Icons.vue'
import Maps from 'src/pages/Template/Maps.vue'
import Notifications from 'src/pages/Template/Notifications.vue'
import Upgrade from 'src/pages/Template/Upgrade.vue'
import Profile from "../pages/UserProfile/Profile";
import Register from "../security/Register";
import Login from "../security/Login";
import SearchProduct from "../pages/Products/SearchProduct";
import UserProduct from "../pages/Products/UserProduct";
import AddProduct from "../pages/Products/AddProduct";
import ShowProduct from "../pages/Products/ShowProduct";
import MyRentals from "../pages/Products/MyRentals";
import ShowRental from "../pages/Products/ShowRental";
import EditProduct from "../pages/Products/EditProduct";
import SocialLogin from "../security/SocialLogin";
import Home from "../pages/Home";

const routes = [
  { path: '/oauth2/redirect',
    component: SocialLogin,
    props: (route) => ({
      token: route.query.token,
      error: route.query.error
      })
  },
  {
    path: '/',
    component: DashboardLayout,
    redirect: '/home',
    children:[
      {
        path: 'home',
        name: 'Home',
        component: Home
      },
      {
        path: 'search',
        name: 'Search',
        component: SearchProduct
      },
      {
        path: 'login',
        name: 'Login',
        component: Login,
      },
      {
        path: 'login/:logError',
        name: 'LoginError',
        component: Login,
        props:true
      },
      {
        path: 'register/',
        name: 'Register',
        component: Register
      },
      {
        path: 'myproducts',
        name: 'UserProduct',
        component: UserProduct,
        meta: {
          requiresAuth: true
        }
      },
      {
        path: 'myrentals',
        name: 'MyRentals',
        component: MyRentals,
        meta: {
          requiresAuth: true
        }
      },
      {
        path: 'showrental/:id',
        name: 'ShowRentals',
        component: ShowRental,
        props:true,
        meta: {
          requiresAuth: true
        }
      },
      {
        path: 'addproduct',
        name: 'AddProduct',
        component: AddProduct,
        meta: {
          requiresAuth: true
        }
      },
      {
        path: 'editproduct/:id',
        name: 'EditProduct',
        component: EditProduct,
        meta: {
          requiresAuth: true
        },
        props:true
      },
      {
        path: 'showproduct/:id',
        name: 'ShowProduct',
        component: ShowProduct,
        props:true
      },
      {
        path: 'table-list',
        name: 'Table List',
        component: TableList
      },
      {
        path: 'typography',
        name: 'Typography',
        component: Typography
      },
      {
        path: 'icons',
        name: 'Icons',
        component: Icons
      },
      {
        path: 'maps',
        name: 'Maps',
        component: Maps
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: Notifications
      },
      {
        path:'profile',
        name:'Profile',
        component:Profile,
        meta: {
          requiresAuth: true
        }
      },
      {
        path: 'upgrade',
        name: 'Upgrade to PRO',
        component: Upgrade
      }
    ]
  },
  { path: '*', component: NotFound }
];


/**
 * Asynchronously load view (Webpack Lazy loading compatible)
 * The specified component must be inside the Views folder
 * @param  {string} name  the filename (basename) of the view to load.
function view(name) {
   var res= require('../components/Dashboard/Views/' + name + '.vue');
   return res;
};**/

export default routes
