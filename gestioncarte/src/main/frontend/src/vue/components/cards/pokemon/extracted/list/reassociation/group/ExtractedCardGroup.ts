import {ExtractedCardEntry} from "@components/extraction";
import {ExtractedPokemonCardDTO} from "@/types";

export type ExtractedCardGroup = {
    entries: ExtractedCardEntry<ExtractedPokemonCardDTO>[],
    reviewed: boolean;
};
