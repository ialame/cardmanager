<template>
    <table class="table table-striped mt-4">
        <thead>
            <tr>
                <slot name="beforeHeader" />
                <th scope="col">Numero</th>
                <th scope="col">Nom</th>
                <th scope="col" v-if="showBrackets">Crochets</th>
                <th scope="col" v-if="showFeatures">Particularités</th>
                <th scope="col" v-if="showPromos">Promo</th>
                <th scope="col">Langues</th>
                <slot name="afterHeader" />
            </tr>
        </thead>
        <tbody>
            <tr v-for="entry in entries" :key="entry.id" v-memo="[entry.id, entry.memo]">
                <slot name="beforeEntry" :card="entry.card" :index="entry.index" />
                <td>{{ entry.number }}</td>
                <td>
                    <a class="text-dark fw-bold c-pointer" @click="$emit('openCard', entry.card, entry.index)">{{ entry.name || '!!! CARTE SANS NOM !!!' }}</a>
                    <span v-if="entry.card.level" class="small"> Lv.{{entry.card.level}}</span>
                    <span v-if="entry.originalName"> ({{ entry.originalName }})</span>
                    <IdTooltip v-if="entry.card.id" :id="entry.card.id" />
                </td>
                <td v-if="showBrackets">
                    <HoverDataList #default="{ value: bracket }" :value="entry.card.brackets">
                        <BracketLabel :bracket="bracket" />
                    </HoverDataList>
                </td>
                <td v-if="showFeatures">
                    <HoverDataList :value="entry.card.featureIds" #default="{ value: id }">{{ features.find(f => f.id === id)?.pcaName }}</HoverDataList>
                </td>
                <td v-if="showPromos">
                    <HoverDataList :value="entry.card.promos.filter(p => p.localization === mainLocalization || !Object.keys(entry.card.translations).includes(mainLocalization))" #default="{ value: promo }">{{ promo.name }}</HoverDataList>
                </td>
                <td>
                    <div class="d-flex flex-row">
                        <template v-for="localization in entry.localizations" :key="localization">
                            <slot name="localization" :localization="localization" :card="entry.card" :index="entry.index">
                                <Flag :lang="localization" class="me-1" />
                            </slot>
                        </template>
                    </div>
                </td>
                <slot name="afterEntry" :card="entry.card" :index="entry.index" />
            </tr>
        </tbody>
    </table>
</template>

<script lang="ts" setup>
import {PokemonCardDTO} from '@/types';
import HoverDataList from '@components/table/HoverDataList.vue';
import {computed} from 'vue';
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {Flag, LocalizationCode} from "@/localization";
import {sortByNumber} from "@components/extraction";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {BracketLabel} from "@components/cards/pokemon/bracket";

interface CardEntry {
    index: number;
    id: string;
    number: string;
    name: string;
    originalName?: string;
    card: PokemonCardDTO;
    localizations: LocalizationCode[];
    memo: any;
}

interface Props {
    cards: PokemonCardDTO[];
    mainLocalization: LocalizationCode;
    memo?: (c: PokemonCardDTO, i: number) => any;
    idGetter?: (c: PokemonCardDTO, i: number) => any;
}

interface Emit {
	(e: 'openCard', card: PokemonCardDTO, i: number): void;
}
const props = defineProps<Props>();
defineEmits<Emit>();

const features = PokemonComposables.pokemonFeatureService.all;
const entries = computed<CardEntry[]>(() => props.cards.map((card, index) => ({
    index,
    id: props.idGetter ? props.idGetter(card, index) : card.id,
    number: card.translations?.[props.mainLocalization]?.number || '',
    name: card.translations?.[props.mainLocalization]?.name || '',
    originalName: card.translations?.[props.mainLocalization]?.originalName || '',
    card: card,
    localizations: card.translations ? Object.keys(card.translations) as LocalizationCode[] : [],
    memo: props.memo ? props.memo(card, index) : card.id
})).sort((c1, c2) => sortByNumber(c1.number || '', c2.number || '')));
const showBrackets = computed<boolean>(() => entries.value.some(c => c.card.brackets?.length > 0));
const showFeatures = computed<boolean>(() => entries.value.some(c => c.card.featureIds?.length > 0));
const showPromos = computed<boolean>(() => entries.value.some(c => c.card.promos?.length > 0));
</script>
