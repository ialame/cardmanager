<template>
	<nav class="navbar navbar-expand-lg navbar-dark bg-secondary p-0">
        <ul class="navbar-nav h-100">
            <li class="pca-nav-item nav-item me-2" :class="{ 'selected': route.path === '' || route.path === '/' }">
                <router-link to="/" class="text-white"><Icon name="home-outline" class="me-1 icon-24" /></router-link>
            </li>
            <li class="pca-nav-item nav-item text-white" :class="{ 'selected': !tcg }" @click="tcg = ''">
                <Icon src="/svg/pca.svg" class="me-1 icon-24" />
            </li>
            <TcgNavItem tcg="pokemon" title="Pokémon" />
            <TcgNavItem tcg="yugioh" title="Yu-Gi-Oh!" />
            <TcgNavItem tcg="onepiece" title="One Piece" />
            <TcgNavItem tcg="lorcana" title="Lorcana" />
        </ul>
        <ul class="navbar-nav ms-auto me-auto"><li class="nav-item"><span class="nav-link title h4">{{title}}</span></li></ul>
        <ul class="navbar-nav">
            <li class="nav-item">
                <MenuDropdown :in-nav-bar="true" label-class="ms-3 me-2" menu-class="dropdown-menu-end">
                    <DropdownItem href="https://pcagrade.com/" target="_blank"><Icon src="/svg/pca.svg" class="icon-16 me-1" />Site PHP</DropdownItem>
                    <DropdownItem href="/swagger-ui.html" target="_blank"><Icon src="/svg/swagger.svg" class="icon-16 me-1" />Swagger</DropdownItem>
                    <DropdownItem to="/scheduler"><Icon name="timer" class="icon-16 me-1" />Scheduler</DropdownItem>
                    <DropdownItem to="/caches/pages"><Icon name="documents-outline" class="icon-16 me-1" />Pages en cache</DropdownItem>
                    <DropdownItem @click="clearCache"><Icon name="trash" class="icon-16 me-1" />Vider le cache</DropdownItem>
                    <DropdownItem href="/download/logs" :download="getDownloadFileName()"><Icon src="/svg/download.svg" class="icon-16 me-1"></Icon>Logs</DropdownItem>
                    <DropdownItem @click="restart"><Icon name="refresh-outline" class="icon-16 me-1" />Redemarer</DropdownItem>
                    <DropdownItem @click="logout"><Icon name="log-out-outline" class="icon-16 me-1" />Logout</DropdownItem>
                </MenuDropdown>
            </li>
        </ul>
	</nav>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {getDateStr} from 'src/retriever';
import DropdownItem from './dropdown/DropdownItem.vue';
import MenuDropdown from './dropdown/MenuDropdown.vue';
import Icon from "@components/Icon.vue";
import {useSideBarTabs} from "@/vue/composables/PageComposables";
import TcgNavItem from "@components/TcgNavItem.vue";
import {useRoute} from "vue-router";

interface Props {
	title: string;
}

defineProps<Props>();

const clearCache = () => rest.delete("/api/caches");
const restart = () => rest.post("/actuator/restart");
const logout = () => rest.post("/logout");
const getDownloadFileName = () =>  'retriever-' + getDateStr() + '.logs';

const route = useRoute();

const tcg = useSideBarTabs();
</script>

<style lang="scss" scoped>
@import 'src/variables.scss';

.title { color: rgba(255, 255, 255, 0.75) !important }
.navbar {
	z-index: 1001;
	top: 0;
}
li.pca-nav-item {
    height: 100%;
    transition: 0.3s;
    padding: 16px 12px 13px 12px;
    cursor: pointer;
    &.selected {
        background-color: $primary;
    }
    &:hover:not(.selected) {
        background-color: $gray-500;
    }
}
</style>
