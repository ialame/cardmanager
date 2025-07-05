import {PokemonCardEditForm} from '@components/cards/pokemon/edit';
import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import {PokemonCardService} from "@/services/card/pokemon/PokemonCardService";
import {beforeEach, describe, vi} from 'vitest';
import createMocker from "vitest-fetch-mock";

const fetchMock = createMocker(vi);
window.location.href = "http://localhost/";

describe('PokemonCardEditForm', () => {
    beforeEach(() => {
        fetchMock.enableMocks();
    });
    ComponentTestHelper.exists(PokemonCardEditForm, {
        props: { modelValue: PokemonCardService.newPokemonCard() }
    });
});
