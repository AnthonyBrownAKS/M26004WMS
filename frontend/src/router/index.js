import { createRouter, createWebHistory } from 'vue-router'
import MainLayout
    from '@/layout/MainLayout.vue'

import Inbound
    from '@/views/Inbound.vue'

import Outbound
    from '@/views/Outbound.vue'

import TaskQuery
    from '@/views/TaskQuery.vue'

import Material
    from '@/views/Material.vue'

import Customer
    from '@/views/Customer.vue'

import Inventory
    from '@/views/Inventory.vue'

import OperationLog
    from '@/views/OperationLog.vue'

import ApiLog
    from '@/views/ApiLog.vue'

import System
    from '@/views/System.vue'
import LocationView from "@/views/LocationView.vue";

const routes = [

    {

        path: '/',

        component: MainLayout,

        redirect: '/inbound',

        children: [

            {

                path: 'inbound',

                component: Inbound

            },

            {

                path: 'outbound',

                component: Outbound

            },

            {

                path: 'task-query',

                component: TaskQuery

            },

            {

                path: 'material',

                component: Material

            },

            {

                path: 'customer',

                component: Customer

            },

            {

                path: 'inventory',

                component: Inventory

            },

            {

                path: 'operation-log',

                component: OperationLog

            },

            {

                path: 'api-log',

                component: ApiLog

            },

            {

                path: 'system',

                component: System

            },

            {
                path: 'location',

                component: LocationView

            }

        ]

    }

]

const router = createRouter({

    history: createWebHistory(),

    routes

})

export default router


