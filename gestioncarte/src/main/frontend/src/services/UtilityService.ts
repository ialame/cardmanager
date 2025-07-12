
export namespace UtilityService {
    type Target = { id: number; }

    export function findById<T extends Target>(id: number, targets: T[]) {
        return targets.find(t => t.id === id);
    }
}