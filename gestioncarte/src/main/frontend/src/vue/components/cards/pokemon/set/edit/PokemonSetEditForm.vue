<template>
    <AdvancedForm v-model="value" :small="true" :rules="isParent ? rulesParent : rules">
        <div class="container">
            <FormRow v-if="!isParent">
                <PokemonSerieSelect path="serie" :values="filteredSeries" advanced />
            </FormRow>
            <FormRow class="form-group align-items-end">
                <AdvancedFormInput label="Code extension" path="shortName" />
                <AdvancedFormInput label="Numero sur" path="totalNumber" />
                <Column class="mb-2">
                    <AdvancedFormCheckbox label="Promo" path="promo" />
                </Column>
            </FormRow>
            <PokemonSetEditFormParentSetSelect :isParent="isParent" />
            <SetIconSelect />
            <PokemonSetEditFormTranslations :localizations="localizations" @update:localizations="l => $emit('update:localizations', l)" :isParent="isParent" />
            <PokemonSetEditFormExtensionsBulbapedia v-if="!isParent" />
            <hr>
            <AdvancedFormCollapse v-if="!isParent && (localizations.includes('us') || value?.translations?.us?.available)" class="card" path="sources" label="Sources de Traduction">
                <FormRow>
                    <AdvancedFormInput path="pkmncardsComSetPath">
                        <template #label>
                            URL de l'extension&nbsp;<a href="https://pkmncards.com/sets/" target="_blank" rel="noopener">pkmncards.com</a>
                        </template>
                    </AdvancedFormInput>
                </FormRow>
                <FormRow>
                    <AdvancedFormListInput path="officialSitePaths" initialValue="">
                        <template #label>
                            URL de n'importe quelle carte de cette extension sur&nbsp;<a href="https://www.pokemon.com/us/pokemon-tcg/pokemon-cards/" target="_blank" rel="noopener">le site officiel</a>
                        </template>
                        <template #default="{path: p}">
                            <AdvancedFormInput :path="p" />
                        </template>
                    </AdvancedFormListInput>
                </FormRow>
                <FormRow>
                    <AdvancedFormListInput path="ptcgoSets" label="Fichiers ptcgo">
                        <template #default="{path: p}">
                            <AdvancedFormInput :path="concatPaths([p, 'fileName'])" />
                        </template>
                    </AdvancedFormListInput>
                </FormRow>
            </AdvancedFormCollapse>
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
    AdvancedFormCollapse,
    AdvancedFormInput,
    AdvancedFormListInput,
    AdvancedFormSaveButton
} from "@components/form/advanced";
import {ExpansionBulbapediaDTO, ExtractedImageDTO, PokemonSetDTO, PtcgoSetDTO} from "@/types";
import {computed} from "vue";
import FormRow from "@components/form/FormRow.vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {cloneDeep, isNil, isString} from "lodash";
import Column from '@components/grid/Column.vue';
import {EditedPokemonSet, getUsJp, WikiUrls} from "./logic";
import SetIconSelect from "./SetIconSelect.vue";
import PokemonSetEditFormExtensionsBulbapedia from "./PokemonSetEditFormExtensionsBulbapedia.vue";
import PokemonSetEditFormTranslations from "./PokemonSetEditFormTranslations.vue";
import {concatPaths} from "@/path";
import {PokemonSerieSelect, pokemonSerieService} from "@components/cards/pokemon/serie";
import {LocalizationCode} from "@/localization";
import {rules, rulesParent} from "./validation";
import {computedAsync} from "@vueuse/core";
import PokemonSetEditFormParentSetSelect
    from "@components/cards/pokemon/set/edit/PokemonSetEditFormParentSetSelect.vue";

interface Props {
    modelValue: PokemonSetDTO;
    icon?: ExtractedImageDTO;
    expansionsBulbapedia?: ExpansionBulbapediaDTO[];
    ptcgoSets?: PtcgoSetDTO[];
    wikiUrls?: WikiUrls;
    officialSitePaths?: string[];
    pkmncardsComSetPath?: string;
    japaneseOfficialSitePgs?: string[];
    pokecardexCode?: string;
    localizations?: LocalizationCode[];
    isParent?: boolean;
}

interface Emits {
    (e: 'update:modelValue', v: PokemonSetDTO): void;
    (e: 'update:icon', v: ExtractedImageDTO): void;
    (e: 'update:expansionsBulbapedia', v: ExpansionBulbapediaDTO[]): void;
    (e: 'update:ptcgoSets', v: PtcgoSetDTO[]): void;
    (e: 'update:wikiUrls', v: WikiUrls): void;
    (e: 'update:officialSitePaths', v: string[]): void;
    (e: 'update:pkmncardsComSetPath', v: string): void;
    (e: 'update:japaneseOfficialSitePgs', v: string[]): void;
    (e: 'update:pokecardexCode', v: string): void;
    (e: 'update:localizations', args: LocalizationCode[]): void;
    (e: 'save'): void;
}

const props = withDefaults(defineProps<Props>(), {
    localizations: () => [],
    isParent: false,
});
const emit = defineEmits<Emits>();

const series = pokemonSerieService.all;
const sets = PokemonComposables.pokemonSetService.all;

const filteredSeries = computedAsync(() => pokemonSerieService.find(s => props.localizations.some(l => s.translations[l])), []);

const cleanPkmncardsComSetPath = (pkmncardsComSetPath?: string) => {
    if (isNil(pkmncardsComSetPath)) {
        return '';
    } else if (pkmncardsComSetPath.startsWith('http')) {
        const split = pkmncardsComSetPath.split('/');

        if (split.length > 7) {
            return split[6] + '/' + split[7];
        }
    }
    return pkmncardsComSetPath;
}
const cleanJapaneseOfficialSitePgs = (japaneseOfficialSitePgs: string[]) => japaneseOfficialSitePgs ? japaneseOfficialSitePgs.map(pg => {
    const matches = new RegExp(/pg=([^&]*)&/).exec(pg);

    return !isNil(matches) && matches.length > 1 && matches[1].length > 0 ? matches[1] : pg;
}) : [];
const cleanOfficialSitePaths = (officialSitePaths: string[]) => {
    return officialSitePaths.map(v => {
        if (!v || !isString(v)) {
            return '';
        } else if (v.startsWith('http')) {
            const split = v.split('/');

            if (split.length > 7) {
                return split[6] + '/' + split[7];
            }
        }
        return v;
    });
}
const cleanPokecardexCode = (pokecardexCode?: string) => {
    if (isNil(pokecardexCode)) {
        return '';
    } else if (pokecardexCode.startsWith('http')) {
        const split = pokecardexCode.split('/');

        if (split.length > 4) {
            return split[4];
        }
    }
    return pokecardexCode;
}

const value = computed<EditedPokemonSet>({
    get: () => {
        const copy = cloneDeep(props.modelValue) as any;

        copy.serie = series.value.find(s => s.id === copy.serieId) || undefined; // TODO use pokemonSerieService.find
        copy.parent = sets.value.find(s => s.id === copy.parentId) || undefined; // TODO use PokemonComposables.pokemonSetService.find
        if (copy.shortName) {
            copy.icon = props.icon;
        }
        if (!props.isParent) {
            copy.expansionsBulbapedia = {
                name: props.expansionsBulbapedia?.[0]?.name || '',
                list: cloneDeep(props.expansionsBulbapedia) || []
            };
            copy.ptcgoSets = cloneDeep(props.ptcgoSets) || [];
            copy.wikiUrls = cloneDeep(props.wikiUrls) || {};
            copy.officialSitePaths = cloneDeep(props.officialSitePaths) || [];
            copy.pkmncardsComSetPath = props.pkmncardsComSetPath;
            copy.japaneseOfficialSitePgs = props.japaneseOfficialSitePgs;
            copy.pokecardexCode = props.pokecardexCode;
        }
        delete copy.serieId;
        delete copy.parentId;
        return copy;
    },
    set: v => {
        const copy = cloneDeep(v) as any;
        const icon = copy.icon;
        const expansionsBulbapedia = cloneDeep(copy.expansionsBulbapedia?.list) || [];
        const ptcgoSets = cloneDeep(copy.ptcgoSets) || [];
        const wikiUrls = cloneDeep(copy.wikiUrls) || {};
        const officialSitePaths = cloneDeep(copy.officialSitePaths) || [];
        const pkmncardsComSetPath = copy.pkmncardsComSetPath;
        const japaneseOfficialSitePgs = copy.japaneseOfficialSitePgs;
        const pokecardexCode = copy.pokecardexCode;


        copy.serieId = copy.serie?.id || '';
        copy.parentId = copy.parent?.id || '';
        delete copy.serie;
        delete copy.parent;
        delete copy.icon;
        delete copy.expansionsBulbapedia;
        delete copy.ptcgoSets;
        delete copy.wikiUrls;
        delete copy.officialSitePaths;
        delete copy.pkmncardsComSetPath;
        delete copy.japaneseOfficialSitePgs;
        delete copy.pokecardexCode;
        emit('update:modelValue', copy);
        if (copy.shortName) {
            emit('update:icon', icon);
        }
        for (const expansion of expansionsBulbapedia) {
            expansion.localization = getUsJp(v);
            expansion.name = v.expansionsBulbapedia.name || expansion.name || '';
        }
        if (!props.isParent) {
            emit('update:expansionsBulbapedia', expansionsBulbapedia);
            emit('update:ptcgoSets', ptcgoSets);
            emit('update:wikiUrls', wikiUrls);
            emit('update:officialSitePaths', cleanOfficialSitePaths(officialSitePaths));
            emit('update:pkmncardsComSetPath', cleanPkmncardsComSetPath(pkmncardsComSetPath));
            emit('update:japaneseOfficialSitePgs', cleanJapaneseOfficialSitePgs(japaneseOfficialSitePgs));
            emit('update:pokecardexCode', cleanPokecardexCode(pokecardexCode));
        }
    },
});
</script>
