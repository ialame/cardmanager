<template>
    <div class="form-check" :class="{ 'form-switch': slider }">
        <input v-bind="inputProps" class="form-check-input advanced-form-checkbox" :type="type" :checked="source" @change="e => value = e.target.checked" :indeterminate.prop="indeterminate">
        <slot name="label"><label v-if="label" class="form-check-label ms-1">{{label}}</label></slot>
    </div>
</template>

<script lang = "ts" setup>
import {Path} from "@/path";
import {useAdvancedFormInput} from "@components/form/advanced/logic";

interface Props {
    path: Path;
    label?: string;
    slider?: boolean;
    indeterminate?: boolean;
    type?: 'checkbox' | 'radio';
}

const props = withDefaults(defineProps<Props>(), {
    slider: true,
    indeterminate: false,
    type: 'checkbox'
});

const { value, source, inputProps } = useAdvancedFormInput<any, boolean>(() => props.path);

</script>

<style lang="scss" scoped>
@import "src/variables";

.form-check {
    .form-check-input.is-valid {
        border-color: rgba(0, 0, 0, 0.25);
        &:checked {
            background-color: $primary;
            border-color: $primary;
        }
        ~ .form-check-label {
            color: $black;
        }
    }
    display: flex;
}
</style>
