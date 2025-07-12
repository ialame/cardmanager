<template>
    <div class="d-flex flex-column flex-shrink-0 sidebar shadow-lg border-end" :class="{'locked': locked }">
		<div>
			<div class="float-end">
				<button type="button" class="btn no-focus p-0" @click="toogleLock()"><Icon :name="locked ? 'lock-closed' : 'lock-open'" class="icon-24" /></button>
			</div>
		</div>
		<div class="sidebar-scroll mt-2">
            <transition :name="`slide-${slideSide}`" mode="out-in">
                <component :is="activeSidebar" />
            </transition>
		</div>
	</div>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue';
import {useStorageAsync} from "@vueuse/core";
import Icon from "@components/Icon.vue";
import DefaultSideBar from "@components/sidebar/DefaultSideBar.vue";
import PokemonSideBar from "@components/cards/pokemon/PokemonSideBar.vue";
import {compareTcgNames, useSideBarTabs} from "@/vue/composables/PageComposables";
import YuGiOhSideBar from "@components/cards/yugioh/YuGiOhSideBar.vue";
import OnePieceSideBar from "@components/cards/onepiece/OnePieceSideBar.vue";
import LorcanaSideBar from "@components/cards/lorcana/LorcanaSideBar.vue";

const locked = useStorageAsync("sidebar-lock", true);
const toogleLock = () => {
	locked.value = !locked.value;
};

const tcg = useSideBarTabs();
const activeSidebar = computed(() => {
    if (tcg.value === 'pokemon') {
        return PokemonSideBar;
    } else if (tcg.value === 'yugioh') {
        return YuGiOhSideBar;
    } else if (tcg.value === 'onepiece') {
		return OnePieceSideBar;
	} else if (tcg.value === 'lorcana') {
        return LorcanaSideBar;
    }
    return DefaultSideBar;
});
const slideSide = ref('right');

watch(tcg, (n, o) => {
    if (compareTcgNames(n, o) > 0) {
        slideSide.value = 'left'
    } else if (compareTcgNames(n, o) < 0) {
        slideSide.value = 'right'
    }
});
</script>

<style lang="scss" scoped>
@import 'src/variables.scss';

.sidebar {
	background-color: $light-bg;
	height: 100%;
	width: $sidebar-width;
	z-index: 2;
	padding: 1rem;
    min-height: 70vh;
    &:not(:hover):not(.locked) {
        width: 20px;
        background-color: $gray-500;
        border-color: $gray-500;
        padding: 0.25rem;
        & > * {
          display: none;
        }
    }
}
:deep(ul) {
	list-style: none;
	padding-left: 1rem;
}

:deep(a) {
	color: $black;
	text-decoration: none;
	margin-left: 0.5rem;
}

.sidebar-scroll {
	overflow-y: scroll;
}
::-webkit-scrollbar {
	width: 0;
}
.slide-right-enter-active, .slide-right-leave-active, .slide-left-enter-active, .slide-left-leave-active {
    transition: all .2s ease;
    :deep(.collapsing) {
        transition: none !important;
    }
}
.slide-right-enter-from, .slide-left-leave-to {
    transform: translateX(-100%);
    opacity: 0;
}
.slide-right-leave-to, .slide-left-enter-from {
    transform: translateX(100%);
    opacity: 0;
}

</style>
