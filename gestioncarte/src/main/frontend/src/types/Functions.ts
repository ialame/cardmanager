export type Consumer<T> = (t: T) => void;
export type Predicate<T> = (item: T) => boolean;
export type Supplier<T> = () => T;

export type AsyncPredicate<T> = (item: T) => boolean | Promise<boolean>;
