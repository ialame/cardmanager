<template>
    <AdvancedForm v-model="value" :rules="rules" small>
        <div class="container">
            <FormRow class="mb-2">
                <LorcanaSerieSelect path="serie" advanced />
            </FormRow>
            <FormRow class="mb-2">
                <AdvancedFormInput label="Number" path="number" />
                <Column class="mb-2">
                    <AdvancedFormCheckbox label="Promo" path="promo" />
                </Column>
            </FormRow>
            <AdvancedFormTranslations label="Traductions" path="translations" :localizations="['us', 'fr', 'de']" #default="{path}">
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
                </FormRow>
                <FormRow>
                    <AdvancedFormInput label="Date de sortie" :path="concatPaths([path, 'releaseDate'])" type="date" min="1990-01-01" :max="CommonSetEditLogic.maxDate" />
                </FormRow>
            </AdvancedFormTranslations>
            <FormRow>
                <AdvancedFormListInput label="Extension Mushu report" path="mushuSets" initialValue="{key: '', name: ''}" #default="{path}">
                    <FormRow class="w-100">
                        <AdvancedFormInput label="Numéro" :path="concatPaths([path, 'key'])" />
                        <AdvancedFormInput label="Nom" :path="concatPaths([path, 'name'])" />
                    </FormRow>
                </AdvancedFormListInput>
            </FormRow>
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
import {CommonSetEditLogic} from "@components/form/set/CommonSetEditLogic";
import FormRow from "@components/form/FormRow.vue";
import {
    AdvancedForm,
    AdvancedFormCheckbox,
    AdvancedFormInput,
    AdvancedFormListInput,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import Column from "@components/grid/Column.vue";
import {LorcanaSetDTO} from "@/types";
import {LorcanaSerieSelect, lorcanaSerieService} from "@components/cards/lorcana/serie";
import {computedAsyncGetSet} from "@/vue/composables/AsynComposables";
import {cloneDeep, isNil} from "lodash";
import {EditedLorcanaSet} from "@components/cards/lorcana/set/edit/logic";
import {rules} from "@components/cards/lorcana/set/edit/validation";

interface Props {
    modelValue?: LorcanaSetDTO;
}

interface Emits {
    (e: 'update:modelValue', value: LorcanaSetDTO): void;
    (e: 'save'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const value = computedAsyncGetSet<EditedLorcanaSet>({
    get: async () => {
        const v = (cloneDeep(props.modelValue) || {}) as any;

        if (!isNil(v.serieId)) {
            v.serie = await lorcanaSerieService.get(v.serieId);
        }
        delete v.serieId;
        return v;
    },
    set: (value: EditedLorcanaSet) => {
        const v = cloneDeep(value) as any;

        v.serieId = v.serie?.id;
        delete v.serie;
        emit('update:modelValue', v);
    }
});

</script>
