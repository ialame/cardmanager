<template>
    <div :class="'drop' + direction">
        <a ref="dropdown" :class="computedClass" href="#" role="button" data-bs-toggle="dropdown" data-bs-auto-close="outside" aria-expanded="false">
            <slot name="label">{{label}}</slot>
        </a>
        <div :class="computedMenuClass">
            <template v-if="visible">
                <slot />
            </template>
        </div>
    </div>
</template>

<script lang="ts" setup>
import {Dropdown as BsDropdown} from 'bootstrap';
import {computed, onMounted, ref} from 'vue';
import {useEventListener} from "@vueuse/core";

type Direction = 'up' | 'down' | 'start' | 'end';

interface Props {
    inNavBar?: boolean;
    subDropdown?: boolean;
    isSelect?: boolean;
    labelClass?: string;
    menuClass?: string;
    label?: string;
    direction?: Direction;
    disabled?: boolean;
}
interface Emits {
    (e: 'toggle', isOpen: boolean): void;
}

const props = withDefaults(defineProps<Props>(), {
    inNavBar: false,
    subDropdown: false,
    isSelect: false,
    labelClass: '',
    menuClass: '',
    label: '',
    direction: 'down',
    disabled: false,
});
const emit = defineEmits<Emits>();

const dropdown = ref<HTMLAnchorElement>();
const bsDropdown = ref<BsDropdown>();
const show = () => bsDropdown.value?.show();
const hide = () => bsDropdown.value?.hide();
const visible = ref(false);
const computedClass = computed(() => {
    let value = 'dropdown-toggle no-focus';

    if (props.inNavBar) {
        value += ' nav-link';
    }
    if (props.isSelect) {
        value += ' form-select form-control';
    }
    if (props.subDropdown) {
        value += ' dropdown-item';
    } else if (!props.isSelect) {
        value += ' btn';
    }
    if (props.disabled) {
        value += ' disabled';
    }
    return value + ' ' + props.labelClass;
});

const computedMenuClass = computed(() => {
    let value = "dropdown-menu z-top";

    if (props.isSelect) {
        value += " dropdown-menu-select w-100";
    }
    return value + " " + props.menuClass;
})

onMounted(() => {
    if (dropdown.value) {
        bsDropdown.value = new BsDropdown(dropdown.value, {
            offset: props.isSelect ? [0, 0] : [0, 2],
        });

        useEventListener(dropdown.value, 'show.bs.dropdown', () => visible.value = true);
        useEventListener(dropdown.value, 'shown.bs.dropdown', () => emit('toggle', true));
        useEventListener(dropdown.value, 'hide.bs.dropdown', () => visible.value = false);
        useEventListener(dropdown.value, 'hidden.bs.dropdown', () => emit('toggle', false));
    }
});
defineExpose({visible, show, hide});
</script>

<style lang="scss" scoped>
@import 'src/variables';
@import 'src/mixins';

a {
    &.disabled {
        pointer-events: none;

        &:not(.btn) {
            background-color: $gray-200;
        }
    }
    &.form-select.dropdown-toggle {
        text-decoration: none;
        min-height: 38px;
        &:focus {
            border-color: $gray-400;
        }
        &.show:has(+ .dropdown-menu-select[data-popper-placement^="bottom"]) {
            border-bottom: none;
            border-bottom-left-radius: 0;
            border-bottom-right-radius: 0;
            background-color: $light-bg;
        }
        &.show:has(+ .dropdown-menu-select[data-popper-placement^="top"]) {
            border-top: none;
            border-top-left-radius: 0;
            border-top-right-radius: 0;
            background-color: $light-bg;
        }
        &:after {
            content: none;
        }
    }
}

.dropdown-menu-select {
    padding-left: 0;
    padding-right: 0;

    &:not(:has(:deep(> .virtual-list))) {
        @include scroller($secondary, $light-bg);
        max-height: 300px;
    }
    &:deep(> .virtual-list) {
        @include scroller($secondary, $light-bg);
    }
    &.show[data-popper-placement^="top"] {
        border-bottom: none;
        border-bottom-left-radius: 0;
        border-bottom-right-radius: 0;
        background-color: $light-bg;

        &::-webkit-scrollbar-track {
            border-top-left-radius: 0 !important;
            border-bottom-left-radius: 0 !important;
            border-bottom-right-radius: 0 !important;
        }
    }

    &.show[data-popper-placement^="bottom"] {
        border-top: none;
        border-top-left-radius: 0;
        border-top-right-radius: 0;
        background-color: $light-bg;

        &::-webkit-scrollbar-track {
            border-bottom-left-radius: 0 !important;
            border-top-left-radius: 0 !important;
            border-top-right-radius: 0 !important;
        }
    }
}
</style>
