import {ExtractedPokemonCardDTO, PokemonCardDTO, PokemonSetDTO} from "@/types";
import {LocalizationCode} from "@/localization";
import {ExtractedCardEntry} from "@components/extraction";
import {intersection} from "lodash";
import log from "loglevel";
import {ExtractedCardGroup} from "@components/cards/pokemon/extracted/list/reassociation/group/ExtractedCardGroup";

const areCardsInSameLocalizedGroup = (c1: PokemonCardDTO, c2: PokemonCardDTO, s: PokemonSetDTO[], l: LocalizationCode): boolean => {
    return c1.translations[l]?.number !== undefined && c1.translations[l]?.number === c2.translations[l]?.number && c1.translations[l]?.name !== undefined && c1.translations[l]?.name === c2.translations[l]?.name && (s.filter(set => set?.translations?.[l] !== undefined).length > 0 || (l === 'jp' && c1.translations?.jp?.number === '—'));
}

const areCardsInSameGroup = (card1: PokemonCardDTO, card2: PokemonCardDTO, sets: PokemonSetDTO[]): boolean => {
    const intersectingSets = intersection(card1.setIds, card2.setIds)
        .map(i => sets.find(s => s.id === i))
        .filter(i => i !== undefined) as PokemonSetDTO[];

    return areCardsInSameLocalizedGroup(card1, card2, intersectingSets, "us") || areCardsInSameLocalizedGroup(card1, card2, intersectingSets, "jp");
}

export function findGroup(card: ExtractedCardEntry<ExtractedPokemonCardDTO>, groups: ExtractedCardGroup[], sets: PokemonSetDTO[]): ExtractedCardGroup | undefined {
    return groups.find(g => g.entries.some(c2 => areCardsInSameGroup(card.extractedCard.card, c2.extractedCard.card, sets)));
}

function isStaff(card: PokemonCardDTO) {
    return card.brackets.some(b => b.name.toLowerCase() === 'staff') || card.promos.some(p => p.name.toLowerCase().includes('staff'));
}


export function buildCardGroups(entries: ExtractedCardEntry<ExtractedPokemonCardDTO>[], sets: PokemonSetDTO[]): ExtractedCardGroup[] {
    const groupsBuilder: ExtractedCardGroup[] = [];

    for (const card of entries) {
        if (isStaff(card.extractedCard.card) || (card.extractedCard.card.distribution && !card.extractedCard.card.alternate && card.extractedCard.card.translations.us?.available && !card.extractedCard.card.translations.jp?.available)) {
            continue;
        }

        let group = findGroup(card, groupsBuilder, sets);

        if (!group) {
            group = {
                entries: [],
                reviewed: false
            };
            groupsBuilder.push(group);
        }
        group.entries.push(card);
    }
    log.debug("Card groups build", groupsBuilder);
    return groupsBuilder.filter(g => {
        if (g.entries.length <= 1) {
            return false;
        }

        const locs = g.entries.flatMap(e => Object.keys(e.extractedCard.card.translations));

        return locs.length > 1 && locs.includes("us") && locs.includes("jp");
    });
}
