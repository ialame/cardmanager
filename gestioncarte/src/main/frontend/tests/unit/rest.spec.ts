import rest from "@/rest";
import {beforeEach, describe, expect, it, vi} from 'vitest';
import createMocker from "vitest-fetch-mock";

const fetchMock = createMocker(vi);
window.location.href = "http://localhost/";

describe('rest', () => {
    beforeEach(() => {
        fetchMock.enableMocks();
    })
    it('call with url and method', async () => {
        fetchMock.once(JSON.stringify({foo: "bar"}));

        const result = await rest.get('test');

        expect(fetchMock.mock.calls.length).toBe(1);
        expect(fetchMock.mock.calls[0][0]).toBe('http://localhost/test');
        expect(result).toStrictEqual({foo: "bar"});
    });
});
