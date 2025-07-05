<template>
    <ul class="ps-0 ms-0">
        <Collapse open tag="li" label="Cartes">
            <ul>
                <SideBarLink to="/cards/lorcana/extraction" label="Lancer une extraction" />
                <SideBarLink to="/cards/lorcana/extracted" label="Cartes en attente" />
                <SideBarLink to="/cards/lorcana/new" label="Ajout manuel" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Extensions">
            <ul>
                <SideBarLink to="/cards/lorcana/sets/new" label="Ajout manuel" />
                <SideBarLink to="/cards/lorcana/sets" label="List" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Series">
            <ul>
                <SideBarLink to="/cards/lorcana/series/new" label="Ajout manuel" />
            </ul>
        </Collapse>
    </ul>
</template>

<script lang="ts" setup>
import Collapse from '@components/collapse/Collapse.vue';
import SideBarLink from "@components/sidebar/SideBarLink.vue";</script>
