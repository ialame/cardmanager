<template>
    <Row class="drag-and-drop">
        <div class="row vl w-50" ref="leftDiv">
            <slot name="label-left"><h5 class="ms-2" v-if="labelLeft">{{labelLeft}}</h5></slot>
            <DragAndDropListItem v-for="(item, index) in left" :key="index" @drop="drop(item, 'right')">
                <slot :item="item"></slot>
            </DragAndDropListItem>
        </div>
        <div class="row w-50" ref="rightDiv">
            <slot name="label-right"><h5 class="ms-2" v-if="labelRight">{{labelRight}}</h5></slot>
            <DragAndDropListItem v-for="(item, index) in modelValue" :key="index" @drop="drop(item, 'left')">
                <slot :item="item"></slot>
            </DragAndDropListItem>
        </div>
    </Row>
</template>

<script lang="ts" setup>
import {computed, ref} from "vue";
import DragAndDropListItem from "@components/form/list/DragAndDropListItem.vue";
import Row from "@components/grid/Row.vue";
import {DOMComposables} from "@/vue/composables/DOMComposables";

interface Props {
    items: any[];
    modelValue?: any[];
    labelLeft?: string;
    labelRight?: string;
}
interface Emits {
    (e: 'update:modelValue', modelValue: any[]): void; 
}

const props = withDefaults(defineProps<Props>(), {
    modelValue: () => [],
});
const emit = defineEmits<Emits>();

const leftDiv = ref<HTMLDivElement>();
const rightDiv = ref<HTMLDivElement>();
const left = computed(() => props.items.filter(item => !props.modelValue.includes(item)));
const isMouseInRect = DOMComposables.useIsMouseInRect();

const drop = (item: any, side: 'left' | 'right') => {
    const rect = side === 'left' ? leftDiv.value!.getBoundingClientRect() : rightDiv.value!.getBoundingClientRect();
    
    if (isMouseInRect(rect)) {
        emit('update:modelValue', side === 'right' ? [...props.modelValue, item] : props.modelValue.filter(i => i !== item));
    }
}
</script>

<style lang="scss" scoped>
.drag-and-drop {
    min-height: 200px;
}
</style>
