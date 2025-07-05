<template>
    <AdvancedFormCollapse v-if="!readOnly || !isEmpty(source)" :path="path" :label="label" class="form-group">
        <template #label><slot name="label" /></template>
        <template #after-label>
            <template v-if="!readOnly">
                <FormButton color="secondary" class="ms-2 form-btn" @click.stop="addItem()"><Icon name="add" /></FormButton>
                <FormButton v-if="reviewable" color="success" class="ms-2 form-btn" @click.stop="reviewed = true"><Icon name="checkmark-outline" /></FormButton>
            </template>
            <AdvancedFormFeedback class="ms-2" :validationResults="validationResults" />
            <AdvancedFormMergeButton v-if="readOnly" :path="path" />
        </template>
        <div v-for="(item, index) in entries" :key="item.key" class="mt-1">
            <hr v-if="useSeparators && index > 0" class="w-100" />
            <div class="d-flex flex-row list-input-item" :class="itemClass">
                <slot :item="item" :items="entries" :first="index === 0" :path="concatPaths([path, item.key])" :index="index" />
                <div v-if="!readOnly" class="ms-2 d-flex flex-column list-input-button-container">
                    <FormButton color="danger" class="list-input-button form-btn" @click="deleteItem(item.key)"><Icon name="trash" /></FormButton>
                </div>
            </div>
        </div>
    </AdvancedFormCollapse>
</template>

<script lang="ts" setup>
import {cloneDeep, concat, find, isEmpty} from 'lodash';
import {computed} from 'vue';
import FormButton from '@components/form/FormButton.vue';
import Icon from "@components/Icon.vue";
import AdvancedFormCollapse from "@components/form/advanced/AdvancedFormCollapse.vue";
import AdvancedFormMergeButton from "@components/form/advanced/merge/AdvancedFormMergeButton.vue";
import AdvancedFormFeedback from "@components/form/advanced/AdvancedFormFeedback.vue";
import {useAdvancedFormInput} from "@components/form/advanced/logic";
import {concatPaths, Path} from "@/path";
import {sortArrayToValue} from "@/validation";

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
    path: Path;
    label?: string;
    initialValue?: any;
    itemClass?: string;
    useSeparators?: boolean;
    filter?: (v: any) => boolean;
}

interface Emits {
    (e: 'newItem', value: any): void;
    (e: 'deleteItem', value: any, i: number): void;
}

const props = withDefaults(defineProps<Props>(), {
    required: "collapsable",
    initialValue: {},
    useSeparators: false,
    filter: () => true,
});
const emit = defineEmits<Emits>();

const { value, source, readOnly, validationResults, reviewable, reviewed, side, rules } = useAdvancedFormInput<any, any[]>(() => props.path);

const deleteItem = (i: number) => {
    const list = [];

    for (const item of entries.value) {
        if (item.key !== i) {
            list.push(item.value);
        }
    }
    emit('deleteItem', entries.value[i], i);
    value.value = list;
};
const addItem = () => {
    const v = props.initialValue instanceof Function ? props.initialValue() : cloneDeep(props.initialValue);

    emit('newItem', v);
    value.value = concat(value.value ?? [], v);
};
const entries = computed(() => {
    let i = 0;
    let val = source.value ?? [];

    if (side.value >= 0) {
        val = sortArrayToValue(val, value.value, rules.value?.each?.comparator);
    }
    return val.map(v => new Entry(i++, v))
        .filter(e => props.filter(e.value));
});
const update = (key: number, v: any) => {
    const list = entries.value.map(e => {
        return {
            key: e.key,
            value: e.value
        };
    });
    const target = find(list, e => e.key === key);

    if (target) {
        target.value = v;

        value.value = list;
    }
};
</script>

<style lang="scss" scoped>
:not(.advanced-form-collapse:has(>div>input.advanced-form-input, >div>div>input.advanced-form-select)) + .list-input-button-container {
    margin-top: auto !important;

    .form-btn {
        margin-bottom: 1px !important;
    }
}
</style>
