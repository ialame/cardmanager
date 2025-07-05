<template>
    <div class="position-fixed w-100 h-100">
        <NavBar :title="title" />
        <AlertList />
        <div class="retriever-main-div">
            <SideBar />
            <div ref="contendDiv" class="retriever-content">
                <RouterView />
            </div>
            <ConfirmModal />
        </div>
        <div class="footer-bar" :class="{'production': envName === 'production'}">
            <span>Professional Card Retriever - {{ version }} | ©2022 PCA - All rights reserved</span>
        </div>
    </div>
    <div class="pca-bg" />
</template>

<script lang="ts" setup>
import NavBar from '@components/NavBar.vue';
import SideBar from '@components/sidebar/SideBar.vue';
import ConfirmModal from "@components/modal/confirm/ConfirmModal.vue";
import {RouterView} from "vue-router";
import {getPageTitle} from "@/vue/composables/PageComposables";
// noinspection ES6UnusedImports
import {envName, version} from '@/utils';
import AlertList from "@/alert/AlertList.vue";
import {ref} from "vue";
import {provideContentDiv} from "@components/side";

const title = getPageTitle();

const contendDiv = ref<HTMLDivElement>();
provideContentDiv(contendDiv);
</script>

<style lang="scss" scoped>
@import "src/variables";
@import "src/mixins";

.retriever-main-div {
    display: flex;
    flex-direction: row;
    height: 100%;
    padding-bottom: 83px;
    .retriever-content {
        width: 100%;
        @include scroller($primary, $light-bg);
    }
}

.footer-bar {
    overflow: hidden;
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 24px;
    text-align: center;
    z-index: 1001;

    &.production {
        background-color: $secondary;
    }
    &:not(.production) {
        background-color: $primary;
    }

    span {
        color: white;
        text-align: center;
        font-size: 0.875em;
    }
}

.pca-bg {
    background-image: $icon-star-red;
    background-position: bottom $spacer right $spacer;
    background-repeat: no-repeat;
    position: fixed;
    top: 0;
    z-index: -1;
    height: 100%;
    width: 100%;
}
</style>
