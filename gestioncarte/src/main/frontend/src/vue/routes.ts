import {RouteRecordRaw} from "vue-router";
import {yugiohRoutes} from "@components/cards/yugioh/routes";
import {onePieceRoutes} from "@components/cards/onepiece/routes";
import {pokemonRoutes} from "@components/cards/pokemon/routes";
import {lorcanaRoutes} from "@components/cards/lorcana/routes";
import {promoRoutes} from "@components/cards/promo/routes";

const routes: Readonly<RouteRecordRaw[]> = [
    { path: '/', component: () => import('@/vue/pages/Index.vue') },
    { path: '/scheduler', component: () => import('@/vue/pages/Scheduler.vue') },
    { path: '/caches/pages', component: () => import('@/vue/pages/ListCachedPages.vue') },
    { path: '/cards/tags', children: [
        { path: '', component: () => import('@/vue/pages/cards/tags/List.vue') },
        { path: 'new', component: () => import('@/vue/pages/cards/tags/Edit.vue') },
        { path: ':tagId', component: () => import('@/vue/pages/cards/tags/Edit.vue') },
    ] },
    { path: '/fields/mappings', children: [
        { path: '', component: () => import('@/vue/pages/fields/mappings/List.vue') },
    ] },
    promoRoutes,
    pokemonRoutes,
    yugiohRoutes,
    onePieceRoutes,
    lorcanaRoutes
];

export default routes;
