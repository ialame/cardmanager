import {RouteRecordRaw} from "vue-router";

export const promoRoutes: RouteRecordRaw =  { path: '/cards/:tcg/promos', children: [
    { path: 'events', children: [
        { path: 'new', component: () => import('@/vue/pages/cards/promos/events/Edit.vue') },
        { path: ':eventId', component: () => import('@/vue/pages/cards/promos/events/Edit.vue') },
        { path: 'extracted', component: () => import('@/vue/pages/cards/promos/events/extraction/List.vue') },
        { path: 'extract', component: () => import('@/vue/pages/cards/promos/events/extraction/Single.vue') },
        { path: 'traits', children: [
            { path: '', component: () => import('@pages/cards/promos/events/traits/List.vue') },
            { path: 'new', component: () => import('@pages/cards/promos/events/traits/Edit.vue') },
            { path: ':traitId', component: () => import('@pages/cards/promos/events/traits/Edit.vue') },
            { path: ':traitId/history', component: () => import('@pages/cards/promos/events/traits/History.vue') },
            { path: 'merge/:traitIds', component: () => import('@pages/cards/promos/events/traits/Merge.vue') },
        ] }
    ] },
    { path: 'versions', children: [
        { path: '', component: () => import('@pages/cards/promos/versions/List.vue') },
        { path: 'new', component: () => import('@pages/cards/promos/versions/Edit.vue') },
        { path: ':versionId', component: () => import('@pages/cards/promos/versions/Edit.vue') },
    ] }
] };
