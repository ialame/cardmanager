<template>
	<template v-if="editable || !isEmpty(modelValue)">
		<label class="form-label ps-0" v-if="label">{{label}}</label>
		<button type="button" class="btn no-focus p-0 ms-1" @click="addItem()" v-if="editable"><Icon name="add" class="icon-24" /></button>
		<div v-for="(item, index) in entries" :key="item.key">
            <hr v-if="useSeparators && index > 0" class="w-100"/>
            <div class="d-flex flex-row mb-1 list-input-item" :class="itemClass">
                <slot :item="item" :items="entries" :first="index === 0"></slot>
                <div class="ms-2 d-flex flex-column" v-if="editable">
                    <FormButton color="danger" class="mt-auto list-input-button" @click="deleteItem(item.key)"><Icon name="trash" /></FormButton>
                </div>
            </div>
		</div>
	</template>
</template>

<script lang="ts" setup>
import {cloneDeep, concat, find, isEmpty, map} from 'lodash';
import {computed} from 'vue';
import FormButton from '../FormButton.vue';
import Icon from "@components/Icon.vue";

class Entry {
	key: number;
	private _value: any;

	constructor(key: number, value: any) {
		this.key = key;
		this._value = value;
	}

	get value() {
		return this._value;
	}
	set value(value: any) {
		this._value = value;

		update(this.key, value);
	}
}

interface Props {
	modelValue: any[];
	label?: string;
	editable?: boolean;
	initialValue?: any;
    itemClass?: string;
    useSeparators?: boolean;
}

interface Emits {
	(e: 'update:modelValue', value: any[]): void;
	(e: 'newItem', value: any): void;
	(e: 'deleteItem', value: any, i: number): void;
}

const props = withDefaults(defineProps<Props>(), {
	editable: true,
	initialValue: {},
    useSeparators: false,
});
const emit = defineEmits<Emits>();

const deleteItem = (i: number) => {
	const list = [];

	for (const item of entries.value) {
		if (item.key !== i) {
			list.push(item.value);
		}
	}
	emit('deleteItem', entries.value[i], i);
	emit('update:modelValue', list);
};
const addItem = () => {
	const value = props.initialValue instanceof Function ? props.initialValue() : cloneDeep(props.initialValue);

	emit('newItem', value);
	emit('update:modelValue', concat(props.modelValue || [], value))
};
const entries = computed(() => {
	let i = 0;
		
	return map(props.modelValue, v => new Entry(i++, v));
});
const update = (key: number, value: any) => {
	const list = map(entries.value, e => {
		return {
			key: e.key,
			value: e.value
		};
	});
	const target = find(list, e => e.key === key);

	if (target) {
		target.value = value;

		emit('update:modelValue', map(list, "value"));
	}
};
</script>
