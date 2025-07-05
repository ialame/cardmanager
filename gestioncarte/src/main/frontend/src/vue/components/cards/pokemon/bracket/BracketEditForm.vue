<template>
    <AdvancedForm v-model="bracket" :rules="bracketValidationRules">
        <FormRow>
            <AdvancedFormInput path="name" label="Nom" required="required" />
            <AdvancedFormLocalizationSelect path="localization" label="Langue" required="required" :localizations="['us', 'jp']" />
            <Column class="pt-4">
                <AdvancedFormCheckbox class="mt-4" path="bold" label="Gras" />
            </Column>
        </FormRow>
        <AdvancedFormTranslations path="translations" label="Traductions" required="required" :localizations="localizationGroup" :requiredLocalizations="requiredLocalizations" fixedLocalizations #default="{ required: r, path: p }">
            <AdvancedFormInput :path="concatPaths([p, 'name'])" :required="r ? 'required' : 'optional'" />
        </AdvancedFormTranslations>
        <AdvancedFormSaveButton @save="$emit('save')" />
    </AdvancedForm>
</template>

<script lang="ts" setup>
import {
    AdvancedForm,
    AdvancedFormCheckbox,
    AdvancedFormInput,
    AdvancedFormLocalizationSelect,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {computed} from "vue";
import {BracketDTO} from "@/types";
import FormRow from "@components/form/FormRow.vue";
import Column from "@components/grid/Column.vue";
import {computedAsync, useVModel} from "@vueuse/core";
import rest from "@/rest";
import {bracketValidationRules} from "./validation";
import {LocalizationCode} from "@/localization";
import {concatPaths} from "@/path";

interface Props {
    modelValue: BracketDTO;
}

interface Emits {
    (e: 'update:modelValue', value: BracketDTO): void;
    (e: 'save'): void;
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const bracket = useVModel(props, 'modelValue', emit);

const localizationGroup = computedAsync<LocalizationCode[]>(async () => {
    const l = bracket.value?.localization;

    if (l) {
        return rest.get(`/api/localizations/groups/${l === 'us' ? 'west' : l}`);
    }
    return [];
});
const requiredLocalizations = computed(() => ({ [bracket.value?.localization]: 'optional' }));

</script>
