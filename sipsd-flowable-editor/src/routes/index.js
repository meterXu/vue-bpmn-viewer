import routes from '../views/'
import Login from '../views/Login'
import {util} from "@dpark/s2-utils";
export const frameRoutes = util.parseRoutes([
    {
        path:'/bpmn/login',
        name:'@getRoutesName(path)',
        component:Login
    },
    ...routes
])
export default util.parseRoutes([
    {
        'path':'/bpmn',
        'name':'@getRoutesName(path)',
        'component':"layouts/RouteView",
        'children':[
        ]
    }
])
