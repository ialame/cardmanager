import {Path} from "@/path";
import {ValidationResultMap} from "@/validation";
import {cloneDeep, isEmpty, isEqual} from "lodash";
import {shallowRef, watch} from "vue";
import log from "loglevel";
import {Service} from "@/types";
import {isReactive, triggerRef} from "vue-demi";
import {useRaise} from "@/alert";

export type ExtractedCardEntry<T> = {
    extractedCard: T;
    optionalPaths: Path[];
    reviewedPaths: Path[];
    validationResults: ValidationResultMap;
    status: string[];
}

export const createExtractedCardEntry = <T>(c: T): ExtractedCardEntry<T> => ({
    extractedCard: cloneDeep(c),
    optionalPaths: [],
    reviewedPaths: [],
    validationResults: {},
    status: [],
});

export const useExtractedCardEntries = <T extends object, Id>(service: Service<T, Id>, validate?: (e: ExtractedCardEntry<T>) => Promise<void>) => {
    const entries = shallowRef<ExtractedCardEntry<T>[]>([]);
    const raise = useRaise();

    const validateEntries = async () => {
        if (validate) {
            for (const entry of entries.value) {
                await validate(entry);
            }
        }
        triggerRef(entries);
    }
    const setEntry = async (entry: ExtractedCardEntry<T>) => {
        entry = isReactive(entry) ? cloneDeep(entry) : entry;

        const entryId = service.getId(entry.extractedCard);

        if (entryId) {
            const index = entries.value.findIndex(e => service.getId(e.extractedCard) === entryId);

            if (index >= 0) {
                if (!isEqual(entry.extractedCard, entries.value[index].extractedCard)) {
                    entry.extractedCard = cloneDeep(await service.save(entry.extractedCard));
                }
                entries.value[index] = entry;
            } else {
                throw new Error("Entry not found");
            }
        } else {
            entry.extractedCard = cloneDeep(await service.save(entry.extractedCard));
        }
        await validateEntries();
        raise.success("Modification enregistrée");
    }
    const ignoreCard = async (entry: ExtractedCardEntry<T>) => {
        const entryId = service.getId(entry.extractedCard);
        const old = entries.value.find(e => service.getId(e.extractedCard) === entryId);
        const oldId = service.getId(old?.extractedCard);

        if (old && oldId) {
            await service.delete(oldId);
        }
        entries.value = entries.value.filter(e => service.getId(e.extractedCard) !== entryId);
        await validateEntries();
        raise.success("Cartes effacées de l'extraction");
    }
    watch(service.all, async cards => {
        if (isEmpty(cards)) {
            entries.value = [];
            return;
        }

        entries.value = await Promise.all(cards.map(async c => {
            let entry = entries.value.find(e => service.getId(e.extractedCard) === service.getId(c));

            if (!entry) {
                entry = createExtractedCardEntry(c);
            } else {
                entry = cloneDeep(entry);
                entry.extractedCard = cloneDeep(c);
            }
            validate && await validate(entry);
            return entry;
        }));
        log.debug("Extracted card entries updated");
    }, {immediate: true});
    return { entries, setEntry, ignoreCard };
}
