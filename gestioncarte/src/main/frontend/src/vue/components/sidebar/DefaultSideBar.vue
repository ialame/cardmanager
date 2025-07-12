<template>
    <ul class="ps-0 ms-0">
        <SideBarLink to="/scheduler" label="Scheduler" />
        <SideBarLink to="/caches/pages" label="Pages en cache" />
        <Collapse tag="li" label="Tags" open>
            <ul>
                <SideBarLink to="/cards/tags" label="Liste" />
                <SideBarLink to="/cards/tags/new" label="Ajout" />
            </ul>
        </Collapse>
        <SideBarLink to="/fields/mappings" label="Dictionaire des champs" />
    </ul>
</template>

<script lang="ts" setup>
import Collapse from '@components/collapse/Collapse.vue';
import SideBarLink from "@components/sidebar/SideBarLink.vue";</script>
