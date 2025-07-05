import {ExtractedPokemonCardDTO} from "@/types";
import {getLegendKeys, getValidationStatus, validate} from "@/validation";
import {createPokemonCardRules} from "@components/cards/pokemon/edit";
import {LocalizationCode} from "@/localization";
import {ExtractedCardEntry, useExtractedCardEntries} from "@components/extraction";
import {cloneDeep, isEmpty} from "lodash";
import {extractedPokemonCardsService} from "@components/cards/pokemon/extracted/list";
import {computed} from "vue";

export type ExtractedPokemonCardEntry = ExtractedCardEntry<ExtractedPokemonCardDTO>;
export const validateEntry = async (entry: ExtractedPokemonCardEntry) => {
    entry.validationResults = await validate(createPokemonCardRules(entry.extractedCard.translations), entry.extractedCard.card, entry.extractedCard.savedCards, entry.optionalPaths, entry.reviewedPaths);
}

function isNewSet(entry: ExtractedPokemonCardEntry, localization: LocalizationCode) {
    return entry.extractedCard.card.translations?.[localization]?.available && entry.extractedCard.savedCards.every(c => !c.translations?.[localization]?.available);
}

export const processEntryStatus = (entry: ExtractedPokemonCardEntry, duplicate?: boolean) => {
    const status = new Set<string>();

    getLegendKeys(entry.validationResults).forEach(key => status.add(key));

    if (duplicate) {
        status.add('duplicate');
    }
    if (entry.extractedCard.savedCards.length === 0) {
        status.add('new');
    }
    switch (entry.extractedCard.bulbapediaStatus) {
        case 'no_page_2':
            status.add('bulbapedia-no-page-2');
            break;
        case 'not_in_page_2':
            status.add('bulbapedia-not-in-page-2');
            break;
        case 'not_in_page_1':
            status.add('bulbapedia-not-in-page-1');
            break;
        case 'no_set':
            status.add('bulbapedia-no-set');
            break;
    }
    if (Object.values(entry.validationResults).flat().some(r => getValidationStatus(r) === 'invalid')) {
        status.add('invalid');
    } else if (entry.extractedCard.reviewed) {
        status.add('reviewed');
        if (status.has('breaking-change')) {
            status.delete('breaking-change');
            if (!status.has('change')) {
                status.add('change');
            }
        }
    }
    if (status.has('breaking-change') && status.has('change')) {
        status.delete('change');
    }
    if (entry.extractedCard.savedCards.length > 1) {
        const translationKeys = entry.extractedCard.savedCards
            .map(c => Object.values(c.translations))
            .flat()
            .filter(t => t?.available)
            .map(t => t?.localization);

        if (new Set(translationKeys).size !== translationKeys.length) { // has duplicate translations
            status.add('breaking-merge');
        } else {
            status.add('merge');
        }
    }
    if (!status.has('new') && (isNewSet(entry, 'us') || isNewSet(entry, 'jp'))) {
        status.add('new-charset');
    }
    if (!status.has('invalid') && !status.has('breaking-change') && !status.has('breaking-merge') && !status.has('missing-translation') && !status.has('duplicate') && !status.has('translation-conflict') && !status.has('bulbapedia-no-set')) {
        status.add('valid');
    }
    entry.status = Array.from(status);
}

export const useExtractedPokemonCardEntries = () => {
    const doValidateEntry = async (entry: ExtractedPokemonCardEntry) => {
        if (isEmpty(entry.validationResults)) {
            await validateEntry(entry);
        }
        processEntryStatus(entry, entry.extractedCard.savedCards.some(c => duplicateIds.value.includes(c.id)));
    }

    const {entries, setEntry, ignoreCard} = useExtractedCardEntries(extractedPokemonCardsService, doValidateEntry);

    const duplicateIds = computed(() => extractedPokemonCardsService.all.value
        .flatMap(c => c.savedCards)
        .map(c => c.id)
        .filter((val, i, iteratee) => iteratee.includes(val, i + 1)));

    const resetCard = async (entry: ExtractedPokemonCardEntry) => {
        const copy = cloneDeep(entry);

        copy.extractedCard.reviewed = false;
        copy.optionalPaths = [];
        copy.reviewedPaths = [];
        copy.validationResults = {};
        copy.status = [];

        copy.extractedCard.card = cloneDeep(copy.extractedCard.rawExtractedCard);
        await setEntry(copy);
    };

    return { entries, setEntry, ignoreCard, resetCard };
}
