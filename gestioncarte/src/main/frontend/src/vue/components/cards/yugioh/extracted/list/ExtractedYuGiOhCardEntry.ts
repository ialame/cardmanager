import {ExtractedYuGiOhCardDTO} from "@/types";
import {ExtractedCardEntry, useExtractedCardEntries} from "@components/extraction";
import {extractedYuGiOhCardsService} from "@components/cards/yugioh/extracted/list/service";

export type ExtractedYuGiOhCardEntry = ExtractedCardEntry<ExtractedYuGiOhCardDTO>;

export const useExtractedYuGiOhCardEntries = () => {
    const { entries, setEntry, ignoreCard } = useExtractedCardEntries(extractedYuGiOhCardsService);

    return { entries, setEntry, ignoreCard };
}
