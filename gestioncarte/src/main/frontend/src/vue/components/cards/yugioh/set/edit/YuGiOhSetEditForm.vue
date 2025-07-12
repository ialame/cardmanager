<template>
    <AdvancedForm v-model="value" :small="true" :rules="yugiohSetRules">
        <div class="container">
            <FormRow class="mb-2">
                <YuGiOhSerieSelect path="serie" advanced />
            </FormRow>
            <FormRow class="mb-2">
                <AdvancedFormInput label="Type" path="type" />
                <Column class="mb-2">
                    <AdvancedFormCheckbox label="Promo" path="promo" />
                </Column>
            </FormRow>
            <AdvancedFormTranslations label="Traductions" path="translations" :localizations="yugiohLocalizations" #default="{path}">
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
                    <AdvancedFormInput :path="concatPaths([path, 'code'])" label="Code" />
                </FormRow>
                <FormRow>
                    <AdvancedFormInput label="Date de sortie" :path="concatPaths([path, 'releaseDate'])" type="date" min="1990-01-01" :max="CommonSetEditLogic.maxDate" />
                </FormRow>
                <FormRow>
                    <AdvancedFormListInput label="PIDs site officiel" :path="concatPaths([path, 'officialSitePids'])" initialValue="" #default="{path: path2}">
                        <AdvancedFormInput :path="path2" />
                    </AdvancedFormListInput>
                </FormRow>
                <FormRow>
                    <AdvancedFormListInput label="URL Yugipedia" :path="concatPaths([path, 'yugipediaSets'])" initialValue="" #default="{path: path2}">
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
import {YuGiOhSetDTO} from "@/types";
import {
    AdvancedForm,
    AdvancedFormCheckbox,
    AdvancedFormInput,
    AdvancedFormListInput,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import Column from "@components/grid/Column.vue";
import FormRow from "@components/form/FormRow.vue";
import {concatPaths} from "@/path";
import {useYuGiOhLocalizations} from "@components/cards/yugioh";
import {YuGiOhSerieSelect, yugiohSerieService} from "@components/cards/yugioh/serie";
import {EditedYuGiOhSet, EditedYuGiOhSetTranslation, yugiohSetRules} from "@components/cards/yugioh/set";
import {cloneDeep, isNil} from "lodash";
import {computedAsync} from "@vueuse/core";
import {computed} from "vue";
import {CommonSetEditLogic} from "@components/form/set/CommonSetEditLogic";

interface Props {
    modelValue: YuGiOhSetDTO;
}

interface Emits {
    (e: 'update:modelValue', value: YuGiOhSetDTO): void;
    (e: 'save'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();
const yugiohLocalizations = useYuGiOhLocalizations();

const serie = computedAsync(async () => {
    if (!isNil(props.modelValue?.serieId)) {
        return await yugiohSerieService.get(props.modelValue.serieId);
    }
    return undefined;
});

const value = computed<EditedYuGiOhSet>({
    get: () => {
        if (isNil(props.modelValue)) {
            return undefined;
        }

        const copy = cloneDeep(props.modelValue) as any;

        if (!isNil(serie.value)) {
            copy.serie = serie.value;
        }
        for (const t of Object.values(copy.translations)) {
            const translation = t as EditedYuGiOhSetTranslation;

            translation.officialSitePids = props.modelValue.officialSitePids
                ?.filter(p => p.localization === translation.localization)
                .map(p => p.pid as string) ?? [];
        }
        for (const t of Object.values(copy.translations)) {
            const translation = t as EditedYuGiOhSetTranslation;

            translation.yugipediaSets = props.modelValue.yugipediaSets
                ?.filter(p => p.localization === translation.localization)
                .map(p => p.url as string) ?? [];
        }
        delete copy.officialSitePids;
        delete copy.yugipediaSets;
        delete copy.serieId;
        return copy;
    },
    set: (v: EditedYuGiOhSet) => {
        if (isNil(v)) {
            return;
        }
        
        const copy = cloneDeep(v) as any;

        copy.serieId = copy.serie?.id;
        copy.officialSitePids = [];
        copy.yugipediaSets = [];
        for (const t of Object.values(copy.translations)) {
            const translation = t as EditedYuGiOhSetTranslation;

            for (const pid of translation.officialSitePids ?? []) {
                copy.officialSitePids.push({
                    pid: pid,
                    localization: translation.localization
                });
            }
            for (const url of translation.yugipediaSets ?? []) {
                copy.yugipediaSets.push({
                    url: url,
                    localization: translation.localization
                });
            }
            delete (t as any).officialSitePids;
            delete (t as any).yugipediaSets;
        }
        delete copy.serie;
        emit('update:modelValue', copy);
    }
});

</script>
