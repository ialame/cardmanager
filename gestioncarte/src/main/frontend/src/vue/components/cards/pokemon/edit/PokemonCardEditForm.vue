<template>
    <Loading v-if="!resetting">
        <AdvancedForm ref="form" v-model="value" v-model:optionalPaths="optionalPathsRef" v-model:reviewedPaths="reviewedPathsRef" :mergeSources="mergeSources" :tabConfig="tabConfig" :disabled="disabled" :rules="rules" small>
            <template #tabs>
                <AdvancedFormTabButton tab="main"><Icon name="document-outline" /></AdvancedFormTabButton>
                <AdvancedFormTabButton tab="parent"><Icon src="/svg/parent.svg" /></AdvancedFormTabButton>
                <AdvancedFormTabButton tab="save"><Icon name="save-outline" /></AdvancedFormTabButton>
            </template>
            <div class="container">
                <AdvancedFormTab tab="main">
                    <PokemonCardEditFormTabHeader :link="modelValue.link" />
                    <PokemonCardEditFormMain />
                    <PokemonCardEditFormTranslations :sourceTranslations="sourceTranslations" />
                </AdvancedFormTab>
                <AdvancedFormTab tab="parent">
                    <PokemonCardEditFormTabHeader :link="modelValue.link" />
                    <AdvancedFormInput path="parentId" label="ID carte mere" />
                </AdvancedFormTab>
            </div>
            <template #out-of-side>
                <AdvancedFormTab tab="parent">
                    <hr>
                    <SingleLineCardWithButtons v-for="card in parentCards" :key="card.id" class="parent-card" :class="{ 'selected-parent': modelValue.parentId === card.id }" :card="card" @click="value.parentId = card.id"  :showCompare="!disabled" @openInfo="setInfoCard(card)" />
                </AdvancedFormTab>
                <AdvancedFormTab tab="save">
                    <DragAndDropList :items="savedCards" v-model="cardsToSave" v-slot="{item: card}" labelLeft="Carte en base non associée" labelRight="Carte en base associée">
                        <SingleLineCardWithButtons :card="card" class="bg-white" :showCompare="!disabled" @openInfo="setInfoCard(card)" />
                    </DragAndDropList>
                </AdvancedFormTab>
                <div class="p-0">
                    <div class="d-flex flex-row float-end">
                        <FormButton color="secondary" class="me-2 mt-4 mb-4" title="Telecharger le resultat en json" @click="downloadJson(modelValue)"><Icon src="/svg/download.svg" /></FormButton>
                        <InAdvancedFormTab :tabs="['main', 'parent']">
                            <FormButton color="info" class="me-2 mt-4 mb-4" v-if="tabConfig.parent || tabConfig.save" @click="goToNextTab()">Suivant <Icon name="chevron-forward" /></FormButton>
                        </InAdvancedFormTab>
                        <InAdvancedFormTab v-if="!disabled" tabs="_last">
                            <slot name="save-buttons" />
                            <AdvancedFormSaveButton @save="$emit('save', cardsToSave)" :label="saveButtonText" />
                        </InAdvancedFormTab>
                    </div>
                </div>
            </template>
        </AdvancedForm>
    </Loading>
    <Modal ref="infoModal" label="Info carte" size="lg">
        <PokemonCardEditForm v-if="infoCard" :modelValue="infoCard" :disabled="true" />
    </Modal>
</template>

<script lang="ts" setup>
import {BracketDTO, FeatureDTO, PokemonCardDTO, PokemonSetDTO, SourcedPokemonCardTranslationDTO} from '@/types';
import {computed, ref} from 'vue';
import {
    AdvancedForm,
    AdvancedFormInput,
    AdvancedFormSaveButton,
    AdvancedFormTab,
    AdvancedFormTabButton,
    InAdvancedFormTab
} from "@components/form/advanced";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {cloneDeep, concat, toPath, uniqBy} from "lodash";
import PokemonCardEditFormTranslations from "@components/cards/pokemon/edit/PokemonCardEditFormTranslations.vue";
import PokemonCardEditFormMain from "@components/cards/pokemon/edit/PokemonCardEditFormMain.vue";
import Icon from "@components/Icon.vue";
import {
    EditedPokemonCard,
    localizationsBoundToUS,
    localizationsWithSet,
    SingleLineCardWithButtons,
    useEditedPokemonCardRules,
    useParentCards,
    useSavedCards
} from "@components/cards/pokemon/edit";
import DragAndDropList from "@components/form/list/DragAndDropList.vue";
import Modal from '@components/modal/Modal.vue';
import {RestComposables} from "@/vue/composables/RestComposables";
import rest from "@/rest";
import PokemonCardEditFormTabHeader from "@components/cards/pokemon/edit/PokemonCardEditFormTabHeader.vue";
import FormButton from "@components/form/FormButton.vue";
import {downloadData, getDateStr} from "@/retriever";
import Loading from "@components/Loading.vue";
import {Path, pathEndsWith, pathIncludes, pathStartsWith, pathToString, trimPathPrefix} from "@/path";
import {LocalizationCode, localizationCodes} from "@/localization";
import pokemonSetService = PokemonComposables.pokemonSetService;

interface Props {
	modelValue: PokemonCardDTO;
    disabled?: boolean;
    showIgnore?: boolean;
    showFullArt?: boolean;
    defaultSavedCardIds?: string[];
    diffSource?: PokemonCardDTO[];
    showMerge?: boolean;
    saveButtonText?: string;
    reviewedPaths?: Path[],
    optionalPaths?: Path[],
}

interface Emits {
	(e: 'update:modelValue', v: PokemonCardDTO): void;
    (e: 'update:reviewedPaths', v: Path[]): void;
    (e: 'update:optionalPaths', v: Path[]): void;
    (e: 'save', cardsToSave: PokemonCardDTO[]): void; // TODO move cardsToSave to props with defaultSavedCardIds
    (e: 'ignore'): void;
}

const props = withDefaults(defineProps<Props>(), {
    disabled: false,
    showIgnore: false,
    showFullArt: true,
    showMerge: true,
    defaultSavedCardIds: () => [],
    diffSource: () => [],
    reviewedPaths: () => [],
    optionalPaths: () => [],
    saveButtonText: 'Enregistrer'
});
const emit = defineEmits<Emits>();

const features = PokemonComposables.pokemonFeatureService.all;
const sets = pokemonSetService.all;
const parentCards = useParentCards(() => props.modelValue);
const { savedCards, cardsToSave } = useSavedCards(() => props.modelValue, () => props.defaultSavedCardIds, () => props.disabled);

const fixPathList = (paths: Path[]) => {
    const result = cloneDeep(paths);

    for (const name of ['brackets']) {
        if (result.some(p => pathEndsWith(p, name))) {
            result.push(name);
        }
        for (const p of result) {
            if (pathIncludes(p, name)) {
                if (pathStartsWith(p, 'us')) {
                    result.push(trimPathPrefix(p, 'us'));
                } else if (pathStartsWith(p, 'jp')) {
                    const newPath = toPath(trimPathPrefix(p, 'jp'));
                    const oldIndex = parseInt(newPath[1]);

                    newPath[1] = (oldIndex + (value.value.us as any)?.[name]?.length).toString();
                    result.push(trimPathPrefix(p, 'jp'));
                }
            }
        }
    }
    if (result.some(p => pathEndsWith(p, 'features'))) {
        result.push('featureIds');
    }
    if (result.some(p => pathEndsWith(p, 'brackets'))) {
        result.push('brackets');
    }
    if (result.some(p => pathEndsWith(p, 'set'))) {
        result.push('setIds');
    }
    if (result.some(p => pathEndsWith(p, 'translations'))) {
        result.push('setIds');
    }
    return uniqBy(result, pathToString);
}

const reviewedPathsRef = computed({
    get: () => props.reviewedPaths,
    set: (v: Path[]) => emit('update:reviewedPaths', fixPathList(v))
});
const optionalPathsRef = computed({
    get: () => props.optionalPaths,
    set: (v: Path[]) => emit('update:optionalPaths', fixPathList(v))
});

const mapTranslation = (v: any, l: LocalizationCode, usedSets: PokemonSetDTO[]) => ({
    brackets: v.brackets.filter((b: BracketDTO) => b.localization === l),
    set: usedSets.find(s => s.translations?.[l]?.available) || undefined
});

const mapToEditCard = (c: PokemonCardDTO): EditedPokemonCard => {
    const v = cloneDeep(c) as any;
    const usedSets = v.setIds
        .map((id: string) => sets.value.find(s => s.id === id))
        .filter((s: PokemonSetDTO) => !!s) as PokemonSetDTO[];

    v.type = v.type || '';
    for (const l of localizationsWithSet) {
        v[l] = mapTranslation(v, l, usedSets);
    }
    v.features = v.featureIds
        .map((id: string) => features.value.find(f => f.id === id) || {}); // FIXME use pokemonFeatureService.get(id) instead
    delete v.brackets;
    delete v.setIds;
    delete v.featureIds;
    return v;
};

const value = computed<EditedPokemonCard>({
    get: () => mapToEditCard(props.modelValue),
    set: v => {
        const newValue = cloneDeep(v) as any;

        newValue.brackets = [];
        newValue.setIds = []
        newValue.featureIds = newValue.features.map((f: FeatureDTO) => f?.id);

        for (const l of localizationsBoundToUS) {
            if (newValue.translations[l]?.available && newValue.translations.us?.available) {
                newValue.translations[l].number = newValue.translations.us.number;
                newValue.translations[l].rarity = newValue.translations.us.rarity;
            }
        }

        for (const l of localizationsWithSet) {
            newValue.brackets = [...newValue.brackets, ...newValue[l].brackets];
            newValue.setIds = [...newValue.setIds, newValue[l].set?.id].filter(id => !!id) as string[];
            delete newValue[l];
        }
        delete newValue.features;
        emit('update:modelValue', newValue);
    }
});
const showParentTab = computed(() => props.modelValue.distribution || props.modelValue.alternate || parentCards.value.length > 0 || false);
const infoModal = ref<typeof Modal>();
const infoCard = ref<PokemonCardDTO>();
const setInfoCard = (card: PokemonCardDTO) => {
    infoCard.value = card;
    infoModal.value?.show();
};

const mergeSources = computed(() => uniqBy(concat(props.diffSource, cardsToSave.value), 'id')
    .map(c => {
        const card = mapToEditCard(c);

        for (const l of localizationCodes) {
            if (card.translations[l] && !card.translations[l]?.available) {
                delete card.translations[l];
            }
        }
        return card;
    }));

const sourceTranslations = RestComposables.useRestComposable<SourcedPokemonCardTranslationDTO[], false, false>(async () => {
    if (props.disabled) {
        return [];
    }

    const us = rest.post(`/api/cards/pokemon/translations`, {data: props.modelValue});
    const jp = rest.post(`/api/cards/pokemon/translations`, {data: props.modelValue, params: {from: 'jp'}});

    return (await Promise.all([us, jp]))
        .filter(x => !!x)
        .flatMap(x => x)
        .filter(x => !!x);
}, [], { watch: [() => props.disabled, () => props.modelValue], shallow: false });

const rules = useEditedPokemonCardRules(sourceTranslations);

const form = ref<typeof AdvancedForm>();
const goToNextTab = () => form.value?.tabControl?.goToNextTab();
const downloadJson = (card?: PokemonCardDTO) => downloadData("pokemon-card-" + getDateStr() + ".json", card || props.modelValue);

const tabConfig = computed(() => ({ parent: showParentTab.value, save: !props.disabled && savedCards.value.length > 0 }));

const resetting = ref(false);
const reset = () => {
    resetting.value = true;
    setTimeout(() => {
        resetting.value = false;
    }, 200);
};

defineExpose({ reset });
</script>
