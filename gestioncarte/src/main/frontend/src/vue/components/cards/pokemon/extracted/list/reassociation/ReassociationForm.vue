<template>
    <div v-if="modelValue !== undefined" class="p-0">
        <Row>
            <Column><Flag lang="us" /> Anglais</Column>
            <Column><Flag lang="jp" /> Japonais</Column>
        </Row>
        <hr>
        <ReassociationFormEntry v-for="entry in entriesWithBoth" :key="entry.extractedCard.id" :entry="entry" @reassociate="reassociate" />
        <ReassociationFormEntry v-for="entry in usEntries" :key="entry.extractedCard.id" :entry="entry" @reassociate="reassociate" />
        <ReassociationFormEntry v-for="entry in jpEntries" :key="entry.extractedCard.id" :entry="entry" @reassociate="reassociate" />
        <Row>
            <Column>
                <FormButton color="primary" title="Appliquer" @Click="$emit('apply')" class="float-end">Appliquer</FormButton>
            </Column>
        </Row>
    </div>
</template>

<script lang="ts" setup>
import {ExtractedCardGroup, ExtractedPokemonCardEntry} from '@components/cards/pokemon/extracted/list';
import {computed} from 'vue';
import ReassociationFormEntry from "./ReassociationFormEntry.vue";
import Row from "@components/grid/Row.vue";
import Column from "@components/grid/Column.vue";
import {Flag} from "@/localization";
import {cloneDeep, uniqBy} from "lodash";
import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import FormButton from "@components/form/FormButton.vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {asyncFilter} from "@/utils";
import pokemonSetService = PokemonComposables.pokemonSetService;

interface Props {
    modelValue?: ExtractedCardGroup;
}

interface Emits {
    (e: 'update:modelValue', v: ExtractedCardGroup): void;
    (e: 'apply'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();
const update = ModelComposables.useUpdateModel<ExtractedCardGroup>(() => props.modelValue, emit);

const entriesWithBoth = computed(() => props.modelValue ? props.modelValue.entries.filter(e => e.extractedCard.card.translations.us && e.extractedCard.card.translations.jp) : []);
const usEntries = computed(() => props.modelValue ? props.modelValue.entries.filter(e => e.extractedCard.card.translations.us && !e.extractedCard.card.translations.jp) : []);
const jpEntries = computed(() => props.modelValue ? props.modelValue.entries.filter(e => !e.extractedCard.card.translations.us && e.extractedCard.card.translations.jp) : []);

const reassociateEntry = async (entry: ExtractedPokemonCardEntry, other: ExtractedPokemonCardEntry) => {
    if (!props.modelValue) {
        return;
    }

    const copy = cloneDeep(entry);
    if (other.extractedCard.card.translations.jp) {
        copy.extractedCard.card.translations.jp = other.extractedCard.card.translations.jp;
    } else {
        delete copy.extractedCard.card.translations.jp;
    }
    copy.extractedCard.card.setIds = [...(await asyncFilter(entry.extractedCard.card.setIds, async i => !!(await pokemonSetService.get(i))?.translations?.us)), ...(await asyncFilter(other.extractedCard.card.setIds, async i => !!(await pokemonSetService.get(i))?.translations?.jp))];
    copy.extractedCard.card.promos = [...entry.extractedCard.card.promos.filter(p => p.localization === 'us'), ...other.extractedCard.card.promos.filter(p => p.localization === 'jp')];
    copy.extractedCard.card.brackets = [...entry.extractedCard.card.brackets.filter(p => p.localization === 'us'), ...other.extractedCard.card.brackets.filter(p => p.localization === 'jp')];
    copy.extractedCard.savedCards = uniqBy([...entry.extractedCard.savedCards, ...other.extractedCard.savedCards], 'id');
    return copy;
}

const reassociate = async (idUs: string, idJp: string) => {
    if (!props.modelValue) {
        return;
    }

    const iUs = props.modelValue.entries.findIndex(e => e.extractedCard.id === idUs);
    const iJp = props.modelValue.entries.findIndex(e => e.extractedCard.id === idJp);

    const us = props.modelValue.entries[iUs];
    const jp = props.modelValue.entries[iJp];

    if (!us || !jp) {
        return;
    }
    const newUs = await reassociateEntry(us, jp);
    const newJp = await reassociateEntry(jp, us);

    update(g => {
        if (!g) {
            return;
        }
        if (newUs) {
            g.entries[iUs] = newUs;
        }
        if (newJp) {
            g.entries[iJp] = newJp;
        }
    });
};

</script>
