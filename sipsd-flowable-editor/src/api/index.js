import {util} from '@dpark/s2-utils'

export function staticPermission(){
    return {
        menu:util.parseRoutes([
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
