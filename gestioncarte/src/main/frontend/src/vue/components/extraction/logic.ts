import {LegendType} from "@/types";

export type ExtractionStatusLegendType = LegendType & { key: string };

export const sortByNumber = (s1: string, s2: string) => {
    const n1 = parseInt(s1.split('/')?.[0] || '0');
    const n2 = parseInt(s2.split('/')?.[0] || '0');

    if (n1 > 0 && !isNaN(n1) && n2 > 0 && !isNaN(n2)) {
        return n1 - n2;
    }
    return s1.localeCompare(s2);
}
