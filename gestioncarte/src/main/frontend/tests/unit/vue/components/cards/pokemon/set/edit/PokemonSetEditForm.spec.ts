import {PokemonSetEditForm} from "@components/cards/pokemon/set";
import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import {PokemonSetService} from "@/services/card/pokemon/set/PokemonSetService";
import {describe} from 'vitest';
import newSet = PokemonSetService.newSet;

describe('PokemonSetEditForm', () => {
    ComponentTestHelper.exists(PokemonSetEditForm, { props: {
        modelValue: newSet(),
    }});
});
