<template>
    <AdvancedForm
        v-model="value"
        v-model:reviewedPaths="rp"
        v-model:optionalPaths="op"
        :mergeSources="ms"
        :rules="rules"
        small
    >
        <div class="container">
            <AdvancedFormMergeHeader :labels="['Carte extraite', 'Carte en BDD']" #="{side: side}">
                <FormButton v-if="side !== -1" color="danger" :newTab="true" class="mb-1 me-2 form-btn" @click="removeSource(side)">
                    <Icon name="trash" />
                </FormButton>
            </AdvancedFormMergeHeader>
            <FormRow>
                <AdvancedFormInput path="number" label="Numero" />
                <AdvancedFormInput path="artist" label="Artiste" />
            </FormRow>
            <FormRow>
                <AdvancedFormInput path="rarity" label="Rareté" />
                <AdvancedFormInput path="attribute" label="Attribut" />
            </FormRow>
            <FormRow class="align-items-end">
                <AdvancedFormInput path="color" label="Couleur" />
                <AdvancedFormInput path="type" label="Type" />
                <AdvancedFormInput path="parallel" label="Parallel" type="number" />
            </FormRow>
            <FormRow>
                <OnePieceSetSelect path="setIds" advanced :mapper="setMapper" />
            </FormRow>
            <CardTagListInput />
            <AdvancedFormTranslations path="translations" :localizations="['us', 'jp']" #default="{ path, localization }">
                <FormRow>
                    <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
                    <AdvancedFormInput :path="concatPaths([path, 'labelName'])" label="Nom d'étiquette" />
                </FormRow>
                <PromoList :localization="localization" path="promos" tcg="onepiece" />
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
import {OnePieceCardDTO} from "@/types";
import FormRow from "@components/form/FormRow.vue";
import {CardTagListInput} from "@components/cards/tags";
import {concatPaths, Path} from "@/path";
import {OnePieceSetSelect} from "@components/cards/onepiece/set";
import {useVModel} from "@vueuse/core";
import {setMapper} from "@components/cards/onepiece/edit/logic";
import {rules} from "@components/cards/onepiece/edit/validation";
import {PromoList} from "@components/cards/promo";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {triggerRef} from "vue-demi";

interface Props {
    modelValue: OnePieceCardDTO;
    mergeSources?: OnePieceCardDTO[];
    reviewedPaths?: Path[],
    optionalPaths?: Path[],
}

interface Emits {
    (e: 'update:modelValue', value: OnePieceCardDTO): void;
    (e: 'update:mergeSources', value: OnePieceCardDTO[]): void;
    (e: 'update:reviewedPaths', v: Path[]): void;
    (e: 'update:optionalPaths', v: Path[]): void;
    (e: 'save'): void;
}

const props = withDefaults(defineProps<Props>(), {
    mergeSources: () => []
});
const emit = defineEmits<Emits>();

const value = useVModel(props, 'modelValue', emit);
const ms = useVModel(props, 'mergeSources', emit);
const rp = useVModel(props, 'reviewedPaths', emit);
const op = useVModel(props, 'optionalPaths', emit);

const removeSource = (side: number) => {
    ms.value.splice(side, 1);
    triggerRef(ms);
};

</script>
