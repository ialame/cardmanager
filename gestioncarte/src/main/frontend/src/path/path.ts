import {concat, flatMap, isArray, isObject, map, toPath} from "lodash";

export type Path = string | number | symbol | (string | number | symbol)[];

export function comparePaths(path1: Path, path2: Path): number {
    const p1 = toPath(path1);
    const p2 = toPath(path2);

    for (let i = 0; i < p1.length && i < p2.length; i++) {
        const c = p1[i].localeCompare(p2[i]);

        if (c !== 0) {
            return c;
        }
    }
    return p1.length - p2.length;
}
export function arePathEqual(path1: Path, path2: Path): boolean {
    return comparePaths(path1, path2) === 0;
}

export function getParentPath(path: Path, depth?: number): Path {
    const p = toPath(path);

    return p.slice(0, p.length - (depth && depth > 0 ? depth : 1));
}

export function pathToString(path: Path): string {
    return toPath(path).join(".");
}

export function concatPaths(paths: Path[]): Path {
    return paths.flatMap(toPath);
}

function _pathStartsWith(path: string[], prefix: string[]): boolean {
    return prefix.every((v, i) => v === path[i]);
}

export function pathStartsWith(path: Path, prefix: Path): boolean {
    const p = toPath(path);
    const pr = toPath(prefix);

    return _pathStartsWith(p, pr);
}

export function trimPathPrefix(path: Path, prefix: Path): Path {
    const p = toPath(path);
    const pr = toPath(prefix);

    if (_pathStartsWith(p, pr)) {
        return p.slice(pr.length);
    }
    return p;
}

function _pathEndsWith(path: string[], suffix: string[]): boolean {
    return suffix.every((v, i) => v === path[path.length - suffix.length + i]);
}

export function pathEndsWith(path: Path, suffix: Path): boolean {
    const p = toPath(path);
    const su = toPath(suffix);

    return _pathEndsWith(p, su);
}

export function trimPathSuffix(path: Path, suffix: Path): Path {
    const p = toPath(path);
    const su = toPath(suffix);

    if (_pathEndsWith(p, su)) {
        return p.slice(0, p.length - su.length);
    }
    return p;
}

export function pathIncludes(path: Path, subPath: Path): boolean {
    const p = toPath(path);
    const sp = toPath(subPath);

    return p.some((_v, i) => _pathStartsWith(p.slice(i), sp));
}

export function trimPathList(paths: Path[] | undefined, path: Path) {
    return paths?.filter(p => pathStartsWith(p, path)).map(p => trimPathPrefix(p, path)) ?? [];
}

export function pathArrayIncludes(paths: Path[] | undefined, path: Path) {
    return !!paths?.some(p => arePathEqual(p, path));
}

export function getPaths(value: any, parentPath: Path = ""): Path[] {
    let result: Path[];

    if (isArray(value)) {
        let idx = 0;

        result = flatMap(value, v => getPaths(v, concatPaths([parentPath, idx++])));
    }
    else if (isObject(value)) {
        result = flatMap(Object.keys(value), k => map(getPaths((value as any)[k], k), sk => concatPaths([parentPath, sk])));
    }
    else {
        result = [];
    }
    return concat(result, parentPath || []);
}
