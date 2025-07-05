import {ExtractedOnePieceCardDTO} from "@/types";
import {ExtractedCardEntry, useExtractedCardEntries} from "@components/extraction";
import {extractedOnePieceCardsService} from "@components/cards/onepiece/extraction/list/service";
import {getLegendKeys, getValidationStatus, validate} from "@/validation";
import {rules} from "@components/cards/onepiece/edit/validation";
import {LocalizationCode} from "@/localization";
import {ComposablesHelper} from "@/vue/composables/ComposablesHelper";
import {WorkflowComposables} from "@/vue/composables/WorkflowComposables";

export type ExtractedOnePieceCardEntry = ExtractedCardEntry<ExtractedOnePieceCardDTO>;

function isNewSet(entry: ExtractedOnePieceCardEntry, localization: LocalizationCode) {
    return entry.extractedCard.card.translations?.[localization]?.available && entry.extractedCard.savedCards.every(c => !c.translations?.[localization]?.available);
}

export const processEntryStatus = (entry: ExtractedOnePieceCardEntry) => {
    const status = new Set<string>();

    getLegendKeys(entry.validationResults).forEach(key => status.add(key));

    if (entry.extractedCard.savedCards.length === 0) {
        status.add('new');
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

export const useExtractedOnePieceCardEntries = () => {
    const doValidateEntry = async (entry: ExtractedOnePieceCardEntry) => {
        entry.validationResults = await validate(rules, entry.extractedCard.card, entry.extractedCard.savedCards);
        processEntryStatus(entry);
    }

    const { entries, setEntry, ignoreCard } = useExtractedCardEntries(extractedOnePieceCardsService, doValidateEntry);
    const editCardWorkflow = ComposablesHelper.unwrap(WorkflowComposables.useWorkflow(entries, {isValid: c => !c.status.includes('invalid') }), 'next', 'isValid', 'reset');

    return { entries, setEntry, ignoreCard, editCardWorkflow };
}
