import {MaybeRefOrGetter} from "@vueuse/core";
import {isRef, triggerRef} from "vue-demi";
import {ExtractedCardEntry} from "@components/extraction/ExtractedCardEntry";
import {useProvideSelection} from "@/selection";

export const useSelectedCards = <T, Id>(entries: MaybeRefOrGetter<ExtractedCardEntry<T>[]>, idGetter: (t: T) => Id, ignoreCard: (entry: ExtractedCardEntry<T>) => Promise<void>) => {
    const { selected, isSelected, select, revert } = useProvideSelection<ExtractedCardEntry<T>, Id>(entries, e => idGetter(e.extractedCard));

    const removeSelected = () => Promise.all(selected.value
        .map(e => ignoreCard(e)))
        .then(() => {
            if (isRef(entries)) {
                triggerRef(entries);
            }
        });

    const selectAll = (entryList: ExtractedCardEntry<T>[], s: boolean) => entryList.forEach(e => select(e, s))

    return { selected, isSelected, select, revert, removeSelected, selectAll };
}
