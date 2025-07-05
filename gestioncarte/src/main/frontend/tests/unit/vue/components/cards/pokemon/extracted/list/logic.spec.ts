import {ExtractedPokemonCardEntry, useCardWorkflow} from "@components/cards/pokemon/extracted/list";
import {ExtractedPokemonCardDTO, PokemonCardDTO} from "@/types";
import {PokemonCardService} from "@/services/card/pokemon/PokemonCardService";
import {createExtractedCardEntry} from "@components/extraction";
import {describe, expect, it, vi} from 'vitest';
import newPokemonCard = PokemonCardService.newPokemonCard;

const createCard = (id: string): PokemonCardDTO => {
    const card = newPokemonCard();

    card.id = id;
    return card;
}
const createExtractedCard = (id: string, ...associationsIds: string[]): ExtractedPokemonCardDTO => {
    const card = createCard(id);

    return {
        id,
        card,
        rawExtractedCard: card,
        savedCards: associationsIds.map(createCard),
        bulbapediaStatus: 'ok',
        reviewed: false,
        parentCards: [],
        translations: [],
    }
}
const createEntry = (id: string, ...associationsIds: string[]): ExtractedPokemonCardEntry => createExtractedCardEntry(createExtractedCard(id, ...associationsIds));
const createInvalidEntry = (id: string, ...associationsIds: string[]): ExtractedPokemonCardEntry => {
    const entry = createEntry(id, ...associationsIds);

    entry.status = ['invalid'];
    return entry;
}

describe('useCardWorkflow', () => {
    const cards = [
        createEntry('1'),
        createEntry('2'),
        createEntry('3'),
        createEntry('4'),
        createEntry('5')
    ] as any;
    const invalidCards = [
        createEntry('1'),
        createInvalidEntry('2'),
        createInvalidEntry('3'),
        createInvalidEntry('4'),
        createEntry('5')
    ] as any;
    it('are value initialized', () => {
        const { cardsWorkflow, editCard, setEditCard } = useCardWorkflow(cards, vi.fn(), vi.fn());

        expect(cardsWorkflow.value).not.toBeUndefined();
        expect(editCard.value).toBeUndefined();
        expect(setEditCard).toBeInstanceOf(Function);
    });
    it('should have index at undefined on setEditCard with only valid cards', () => {
        const { cardsWorkflow, setEditCard } = useCardWorkflow(cards, vi.fn(), vi.fn());

        setEditCard();
        expect(cardsWorkflow.value.index).toBe(undefined);
        expect(cardsWorkflow.value.current).toBe(undefined);
    });
    it('should have index at 4 on next with 4 as argument', () => {
        const { cardsWorkflow, setEditCard } = useCardWorkflow(cards, vi.fn(), vi.fn());

        setEditCard(cards[4]);
        expect(cardsWorkflow.value.index).toBe(4);
        expect(cardsWorkflow.value.current).toBe(cards[4]);
    });
    it('should have index at undefined after reset', () => {
        const { cardsWorkflow, setEditCard } = useCardWorkflow(cards, vi.fn(), vi.fn());

        setEditCard(cards[4]);
        cardsWorkflow.value.reset()
        expect(cardsWorkflow.value.index).toBe(undefined);
        expect(cardsWorkflow.value.current).toBe(undefined);
    });
    it('should have index at 1 on setEditCard', () => {
        const { cardsWorkflow, setEditCard } = useCardWorkflow(invalidCards, vi.fn(), vi.fn());

        setEditCard();
        expect(cardsWorkflow.value.index).toBe(1);
        expect(cardsWorkflow.value.current).toBe(invalidCards[1]);
    });
});
