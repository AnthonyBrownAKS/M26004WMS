import { createRouter, createWebHistory } from 'vue-router'

import MainLayout from '@/layout/MainLayout.vue'

import Inbound from '@/views/Inbound.vue'
import Outbound from '@/views/Outbound.vue'
import TaskQuery from '@/views/TaskQuery.vue'

const routes = [

    {
        path: '/',
        component: MainLayout,

        children: [

            {
                path: '',
                redirect: '/inbound'
            },

            {
                path: 'inbound',
                name: 'Inbound',
                component: Inbound
            },

            {
                path: 'outbound',
                name: 'Outbound',
                component: Outbound
            },

            {
                path: 'task-query',
                name: 'TaskQuery',
                component: TaskQuery
            }

        ]
    }

]

const router = createRouter({

    history: createWebHistory(),

    routes

})

export default router


