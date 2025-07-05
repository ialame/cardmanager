<template>
    <FormInput v-bind="$attrs" :classes=" errorMessage ? 'is-invalid': ''" :modelValue="props.modelValue" @update:modelValue="v => $emit('update:modelValue', v)" :invalidFeedback="errorMessage">
        <template #label>
            <slot name="label"><a href="https://regex101.com/" target="_blank" rel="noopener">{{label}}</a></slot>
        </template>
        <template v-for="(_, slot) in $slots" :key="slot" #[slot]="data">
            <slot :name="slot" v-bind="data" :key="slot"></slot>
        </template>
    </FormInput>
</template>

<script lang="ts" setup>
import FormInput from '@components/form/FormInput.vue';
import {computed} from "vue";

interface Props {
    modelValue?: string;
    label?: string;
}
interface Emits {
    (e: 'update:modelValue', f: string): void
}

const props = withDefaults(defineProps<Props>(), {
    label: 'Regex'
});
defineEmits<Emits>();

const errorMessage = computed(() => {
    if (!props.modelValue) {
        return '';
    }
    try {
        new RegExp(props.modelValue);
        return '';
    } catch (e: any) {
        return `La Regex est invalide: ${e.message.replace('Invalid regular expression: ','')}`;
    }
});

</script>
