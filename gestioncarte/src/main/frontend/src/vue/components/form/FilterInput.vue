<template>
    <FormRow>
        <div :class="{ 'is-valid': count > 0, 'is-invalid': count === 0 }">
            <ListInput label="Filtres" :modelValue="modelValue" @update:modelValue="v => $emit('update:modelValue', v)" initialValue="">
                <template v-slot="slotProps">
                    <FormInput v-model="slotProps.item.value" @submit="$emit('submit')" @keyup="e => $emit('keyup', e)" />
                </template>
            </ListInput>
        </div>
        <div v-if="modelValue && count > 0" class="valid-feedback">{{count}} cartes trouvées pour ce filtre.</div>
        <div v-if="modelValue && count === 0" class="invalid-feedback">Pas de carte trouvée pour ce filtre.</div>
    </FormRow>
</template>

<script lang="ts" setup>
import FormInput from './FormInput.vue';
import FormRow from './FormRow.vue';
import ListInput from "@components/form/list/ListInput.vue";

interface Props {
    modelValue: string[],
    count?: number
}

interface Emits {
    (e: 'update:modelValue', v: string[]): void
    (e: 'submit'): void
    (e: 'keyup', ev: KeyboardEvent): void
}

withDefaults(defineProps<Props>(), {
	count: 0
});
defineEmits<Emits>();
</script>
