export type DeepKeyRemap<T, U> = T extends U ? T : T extends object ? {[K in keyof T]: DeepKeyRemap<T[K], U>} : T extends (infer V)[] ? DeepKeyRemap<V, U>[] : U;
export type DeepWriteable<T> = { -readonly [P in keyof T]: DeepWriteable<T[P]> };
export type DeepPartial<T> = { [P in keyof T]?: DeepPartial<T[P]>; }
