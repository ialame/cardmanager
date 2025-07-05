<template>
    <ul class="ps-0 ms-0">
        <Collapse open tag="li" label="Cartes">
            <ul>
                <SideBarLink to="/cards/yugioh/extraction" label="Lancer une extraction" />
                <SideBarLink to="/cards/yugioh/extracted" label="Cartes en attente" />
                <SideBarLink to="/cards/yugioh/new" label="Ajout manuel" />
                <SideBarLink to="/cards/yugioh/merge" label="Fusion manuelle" />
                <SideBarLink to="/cards/yugioh/search" label="Liste" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Extensions">
            <ul>
                <SideBarLink to="/cards/yugioh/sets/new" label="Ajout manuel" />
                <SideBarLink to="/cards/yugioh/sets/unsaved" label="Nouvelles extension" />
                <SideBarLink to="/cards/yugioh/sets" label="Liste" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Series">
            <ul>
                <SideBarLink to="/cards/yugioh/series/new" label="Ajout manuel" />
            </ul>
        </Collapse>
    </ul>
</template>

<script lang="ts" setup>
import Collapse from '@components/collapse/Collapse.vue';
import SideBarLink from "@components/sidebar/SideBarLink.vue";</script>
