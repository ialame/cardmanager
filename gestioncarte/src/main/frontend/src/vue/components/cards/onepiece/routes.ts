import {RouteRecordRaw} from "vue-router";

export const onePieceRoutes: RouteRecordRaw = { path: '/cards/onepiece', children: [
    { path: 'extraction', component: () => import('@/vue/pages/cards/onepiece/extraction/Start.vue') },
    { path: 'extracted', component: () => import('@/vue/pages/cards/onepiece/extraction/List.vue') },
    { path: 'new', component: () => import('@/vue/pages/cards/onepiece/Edit.vue') },
    { path: ':cardId', component: () => import('@/vue/pages/cards/onepiece/Edit.vue') },
    { path: 'sets',  children: [
        { path: '', component: () => import('@/vue/pages/cards/onepiece/sets/List.vue') },
        { path: 'new', component: () => import('@/vue/pages/cards/onepiece/sets/Edit.vue') },
        { path: ':setId', component: () => import('@/vue/pages/cards/onepiece/sets/Edit.vue') },
        { path: ':setId/list', component: () => import('@/vue/pages/cards/onepiece/List.vue') },
        { path: 'unsaved', component: () => import('@/vue/pages/cards/onepiece/sets/Unsaved.vue') },
    ] },
    { path: 'series',  children: [
        { path: 'new', component: () => import('@/vue/pages/cards/onepiece/series/Edit.vue') },
        { path: ':serieId', component: () => import('@/vue/pages/cards/onepiece/series/Edit.vue') },
    ] },
] };
