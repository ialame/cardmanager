<template>
    <AdvancedForm v-model="cardTag" :rules="rules">
        <FormRow>
            <AdvancedFormSelect path="type" label="Type" required="required" :values="cardTagTypes" />
        </FormRow>
        <AdvancedFormTranslations path="translations" label="Traductions" required="required" availableSubpath="" :requiredLocalizations="{ 'us': 'optional'}" #default="{ path: p }">
            <AdvancedFormInput :path="concatPaths([p, 'name'])" />
        </AdvancedFormTranslations>
        <AdvancedFormSaveButton @save="$emit('save')" />
    </AdvancedForm>
</template>

<script lang="ts" setup>
import {
    AdvancedForm,
    AdvancedFormInput,
    AdvancedFormSaveButton,
    AdvancedFormSelect,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {CardTagDTO} from "@/types";
import FormRow from "@components/form/FormRow.vue";
import {useVModel} from "@vueuse/core";
import {defaultValidator, ValidationRules} from "@/validation";
import {concatPaths} from '@/path';
import {localizationCodes} from "@/localization";
import {useCardTagTypes} from "@components/cards/tags/logic";

interface Props {
    modelValue: CardTagDTO;
}

interface Emits {
    (e: 'update:modelValue', value: CardTagDTO): void;
    (e: 'save'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const cardTag = useVModel(props, 'modelValue', emit);
const cardTagTypes = useCardTagTypes();
const rules: ValidationRules<CardTagDTO> = {
    type: { validators: [defaultValidator], required: 'optional', },
    translations: {
        required: 'optional',
        each: {
            keys: localizationCodes,
            name: { validators: [defaultValidator], required: 'optional' },
        },
    },
};
</script>
