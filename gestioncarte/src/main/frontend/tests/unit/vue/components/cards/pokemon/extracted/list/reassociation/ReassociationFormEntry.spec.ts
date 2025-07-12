import ReassociationFormEntry from "@components/cards/pokemon/extracted/list/reassociation/ReassociationFormEntry.vue";
import {ComponentTestHelper} from "@tu/helpers";
import {CardGroupTestHelper} from "../CardGroupTestHelper";
import {describe} from 'vitest';

describe('ReassociationFormEntry', () => {
    ComponentTestHelper.exists(ReassociationFormEntry, { props: { 'entry': CardGroupTestHelper.mockCardGroup(1, 1)}});
});
