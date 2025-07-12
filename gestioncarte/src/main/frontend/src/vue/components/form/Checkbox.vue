<template>
	<div class="form-check" :class="{ 'form-switch': slider }">
		<input class="form-check-input" :class="inputClass" type="checkbox" :checked="props.modelValue || false" @click.stop @change="event => $emit('update:modelValue', event.target.checked)" :disabled="disabled" :indeterminate.prop="indeterminate">
		<slot name="label"><label v-if="label" class="form-check-label ms-1">{{label}}</label></slot>
	</div>
</template>

<script lang = "ts" setup>
interface Props {
	label?: string;
	modelValue?: boolean;
	required?: boolean;
	disabled?: boolean;
	inputClass?: string;
    slider?: boolean;
    indeterminate?: boolean;
}
interface Emits {
	(e: 'update:modelValue', v: boolean): void
}

const props = withDefaults(defineProps<Props>(), {
	required: false,
	disabled: false,
    slider: true,
    indeterminate: false,
});
defineEmits<Emits>();
</script>

<style lang="scss" scoped>
.form-check { display: flex; }
</style>
