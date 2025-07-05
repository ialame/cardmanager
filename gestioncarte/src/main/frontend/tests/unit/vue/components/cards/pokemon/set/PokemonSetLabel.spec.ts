import {PokemonSetLabel} from '@/vue/components/cards/pokemon/set';
import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import {describe} from 'vitest';

describe('PokemonSetLabel', () => {
   ComponentTestHelper.exists(PokemonSetLabel);
});
