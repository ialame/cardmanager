<template>
    <AdvancedForm v-model="value" :small="true">
        <div class="container">
            <FormRow class="mb-2">
                <OnePieceSerieSelect path="serie" advanced />
            </FormRow>
            <FormRow class="mb-2">
                <AdvancedFormInput label="Code" path="code" />
                <Column class="mb-2">
                    <AdvancedFormCheckbox label="Promo" path="promo" />
                </Column>
            </FormRow>
            <AdvancedFormTranslations label="Traductions" path="translations" :localizations="['us', 'jp']" #default="{path}">
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
                </FormRow>
                <FormRow>
                    <AdvancedFormInput label="Date de sortie" :path="concatPaths([path, 'releaseDate'])" type="date" min="1990-01-01" :max="CommonSetEditLogic.maxDate" />
                </FormRow>
                <FormRow>
                    <AdvancedFormListInput label="Ids sur le site officiel" :path="concatPaths([path, 'officialSiteIds'])" #default="{path: path2}">
                        <AdvancedFormInput :path="path2" />
                    </AdvancedFormListInput>
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
import {OnePieceSetDTO} from "@/types";
import {OnePieceSerieSelect, onePieceSerieService} from "@components/cards/onepiece/serie";
import {computedAsyncGetSet} from "@/vue/composables/AsynComposables";
import {cloneDeep, isNil} from "lodash";
import {EditedOnePieceSet, EditedOnePieceSetTranslation} from "@components/cards/onepiece/set/edit/logic";

interface Props {
    modelValue?: OnePieceSetDTO;
}

interface Emits {
    (e: 'update:modelValue', value: OnePieceSetDTO): void;
    (e: 'save'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const value = computedAsyncGetSet<EditedOnePieceSet>({
    get: async () => {
        const v = (cloneDeep(props.modelValue) ?? {}) as any;

        if (!isNil(v.serieId)) {
            v.serie = await onePieceSerieService.get(v.serieId);
        }
        for (const t of Object.values(v.translations)) {
            const translation = t as EditedOnePieceSetTranslation;

            translation.officialSiteIds = props.modelValue?.officialSiteIds?.filter(i => i.localization === translation.localization)
                .map(i => i.id as number) ?? [];
        }
        delete v.serieId;
        return v;
    },
    set: (value: EditedOnePieceSet) => {
        const v = cloneDeep(value) as any;

        v.serieId = v.serie.id;
        v.officialSiteIds = [];
        for (const t of Object.values(v.translations)) {
            const translation = t as EditedOnePieceSetTranslation;

            for (const id of translation.officialSiteIds ?? []) {
                v.officialSiteIds.push({
                    id,
                    localization: translation.localization
                });
            }
            delete (t as any).officialSiteIds;
        }
        delete v.serie;
        emit('update:modelValue', v);
    }
});

</script>
