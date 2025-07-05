import {RouteRecordRaw} from "vue-router";

export const yugiohRoutes: RouteRecordRaw =  { path: '/cards/yugioh', children: [
    { path: 'extraction', component: () => import('@/vue/pages/cards/yugioh/extraction/Start.vue') },
    { path: 'extracted', component: () => import('@/vue/pages/cards/yugioh/extraction/List.vue') },
    { path: 'search', component: () => import('@/vue/pages/cards/yugioh/Search.vue') },
    { path: 'new', component: () => import('@/vue/pages/cards/yugioh/Edit.vue') },
    { path: ':cardId', component: () => import('@/vue/pages/cards/yugioh/Edit.vue') },
    { path: 'sets',  children: [
        { path: '', component: () => import('@/vue/pages/cards/yugioh/sets/List.vue') },
        { path: 'new', component: () => import('@/vue/pages/cards/yugioh/sets/Edit.vue') },
        { path: ':setId', component: () => import('@/vue/pages/cards/yugioh/sets/Edit.vue') },
        { path: 'unsaved', component: () => import('@/vue/pages/cards/yugioh/sets/Unsaved.vue') },
        { path: ':setId/cards', component: () => import('@/vue/pages/cards/yugioh/List.vue') },
    ] },
    { path: 'merge',  children: [
        { path: '', component: () => import('@/vue/pages/cards/yugioh/merge/Search.vue') },
        { path: ':cardIds', component: () => import('@/vue/pages/cards/yugioh/merge/Merge.vue') },
    ] },
    { path: 'series',  children: [
        { path: 'new', component: () => import('@/vue/pages/cards/yugioh/series/Edit.vue') },
        { path: ':serieId', component: () => import('@/vue/pages/cards/yugioh/series/Edit.vue') },
    ] },
] };
