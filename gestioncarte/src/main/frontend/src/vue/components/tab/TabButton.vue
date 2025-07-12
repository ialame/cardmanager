<template>
	<FormButton color="link" class="tab-btn no-focus" :class="{active: modelValue === name}" @click="$emit('update:modelValue', name)">
        <div><slot /></div>
    </FormButton>
</template>

<script lang="ts" setup>
import FormButton from '@components/form/FormButton.vue';

interface Props {
	modelValue?: string;
    name: string;
}

defineProps<Props>();
</script>

<style lang="scss" scoped>
@import "src/variables";

button.tab-btn {
	padding: 0;
    margin: 0.375rem 0.3rem;
	transition: transform .2s;
}
button.tab-btn:hover, button.tab-btn.active {
	transform: scale(1.5);
}

button.tab-btn.active>div {
	border-bottom: 2px solid $primary;
	border-radius: 0;
	padding-bottom: 2px;
}
</style>
