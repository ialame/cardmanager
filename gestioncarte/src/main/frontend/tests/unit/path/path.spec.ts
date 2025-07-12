import {
    arePathEqual,
    comparePaths,
    concatPaths,
    getParentPath,
    Path,
    pathEndsWith,
    pathIncludes,
    pathStartsWith,
    pathToString
} from "@/path";
import {describe, expect, it} from 'vitest';

const equalPaths = [
    ["a", "a"],
    ["a.a", "a[a]"],
    ["a.a", "a.a"],
    ["a.a", ["a", "a"]],
];
const notEqualPaths = [
    ["a", "b"],
    ["a.a", "a"],
    ["a", "a.a"],
    ["a.a", "a.b"],
    ["a.a", ["a", "b"]],
];

describe('comparePaths', () => {
    it.each(equalPaths)('%s is equal to %s', (path1: Path, path2: Path) => {
        expect(comparePaths(path1, path2)).toEqual(0);
    });
    it.each(notEqualPaths)('%s is not equal to %s', (path1: Path, path2: Path) => {
        expect(comparePaths(path1, path2)).not.toEqual(0);
    });
});

describe('arePathEqual', () => {
    it.each(equalPaths)('%s is equal to %s', (path1: Path, path2: Path) => {
        expect(arePathEqual(path1, path2)).toBeTruthy();
    });
    it.each(notEqualPaths)('%s is not equal to %s', (path1: Path, path2: Path) => {
        expect(arePathEqual(path1, path2)).toBeFalsy();
    });
});

describe('getParentPath', () => {
    it.each([
        ["a", []],
        ["a.a", ["a"]],
        ["a.a.a", ["a", "a"]],
        ["a[a].a", ["a", "a"]],
        ["a.a[a]", ["a", "a"]],
        [['a', 'a', 'a'], ["a", "a"]],
    ])('%s has %s as parent', (path: Path, parent: Path) => {
        expect(getParentPath(path)).toEqual(parent);
    });
});

describe('pathToString', () => {
    it.each([
        ["a", "a"],
        ["a.a", "a.a"],
        ["a.a.a", "a.a.a"],
        ["a[a].a", "a.a.a"],
        ["a.a[a]", "a.a.a"],
        [['a', 'a', 'a'], "a.a.a"],
    ])('%s to string is %s', (path: Path, name: string) => {
        expect(pathToString(path)).toEqual(name);
    });
});

describe('concatPaths', () => {
    it.each([
        [["a", "a"], 'a.a'],
        [["a", "a", "a"], 'a.a.a'],
        [["a", "a", "a", "a"], 'a.a.a.a'],
        [["a[a].a", "a.a.a"], 'a.a.a.a.a.a'],
    ])('%s concat to %s', (paths: Path[], concat: Path) => {
        expect(pathToString(concatPaths(paths))).toEqual(concat);
    });
});

describe('pathStartsWith', () => {
    it.each([
        ["a", "a"],
        ["a.a", "a"],
        ["a.a.a", "a.a"],
    ])('%s starts with %s', (path: Path, prefix: Path) => {
        expect(pathStartsWith(path, prefix)).toBeTruthy();
    });
    it.each([
        ["a", "b"],
    ])('%s don\'t starts with %s', (path: Path, prefix: Path) => {
        expect(pathStartsWith(path, prefix)).toBeFalsy()
    });
});

describe('pathEndsWith', () => {
    it.each([
        ["a", "a"],
        ["a.a", "a"],
        ["a.a.a", "a.a"],
    ])('%s starts with %s', (path: Path, suffix: Path) => {
        expect(pathEndsWith(path, suffix)).toBeTruthy();
    });
    it.each([
        ["a", "b"],
    ])('%s don\'t starts with %s', (path: Path, suffix: Path) => {
        expect(pathEndsWith(path, suffix)).toBeFalsy()
    });
});

describe('pathIncludes', () => {
    it.each([
        ["a", "a"],
        ["a.a", "a"],
        ["a.a.a", "a.a"],
    ])('%s starts with %s', (path: Path, includes: Path) => {
        expect(pathIncludes(path, includes)).toBeTruthy();
    });
    it.each([
        ["a", "b"],
    ])('%s don\'t starts with %s', (path: Path, includes: Path) => {
        expect(pathIncludes(path, includes)).toBeFalsy()
    });
});
