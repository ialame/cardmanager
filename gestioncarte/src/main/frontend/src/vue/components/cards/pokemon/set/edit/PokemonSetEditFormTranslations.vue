<template>
    <AdvancedFormTranslations path="translations" label="Translations" :localizations="localizations" @update:localizations="l => $emit('update:localizations', l)" :fixedLocalizations="isParent" class="mt-2" #default="{ path, localization }">
        <FormRow>
            <AdvancedFormInput :path="concatPaths([path, 'name'])" label="Nom" />
        </FormRow>
        <FormRow v-if="allLocalizations[localization].hasOriginalName">
            <AdvancedFormInput :path="concatPaths([path, 'originalName'])" label="Nom en caractère kanji (日本語)"  />
        </FormRow>
        <FormRow v-if="!isParent">
            <AdvancedFormInput label="Date de sortie" :path="concatPaths([path, 'releaseDate'])" type="date" min="1990-01-01" :max="CommonSetEditLogic.maxDate" />
        </FormRow>
        <template v-if="!isParent && (localization === 'fr' || localization === 'de' || localization === 'es' || localization === 'it' || localization === 'jp')">
            <WikiUrlInput v-if="localization === 'fr'" path="wikiUrls.fr" label="pokepedia.fr" href="https://www.pokepedia.fr/S%C3%A9rie_(JCC)" />
            <WikiUrlInput v-else-if="localization === 'de'" path="wikiUrls.de" label="pokewiki.de" href="https://www.pokewiki.de/Erweiterung" />
            <WikiUrlInput v-else-if="localization === 'es'" path="wikiUrls.es" label="wikidex.net" href="https://www.wikidex.net/wiki/Lista_de_expansiones_del_Juego_de_Cartas_Coleccionables_Pok%C3%A9mon" />
            <WikiUrlInput v-else-if="localization === 'it'" path="wikiUrls.it" label="pokemoncentral.it" href="https://wiki.pokemoncentral.it/Gioco_di_Carte_Collezionabili_Pok%C3%A9mon" />
            <FormRow v-else-if="localization === 'jp'">
                <AdvancedFormListInput path="japaneseOfficialSitePgs" initialValue="">
                    <template #label>
                        Numéro de <span class="fw-bold">PG</span> de l'extension sur le <a target="_blank" href="https://www.pokemon-card.com/card-search/index.php?keyword=&se_ta=&regulation_sidebar_form=all&pg=&illust=&sm_and_keyword=true" rel="noopener">site officiel Japonais</a> ou <span class="fw-bold">lien de la page après recherche</span> :
                    </template>
                    <template #default="{path: p}">
                        <AdvancedFormInput :path="p" />
                    </template>
                </AdvancedFormListInput>
            </FormRow>
            <FormRow v-if="localization === 'fr'">
                <AdvancedFormInput label="Code url pokecardex.com" path="pokecardexCode">
                    <template #label>
                        Code de l'extension sur&nbsp;<a target="_blank" href="https://www.pokecardex.com/" rel="noopener">pokecardex.com</a>&nbsp;ou URL de l'extension:
                    </template>
                </AdvancedFormInput>
            </FormRow>
        </template>
    </AdvancedFormTranslations>
</template>

<script lang="ts" setup>
import FormRow from "@components/form/FormRow.vue";
import {AdvancedFormInput, AdvancedFormTranslations} from "@components/form/advanced";
import {CommonSetEditLogic} from "@components/form/set/CommonSetEditLogic";
import {LocalizationCode, localizations as allLocalizations} from '@/localization';
import {concatPaths} from "@/path";
import WikiUrlInput from "./WikiUrlInput.vue";
import AdvancedFormListInput from "@components/form/advanced/input/AdvancedFormListInput.vue";

interface Props {
    localizations?: LocalizationCode[];
    isParent?: boolean;
}

interface Emits {
    (e: 'update:localizations', args: LocalizationCode[]): void;
}

withDefaults(defineProps<Props>(), {
    localizations: () => [],
    isParent: false,
});

defineEmits<Emits>();

</script>
