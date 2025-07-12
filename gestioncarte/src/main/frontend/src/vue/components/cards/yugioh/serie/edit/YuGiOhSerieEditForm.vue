<template>
    <AdvancedForm v-model="value" :small="true">
        <div class="container">
            <AdvancedFormTranslations label="Traductions" path="translations" :localizations="yugiohLocalizations" #default="{path}" availableSubpath="">
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
import {YuGiOhSerieDTO} from "@/types";
import {computed} from "vue";
import {
    AdvancedForm,
    AdvancedFormInput,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {useYuGiOhLocalizations} from "@components/cards/yugioh";
import FormRow from "@components/form/FormRow.vue";
import {concatPaths} from "@/path";

interface Props {
    modelValue: YuGiOhSerieDTO;
}

interface Emits {
    (e: 'update:modelValue', value: YuGiOhSerieDTO): void;
    (e: 'save'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();
const yugiohLocalizations = useYuGiOhLocalizations();

const value = computed({
    get: () => props.modelValue,
    set: (v: YuGiOhSerieDTO) => emit('update:modelValue', v)
});

</script>
