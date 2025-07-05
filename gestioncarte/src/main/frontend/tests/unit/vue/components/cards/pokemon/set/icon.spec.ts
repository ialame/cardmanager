import {getSetIcon, getSetIconLink} from "@components/cards/pokemon/set";
import {beforeEach, describe, expect, it, vi} from 'vitest';
import createMocker from "vitest-fetch-mock";

const fetchMock = createMocker(vi);
window.location.href = "http://localhost/";

describe('getSetIconLink', () => {
   it.each([
         ["base1", "/images/symbole/pokemon/noir/base1.png"],
         [{shortName: "base1"}, "/images/symbole/pokemon/noir/base1.png"],
   ])('should return the correct link for %s', (s: any, link: string) => {
       expect(getSetIconLink(s)).toBe(link);
   });
   it('should return empty with undefined', () => {
       expect(getSetIconLink(undefined)).toBe("");
   });
   it('should return empty with empty', () => {
       expect(getSetIconLink('')).toBe("");
   });
   it('should return empty with set without short name', () => {
       expect(getSetIconLink({})).toBe("");
   });
});


describe('getSetIcon', () => {
    beforeEach(() => {
        fetchMock.enableMocks();
        fetchMock.mockResponse(async r => {
            if (r.url.includes('/images/symbole/pokemon/noir/base1.png')) {
                return 'base1';
            }
            return {status: 404};
        });
    });
    it.each([
        ["base1", "YmFzZTE="],
        [{shortName: "base1"}, "YmFzZTE="],
    ])('should return the correct image for %s', async (s: any, base64Image: string) => {
        expect(await getSetIcon(s)).toEqual({base64Image, source: 'saved'});
    });
    it('should return undefined with undefined', async () => {
        expect(await getSetIcon(undefined)).toBe(undefined);
    });
    it('should return undefined with empty', async () => {
        expect(await getSetIcon('')).toBe(undefined);
    });
    it('should return undefined with set without short name', async () => {
        expect(await getSetIcon({})).toBe(undefined);
    });
    it('should return undefined with set without saved icon', async () => {
        expect(await getSetIcon({shortName: 'unknown'})).toBe(undefined);
    });
});
