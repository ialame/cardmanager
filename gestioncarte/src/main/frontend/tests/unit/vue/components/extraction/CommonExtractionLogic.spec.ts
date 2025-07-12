import {sortByNumber} from "@components/extraction";
import {describe, expect, it} from 'vitest';

describe('sortByNumber', () => {
    it('should sort by number with normal numbers', () => {
        const numbers = ["3", "1", "2"];
        const sortedNumbers = numbers.sort(sortByNumber);

        expect(sortedNumbers).toEqual(["1", "2", "3"]);
    });
    it('should sort by number with numbers with 1 leading zeros', () => {
        const numbers = ["03", "01", "02"];
        const sortedNumbers = numbers.sort(sortByNumber);

        expect(sortedNumbers).toEqual(["01", "02", "03"]);
    });
    it('should sort by number with numbers with 2 leading zeros', () => {
        const numbers = ["003", "001", "002"];
        const sortedNumbers = numbers.sort(sortByNumber);

        expect(sortedNumbers).toEqual(["001", "002", "003"]);
    });
    it('should sort by number with numbers with 2 leading zeros and a slas total', () => {
        const numbers = ["003/100", "001/100", "002/100"];
        const sortedNumbers = numbers.sort(sortByNumber);

        expect(sortedNumbers).toEqual(["001/100", "002/100", "003/100"]);
    });
    it('should sort by number with numbers with  leading text', () => {
        const numbers = ["SWSH003", "SWSH001", "SWSH002"];
        const sortedNumbers = numbers.sort(sortByNumber);

        expect(sortedNumbers).toEqual(["SWSH001", "SWSH002", "SWSH003"]);
    });
});
