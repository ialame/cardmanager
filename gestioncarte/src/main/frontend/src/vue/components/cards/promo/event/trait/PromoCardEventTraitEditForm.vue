<template>
    <AdvancedForm v-model="value" :mergeSources="mergeSources" :disabled="disabled" small>
        <AdvancedFormMergeHeader :labels="['Carcteristique extraite', 'Carcteristique en BDD']" />
        <div class="container">
            <FormRow>
                <AdvancedFormInput path="name" label="Nom" />
                <AdvancedFormInput path="type" label="Type" />
            </FormRow>
            <AdvancedFormTranslations path="translations" label="Traductions" availableSubpath="" #default="{path}" :localizations="localizations">
                <FormRow class="w-100">
                    <Column>
                        <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
                    </Column>
                    <Column>
                        <AdvancedFormInput :path="concatPaths([path, 'labelName'])" label="Nom d'étiquette" />
                    </Column>
                </FormRow>
            </AdvancedFormTranslations>
        </div>
        <template #out-of-side>
            <div v-if="!disabled" class="p-0">
                <div class="d-flex flex-row float-end">
                    <AdvancedFormSaveButton @save="$emit('save')" />
                </div>
            </div>
        </template>
    </AdvancedForm>
</template>

<script lang="ts" setup>
import {
    AdvancedForm,
    AdvancedFormInput,
    AdvancedFormMergeHeader,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {PromoCardEventTraitDTO} from "@/types";
import {computed} from "vue";
import FormRow from "@components/form/FormRow.vue";
import {concatPaths} from "@/path";
import Column from "@components/grid/Column.vue";
import {LocalizationCode} from "@/localization";

interface Props {
    modelValue: PromoCardEventTraitDTO;
    mergeSources?: PromoCardEventTraitDTO[];
    localizations?: LocalizationCode[];
    disabled?: boolean;
}

interface Emits {
    (e: 'update:modelValue', v: PromoCardEventTraitDTO): void
    (e: 'save'): void
}

const props = withDefaults(defineProps<Props>(), {
    disabled: false
});
const emit = defineEmits<Emits>();

const value = computed({
    get: () => props.modelValue,
    set: (v: PromoCardEventTraitDTO) => emit('update:modelValue', v)
});
</script>
