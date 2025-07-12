import createMocker from "vitest-fetch-mock";
import {RestComposables} from "@/vue/composables/RestComposables";
import {beforeEach, describe, expect, it, vi} from 'vitest';

const fetchMock = createMocker(vi);
window.location.href = "http://localhost/";

describe('RestComposables.useRestComposable', () => {
    beforeEach(() => {
        fetchMock.enableMocks();
    })
    it('call with url and method', async () => {
        fetchMock.once(JSON.stringify({foo: "bar"}));
        const test = RestComposables.useRestComposable("test", {foo: "bar"});

        expect(fetchMock.mock.calls.length).toBe(1);
        expect(fetchMock.mock.calls[0][0]).toBe('http://localhost/test');
        expect(test.value).toStrictEqual({foo: "bar"});
    });
});
