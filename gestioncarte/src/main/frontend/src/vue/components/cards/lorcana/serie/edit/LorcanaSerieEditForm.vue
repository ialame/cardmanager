<template>
    <AdvancedForm v-model="value" :small="true">
        <div class="container">
            <AdvancedFormTranslations label="Traductions" path="translations" :localizations="['us', 'fr', 'de']" #default="{path}">
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
                </FormRow>
            </AdvancedFormTranslations>
        </div>
        <template #out-of-side>
            <div class="container p-0">
                <div class="d-flex flex-row float-end">
                    <slot name="save-buttons" />
                    <AdvancedFormSaveButton @save="$emit('save')" />
                </div>
            </div>
        </template>
    </AdvancedForm>
</template>

<script lang="ts" setup>
import {concatPaths} from "@/path";
import FormRow from "@components/form/FormRow.vue";
import {
    AdvancedForm,
    AdvancedFormInput,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {LorcanaSerieDTO} from "@/types";
import {useVModel} from "@vueuse/core";

interface Props {
    modelValue: LorcanaSerieDTO;
}

interface Emits {
    (e: 'update:modelValue', value: LorcanaSerieDTO): void;
    (e: 'save'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const value = useVModel(props, 'modelValue', emit);

</script>
