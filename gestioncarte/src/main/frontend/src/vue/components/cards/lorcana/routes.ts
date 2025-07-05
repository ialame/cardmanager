import {RouteRecordRaw} from "vue-router";

export const lorcanaRoutes: RouteRecordRaw = { path: '/cards/lorcana', children: [
    { path: 'extraction', component: () => import('@/vue/pages/cards/lorcana/extraction/Start.vue') },
    { path: 'extracted', component: () => import('@/vue/pages/cards/lorcana/extraction/List.vue') },
    { path: 'new', component: () => import('@/vue/pages/cards/lorcana/Edit.vue') },
    { path: ':cardId', component: () => import('@/vue/pages/cards/lorcana/Edit.vue') },
    { path: 'sets',  children: [
        { path: '', component: () => import('@/vue/pages/cards/lorcana/sets/List.vue') },
        { path: 'new', component: () => import('@/vue/pages/cards/lorcana/sets/Edit.vue') },
        { path: ':setId', component: () => import('@/vue/pages/cards/lorcana/sets/Edit.vue') },
        { path: ':setId/list', component: () => import('@/vue/pages/cards/lorcana/List.vue') },
    ] },
    { path: 'series',  children: [
        { path: 'new', component: () => import('@/vue/pages/cards/lorcana/series/Edit.vue') },
        { path: ':serieId', component: () => import('@/vue/pages/cards/lorcana/series/Edit.vue') },
    ] },
] };
