import {getPageTitle, usePageTitle, useSideBarTabs} from "@/vue/composables/PageComposables";
import {nextTick} from "vue";
import {mount} from "@vue/test-utils";
import {useRoute} from "vue-router";
import {reactive} from "vue-demi";
import {describe, expect, it, Mock, vi} from 'vitest';

describe('getPageTitle', () => {
    it('set the default title', async () => {
        const title = getPageTitle();

        expect(title.value).toEqual("");
        expect(document.title).toEqual("Professional Card Retriever");
    });
    it('set a new title', async () => {
        const title = getPageTitle();

        title.value = "test";
        await nextTick();
        expect(title.value).toEqual("test");
        expect(document.title).toEqual("Professional Card Retriever - test");
    });
});
describe('usePageTitle', () => {
    it('set the initial title', async () => {
        mount({
            setup: () => {
                const title = usePageTitle("test");

                expect(title.value).toEqual("test");
                expect(document.title).toEqual("Professional Card Retriever - test");
            }
        });
    });
});

vi.mock('vue-router', () => ({
    useRoute: vi.fn(),
    useRouter: vi.fn(() => ({
        push: () => {}
    }))
}));
describe('useSideBarTabs', () => {
    const route = reactive({ path: "/test" });

    (useRoute as Mock).mockImplementation(() => route) as any;
    it('is empty', async () => {
        const tcg = useSideBarTabs();

        expect(tcg.value).toEqual("");
    });
    it.each([
        ["/cards/pokemon", "pokemon"],
        ["/cards/magic", "magic"],
    ])('works with %s and returns %s', async (p, e) => {
        const tcg = useSideBarTabs();

        route.path = p;
        await nextTick();
        expect(tcg.value).toEqual(e);
    });

});
