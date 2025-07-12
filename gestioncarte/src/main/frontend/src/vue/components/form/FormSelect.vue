<template>
    <Dropdown ref="dropdown" :isSelect="true" @toggle="toggle">
        <template #label>
            <input type="hidden" v-model="mappedValue" :required="required">
            <input ref="filterInput" v-show="dropdown?.visible" type="text" v-model="filter" class="filter-input" />
            <slot v-if="!dropdown?.visible" :value="modelValue">{{ mappedValue }}</slot>
        </template>
        <div class="virtual-list" v-bind="containerProps" :style="{ height: `${maxHeight}px` }">
            <div v-bind="wrapperProps">
                <template v-for="item in list" :key="item.index">
                    <DropdownItem @click="setValue(item.data.value)" class="select-item" :class="{'highlight': item.data.value === modelValue}">
                        <slot :value="item.data.value">{{ item.data.label }}</slot>
                    </DropdownItem>
                </template>
            </div>
        </div>
    </Dropdown>
</template>

<script lang="ts" setup>
import Dropdown from "@components/dropdown/Dropdown.vue";
import {computed, ref, watch} from "vue";
import DropdownItem from "@components/dropdown/DropdownItem.vue";
import {useEventListener, useVirtualList} from "@vueuse/core";
import {findIndex, isNumber, isString} from "lodash";
import {UseVirtualListItemSize} from "@/types";

interface Props {
    modelValue?: any;
    values: any[];
    mapper?: (value: any, index: number) => string;
    required?: boolean;
    itemHeight?: UseVirtualListItemSize;
}
interface Emits {
    (e: 'update:modelValue', v: any): void
    (e: 'changed'): void;
}

const props = withDefaults(defineProps<Props>(), {
    mapper: (value: any) => {
        try {
            if (!value) {
                return '';
            } else if (isString(value)) {
                return value;
            }
            return JSON.stringify(value);
        } catch (e) {
            return value.toString();
        }
    },
    values: () => [],
    required: false,
    itemHeight: 32
});
const emit = defineEmits<Emits>();

const filterInput = ref<HTMLInputElement>();
const dropdown = ref<typeof Dropdown>();

const setValue = (value: any, close?: boolean) => {
    emit('update:modelValue', value);
    if (close !== false) {
        dropdown.value?.hide();
    }
    emit('changed');
};
const toggle = (o: boolean) => {
    if (o && filterInput.value) {
        filter.value = "";
        setTimeout(() => filterInput.value?.focus(), 10);
    }
};
const filter = ref('');

const createEntry = (v: any, i: number) => ({
    value: v,
    index: i,
    label: props.mapper(v, i) || ''
});
const computedList = computed(() => [createEntry(undefined, -1), ...props.values.map((v, i) => createEntry(v, i)).filter(v => !filter.value || normalize(v.label).includes(normalize(filter.value)))]);
const mappedValue = computed(() => props.mapper(props.modelValue, props.values.indexOf(props.modelValue)));
const normalize = (s: string) => s.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, "");

const {list, scrollTo, containerProps, wrapperProps} = useVirtualList(computedList, {itemHeight: props.itemHeight});

const nextValue = () => {
    const index = findIndex(computedList.value, v => v.value === props.modelValue) || 0;
    const nextIndex = index < computedList.value.length - 1 ? index + 1 : 0;

    setValue(computedList.value[nextIndex].value, false);
    scrollTo(nextIndex);
}
const previousValue = () => {
    const index = findIndex(computedList.value, v => v.value === props.modelValue) || 0;
    const prevIndex = index > 0 ? index - 1 : computedList.value.length - 1;

    setValue(computedList.value[prevIndex].value, false);
    scrollTo(prevIndex);
}
const maxHeight = computed(() => {
    if (isNumber(props.itemHeight)) {
        return Math.min(300, props.itemHeight * computedList.value.length);
    }
    let height = 0;

    for (let i = 0; i < computedList.value.length; i++) {
        height += props.itemHeight(i);
        if (height > 300) {
            return 300;
        }
    }
    return height;
});

watch(filter, () => scrollTo(0));
useEventListener(document, 'keydown', (e: KeyboardEvent) => {
    if (!dropdown.value?.visible) {
        return;
    }
    if (e.key === 'ArrowDown') {
        nextValue();
        e.preventDefault();
    } else if (e.key === 'ArrowUp') {
        previousValue();
        e.preventDefault();
    } else if (e.key === 'Enter') {
        dropdown.value?.hide();
        e.preventDefault();
    }
});
</script>

<style lang="scss" scoped>
@import 'src/variables';

.select-item:not(.highlight):hover {
    background-color: $gray-500;
}
.highlight {
    background-color: $primary;
    color: $white;
}
.filter-input {
    border: none;
    outline: none;
    width: 100%;
    padding: 0;
    margin: 0;
}
</style>
