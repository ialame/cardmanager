import {describe, expect, it} from "vitest";
import {merge} from "@/merge";

describe('merge', () => {
    it('merge', () => {
        const result = merge([{a: 1, b: 2}, {a: 1, b: 2, c: 3}]);

        expect(result).toStrictEqual({a: 1, b: 2, c: 3});
    });
});
