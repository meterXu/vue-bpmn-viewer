import routes from '@/views/'
import Login from '@/views/Login'
import {parseRoutes} from "@/utils/util";
export const frameRoutes = parseRoutes([
    {
        path:'/bpmn/login',
        name:'@getRoutesName(path)',
        component:Login
    }
])
export default parseRoutes([
    {
        'path':'/bpmn',
        'name':'@getRoutesName(path)',
        'component':"layouts/RouteView",
        'children':[
            ...routes
        ]
    }
])
