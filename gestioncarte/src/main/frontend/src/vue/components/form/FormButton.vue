<template>
	<button ref="button" :style="computedStyle" type="button" :class="'btn btn-' + color" :disabled="props.disabled || (loading && saveLock)" @click="click">
        <div v-if="loading && saveLock" class="spinner-border" role="status" />
        <slot v-else />
	</button>
</template>

<script lang="ts" setup>
import {computed, ref, watchEffect} from "vue";
import {useEventListener} from "@vueuse/core";
import {useRouter} from "vue-router";
import {SaveComposables} from "@/vue/composables/SaveComposables";

type Color = 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'light' | 'dark' | 'body' | 'muted' | 'white' | 'black-50' | 'white-50' | 'link' | 'toggle' | 'bulbapedia';

interface Props {
	color?: Color;
	style?: Partial<CSSStyleDeclaration>;
	disabled?: boolean;
    href?: string;
    newTab?: boolean;
    loading?: boolean;
}
interface Emits {
    (e: 'hover', isHover: boolean): void;
    (e: 'click', event: MouseEvent): void;
}

const props = withDefaults(defineProps<Props>(), {
	color: 'primary',
	style: () => ({}),
	disabled: false,
    loading: false
});
const emit = defineEmits<Emits>();

const router = useRouter();
const button = ref<HTMLButtonElement>();
const saveLock = SaveComposables.useGlobalSaveLock();
const computedStyle = computed(() => {
    if (props.loading && saveLock.value) {
        return {
            width: `${button.value?.clientWidth}px`,
            ...props.style
        };
    }
    return props.style;
});

const click = (e: MouseEvent) => {
    emit('click', e);

    if (!e.defaultPrevented && props.href) {
        if (!props.newTab) {
            router.push(props.href);
        } else {
            window.open(props.href, '_blank', 'noopener noreferrer');
        }
    }
};
const isHover = ref(false);

useEventListener(button, 'mouseenter', () => {
    emit('hover', true);
    isHover.value = true;
});
useEventListener(button, 'mouseleave', () => {
    emit('hover', false);
    isHover.value = false;
});
watchEffect(() => {
    if (props.disabled && isHover.value) {
        emit('hover', false);
        isHover.value = false;
    }
});
</script>

<style lang="scss" scoped>
.spinner-border {
	width: 1.5rem;
	height: 1.5rem;
}
</style>
