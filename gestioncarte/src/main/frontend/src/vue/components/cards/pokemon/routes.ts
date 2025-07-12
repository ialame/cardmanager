import {RouteRecordRaw} from "vue-router";

export const pokemonRoutes: RouteRecordRaw = { path: '/cards/pokemon', children: [
    { path: 'extraction', component: () => import('@/vue/pages/cards/pokemon/extraction/Start.vue') },
    { path: 'extracted', component: () => import('@/vue/pages/cards/pokemon/extraction/List.vue') },
    { path: ':cardId', component: () => import('@/vue/pages/cards/pokemon/Info.vue') },
    { path: 'new', component: () => import('@/vue/pages/cards/pokemon/Edit.vue') },
    { path: ':cardId/edit', component: () => import('@/vue/pages/cards/pokemon/Edit.vue') },
    { path: ':cardId/history', component: () => import('@/vue/pages/cards/pokemon/History.vue') },
    { path: 'search', component: () => import('@/vue/pages/cards/pokemon/Search.vue') },
    { path: 'sets',  children: [
        { path: '', component: () => import('@pages/cards/pokemon/sets/List.vue') },
        { path: 'new', component: () => import('@/vue/pages/cards/pokemon/sets/Edit.vue') },
        { path: ':setId', component: () => import('@/vue/pages/cards/pokemon/sets/Edit.vue') },
        { path: 'search', component: () => import('@/vue/pages/cards/pokemon/sets/Search.vue') },
        { path: 'parent', component: () => import('@/vue/pages/cards/pokemon/sets/Parent.vue') },
        { path: ':setId/list', component: () => import('@/vue/pages/cards/pokemon/List.vue') },
    ] },
    { path: 'merge',  children: [
        { path: '', component: () => import('@/vue/pages/cards/pokemon/merge/Search.vue') },
        { path: ':cardIds', component: () => import('@/vue/pages/cards/pokemon/merge/Merge.vue') },
    ] },
    { path: 'features', children: [
        { path: '', component: () => import('@/vue/pages/cards/pokemon/features/List.vue') },
        { path: 'new', component: () => import('@/vue/pages/cards/pokemon/features/Edit.vue') },
        { path: ':featureId', component: () => import('@/vue/pages/cards/pokemon/features/Edit.vue') },
    ] },
    { path: 'brackets', children: [
        { path: '', component: () => import('@/vue/pages/cards/pokemon/brackets/List.vue') },
        { path: 'new', component: () => import('@/vue/pages/cards/pokemon/brackets/Edit.vue') },
        { path: ':bracketId', component: () => import('@/vue/pages/cards/pokemon/brackets/Edit.vue') },
    ] },
    { path: 'series',  children: [
        { path: '', component: () => import('@/vue/pages/cards/pokemon/series/List.vue') },
        { path: 'new', component: () => import('@/vue/pages/cards/pokemon/series/Edit.vue') },
        { path: ':serieId', component: () => import('@/vue/pages/cards/pokemon/series/Edit.vue') },
    ] },
    { path: 'images', component: () => import('@/vue/pages/cards/pokemon/images/List.vue') },
] };
