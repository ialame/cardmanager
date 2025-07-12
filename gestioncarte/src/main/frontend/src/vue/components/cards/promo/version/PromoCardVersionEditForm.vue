<template>
    <AdvancedForm v-model="value" small>
        <div class="container">
            <FormRow>
                <AdvancedFormInput path="name" label="Nom" />
                <Column class="mt-auto">
                    <AdvancedFormCheckbox path="hidden" label="Caché" />
                </Column>
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
            <AdvancedFormSaveButton @save="$emit('save')" />
        </div>
    </AdvancedForm>
</template>

<script lang="ts" setup>
import {
    AdvancedForm,
    AdvancedFormCheckbox,
    AdvancedFormInput,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {PromoCardVersionDTO} from "@/types";
import {computed} from "vue";
import FormRow from "@components/form/FormRow.vue";
import {concatPaths} from "@/path";
import Column from "@components/grid/Column.vue";
import {LocalizationCode} from "@/localization";

interface Props {
    modelValue: PromoCardVersionDTO;
    localizations?: LocalizationCode[];
}

interface Emits {
    (e: 'update:modelValue', v: PromoCardVersionDTO): void
    (e: 'save'): void
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const value = computed({
    get: () => props.modelValue,
    set: (v: PromoCardVersionDTO) => emit('update:modelValue', v)
});
</script>
