import {ClearExtractedCards} from "@components/cards/pokemon/extracted/list";
import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import {beforeAll, describe, vi} from 'vitest';
import createMocker from "vitest-fetch-mock";

const fetchMock = createMocker(vi);
window.location.href = "http://localhost/";

describe("ClearExtractedCards", () => {
    beforeAll(() => {
        fetchMock.enableMocks();
    });
    ComponentTestHelper.exists(ClearExtractedCards);
});
