import Vue from 'vue'
import axios from 'axios'
import {globalStore,project} from '@/utils'
import { ACCESS_TOKEN} from "@/utils/mutation-types"
import {parseRoutes} from '@/utils/util'

export function staticPermission(){
    return {
        menu:parseRoutes([
            {
                'path':'/bpmn',
                'name':'@getRoutesName(path)',
                'meta': { title: '系统菜单',icon:'menu'},
                'children':[
                    {
                        path: '/bpmn/m1/readme',
                        name: '@getRoutesName(path)',
                        meta: {
                            title: 'readme'
                        },
                    },
                    {
                        path: '/bpmn/m2/page_a',
                        name: '@getRoutesName(path)',
                        meta: {
                            title: 'page-a'
                        },
                    },
                    {
                        path: '/bpmn/m2/page_b',
                        name: '@getRoutesName(path)',
                        meta: {
                            title: 'page-b'
                        },
                    }
                ]
            },
        ])
    }
}
