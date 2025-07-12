<template>
	<div :class="computedClass" v-for="(v, index) in value" :key="index">
		<slot :value="v"><span>{{v}}</span></slot>
	</div>
</template>

<script lang="ts" setup>
import {computed} from "vue";

interface Props { 
	value?: any[];
	noEffect?: boolean;
	class?: string;
}

const props = withDefaults(defineProps<Props>(),  {
	value: () => [],
	noEffect: false,
	class: ''
});
const computedClass = computed(() => (props.noEffect ? '' : 'border rounded list-element-hover ') + 'mb-0' + (props.class ? ' ' + props.class : ''));
</script>

<style lang="scss" scoped>
.list-element-hover {
    padding: 0.25rem;
    &.border {
        border-color: rgba(0, 0, 0, 0) !important;
    }
    &:hover {
        border-color: #aaa !important;
        background-color: rgba(0, 0, 0, 0.1) !important;
    }
    >.list-element-remove {
        display: none;
        padding: 0;
        margin: 0.1rem;
        width: 20px;
        height: 20px;
    }
    >.list-element-remove>ion-icon {
        position: relative;
        top: -2px;
    }
}
</style>
