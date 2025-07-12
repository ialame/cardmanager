<template>
	<div :class="computedSize"><slot></slot></div>
</template>

<script lang="ts" setup>
import {computed} from "vue";

interface Props {
	size?: string;
}

const props = defineProps<Props>();
const computedSize = computed(() => props.size ? 'col-' + props.size : 'col');
</script>
