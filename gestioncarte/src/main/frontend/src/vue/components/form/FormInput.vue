<template>
	<Column class="form-group">
		<label class="form-label ps-0" v-if="isVisible"><slot name="label">{{label}}</slot></label>
		<div :class="validClass">
			<slot name="before"></slot>
			<slot>
				<input ref="input" :type="type" :class="classes + ' form-control'"  :disabled="disabled" :value="modelValue" @input="(event) => $emit('update:modelValue', event.target.value)" @keyup="onKeyup" :required="required" autocomplete="nope" :placeholder="placeholder" />
			</slot>
			<slot name="after"></slot>
		</div>
		<div v-if="validFeedback" class="valid-feedback">{{validFeedback}}</div>
		<div v-if="invalidFeedback" class="invalid-feedback">{{invalidFeedback}}</div>
	</Column>
</template>

<script lang="ts" setup>
import Column from '@components/grid/Column.vue';
import {computed, ref, Ref, useSlots} from "vue";
import {DOMComposables} from "@/vue/composables/DOMComposables";

interface Props {
	label?: string;
	modelValue?: string | number;
	required?: boolean;
	disabled?: boolean;
	validFeedback?: string;
	invalidFeedback?: string;
	classes?: string;
    type?: string;
    placeholder?: string;
}
interface Emits {
	(e: 'update:modelValue', f: string | number): void
	(e: 'submit'): void
	(e: 'keyup', f: KeyboardEvent): void
}

const props = withDefaults(defineProps<Props>(), {
	label: "",
	modelValue: "",
	required: false,
	disabled: false,
	validFeedback: "",
	invalidFeedback: "",
	classes: "",
    type: "text"
});
const emit = defineEmits<Emits>();
const slots = useSlots();
const isVisible = computed(() => props.label || (slots.label && slots.label().findIndex(o => o.type !== Comment) !== -1));
const onKeyup = (e: KeyboardEvent): void => {
	emit('keyup', e);
	if (e.key === 'Enter') {
		emit('submit');
	}
};
const validClass = computed(() => {
	let classes = 'd-flex flex-row w-100';

	if (props.classes.includes('is-valid') && props.validFeedback) {
		classes += ' is-valid';
	} else if (props.classes.includes('is-invalid') && props.invalidFeedback) {
		classes += ' is-invalid';
	}
	return classes;
});
const input = ref<HTMLInputElement>();
const overflown = DOMComposables.useOverflown(input as Ref<HTMLElement>);

defineExpose( { overflown });
</script>
