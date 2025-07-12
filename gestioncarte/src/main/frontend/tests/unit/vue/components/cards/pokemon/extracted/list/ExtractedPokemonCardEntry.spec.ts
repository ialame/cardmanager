import snorlaxGXExtracted from "@tr/card/pokemon/snorlax-GX-extracted.json";
import {
    ExtractedPokemonCardEntry,
    extractedPokemonCardsService,
    useExtractedPokemonCardEntries
} from "@components/cards/pokemon/extracted/list";
import snorlaxGXDb from "@tr/card/pokemon/snorlax-GX-db.json";
import {cloneDeep, isObject} from "lodash";
import {ExtractedPokemonCardDTO} from "@/types";
import {mockServiceCalls} from "@tu/types/ServiceTestHelper";
import {watchOnce} from "@vueuse/core";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {TestHelper} from "@tu/TestHelper";
import {describe, expect, it} from 'vitest';
import {pokemonCardTranslatorService} from "@components/cards/pokemon/translators";
import pokemonSetService = PokemonComposables.pokemonSetService;
import pokemonFeatureService = PokemonComposables.pokemonFeatureService;
import nextTrigger = TestHelper.nextTrigger;

function mockExtraction(extraction: ExtractedPokemonCardDTO[]) {
    mockServiceCalls(pokemonSetService, []);
    mockServiceCalls(pokemonFeatureService, []);
    mockServiceCalls(extractedPokemonCardsService, extraction);
    mockServiceCalls(pokemonCardTranslatorService, []);
}

type TestCallback = (entries: ExtractedPokemonCardEntry[]) => Promise<void> | void;
function withExtraction(extraction: ExtractedPokemonCardDTO[] | string, test: (entries: ExtractedPokemonCardEntry[]) => Promise<void> | void): TestCallback {
    return () => new Promise<void>(resolve => {
        const e = typeof extraction === 'string' ? import(`@tr/card/pokemon/extracted/extracted-pokemon-cards-${extraction}.json`) : Promise.resolve(extraction);

        e.then(extraction => {
            if (isObject(extraction) && 'default' in extraction) {
                extraction = extraction.default;
            }
            mockExtraction(extraction);
            const {entries} = useExtractedPokemonCardEntries();

            watchOnce(entries, e => {
                const result = test(e);

                if (result instanceof Promise) {
                    result.then(resolve);
                } else {
                    resolve();
                }
            });
        });
    });
}

describe('useExtractedPokemonCardEntries', () => {
    it('should return empty array if no cards', withExtraction([], entries => expect(entries).toEqual([])));
    it('should have cards', withExtraction([
        {
            id: '1',
            reviewed: false,
            bulbapediaStatus: 'ok',
            savedCards: [],
            translations: [],
            parentCards: [],
            rawExtractedCard: snorlaxGXExtracted as any,
            card: snorlaxGXExtracted as any
        },
    ], entries => {
        expect(entries.length).toEqual(1);
        expect(entries[0].extractedCard).toEqual(extractedPokemonCardsService.all.value[0]);
    }));
    it('should all be duplicate', withExtraction([
        {
            id: '1',
            reviewed: false,
            bulbapediaStatus: 'ok',
            savedCards: [snorlaxGXDb as any],
            translations: [],
            parentCards: [],
            rawExtractedCard: snorlaxGXExtracted as any,
            card: snorlaxGXExtracted as any
        },
        {
            id: '2',
            reviewed: false,
            bulbapediaStatus: 'ok',
            savedCards: [snorlaxGXDb as any],
            translations: [],
            parentCards: [],
            rawExtractedCard: snorlaxGXExtracted as any,
            card: snorlaxGXExtracted as any
        },
    ], entries => {
        expect(entries.length).toEqual(2);
        expect(entries.every(entry => entry.status.includes('duplicate'))).toBeTruthy();
    }));
    it('should add new entry', async () => {
        mockExtraction([
            {
                id: '1',
                reviewed: false,
                bulbapediaStatus: 'ok',
                savedCards: [snorlaxGXDb as any],
                translations: [],
                parentCards: [],
                rawExtractedCard: snorlaxGXExtracted as any,
                card: snorlaxGXExtracted as any
            }
        ]);
        const {entries, setEntry} = useExtractedPokemonCardEntries();

        await nextTrigger(entries);
        expect(entries.value.length).toEqual(1);

        await setEntry({
            extractedCard: {
                id: '',
                reviewed: false,
                bulbapediaStatus: 'ok',
                savedCards: [snorlaxGXDb as any],
                translations: [],
                parentCards: [],
                rawExtractedCard: snorlaxGXExtracted as any,
                card: snorlaxGXExtracted as any
            },
            optionalPaths: [],
            reviewedPaths: [],
            validationResults: {},
            status: []
        });

        expect(extractedPokemonCardsService.all.value.length).toEqual(2);

        await nextTrigger(entries);
        expect(entries.value.length).toEqual(2);
    });
    it('should modify existing entry', async () => {
        mockExtraction([
            {
                id: '1',
                reviewed: false,
                bulbapediaStatus: 'ok',
                savedCards: [snorlaxGXDb as any],
                translations: [],
                parentCards: [],
                rawExtractedCard: snorlaxGXExtracted as any,
                card: snorlaxGXExtracted as any
            }
        ]);
        const {entries, setEntry} = useExtractedPokemonCardEntries();

        await nextTrigger(entries);
        expect(entries.value.length).toEqual(1);

        const entry = cloneDeep(entries.value[0]);

        entry.extractedCard.card.type = 'test';
        await setEntry(entry);

        expect(extractedPokemonCardsService.all.value.length).toEqual(1);
        expect(entries.value.length).toEqual(1);
        expect(entries.value[0].extractedCard.card.type).toEqual('test');
        expect(extractedPokemonCardsService.all.value[0].card.type).toEqual('test');
    });
    it('should remove entry', async () => {
        mockExtraction([
            {
                id: '1',
                reviewed: false,
                bulbapediaStatus: 'ok',
                savedCards: [snorlaxGXDb as any],
                translations: [],
                parentCards: [],
                rawExtractedCard: snorlaxGXExtracted as any,
                card: snorlaxGXExtracted as any
            },
            {
                id: '2',
                reviewed: false,
                bulbapediaStatus: 'ok',
                savedCards: [snorlaxGXDb as any],
                translations: [],
                parentCards: [],
                rawExtractedCard: snorlaxGXExtracted as any,
                card: snorlaxGXExtracted as any
            }
        ]);
        const {entries, ignoreCard} = useExtractedPokemonCardEntries();

        await nextTrigger(entries);
        expect(entries.value.length).toEqual(2);

        await ignoreCard(entries.value[0]);

        expect(extractedPokemonCardsService.all.value.length).toEqual(1);
        expect(entries.value.length).toEqual(1);
    });
    it('should reset extraction', async () => {
        mockExtraction([
            {
                id: '1',
                reviewed: false,
                bulbapediaStatus: 'ok',
                savedCards: [snorlaxGXDb as any],
                translations: [],
                parentCards: [],
                rawExtractedCard: cloneDeep(snorlaxGXExtracted) as any,
                card: cloneDeep(snorlaxGXExtracted) as any
            }
        ]);
        const {entries, setEntry, resetCard} = useExtractedPokemonCardEntries();

        await nextTrigger(entries);
        expect(entries.value.length).toEqual(1);

        const entry = cloneDeep(entries.value[0]);

        entry.extractedCard.card.type = 'test';
        await setEntry(entry);

        expect(extractedPokemonCardsService.all.value.length).toEqual(1);
        expect(entries.value.length).toEqual(1);
        expect(entries.value[0].extractedCard.card.type).toEqual('test');
        expect(extractedPokemonCardsService.all.value[0].card.type).toEqual('test');

        await resetCard(entries.value[0]);

        expect(entries.value[0].extractedCard.card.type).toEqual('Colorless');
        expect(extractedPokemonCardsService.all.value[0].card.type).toEqual('Colorless');
    });
    it('2022129231126', withExtraction('2022129231126', entries => {
        expect(entries.length).toEqual(3);
        expect(entries[0].extractedCard.card.translations.jp?.name).toEqual('Rapidash');
        expect(entries[1].extractedCard.card.translations.jp?.name).toEqual('Celebi');
        expect(entries[2].extractedCard.card.translations.jp?.name).toEqual('Ho-Oh');
    }));
    it('20221220152244', withExtraction('20221220152244', entries => {
        expect(entries.length).toEqual(1);
        expect(entries[0].extractedCard.card.translations.jp?.name).toEqual('Pikachu');
        expect(entries[0].status).toEqual(expect.arrayContaining(['invalid']));
    }));
    it('2022122215421', withExtraction('2022122215421', entries => {
        expect(entries.length).toEqual(1);
        expect(entries[0].extractedCard.card.translations.jp?.name).toEqual('Lugia');
        expect(entries[0].status).not.toEqual(expect.arrayContaining(['invalid']));
        expect(entries[0].status).not.toEqual(expect.arrayContaining(['breaking-change']));
    }));
    it('2022122375924', withExtraction('2022122375924', entries => {
        expect(entries.length).toEqual(1);
        expect(entries[0].extractedCard.card.translations.jp?.name).toEqual('Lugia');
        expect(entries[0].status).toEqual(expect.arrayContaining(['invalid']));
        expect(entries[0].status).toEqual(expect.arrayContaining(['breaking-change']));
    }));
});
