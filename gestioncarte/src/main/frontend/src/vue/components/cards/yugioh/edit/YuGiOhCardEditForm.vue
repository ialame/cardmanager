<template>
    <AdvancedForm v-model="value" :mergeSources="mergeSources" :rules="yugiohCardRules" small>
        <div class="container">
            <AdvancedFormMergeHeader :labels="['Carte extraite', 'Carte en BDD']" />
            <FormRow>
                <AdvancedFormInput path="level" label="Niveau" type="number" />
                <AdvancedFormInput path="artist" label="Artiste" />
            </FormRow>
            <FormRow class="align-items-end">
                <CardFoilSelect path="foil" label="Foil" />
                <Column class="mb-2">
                    <AdvancedFormCheckbox path="sneakPeek" label="Sneak Peek" />
                </Column>
            </FormRow>
            <FormRow>
                <YuGiOhSetSelect path="setIds" advanced :mapper="setMapper" />
            </FormRow>
            <AdvancedFormListInput path="types" label="Types" initialValue="" #default="{ path }">
                <AdvancedFormInput :path="path" />
            </AdvancedFormListInput>
            <CardTagListInput />
            <AdvancedFormTranslations path="translations" :localizations="yugiohLocalizations" #default="{ path }">
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'number'])" label="Numero" />
                    <AdvancedFormInput :path="concatPaths([path, 'rarity'])" label="Rareté" />
                </FormRow>
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
                    <AdvancedFormInput :path="concatPaths([path, 'labelName'])" label="Nom d'étiquette" />
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
import {
    AdvancedForm,
    AdvancedFormCheckbox,
    AdvancedFormInput,
    AdvancedFormListInput,
    AdvancedFormMergeHeader,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {YuGiOhCardDTO} from "@/types";
import FormRow from "@components/form/FormRow.vue";
import {CardTagListInput} from "@components/cards/tags";
import {concatPaths} from "@/path";
import {setMapper, yugiohCardRules} from "@components/cards/yugioh/edit";
import {YuGiOhSetSelect} from "@components/cards/yugioh/set";
import {CardFoilSelect} from "@components/cards/foil";
import {useYuGiOhLocalizations} from "@components/cards/yugioh";
import Column from "@components/grid/Column.vue";
import {useVModel} from "@vueuse/core/index";

interface Props {
    modelValue: YuGiOhCardDTO;
    mergeSources?: YuGiOhCardDTO[];
}

interface Emits {
    (e: 'update:modelValue', value: YuGiOhCardDTO): void;
    (e: 'save'): void;
}

const props = withDefaults(defineProps<Props>(), {
    mergeSources: () => []
});
const emit = defineEmits<Emits>();
const yugiohLocalizations = useYuGiOhLocalizations();

const value = useVModel(props, 'modelValue', emit);
</script>
