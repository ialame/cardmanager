<template>
    <ListTable :cards="cards" @openCard="(c, i) => $emit('editCard', modelValue[i])" :memo="memo" :idGetter="(c, i) => props.modelValue[i].extractedCard.id">
        <template #beforeHeader>
            <th scope="col"></th>
            <th scope="col" class="min-w-100">Status</th>
        </template>
        <template #afterHeader>
            <th scope="col" class="min-w-150"></th>
        </template>
        <template #beforeEntry="{index}">
            <td>
                <SelectionCheckbox :entry="modelValue[index]" />
            </td>
            <td>
                <ExtractedCardStatus :entry="modelValue[index]" class="float-start" />
            </td>
        </template>
        <template #afterEntry="{card, index}">
            <td>
                <div class="float-end">
                    <FormButton v-if="findGroup(index) > -1" color="link" title="Reassocier" @click="openReassociation(index)" class="me-2 p-0 no-focus">
                        <Icon name="git-compare-outline" />
                    </FormButton>
                    <FormButton color="link" title="Reset" @click="$emit('resetCard', modelValue[index])" class="me-2 p-0 no-focus">
                        <Icon name="refresh-outline" />
                    </FormButton>
                    <a v-if="card.link" title="Lien bulbapedia" :href="card.link" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                        <BulbapediaIcon />
                    </a>
                    <FormButton color="link" title="Enlever la carte" @click="$emit('removeCard', modelValue[index])" class="me-2 p-0 no-focus">
                        <Icon name="trash" class="text-danger" />
                    </FormButton>
                </div>
            </td>
        </template>
        <template #localization="{card, index, localization}">
            <TranslationSourceListDropdown :localization="localization" :card="card" :sourceTranslations="modelValue[index].extractedCard.translations" @submit="(k, t) => setTranslation(modelValue[index], localization, k, t)" />
        </template>
    </ListTable>
</template>

<script lang="ts" setup>
import {PokemonCardDTO, PokemonCardTranslationDTO} from '@/types';
import FormButton from '@components/form/FormButton.vue';
import {computed} from 'vue';
import {ExtractedCardGroup, NameKey} from './logic';
import {
    ExtractedCardStatus,
    ExtractedPokemonCardEntry,
    TranslationSourceListDropdown
} from "@components/cards/pokemon/extracted/list";
import {cloneDeep} from "lodash";
import BulbapediaIcon from "@components/cards/pokemon/BulbapediaIcon.vue";
import Icon from "@components/Icon.vue";
import ListTable from "@components/cards/pokemon/ListTable.vue";
import {LocalizationCode} from "@/localization";
import {SelectionCheckbox} from "@/selection";

interface Props {
    modelValue: ExtractedPokemonCardEntry[];
    groups: ExtractedCardGroup[];
}

interface Emit {
	(e: 'editCard', entry: ExtractedPokemonCardEntry): void;
	(e: 'removeCard', entry: ExtractedPokemonCardEntry): void;
	(e: 'resetCard', entry: ExtractedPokemonCardEntry): void;
    (e: 'update:entry', entry: ExtractedPokemonCardEntry): void;
    (e: 'openReassociation', group: number): void;
}
const props = defineProps<Props>();
const emit = defineEmits<Emit>();

const cards = computed(() => props.modelValue.map(e => e.extractedCard.card));

const memo = (c: PokemonCardDTO, i: number) => cloneDeep(props.modelValue[i].status)
    .sort((a, b) => a.localeCompare(b))
    .join(',');

const setTranslation = (entry: ExtractedPokemonCardEntry, localization: LocalizationCode, key: NameKey, translation: string) => {
    if (entry.extractedCard.card.translations[localization] !== undefined) {
        const copy = cloneDeep(entry);

        (copy.extractedCard.card.translations[localization] as PokemonCardTranslationDTO)[key] = translation;
        emit('update:entry', copy);
    }
};

const findGroup = (index: number) => props.groups.findIndex(g => g.entries.some(e => e.extractedCard.id === props.modelValue[index].extractedCard.id));
const openReassociation = (index: number) => {
    const g = findGroup(index);

    if (g > -1 && g < props.groups.length) {
        emit('openReassociation', g);
    }
}
</script>
