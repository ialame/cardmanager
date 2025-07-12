<template>
    <AdvancedForm v-model="value" :mergeSources="ms" :rules="rules" small>
        <div class="container">
            <AdvancedFormMergeHeader :labels="['Carte extraite', 'Carte en BDD']" #="{side: side}">
                <FormButton v-if="side !== -1" color="danger" :newTab="true" class="mb-1 me-2 form-btn" @click="removeSource(side)">
                    <Icon name="trash" />
                </FormButton>
            </AdvancedFormMergeHeader>
            <FormRow>
                <AdvancedFormInput path="artist" label="Artiste" />
            </FormRow>
            <FormRow>
                <AdvancedFormSelect path="rarity" label="Rareté"  :values="['Common', 'Uncommon', 'Rare', 'Super Rare', 'Legendary', 'Enchanted', 'Promo']"/>
                <AdvancedFormSelect path="ink" label="Ink" :values="['Amber', 'Amethyst', 'Emerald', 'Ruby', 'Sapphire', 'Steel']" />
                <AdvancedFormCheckbox path="reprint" label="Reprint" />
            </FormRow>
            <FormRow>
                <LorcanaSetSelect path="set" advanced />
            </FormRow>
            <CardTagListInput />
            <AdvancedFormTranslations path="translations" :localizations="['us', 'fr', 'de']" #default="{ path }">
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'number'])" label="Numero" />
                    <AdvancedFormInput :path="concatPaths([path, 'fullNumber'])" label="Numero complet" />
                </FormRow>
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
                    <AdvancedFormInput :path="concatPaths([path, 'labelName'])" label="Nom d'étiquette" />
                </FormRow>
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'title'])" label="Title" />
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
    AdvancedFormInput,
    AdvancedFormMergeHeader,
    AdvancedFormSaveButton,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {LorcanaCardDTO, LorcanaCardTranslationDTO} from "@/types";
import FormRow from "@components/form/FormRow.vue";
import {CardTagListInput} from "@components/cards/tags";
import {concatPaths} from "@/path";
import {LorcanaSetSelect, lorcanaSetService} from "@components/cards/lorcana/set";
import {EditedLorcanaCard} from "@components/cards/lorcana/edit/logic";
import {computedAsyncGetSet} from "@/vue/composables/AsynComposables";
import {cloneDeep, isNil, trim} from "lodash";
import AdvancedFormCheckbox from "@components/form/advanced/input/AdvancedFormCheckbox.vue";
import AdvancedFormSelect from "@components/form/advanced/input/AdvancedFormSelect.vue";
import {rules} from "@components/cards/lorcana/edit/validation";
import Icon from "@components/Icon.vue";
import FormButton from "@components/form/FormButton.vue";
import {triggerRef} from "vue-demi";
import {useVModel} from "@vueuse/core";

interface Props {
    modelValue: LorcanaCardDTO;
    mergeSources?: LorcanaCardDTO[];
}

interface Emits {
    (e: 'update:modelValue', value: LorcanaCardDTO): void;
    (e: 'update:mergeSources', value: LorcanaCardDTO[]): void;
    (e: 'save'): void;
}

const props = withDefaults(defineProps<Props>(), {
    mergeSources: () => []
});
const emit = defineEmits<Emits>();

const handleTranslationChange = (translation: LorcanaCardTranslationDTO) => {
    if (translation?.fullNumber && !translation.number) {
        const split = translation.fullNumber.split('·');

        if (split.length) {
            translation.number = trim(split[0]);
        }
    }
    if (translation?.name) {
        const split = translation.name.split(' - ');

        if (split.length >= 2 && (!translation.title || !translation.labelName)) {
            translation.name = trim(split[0]);
            if (!translation.labelName) {
                translation.labelName = trim(split[0]);
            }
            if (!translation.title) {
                translation.title = trim(split[1]);
            }
        } else if (!translation.labelName) {
            translation.labelName = translation.name;
        }
    }
}

const value = computedAsyncGetSet<EditedLorcanaCard>({
    get: async () => {
        const v = cloneDeep(props.modelValue) as any;

        if (!isNil(v.setIds) && v.setIds.length > 0) {
            v.set = await lorcanaSetService.get(v.setIds[0]);
        }
        delete v.setIds;
        return v;
    },
    set: (value: EditedLorcanaCard) => {
        if (isNil(value)) {
            return;
        }

        const v = cloneDeep(value) as any;

        if (!isNil(v?.set?.id)) {
            v.setIds = [v.set.id];
        }
        Object.values(v?.translations).forEach(t => handleTranslationChange(t as LorcanaCardTranslationDTO));
        delete v.set;
        emit('update:modelValue', v);
    }
});

const ms = useVModel(props, 'mergeSources', emit);

const removeSource = (side: number) => {
    ms.value.splice(side, 1);
    triggerRef(ms);
};
</script>
