<template>
    <div class="d-flex flex-column w-100">
        <div class="d-flex justify-content-center w-100">
            <progress :value="value" :max="max" class="w-100"></progress>
        </div>
        <div class="d-flex justify-content-center">
            <span>{{text}}</span>
        </div>
    </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue';

interface Props {
	value: number;
    max: number;
    label?: string;
}

const props = defineProps<Props>();

const text = computed(() => {
    if (props.label) {
        return `${props.label}: ${props.value}/${props.max}`;
    }
    return `${props.value}/${props.max}`;
});
</script>
