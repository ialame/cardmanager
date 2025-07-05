<template>
	<component :is="tag">
		<slot name="toggle">
            <div class="d-flex flex-row">
                <FormButton color="toggle" class="no-focus collapse-button p-0" @click="isOpen = !isOpen" :aria-expanded="isOpen"><slot name="label">{{label}}</slot></FormButton>
                <slot name="after-label" />
            </div>
		</slot>
        <transition name="collapse">
            <div v-if="isOpen">
                <slot />
            </div>
        </transition>
	</component>
</template>

<script lang="ts" setup>
import FormButton from '@components/form/FormButton.vue';
import {useVModel} from "@vueuse/core";

type TagType = 'div' | 'ul' | 'li';

interface Props { 
	label?: string;
    open?: boolean;
	tag?: TagType;
}
interface Emits {
    (e: 'update:open', open: boolean): void,
}

const props = withDefaults(defineProps<Props>(),  {
	label: '',
    open: true,
	tag: 'div'
});
const emit = defineEmits<Emits>();
const isOpen = useVModel(props, 'open', emit, {passive: true});
</script>

<style lang="scss">
@import "src/variables";
@import "src/mixins";

$transition-time: .35s;

.collapse-button {
    white-space: nowrap;
    margin-top: 0.25rem;

    &:before {
        display: inline-block;
        width: 1.25em;
        line-height: 0;
        //noinspection ALL
        content: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3e%3cpath fill='none' stroke='rgba%280,0,0,.5%29' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M5 14l6-6-6-6'/%3e%3c/svg%3e");
        transition: transform $transition-time ease;
        transform-origin: .5em 50%;
    }

    &[aria-expanded="true"]:before {
        transform: rotate(90deg);
    }
}
.collapse-enter-active, .collapse-leave-active {
    overflow: hidden;
    @include transition(max-height $transition-time ease);
}
.collapse-enter-from, .collapse-leave-to {
    max-height: 0;
}
.collapse-enter-to, .collapse-leave-from {
    max-height: 1000px;
}

</style>
